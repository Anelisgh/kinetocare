# KinetoCare — Platformă Clinică cu Arhitectură Microservicii

Platformă software distribuită pentru gestionarea activității clinicilor de kinetoterapie, dezvoltată pornind de la analiza unei aplicații reale din domeniu și a nevoilor identificate prin consultarea directă a kinetoterapeuților și a specialiștilor în recuperare medicală.

Conectează pacienții cu terapeuții, acoperind end-to-end fluxul clinic: programări, evaluări, monitorizarea progresului și comunicare în timp real.

---

## 🎯 Funcționalități principale

### 👤 Modul Pacient
- **Programare online** cu selecție automată a serviciului (în funcție de etapa tratamentului) și vizualizarea sloturilor disponibile în timp real.
- **Pagina principală** cu diagnosticul curent, serviciul activ și numărul de ședințe rămase până la reevaluare.
- **Jurnal de recuperare** completat după fiecare ședință (nivel durere, oboseală, dificultate exerciții).
- **Selectarea terapeutului preferat** pe baza locației și specializării.

<details>
<summary>🔍 <b>Vezi fluxul detaliat de utilizare și navigare (Pacient)</b></summary>

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

</details>

### 👨‍⚕️ Modul Terapeut
- **Calendar interactiv** cu programările zilnice și săptămânale (FullCalendar).
- **Fișa completă a fiecărui pacient**: evaluări inițiale, reevaluări și grafice de evoluție generate automat din jurnalele de recuperare.
- **Setarea programului de lucru** per locație și gestionarea concediilor.
- **Statistici rapide**: programări active, pacienți ce necesită reevaluare.

<details>
<summary>🔍 <b>Vezi fluxul detaliat de utilizare și navigare (Terapeut)</b></summary>

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

</details>

### ⚙️ Modul Administrativ
- **Gestiunea locațiilor** și a catalogului de servicii (tip, preț, durată).
- **Dezactivarea conturilor** cu anularea automată a programărilor viitoare aferente.
- **Statistici agregate per locație**: programări, venituri, terapeuți activi, rată anulări.

<details>
<summary>🔍 <b>Vezi fluxul detaliat de utilizare și navigare (Administrator)</b></summary>

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

</details>

### 🔄 Module comune
- **Chat bidirecțional** pacient-terapeut în timp real.
- **Sistem de notificări in-app** pentru programări, mesaje noi și remindere.

---

## 🏛 Arhitectură și Decizii Tehnice

### Arhitectură Microservicii (Database-per-Service)
Sistemul este structurat pe patru niveluri decuplate (*Client, Edge, Domain Services, Data Layer*), respectând strict modelul **Database-per-Service**. Fiecare microserviciu deține propria schemă MySQL izolată, prevenind cuplajul la nivel de date și permițând evoluția și scalarea independentă a modulelor.

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

Traficul extern este interceptat exclusiv de API Gateway și Keycloak, în timp ce serviciile de domeniu comunică sincron prin clienți declarativi OpenFeign și asincron prin mesagerie bazată pe evenimente.

| Microserviciu | Port intern | Bază de date | Responsabilitate principală |
| :--- | :--- | :--- | :--- |
| `api-gateway` | 8081 | — | Punct unic de intrare, rutare reactivă (WebFlux), Auth Proxy, agregare BFF |
| `user-service` | 8082 | `user_db` | Management conturi, sincronizare Keycloak Admin API, scriere duală cu commit amânat |
| `pacienti-service` | 8083 | `pacienti_db` | Profil clinic pacienți, jurnale zilnice de recuperare, emisie evenimente jurnal |
| `terapeuti-service` | 8084 | `terapeuti_db` | Profile terapeuți, orare de lucru per locație, evidență concedii |
| `programari-service` | 8085 | `programari_db` | Motor de booking, AFD stări clinice, calcul sliding window, scheduler remindere |
| `servicii-service` | 8086 | `servicii_db` | Catalog servicii medicale, durate, tarife și tipuri de terapie |
| `chat-service` | 8087 | `chat_db` | Mesagerie WebSocket/STOMP, validare relații active, canal dedicat erori |
| `notificari-service` | 8088 | `notificari_db` | Consumer asincron RabbitMQ, inserare idempotentă, management Dead Letter Queue |

### API Gateway & Arhitectură BFF (Backend-For-Frontend)
Construit pe **Spring Cloud Gateway** și **Spring WebFlux** (reactor Netty non-blocant), Gateway-ul îndeplinește un rol dublu:
1. **Reverse Proxy Securizat:** Centralizează politicile CORS, expune rutele externe (`/api/**`) și blochează accesul direct la endpoint-urile interne administrative ale microserviciilor.
2. **Backend-For-Frontend (BFF):** Implementează modelul hibrid de securitate token, gestionând jetoanele de reîmprospătare (Refresh Token) prin cookie-uri securizate `httpOnly` și `SameSite=Lax`, eliminând riscul de furt al sesiunii prin atacuri XSS în browser.

