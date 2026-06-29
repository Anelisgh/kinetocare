1. Figura A4.1 Topologia generală a arhitecturii microserviciilor pe niveluri de execuție

```mermaid
flowchart TD
    subgraph ClientLayer ["Nivelul Client (Client Layer)"]
        React["Aplicație React SPA<br/>(Interfața unică cu utilizatorul)"]
    end

    subgraph EdgeLayer ["Nivelul de Margine (Edge Layer)"]
        APIGW["API Gateway<br/>(Rutare, Agregare & Auth Proxy)"]
        Keycloak["Keycloak Server<br/>(Identitate și Autentificare)"]
    end

    subgraph DomainLayer ["Nivelul Serviciilor de Domeniu (Domain Services)"]
        direction LR
        US["user-service"]
        PS["pacienti-service"]
        TS["terapeuti-service"]
        ProgS["programari-service"]
        SS["servicii-service"]
        CS["chat-service"]
        NS["notificari-service"]
    end

    subgraph DataLayer ["Nivelul de Date (Data Layer) - Scheme MySQL izolate"]
        direction LR
        DB_US[(user_db)]
        DB_PS[(pacienti_db)]
        DB_TS[(terapeuti_db)]
        DB_ProgS[(programari_db)]
        DB_SS[(servicii_db)]
        DB_CS[(chat_db)]
        DB_NS[(notificari_db)]
    end

    React -->|Cereri HTTP securizate| APIGW
    APIGW <-->|Proxy Auth & Validare JWKS| Keycloak
    US -->|Keycloak Admin API| Keycloak
    
    %% Rutare trafic fara etichete redundante
    APIGW --> US & PS & TS & ProgS & SS & CS & NS

    %% Persistenta dedicata fara etichete redundante
    US -.-> DB_US
    PS -.-> DB_PS
    TS -.-> DB_TS
    ProgS -.-> DB_ProgS
    SS -.-> DB_SS
    CS -.-> DB_CS
    NS -.-> DB_NS

    classDef default fill:#f9f9f9,stroke:#333,stroke-width:1px;
    classDef db fill:#f0f8ff,stroke:#0056b3,stroke-width:2px;
    class DB_US,DB_PS,DB_TS,DB_ProgS,DB_SS,DB_CS,DB_NS db;
```




2. Figura A4.2. Graful orientat al fluxurilor de comunicare inter-servicii

```mermaid
graph LR

%% Servicii active din stânga (rol de orchestrator/apelant)
subgraph ActiveServices ["Servicii Active / Apelante"]
    US[user-service]
    PS[pacienti-service]
    CS[chat-service]
end

%% Serviciul central tranzacțional
PR[programari-service]

%% Servicii din dreapta (Cataloage / Dependențe de resurse)
subgraph ProviderServices ["Resurse & Cataloage (Dependențe)"]
    TS[terapeuti-service]
    SS[servicii-service]
end

%% Infrastructură asincronă de notificare
subgraph AsyncInfrastructure ["Infrastructură Asincronă / Mesagerie"]
    RabbitMQ[RabbitMQ Broker]
    NS[notificari-service]
end

%% Inițializare și activare profile
US -->|Inițializare & Activare profil| PS
US -->|Inițializare & Activare profil| TS

%% Operații care implică programări
US -->|Anulare cascadă| PR
PS -->|Anulare programări & Marcare Jurnal| PR
CS -->|Validare securitate clinică| PR

%% Dependințe ale programărilor
PR -->|Rezoluție identitate| US
PR -->|Interogare profil & jurnal| PS
PR -->|Validare orar| TS
PR -->|Preluare snapshot| SS

%% Evenimente asincrone
PR -.->|Evenimente programări| RabbitMQ
PS -.->|Evenimente jurnale| RabbitMQ
CS -.->|Evenimente mesaje| RabbitMQ

RabbitMQ -.->|Consumare| NS

%% Stilizare
classDef leafNode fill:#e8f5e9,stroke:#2e7d32,stroke-width:2px;
classDef centralNode fill:#fff3e0,stroke:#e65100,stroke-width:3px;
classDef normalNode fill:#e3f2fd,stroke:#1565c0,stroke-width:2px;
classDef broker fill:#ffebee,stroke:#c62828,stroke-width:2px,stroke-dasharray:5 5;

class TS,SS leafNode;
class PR centralNode;
class US,PS,CS,NS normalNode;
class RabbitMQ broker;
```

4. Figura A5.2. Structura detaliată Entitate-Relație (ERD) a bazelor de date

