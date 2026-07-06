# Anexa 6 și Anexa 7 — Diagrame Arhitecturale, de Flux și Secvență

Acest fișier conține toate diagramele tehnice din anexele lucrării de dizertație, optimizate pentru parsare fără erori în interpretorul Mermaid.

---

## Anexa 6. Diagrame arhitecturale și de flux (Capitolul 5)

### Figura 6.1. Diagrama de tranziție a automatului finit clinic
```mermaid
stateDiagram-v2
    direction LR

    [*] --> EvaluareInitiala : Nicio evaluare in DB

    EvaluareInitiala --> TratamentActiv : Terapeut completeaza Evaluarea Initiala - buget N sedinte

    TratamentActiv --> Reevaluare : Sedinte finalizate egale sau peste N - buget epuizat

    Reevaluare --> TratamentActiv : Terapeut finalizeaza Reevaluarea - nou buget

    state "S_A: Evaluare Inițială" as EvaluareInitiala
    state "S_B: Tratament Activ" as TratamentActiv
    state "S_C: Reevaluare" as Reevaluare
```

---

### Figura 6.2. Diagrama de secvență — Generarea sloturilor de disponibilitate
```mermaid
sequenceDiagram
    autonumber
    participant C as Client React
    participant PS as programari-service
    participant TS as terapeuti-service
    participant SS as servicii-service
    participant DB as programari_db

    C->>PS: GET /api/programari/disponibilitate?terapeutKeycloakId=&locatieId=&data=&serviciuId=

    rect rgb(240, 248, 255)
        Note over PS,TS: Faza 1 - Colectarea constrângerilor - fail-fast
        PS->>TS: GET /terapeut/by-keycloak/{keycloakId} - returnează Map cu id numeric
        PS->>TS: GET /concediu/check/terapeut/{terapeutId}/data/{data} - false
        PS->>TS: GET /disponibilitate/terapeut/{terapeutId}/locatie/{locatieId}/zi/{zi} - DisponibilitateDTO (oraInceput, oraSfarsit)
        PS->>SS: GET /servicii/{serviciuId} - DetaliiServiciuDTO (durataMinute)
    end

    rect rgb(245, 245, 220)
        Note over PS,DB: Faza 2 - Preluarea agendei locale
        PS->>DB: findByTerapeutKeycloakIdAndDataAndStatus
        DB-->>PS: List<Programare> programăriExistente
    end

    rect rgb(244, 255, 244)
        Note over PS: Faza 3 - Fereastra glisantă
        PS->>PS: cursor = oraInceput
        loop Avans cursor cu durataMinute + 10 min buffer
            PS->>PS: Verifică suprapunere și orar viitor
            PS->>PS: Adaugă slot valid în listă
        end
    end

    PS-->>C: List<LocalTime> (sloturi libere)
```

---

### Figura 6.3. Diagrama de decizie — Partiționarea temporală la granița zilei
```mermaid
flowchart TD
    A["Start - gasesteInFereastra"] --> B["centruFereastra = acum + oreInainte"]
    B --> C["startFereastra = centru - marja"]
    C --> D["endFereastra = centru + marja"]
    D --> E{"startFereastra.date == endFereastra.date?"}

    E -- Da --> F["Query unic\nfindProgramariInFereastra"]
    E -- Nu - granița nopții depășită --> G["Sub-query 1 - Ziua 1<br/>start.date, start.time la 23:59:59"]
    E -- Nu - granița nopții depășită --> H["Sub-query 2 - Ziua 2<br/>end.date, 00:00:00 la end.time"]

    G --> I["Concatenare liste"]
    H --> I
    F --> J["Returnează lista programări"]
    I --> J
```

---

