## 5.2 Modelul conceptual al datelor — harta schemelor

În conformitate cu principiile arhitecturii bazate pe microservicii și a tiparului *Database-per-Service* (a cărui justificare arhitecturală este detaliată în secțiunea 4.5.1), datele platformei **KinetoCare** sunt distribuite în șapte scheme logice izolate, prezentate din punct de vedere al mapării logice în această secțiune.

### 5.2.1 Harta globală a schemelor (Diagramă Mermaid)

Harta conceptuală prezentată mai jos ilustrează cele șapte scheme de baze de date, tabelele principale găzduite de acestea și modul în care datele se corelează la nivel logic prin intermediul identificatorilor unici, depășind granițele fizice ale sistemelor de stocare:

```mermaid
classDiagram
    %% Scheme de Baze de Date ca grupuri logice
    class user_db {
        <<Database>>
        users
    }
    class terapeuti_db {
        <<Database>>
        terapeuti
        locatii
        disponibilitate_terapeut
        concediu_terapeut
    }
    class pacienti_db {
        <<Database>>
        pacienti
        jurnal_pacient
    }
    class programari_db {
        <<Database>>
        programari
        evaluari
        evolutii
        relatie_pacient_terapeut
    }
    class servicii_db {
        <<Database>>
        servicii
        tip_serviciu
    }
    class chat_db {
        <<Database>>
        conversatii
        mesaje
    }
    class notificari_db {
        <<Database>>
        notificari
    }

    %% Conexiuni Logice (Cross-Database prin Keycloak UUID sau ID Logic)
    users ..> terapeuti : "keycloak_id (1:1)"
    users ..> pacienti : "keycloak_id (1:1)"
    pacienti ..> programari : "pacient_keycloak_id (1:N)"
    terapeuti ..> programari : "terapeut_keycloak_id (1:N)"
    locatii ..> programari : "locatie_id (1:N) - logic"
    servicii ..> programari : "serviciu_id (1:N) - logic"
    pacienti ..> conversatii : "pacient_keycloak_id (1:N)"
    terapeuti ..> conversatii : "terapeut_keycloak_id (1:N)"
    users ..> notificari : "user_keycloak_id (1:N)"
    
    %% Relații Fizice Interne (FOREIGN KEY cu integritate referențială rigidă)
    terapeuti "1" --> "0..*" disponibilitate_terapeut : "terapeut_id (FK)"
    terapeuti "1" --> "0..*" concediu_terapeut : "terapeut_id (FK)"
    locatii "1" --> "0..*" disponibilitate_terapeut : "locatie_id (FK)"
    pacienti "1" --> "0..*" jurnal_pacient : "pacient_id (FK)"
    tip_serviciu "1" --> "0..*" servicii : "tip_serviciu_id (FK)"
    conversatii "1" --> "0..*" mesaje : "conversatie_id (FK)"
    programari "1" --> "0..1" evaluari : "programare_id (FK)"
```

### 5.2.2 Diferența dintre cheile externe fizice și referințele aplicative

Modelarea datelor într-un sistem distribuit impune o separare clară între mecanismele de integritate referențială utilizate, în funcție de granițele tranzacționale și logice ale microserviciilor:

1. **Cheile externe fizice (`FOREIGN KEY`):** Sunt utilizate exclusiv **în interiorul aceleiași scheme de bază de date** pentru a guverna relații puternic coezive. Ele sunt impuse la nivelul motorului de stocare MySQL InnoDB și garantează integritatea referențială rigidă (de exemplu, împiedică ștergerea unei conversații dacă există mesaje asociate acesteia sau blochează salvarea unei disponibilități pentru un ID de terapeut inexistent în tabela locală). Aceste relații beneficiază de suport tranzacțional ACID nativ și de cascade automate la nivel SQL (`ON DELETE CASCADE` / `RESTRICT`).
2. **Referințele aplicative** (*soft references* în literatura de specialitate): Sunt atribute simple (de regulă stocate sub formă de `VARCHAR(36)` sau `BIGINT`) folosite pentru a asocia entități **aflate în baze de date (scheme) separate logic** (cu posibilitatea separării fizice în producție prin instanțe de baze de date dedicate), fără ca motorul SQL să poată valida sau impune aceste asocieri. Exemple notabile includ:
   * Referirea profilului de pacient (`pacienti_db.pacienti`) sau terapeut (`terapeuti_db.terapeuti`) la contul central de utilizator (`user_db.users`) prin intermediul stringului universal `keycloak_id`.
   * Corelarea programărilor (`programari_db.programari`) cu clinica fizică (`terapeuti_db.locatii`) prin modificatorul numeric `locatie_id`.
   * Corelarea ședințelor cu prețul standard din catalog prin `serviciu_id` (`servicii_db.servicii.id`).

Pentru aceste asocieri, motorul MySQL nu poate aplica validări fizice sau constrângeri de integritate (deoarece conexiunile cross-database sunt blocate structural). Integritatea datelor la nivelul referințelor aplicative este o responsabilitate delegată în totalitate **straturilor de aplicație** (prin cod Java în microservicii ce validează datele prin apeluri HTTP sincrone) și arhitecturilor orientate pe evenimente (unde evenimentele asincrone propagă modificările și aliniază stările downstream).

*Notă:* Justificarea arhitecturală a separării datelor este detaliată în §4.5, această secțiune vizând exclusiv modelul de asamblare a datelor.