```mermaid
classDiagram
  direction LR

  classDef tblUser       fill:#eff6ff,stroke:#3b82f6,stroke-width:2px,color:#1e3a8a
  classDef tblTerapeuti  fill:#f0fdf4,stroke:#22c55e,stroke-width:2px,color:#14532d
  classDef tblPacienti   fill:#faf5ff,stroke:#a855f7,stroke-width:2px,color:#4a1d96
  classDef tblProgramari fill:#fff7ed,stroke:#f97316,stroke-width:2px,color:#7c2d12
  classDef tblServicii   fill:#fefce8,stroke:#eab308,stroke-width:2px,color:#713f12
  classDef tblChat       fill:#ecfeff,stroke:#06b6d4,stroke-width:2px,color:#164e63
  classDef tblNotificari fill:#fdf4ff,stroke:#ec4899,stroke-width:2px,color:#831843

  namespace user_db {
    class users:::tblUser
  }
  namespace terapeuti_db {
    class terapeuti:::tblTerapeuti
    class locatii:::tblTerapeuti
    class disponibilitate_terapeut:::tblTerapeuti
    class concediu_terapeut:::tblTerapeuti
  }
  namespace pacienti_db {
    class pacienti:::tblPacienti
    class jurnal_pacient:::tblPacienti
  }
  namespace programari_db {
    class programari:::tblProgramari
    class evaluari:::tblProgramari
    class evolutii:::tblProgramari
    class relatie_pacient_terapeut:::tblProgramari
  }
  namespace servicii_db {
    class servicii:::tblServicii
    class tip_serviciu:::tblServicii
  }
  namespace chat_db {
    class conversatii:::tblChat
    class mesaje:::tblChat
  }
  namespace notificari_db {
    class notificari:::tblNotificari
  }

  %% Legături Logice Cross-Service (referințe slabe în cod, baze de date izolate)
  users ..> terapeuti : "keycloak_id (1-1)"
  users ..> pacienti : "keycloak_id (1-1)"
  users ..> notificari : "user_keycloak_id (1-N)"

  pacienti ..> terapeuti : "terapeut_keycloak_id (N-1)"
  pacienti ..> locatii : "locatie_preferata_id (N-1)"
  pacienti ..> programari : "pacient_keycloak_id (1-N)"
  pacienti ..> evaluari : "pacient_keycloak_id (1-N)"
  pacienti ..> evolutii : "pacient_keycloak_id (1-N)"
  pacienti ..> relatie_pacient_terapeut : "pacient_keycloak_id (1-N)"
  pacienti ..> conversatii : "pacient_keycloak_id (1-N)"

  terapeuti ..> programari : "terapeut_keycloak_id (1-N)"
  terapeuti ..> evaluari : "terapeut_keycloak_id (1-N)"
  terapeuti ..> evolutii : "terapeut_keycloak_id (1-N)"
  terapeuti ..> relatie_pacient_terapeut : "terapeut_keycloak_id (1-N)"
  terapeuti ..> conversatii : "terapeut_keycloak_id (1-N)"

  locatii ..> programari : "locatie_id (1-N)"
  servicii ..> programari : "serviciu_id (1-N)"
  servicii ..> evaluari : "serviciu_recomandat_id (1-N)"
  programari ..> jurnal_pacient : "programare_id (1-0..1)"

  %% Legături Fizice Intra-Service (Foreign Key-uri SQL reale în aceleași scheme)
  terapeuti --> disponibilitate_terapeut : "terapeut_id (1-N)"
  terapeuti --> concediu_terapeut : "terapeut_id (1-N)"
  locatii --> disponibilitate_terapeut : "locatie_id (1-N)"
  pacienti --> jurnal_pacient : "pacient_id (1-N)"
  conversatii --> mesaje : "conversatie_id (1-N)"
  programari --> evaluari : "programare_id (1-0..1)"
  tip_serviciu "1" --> "0..*" servicii : "tip_serviciu_id (FK fizic)"
  
```

3. Figura A5.1. Diagrama conceptuală globală a conexiunilor logice între schemele bazei de date

```mermaid
%%{init: {"flowchart": {"curve": "basis", "nodeSpacing": 55, "rankSpacing": 80}}}%%

flowchart LR
    %% Coloana 1
    subgraph C1[" "]
        direction TB
        DB_US[(user_db)]
        DB_SS[(servicii_db)]
    end

    %% Coloana 2
    subgraph C2[" "]
        direction TB
        DB_TS[(terapeuti_db)]
        DB_PS[(pacienti_db)]
    end

    %% Coloana 3
    subgraph C3[" "]
        direction TB
        DB_CS[(chat_db)]
        DB_PR[(programari_db)]
        DB_NS[(notificari_db)]
    end

    %% Relațiile logice principale
    DB_US -. "keycloak_id" .-> DB_TS
    DB_US -. "keycloak_id" .-> DB_PS
    DB_US -. "user_keycloak_id" .-> DB_NS

    DB_PS -. "terapeut_keycloak_id" .-> DB_TS
    DB_PS -. "pacient_keycloak_id" .-> DB_PR
    DB_PS -. "pacient_keycloak_id" .-> DB_CS

    DB_TS -. "terapeut_keycloak_id" .-> DB_PR
    DB_TS -. "terapeut_keycloak_id" .-> DB_CS
    DB_TS -. "locatie_id" .-> DB_PR

    DB_SS -. "serviciu_id" .-> DB_PR

    %% Stilizare culori
    style DB_US fill:#eff6ff,stroke:#3b82f6,stroke-width:2px,color:#1e3a8a
    style DB_TS fill:#f0fdf4,stroke:#22c55e,stroke-width:2px,color:#14532d
    style DB_PS fill:#faf5ff,stroke:#a855f7,stroke-width:2px,color:#4a1d96
    style DB_PR fill:#fff7ed,stroke:#f97316,stroke-width:2px,color:#7c2d12
    style DB_SS fill:#fefce8,stroke:#eab308,stroke-width:2px,color:#713f12
    style DB_CS fill:#ecfeff,stroke:#06b6d4,stroke-width:2px,color:#164e63
    style DB_NS fill:#fdf4ff,stroke:#ec4899,stroke-width:2px,color:#831843

    %% Ascundem marginile subgraph-urilor
    style C1 fill:transparent,stroke:transparent
    style C2 fill:transparent,stroke:transparent
    style C3 fill:transparent,stroke:transparent
```

# Capitolul 6

## Fig. A6.1. Diagrama de tranziție a stărilor clinice (Automat Finit Determinist)

```mermaid
stateDiagram-v2
    direction LR

    [*] --> EvaluareInitiala : Nicio evaluare înregistrată în DB

    EvaluareInitiala --> TratamentActiv : Terapeutul completează Evaluarea Inițială<br/>(stabilește bugetul de N ședințe)

    TratamentActiv --> Reevaluare : Ședințe efectuate >= N<br/>(bugetul de tratament este epuizat)

    Reevaluare --> TratamentActiv : Terapeutul finalizează Reevaluarea<br/>(stabilește un nou buget de ședințe)

    state "S_A: Evaluare Inițială" as EvaluareInitiala
    state "S_B: Tratament Activ" as TratamentActiv
    state "S_C: Reevaluare" as Reevaluare
```