### Figura 6.4. Diagrama de secvență — Propagarea contextului de securitate STOMP
```mermaid
sequenceDiagram
    autonumber
    participant C as Client WebSocket React
    participant SI as StompSecurityInterceptor
    participant SC as SecurityContextHolder (ThreadLocal)
    participant CS as ChatService
    participant FI as FeignRequestInterceptor
    participant PR as programari-service

    C->>SI: Cadru STOMP SEND cu antet Authorization
    
    rect rgb(240, 255, 240)
        Note over SI,SC: preSend - Inițializare context local
        SI->>SI: Extrage și validează JWT
        SI->>SC: setAuthentication
        SI->>SI: accessor.setUser - leagă identitatea de sesiune
    end

    SI->>CS: Rutare cadru STOMP la ChatController

    rect rgb(240, 248, 255)
        Note over CS,PR: Execuție logică clinică inter-servicii
        CS->>PR: getRelatieStatus
        activate FI
        FI->>SC: getAuthentication - citește din ThreadLocal
        FI->>PR: HTTP GET securizat
        deactivate FI
        PR-->>CS: relație activă - true
        CS->>CS: Persistă și retransmite mesajul
    end

    CS-->>SI: Procesare finalizată

    rect rgb(255, 240, 240)
        Note over SI,SC: postSend - Igienizare preventivă thread
        SI->>SC: clearContext - previne credential leakage
    end
```

---

### Figura 6.5. Diagrama de secvență — Înregistrare utilizator cu compensare Saga
```mermaid
sequenceDiagram
    autonumber
    participant C as Client Formular Înregistrare
    participant US as user-service (KeycloakService)
    participant KC as Keycloak IAM
    participant DB as user_db (MySQL)
    participant PS as pacienti-service / terapeuti-service

    C->>US: POST /register

    rect rgb(240, 255, 240)
        Note over US,KC: Faza 1-2 - Scriere în Keycloak
        US->>KC: createUser - 201 Created
        US->>KC: assignRealmRole - 204
    end

    rect rgb(240, 248, 255)
        Note over US,DB: Faza 3 - Salvare locală
        US->>DB: userRepository.save
    end

    rect rgb(255, 250, 240)
        Note over US,PS: Faza 4 - Inițializare profil downstream
        US->>PS: POST /initialize/{keycloakId}
        PS-->>US: 500 Internal Server Error
    end

    rect rgb(255, 240, 240)
        Note over US,KC: Compensare automată
        US->>KC: deleteUser - 204
        Note over US,DB: Spring execută rollback SQL
    end

    US-->>C: 500 Registration Failed
```

---

### Figura 6.6. Diagrama arhitecturală — Topologia RabbitMQ cu DLX
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

---

### Figura 6.7. Diagrama de secvență — Fluxul de schimbare a terapeutului
```mermaid
sequenceDiagram
    autonumber
    participant P as Pacient React
    participant AG as api-gateway BFF
    participant PCS as pacienti-service
    participant PRS as programari-service
    participant RMQ as RabbitMQ
    participant NTS as notificari-service
    participant T as Terapeut vechi WebSocket

    P->>AG: POST /choose-terapeut/{terapeutKeycloakId}
    AG->>PCS: chooseTerapeut
    PCS->>PCS: Actualizează preferința în DB locală
    PCS->>PRS: DELETE /cancel-upcoming - Feign
    activate PRS
    PRS->>PRS: dezactiveazaRelatiaActiva - arhivare
    PRS->>PRS: Anulează programările viitoare
    PRS->>RMQ: Publică evenimentul programareAnulataDePacient
    deactivate PRS
    RMQ->>NTS: Consumă evenimentul
    NTS->>T: Notificare în timp real
    PCS-->>AG: PacientResponse
    AG-->>P: Succes (UI actualizat)
```

---

