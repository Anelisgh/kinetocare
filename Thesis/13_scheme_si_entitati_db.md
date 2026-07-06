## Secțiunea 13: Schemele și Entitățile Bazelor de Date (Database Schema & Entities)

Această secțiune detaliază structura exactă a entităților din baza de date pentru fiecare microserviciu, incluzând cheile primare, tipurile de date, constrângerile, relațiile stabilite prin enumerări și metadatele de auditare adăugate recent pentru conformitate deplină cu cerințele GDPR (jurnale de auditare).

Entitățile clinice și cele ce conțin date cu caracter personal moștenesc acum o structură de bază comună de auditare (`BaseAuditableEntity`), adăugând atributele:
* `createdBy` (String, 36 caractere) - identificatorul utilizatorului/sistemului care a creat înregistrarea.
* `lastModifiedBy` (String, 36 caractere) - identificatorul utilizatorului/sistemului care a modificat ultima dată înregistrarea.
* `createdAt` (OffsetDateTime) - amprenta temporală a creării.
* `updatedAt` (OffsetDateTime) - amprenta temporală a ultimei actualizări.

---

### 📦 USER SERVICE

#### 🟢 User
* `id` (Long, PK, auto-increment)
* `keycloakId` (String, unique, 36 chars, not null)
* `email` (String, unique, 100 chars, not null)
* `nume` (String, 100 chars, not null)
* `prenume` (String, 100 chars, not null)
* `telefon` (String, 20 chars, not null)
* `gen` (Gen enum, not null)
* `role` (UserRole enum, not null)
* `active` (Boolean, default true, not null)
* **[AUDIT]** `createdBy` (String, 36 chars)
* **[AUDIT]** `lastModifiedBy` (String, 36 chars)
* **[AUDIT]** `createdAt` (OffsetDateTime, not null)
* **[AUDIT]** `updatedAt` (OffsetDateTime, not null)

* **UserRole enum**: `ADMIN`, `TERAPEUT`, `PACIENT`
* **Gen enum**: `MASCULIN`, `FEMININ`

---

### 📦 TERAPEUTI SERVICE

#### 🟢 Terapeut
* `id` (Long, PK, auto-increment)
* `keycloakId` (String, unique, 36 chars, not null)
* `specializare` (Specializare enum)
* `pozaProfil` (MEDIUMTEXT, Base64)
* `active` (Boolean, default true, not null)
* **[AUDIT]** `createdBy` (String, 36 chars)
* **[AUDIT]** `lastModifiedBy` (String, 36 chars)
* **[AUDIT]** `createdAt` (OffsetDateTime, not null)
* **[AUDIT]** `updatedAt` (OffsetDateTime, not null)

* **Specializare enum**: `ADULTI`, `PEDIATRIE`

#### 🟢 Locatie
* `id` (Long, PK, auto-increment)
* `nume` (String, 200 chars, not null)
* `adresa` (String, 300 chars, not null)
* `oras` (String, 100 chars, not null)
* `judet` (String, 100 chars, not null)
* `codPostal` (String, 10 chars)
* `telefon` (String, 20 chars)
* `active` (Boolean, default true, not null)
* **[AUDIT]** `createdBy` (String, 36 chars)
* **[AUDIT]** `lastModifiedBy` (String, 36 chars)
* **[AUDIT]** `createdAt` (OffsetDateTime, not null)
* **[AUDIT]** `updatedAt` (OffsetDateTime, not null)

#### 🟡 DisponibilitateTerapeut (Fără audit GDPR extins)
* `id` (Long, PK, auto-increment)
* `terapeutId` (Long, not null)
* `ziSaptamana` (Integer, not null)
* `locatieId` (Long, not null)
* `oraInceput` (LocalTime, not null)
* `oraSfarsit` (LocalTime, not null)
* `active` (Boolean, default true, not null)
* `createdAt` (OffsetDateTime, not null)
* `updatedAt` (OffsetDateTime, not null)

#### 🟡 ConcediuTerapeut (Fără audit GDPR extins)
* `id` (Long, PK, auto-increment)
* `terapeutId` (Long, not null)
* `dataInceput` (LocalDate, not null)
* `dataSfarsit` (LocalDate, not null)
* `createdAt` (OffsetDateTime, not null)