## Fig. A6.2. Fluxul secvențial de generare a ferestrelor de disponibilitate

```mermaid
sequenceDiagram
    autonumber
    participant C as Client (Aplicație React)
    participant PS as programari-service
    participant TS as terapeuti-service
    participant SS as servicii-service
    participant DB as programari_db

    C->>PS: GET /api/programari/disponibilitate?terapeutKeycloakId=&locatieId=&data=&serviciuId=
    
    rect rgb(240, 248, 255)
        Note over PS,TS: Faza 1: Colectarea constrângerilor (Feign)
        PS->>TS: GET /terapeut/by-keycloak/{keycloakId}
        TS-->>PS: returnează Map cu id numeric (Long)
        PS->>TS: GET /concediu/check/terapeut/{terapeutId}/data/{data}
        TS-->>PS: false (terapeutul nu este în concediu)
        PS->>TS: GET /disponibilitate/terapeut/{terapeutId}/locatie/{locatieId}/zi/{zi}
        TS-->>PS: returnează DisponibilitateDTO (oraInceput, oraSfarsit)
        PS->>SS: GET /servicii/{serviciuId}
        SS-->>PS: returnează DetaliiServiciuDTO (durataMinute)
    end

    rect rgb(245, 245, 220)
        Note over PS,DB: Faza 2: Preluarea agendei locale
        PS->>DB: findByTerapeutKeycloakIdAndDataAndStatus(terapeutKeycloakId, data, PROGRAMATA)
        DB-->>PS: List<Programare> programăriExistente
    end

    rect rgb(244, 255, 244)
        Note over PS: Faza 3: Generare (Sliding Window)
        PS->>PS: Inițializează cursor = oraInceput
        loop Avans cursor cu durataMinute + 10 min (buffer)
            PS->>PS: Verifică suprapunere cu programăriExistente (esteLiber)
            PS->>PS: Verifică încadrare în orar și timp viitor (nu din trecut)
            PS->>PS: Adaugă slot orar valid în listă
        end
    end

    PS-->>C: returnează List<LocalTime> (sloturi libere)
```

## Fig. A6.3. Arborele decizional pentru partiționarea temporală binară a interogărilor

```mermaid
%%{init: {"theme": "base", "flowchart": {"curve": "basis", "nodeSpacing": 45, "rankSpacing": 55}}}%%

flowchart TD
    A["Start: Metoda gasesteInFereastra(oreInainte, marjaMinute)"] --> B["Calculează momentul central al ferestrei:<br/>centruFereastra = acum + oreInainte"]
    B --> C["Determină începutul ferestrei:<br/>startFereastra = centruFereastra - marja"]
    C --> D["Determină sfârșitul ferestrei:<br/>endFereastra = centruFereastra + marja"]
    D --> E{"Fereastra de timp este<br/>în aceeași zi calendaristică?"}

    E -- "Da" --> F["Execută o interogare SQL unică pentru intervalul<br/>start.time - end.time în data start.date"]
    F --> J["Returnează lista programărilor identificate<br/>în fereastra de timp"]

    E -- "Nu" --> G["Execută prima subinterogare pentru ziua de început:<br/>start.time → 23:59:59"]
    G --> H["Execută a doua subinterogare pentru ziua următoare:<br/>00:00:00 → end.time"]
    
    H --> I["Combină rezultatele celor două subinterogări"]
    I --> J

    classDef start fill:#eff6ff,stroke:#3b82f6,stroke-width:2px,color:#1e3a8a;
    classDef calc fill:#f0fdf4,stroke:#22c55e,stroke-width:2px,color:#14532d;
    classDef decision fill:#faf5ff,stroke:#a855f7,stroke-width:2px,color:#4a1d96;
    classDef query fill:#fff7ed,stroke:#f97316,stroke-width:2px,color:#7c2d12;
    classDef merge fill:#ecfeff,stroke:#06b6d4,stroke-width:2px,color:#164e63;
    classDef result fill:#fdf4ff,stroke:#ec4899,stroke-width:2px,color:#831843;

    class A start;
    class B,C,D calc;
    class E decision;
    class F,G,H query;
    class I merge;
    class J result;
```

## Fig. A6.4. Ciclul de viață al interceptorului de securitate pe canalul STOMP

```mermaid
---
config:
  theme: redux-color
---
sequenceDiagram
    autonumber

    participant C as "Client WebSocket<br/>(React)"
    participant SI as "StompSecurityInterceptor"
    participant SC as "SecurityContextHolder<br/>(ThreadLocal)"
    participant CS as "ChatService"
    participant FI as "FeignRequestInterceptor"
    participant PR as "programari-service<br/>(via Feign)"

    C->>SI: "Transmite cadru STOMP [SEND] cu antet Authorization"

    rect rgb(240, 255, 244)
        Note over SI,SC: Faza 1: preSend() — Inițializarea contextului de securitate
        SI->>SI: "Extrage token-ul JWT din antetele STOMP"
        SI->>SI: "Validează token-ul prin jwtDecoder.decode(token)"
        SI->>SI: "Construiește JwtAuthenticationToken"
        SI->>SC: "setAuthentication(jwtAuthToken)"
        SI->>SI: "accessor.setUser(jwtAuthToken)"
    end

    SI->>CS: "Rutează cadrul STOMP către ChatController / ChatService"

    rect rgb(240, 248, 255)
        Note over CS,PR: Faza 2: Executarea logicii de chat și validarea relației active
        CS->>+FI: "Inițiază apelul Feign către programari-service"
        FI->>SC: "getAuthentication()"
        SC-->>FI: "returnează autentificarea curentă"
        FI->>FI: "Injectează antetul Authorization Bearer JWT"
        FI->>PR: "HTTP GET /relatii/status-keycloak"
        PR-->>FI: "returnează confirmarea relației active: true"
        FI-->>-CS: "transmite răspunsul către ChatService"

        CS->>CS: "Persistă mesajul în baza de date"
        CS->>CS: "Transmite mesajul în timp real prin WebSocket"
    end

    CS-->>SI: "Finalizează procesarea mesajului"

    rect rgb(255, 240, 245)
        Note over SI,SC: Faza 3: postSend() — Curățarea contextului de securitate
        SI->>SC: "clearContext()"
        Note over SC: ThreadLocal este curățat, iar firul de execuție poate reveni în pool
    end
```