### Figura 6.8. Diagrama de secvență — Trimiterea mesajelor în timp real și validarea relației clinice
```mermaid
sequenceDiagram
    autonumber
    actor U as Client (React STOMP)
    participant AG as api-gateway (BFF / WS)
    participant CS as chat-service (WebSocket / STOMP)
    participant PS as programari-service
    participant RMQ as RabbitMQ (Broker)
    participant NS as notificari-service

    U->>AG: Trimite mesaj (WS frame: Destination '/app/chat.send')
    AG->>CS: Rutează către WebSocketChatController
    activate CS
    CS->>PS: Feign: Verifică status relație (pacient, terapeut)
    activate PS
    PS-->>CS: Răspuns (Boolean: isActiva)
    deactivate PS

    alt Relația este Inactivă / Arhivată
        CS->>CS: Aruncă ForbiddenOperationException
        CS-->>U: WS error subscrisă la '/queue/errors' (Mesaj blocat)
    else Relația este Activă
        alt Prima interacțiune (Lazy Init)
            CS->>CS: Inserează înregistrarea în tabela 'conversatii'
        end
        CS->>CS: Salvează mesajul în tabela 'mesaje'
        CS->>RMQ: Publică eveniment 'notificare.mesaj.nou' în 'notificari.exchange'
        CS-->>U: WS broadcast pe '/queue/conversatii/{id}' (Livrare instantă)
        deactivate CS
        
        activate RMQ
        RMQ->>NS: Consumă din coada de notificări
        deactivate RMQ
        activate NS
        NS->>NS: Salvează notificare în DB (Alerte in-app)
        deactivate NS
    end
```

---

## Anexa 7. Diagrame de flux și navigare

