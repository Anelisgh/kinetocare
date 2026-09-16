## 6.6 Implementarea Tranzacțiilor de Compensare (*Dual-write* Keycloak + DB)

Această secțiune analizează provocările consistenței distribuite în scenariile de scriere duală (*dual-write*), combinând serverul de identitate Keycloak cu bazele de date relaționale locale. Este detaliat mecanismul tranzacțional compensatoriu implementat pentru a asigura atomicitatea înregistrării utilizatorilor și tranzacțiile de compensare sincrone utilizate în dezactivarea securizată a conturilor.

### 6.6.1 Problema consistenței distribuite în scenariile de identitate

În arhitecturile bazate pe microservicii, managementul identității utilizatorilor implică frecvent o structură hibridă. Pe de o parte, este necesar un sistem robust de tip **IAM** (*Identity and Access Management*), precum serverul Keycloak, dedicat securizării fluxurilor de autentificare, stocării securizate a credențialelor și managementului jetoanelor *JWT*. Pe de altă parte, datele operaționale (detaliile de contact, istoricul medical și referințele specifice) trebuie stocate în baza de date locală a aplicației, sub coordonarea unui microserviciu dedicat, precum `user-service`.

Această asimetrie introduce problema critică a **scrierii duale** (*dual-write*). Deoarece Keycloak și baza de date relațională locală a aplicației (`user_db`) reprezintă sisteme de stocare distincte, nicio tranzacție clasică ACID nu poate acoperi ambele sisteme în mod nativ.

Dacă o operație complexă ce implică ambele sisteme eșuează parțial, starea datelor devine inconsistentă:

- **Scenariul contului fantomă:** Utilizatorul este creat cu succes în Keycloak, dar salvarea în `user_db` eșuează din cauza unei constrângeri de bază de date (de exemplu, un e-mail duplicat detectat tardiv). Pacientul va deține un cont valid în Keycloak și se va putea autentifica, obținând un jeton *JWT*, dar la accesarea platformei vor fi returnate erori HTTP de tip 404 sau 500, deoarece profilul local nu există.
- **Scenariul contului orfan:** Salvarea locală în baza de date se efectuează, dar actualizarea în Keycloak eșuează. Contul va figura ca activ în tabelele clinicii, însă utilizatorul nu va putea accesa niciodată platforma.
- **Riscuri GDPR și de securitate:** Conturile fantomă abandonate în Keycloak reprezintă breșe de conformitate și pot fi exploatate pentru a genera acces neautorizat, ocolind auditul clinic intern.

Pentru a rezolva această limitare în absența tranzacțiilor distribuite rigide (care implică blocarea resurselor și degradarea performanței), platforma KinetoCare utilizează un model de **tranzacții compensatorii sincrone** (*compensating transactions*), o versiune simplificată și adaptată a principiului de *rollback* din tiparul *Saga*.

### 6.6.2 Înregistrarea utilizatorului: Strategia de *rollback* compensatoriu sincron

Metoda de înregistrare a unui utilizator nou, localizată în `KeycloakService.registerUser()`, utilizează o strategie de compensare sincronă. Deși metoda este adnotată cu `@Transactional`, Spring poate gestiona în mod nativ exclusiv *rollback*-ul bazei de date relaționale locale (MySQL). Din această cauză, pașii exteriori sunt înveliți într-o logică defensivă de tip `try/catch`, capabilă să orchestreze compensarea explicit.

Secvența operațională se desfășoară în patru faze distincte:

**Faza 1: Crearea contului în Keycloak.** API-ul Keycloak este apelat pentru crearea utilizatorului:
```java
createUserInKeycloak()
```
Dacă acest pas eșuează (de exemplu, din cauza complexității insuficiente a parolei sau a problemelor de rețea), procesul se oprește instantaneu. Nu există nicio modificare persistentă în restul sistemului, nefiind necesară nicio compensare. La succes, Keycloak returnează un cod HTTP `201 Created`, de unde este extras identificatorul unic `keycloakId` (UUID).

**Faza 2: Alocarea rolului de securitate.** Rolul specific (Pacient sau Terapeut) este asociat contului nou creat în Keycloak. În caz de eșec la acest nivel, contul ar rămâne fără drepturi de acces. Blocul de excepție interceptează eroarea și rulează imediat tranzacția de compensare:
```java
deleteUserInKeycloak(keycloakId)
```
Această acțiune șterge sincron contul creat la Faza 1, restabilind starea curată a sistemului.