## Fig A6.6
```mermaid
---
config:
  theme: redux-color
---
sequenceDiagram
    autonumber

    participant A as "Admin (React SPA)"
    participant US as "user-service"
    participant DB as "user_db (MySQL)"
    participant KC as "Keycloak IAM"
    participant PS as "pacienti-service /<br/>terapeuti-service"
    participant PR as "programari-service"

    A->>US: "PATCH /users/by-keycloak/{keycloakId}/toggle-active"

    rect rgb(224, 242, 255)
        Note over US,KC: Faza 1: Consistență puternică — actualizare atomică cu rollback local
        US->>DB: "Actualizează starea utilizatorului (active = false)"
        US->>KC: "setUserEnabled(keycloakId, false)"

        alt Sincronizare Keycloak reușită
            KC-->>US: "204 No Content (Confirmare dezactivare în IAM)"
            Note over US,DB: Commit tranzacție locală în MySQL

            rect rgb(254, 243, 199)
                Note over US,PR: Faza 2: Consistență eventuală — propagare best-effort în lanț
                US->>PS: "PATCH /pacient (sau /terapeut)/by-keycloak/{keycloakId}/toggle-active?active=false"
                Note over US,PS: Try-Catch: Dacă serviciul este indisponibil, eroarea este doar logată
                
                US->>PR: "PATCH /programari/admin/cancel-by-pacient (sau -by-terapeut)?keycloakId=..."
                Note over US,PR: Try-Catch: Anularea programărilor viitoare și eliberarea agendei
            end

            US-->>A: "200 OK (Utilizator dezactivat cu succes)"

        else Sincronizare Keycloak eșuată
            KC-->>US: "500 Internal Server Error / Timeout"
            Note over US,DB: Rollback automat al tranzacției SQL (Starea revine la ACTIVE)
            Note over US,A: Faza 2 este complet blocată pentru a păstra integritatea datelor
            US-->>A: "500 External Service Error (Dezactivare operativă anulată)"
        end
    end
```

## Fig. A6.7

```mermaid
---
config:
  theme: base
  flowchart:
    curve: basis
    nodeSpacing: 55
    rankSpacing: 65
---
flowchart LR
    subgraph Producatori["Producători de Evenimente"]
        PS["programari-service"]
        PACS["pacienti-service"]
        CS["chat-service"]
    end

    subgraph Broker["RabbitMQ Message Broker"]
        direction TB
        EX["TopicExchange<br/>notificari.exchange"]
        MQ["Main Queue<br/>notificari.queue.v2"]
        DLX["DLX Fanout<br/>notificari.dlx"]
        DLQ["DLQ Queue<br/>notificari.queue.dead"]

        EX -->|"Rutare wildcard: notificare.#"| MQ
        MQ -. "NACK (requeue=false)" .-> DLX
        DLX --> DLQ
    end

    subgraph Consumatori["Consumatori - notificari-service"]
        NC["NotificareConsumer<br/>(Procesare & Persistență)"]
        DC["DeadLetterConsumer<br/>(Audit & Carantină)"]
    end

    PS -->|"notificare.programare.*<br/>notificare.reminder.*<br/>notificare.evaluare.*<br/>notificare.reevaluare.*"| EX
    PACS -->|"notificare.jurnal.completat"| EX
    CS -->|"notificare.mesaj.nou"| EX
    MQ -->|"ACK - procesare cu succes"| NC
    DLQ --> DC
    classDef prod fill:#f1f5f9,stroke:#64748b,stroke-width:2px,color:#334155;
    classDef exchange fill:#fef9c3,stroke:#ca8a04,stroke-width:2px,color:#713f12;
    classDef queue fill:#dbeafe,stroke:#2563eb,stroke-width:2px,color:#1e3a8a;
    classDef consumer fill:#dcfce7,stroke:#16a34a,stroke-width:2px,color:#14532d;
    classDef dead fill:#fee2e2,stroke:#dc2626,stroke-width:2px,color:#7f1d1d;

    class PS,PACS,CS prod;
    class EX,DLX exchange;
    class MQ queue;
    class DLQ dead;
    class NC consumer;
    class DC dead;
```



## Flux terapeut

