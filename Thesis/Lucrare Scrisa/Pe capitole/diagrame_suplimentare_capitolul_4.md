# Diagrame Suplimentare - Capitolul 4

Acest fișier conține diagramele solicitate pentru secțiunile 4.2, 4.3 și 4.4. Ele documentează fluxurile de mesagerie, concurență tranzacțională, optimizarea prin API Gateway și modelul de securitate Zero-Trust.

---

## Secțiunea 4.2 — Gestiunea comunicării inter-servicii

### Figura 4.3. Diagrama de secvență a fluxului de rezervare clinică
Această diagramă prezintă tranzacția clinică securizată la crearea unei programări, interogarea FSM pentru determinarea tipului de serviciu și aplicarea blocării pesimiste (SELECT ... FOR UPDATE) pentru evitarea rezervărilor duble.

```mermaid
sequenceDiagram
    autonumber
    participant C as Client (React SPA)
    participant GW as API Gateway (WebFlux)
    participant PS as programari-service
    participant SS as servicii-service (Feign)
    participant DB as programari_db (MySQL)
    participant RMQ as Broker RabbitMQ (AMQP)

    C->>GW: POST /api/programari
    Note over GW: Validare JWT și Rutare asincronă reactivă
    GW->>PS: POST /programari
    
    rect rgb(240, 248, 255)
        Note over PS, DB: Context Tranzacțional - @Transactional
        
        PS->>DB: countProgramariActiveSauFinalizate
        DB-->>PS: contor (Află dacă e Prima Întâlnire)
        
        PS->>DB: findFirstByPacientKeycloakIdOrderByDataDesc
        DB-->>PS: Optional (Identifică starea în FSM)
        
        alt FSM - Fără evaluare în istoric
            PS->>SS: GET /servicii/nume?nume=Evaluare Initiala
            SS-->>PS: DetaliiServiciuDTO (durată, preț)
        else FSM - Evaluare clinică activă (ședințe efectuate sub recomandate)
            PS->>SS: GET /servicii/{recomandatId}
            SS-->>PS: DetaliiServiciuDTO
        else FSM - Evaluare finalizată (toate ședințele efectuate)
            PS->>SS: GET /servicii/nume?nume=Reevaluare
            SS-->>PS: DetaliiServiciuDTO
        end

        Note over PS, DB: Securizare concurență - Pessimistic Lock
        PS->>DB: existaSuprapunere (SELECT FOR UPDATE)
        
        alt Interval Ocupat (Lock dobândit de altă tranzacție)
            DB-->>PS: true (excepție ResourceAlreadyExistsException)
            PS-->>GW: 409 Conflict
            GW-->>C: Notificare în interfață: Intervalul orar este ocupat
        else Interval Liber
            DB-->>PS: false
            PS->>DB: save
            DB-->>PS: Programare confirmată (ID generat)
        end
    end

    Note over PS, RMQ: Decuplare prin Evenimente (Asincron)
    PS->>RMQ: Publică Eveniment: notificare.programare.noua
    
    PS-->>GW: 201 Created (ProgramareResponseDTO)
    GW-->>C: 201 Created (Rezervare Confirmată)
```

---

### Figura 4.4. Topologia RabbitMQ și circuitul mesajelor toxice (DLQ)
Această diagramă descrie arhitectura cozilor, logica de rutare prin chei semantice Topic și devierea automată a mesajelor structural afectate (NACK fără requeue) în carantină.

```mermaid
graph TD
    %% Stiluri noduri
    classDef main fill:#cce5ff,stroke:#007bff,stroke-width:2px;
    classDef dlq fill:#f8d7da,stroke:#dc3545,stroke-width:2px;
    classDef pub fill:#e2e3e5,stroke:#383d41,stroke-width:2px;
    classDef broker fill:#fff3cd,stroke:#ffc107,stroke-width:2px;

    Pub1["programari-service<br/>Publisher"]:::pub
    Pub2["user-service<br/>Publisher"]:::pub

    subgraph RabbitMQ ["Broker RabbitMQ"]
        EX["Topic Exchange:<br/>notificari.exchange"]:::broker
        Q_Main["Coadă Principală:<br/>notificari.queue.v2"]:::main
        
        DLX["Dead Letter Exchange - Fanout:<br/>notificari.dlx"]:::broker
        Q_Dead["Coadă Carantină - DLQ:<br/>notificari.queue.dead"]:::dlq
    end

    Sub["notificari-service<br/>Consumer"]:::pub

    Pub1 -->|1. Eveniment programare, routing: notificare.programare| EX
    Pub2 -->|1. Eveniment cont nou, routing: notificare.user.creat| EX

    EX -->|2. Binding pattern: notificare.#| Q_Main
    
    Q_Main -->|3. Consumare mesaj| Sub
    
    Sub -->|4. Eșec deserializare - Mesaj structural toxic| Sub
    Sub -->|5. Trimitere NACK - requeue=false| Q_Main
    
    Q_Main -->|6. Redirecționare automată - argument x-dead-letter-exchange| DLX
    DLX -->|7. Rutare directă| Q_Dead
    
    Q_Dead -->|8. Audit și analiză manuală| Audit["Inginer Sistem / Monitorizare"]:::pub
```