### Securitate Zero-Trust (OAuth2/JWT + Keycloak) & Extindere pe WebSocket/STOMP
Niciun microserviciu intern nu acordă încredere implicită traficului de rețea. Fiecare domeniu validează semnătura criptografică asimetrică a jetoanelor JWT prin setul de chei publice expus de Keycloak (JWKS). Autorizarea la nivel de resursă este guvernată prin **RBAC** (`ROLE_PACIENT`, `ROLE_TERAPEUT`, `ROLE_ADMIN`).

Pentru conexiunile de chat în timp real, filtrele HTTP tradiționale nu se aplică pe conexiunile persistente TCP. Securitatea este extinsă la nivel de cadru STOMP printr-un interceptor dedicat de canal (`StompSecurityInterceptor`):

```mermaid
---
config:
  theme: redux-color
---
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

La recepționarea fiecărui cadru `CONNECT` sau `SEND`, interceptorul decodează token-ul JWT, validează identitatea expeditorului împotriva `Principal`-ului și populează `SecurityContextHolder`. Când `chat-service` interoghează `programari-service` prin Feign pentru a verifica dacă relația terapeutică este activă, `FeignRequestInterceptor` extrage JWT-ul din contextul curent și îl propagă în antetul HTTP. În `postSend()`, contextul de securitate este curățat obligatoriu prin `clearContext()`, prevenind contaminarea firelor de execuție refolosite din thread pool.

### Logică de Business — Automatul Finit Determinist (AFD) pentru Stările Clinice
Pentru a ghida pacientul fără eroare și a elimina selecția greșită a serviciilor, tranziția planului de recuperare este modelată ca un **Automat Finit Determinist (AFD)** implementat în metoda `determinaServiciulCorect` din `ProgramareService`.

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

Starea clinică a pacientului este calculată determinist pe baza istoricului de evaluări și a numărului de ședințe finalizate (`countSedintePacientDupaData`):
- **Starea $S_A$ (Evaluare Inițială):** Dacă pacientul nu are nicio evaluare anterioară în baza de date, sistemul impune automat rezervarea unei ședințe de "Evaluare Inițială".
- **Starea $S_B$ (Tratament Activ):** Odată ce terapeutul a salvat evaluarea inițială recomandând un serviciu specific și un buget de $N$ ședințe, pacientul este restricționat la acel serviciu prescris.
- **Starea $S_C$ (Reevaluare):** La epuizarea pachetului de ședințe ($\text{efectuate} \ge N$), sistemul comută automat în starea de "Reevaluare", blocând continuarea oarbă a tratamentului până la o nouă examinare de specialitate.

### Algoritmul de Disponibilitate — Fereastră Glisantă (Sliding Window)
Calculul intervalelor libere pentru rezervări combină date distribuite din multiple microservicii printr-un algoritm de tip fereastră glisantă implementat în `ProgramareService.getSloturiDisponibile`:

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

Procesul se desfășoară în trei etape riguroase:
1. **Colectarea constrângerilor:** `programari-service` interoghează `terapeuti-service` pentru a verifica eventualele concedii și a obține orarul de lucru (`oraInceput`, `oraSfarsit`) pentru locația și ziua solicitate, respectiv `servicii-service` pentru durata exactă în minute a serviciului.
2. **Preluarea agendei:** Se extrag din baza locală toate programările deja confirmate (`StatusProgramare.PROGRAMATA`) ale terapeutului din ziua respectivă.
3. **Iterarea ferestrei glisante:** Un cursor temporal pornește de la `oraInceput`. La fiecare pas, metoda `esteLiber` testează coliziunea temporală ($\text{startNou} < \text{p.oraSfarsit} \land \text{endNou} > \text{p.oraInceput}$). Dacă slotul este liber și situat în viitor, este salvat, iar cursorul avansează cu $\text{durataMinute} + 10\text{ minute}$, garantând o pauză operațională terapeutului între pacienți.

### Partiționarea Temporală la Miezul Nopții (ReminderScheduler)
Pentru expedierea notificărilor automate de reminder cu 24 de ore și respectiv 2 ore înainte de programare, `ReminderScheduler` rulează joburi asincrone periodice (`@Scheduled`). Detectarea programărilor vizate într-o fereastră mobilă cu marjă ($\pm 15$ sau $\pm 8$ minute) ridică o provocare matematică atunci când intervalul calculat traversează miezul nopții:

```mermaid
---
config:
  theme: base
  flowchart:
    curve: basis
    nodeSpacing: 45
    rankSpacing: 55