```mermaid
---
config:
  theme: base
  flowchart:
    curve: basis
    nodeSpacing: 45
    rankSpacing: 55
---
flowchart TD
    A["Autentificarea terapeutului"] --> AA["Redirecționare către pagina principală"]

    AA --> B{"Profilul este<br/>complet configurat?"}

    B -- "Nu" --> C["Afișare banner de avertizare pe pagina principală<br/>(se recomandă configurarea specializării și a orarului)"]
    C --> F{"Interacțiune cu bara de navigare"}

    B -- "Da" --> E["Terapeutul devine vizibil în sistem<br/>Pacienții îl pot selecta și programa"]
    E --> F

    %% Traseu Acasă / Calendar
    F -- "Acasă / Calendar" --> G["Afișare calendar clinic<br/>cu filtrare după locațiile clinice active"]
    G --> H{"Programarea este marcată<br/>ca primă întâlnire?"}

    H -- "Da" --> I["Indicator: prima întâlnire<br/>cu pacientul"]
    H -- "Nu" --> J["Programare obișnuită"]

    I --> K["Click pe programare:<br/>deschidere panou cu detalii<br/>Nume, telefon, dată/oră, serviciu, locație, status"]
    J --> K

    K --> L{"Perioada programării?"}
    L -- "În viitor / PROGRAMATĂ" --> LA["Opțiune de anulare<br/>a programării"]
    L -- "În trecut / FINALIZATĂ" --> LB["Opțiune de marcare<br/>ca neprezentare"]

    %% Traseu Pagina Pacienți
    F -- "Pacienți" --> M["Listă pacienți activi și arhivați<br/>Căutare după nume sau diagnostic"]
    M --> N["Selectare pacient:<br/>deschidere fișa pacientului"]
    N --> O["Date demografice și bară de progres tratament<br/>Ședințe efectuate / recomandate"]
    O --> P{"Pacientul are<br/>jurnale completate?"}

    P -- "Da" --> PA["Afișare grafic evolutiv de recuperare<br/>Durere, oboseală, dificultate exerciții"]
    P -- "Nu" --> PB["Ascundere grafic de evoluție"]

    PA --> Q{"Selectare tab<br/>în fișa pacientului"}
    PB --> Q

    Q -- "Evaluări" --> QA["Vizualizare istoric evaluări"]
    QA --> QAA{"Evaluarea aparține<br/>terapeutului curent?"}

    QAA -- "Da" --> QAAB{"Relația pacient-terapeut<br/>este activă?"}
    QAAB -- "Da" --> QAAC["Permite editarea evaluării"]
    QAAB -- "Nu" --> QAAD["Editarea este dezactivată<br/>(relație arhivată)"]

    QAA -- "Nu" --> QAAE["Doar vizualizare"]

    Q -- "Evoluții" --> QB["Timeline de note clinice private<br/>Editarea propriilor evoluții"]
    Q -- "Programări" --> QC["Listă istoric programări<br/>Filtrare opțională a celor anulate"]
    Q -- "Jurnale" --> QD["Feedback detaliat<br/>completat de pacient"]

    %% Traseu Pagina Evaluări
    F -- "Evaluări" --> R{"Relația pacient-terapeut<br/>este activă?"}
    R -- "Da" --> RA["Formular de adăugare evaluare nouă<br/>Tip: inițială / reevaluare<br/>Diagnostic vizibil<br/>Serviciu și ședințe recomandate<br/>Observații private"]
    R -- "Nu" --> RB["Adăugarea evaluării<br/>este dezactivată"]

    %% Traseu Pagina Evoluții
    F -- "Evoluții" --> S{"Relația pacient-terapeut<br/>este activă?"}
    S -- "Da" --> SA["Adăugare notă privată de evoluție<br/>independent de existența unei programări active"]
    S -- "Nu" --> SB["Adăugarea notei<br/>este dezactivată"]

    SA --> SC["Vizualizare istoric<br/>note clinice private pacient"]
    SB --> SC

    %% Traseu Pagina Mesaje
    F -- "Mesaje / Chat" --> T{"Statutul pacientului?"}
    T -- "Activ" --> TA["Comunicare bidirecțională activă"]
    T -- "Arhivat" --> TB["Vizualizare istoric conversație<br/>Trimiterea de mesaje noi este blocată"]

    %% Traseu Profil și Notificări
    F -- "Profil" --> U["Editare date personale, parolă, poză,<br/>concedii și disponibilitate orară"]
    U --> B

    F -- "Notificări" --> V["Dropdown în bara de navigare<br/>cu alerte in-app"]

    %% Clase de stilizare
    classDef auth fill:#f1f5f9,stroke:#64748b,stroke-width:2px,color:#334155;
    classDef check fill:#fef9c3,stroke:#ca8a04,stroke-width:2px,color:#713f12;
    classDef action fill:#dbeafe,stroke:#2563eb,stroke-width:2px,color:#1e3a8a;
    classDef success fill:#dcfce7,stroke:#16a34a,stroke-width:2px,color:#14532d;
    classDef tab fill:#f5f3ff,stroke:#7c3aed,stroke-width:2px,color:#4c1d95;
    classDef danger fill:#fee2e2,stroke:#dc2626,stroke-width:2px,color:#7f1d1d;

    class A,AA,C auth;
    class B,H,L,P,Q,QAA,QAAB,R,S,T check;
    class E,F,G,I,J,K,M,N,O,U,V action;
    class LA,PA,QA,QAAC,QB,QC,QD,RA,SA,SC,TA success;
    class LB,PB,QAAD,QAAE,RB,SB,TB danger;
```


## Flux pacient