**Faza 3: Persistența locală a utilizatorului.** Utilizatorul este salvat local în tabela `user_db` prin:
```java
userRepository.save(userEntity)
```
Dacă salvarea eșuează (de exemplu, dintr-o eroare internă de persistență), blocul `catch` rulează din nou compensarea — ștergerea sincronă a utilizatorului din Keycloak, eliminând contul fantomă.

**Faza 4: Inițializarea profilului clinic *downstream*.** Este inițializat un profil clinic gol în microserviciul corespunzător rolului (`pacienti-service` sau `terapeuti-service`). Deoarece utilizatorul nu este încă autentificat la acest moment (nu deține un jeton *JWT* valid în contextul de securitate), apelul inter-servicii se realizează direct prin `RestTemplate`:
```java
restTemplate.postForEntity(url, null, Void.class)
```
Dacă acest apel *downstream* eșuează (de exemplu, din cauza indisponibilității temporare a serviciului), este declanșată o cascadă dublă de compensare:
- Blocul de excepție apelează ștergerea de siguranță a utilizatorului din Keycloak (`deleteUserInKeycloak`).
- Excepția propagată declanșează *rollback*-ul automat al tranzacției SQL din Spring, anulând salvarea locală efectuată la Faza 3.
- Sistemul revine în totalitate la starea inițială, garantând consistența tranzacțională.

### 6.6.3 Diagrama secvențială a înregistrării cu compensare

Fluxul de înregistrare și compensare în caz de eroare este ilustrat în diagrama de mai jos:

```mermaid
sequenceDiagram
    autonumber
    participant C as Client (Formular Inregistrare)
    participant US as user-service (KeycloakService)
    participant KC as Keycloak IAM
    participant DB as user_db (MySQL)
    participant PS as pacienti-service / terapeuti-service

    C->>US: POST /api/users/auth/register {email, password, role}
    
    rect rgb(240, 255, 240)
        Note over US,KC: Faza 1-2: Scriere in Keycloak
        US->>KC: createUser(UserRepresentation)
        KC-->>US: 201 Created (returneaza keycloakId)
        US->>KC: assignRealmRole(keycloakId, roleName)
        KC-->>US: 204 No Content
    end

    rect rgb(240, 248, 255)
        Note over US,DB: Faza 3: Salvare DB locala (Tranzactie activa)
        US->>DB: userRepository.save(userEntity)
        DB-->>US: returneaza entitatea salvata
    end

    rect rgb(255, 250, 240)
        Note over US,PS: Faza 4: Initializare profil downstream
        US->>PS: POST /pacient/initialize/{keycloakId} (RestTemplate)
        PS-->>US: 500 Internal Server Error (ESEC APEL!)
    end

    rect rgb(255, 240, 240)
        Note over US,KC: Compensare automata in caz de esec
        US->>KC: deleteUser(keycloakId) (Sterge contul creat anterior)
        KC-->>US: 204 No Content
        Note over US,DB: Spring executa Rollback SQL automat pentru Faza 3
    end
    
    US-->>C: returneaza 500 Registration Failed (Sistemul a ramas curat)
```

### 6.6.4 Dezactivarea contului: Mecanism de compensare sincronă cu consistență asimetrică

Metoda `UserService.toggleUserActive(keycloakId, active)` adresează o problemă tranzacțională distinctă: dezactivarea unui cont existent. Această acțiune trebuie propagată în patru subsisteme, într-o succesiune strictă:

1. **DB Local (`user_db`):** Proprietatea `active` a utilizatorului este setată pe `false`.
2. **Keycloak IAM:** Contul este dezactivat, blocând imediat emiterea de noi jetoane de acces și orice operațiune de reîmprospătare a sesiunii, limitând accesul rezidual la intervalul de valabilitate al jetonului activ existent.
3. **Serviciul de Profil (`pacienti-service` / `terapeuti-service`):** Profilul este marcat ca inactiv pentru a fi exclus din căutări.
4. **Serviciul de Programări (`programari-service`):** Toate programările viitoare asociate utilizatorului sunt anulate automat, iar mesajele de notificare sunt trimise partenerilor afectați.