---

### 📦 PACIENTI SERVICE

#### 🟢 Pacient
* `id` (Long, PK, auto-increment)
* `keycloakId` (String, unique, 36 chars, not null)
* `dataNasterii` (LocalDate)
* `cnp` (String, unique, 13 chars)
* `faceSport` (FaceSport enum)
* `detaliiSport` (String, 500 chars)
* `orasPreferat` (String, 100 chars)
* `locatiePreferataId` (Long)
* `terapeutKeycloakId` (String, 36 chars)
* `active` (Boolean, default true, not null)
* **[AUDIT]** `createdBy` (String, 36 chars)
* **[AUDIT]** `lastModifiedBy` (String, 36 chars)
* **[AUDIT]** `createdAt` (OffsetDateTime, not null)
* **[AUDIT]** `updatedAt` (OffsetDateTime, not null)

* **FaceSport enum**: `DA`, `NU`

#### 🟢 JurnalPacient
* `id` (Long, PK, auto-increment)
* `pacientId` (Long, not null)
* `programareId` (Long, not null)
* `data` (LocalDate, not null)
* `nivelDurere` (Integer, not null)
* `dificultateExercitii` (Integer, not null)
* `nivelOboseala` (Integer, not null)
* `comentarii` (TEXT)
* **[AUDIT]** `createdBy` (String, 36 chars)
* **[AUDIT]** `lastModifiedBy` (String, 36 chars)
* **[AUDIT]** `createdAt` (OffsetDateTime, not null)
* **[AUDIT]** `updatedAt` (OffsetDateTime)

---

### 📦 PROGRAMARI SERVICE

#### 🟢 Programare
* `id` (Long, PK, auto-increment)
* `pacientKeycloakId` (String, 36 chars, not null)
* `terapeutKeycloakId` (String, 36 chars, not null)
* `locatieId` (Long, not null)
* `serviciuId` (Long, not null)
* `tipServiciu` (String, 100 chars, not null)
* `pret` (BigDecimal, not null)
* `durataMinute` (Integer, not null)
* `primaIntalnire` (Boolean)
* `data` (LocalDate, not null)
* `oraInceput` (LocalTime, not null)
* `oraSfarsit` (LocalTime, not null)
* `status` (StatusProgramare enum, not null)
* `motivAnulare` (MotivAnulare enum)
* `areEvaluare` (Boolean, default false, not null)
* `areJurnal` (Boolean, default false, not null)
* **[AUDIT]** `createdBy` (String, 36 chars)
* **[AUDIT]** `lastModifiedBy` (String, 36 chars)
* **[AUDIT]** `createdAt` (OffsetDateTime, not null)
* **[AUDIT]** `updatedAt` (OffsetDateTime, not null)

* **StatusProgramare enum**: `PROGRAMATA`, `FINALIZATA`, `ANULATA`
* **MotivAnulare enum**: `ANULAT_DE_PACIENT`, `ANULAT_DE_TERAPEUT`, `NEPREZENTARE`, `ADMINISTRATIV`

#### 🟢 Evaluare
* `id` (Long, PK, auto-increment)
* `pacientKeycloakId` (String, 36 chars, not null)
* `terapeutKeycloakId` (String, 36 chars, not null)
* `programareId` (Long)
* `tip` (TipEvaluare enum, not null)
* `data` (LocalDate, not null)
* `diagnostic` (TEXT, not null)
* `sedinteRecomandate` (Integer)
* `serviciuRecomandatId` (Long)
* `observatii` (TEXT)
* **[AUDIT]** `createdBy` (String, 36 chars)
* **[AUDIT]** `lastModifiedBy` (String, 36 chars)
* **[AUDIT]** `createdAt` (OffsetDateTime, not null)
* **[AUDIT]** `updatedAt` (OffsetDateTime, not null)

* **TipEvaluare enum**: `INITIALA`, `REEVALUARE`

#### 🟢 RelatiePacientTerapeut
* `id` (Long, PK, auto-increment)
* `pacientKeycloakId` (String, 36 chars, not null)
* `terapeutKeycloakId` (String, 36 chars, not null)
* `dataInceput` (LocalDate, not null)
* `dataSfarsit` (LocalDate)
* `activa` (Boolean, default true, not null)
* **[AUDIT]** `createdBy` (String, 36 chars)
* **[AUDIT]** `lastModifiedBy` (String, 36 chars)
* **[AUDIT]** `createdAt` (OffsetDateTime, not null)
* **[AUDIT]** `updatedAt` (OffsetDateTime, not null)