```mermaid
---
config:
  theme: base
  flowchart:
    curve: basis
    nodeSpacing: 45
    rankSpacing: 55
---
flowchart TD
    A["Autentificarea pacientului"] --> AA["Verificarea stării profilului pacientului"]

    AA --> B{"Profilul este complet?<br/>(CNP și data nașterii)"}

    B -- "Nu" --> C["Redirecționare către completarea profilului<br/>(CNP, data nașterii, sport practicat și detalii relevante)"]
    C --> C1["Salvarea datelor obligatorii"]
    C1 -- "Reevaluare stare profil" --> B

    B -- "Da" --> D["Acces la pagina principală a pacientului"]
    D --> E{"Interacțiune cu bara de navigare"}

    %% Traseu Acasă
    E -- "Acasă" --> F["Afișarea informațiilor generale ale pacientului<br/>(nume, vârstă, diagnostic, terapeut și locație)"]
    F --> F1{"Există diagnostic configurat?"}

    F1 -- "Da" --> F2["Afișarea progresului tratamentului<br/>(ședințe efectuate / ședințe recomandate)"]
    F2 --> F3{"Planul de tratament<br/>este completat?"}

    F3 -- "Da" --> F3A["Afișarea unui mesaj privind finalizarea planului<br/>și recomandarea unei reevaluări"]
    F3 -- "Nu" --> F3B["Afișarea progresului curent al tratamentului"]

    F1 -- "Nu" --> G{"Pacientul are<br/>terapeut selectat?"}
    F3A --> G
    F3B --> G

    G -- "Nu" --> H["Afișarea opțiunilor de alegere<br/>a terapeutului și a locației preferate"]
    H --> H1["Salvarea preferințelor pacientului"]
    H1 -- "Stare actualizată" --> I

    G -- "Da" --> I{"Pacientul are<br/>programare viitoare?"}

    I -- "Da" --> J["Afișarea detaliilor programării active<br/>(dată, oră, terapeut, locație, serviciu și opțiune de anulare)"]
    J --> J1["Anularea programării, dacă pacientul solicită acest lucru"]
    J1 -- "Stare actualizată" --> I

    I -- "Nu" --> K["Afișarea opțiunii de creare a unei programări<br/>Serviciul recomandat este stabilit automat prin AFD<br/>(automat finit determinist)"]
    K --> K1["Crearea unei programări noi"]
    K1 -- "Stare actualizată" --> I

    %% Traseu Programări
    E -- "Programări" --> L["Vizualizarea programărilor"]

    L --> LA["Afișarea activității curente<br/>(terapeut selectat, programare activă sau posibilitate de rezervare)"]
    L --> LB["Afișarea istoricului programărilor<br/>(programări finalizate, anulate sau viitoare)"]

    %% Traseu Jurnale
    E -- "Jurnale" --> M{"Există ședințe finalizate<br/>fără jurnal completat?"}

    M -- "Da" --> MA["Afișarea formularului de jurnal<br/>pentru o ședință finalizată"]
    MA --> MA1{"Există mai multe ședințe<br/>eligibile pentru completare?"}

    MA1 -- "Da" --> MA2["Selectarea ședinței dintr-o listă"]
    MA1 -- "Nu" --> MA3["Preselectarea automată a ședinței"]

    MA2 & MA3 --> MB["Completarea metricilor de recuperare<br/>durere, dificultate și oboseală pe scară 1-10<br/>comentarii opționale"]

    MB --> MB1["Salvarea jurnalului pacientului"]
    MB1 -- "Stare actualizată" --> M

    M -- "Nu" --> N["Afișarea stării: pacientul este la zi<br/>cu jurnalele de recuperare"]
    N --> NC["Afișarea istoricului jurnalelor completate"]
    NC --> NC1["Editarea unui jurnal existent<br/>și salvarea modificărilor"]
    NC1 -- "Stare actualizată" --> M

    %% Traseu Mesaje
    E -- "Mesaje" --> O{"Relația pacient-terapeut<br/>este activă?"}

    O -- "Da" --> OA["Comunicare bidirecțională activă<br/>între pacient și terapeut"]
    O -- "Nu" --> OB["Vizualizarea istoricului conversațiilor<br/>Trimiterea de mesaje noi este dezactivată"]

    %% Traseu Profil
    E -- "Profil" --> P["Vizualizarea datelor personale<br/>și a preferințelor pacientului"]

    P --> PA["Editarea datelor de profil<br/>(nume, prenume, telefon, sport, gen și locație preferată)"]
    P --> PB["Schimbarea parolei prin opțiune dedicată"]

    PA & PB --> PC["Salvarea modificărilor"]
    PC -- "Stare actualizată" --> P

    %% Traseu Notificări
    E -- "Notificări" --> Q["Afișarea notificărilor in-app<br/>în bara de navigare"]

    %% Clase de stilizare
    classDef auth fill:#f1f5f9,stroke:#64748b,stroke-width:2px,color:#334155;
    classDef check fill:#fef9c3,stroke:#ca8a04,stroke-width:2px,color:#713f12;
    classDef action fill:#dbeafe,stroke:#2563eb,stroke-width:2px,color:#1e3a8a;
    classDef success fill:#dcfce7,stroke:#16a34a,stroke-width:2px,color:#14532d;
    classDef warning fill:#fff7ed,stroke:#f97316,stroke-width:2px,color:#7c2d12;
    classDef danger fill:#fee2e2,stroke:#dc2626,stroke-width:2px,color:#7f1d1d;

    class A,AA,C,C1,D auth;
    class B,F1,F3,G,I,M,MA1,O check;
    class E,F,H,J,K,L,LA,LB,MA,MA2,MA3,MB,P,Q action;
    class F2,F3B,H1,K1,MB1,N,NC,OA,PA,PB,PC success;
    class F3A warning;
    class J1,NC1,OB danger;
```


## Flux admin

