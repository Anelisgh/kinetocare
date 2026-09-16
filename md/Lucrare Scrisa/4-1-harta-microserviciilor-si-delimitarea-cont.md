# Capitolul 4. Arhitectura Sistemului

Acest capitol descrie în detaliu arhitectura distribuită a platformei KinetoCare, evidențiind topologia microserviciilor și delimitarea riguroasă a contextelor logice de *business*. Sunt analizate fluxurile de comunicare sincronă și asincronă, structura și rolul de agregare reactivă al API *Gateway*-ului, modelul de securitate descentralizat de tip *Zero-Trust* și, în final, strategia de izolare a persistenței prin tiparul *Database-per-Service* și mecanismele compensatorii pentru garantarea integrității datelor.

## 4.1 Harta microserviciilor și delimitarea contextelor (*Bounded Contexts*)

Această secțiune expune organizarea structurală a platformei KinetoCare, detaliind delimitarea strictă a contextelor logice de *business* (*Bounded Contexts*) și maparea acestora sub forma unor componente autonome de execuție. Este definit pilonul central al integrării distribuite — cheia universală de pivotare — și este prezentată topologia generală a rețelei interne de microservicii.

### 4.1.1 Structura de ansamblu a sistemului

Sistemul KinetoCare este organizat în opt unități de execuție complet independente (*deployable units*), stratificate pe patru niveluri arhitecturale cu responsabilități strict delimitate:

- **Nivelul client (*Client Layer*):** Aplicația de interfață utilizator (*React SPA*) reprezintă canalul exclusiv de interacțiune vizuală pentru toate categoriile de actori (pacienți, terapeuți, administratori).
- **Nivelul de margine (*Edge Layer*):** API *Gateway*-ul și serverul de identitate Keycloak formează perimetrul de securitate al platformei față de exterior. *Gateway*-ul guvernează rutarea și agregarea datelor, în timp ce Keycloak gestionează exclusiv ciclurile de autentificare.
- **Nivelul serviciilor de domeniu (*Domain Services Layer*):** Șapte microservicii *backend*, fiecare guvernând un singur domeniu delimitat logic (*Bounded Context*).
- **Nivelul de date (*Data Layer*):** Șapte scheme de baze de date relaționale complet izolate, fiecare aflată în posesia exclusivă a unui singur microserviciu, garantând decuplarea la nivel de persistență.

O regulă arhitecturală fundamentală a sistemului stipulează că niciun microserviciu nu accesează direct schema de date a altui serviciu; orice necesitate de date inter-domenii este rezolvată exclusiv prin intermediul contractelor API formalizate.

### 4.1.2 Delimitarea contextelor de domeniu (*Bounded Contexts*)

Delimitarea contextelor reflectă granițele naturale ale proceselor de *business* dintr-o clinică de recuperare medicală, aplicând principiile *Domain-Driven Design* (*DDD*). Fiecare context deține un model propriu de date, un limbaj ubicuu (*Ubiquitous Language*) și un mecanism independent de persistență.

**Contextul de identitate și autentificare (`user-service`)**

Acest context încapsulează modelul canonic al utilizatorului din perspectiva securității. Entitatea principală stochează atributele de bază (e-mail, nume, date de contact, roluri) și funcționează ca un strat de abstractizare peste furnizorul extern de identitate (*Identity Provider*) Keycloak. Operațiunile de creare, suspendare sau recuperare a conturilor sunt delegate către Keycloak, în timp ce baza de date locală acționează ca o oglindă optimizată pentru a deservi interogările rapide de la nivelul rețelei interne. O responsabilitate critică a acestui context este orchestrarea cascadei de dezactivare: la suspendarea unui cont, starea este propagată prin apeluri sincrone către profilurile medicale și sistemul de programări, pentru a invalida operațiunile viitoare.

**Contextul profilului clinic (`pacienti-service`)**

Acest domeniu extinde identitatea de bază cu atributele specifice istoricului medical (date demografice sensibile, detalii despre activitatea sportivă și preferințe clinice). O responsabilitate distinctă în cadrul acestui context o reprezintă gestionarea jurnalului subiectiv de progres — un mecanism prin care feedback-ul pacientului este capturat după fiecare intervenție terapeutică. Izolarea acestor informații de contextul de identitate garantează un nivel superior de confidențialitate a datelor clinice.

**Contextul resurselor umane și disponibilității (`terapeuti-service`)**

Acest microserviciu modelează subdomenii puternic coezive: profilul profesional, infrastructura fizică (catalogul de locații ale clinicii) și managementul timpului (ferestre de disponibilitate și perioade de concediu). Din perspectivă topologică, funcționează ca un nod-frunză (*leaf node*) în graful dependențelor: nu inițiază apeluri de rețea către alte componente, ci este exclusiv interogat pentru validarea conflictelor de orar la inițierea rezervărilor. Activele media (fotografiile de profil) sunt stocate direct în schema bazei de date (codificate *Base64*), simplificând arhitectura prin evitarea introducerii unui serviciu terț de tip *Object Storage* pentru un volum marginal de date.

**Contextul tranzacțional clinic (`programari-service`)**