---

## Secțiunea 4.3 — API Gateway și tiparul BFF (Scatter-Gather)

### Figura 4.5. Optimizarea latenței: Interogare Serială vs. BFF Reactiv (Scatter-Gather)
Diagramele Gantt de mai jos ilustrează economia de timp obținută prin execuția concurentă a cererilor downstream (prin `Mono.zip` sau `/users/batch`) comparativ cu un model serial care suferă de penalizarea de latență N+1.

```mermaid
gantt
    title Model Serial Clasic - Cereri separate consecutive
    dateFormat  X
    section Frontend la API
    R1 - Date utilizator - user-service :active, s1, 0, 80
    R2 - Profil medical - pacienti-service :crit, s2, 80, 180
    R3 - Orar viitor - programari-service :crit, s3, 180, 280
    Consolidare date si randare :s4, 280, 300
```

```mermaid
gantt
    title Model BFF Reactiv - Agregare paralelă pe server
    dateFormat  X
    section Gateway la Core
    BFF Request - Clienti WebFlux :active, p1, 0, 10
    Mono 1 - Date utilizator - user-service :active, p2, 10, 90
    Mono 2 - Profil medical - pacienti-service :crit, p3, 10, 110
    Mono 3 - Orar viitor - programari-service :active, p4, 10, 105
    Agregare si raspuns :p5, 110, 120
```

---

## Secțiunea 4.4 — Securizarea canalelor WebSocket STOMP

### Figura 4.6. Ciclul de viață al ThreadLocal în Interceptorul STOMP
Această diagramă conceptualizează fluxul de securitate pe canale persistente în cele 3 faze ale `StompSecurityInterceptor` care protejează aplicația împotriva scurgerilor de credențiale (credential leakage).

```mermaid
graph TD
    subgraph Faza1 ["Faza 1: preSend"]
        A["Cadru STOMP SEND<br/>Antet Authorization: Bearer JWT"] --> B["StompSecurityInterceptor.preSend"]
        B --> C["Validare locală JWT - JWKS Keycloak"]
        C --> D["Setare ThreadLocal:<br/>SecurityContextHolder.setAuthentication"]:::highlight
        D --> D2["accessor.setUser"]
    end

    subgraph Faza2 ["Faza 2: Execuție Logică"]
        D2 --> E["ChatController / ChatService"]
        E -->|Apel OpenFeign securizat| F["programari-service<br/>Preluare token din SecurityContext"]
    end

    subgraph Faza3 ["Faza 3: postSend"]
        F --> G["StompSecurityInterceptor.postSend"]
        G --> H["Igienizare obligatorie:<br/>SecurityContextHolder.clearContext"]:::highlight
        H --> I["Fir de execuție returnat în pool<br/>Fără risc de credential leakage"]
    end

    %% Referință text
    RefNote["Notă: Pentru o vizualizare secvențială detaliată a propagării contextului STOMP, consultați Figura 6.4 - Anexa 6"]

    classDef highlight fill:#cce5ff,stroke:#007bff,stroke-width:2px;
```

---

## Secțiunea 4.5 — Modelul de Date și Persistența Distribuită

### Figura 4.7. Diagrama logică a bazelor de date și legăturilor inter-servicii
Această diagramă ilustrează cele 7 scheme SQL complet izolate (Database-per-Service) conform definiției fizice a tabelelor. Sunt reprezentate principalele tabele din fiecare microserviciu, coloanele cheie și modul în care acestea stabilesc referințe logice descentralizate pe baza cheii universale de pivotare `keycloak_id` sau referințe de catalog.