### Figura 7.1. Harta de navigare și decizie a pacientului
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
    A["Autentificarea pacientului"] --> AA["Redirecționare către ProfileGuard"]
    AA --> B{"Profil complet?<br/>(CNP și Data Nașterii)"}
    
    B -- "Nu" --> C["Redirecționare la CompleteProfile<br/>(Completare CNP, Data Nașterii, Sport/Detalii)"]
    C --> C1["Salvare date obligatorii"] --> AA
    
    B -- "Da" --> D["Redirecționare la Pagina Principală (Homepage)"]
    D --> E{"Interacțiune cu bara de navigare"}
    
    %% Traseu Acasa
    E -- "Acasă" --> F["Afișare detalii pacient<br/>(Nume, vârstă, diagnostic și terapeut/locație)"]
    F --> F1{"Diagnostic configurat?"}
    
    F1 -- "Da" --> F2["Afișare bară progres tratament<br/>(Ședințe efectuate / recomandate)"]
    F2 --> F3{"Plan completat?"}
    F3 -- "Da" --> F3A["Afișare mesaj: plan finalizat<br/>se recomandă reevaluarea"]
    F3 -- "Nu" --> F3B["Afișare normală progres"]
    
    F1 -- "Nu" --> G{"Terapeut selectat?"}
    F3A --> G
    F3B --> G
    
    G -- "Nu" --> H["Afișare TerapeutSection<br/>(Alegere Terapeut și Locație preferată)"]
    H --> H1["Salvare preferințe"] --> F
    
    G -- "Da" --> I{"Are programare viitoare?"}
    I -- "Da" --> J["Afișare ActiveAppointmentCard<br/>(Detalii + Opțiune Anulare)"]
    J --> J1["Anulare programare"] --> F
    
    I -- "Nu" --> K["Afișare BookingWidget<br/>(Programare rapidă - servicii stabilite automat de FSM)"]
    K --> K1["Creare programare nouă"] --> F
    
    %% Traseu Programari
    E -- "Programări" --> L["Vizualizare panou Programările Mele"]
    L --> LA["Secțiunea Activitate Curentă<br/>(Fără terapeut: TerapeutSection<br/>Cu programare: ActiveAppointmentCard<br/>Fără programare: BookingWidget)"]
    L --> LB["Secțiunea Istoric Programări<br/>(Afișare HistoryAppointmentCard pentru programări finalizate/anulate)"]
    
    %% Traseu Jurnale
    E -- "Jurnale" --> M{"Are ședințe finalizate<br/>fără jurnal completat?"}
    M -- "Da" --> MA["Formular Jurnal Programare"]
    MA --> MA1{"Multiple ședințe?"}
    MA1 -- "Da" --> MA2["Dropdown selectare programare"]
    MA1 -- "Nu" --> MA3["Preselectare automată"]
    
    MA2 & MA3 --> MB["Introducere metrici:<br/>durere, dificultate, oboseală (1-10)<br/>+ Comentarii (opțional)"]
    MB --> MB1["Salvare Jurnal"] --> M
    
    M -- "Nu" --> N["Afișare stare 'La zi!'"]
    N --> NC["Secțiune Istoric Jurnale<br/>(Listă jurnale completate cu metrici vizuale)"]
    NC --> NC1["Editare jurnal existent (✏️)<br/>Modificare metrici și salvare inline"] --> M
    
    %% Traseu Mesaje
    E -- "Mesaje" --> O{"Relația pacient-terapeut<br/>este activă?"}
    O -- "Da" --> OA["Comunicare bidirecțională activă cu terapeutul"]
    O -- "Nu" --> OB["Vizualizare istoric mesaje cu foștii terapeuți<br/>(Trimiterea de mesaje noi este dezactivată)"]
    
    %% Traseu Profil
    E -- "Profil" --> P["Vizualizare date personale și preferințe terapeut/locație"]
    P --> PA["Editează date profil<br/>(Nume, prenume, telefon, sport, gen, locație preferată)"]
    P --> PB["Schimbare Parolă (opțiune dedicată)"]
    PA & PB --> PC["Salvare modificări"] --> P
    
    %% Traseu Notificari
    E -- "Notificări" --> Q["Dropdown în bara de navigare cu alerte in-app"]

    %% Clase de stilizare
    classDef auth fill:#f1f5f9,stroke:#64748b,stroke-width:2px,color:#334155;
    classDef check fill:#fef9c3,stroke:#ca8a04,stroke-width:2px,color:#713f12;
    classDef action fill:#dbeafe,stroke:#2563eb,stroke-width:2px,color:#1e3a8a;
    classDef success fill:#dcfce7,stroke:#16a34a,stroke-width:2px,color:#14532d;
    classDef danger fill:#fee2e2,stroke:#dc2626,stroke-width:2px,color:#7f1d1d;

    class A,AA,C,C1,D auth;
    class B,F1,F3,G,I,L1,L3,M,MA1,O check;
    class E,F,H,J,K,L,LA,LB,P,Q action;
    class F2,F3B,H1,K1,MA,MA3,MB,MB1,N,NC,OA,PA,PB,PC success;
    class F3A,J1,OB,NC1 danger;