---
flowchart LR
    A["Start: gasesteInFereastra(oreInainte, marjaMinute)"] --> B["Calculează fereastra:<br/>centruFereastra = acum + oreInainte<br/>[centru - marja, centru + marja]"]
    B --> C{"Fereastra este în<br/>aceeași zi calendaristică?"}

    C -- "Da" --> D["Execută interogare SQL unică:<br/>start.time → end.time în data start.date"]
    D --> G["Returnează lista programărilor"]

    C -- "Nu (trece miezul nopții)" --> E["Subinterogare 1 (Ziua 1):<br/>start.time → 23:59:59.999"]
    E --> F["Subinterogare 2 (Ziua 2):<br/>00:00:00 → end.time"]
    F --> H["Fuzionare liste rezultate (merge)"]
    H --> G

    classDef start fill:#eff6ff,stroke:#3b82f6,stroke-width:2px,color:#1e3a8a;
    classDef calc fill:#f0fdf4,stroke:#22c55e,stroke-width:2px,color:#14532d;
    classDef decision fill:#faf5ff,stroke:#a855f7,stroke-width:2px,color:#4a1d96;
    classDef query fill:#fff7ed,stroke:#f97316,stroke-width:2px,color:#7c2d12;
    classDef merge fill:#ecfeff,stroke:#06b6d4,stroke-width:2px,color:#164e63;
    classDef result fill:#fdf4ff,stroke:#ec4899,stroke-width:2px,color:#831843;

    class A start;
    class B calc;
    class C decision;
    class D,E,F query;
    class H merge;
    class G result;
```

Pentru a preveni interogările SQL invalide pe coloane separate de tip `DATE` și `TIME`, algoritmul `gasesteInFereastra` aplică o partiționare binară:
- Dacă `startFereastra` și `endFereastra` se află în aceeași zi calendaristică, se execută o interogare singulară `findProgramariInFereastra(data, start, end)`.
- Dacă intervalul trece peste pragul orei 00:00 (de exemplu `23:50` – `00:10`), metoda descompune căutarea în două sub-interogări complementare: prima pentru ziua curentă în intervalul `[start, 23:59:59.999]`, iar a doua pentru ziua următoare în intervalul `[00:00:00, end]`, fuzionând listele rezultate înainte de publicarea evenimentelor.

### Comunicare Asincronă & Topologia RabbitMQ cu Dead Letter Queue (DLQ)
Evenimentele asincrone generate de activitatea clinică sunt decuplate prin intermediul brokerului **RabbitMQ**, garantând persistența mesajelor și eliberarea rapidă a fluxurilor HTTP de execuție:

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
    style Producatori fill:#ffffea,stroke:#c4c400,stroke-width:1px
    style Broker fill:#ffffea,stroke:#7b8fff,stroke-width:1px
    style Consumatori fill:#ffffea,stroke:#c4c400,stroke-width:1px

    class PS,PACS,CS prod;
    class EX,DLX exchange;
    class MQ queue;
    class DLQ dead;
    class NC consumer;
    class DC dead;
```

Topologia de mesagerie include elemente avansate de robustețe:
- **Topic Exchange & Wildcard Binding:** Producătorii emit pe `notificari.exchange`, iar coada durabilă `notificari.queue.v2` este legată prin șablonul flexibil `notificare.#`, permițând adăugarea de noi tipuri de notificări fără rescrierea infrastructurii.
- **Consumer Idempotent (Anti-Duplicate):** `NotificareConsumer` utilizează un identificator unic per mesaj (`UUID`), validat atomic în tabela MySQL `mesaje_procesate` prin sintaxa `INSERT INTO mesaje_procesate ... ON DUPLICATE KEY UPDATE message_id=message_id`. Dacă mesajul a mai fost livrat (redelivery la reconectare), acesta este absorbit silențios fără a genera notificări duplicate în interfață.
- **Dead Letter Exchange (DLX) & Carantină (DLQ):** În cazul unei erori fatale la deserializare sau procesare, containerul Spring AMQP emite un `NACK` cu parametrul `requeue=false`. Brokerul rutează automat mesajul otrăvit către `notificari.dlx` și în coada de carantină `notificari.queue.dead`, unde `DeadLetterConsumer` îl loghează pentru auditare fără a re-arunca excepții (eliminând riscul buclelor infinite).

### Gestionarea Scrierii Duale (Dual-Write) prin Verificare Sincronă cu Commit Amânat
Dezactivarea unui cont de către administrator (`UserService.toggleUserActive`) rezolvă problema scrierii duale (*dual-write problem* între baza locală MySQL și furnizorul de identitate Keycloak) printr-un mecanism sincron de **tranzacție locală cu commit amânat** (*deferred commit*) combinat cu propagare *best-effort*:

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

