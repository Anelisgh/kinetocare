## Secțiunea 8: Analiză Detaliată per Microserviciu (Per-Microservice Deep Dive)
Această secțiune oferă o analiză granulară a microserviciilor de bază, documentând schemele lor interne de baze de date, interfețele REST expuse, fluxurile logicii de business și dependențele inter-servicii.
### 8.1 Microserviciul: `user-service`
Serviciul `user-service` este sursa autoritară pentru identitate, operațiuni de autentificare și date de profil de bază ale utilizatorilor. Funcționează ca un strat de abstractizare peste serverul Keycloak Identity and Access Management (IAM), menținând o bază de date locală sincronizată.
### 8.1.1 Entități în Baza de Date & Enum-uri (Schema: `user_db`)
**Entitatea: `User`**
| Câmp | Tip | Constrângeri | Descriere |
|---|---|---|---|
| `id` | `Long` | PK, Auto-increment | ID-ul intern al bazei de date |
| `keycloakId` | `String` | Unic, lungime=36 | UUID-ul universal generat de Keycloak |
| `email` | `String` | Unic, lungime=100 | Credențială de autentificare / Username |
| `nume` | `String` | lungime=100 | Numele de familie |
| `prenume` | `String` | lungime=100 | Prenumele |
| `telefon` | `String` | lungime=20 | Numărul de contact |
| `gen` | `Enum (Gen)` | `MASCULIN`, `FEMININ` | Genul |
| `role` | `Enum (UserRole)` | `PACIENT`, `TERAPEUT`, `ADMIN` | Rolul în sistem |
| `active` | `Boolean` | Implicit=true | Flag de ștergere logică (soft-delete) / suspendare cont |
| `createdAt` | `OffsetDateTime` | NOT NULL | Data și ora creării contului |
Indecși: `idx_users_role`, `idx_users_keycloak_id`, `idx_users_email`.
### 8.1.2 Endpoint-uri REST
**Controller: `AuthController` (Public / Neautentificat)**
- `POST /users/auth/register`: Primește payload-ul de înregistrare, creează utilizatorul în Keycloak, adaugă înregistrarea în DB și inițializează un profil gol corespunzător rolului.
- `POST /users/auth/forgot-password`: Declanșează fluxul Keycloak de trimitere a emailului pentru actualizarea parolei (`UPDATE_PASSWORD`).
**Controller: `UserController` (Autentificat)**
- `GET /users/auth/me`: Returnează profilul utilizatorului autentificat curent pe baza claim-ului Subject din JWT.
- `GET /users/by-keycloak/{keycloakId}`: Preluarea profilului pe baza UUID-ului Keycloak (folosit intens de API Gateway).
- `GET /users/{id}`: Preluarea profilului după ID-ul intern din DB.
- `POST /users/batch`: Acceptă o listă de `keycloakId`-uri și returnează o listă de `UserDTO`-uri (rezolvă problemele de interogare N+1).
- `POST /users/batch/ids`: La fel ca mai sus, folosind ID-urile interne din DB.
- `PATCH /users/{keycloakId}`: Actualizează detaliile utilizatorului (sincronizează modificările în Keycloak).
- `PUT /users/my-password`: Actualizează parola direct în Keycloak.
- `GET /users` *(Rol: ADMIN)*: Listează toți utilizatorii cu filtre opționale (`role`, `active`).
- `PATCH /users/by-keycloak/{keycloakId}/toggle-active` *(Rol: ADMIN)*: Suspendă sau reactivează un cont.
### 8.1.3 Logica de Business Principală
**Fluxul de Înregistrare (`KeycloakService.registerUser`)**
1. Validează că rolul solicitat nu este `ADMIN`.
2. Creează utilizatorul în Keycloak prin Keycloak Admin Client (`RealmResource.users().create()`).
3. Asociază rolul de Realm corespunzător UUID-ului proaspăt generat.
4. Persistă utilizatorul în baza de date locală MySQL `user_db`.
5. **Inițializarea Profilului:** Efectuează un apel sincron HTTP (prin `RestTemplate`) către `pacienti-service` (`/pacient/initialize/{id}`) sau `terapeuti-service` (`/terapeut/initialize/{id}`) pentru a crea înregistrarea goală extinsă.
6. **Rollback:** Implementează un bloc `catch` care încearcă să șteargă utilizatorul Keycloak nou creat dacă scrierea în DB locală sau inițializarea profilului extern eșuează.
**Cascada de Dezactivare din Admin (`UserService.toggleUserActive`)**
Atunci când un administrator dezactivează un cont (`active = false`):
1. Actualizează `active = false` în DB locală.
2. Sincronizează starea dezactivată în Keycloak (`keycloakSyncService.setUserEnabled()`), închizând sesiunile active.
3. Propagă starea prin Feign (`pacientiClient.toggleActive` / `terapeutiClient.toggleActive`).
4. **Anularea Programărilor:** Folosește clienții Feign (`programariClient.cancelByTerapeut` / `cancelByPacient`) pentru a anula imediat toate programările *viitoare* asociate cu utilizatorul respectiv.
### 8.1.4 Dependențe Inter-Servicii
- **Serverul Keycloak:** Apeluri sincrone prin clientul de admin pentru administrarea identităților.
- **Clienți Feign:** `PacientiClient`, `TerapeutiClient`, `ProgramariClient` pentru actualizări în cascadă și preluarea de date în lot (batch).
- **RabbitMQ:** Nu publică direct evenimente din `user-service`.
### 8.2 Microserviciul: `pacienti-service`
Serviciul `pacienti-service` gestionează datele clinice și demografice specifice pacienților care extind identitatea de bază din `User`. Se ocupă de completarea profilului, maparea preferințelor pentru terapeuți și feedback-ul clinic subiectiv (Jurnalul Pacientului).
### 8.2.1 Entități în Baza de Date & Enum-uri (Schema: `pacienti_db`)
**Entitatea: `Pacient`**
| Câmp | Tip | Constrângeri | Descriere |
|---|---|---|---|
| `id` | `Long` | PK, Auto-increment | ID-ul intern al bazei de date |
| `keycloakId` | `String` | Unic, lungime=36 | Face legătura cu identitatea din `user-service` |
| `dataNasterii` | `LocalDate` | — | Data nașterii |
| `cnp` | `String` | Unic, lungime=13 | Codul Numeric Personal (CNP) |
| `faceSport` | `Enum(FaceSport)` | `DA`, `NU` | Indicator privind stilul de viață |
| `detaliiSport` | `String` | lungime=500 | Detalii suplimentare despre activitatea sportivă |
| `orasPreferat` | `String` | lungime=100 | Orașul preferat pentru tratament |
| `locatiePreferataId` | `Long` | — | FK către `terapeuti_db.locatii` |
| `terapeutKeycloakId` | `String` | lungime=36 | Preferința pentru terapeutul selectat |
| `active` | `Boolean` | Implicit=true | Flag de ștergere logică sincronizat din `user-service` |
| `createdAt` | `OffsetDateTime` | NOT NULL | Data și ora creării profilului |
| `updatedAt` | `OffsetDateTime` | NOT NULL | Data și ora actualizării profilului |
**Entitatea: `JurnalPacient`** *(Consultați Secțiunea 5.2 pentru schema completă `JurnalPacient`).*
### 8.2.2 Endpoint-uri REST
**Controller: `PacientController`**
- `POST /pacient/initialize/{keycloakId}`: Apelat de `user-service` în timpul înregistrării pentru a instanția o linie de profil goală.
- `POST /pacient/{keycloakId}`: Completează profilul (folosit la prima conectare).
- `PATCH /pacient/{keycloakId}`: Actualizează datele existente de profil.
- `GET /pacient/by-keycloak/{keycloakId}`: Preluarea datelor pacientului pentru pagina de pornire și paginile de profil din frontend.
- `GET /pacient/{id}` & `GET /pacient/{id}/keycloak-id`: Căutări utilitare pentru agregarea datelor inter-servicii.
- `POST /pacient/batch/keycloak-ids`: Căutare bulk pentru a evita interogările de tip N+1 în timpul randării listelor.
- `POST /pacient/{keycloakId}/choose-terapeut/{terapeutKeycloakId}`: Actualizează terapeutul preferat al pacientului.
- `DELETE /pacient/{keycloakId}/remove-terapeut`: Șterge terapeutul preferat.
- `PATCH /pacient/by-keycloak/{keycloakId}/toggle-active`: Endpoint intern apelat de `user-service` pentru a reflecta suspendările de conturi.
**Controller: `JurnalController`**
- `POST /jurnal/{pacientId}`: Adaugă o nouă înregistrare în jurnal.
- `PUT /jurnal/{pacientId}/{jurnalId}`: Modifică o înregistrare existentă.
- `GET /jurnal/istoric/{pacientId}`: Preluarea istoricului jurnalelor pacientului.
### 8.2.3 Logica de Business Principală
**Fluxul de Inițializare a Profilului**
Când un pacient se înregistrează, el furnizează doar Numele, Email-ul și Parola. Serviciul `pacienti-service` primește cererea POST pe `/initialize` și introduce o linie simplă în tabela `Pacient` conținând doar `keycloakId`. Pacientul este apoi forțat să completeze un asistent (wizard) de completare a profilului la prima conectare, care trimite cererea `POST /pacient/{keycloakId}` ce conține CNP-ul, data nașterii și datele despre stilul de viață.
**Alegerea Terapeutului & Cascada de Anulare**
Atunci când un pacient apelează `chooseTerapeut` sau `removeTerapeut`, serviciul actualizează valoarea `terapeutKeycloakId`. În mod critic, compară noul ID cu vechiul ID. Dacă terapeutul s-a schimbat, execută un apel Feign către `programari-service`:
```java
programariClient.anuleazaProgramariCuTerapeut(pacientKeycloakId, oldTerapeutKeycloakId);
```
Acest declanșator asincron garantează executarea cascadei de relații clinice (Secțiunea 4.5) în `programari-service`, arhivând vechea relație și anulând programările viitoare cu terapeutul abandonat.
### 8.2.4 Dependențe Inter-Servicii
- **Clienți Feign:** `ProgramariClient` (pentru a prelua datele programărilor pentru jurnal, a actualiza flag-ul `areJurnal`, a executa preluarea de date în lot (batch) și a declanșa cascada de anulare).
- **RabbitMQ:** Trimite evenimentul `notificare.jurnal.completat` în `notificari.exchange` prin intermediul `NotificarePublisher`.
### 8.3 Microserviciul: `terapeuti-service`
Serviciul `terapeuti-service` gestionează datele despre resursele umane ale clinicii, inclusiv specializările terapeuților, metadatele locațiilor și invarianții complexi de planificare (ferestrele de disponibilitate și perioadele de concediu) care alimentează motorul de rezervări.
### 8.3.1 Entități în Baza de Date & Enum-uri (Schema: `terapeuti_db`)
**Entitatea: `Terapeut`**
| Câmp | Tip | Constrângeri | Descriere |
|---|---|---|---|
| `id` | `Long` | PK, Auto-increment | ID-ul intern al bazei de date |
| `keycloakId` | `String` | Unic, lungime=36 | Face legătura cu identitatea din `user-service` |
| `specializare` | `Enum` | `ADULTI`, `PEDIATRIE` | Specializarea clinică |
| `pozaProfil` | `String` | `MEDIUMTEXT` (16MB) | Imaginea de avatar codificată Base64 |
| `active` | `Boolean` | Implicit=true | Flag de ștergere logică sincronizat din `user-service` |
| `createdAt` | `OffsetDateTime` | NOT NULL | Data și ora creării profilului |
| `updatedAt` | `OffsetDateTime` | NOT NULL | Data și ora actualizării profilului |
**Entitatea: `Locatie`**
| Câmp | Tip | Constrângeri | Descriere |
|---|---|---|---|
| `id` | `Long` | PK, Auto-increment | — |
| `nume` | `String` | lungime=200 | e.g., “Kineto Bebe Moșilor” |
| `adresa` | `String` | lungime=300 | Adresa fizică |
| `oras` | `String` | lungime=100 | Orașul |
| `judet` | `String` | lungime=100 | Județul |
| `codPostal` | `String` | lungime=10 | Codul poștal |
| `telefon` | `String` | lungime=20 | Numărul de contact |
| `active` | `Boolean` | Implicit=true | Dacă este false, blochează crearea de noi disponibilități în această clinică |
| `createdAt` | `OffsetDateTime` | NOT NULL | Data și ora creării înregistrării |
| `updatedAt` | `OffsetDateTime` | NOT NULL | Data și ora actualizării înregistrării |
**Entitatea: `DisponibilitateTerapeut`**
| Câmp | Tip | Constrângeri | Descriere |
|---|---|---|---|
| `id` | `Long` | PK, Auto-increment | ID-ul intern al bazei de date |
| `terapeutId` | `Long` | FK | Referință internă în DB către `Terapeut` |
| `locatieId` | `Long` | FK | Referință internă în DB către `Locatie` |
| `ziSaptamana` | `Integer` | 1-7 | Ziua din săptămână conform standardului ISO-8601 (1=Luni) |
| `oraInceput` | `LocalTime` | — | Ora de începere a turei |
| `oraSfarsit` | `LocalTime` | — | Ora de încheiere a turei |
| `active` | `Boolean` | Implicit=true | Indică dacă tura este activă |
| `createdAt` | `OffsetDateTime` | NOT NULL | Data și ora creării înregistrării |
| `updatedAt` | `OffsetDateTime` | NOT NULL | Data și ora actualizării înregistrării |
**Entitatea: `ConcediuTerapeut`**
| Câmp | Tip | Constrângeri | Descriere |
|---|---|---|---|
| `terapeutId` | `Long` | FK | Referință internă în DB către `Terapeut` |
| `dataInceput` | `LocalDate` | — | Data de începere a concediului (inclusiv) |
| `dataSfarsit` | `LocalDate` | — | Data de încheiere a concediului (inclusiv) |
| `createdAt` | `OffsetDateTime` | NOT NULL | Data și ora creării înregistrării |
### 8.3.2 Endpoint-uri REST
**Controller: `TerapeutController`**
- `POST /terapeut/initialize/{keycloakId}`: Apelat de `user-service` în timpul înregistrării.
- `GET /terapeut/by-keycloak/{keycloakId}`: Returnează un DTO complex agregat `TerapeutDTO` ce conține profilul de bază, toate înregistrările active `DisponibilitateDTO` mapate pe locațiile corespunzătoare `LocatieDTO` și toate viitoarele înregistrări `ConcediuDTO`.
- `PATCH /terapeut/{keycloakId}`: Actualizează datele de profil (inclusiv procesarea imaginii Base64).
- `PATCH /terapeut/by-keycloak/{keycloakId}/toggle-active`: Endpoint intern pentru suspendarea contului.
**Controller: `DisponibilitateController` & `ConcediuController`**
- Operațiuni standard CRUD definite la nivel de `keycloakId`, permițându-le terapeuților să își gestioneze propriile programe.
**Controller: `LocatieController`**
- Operațiuni CRUD pentru locațiile clinicilor, restricționate pentru rolurile de tip `ADMIN`.
### 8.3.3 Logica de Business Principală
**Algoritmul de Căutare și Filtrare (`TerapeutService.searchTerapeuti`)**
Folosit de API Gateway pentru a alimenta motorul de căutare din interfața pacienților.
1. Interoghează toți terapeuții activi care au specializarea (`specializare`) solicitată (`ADULTI` / `PEDIATRIE`).
2. Preluarea *tuturor* înregistrărilor active `DisponibilitateTerapeut` pentru acei terapeuți specifici în mod bulk pentru a evita interogările N+1.
3. Grupează disponibilitățile după `terapeutId` în memorie folosind Java Streams API (`Collectors.groupingBy`).
4. Aplică filtre geografice (`judet`, `oras`, `locatieId`) prin iterarea entităților `Locatie` asociate.
5. Returnează un obiect construit dinamic `TerapeutSearchDTO` ce listează terapeutul și exclusiv locațiile unice care corespund criteriilor de căutare.
**Garanții pentru Integritatea Programului**
Atunci când un terapeut încearcă să adauge o nouă disponibilitate (`Disponibilitate`) sau o perioadă de concediu (`Concediu`), serviciul impune invarianți temporali stricți:
- **Suprapunerea Disponibilităților (Disponibilitate Overlap):** Interogarea JPQL `findOverlappingDisponibilitate` garantează că un terapeut nu poate be programat la două locații diferite în aceeași zi în intervale de timp care se suprapun (`oraInceput < :oraSfarsit AND oraSfarsit > :oraInceput`).
- **Suprapunerea Concediilor (Concediu Overlap):** Se asigură că perioadele de concediu nu se intersectează între ele (`dataInceput <= :dataSfarsit AND dataSfarsit >= :dataInceput`).
### 8.3.4 Dependențe Inter-Servicii
`terapeuti-service` funcționează ca un **Nod Frunză (Leaf Node)** în clusterul de microservicii. Acesta nu inițiază apeluri Feign sau evenimente RabbitMQ. În schimb, este intens interogat de:
- `user-service` (pentru sincronizarea stării)
- `programari-service` (pentru validarea limitelor de tură și a concediilor în timpul algoritmului de generare a sloturilor, prin `TerapeutiClient`)
- `API Gateway` (agregare BFF)
### 8.4 Microserviciul: `programari-service`
Serviciul `programari-service` este orchestratorul central al domeniului de business. Coordonează terapeuții, pacienții și oferirea de servicii pentru a crea programări valide, gestionează ciclul de viață al relațiilor clinice și administrează matricea de evaluări.
### 8.4.1 Entități în Baza de Date & Enum-uri (Schema: `programari_db`)
**Entitatea: `Programare`**
- `id` (PK), `pacientKeycloakId`, `terapeutKeycloakId`, `locatieId`, `serviciuId`.
- `tipServiciu` (String), `pret` (BigDecimal), `durataMinute` (Integer), `primaIntalnire` (Boolean).
- `data` (LocalDate), `oraInceput` (LocalTime), `oraSfarsit` (LocalTime).
- `status` (`PROGRAMATA`, `FINALIZATA`, `ANULATA`).
- `motivAnulare` (`ANULAT_DE_PACIENT`, `ANULAT_DE_TERAPEUT`, `NEPREZENTARE`, `ADMINISTRATIV`).
- `areEvaluare` (Boolean), `areJurnal` (Boolean), `createdAt` (OffsetDateTime), `updatedAt` (OffsetDateTime).
**Entitatea: `RelatiePacientTerapeut`**
- `id` (PK), `pacientKeycloakId`, `terapeutKeycloakId`.
- `dataInceput` (LocalDate), `dataSfarsit` (LocalDate), `activa` (Boolean).
- `createdAt` (OffsetDateTime), `updatedAt` (OffsetDateTime).
**Entitatea: `Evaluare`**
- `id` (PK), `pacientKeycloakId`, `terapeutKeycloakId`, `programareId` (FK).
- `tip` (`INITIALA`, `REEVALUARE`).
- `data` (LocalDate), `diagnostic` (TEXT), `sedinteRecomandate` (Integer), `serviciuRecomandatId` (Long), `observatii` (TEXT).
- `createdAt` (OffsetDateTime), `updatedAt` (OffsetDateTime).
**Entitatea: `Evolutie`**
- `id` (PK), `pacientKeycloakId`, `terapeutKeycloakId`, `observatii` (TEXT).
- `createdAt` (OffsetDateTime), `updatedAt` (OffsetDateTime).
### 8.4.2 Endpoint-uri REST
- `POST /programari`: Creează o nouă programare (cu validarea suprapunerilor de program).
- `GET /programari/pacient/{keycloakId}` & `/terapeut/{keycloakId}`: Preluarea ședințelor programate.
- `GET /programari/{id}/detalii`: Endpoint Feign intern folosit de `pacienti-service` pentru sincronizarea Jurnalului.
- `POST /programari/anuleaza-cu-terapeut`: Declanșat de `pacienti-service` pentru arhivarea relației în cascadă.
- `GET /relatii/status-keycloak`: Declanșat de `chat-service` pentru validarea contextului de securitate înaintea trimiterii mesajelor.
### 8.4.3 Dependențe Inter-Servicii
- **Clienți Feign:** Agregă date din `TerapeutiClient` (limitele programului), `PacientiClient`, `UserClient` (metadate pentru notificări) și `ServiciiClient` (facturare/durată).
- **RabbitMQ:** Trimite evenimente precum `notificare.programare.noua`, `notificare.reevaluare.recomandata` și `notificare.jurnal.reminder`.
*(Consultați Secțiunile 3 și 4 pentru o analiză detaliată a algoritmului de rezervare `determinaServiciulCorect`, a matricei de suprapunere și a joburilor cron adnotate cu `@Transactional`).*
### 8.4.4 Cea mai Complexă Agregare: `FisaPacientService.getFisaPacient()`
Metoda: `FisaPacientService.getFisaPacient(String pacientKeycloakId, String terapeutKeycloakId)`
Adnotare: `@Transactional(readOnly = true)`
Această metodă reprezintă **cea mai complexă agregare de date din întreaga bază de cod KinetoCare**. Construiește o vedere clinică completă, de 360 de grade a unui pacient, prin orchestrarea datelor din **4 microservicii diferite** și **4 interogări în baza de date locală**, producând un obiect rezultat de tip `FisaPacientDetaliiDTO` cu 13 câmpuri. Acesta este motorul de date din spatele interfeței terapeutului „Fișa Pacientului” — ecranul unde terapeutul poate vizualiza dintr-o singură privire toate informațiile despre pacient.
**Orchestrarea pas cu pas:**
| Pas | Sursă | Date Preluate | Tip Comunicare |
|------|--------|----------------|-------------------|
| 1 | `userClient` -> `user-service` | Identitate pacient (nume, email, telefon, gen) | Sincronă Feign |
| 2 | `pacientiClient` -> `pacienti-service` | Profil medical (data nașterii -> vârsta calculată, obiceiuri sportive) | Sincronă Feign |
| 3 | `programareService` (local) | Situația clinică curentă (diagnosticul activ, ședințe rămase) | Apel de metodă locală |
| 4 | `evaluareRepository` (local) | Istoricul complet al evaluărilor (toate evaluările, de la toți terapeuții) | Interogare locală JPA |
| 4a | `userClient` x N -> `user-service` | Numele terapeutului care a efectuat fiecare evaluare | Sincronă Feign (per evaluare) |
| 4b | `serviciiClient` x N -> `servicii-service` | Numele serviciului recomandat pentru fiecare evaluare | Sincronă Feign (per evaluare) |
| 5 | `evolutieService` (local) | Istoricul notelor de evoluție (doar de la terapeutul curent) | Apel de metodă locală |
| 6 | `programareService` (local) | Istoricul complet al programărilor (trecute și viitoare) | Apel de metodă locală |
| 7 | `pacientiClient` -> `pacienti-service` | Istoricul jurnalului pacientului (feedback-ul subiectiv introdus) | Sincronă Feign |
**Total apeluri externe:** 3 apeluri fixe Feign + **2 x N** apeluri Feign (unde N = numărul de evaluări din istoricul pacientului) + 4 interogări în baza de date locală.
**Problema N+1 în `buildEvaluariList`:**
Metoda ajutătoare privată `buildEvaluariList(pacientKeycloakId)` preia mai întâi toate entitățile `Evaluare` din baza de date locală. Apoi, pentru **fiecare** evaluare în parte, efectuează alte două apeluri de rețea între servicii:
```java
return evaluari.stream().map(eval -> {
    // Apelul 1: Rezolvarea numelui terapeutului din user-service
    UserDisplayCalendarDTO terapeutDetails = userClient.getUserByKeycloakId(eval.getTerapeutKeycloakId());
    numeTerapeut = terapeutDetails.nume() + " " + terapeutDetails.prenume();
    // Apelul 2: Rezolvarea numelui serviciului din servicii-service
    DetaliiServiciuDTO serviciu = serviciiClient.getServiciuById(eval.getServiciuRecomandatId());
    serviciuNume = serviciu.nume();
    return new EvaluareResponseDTO(/* ... 10 fields ... */);
}).toList();
```
Aceasta reprezintă o problemă clasică de **interogare N+1** la nivelul comunicării dintre servicii — genul de problemă care este ușor de trecut cu vederea într-un monolit, dar care devine un factor critic de performanță într-o arhitectură de microservicii. Pentru un pacient cu 5 evaluări, această singură metodă generează 3 + (5 x 2) = **13 apeluri HTTP externe**. Fiecare apel este învelit într-un handler de erori Feign (`try/catch`) care degradează elegant performanța: dacă `user-service` este temporar indisponibil, numele terapeutului va avea valoarea implicită `null` în loc de a prăbuși întreaga agregare.
**De ce este aceasta cea mai complexă agregare:**
1. **Amploare (Breadth):** Interacționează cu 4 microservicii diferite (`user-service`, `pacienti-service`, `servicii-service` și baza de date locală `programari-service`), fiind mai complexă decât orice altă funcție din sistem.
2. **Profunzime (Depth):** Bucla imbricată N+1 din `buildEvaluariList` generează un model de apel multiplicativ — numărul total de apeluri externe nefiind fix, ci crescând direct proporțional cu istoricul clinic al pacientului.
3. **Diversitatea datelor:** DTO-ul rezultat (`FisaPacientDetaliiDTO`) îmbină date de identitate, date medicale, evaluări clinice, note de evoluție, istoricul programărilor și feedback-ul subiectiv din jurnal într-un singur payload coerent — un dosar complet al pacientului.
4. **Toleranță la erori:** Fiecare apel extern este învelit în blocuri defensive `try/catch` cu strategii de fallback bazate pe `log.warn`, garantând că o problemă temporară într-un serviciu (cum ar fi încetinirea `servicii-service`) degradează doar o secțiune a fișei pacientului, fără a bloca vizualizarea întregului dosar.
**Comparație cu `ProfileService.getProfile()` (Gateway):**
| Dimensiune | `ProfileService.getProfile()` | `FisaPacientService.getFisaPacient()` |
|-----------|------------------------------|--------------------------------------|
| Locație | API Gateway (reactiv WebFlux) | programari-service (imperativ MVC) |
| Apeluri externe | Până la 5 (fix) | 3 + 2xN (variabil) |
| Surse de date | 3 microservicii | 4 microservicii + DB locală |
| Câmpuri de ieșire | ~15 (flat map) | 13 de nivel superior (liste imbricate) |
| Modelul N+1 | Niciunul | Dublu N+1 per evaluare |
| Stil comunicare | Non-blocant (`Mono.zip`) | Blocant (Feign) |
### 8.5 Microserviciul: `servicii-service`
Serviciul `servicii-service` funcționează ca motor de catalog și prețuri al clinicii. Acesta administrează metadatele pentru intervențiile clinice.
### 8.5.1 Entități în Baza de Date (Schema: `servicii_db`)
**Entitatea: `TipServiciu`**
- `id` (PK), `nume` (e.g., “Kinetoterapie”, “Evaluare”), `descriere`, `active`, `createdAt` (OffsetDateTime), `updatedAt` (OffsetDateTime).
**Entitatea: `Serviciu`**
- `id` (PK), `tipServiciuId` (FK), `nume` (e.g., “Kinetoterapie Adulți”).
- `pret` (BigDecimal), `durataMinute` (Integer).
- `active` (Boolean), `createdAt` (OffsetDateTime), `updatedAt` (OffsetDateTime).
### 8.5.2 Endpoint-uri REST
- `GET /servicii`: Returnează toate serviciile active (Destinat pacienților).
- `GET /servicii/admin`: Returnează toate serviciile, inclusiv cele inactive.
- `GET /servicii/cauta`: Endpoint special folosit intern pentru rezolvarea corectă a serviciului clinic.
- `POST /servicii`, `PATCH /servicii/{id}`, `PATCH /servicii/{id}/toggle-active`: Operațiuni CRUD de administrare.
### 8.5.3 Logica de Business Principală
**Rezolvarea Serviciului (`ServiciuService.cautaDupaNume`)**
Folosit de `programari-service` pentru a determina automat dacă un pacient are nevoie de o „Evaluare” în detrimentul unei ședințe standard:
1. Încearcă o căutare după potrivirea exactă a numelui (`findByNumeContainingIgnoreCase`).
2. În caz de eșec, execută o interogare de tip fallback pe categoria părinte: `findByTipServiciu_NumeContainingIgnoreCase`.
Acest lucru previne hardcodarea ID-urilor între microservicii, asigurând supraviețuirea motorului de rezervări în cazul unor restructurări de catalog.
### 8.6 Microserviciul: `chat-service`
Gestionează mesageria securizată în timp real între perechile autorizate pacient-terapeut.
### 8.6.1 Entități în Baza de Date (Schema: `chat_db`)
- **`Conversatie`**: `id`, `pacientKeycloakId`, `terapeutKeycloakId`, `ultimulMesajLa`, `createdAt` (OffsetDateTime), `updatedAt` (OffsetDateTime).
- **`Mesaj`**: `id`, `conversatieId` (FK), `expeditorKeycloakId`, `tipExpeditor` (`PACIENT`, `TERAPEUT`), `continut`, `esteCitit`, `cititLa`, `trimisLa` (OffsetDateTime, not null).
### 8.6.2 Endpoint-uri REST & WebSocket
- **WebSocket:** `/chat/ws-chat` (endpoint SockJS).
- **Destinație STOMP:** `@MessageMapping("/chat.send")`.
- **REST:** `GET /chat/conversatii` (Istoric), `PUT /chat/mesaje/citite` (Confirmări de citire).
### 8.6.3 Dependențe Inter-Servicii
- **Securitate:** `StompSecurityInterceptor` asociază tokenul Keycloak JWT cu sesiunea STOMP.
- **Feign:** Cerere sincronă către `programari-service` (`getRelatieStatusByKeycloak`) pentru validarea statusului `ACTIVA` al relației.
- **RabbitMQ:** Emite notificări asincrone pentru utilizatorii offline (`notificare.mesaj.nou`) către `notificari-service`.
*(Consultați Secțiunea 6 pentru o analiză completă a integrării STOMP și a mapării contextului de securitate pe thread-uri).*
### 8.7 Microserviciul: `notificari-service`
Colectorul centralizat de evenimente de notificare.
### 8.7.1 Entități în Baza de Date (Schema: `notificari_db`)
- **`Notificare`**: `id`, `userKeycloakId`, `tipUser` (`PACIENT`, `TERAPEUT`), `tip` (Enum), `titlu`, `mesaj`, `entitateLegataId`, `tipEntitateLegata`, `urlActiune`, `esteCitita`, `cititaLa`, `createdAt` (OffsetDateTime).
### 8.7.2 Endpoint-uri REST
- `GET /notificari`: Preluarea listei de notificări ale utilizatorului.
- `GET /notificari/necitite/count`: Alimentează indicatorul roșu cu numărul de notificări din UI.
- `PUT /notificari/{id}/citita`: Confirmare individuală de citire.
- `PUT /notificari/citite-toate`: Executarea marcării în masă ca citite.
### 8.7.3 Dependențe Inter-Servicii
- **Consumatori RabbitMQ:** Ascultă în `notificari.exchange` pe coada `notificari.queue.v2`. Implementează rutarea către DLQ în `notificari.dlx` și către `DeadLetterConsumer` pentru reziliență.
*(Consultați Secțiunea 7 pentru o analiză detaliată a legăturilor exchange-urilor AMQP, a topologiei DLQ și a implementării modelului de design Observer).*
