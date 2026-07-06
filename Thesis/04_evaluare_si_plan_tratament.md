## Secțiunea 4: Evaluarea Clinică și Fluxul Planului de Tratament (Clinical Evaluation & Treatment Plan Flow)
### 4.1 Entitatea: `Evaluare`
Clasa: `com.example.programari_service.entity.Evaluare`
Tabela: `evaluari` (se află în `programari_db`, nu într-un serviciu separat)
| Câmp | Tip | Constrângere | Note |
| --- | --- | --- | --- |
| `id` | `Long` | PK, auto-increment | — |
| `pacientKeycloakId` | `String` | NOT NULL, lungime=36 | UUID-ul Keycloak al pacientului |
| `terapeutKeycloakId` | `String` | NOT NULL, lungime=36 | UUID-ul Keycloak al terapeutului |
| `programareId` | `Long` | nullable | FK către `programari.id` |
| `tip` | `TipEvaluare` | NOT NULL | Enum: `INITIALA` / `REEVALUARE` |
| `data` | `LocalDate` | NOT NULL | Copiată din programarea asociată |
| `diagnostic` | `TEXT` | NOT NULL | Diagnosticul clinic în format text liber |
| `sedinteRecomandate` | `Integer` | nullable | Numărul de ședințe de tratament prescrise |
| `serviciuRecomandatId` | `Long` | nullable | FK către `servicii_db.servicii.id` |
| `observatii` | `TEXT` | nullable | Note clinice suplimentare scrise de terapeut |
| `createdAt` | `OffsetDateTime` | NOT NULL | Data și ora creării înregistrării |
| `updatedAt` | `OffsetDateTime` | NOT NULL | Data și ora actualizării înregistrării |
Sunt declarați patru indecși pe această entitate:
- `idx_eval_pacient_data` pe `(pacient_keycloak_id, data)` — calea de interogare cea mai frecventă (cea mai recentă evaluare a pacientului)
- `idx_eval_terapeut` pe `(terapeut_keycloak_id)` — lista de evaluări ale terapeutului
- `idx_eval_programare` pe `(programare_id)` — căutare unu-la-unu după programare
- `idx_eval_tip` pe `(tip)` — filtrare după tipul evaluării
### 4.2 Entitatea: `RelatiePacientTerapeut`
Clasa: `com.example.programari_service.entity.RelatiePacientTerapeut`
Tabela: `relatie_pacient_terapeut`
| Câmp | Tip | Constrângere | Note |
| --- | --- | --- | --- |
| `id` | `Long` | PK, auto-increment | — |
| `pacientKeycloakId` | `String` | NOT NULL, lungime=36 | — |
| `terapeutKeycloakId` | `String` | NOT NULL, lungime=36 | — |
| `dataInceput` | `LocalDate` | NOT NULL | Data primei programări finalizate sau a evaluării |
| `dataSfarsit` | `LocalDate` | nullable | Setată atunci când relația este dezactivată |
| `activa` | `Boolean` | NOT NULL, implicit=true | Cel mult o singură înregistrare cu `activa=true` per pacient la orice moment dat |
| `createdAt` | `OffsetDateTime` | NOT NULL | Data și ora creării înregistrării |
| `updatedAt` | `OffsetDateTime` | NOT NULL | Data și ora actualizării înregistrării |
Doi indecși:
- `idx_rel_pacient_activa` pe `(pacient_keycloak_id, activa)` — căutare principală pentru „cine este terapeutul meu curent”
- `idx_rel_terapeut_activa` pe `(terapeut_keycloak_id, activa)` — lista de pacienți activi ai terapeutului
Invarianța de unicitate — **cel mult o înregistrare cu `activa=true` per pacient** — este impusă la nivel de aplicație în cadrul `RelatieService`, nu printr-o constrângere de unicitate în baza de date. Acesta este un compromis deliberat: o constrângere de unicitate pe `(pacient_keycloak_id, activa)` nu ar funcționa corect în MySQL deoarece valorile `NULL` nu sunt considerate unice în acest context, iar tipul `BOOLEAN` ar face dificilă implementarea unor soluții alternative bazate pe indecși parțiali.
### 4.3 Cum sunt Asociate Evaluările cu Programările
Asocierea este bidirecțională — entitatea `Evaluare` deține proprietatea `programareId`, iar entitatea `Programare` deține flag-ul `areEvaluare`.
### 4.3.1 `EvaluareService.creeazaEvaluare`
Metoda: `EvaluareService.creeazaEvaluare(EvaluareRequestDTO request)`
Adnotare: `@Transactional`
Obiectul `EvaluareRequestDTO` conține: `pacientKeycloakId`, `terapeutKeycloakId`, `programareId` (opțional), `tip`, `diagnostic`, `sedinteRecomandate`, `serviciuRecomandatId`, `observatii`.
**Etapele de execuție:**
1. `evaluareMapper.toEntity(request)` construiește obiectul `Evaluare` din DTO. În acest stadiu, proprietățile `programareId` și `data` nu sunt încă setate.
2. **Asocierea cu programarea — două căi posibile:**
    **Calea A — `programareId` este null (terapeutul nu a ales o programare din formular):**
    ```java
    Programare ultima = getUltimaProgramare(request.pacientKeycloakId(), request.terapeutKeycloakId());
    ```
    Metoda `getUltimaProgramare` apelează:
    ```java
    programareRepository.findLatestAppointments(pacientKeycloakId, terapeutKeycloakId, PageRequest.of(0, 1))
    ```
    Care execută SQL-ul:
    ```sql
    SELECT p FROM Programare p
    WHERE p.pacientKeycloakId = :pacientKeycloakId
    AND p.terapeutKeycloakId = :terapeutKeycloakId
    AND p.status = 'FINALIZATA'
    ORDER BY p.data DESC, p.oraInceput DESC
    ```
    Se va utiliza cea mai recent finalizată programare dintre acest pacient și acest terapeut. Apoi:
    ```java
    evaluare.setProgramareId(ultima.getId());
    evaluare.setData(ultima.getData());
    ultima.setAreEvaluare(true);
    programareRepository.save(ultima); // setează areEvaluare=true pe programare
    ```
    Dacă nu există absolut nicio programare cu statusul `FINALIZATA` (caz limită: terapeutul creează o evaluare înainte de prima ședință), se folosește ca fallback `evaluare.setData(LocalDate.now())`, iar `programareId` rămâne null.
    **Calea B — `programareId` este furnizat (terapeutul a selectat în mod explicit programarea):**
    ```java
    Programare p = programareRepository.findById(request.programareId())
        .orElseThrow(() -> new ResourceNotFoundException(...));
    evaluare.setProgramareId(p.getId());
    evaluare.setData(p.getData());
    p.setAreEvaluare(true);
    programareRepository.save(p);
    ```