Fluxul este structurat în două faze:
1. **Faza 1 (Consistență puternică prin commit amânat):** Entitatea utilizatorului este modificată în cadrul tranzacției locale `@Transactional` din MySQL (fără commit pe disc). Înainte de a comite tranzacția SQL, se apelează sincron `KeycloakSyncService.setUserEnabled`. Dacă serverul Keycloak este indisponibil sau returnează o eroare, metoda aruncă `ExternalServiceException`, declanșând **rollback-ul automat** al tranzacției locale din MySQL. Utilizatorul rămâne activ în ambele sisteme, prevenind conturile „fantomă”. Commit-ul SQL se execută doar după primirea confirmării pozitive din Keycloak.
2. **Faza 2 (Propagare în cascadă / Best-Effort):** Odată garantată starea principală, dezactivarea este propagată prin apeluri Feign către serviciul de profil (`pacienti-service` sau `terapeuti-service`), iar programările viitoare sunt anulate automat în `programari-service` (`cancelByPacient` sau `cancelByTerapeut`). Aceste apeluri secundare sunt izolate în blocuri `try-catch`: o eventuală indisponibilitate a serviciului de programări este doar logată, fără a compromite starea de securitate deja garantată în Keycloak și baza de date primară.

> [!NOTE]
> **Diferențiere arhitecturală față de Tiparul Saga (Înregistrare vs. Dezactivare):**
> Spre deosebire de fluxul de dezactivare (care utilizează tranzacția locală cu commit amânat), fluxul de **înregistrare a utilizatorilor** (`KeycloakService.registerUser`) implementează tiparul **Saga cu tranzacție compensatorie**: utilizatorul este creat mai întâi în Keycloak (`createUserInKeycloak`), iar dacă salvarea ulterioară în baza de date locală sau inițializarea profilului eșuează, blocul `catch` declanșează un apel compensatoriu explicit de ștergere (`deleteUserInKeycloak`) pentru a anula efectul acțiunii distribuite și a readuce sistemul la starea inițială consistentă.

### Reziliență și Gestionarea Erorilor (Resilience)
Platforma integrează multiple mecanisme defensive pentru a garanta izolarea defecțiunilor:
- **Standardizare Erori (RFC 9457):** Backend-ul utilizează specificația `ProblemDetail` pentru structurarea predictibilă a erorilor de domeniu și maparea directă pe interfața utilizator.
- **Fail-Fast & Custom Error Decoding:** Apelurile Feign inter-servicii sunt interceptate prin `CustomErrorDecoder`, transformând codurile de stare HTTP (404, 403, 400, 500) în excepții specifice de domeniu (`ResourceNotFoundException`, `ForbiddenOperationException`, `ExternalServiceException`).
- **Frontend Error Boundaries:** Aplicația React folosește granițe de eroare și interceptoare globale Axios pentru a asigura degradarea grațioasă a componentelor UI, prevenind blocarea întregii aplicații (White Screen of Death) dacă un singur microserviciu este temporar indisponibil.

---

## 🏗 Infrastructură & Stack Tehnologic

### Stack Tehnologic
- **Backend:** Java 21, Spring Boot 3.x, Spring Cloud Gateway (WebFlux / Netty), OpenFeign, Spring Security (OAuth2 Resource Server / JWT), Spring AMQP (RabbitMQ), Spring Data JPA / Hibernate, MapStruct, Lombok
- **Frontend:** React 19, Vite, React Router DOM v7, Axios, SockJS & StompJS, Recharts, FullCalendar
- **Infrastructură & Date:** MySQL 8.x (7 scheme izolate), RabbitMQ 3.x (cu plugin de management), Keycloak 24+ (IAM / OIDC), Docker & Docker Compose, Kubernetes

### Infrastructură, Containerizare & DevOps
- **Containerizare Multi-Stage (Docker):** Serviciile backend sunt împachetate pornind de la imagini alpine JRE ultra-ușoare. Frontend-ul React este compilat într-un mediu tranzitoriu Node.js și livrat în producție printr-un container optimizat **Nginx**.
- **Orchestrare Locală:** Configurația `docker-compose.yml` orchestrează întregul ecosistem pe o rețea internă dedicată (`kineto-network`), gestionând secvențierea pornirii dependențelor prin mecanisme de `healthcheck`.
- **Orchestrare Kubernetes (k8s):** Nucleul platformei este pregătit pentru orchestrare în cluster prin manifesturi declarative:
  - Izolarea resurselor în `Namespace`-ul `kinetocare`.
  - Separarea configurațiilor și credențialelor prin `ConfigMap` și `Secrets`.
  - Persistența volumelor de date prin `PersistentVolumeClaim` (PVC).
  - Punct unic de intrare extern configurat prin **Ingress Controller**, rutând traficul către Keycloak (`/realms`), API Gateway (`/api`) și Frontend (`/`).
  - Verificarea stării de funcționare prin `readinessProbe` și `livenessProbe` pe endpoint-urile Spring Boot Actuator.