```mermaid
flowchart TD
    %% Styling
    classDef entity fill:#ffffff,stroke:#333,stroke-width:1px;

    subgraph DB_User ["Baza de Date: user_service"]
        direction TB
        T_Users["users<br/>- id - PK<br/>- keycloak_id - UK<br/>- email - UK<br/>- role"]:::entity
    end

    subgraph DB_Pacienti ["Baza de Date: pacienti_service"]
        direction TB
        T_Pacienti["pacienti<br/>- id - PK<br/>- keycloak_id - UK<br/>- cnp - UK<br/>- terapeut_keycloak_id"]:::entity
        T_Jurnal["jurnal_pacient<br/>- id - PK<br/>- pacient_id - FK<br/>- programare_id - FK logic<br/>- nivel_durere"]:::entity
        T_Pacienti -->|FK local| T_Jurnal
    end

    subgraph DB_Terapeuti ["Baza de Date: terapeuti_service"]
        direction TB
        T_Terapeuti["terapeuti<br/>- id - PK<br/>- keycloak_id - UK<br/>- specializare"]:::entity
        T_Locatii["locatii<br/>- id - PK<br/>- nume<br/>- oras"]:::entity
        T_Disp["disponibilitate_terapeut<br/>- id - PK<br/>- terapeut_id - FK<br/>- locatie_id - FK"]:::entity
        T_Concediu["concediu_terapeut<br/>- id - PK<br/>- terapeut_id - FK<br/>- data_inceput"]:::entity
        T_Terapeuti -->|FK local| T_Disp
        T_Terapeuti -->|FK local| T_Concediu
        T_Locatii -->|FK local| T_Disp
    end

    subgraph DB_Programari ["Baza de Date: programari_service"]
        direction TB
        T_Programari["programari<br/>- id - PK<br/>- pacient_keycloak_id - FK logic<br/>- terapeut_keycloak_id - FK logic<br/>- locatie_id - FK logic<br/>- serviciu_id - FK logic<br/>- status"]:::entity
        T_Evaluari["evaluari<br/>- id - PK<br/>- programare_id - FK<br/>- pacient_keycloak_id - FK logic<br/>- terapeut_keycloak_id - FK logic"]:::entity
        T_Evolutii["evolutii<br/>- id - PK<br/>- pacient_keycloak_id - FK logic<br/>- terapeut_keycloak_id - FK logic"]:::entity
        T_Relatie["relatie_pacient_terapeut<br/>- id - PK<br/>- pacient_keycloak_id - FK logic<br/>- terapeut_keycloak_id - FK logic<br/>- activa"]:::entity
        T_Programari -->|FK local| T_Evaluari
    end

    subgraph DB_Servicii ["Baza de Date: servicii_service"]
        direction TB
        T_Servicii["servicii<br/>- id - PK<br/>- nume<br/>- pret<br/>- tip_serviciu_id - FK"]:::entity
        T_TipServiciu["tip_serviciu<br/>- id - PK<br/>- nume - UK"]:::entity
        T_TipServiciu -->|FK local| T_Servicii
    end

    subgraph DB_Chat ["Baza de Date: chat_service"]
        direction TB
        T_Conversatii["conversatii<br/>- id - PK<br/>- pacient_keycloak_id - FK logic<br/>- terapeut_keycloak_id - FK logic"]:::entity
        T_Mesaje["mesaje<br/>- id - PK<br/>- conversatie_id - FK<br/>- expeditor_keycloak_id - FK logic"]:::entity
        T_Conversatii -->|FK local| T_Mesaje
    end

    subgraph DB_Notif ["Baza de Date: notificari_service"]
        direction TB
        T_Notificari["notificari<br/>- id - PK<br/>- user_keycloak_id - FK logic<br/>- tip_user<br/>- este_citita"]:::entity
    end

    %% Legături Logice (keycloak_id)
    T_Pacienti -.->|keycloak_id| T_Users
    T_Terapeuti -.->|keycloak_id| T_Users

    T_Programari -.->|pacient_keycloak_id| T_Pacienti
    T_Programari -.->|terapeut_keycloak_id| T_Terapeuti

    T_Evaluari -.->|pacient_keycloak_id| T_Pacienti
    T_Evolutii -.->|pacient_keycloak_id| T_Pacienti
    T_Relatie -.->|pacient_keycloak_id| T_Pacienti

    T_Conversatii -.->|pacient_keycloak_id| T_Pacienti
    T_Conversatii -.->|terapeut_keycloak_id| T_Terapeuti

    T_Notificari -.->|user_keycloak_id| T_Users

    %% Legături de Catalog / ID-uri fizice inter-domenii
    T_Programari -.->|locatie_id| T_Locatii
    T_Programari -.->|serviciu_id| T_Servicii
    T_Jurnal -.->|programare_id| T_Programari

    %% Apply DB styling to subgraphs
    style DB_User fill:#f5faff,stroke:#0056b3,stroke-width:2px;
    style DB_Pacienti fill:#f5faff,stroke:#0056b3,stroke-width:2px;
    style DB_Terapeuti fill:#f5faff,stroke:#0056b3,stroke-width:2px;
    style DB_Programari fill:#f5faff,stroke:#0056b3,stroke-width:2px;
    style DB_Servicii fill:#f5faff,stroke:#0056b3,stroke-width:2px;
    style DB_Chat fill:#f5faff,stroke:#0056b3,stroke-width:2px;
    style DB_Notif fill:#f5faff,stroke:#0056b3,stroke-width:2px;
```
