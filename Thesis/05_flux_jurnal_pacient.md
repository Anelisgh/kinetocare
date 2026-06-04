## Secțiunea 5: Fluxul Jurnalului Pacientului (Patient Journal Flow)
### 5.1 Prezentare Generală
Jurnalul Pacientului reprezintă feedback-ul subiectiv oferit de pacient după fiecare ședință. Odată ce o programare este finalizată (de regulă prin intermediul jobului cron descris în Secțiunea 3), pacientul primește o notificare care îl invită să completeze jurnalul pentru acea ședință. Aceste date îi permit terapeutului să urmărească nivelul de durere, dificultatea exercițiilor și gradul de oboseală între ședințe, adaptând dinamic planul de tratament.
### 5.2 Entitatea: `JurnalPacient`
Clasa: `com.example.pacienti_service.entity.JurnalPacient`
Tabela: `jurnal_pacient` (schema: `pacienti_db`)
| Câmp | Tip | Constrângere | Note |
| --- | --- | --- | --- |
| `id` | `Long` | PK, auto-increment | — |
| `pacientId` | `Long` | NOT NULL | ID-ul intern în DB al pacientului |
| `programareId` | `Long` | NOT NULL | Referință cross-schema către `programari_db.programari.id` |
| `data` | `LocalDate` | NOT NULL | **Data reală** a programării, preluată în mod sincron |
| `nivelDurere` | `Integer` | NOT NULL | Nivelul de durere (pe o scară de la 1 la 10) |
| `dificultateExercitii` | `Integer` | NOT NULL | Dificultatea exercițiilor (1-10) |
| `nivelOboseala` | `Integer` | NOT NULL | Nivelul de oboseală (1-10) |
| `comentarii` | `TEXT` | nullable | Feedback în format text liber |
| `createdAt` | `OffsetDateTime` | NOT NULL | Data și ora creării înregistrării |
| `updatedAt` | `OffsetDateTime` | nullable | Data și ora actualizării înregistrării |
Indecși:
- `idx_jurnal_pacient` pe `(pacient_id)`
- `idx_jurnal_programare` pe `(programare_id)`
- `idx_jurnal_data` pe `(data)`
### 5.3 Fluxul de Creare a Jurnalului
Metoda: `JurnalService.adaugaJurnal(Long pacientId, String pacientKeycloakId, JurnalRequestDTO request)`
Adnotare: `@Transactional`
Crearea unei înregistrări în jurnal implică apeluri între mai multe servicii pentru a garanta consistența datelor, deoarece nu se consideră sigură preluarea datei programării direct de la pacient.
### Pasul 1: Preluarea Sincronă a Datelor (Feign)
```java
ProgramareJurnalDTO detaliiProgramare = programariClient.getDetaliiProgramare(request.programareId());
```
Înainte de a persista jurnalul, `pacienti-service` efectuează un apel sincron Feign GET către `programari-service` (`/programari/{id}/detalii`). Acesta preia data certă a programării (`data`) și valoarea `terapeutKeycloakId`. Data trimisă de pacient în cerere este ignorată în mod deliberat pentru a preveni inconsistențele cronologice în istoricul clinic.
### Pasul 2: Persistența
Entitatea `JurnalPacient` este construită folosind data verificată (`detaliiProgramare.data()`) și este salvată în `jurnalRepository`.
### Pasul 3: Actualizarea Statusului în Sens Invers (Feign)
```java
try {
    programariClient.marcheazaJurnal(request.programareId());
} catch (Exception e) {
    // Înregistrează un avertisment, fără a bloca salvarea jurnalului
}
```
Un apel sincron Feign POST către `programari-service` (`/programari/{id}/mark-jurnal`) updates entitatea `Programare` corespunzătoare, trecând flag-ul `areJurnal` pe `true`. Acest apel este învelit într-un bloc `try-catch` ca mecanism de fallback: dacă `programari-service` este temporar indisponibil, jurnalul este totuși salvat cu succes local în `pacienti_db`.
### Pasul 4: Notificarea Asincronă
Dacă programarea are un terapeut asociat (`terapeutKeycloakId`), se publică un eveniment prin RabbitMQ:
```java
notificarePublisher.jurnalCompletat(
    detaliiProgramare.terapeutKeycloakId(),
    pacientKeycloakId,
    request.programareId()
);
```
Cheia de rutare: `notificare.jurnal.completat`. Aceasta îl informează pe terapeut că pacientul a completat jurnalul, invitându-l să îl examineze înainte de următoarea ședință.
### 5.4 Preluarea Istoricului și Problema Interogărilor N+1 (N+1 Query Problem)
Metoda: `JurnalService.getIstoric(Long pacientId)`
Adnotare: `@Transactional(readOnly = true)`
Pentru a randa istoricul jurnalelor unui pacient, frontend-ul are nevoie de detaliile din jurnal (durere, oboseală) combinate cu detaliile programării (numele terapeutului, locația).
**Abordarea Naivă (Problema N+1):**
Preluarea a 20 de înregistrări din jurnal și iterarea prin acestea pentru a apela `programariClient.getDetaliiProgramare(id)` de 20 de ori ar genera un deșeu masiv de rețea și latențe ridicate.
**Implementarea Optimizată (Gruparea în Loturi - Batching):**
1. Preia toate înregistrările `JurnalPacient` din baza de date locală.
2. Extrage valorile distincte pentru `programareId` într-o lista.
3. Efectuează un **singur apel sincron batch** către `programari-service`:
```java
List<ProgramareJurnalDTO> batchResults = programariClient.getProgramariBatch(programareIds);
```
4. Mapează rezultatele într-o structură `Map<Long, ProgramareJurnalDTO>` grupate după `programareId`.
5. Iterează prin jurnalele locale și îmbogățește-le (enrich) folosind Map-ul încărcat în memorie, cu o performanță de căutare de tip `O(1)`.
Dacă apelul batch eșuează, sistemul revine elegant la returnarea datelor brute din jurnal (raw journal data) fără detaliile îmbogățite despre programări, prioritizând disponibilitatea sistemului în detrimentul randării complete a interfeței grafice.