Nucleul orchestrator al sistemului reunește patru entități cu cerințe stricte de consistență locală: Programare, Relație Terapeutică, Evaluare și Evoluție. Co-localizarea lor este deliberată: o evaluare medicală este dependentă logic și tranzacțional de programarea care a declanșat-o; separarea în microservicii distincte ar fi impus protocoale complexe de coordonare distribuită. Tot în cadrul acestui context se aplică tiparul *Snapshot* (justificat detaliat în secțiunea 4.5.4) prin denormalizarea prețului și a duratei serviciilor medicale la momentul creării programării, garantând imuabilitatea istorică și corectitudinea auditului financiar.

**Contextul catalogului de servicii (`servicii-service`)**

Gestionează taxonomia intervențiilor medicale și politica tarifară. Este implementată o logică de căutare tolerantă la variații (căutare ierarhică cu proceduri de rezervă/*fallback* pe categorii), permițând componentei de programări să deducă automat serviciul corect fără a crea un cuplaj rigid la nivelul identificatorilor din baza de date.

**Contextul mesageriei în timp real (`chat-service`)**

Gestionează canalul de comunicație sincronă combinând o interfață *REST* pentru persistența arhivelor cu un flux *WebSocket*/*STOMP* pentru comunicarea instantanee. O caracteristică arhitecturală de siguranță este verificarea activă a validității relației clinice: transmiterea mesajelor este blocată dacă nu este confirmat, printr-un apel sincron, că pacientul se află încă sub îngrijirea terapeutului destinatar.

**Contextul reactiv de notificări (`notificari-service`)**

Acționează ca un colector de evenimente asincrone, cu un rol pur reactiv. Consumă mesaje de pe magistrala *AMQP*, le consolidează și expune un panou de notificări acționabile, decuplând complet procesul de alertare de logica tranzacțională a celorlalte module.

### 4.1.3 Cheia universală de pivotare: identificatorul extern

Deoarece modelul *Database-per-Service* previne utilizarea interogărilor SQL care unesc date din scheme diferite, platforma adoptă o convenție globală de referențiere bazată pe un identificator extern (`keycloakId`). Acest UUID, generat și garantat ca imutabil de către furnizorul extern de identitate, este utilizat ca o cheie externă logică (fără constrângeri fizice) în toate bazele de date. Utilizarea unui pivot universal elimină dependența arhitecturală limitativă a identificatorilor auto-incrementali locali (*auto-increment*) — care nu posedă semnificație în afara propriului microserviciu — și asigură o trasabilitate coerentă a datelor la nivelul întregului sistem.

### 4.1.4 Topologia arhitecturală a sistemului

Reprezentarea vizuală de mai jos ilustrează distribuția pe niveluri a componentelor, fluxurile de date și separarea strictă a contextelor de persistență. Se remarcă decuplarea asincronă a modulului de notificări prin intermediul brokerului de mesaje RabbitMQ.

```mermaid
graph TD
    %% Nivelul Client
    subgraph Client Layer
        SPA[Aplicație React SPA]
    end

    %% Nivelul de Margine (Edge/Security)
    subgraph Edge Layer
        GW[API Gateway / BFF]
        IAM[Keycloak IAM]
    end

    %% Nivelul Serviciilor de Domeniu
    subgraph Domain Services Layer
        US[user-service]
        PS[pacienti-service]
        TS[terapeuti-service]
        PR[programari-service]
        SS[servicii-service]
        CS[chat-service]
        NS[notificari-service]
    end

    %% Nivelul de Baze de Date
    subgraph Data Layer
        UDB[(user_db)]
        PDB[(pacienti_db)]
        TDB[(terapeuti_db)]
        PRDB[(programari_db)]
        SDB[(servicii_db)]
        CDB[(chat_db)]
        NDB[(notificari_db)]
    end

    %% Relații și Fluxuri
    SPA <-->|HTTPS / Cookies HttpOnly| GW
    GW <-->|OAuth2 / OIDC| IAM

    %% Relație administrativă directă
    US <-->|Keycloak Admin REST API| IAM

    %% Gateway catre Servicii (BFF routing)
    GW --> US & PS & TS & PR & SS & CS & NS

    %% Servicii catre DB
    US --> UDB
    PS --> PDB
    TS --> TDB
    PR --> PRDB
    SS --> SDB
    CS --> CDB
    NS --> NDB

    %% Fluxuri Asincrone AMQP (RabbitMQ)
    PR -.->|Eveniment AMQP| RMQ[[RabbitMQ Broker]]
    CS -.->|Eveniment AMQP| RMQ
    PS -.->|Eveniment AMQP| RMQ
    NS -.->|Consumă| RMQ

    %% Stilizare
    classDef layerStyle fill:#f9f9f9,stroke:#333,stroke-width:1px,stroke-dasharray: 5 5;
    classDef dbStyle fill:#e1f5fe,stroke:#0288d1,stroke-width:2px;
    classDef serviceStyle fill:#e8f5e9,stroke:#388e3c,stroke-width:2px;

    class UDB,PDB,TDB,PRDB,SDB,CDB,NDB dbStyle;
    class US,PS,TS,PR,SS,CS,NS serviceStyle;
```