```mermaid
---
config:
  theme: base
  flowchart:
    curve: basis
    nodeSpacing: 45
    rankSpacing: 55
---
flowchart TD
    A["Autentificarea administratorului"] --> AA["Redirecționare către ProtectedRoute"]
    AA --> B{"Rolul 'ADMIN' este prezent<br/>în token-ul JWT?"}
    
    B -- "Nu" --> C["Redirecționare la /unauthorized"]
    B -- "Da" --> D["Acces permis în Panoul de Administrare (Dashboard)"]
    D --> E{"Interacțiune cu Navbar (Modul Admin)"}
    
    %% Traseu Statistici
    E -- "Statistici" --> F["Selectare interval de date și filtre<br/>(Locație / Terapeut)"]
    F --> G["Lansare apeluri API REST concurente<br/>(Promise.all pentru performanță)"]
    G --> H["Memoizare algoritmică (useMemo)<br/>pentru calculul indicatorilor (KPI)"]
    H --> I["Randerare grafice vectoriale scalabile<br/>(Recharts: Bar, Area, Line)"]
    
    %% Traseu Locatii
    E -- "Gestiune Locații" --> J["Vizualizare listă locații clinice"]
    J --> JA["Adăugare / Editare detalii locație<br/>(Nume, adresă, contact)"]
    J --> JB["Comutare status (Toggle)<br/>Soft-delete logic pentru păstrarea istoricului"]
    
    %% Traseu Servicii
    E -- "Gestiune Servicii" --> K["Vizualizare catalog de servicii clinice"]
    K --> KA["Adăugare / Editare detalii serviciu<br/>(Nume, durată, preț, tip)"]
    KA --> KAA["Tariful actualizat se salvează ca snapshot<br/>la programare (nu alterează istoricul financiar)"]
    K --> KB["Comutare status (Toggle)<br/>Soft-delete logic"]
    
    %% Traseu Utilizatori
    E -- "Gestiune Utilizatori" --> L["Listare utilizatori cu filtre după rol/status<br/>și căutare textuală"]
    L --> M["Modificare status (Toggle Active)"]
    M --> N{"Status selectat?"}
    
    N -- "Reactivare" --> NA["Apel Keycloak (setUserEnabled=true)<br/>+ Sincronizare DB locală (active=true)"]
    
    N -- "Dezactivare" --> NB["Apel Keycloak (setUserEnabled=false)<br/>+ Sincronizare DB locală (active=false)"]
    NB --> NC["Propagare status de inactivitate<br/>către serviciile de profil (Pacienți/Terapeuți)"]
    NC --> ND["Trimitere apel intern (Feign) către programari-service:<br/>Anulare automată a programărilor viitoare"]
    
    %% Clase de stilizare
    classDef auth fill:#f1f5f9,stroke:#64748b,stroke-width:2px,color:#334155;
    classDef check fill:#fef9c3,stroke:#ca8a04,stroke-width:2px,color:#713f12;
    classDef action fill:#dbeafe,stroke:#2563eb,stroke-width:2px,color:#1e3a8a;
    classDef success fill:#dcfce7,stroke:#16a34a,stroke-width:2px,color:#14532d;
    classDef danger fill:#fee2e2,stroke:#dc2626,stroke-width:2px,color:#7f1d1d;

    class A,AA,D auth;
    class B,N check;
    class E,F,G,J,JA,K,KA,L,M action;
    class H,I,NA,KAA success;
    class C,JB,KB,NB,NC,ND danger;
```


## Flux chat (WebSocket / STOMP)

```mermaid
---
config:
  theme: redux-color
---
sequenceDiagram
    autonumber
    participant U as Client (React STOMP)
    participant AG as api-gateway (BFF / WS)
    participant CS as chat-service (STOMP)
    participant PS as programari-service
    participant RMQ as RabbitMQ (Broker)
    participant NS as notificari-service

    U->>AG: Trimite mesaj (WS frame: '/app/chat.send')
    AG->>CS: Rutează către WebSocketChatController
    activate CS
    CS->>PS: Feign: Verifică status relație (pacient, terapeut)
    activate PS
    PS-->>CS: Răspuns (Boolean: isActiva)
    deactivate PS

    alt Relația este Inactivă / Arhivată
        rect rgba(220, 38, 38, 0.08)
            CS->>CS: Aruncă ForbiddenOperationException
            CS-->>U: WS error pe '/user/queue/errors' (Mesaj blocat)
        end
    else Relația este Activă
        rect rgba(22, 163, 74, 0.08)
            alt Prima interacțiune (Lazy Init)
                CS->>CS: Inserează înregistrarea în tabela 'conversatii'
            end
            CS->>CS: Salvează mesajul în tabela 'mesaje'
            CS->>RMQ: Publică eveniment 'notificare.mesaj.nou'
            CS-->>U: WS broadcast pe '/queue/conversatii/{id}'
            deactivate CS
        end
        
        Note over RMQ, NS: Flux asincron pentru Alerte In-App
        rect rgba(59, 130, 246, 0.08)
            activate RMQ
            RMQ->>NS: Consumă din coada de notificări
            deactivate RMQ
            activate NS
            NS->>NS: Salvează notificare în DB
            deactivate NS
        end
    end
```


# STOMP PPT

```mermaid
sequenceDiagram

    autonumber

    participant C  as Client React

    participant SI as StompInterceptor

    participant SC as SecurityContext

    participant CS as chat-service

    participant PR as programari-service



    C->>SI: STOMP [SEND] + JWT în header



    rect rgb(220, 248, 220)

        Note over SI,SC: preSend() — per cadru STOMP

        SI->>SI: decode(JWT) → JwtAuthToken

        SI->>SC: setAuthentication(jwtAuthToken)

        SI->>SI: accessor.setUser(jwtAuthToken)

    end



    SI->>CS: Rutează cadrul la ChatService



    rect rgb(220, 234, 255)

        Note over CS,PR: Validare clinică + Persistență

        CS->>PR: Feign GET /relatii/status-keycloak

        Note right of CS: JWT auto-injectat din SecurityContext

        PR-->>CS: relație activă = true

        CS->>CS: Salvează mesaj + broadcast WebSocket

    end



    CS-->>SI: Procesare finalizată



    rect rgb(255, 218, 228)

        Note over SI,SC: postSend() — CRITIC

        SI->>SC: clearContext()

        Note over SC: Thread revine în pool fără credențiale

    end
```

# Arhitectura sistemului PPT