3. `evaluareRepository.save(evaluare)` persistă evaluarea în baza de date.
4. `relatieService.asiguraRelatieActiva(request.pacientKeycloakId(), request.terapeutKeycloakId(), evaluareSalvata.getData())` — activează relația terapeutică (vezi Secțiunea 4.5).
Toate cele patru operațiuni — găsirea programării, marcarea `areEvaluare=true`, salvarea evaluării și activarea relației — se execută în interiorul aceleiași granițe `@Transactional`. Dacă oricare dintre pași eșuează, nicio modificare nu va fi salvată în baza de date.
### 4.3.2 De ce `areEvaluare = true` Exclude Ședința din Contorizare
Atunci când proprietatea `areEvaluare` are valoarea `true`, programarea este exclusă din rezultatul `countSedintePacientDupaData`:
```sql
SELECT COUNT(p) FROM Programare p
WHERE p.pacientKeycloakId = :pId
AND p.status = 'FINALIZATA'
AND p.areEvaluare = false   -- ← această excludere
AND p.data >= :dataRef
```
Logica clinică din spate este următoarea: o ședință de evaluare reprezintă o interacțiune de diagnostic, nu o ședință efectivă de tratament. Terapeutul realizează evaluarea, documentarea și planificarea recuperării — nu livrarea tratamentului propriu-zis. Contorizarea acestei ședințe în cadrul cotei `sedinteRecomandate` ar reduce în mod artificial planul de tratament al pacientului cu câte o ședință pentru fiecare evaluare efectuată.
Această excludere este aplicată în mod consecvent atât în `determinaServiciulCorect` (fluxul de rezervare), cât și în `trimiteNotificariDupaFinalizare` (jobul cron post-procesare), garantând că logica nu devine niciodată inconsistentă între decizia de tipul „ce serviciu trebuie rezervat în continuare” și notificarea „planul de tratament a fost finalizat”.
### 4.4 Algoritmul `asiguraRelatieActiva`
Metoda: `RelatieService.asiguraRelatieActiva(String pacientKeycloakId, String terapeutKeycloakId, LocalDate dataInceput)`
Adnotare: `@Transactional`
Această metodă este apelată din două puncte de declanșare distincte:
- **După ce jobul cron finalizează o programare** (`trimiteNotificariDupaFinalizare`) — prima programare finalizată = prima dovadă concretă a unei interacțiuni terapeutice
- **După ce este creată o evaluare** (`EvaluareService.creeazaEvaluare`) — crearea unei evaluări reprezintă în sine o dovadă a unei relații terapeutice active
Algoritmul gestionează trei cazuri distincte pe baza rezultatului interogării:
```java
Optional<RelatiePacientTerapeut> relatieOpt =
    relatieRepository.findByPacientKeycloakIdAndTerapeutKeycloakId(
        pacientKeycloakId, terapeutKeycloakId);
```
Această interogare returnează orice înregistrare existentă (activă sau inactivă) ce leagă această pereche specifică pacient-terapeut.
### Cazul 1 — Înregistrarea există și este deja activă
```java
if (relatieOpt.isPresent()) {
    RelatiePacientTerapeut relatie = relatieOpt.get();
    if (Boolean.FALSE.equals(relatie.getActiva())) {
        // intră pe Cazul 2
    }
    // dacă este deja activă: nu se face nimic
}
```
Dacă relația există și `activa = true`, metoda se termină fără nicio scriere în baza de date. Aceasta reprezintă calea obișnuită pentru fiecare ședință de după prima — relația fiind activată la prima programare finalizată și rămânând activă în continuare.
### Cazul 2 — Înregistrarea există, dar este inactivă (pacient care revine)
```java
if (Boolean.FALSE.equals(relatie.getActiva())) {
    dezactiveazaRelatiaActiva(pacientKeycloakId); // arhivează orice relație activă curentă cu un terapeut DIFERIT
    relatie.setActiva(true);
    relatie.setDataSfarsit(null);   // curăță data de sfârșit provenită din dezactivarea anterioară
    relatieRepository.save(relatie);
}
```
Acest caz vizează un pacient care a lucrat anterior cu terapeutul X (a cărui relație a fost arhivată prin setarea datei `dataSfarsit`), a trecut ulterior la terapeutul Y, iar acum revine la terapeutul X. Rândul existent și arhivat este reactivat: parametrul `activa` trece pe `true`, iar `dataSfarsit` este setat pe null. Înainte de reactivare, se apelează `dezactiveazaRelatiaActiva(pacientKeycloakId)` pentru a arhiva relația curentă activă cu terapeutul Y.
### Cazul 3 — Nu există nicio înregistrare anterioară (prima interacțiune cu acest terapeut)
```java
} else {
    dezactiveazaRelatiaActiva(pacientKeycloakId); // arhivează orice relație activă curentă
    RelatiePacientTerapeut nouaRelatie =
        relatieMapper.toEntity(pacientKeycloakId, terapeutKeycloakId, dataInceput);
    relatieRepository.save(nouaRelatie);
}
```
Se creează o înregistrare complet nouă `RelatiePacientTerapeut` cu statusul `activa = true`, `dataInceput = dataInceput` (data programării) și `dataSfarsit = null`. Înainte de crearea acesteia, se apelează `dezactiveazaRelatiaActiva` pentru a arhiva orice relație activată anterior.
### `dezactiveazaRelatiaActiva` — sub-operațiunea de arhivare
Metoda: `RelatieService.dezactiveazaRelatiaActiva(String pacientKeycloakId)`
Adnotare: `@Transactional`
```java
relatieRepository.findByPacientKeycloakIdAndActivaTrue(pacientKeycloakId)
    .ifPresent(relatie -> {
        relatie.setActiva(false);
        relatie.setDataSfarsit(LocalDate.now());
        relatieRepository.save(relatie);
    });
```
`RelatieRepository.findByPacientKeycloakIdAndActivaTrue` returnează un `Optional<RelatiePacientTerapeut>` — cel mult un singur rând, deoarece invarianța permite doar o singură relație activă per pacient. Dacă este găsită o înregistrare, se setează `activa = false` și se aplică data curentă `dataSfarsit = LocalDate.now()`. Aceasta este o **arhivare logică (soft archive)** — înregistrarea istorică fiind conservată.
Deoarece `asiguraRelatieActiva` este marcată ca `@Transactional`, iar `dezactiveazaRelatiaActiva` este la rândul ei `@Transactional`, propagarea implicită a tranzacțiilor din Spring (`REQUIRED`) determină ca apelul intern să **se alăture tranzacției existente**, în loc să pornească una nouă. Atât arhivarea vechii relații, cât și crearea/reactivarea celei noi sunt comise într-o singură unitate atomică.
### 4.5 Fluxul de Schimbare a Terapeutului (Therapist Change Flow)
Procesul de schimbare a terapeutului este inițiat de pacient din pagina `ProfilPacient`, care apelează `PATCH /api/pacient/{id}` cu parametrul `{ "terapeutKeycloakId": "<new_therapist_uuid>" }`. Serviciul `ProfileService.updateProfile()` din API Gateway rutează câmpul `terapeutKeycloakId` către `pacienti-service`. În interiorul `pacienti-service`, atunci când `terapeutKeycloakId` este actualizat pe entitatea `Pacient`, se lansează un apel Feign către `programari-service`:
```java
programariClient.anuleazaProgramariVechi(pacientKeycloakId, oldTerapeutKeycloakId);
```
Aceasta declanșează execuția metodei `ProgramareService.anuleazaProgramariVechi` în `programari-service`:
### `anuleazaProgramariVechi`
Metoda: `ProgramareService.anuleazaProgramariVechi(String pacientKeycloakId, String terapeutKeycloakId)`
Adnotare: `@Transactional`
```java
// Pasul 1: Arhivarea relației active curente
relatieService.dezactiveazaRelatiaActiva(pacientKeycloakId);
// Pasul 2: Găsirea tuturor programărilor viitoare cu statusul PROGRAMATA la VECHIUL terapeut
List<Programare> programari =
    programareRepository.findByPacientKeycloakIdAndTerapeutKeycloakIdAndStatusAndDataGreaterThanEqual(
        pacientKeycloakId, terapeutKeycloakId,
        StatusProgramare.PROGRAMATA, LocalDate.now(), LocalTime.now());
// Pasul 3: Anularea tuturor acestor programări
if (!programari.isEmpty()) {
    programari.forEach(p -> {
        p.setStatus(StatusProgramare.ANULATA);
        p.setMotivAnulare(MotivAnulare.ANULAT_DE_PACIENT);
    });
    programareRepository.saveAll(programari);
    // Pasul 4: Notificarea terapeutului pentru fiecare programare anulată
    programari.forEach(notificarePublisher::programareAnulataDePacient);
}
```
Codul JPQL pentru `findByPacientKeycloakIdAndTerapeutKeycloakIdAndStatusAndDataGreaterThanEqual`:
```sql
SELECT p FROM Programare p
WHERE p.pacientKeycloakId = :pacientKeycloakId
AND p.terapeutKeycloakId = :terapeutKeycloakId
AND p.status = :status
AND (p.data > :data OR (p.data = :data AND p.oraInceput > :ora))
```
Acest query identifică în mod corect programările care încep **strict în viitor** (fie la o dată viitoare, fie în ziua curentă, dar la o oră viitoare). Programările care au început deja sau care sunt în curs de desfășurare nu sunt anulate.
Valoarea `motivAnulare` este setată pe `ANULAT_DE_PACIENT` deoarece pacientul este cel care a inițiat schimbarea terapeutului — prin urmare, anularea îi este atribuită deciziei sale.
Fiecare programare anulată declanșează `notificarePublisher.programareAnulataDePacient(p)` → cheia de rutare `notificare.programare.anulata.pacient` — notificând vechiul terapeut despre fiecare dintre ședințele sale anulate.
Arhivarea relației (`dezactiveazaRelatiaActiva`) și anularea în lot (batch cancellation) rulează în cadrul aceleiași granițe `@Transactional`. Dacă operațiunea `saveAll` eșuează (e.g., eroare de bază de date), nici relația nu va fi arhivată — asigurând consistența deplină a stării sistemului.
### 4.6 De ce Numărul de Ședințe NU se Resetează la Schimbarea Terapeutului
Aceasta este o decizie arhitecturală asumată în mod deliberat, implementată la nivelul interogării din `countSedintePacientDupaData`:
```sql
SELECT COUNT(p) FROM Programare p
WHERE p.pacientKeycloakId = :pId     -- ← filtrat după pacient, NU după terapeut
AND p.status = 'FINALIZATA'
AND p.areEvaluare = false
AND p.data >= :dataRef               -- ← de la data ultimei evaluări
```
Interogarea contorizează **toate ședințele finalizate care nu au fost evaluări pentru pacientul respectiv**, indiferent de terapeutul care a livrat acele ședințe. Proprietatea `terapeutKeycloakId` nu este inclusă în clauza WHERE.
Atunci când un pacient schimbă terapeutul:
1. Relația veche este arhivată — programările viitoare ale vechiului terapeut sunt anulate.
2. Terapeutul nou va rula în mod natural algoritmul `determinaServiciulCorect` atunci când pacientul face prima sa rezervare. Acest algoritm apelează `findFirstByPacientKeycloakIdOrderByDataDesc` — care aduce **cea mai recentă evaluare la nivelul tuturor terapeuților**.
3. Dacă ultima evaluare a recomandat 10 ședințe, iar pacientul a efectuat deja 7 ședințe cu vechiul terapeut, `countSedintePacientDupaData` va returna valoarea 7. Prima rezervare la noul terapeut va fi programată ca fiind ședința numărul 8 din 10 — continuând planul existent.
Justificarea clinică este documentată în `CONTEXT_DIZERTATIE.md`: diagnosticul și tratamentul prescris aparțin **pacientului**, nu unui terapeut anume. Planul de fizioterapie însoțește pacientul. Noul terapeut vizualizează același diagnostic, aceeași contorizare a ședințelor și preia cazul din exact același stadiu.
Atunci când condiția `sedinteEfectuate >= sedinteRecomandate` este în cele din urmă atinsă (indiferent de numărul de terapeuți care au livrat acele ședințe), sistemul programează automat o **Reevaluare**, oferindu-i noului terapeut oportunitatea de a re-evalua pacientul și de a stabili un nou plan de tratament.
### 4.7 Fluxul de Actualizare a Evaluării (Evaluation Update Flow)
Metoda: `EvaluareService.actualizeazaEvaluare(Long id, EvaluareRequestDTO request)`
Adnotare: `@Transactional`
Terapeuții pot edita parametrii `diagnostic`, `sedinteRecomandate`, `serviciuRecomandatId` și `observatii` ai unei evaluări existente. Câmpurile `programareId`, `data`, `pacientKeycloakId`, `terapeutKeycloakId` și `tip` **nu pot fi actualizate** — acestea fiind câmpuri de identitate care ancorează evaluarea în timp și context.
```java
Evaluare evaluare = evaluareRepository.findById(id)
    .orElseThrow(() -> new ResourceNotFoundException("Evaluarea cu ID-ul " + id + " nu a fost găsită."));
evaluare.setDiagnostic(request.diagnostic());
evaluare.setSedinteRecomandate(request.sedinteRecomandate());
evaluare.setServiciuRecomandatId(request.serviciuRecomandatId());
evaluare.setObservatii(request.observatii());
return evaluareRepository.save(evaluare);
```
Actualizarea proprietății `sedinteRecomandate` modifică retroactiv cota de ședințe. Dacă terapeutul o crește de la 8 la 12, următoarea rezervare prin `determinaServiciulCorect` va constata că `sedinteEfectuate < 12` și va continua cu serviciul recomandat, în loc să programeze o reevaluare. Acest lucru le oferă terapeuților control clinic total pentru a prelungi sau ajusta planurile de tratament.
### 4.8 Endpoint-ul pentru Situația Pacientului (`getSituatiePacient`)
Metoda: `ProgramareService.getSituatiePacient(String pacientKeycloakId)`
Adnotare: `@Transactional(readOnly = true)`
Acest endpoint alimentează bara de progres de pe pagina principală a pacientului (`HomepagePacient`). Este preluat de `HomepageService` din API Gateway ca parte a agregării BFF (`/programari/pacient/by-keycloak/{id}/situatie`).
```java
Optional<Evaluare> evaluareOpt =
    evaluareRepository.findFirstByPacientKeycloakIdOrderByDataDesc(pacientKeycloakId);
if (evaluareOpt.isEmpty()) {
    return evaluareMapper.toEmptySituatiePacientDTO();
}
Evaluare evaluare = evaluareOpt.get();
long sedinteEfectuate = programareRepository.countSedintePacientDupaData(
    pacientKeycloakId, evaluare.getData());
return evaluareMapper.toSituatiePacientDTO(evaluare, sedinteEfectuate);
```
`toSituatiePacientDTO` produce un obiect `SituatiePacientDTO` ce conține:
- `diagnostic` — textul diagnosticului clinic stabilit de terapeut
- `sedinteEfectuate` — numărul de ședințe efectuate de la ultima evaluare
- `sedinteRecomandate` — numărul de ședințe prescrise
- `serviciuRecomandatId` — tipul de serviciu recomandat
Frontend-ul folosește raportul `sedinteEfectuate / sedinteRecomandate` pentru a randa o bară de progres. Aceeași interogare JPQL (care exclude evaluările prin filtrul `areEvaluare = false`) asigură că bara de progres este corectă și nu include ședințele de evaluare.
Dacă nu există nicio evaluare, `toEmptySituatiePacientDTO()` returnează valori egale cu zero, iar frontend-ul randează o stare inițială / de bun venit, informând pacientul că prima sa evaluare nu a fost încă finalizată.