```

---

### Figura 7.2. Fluxul operațional al terapeutului
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
    A["Autentificarea terapeutului"] --> AA["Redirecționare pe Homepage (Acasă)"]
    
    AA --> B{"Profilul este<br>complet configurat?"}
    
    B -- Nu --> C["Afișare banner de avertizare pe Homepage<br>(Sugerează configurarea specializării și orarului)"]
    C --> F{"Interacțiune cu Navbar / Pagini"}
    
    B -- Da --> E["Terapeutul devine vizibil în sistem<br>Pacienții îl pot selecta și programa"]
    E --> F
    
    %% Traseu Acasa / Calendar
    F -->|"Acasă (Calendar)"| G["Afișare FullCalendar săptămânal cu pacienții săi<br>Filtrare după locații clinice active"]
    G --> H{"Programare marcată cu ⭐?"}
    H -- Da --> I["Indicator 'Prima întâlnire' cu pacientul"]
    H -- Nu --> J["Programare normală"]
    I & J --> K["Click pe programare: Deschidere panou detalii<br>Nume, Telefon, Dată/Oră, Serviciu, Locație, Status"]
    K --> L{"Perioada programării?"}
    L -- "În viitor (PROGRAMATĂ)" --> LA["Posibilitate de Anulare programare"]
    L -- "În trecut (FINALIZATĂ)" --> LB["Posibilitate de marcare ca 'Neprezentare'"]

    %% Traseu Pagina Pacienti
    F -->|"Pacienți"| M["Listă pacienți activi și arhivați<br>Căutare după nume sau diagnostic"]
    M --> N["Selectare pacient: Deschidere Fișa Pacientului"]
    N --> O["Date demografice & Bară de progres tratament<br>Ședințe efectuate / Recomandate"]
    O --> P{"Pacientul are jurnale completate?"}
    P -- Da --> PA["Afișare grafic evolutiv de recuperare<br>Durere, Oboseală, Dificultate exerciții"]
    P -- Nu --> PB["Ascundere grafic de evoluție"]
    
    PA & PB --> Q{"Selectare Tab Fișă Pacient"}
    
    Q -- "Evaluări" --> QA["Vizualizare istoric evaluări"]
    QA --> QAA{"Este evaluare proprie?"}
    QAA -- Da --> QAAB{"Relația este activă?"}
    QAAB -- Da --> QAAC["Permite Editarea evaluării"]
    QAAB -- Nu --> QAAD["Editarea este dezactivată<br>(Relație arhivată)"]
    QAA -- Nu --> QAAE["Doar vizualizare (Scrisă de alt terapeut)"]
    
    Q -- "Evoluții" --> QB["Timeline de notițe private<br>Editarea propriilor evoluții"]
    Q -- "Programări" --> QC["Listă istoric programări<br>Filtrare opțională a celor anulate"]
    Q -- "Jurnale" --> QD["Feedback detaliat completat de pacient"]

    %% Traseu Pagina Evaluari
    F -->|"Evaluări"| R{"Relație pacient-terapeut activă?"}
    R -- Da --> RA["Formular adăugare Evaluare Nouă<br>Tip (Inițială/Reevaluare), Diagnostic (Vizibil)<br>Serviciu/Ședințe recomandate & Observații private"]
    R -- Nu --> RB["Adăugarea este dezactivată"]

    %% Traseu Pagina Evolutii
    F -->|"Evoluții"| S{"Relație pacient-terapeut activă?"}
    S -- Da --> SA["Adăugare notă privată de evoluție oricând<br>Independent de existența unei programări active"]
    S -- Nu --> SB["Adăugarea este dezactivată"]
    SA & SB --> SC["Vizualizare istoric notițe private pacient"]

    %% Traseu Pagina Mesaje
    F -->|"Mesaje (Chat)"| T{"Statutul pacientului?"}
    T -- Activ --> TA["Comunicare bidirecțională activă"]
    T -- Arhivat --> TB["Vizualizare istoric conversație<br>Trimiterea de mesaje noi este blocată"]

    %% Traseu Profil & Notificari
    F -->|"Profil"| U["Editare date personale, parolă, poză,<br>concedii și disponibilitate orar"]
    U --> B
    
    F -->|"Notificări"| V["Dropdown în navbar cu alerte in-app"]

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
    class LA,M,QA,QAAC,QB,QC,QD,RA,SA,TA success;
    class LB,QAAD,QAAE,RB,SB,TB danger;
```

---

### Figura 7.3. Arhitectura panoului administrativ și topologia datelor
```mermaid
graph TD
    subgraph AdminPanel ["Modul Administrare - SPA Client"]
        AL[Modul Locații Fizice]
        AS[Modul Servicii și Tarifare]
        AU[Modul Utilizatori și Suspendări]
        AST[Modul Business Intelligence]
    end

    subgraph Edge ["Strat de Margine - API Gateway"]
        RBAC["Validare Rol Administrativ<br/>Protecție la nivel de rută"]
    end

    subgraph Backend ["Microservicii de Domeniu"]
        TS["terapeuti-service<br/>terapeuti_db"]
        SS["servicii-service<br/>servicii_db"]
        US["user-service<br/>user_db si IAM"]
        PS["programari-service<br/>programari_db"]
    end

    AL -->|Cereri securizate HTTP| RBAC
    AS -->|Cereri securizate HTTP| RBAC
    AU -->|Cereri securizate HTTP| RBAC
    AST -->|Interogări HTTP concurente| RBAC

    RBAC -->|Ștergere logică| TS
    RBAC -->|Mutații de catalog| SS
    RBAC -->|Gestionare conturi| US
    RBAC -->|Agregări statistice| PS
```