```mermaid
---
config:
  layout: elk
  flowchart:
    curve: basis
---
flowchart LR
    classDef client fill:#eff6ff,stroke:#3b82f6,stroke-width:2px,color:#1e3a8a;
    classDef edge fill:#fefce8,stroke:#eab308,stroke-width:2px,color:#713f12;
    classDef auth fill:#fecaca,stroke:#dc2626,stroke-width:2px,color:#991b1b;
    classDef domain fill:#f0fdf4,stroke:#22c55e,stroke-width:2px,color:#14532d;
    classDef mq fill:#faf5ff,stroke:#a855f7,stroke-width:2px,color:#581c87;
    classDef db fill:#f1f5f9,stroke:#64748b,stroke-width:2px,color:#334155;

    subgraph ClientLayer ["1. Nivelul Client"]
        React["📱 Aplicație React SPA<br/>(Interfață Utilizator)"]:::client
    end

    subgraph EdgeLayer ["2. Nivelul de Margine"]
        Keycloak["🛡️ Keycloak Server<br/>(Identitate & Autentificare)"]:::auth
        APIGW["🚪 API Gateway<br/>(Spring WebFlux / BFF)"]:::edge
    end

    subgraph DomainLayer ["3. Nivelul Serviciilor de Domeniu"]
        US["👤 user-service"]:::domain
        PS["🏥 pacienti-service"]:::domain
        TS["👨‍⚕️ terapeuți-service"]:::domain
        ProgS["📅 programari-service"]:::domain
        SS["📋 servicii-service"]:::domain
        CS["💬 chat-service"]:::domain
        NS["🔔 notificari-service"]:::domain
        RabbitMQ{{"🐰 RabbitMQ Broker<br/>(Topic Exchange & DLQ)"}}:::mq
    end

    subgraph DataLayer ["4. Nivelul de Date"]
        DB_US[("🗄️ user_db")]:::db
        DB_PS[("🗄️ pacienti_db")]:::db
        DB_TS[("🗄️ terapeuți_db")]:::db
        DB_ProgS[("🗄️ programari_db")]:::db
        DB_SS[("🗄️ servicii_db")]:::db
        DB_CS[("🗄️ chat_db")]:::db
        DB_NS[("🗄️ notificari_db")]:::db
    end

    React -->|"Cereri REST & WebSocket"| APIGW
    APIGW <-->|"Proxy Auth & Validare JWKS"| Keycloak
    US -.->|"Keycloak Admin API (REST)"| Keycloak

    APIGW ==> US
    APIGW ==> PS
    APIGW ==> TS
    APIGW ==> ProgS
    APIGW ==> SS
    APIGW ==> CS
    APIGW ==> NS

    ProgS -.->|"notificare.programare.*"| RabbitMQ
    PS -.->|"notificare.jurnal.completat"| RabbitMQ
    CS -.->|"notificare.mesaj.nou"| RabbitMQ
    RabbitMQ -.->|"notificare.#"| NS

    US --- DB_US
    PS --- DB_PS
    TS --- DB_TS
    ProgS --- DB_ProgS
    SS --- DB_SS
    CS --- DB_CS
    NS --- DB_NS
```

# Sliding window PPT
```mermaid
---
config:
  layout: elk
  flowchart:
    curve: basis
---
flowchart LR
 subgraph SlidingWindow[" "]
    direction LR
        I1["Iterația 1<br><b>08:00–09:00</b><br>✓ Liber"]
        I2["Iterația 2<br><b>09:10–10:10</b><br>✗ Suprapunere"]
        I3["Iterația 3<br><b>10:20–11:20</b><br>✓ Liber"]
        I4["Iterația 4<br><b>11:30–12:30</b><br>✗ Depășit"]
  end
    I1 ==> I2
    I2 ==> I3
    I3 ==> I4
    Config["🗓️ Program: 08:00–12:00 <br> ⏱️ Serviciu: 60 min  <br>⏳ Buffer: +10 min <br> 🔒 Blocat: 09:00–10:00"] ~~~ SlidingWindow
    SlidingWindow ~~~ Result["<b>Sloturi returnate: 08:00 · 10:20</b>"]
    I1 -.-> Result
    I3 -.-> Result

     Config:::config
     I1:::liber
     I2:::eroare
     I3:::liber
     I4:::eroare
     Result:::rezultat
    classDef config fill:#f8fafc,stroke:#94a3b8,stroke-width:1px,color:#334155,font-weight:bold,stroke-dasharray: 5 5
    classDef liber fill:#dcfce7,stroke:#22c55e,stroke-width:2px,color:#14532d
    classDef eroare fill:#fee2e2,stroke:#ef4444,stroke-width:2px,color:#991b1b
    classDef rezultat fill:#dbeafe,stroke:#3b82f6,stroke-width:2px,color:#1e3a8a
    style SlidingWindow fill:transparent,stroke:transparent
```


# Scrierea duala PPT
```mermaid
---
config:
  layout: dagre
---
flowchart LR
    A["Admin dezactivează contul"] --> T["Tranzacție @Transactional"]
    T --> DB["Stare locală modificată<br/>(în tranzacție, neconfirmat încă)"]
    DB --> K["Apel sincron → Keycloak"]
    K --> D{"Keycloak confirmă?"}
    D -- "Nu" --> RB["Excepție propagată<br/>→ rollback total"]
    RB --> S1["ACTIVE în ambele sisteme<br/>(nicio schimbare)"]
    D -- "Da" --> P["Apel Feign → profil + programări<br/>(best-effort, excepție absorbită)"]
    P --> C["Commit tranzacție SQL"]
    C --> S2["INACTIVE local + Keycloak"]
    C -. "risc rezidual: commit eșuează<br/>DUPĂ confirmarea Keycloak" .-> R["Cont fantomă"]

     A:::neutral
     T:::neutral
     DB:::neutral
     K:::neutral
     D:::decision
     RB:::neutral
     P:::neutral
     C:::neutral
     S1:::success
     S2:::success
     R:::risk
    classDef neutral fill:#BBDEFB,stroke:#1565C0,color:#1B1B1B
    classDef decision fill:#E1BEE7,stroke:#6A1B9A,color:#1B1B1B
    classDef success fill:#C8E6C9,stroke:#2E7D32,color:#1B1B1B
    classDef risk fill:#FFCDD2,stroke:#C62828,color:#1B1B1B
```