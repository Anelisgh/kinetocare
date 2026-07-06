# Diagrame pentru Secțiunea 4.1

Aceste două diagrame sunt generate pe baza specificațiilor din text și a analizei codului sursă (`@FeignClient` și logica AMQP). Prima diagramă prezintă topologia pe cele 4 niveluri (Client -> Edge -> Domain -> Data), iar a doua arată proprietățile grafului apelurilor inter-servicii (sincrone via Feign și asincrone via RabbitMQ).

## Figura 4.1. Arhitectura pe patru niveluri a platformei KinetoCare
```mermaid
flowchart TD
    subgraph ClientLayer ["Nivelul Client (Client Layer)"]
        React["Aplicație React SPA<br/>(Singurul canal de interacțiune vizuală)"]
    end

    subgraph EdgeLayer ["Nivelul de Margine (Edge Layer)"]
        APIGW["API Gateway<br/>(Rutare și Agregare)"]
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
    React -->|Autentificare OIDC/OAuth2| Keycloak
    
    APIGW -->|Rutare trafic autorizat| US
    APIGW -->|Rutare trafic autorizat| PS
    APIGW -->|Rutare trafic autorizat| TS
    APIGW -->|Rutare trafic autorizat| ProgS
    APIGW -->|Rutare trafic autorizat| SS
    APIGW -->|Rutare trafic autorizat| CS
    APIGW -->|Rutare trafic autorizat| NS

    US -.->|Persistență dedicată| DB_US
    PS -.->|Persistență dedicată| DB_PS
    TS -.->|Persistență dedicată| DB_TS
    ProgS -.->|Persistență dedicată| DB_ProgS
    SS -.->|Persistență dedicată| DB_SS
    CS -.->|Persistență dedicată| DB_CS
    NS -.->|Persistență dedicată| DB_NS

    classDef default fill:#f9f9f9,stroke:#333,stroke-width:1px;
    classDef db fill:#f0f8ff,stroke:#0056b3,stroke-width:2px;
    class DB_US,DB_PS,DB_TS,DB_ProgS,DB_SS,DB_CS,DB_NS db;
```

---

## Figura 4.2. Graful orientat al apelurilor inter-servicii (Sincron vs. Asincron)
```mermaid
flowchart LR
    %% Definire stiluri
    classDef leafNode fill:#d4edda,stroke:#28a745,stroke-width:2px;
    classDef hubNode fill:#cce5ff,stroke:#007bff,stroke-width:2px;
    classDef reactiveNode fill:#fff3cd,stroke:#ffc107,stroke-width:2px;
    classDef broker fill:#f8d7da,stroke:#dc3545,stroke-width:2px,stroke-dasharray: 5 5;

    %% Noduri principale
    US["user-service<br/>Nod de orchestrare"]:::hubNode
    ProgS["programari-service<br/>Nucleu tranzacțional clinic"]:::hubNode
    PS["pacienti-service<br/>Nod logic clinic"]:::hubNode
    CS["chat-service<br/>Nod operațional"]:::hubNode

    TS["terapeuti-service<br/>Nod-frunză"]:::leafNode
    SS["servicii-service<br/>Nod-frunză"]:::leafNode
    
    NS["notificari-service<br/>Unitate pur reactivă"]:::reactiveNode

    RabbitMQ["Broker AMQP<br/>RabbitMQ"]:::broker

    %% Apeluri sincrone (OpenFeign)
    US -->|Dezactivare| TS
    US -->|Anulare prog| ProgS
    US -->|Inactivare profil| PS

    ProgS -->|Preț/Tarif| SS
    ProgS -->|Concedii| TS
    ProgS -->|Date utilizator| US
    ProgS -->|Verificare pacient| PS

    PS -->|Istoric evaluări| ProgS

    CS -->|Relație activă| ProgS

    %% Apeluri asincrone (Event-Driven)
    ProgS -.->|Eveniment programare| RabbitMQ
    US -.->|Eveniment cont nou| RabbitMQ
    RabbitMQ -.->|Notificare| NS

    %% Legendă
    subgraph Legenda ["Legendă: Tipologia Nodurilor"]
        LN["Nod-Frunză: Leaf Node - Nu apelează nimic"]:::leafNode
        HN["Nod Hub / Orchestrator - Apelează alte servicii"]:::hubNode
        RN["Nod Reactiv - Răspunde doar la evenimente"]:::reactiveNode
    end
```