---

### Figura 7.4. Fluxul operațional al administratorului și procesarea statistică
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
    AA --> B{"Rolul 'ADMIN' este prezent<br/>în realm_access.roles?"}
    
    B -- "Nu" --> C["Redirecționare la /unauthorized"]
    B -- "Da" --> D["Acces permis în Panoul de Administrare (Dashboard)"]
    D --> E{"Interacțiune cu Navbar (Modul Admin)"}
    
    %% Traseu Statistici
    E -- "Statistici / BI" --> F["Selectare interval de date și filtre<br/>(Locație / Terapeut)"]
    F --> G["Lansare apeluri API REST concurente<br/>(Promise.all pentru KPI-uri și diagrame)"]
    G --> H["Memorizare algoritmică (useMemo)<br/>pentru KPI-uri la nivel de client"]
    H --> I["Randerare grafice vectoriale scalabile<br/>(Recharts: BarChart, AreaChart, LineChart)"]
    
    %% Traseu Locatii
    E -- "Gestiune Locații" --> J["Vizualizare listă locații clinice active/inactive"]
    J --> JA["Adăugare / Editare detalii locație<br/>(Nume, adresă, oraș, județ, telefon)"]
    J --> JB["Toggle status active/inactiv<br/>(Soft-delete logic - păstrare istoric)"]
    
    %% Traseu Servicii
    E -- "Gestiune Servicii" --> K["Vizualizare catalog de servicii clinice"]
    K --> KA["Adăugare / Editare detalii serviciu<br/>(Nume, durată, preț, tip)"]
    KA --> KAA["Tariful actualizat se salvează ca snapshot<br/>la programare (nu afectează trecutul)"]
    K --> KB["Toggle status active/inactiv (Soft-delete)"]
    
    %% Traseu Utilizatori
    E -- "Gestiune Utilizatori" --> L["Listare utilizatori cu filtre după rol/status<br/>și căutare textuală"]
    L --> M["Modificare status (Toggle Active)"]
    M --> N{"Status selectat?"}
    
    N -- "Reactivare" --> NA["Apel Keycloak (setUserEnabled=true)<br/>+ Sincronizare DB locală (active=true)"]
    
    N -- "Dezactivare" --> NB["Apel Keycloak (setUserEnabled=false)<br/>+ Sincronizare DB locală (active=false)"]
    NB --> NC["Propagare status de inactivitate<br/>către serviciile de profil (Pacienți/Terapeuți)"]
    NC --> ND["Trimitere Feign Client în programari-service:<br/>Anulare automată a tuturor programărilor viitoare"]
    
    %% Clase de stilizare
    classDef auth fill:#f1f5f9,stroke:#64748b,stroke-width:2px,color:#334155;
    classDef check fill:#fef9c3,stroke:#ca8a04,stroke-width:2px,color:#713f12;
    classDef action fill:#dbeafe,stroke:#2563eb,stroke-width:2px,color:#1e3a8a;
    classDef success fill:#dcfce7,stroke:#16a34a,stroke-width:2px,color:#14532d;
    classDef danger fill:#fee2e2,stroke:#dc2626,stroke-width:2px,color:#7f1d1d;

    class A,AA,D auth;
    class B,N check;
    class E,F,G,J,JA,K,KA,L,M action;
    class H,I,NA success;
    class C,JB,KAA,KB,NB,NC,ND danger;
```

```