#### 🟢 Evolutie
* `id` (Long, PK, auto-increment)
* `pacientKeycloakId` (String, 36 chars, not null)
* `terapeutKeycloakId` (String, 36 chars, not null)
* `observatii` (TEXT, not null)
* **[AUDIT]** `createdBy` (String, 36 chars)
* **[AUDIT]** `lastModifiedBy` (String, 36 chars)
* **[AUDIT]** `createdAt` (OffsetDateTime, not null)
* **[AUDIT]** `updatedAt` (OffsetDateTime, not null)

---

### 📦 SERVICII SERVICE

#### 🟢 Serviciu
* `id` (Long, PK, auto-increment)
* `nume` (String, not null)
* `tipServiciuId` (Long, not null)
* `pret` (BigDecimal, not null)
* `durataMinute` (Integer, not null)
* `active` (Boolean, default true, not null)
* **[AUDIT]** `createdBy` (String, 36 chars)
* **[AUDIT]** `lastModifiedBy` (String, 36 chars)
* **[AUDIT]** `createdAt` (OffsetDateTime, not null)
* **[AUDIT]** `updatedAt` (OffsetDateTime, not null)

#### 🟢 TipServiciu
* `id` (Long, PK, auto-increment)
* `nume` (String, unique, not null)
* `descriere` (TEXT)
* `active` (Boolean, default true, not null)
* **[AUDIT]** `createdBy` (String, 36 chars)
* **[AUDIT]** `lastModifiedBy` (String, 36 chars)
* **[AUDIT]** `createdAt` (OffsetDateTime, not null)
* **[AUDIT]** `updatedAt` (OffsetDateTime, not null)

---

### 📦 CHAT SERVICE (Fără audit GDPR extins)

#### 🟡 Conversatie
* `id` (Long, PK, auto-increment)
* `pacientKeycloakId` (String, 36 chars, not null)
* `terapeutKeycloakId` (String, 36 chars, not null)
* `ultimulMesajLa` (OffsetDateTime)
* `createdAt` (OffsetDateTime, not null)
* `updatedAt` (OffsetDateTime, not null)

#### 🟡 Mesaj
* `id` (Long, PK, auto-increment)
* `conversatieId` (Long, not null)
* `expeditorKeycloakId` (String, 36 chars, not null)
* `tipExpeditor` (TipExpeditor enum, not null)
* `continut` (TEXT, not null)
* `esteCitit` (Boolean, default false, not null)
* `cititLa` (OffsetDateTime)
* `trimisLa` (OffsetDateTime, not null)

* **TipExpeditor enum**: `PACIENT`, `TERAPEUT`

---

### 📦 NOTIFICARI SERVICE (Fără audit GDPR extins)

#### 🟡 Notificare
* `id` (Long, PK, auto-increment)
* `userKeycloakId` (String, 36 chars, not null)
* `tipUser` (TipUser enum, not null)
* `tip` (TipNotificare enum, not null)
* `titlu` (String, 500 chars, not null)
* `mesaj` (TEXT, not null)
* `entitateLegataId` (Long)
* `tipEntitateLegata` (String, 50 chars)
* `urlActiune` (String, 500 chars)
* `esteCitita` (Boolean, default false, not null)
* `cititaLa` (OffsetDateTime)
* `createdAt` (OffsetDateTime, not null)

* **TipUser enum**: `PACIENT`, `TERAPEUT`
* **TipNotificare enum**: `PROGRAMARE_ANULATA_DE_TERAPEUT`, `REMINDER_24H`, `REMINDER_2H`, `MESAJ_DE_LA_TERAPEUT`, `REMINDER_JURNAL`, `REEVALUARE_RECOMANDATA`, `PROGRAMARE_NOUA`, `EVALUARE_INITIALA_NOUA`, `PROGRAMARE_ANULATA_DE_PACIENT`, `JURNAL_COMPLETAT`, `MESAJ_DE_LA_PACIENT`, `REEVALUARE_NECESARA`