**Decizia arhitecturală: Asimetria de consistență.** În loc să impună o consistență puternică (atomică) pe toate cele patru sisteme, platforma KinetoCare utilizează un **mecanism de compensare sincronă directă (*try-compensate*), cu consistență asimetrică**. Această decizie este motivată de prioritățile de securitate și funcționalitate ale clinicii, structurate în tabelul de mai jos:

| Sistem afectat | Model consistență | Justificare clinică și de securitate | Comportament la eșec |
|:---|:---|:---|:---|
| **DB Local + Keycloak** (Pașii 1 și 2) | **Consistență puternică (atomic)** | Este inacceptabil ca un cont să fie dezactivat în baza de date, dar utilizatorul să poată continua să acceseze API-urile folosind jetoane *JWT* active. | Dacă dezactivarea în Keycloak eșuează, tranzacția locală MySQL este anulată (*rollback*), menținând securitatea. |
| **Serviciul de Profil** (Pasul 3) | **Consistență eventuală (*best-effort*)** | Este acceptabil ca un utilizator dezactivat să mai apară timp de câteva minute în listele de terapeuți sau pacienți. | Eșecul este jurnalizat, dar nu anulează dezactivarea generală. Un proces de reîncercare automată poate sincroniza datele ulterior. |
| **Serviciul de Programări** (Pasul 4) | **Consistență eventuală (*best-effort*)** | Anularea programărilor viitoare și trimiterea de notificări pot tolera întârzieri temporare fără a pune în pericol securitatea clinicii. | Eșecul este înregistrat. Un administrator poate declanșa manual curățarea agendei dacă serviciul de programări a fost temporar offline. |

Această asimetrie separă granița critică de securitate (care necesită atomicitate deplină) de granița de date operaționale (care poate tolera consistența eventuală), asigurând o disponibilitate sporită a sistemului.

### 6.6.5 Evaluarea alternativelor arhitecturale

În faza de proiectare a platformei, au fost analizate trei arhitecturi alternative pentru rezolvarea acestei probleme:

- **Protocolul *Two-Phase Commit* (2PC) distribuit:** Este necesar ca toate sistemele implicate — inclusiv Keycloak, un sistem extern — să blocheze resursele locale în faza de pregătire. Acest lucru degradează performanța, introduce un singur punct de eșec critic (coordonatorul de tranzacții) și este dificil de implementat peste API-uri REST terțe.

- **Modelul *Transactional Outbox*:** Modificările în Keycloak nu s-ar realiza direct. Componenta `user-service` ar scrie modificarea într-o tabelă locală `outbox` în cadrul aceleiași tranzacții MySQL. Un *worker* separat ar citi tabela și ar sincroniza asincron Keycloak. Deși elimină cuplarea strânsă, introduce o complexitate operațională semnificativă (managementul *worker*-ului, tratarea duplicatelor), nejustificată pentru volumul redus de înregistrări zilnice dintr-o clinică.

- **Saga coreografică (bazată pe evenimente):** Componentele *downstream* ar asculta evenimentele de dezactivare publicate de `user-service` în RabbitMQ și ar reacționa autonom. A fost preferat un **mecanism de compensare sincronă directă** (coordonat direct din `UserService` ca un orchestrator simplificat) deoarece oferă trasabilitate superioară în cod: logica de *business* este centralizată într-un singur loc, permițând identificarea rapidă a erorilor de sincronizare.

### 6.6.6 Idempotența și reziliența tranzacțiilor compensatorii

Un aspect ingineresc important este asigurarea **idempotenței** tuturor metodelor implicate în compensare. De exemplu, metoda `KeycloakSyncService.setUserEnabled(keycloakId, false)` poate fi apelată de mai multe ori cu același parametru fără a produce efecte secundare adverse sau erori în Keycloak.

Dacă o tentativă de dezactivare eșuează la etapa de actualizare a profilului și operațiunea este reîncercată de un administrator, sistemul reexecută pașii anteriori fără a altera starea globală, asigurând convergența către starea finală definită. Acest design consolidează reziliența arhitecturală în fața fluctuațiilor de rețea specifice sistemelor distribuite.
