# Structura Fizică a Bazelor de Date (MySQL Database Schema & Data Types)

Această secțiune prezintă structura fizică detaliată a bazelor de date relaționale (MySQL 8.0) pentru fiecare microserviciu din cadrul platformei **KinetoCare**. În conformitate cu modelul de arhitectură bazat pe microservicii, fiecare serviciu își gestionează propria bază de date privată (Database-per-Service), asigurând o decuplare completă, securitate sporită a datelor și conformitate cu reglementările GDPR.

Fiecare tabelă din cadrul serviciilor critice include coloane speciale de auditare (`created_by`, `last_modified_by`, `created_at`, `updated_at`), folosite pentru generarea automată a jurnalelor de auditare (Audit Logs).

---

## 1. 📦 USER_SERVICE Database

Baza de date `user_service` stochează detaliile generale ale conturilor de utilizatori, rolurile acestora în platformă și corelarea cu sistemul de identitate Keycloak.

### Tabelul `users`

| Coloană | Tip de date | Constrângeri / Relații | Descriere |
| :--- | :--- | :--- | :--- |
| `id` | `bigint` | `NOT NULL`, `AUTO_INCREMENT`, `PRIMARY KEY` | Identificator unic intern auto-generat. |
| `active` | `bit(1)` | `NOT NULL` | Starea contului (1 = Activ, 0 = Inactiv/Șters logic). |
| `created_at` | `datetime(6)` | `NOT NULL` | Data și ora creării înregistrării. |
| `email` | `varchar(100)` | `NOT NULL`, `UNIQUE` | Adresa de e-mail a utilizatorului (folosită la login). |
| `gen` | `enum('FEMININ','MASCULIN')` | `NOT NULL` | Genul biologic al utilizatorului. |
| `keycloak_id` | `varchar(36)` | `NOT NULL`, `UNIQUE` | UUID-ul corespunzător utilizatorului din Keycloak. |
| `nume` | `varchar(100)` | `NOT NULL` | Numele de familie. |
| `prenume` | `varchar(100)` | `NOT NULL` | Prenumele utilizatorului. |
| `role` | `enum('ADMIN','PACIENT','TERAPEUT')` | `NOT NULL` | Rolul de securitate și acces în platformă. |
| `telefon` | `varchar(20)` | `NOT NULL` | Numărul de telefon de contact. |
| `updated_at` | `datetime(6)` | `DEFAULT CURRENT_TIMESTAMP(6)` | Data ultimei modificări a înregistrării. |
| `created_by` | `varchar(36)` | `DEFAULT 'SYSTEM'` | Utilizatorul care a creat contul (UUID Keycloak / SYSTEM). |
| `last_modified_by` | `varchar(36)` | `DEFAULT 'SYSTEM'` | Utilizatorul care a efectuat ultima modificare. |

#### Indexuri și Performanță:
* `PRIMARY KEY (id)`
* `UNIQUE KEY uk_users_email (email)` - Căutare rapidă la autentificare.
* `UNIQUE KEY uk_users_keycloak_id (keycloak_id)` - Corelare rapidă cu token-ul JWT.
* `KEY idx_users_role (role)` - Filtrare rapidă a utilizatorilor după rol.
* `KEY idx_users_keycloak_id (keycloak_id)` - Index secundar de căutare.
* `KEY idx_users_email (email)` - Index secundar de căutare.

---

## 2. 📦 TERAPEUTI_SERVICE Database

Gestionează profilurile profesionale ale terapeuților, locațiile clinice, disponibilitatea săptămânală și perioadele de concediu ale personalului medical.

### Tabelul `terapeuti`

| Coloană | Tip de date | Constrângeri / Relații | Descriere |
| :--- | :--- | :--- | :--- |
| `id` | `bigint` | `NOT NULL`, `AUTO_INCREMENT`, `PRIMARY KEY` | Identificator intern auto-generat. |
| `active` | `bit(1)` | `NOT NULL` | Starea profilului (1 = Activ, 0 = Inactiv). |
| `created_at` | `datetime(6)` | `NOT NULL` | Data creării profilului. |
| `keycloak_id` | `varchar(36)` | `NOT NULL`, `UNIQUE` | Corelarea cu utilizatorul din `user_service`. |
| `poza_profil` | `mediumtext` | `DEFAULT NULL` | Fotografie de profil stocată în format Base64. |
| `specializare` | `enum('ADULTI','PEDIATRIE')` | `DEFAULT NULL` | Specializarea principală a terapeutului. |
| `updated_at` | `datetime(6)` | `NOT NULL` | Data ultimei actualizări. |
| `created_by` | `varchar(36)` | `DEFAULT 'SYSTEM'` | Autorul creării înregistrării. |
| `last_modified_by` | `varchar(36)` | `DEFAULT 'SYSTEM'` | Autorul ultimei modificări. |

#### Indexuri:
* `PRIMARY KEY (id)`
* `UNIQUE KEY uk_terapeuti_keycloak_id (keycloak_id)`
* `KEY idx_terapeuti_specializare (specializare)`
* `KEY idx_terapeuti_specializare_active (specializare, active)` - Optimizare pentru listarea terapeuților activi dintr-o anumită specializare.

---

### Tabelul `locatii`

| Coloană | Tip de date | Constrângeri / Relații | Descriere |
| :--- | :--- | :--- | :--- |
| `id` | `bigint` | `NOT NULL`, `AUTO_INCREMENT`, `PRIMARY KEY` | Identificator unic al clinicii/punctului de lucru. |
| `active` | `bit(1)` | `NOT NULL` | Starea locației (1 = Activă, 0 = Închisă logic). |
| `adresa` | `varchar(300)` | `NOT NULL` | Adresa fizică completă. |
| `cod_postal` | `varchar(10)` | `DEFAULT NULL` | Codul poștal al locației. |
| `created_at` | `datetime(6)` | `NOT NULL` | Data creării înregistrării clinicii. |
| `judet` | `varchar(100)` | `NOT NULL` | Județul/Regiunea administrativă. |
| `nume` | `varchar(200)` | `NOT NULL` | Denumirea comercială a clinicii. |
| `oras` | `varchar(100)` | `NOT NULL` | Orașul în care se află. |
| `telefon` | `varchar(20)` | `DEFAULT NULL` | Numărul de telefon specific punctului de lucru. |
| `updated_at` | `datetime(6)` | `NOT NULL` | Data ultimei actualizări. |
| `created_by` | `varchar(36)` | `DEFAULT 'SYSTEM'` | Utilizatorul care a adăugat locația. |
| `last_modified_by` | `varchar(36)` | `DEFAULT 'SYSTEM'` | Utilizatorul care a modificat-o ultima dată. |

#### Indexuri:
* `PRIMARY KEY (id)`
* `KEY idx_locatii_oras (oras)` - Căutare și filtrare rapidă după oraș.
* `KEY idx_locatii_active (active)` - Filtrare pentru locațiile disponibile.

---

### Tabelul `disponibilitate_terapeut`

| Coloană | Tip de date | Constrângeri / Relații | Descriere |
| :--- | :--- | :--- | :--- |
| `id` | `bigint` | `NOT NULL`, `AUTO_INCREMENT`, `PRIMARY KEY` | Identificator unic al programului. |
| `active` | `bit(1)` | `NOT NULL` | Starea disponibilității (1 = Activă, 0 = Ștearsă). |
| `created_at` | `datetime(6)` | `NOT NULL` | Data creării. |
| `locatie_id` | `bigint` | `NOT NULL` | ID-ul locației unde lucrează în acel interval. |
| `ora_inceput` | `time(6)` | `NOT NULL` | Ora la care începe tura (ex. 09:00:00). |
| `ora_sfarsit` | `time(6)` | `NOT NULL` | Ora la care se termină tura (ex. 17:00:00). |
| `terapeut_id` | `bigint` | `NOT NULL` | Referință către tabelul `terapeuti(id)`. |
| `updated_at` | `datetime(6)` | `NOT NULL` | Data ultimei actualizări. |
| `zi_saptamana` | `int` | `NOT NULL` | Ziua din săptămână (1 = Luni, ..., 7 = Duminică). |

#### Indexuri:
* `PRIMARY KEY (id)`
* `KEY idx_disp_terapeut (terapeut_id)`
* `KEY idx_disp_locatie (locatie_id)`

---

### Tabelul `concediu_terapeut`

| Coloană | Tip de date | Constrângeri / Relații | Descriere |
| :--- | :--- | :--- | :--- |
| `id` | `bigint` | `NOT NULL`, `AUTO_INCREMENT`, `PRIMARY KEY` | Identificator unic al cererii de concediu. |
| `created_at` | `datetime(6)` | `NOT NULL` | Data adăugării înregistrării. |
| `data_inceput` | `date` | `NOT NULL` | Data de început a concediului. |
| `data_sfarsit` | `date` | `NOT NULL` | Data de sfârșit a concediului. |
| `terapeut_id` | `bigint` | `NOT NULL` | Referință către tabelul `terapeuti(id)`. |

#### Indexuri:
* `PRIMARY KEY (id)`
* `KEY idx_concediu_terapeut (terapeut_id)`
* `KEY idx_concediu_date (data_inceput, data_sfarsit)` - Optimizare critică pentru validarea suprapunerilor la programări.

---

## 3. 📦 PACIENTI_SERVICE Database

Baza de date `pacienti_service` stochează dosarele medicale generale ale pacienților, preferințele lor clinice și jurnalele zilnice completate de aceștia pentru monitorizarea durerii și evoluției acasă.

### Tabelul `pacienti`

| Coloană | Tip de date | Constrângeri / Relații | Descriere |
| :--- | :--- | :--- | :--- |
| `id` | `bigint` | `NOT NULL`, `AUTO_INCREMENT`, `PRIMARY KEY` | Identificator unic intern. |
| `active` | `bit(1)` | `NOT NULL` | Statusul dosarului (1 = Activ, 0 = Inactiv). |
| `cnp` | `varchar(13)` | `DEFAULT NULL`, `UNIQUE` | Codul Numeric Personal (cifrat/securizat). |
| `created_at` | `datetime(6)` | `NOT NULL` | Data deschiderii dosarului de pacient. |
| `data_nasterii` | `date` | `DEFAULT NULL` | Data nașterii pacientului. |
| `detalii_sport` | `varchar(500)` | `DEFAULT NULL` | Tipul de activități fizice practicate de pacient. |
| `face_sport` | `enum('DA','NU')` | `DEFAULT NULL` | Indică dacă pacientul practică sport regulat. |
| `keycloak_id` | `varchar(36)` | `NOT NULL`, `UNIQUE` | Corelarea cu contul unic din Keycloak. |
| `locatie_preferata_id` | `bigint` | `DEFAULT NULL` | ID-ul clinicii preferate pentru ședințe. |
| `oras_preferat` | `varchar(100)` | `DEFAULT NULL` | Orașul de rezidență preferat pentru tratament. |
| `terapeut_keycloak_id` | `varchar(36)` | `DEFAULT NULL` | UUID Keycloak al terapeutului preferat/alocat. |
| `updated_at` | `datetime(6)` | `NOT NULL` | Data ultimei actualizări. |
| `created_by` | `varchar(36)` | `DEFAULT 'SYSTEM'` | Utilizatorul care a creat profilul. |
| `last_modified_by` | `varchar(36)` | `DEFAULT 'SYSTEM'` | Utilizatorul care a editat dosarul. |

#### Indexuri:
* `PRIMARY KEY (id)`
* `UNIQUE KEY uk_pacienti_keycloak_id (keycloak_id)`
* `UNIQUE KEY uk_pacienti_cnp (cnp)`
* `KEY idx_pacienti_terapeut (terapeut_keycloak_id)` - Corelare rapidă a pacienților unui terapeut.

---

### Tabelul `jurnal_pacient`

| Coloană | Tip de date | Constrângeri / Relații | Descriere |
| :--- | :--- | :--- | :--- |
| `id` | `bigint` | `NOT NULL`, `AUTO_INCREMENT`, `PRIMARY KEY` | Identificator unic al intrării în jurnal. |
| `comentarii` | `text` | `DEFAULT NULL` | Observații libere adăugate de pacient. |
| `created_at` | `datetime(6)` | `NOT NULL` | Data completării efective a jurnalului. |
| `data` | `date` | `NOT NULL` | Data de referință pentru starea raportată. |
| `dificultate_exercitii` | `int` | `NOT NULL` | Scara dificultății (1-5 sau 1-10). |
| `nivel_durere` | `int` | `NOT NULL` | Nivelul de durere resimțit (VAS: 0 - Fără durere, 10 - Durere extremă). |
| `nivel_oboseala` | `int` | `NOT NULL` | Nivelul de oboseală raportat (1-5 sau 1-10). |
| `pacient_id` | `bigint` | `NOT NULL` | ID-ul pacientului din tabelul `pacienti(id)`. |
| `programare_id` | `bigint` | `NOT NULL` | Referință către programarea evaluată. |
| `updated_at` | `datetime(6)` | `DEFAULT NULL` | Data ultimei modificări a jurnalului. |
| `created_by` | `varchar(36)` | `DEFAULT 'SYSTEM'` | Creatorul înregistrării (UUID-ul pacientului). |
| `last_modified_by` | `varchar(36)` | `DEFAULT 'SYSTEM'` | Ultimul utilizator care a modificat jurnalul. |

#### Indexuri:
* `PRIMARY KEY (id)`
* `KEY idx_jurnal_pacient (pacient_id)` - Afișare rapidă a istoricului unui pacient.
* `KEY idx_jurnal_programare (programare_id)` - Corelare directă cu ședința.
* `KEY idx_jurnal_data (data)` - Sortare cronologică a evoluției.

---

## 4. 📦 PROGRAMARI_SERVICE Database

Inima logică a clinicii. Gestionează fluxul de programări, evaluările clinice (inițiale și de reevaluare), evoluția pacienților în timp și relațiile contractuale/terapeutice dintre pacienți și specialiști.

### Tabelul `programari`

| Coloană | Tip de date | Constrângeri / Relații | Descriere |
| :--- | :--- | :--- | :--- |
| `id` | `bigint` | `NOT NULL`, `AUTO_INCREMENT`, `PRIMARY KEY` | Identificator unic al ședinței. |
| `are_evaluare` | `bit(1)` | `NOT NULL` | Indicator dacă s-a completat o fișă de evaluare. |
| `are_jurnal` | `bit(1)` | `NOT NULL` | Indicator dacă s-a completat jurnalul pacientului. |
| `created_at` | `datetime(6)` | `NOT NULL` | Data și ora la care a fost plasată programarea. |
| `data` | `date` | `NOT NULL` | Data fizică a programării la clinică. |
| `durata_minute` | `int` | `NOT NULL` | Durata totală a ședinței (ex: 30, 45, 50, 60 minute). |
| `locatie_id` | `bigint` | `NOT NULL` | ID-ul locației clinicii unde are loc ședința. |
| `motiv_anulare` | `enum(...)` | `DEFAULT NULL` | Motivul anulării (ADMINISTRATIV, PACIENT, TERAPEUT, NEPREZENTARE). |
| `ora_inceput` | `time(6)` | `NOT NULL` | Ora exactă de început (ex: 10:00:00). |
| `ora_sfarsit` | `time(6)` | `NOT NULL` | Ora exactă de sfârșit (ex: 10:50:00). |
| `pacient_keycloak_id` | `varchar(36)` | `NOT NULL` | UUID Keycloak al pacientului programat. |
| `pret` | `decimal(10,2)` | `NOT NULL` | Tariful perceput pentru ședință (ex: 150.00). |
| `prima_intalnire` | `bit(1)` | `DEFAULT NULL` | Flag ce semnalează dacă este o primă evaluare. |
| `serviciu_id` | `bigint` | `NOT NULL` | Referință la ID-ul serviciului prestat. |
| `status` | `enum(...)` | `NOT NULL` | Statusul curent al programării (PROGRAMATA, FINALIZATA, ANULATA). |
| `terapeut_keycloak_id` | `varchar(36)` | `NOT NULL` | UUID Keycloak al terapeutului desemnat. |
| `tip_serviciu` | `varchar(100)` | `NOT NULL` | Denumirea simplificată a serviciului prestat. |
| `updated_at` | `datetime(6)` | `NOT NULL` | Data ultimei modificări de stare a ședinței. |
| `created_by` | `varchar(36)` | `DEFAULT 'SYSTEM'` | UUID-ul utilizatorului care a programat. |
| `last_modified_by` | `varchar(36)` | `DEFAULT 'SYSTEM'` | UUID-ul ultimului utilizator care a modificat starea. |

#### Indexuri și Optimizări Critice:
* `PRIMARY KEY (id)`
* `KEY idx_prog_terapeut_data_status (terapeut_keycloak_id, data, status)` - Folosit intensiv la căutarea intervalelor libere și a agendei zilnice a terapeutului.
* `KEY idx_prog_pacient_data (pacient_keycloak_id, data)` - Istoricul de programări al pacientului ordonat cronologic.
* `KEY idx_prog_status_data (status, data)` - Rapoarte statistice de prezență.
* `KEY idx_prog_pacient_status_data (pacient_keycloak_id, status, data, ora_inceput)` - Găsirea viitoarelor programări active ale unui pacient.
* `KEY idx_prog_stats (locatie_id, data, pret)` - Rapoarte financiare și de grad de ocupare pe clinici.
* `KEY idx_prog_overlap (terapeut_keycloak_id, data, ora_inceput, ora_sfarsit)` - Previne dubla rezervare (double-booking) la nivel de bază de date.

---

### Tabelul `evaluari`

Stochează fișele medicale detaliate introduse de terapeuți în urma evaluărilor clinice. Contravine ștergerilor directe pentru a respecta trasabilitatea medicală conform normelor GDPR.

| Coloană | Tip de date | Constrângeri / Relații | Descriere |
| :--- | :--- | :--- | :--- |
| `id` | `bigint` | `NOT NULL`, `AUTO_INCREMENT`, `PRIMARY KEY` | Identificator unic al fișei de evaluare. |
| `created_at` | `datetime(6)` | `NOT NULL` | Data creării fișei de evaluare. |
| `data` | `date` | `NOT NULL` | Data la care s-a efectuat evaluarea fizică. |
| `diagnostic` | `text` | `NOT NULL` | Diagnosticul medical stabilit de terapeut în urma testelor. |
| `observatii` | `text` | `DEFAULT NULL` | Recomandări posturale, limite fizice observate, etc. |
| `pacient_keycloak_id` | `varchar(36)` | `NOT NULL` | Corelarea cu dosarul pacientului evaluator. |
| `programare_id` | `bigint` | `DEFAULT NULL` | Programarea de origine din care a fost generată evaluarea. |
| `sedinte_recomandate` | `int` | `DEFAULT NULL` | Numărul optim de ședințe estimate pentru recuperare. |
| `serviciu_recomandat_id` | `bigint` | `DEFAULT NULL` | Serviciul recomandat în continuare (kineto, fizio, etc.). |
| `terapeut_keycloak_id` | `varchar(36)` | `NOT NULL` | Terapeutul care a efectuat evaluarea. |
| `tip` | `enum('INITIALA','REEVALUARE')` | `NOT NULL` | Tipul evaluării efectuate. |
| `updated_at` | `datetime(6)` | `NOT NULL` | Data ultimei modificări (de regulă restricționată). |
| `created_by` | `varchar(36)` | `DEFAULT 'SYSTEM'` | UUID-ul terapeutului creator. |
| `last_modified_by` | `varchar(36)` | `DEFAULT 'SYSTEM'` | UUID-ul utilizatorului care a modificat fișa. |

#### Indexuri:
* `PRIMARY KEY (id)`
* `KEY idx_eval_pacient (pacient_keycloak_id)`
* `KEY idx_eval_terapeut (terapeut_keycloak_id)`
* `KEY idx_eval_programare (programare_id)`
* `KEY idx_eval_tip (tip)`
* `KEY idx_eval_pacient_data (pacient_keycloak_id, data)` - Găsirea ultimei stări clinice cunoscute a pacientului.

---

### Tabelul `evolutii`

Jurnal de evoluție pe termen lung, completat exclusiv de specialiști, unde se consemnează progresul motor și toleranța la efort.

| Coloană | Tip de date | Constrângeri / Relații | Descriere |
| :--- | :--- | :--- | :--- |
| `id` | `bigint` | `NOT NULL`, `AUTO_INCREMENT`, `PRIMARY KEY` | Identificator unic al notei de evoluție. |
| `created_at` | `datetime(6)` | `NOT NULL` | Data înregistrării notei. |
| `observatii` | `text` | `NOT NULL` | Conținutul notei clinice de progres. |
| `pacient_keycloak_id` | `varchar(36)` | `NOT NULL` | Pacientul monitorizat. |
| `terapeut_keycloak_id` | `varchar(36)` | `NOT NULL` | Terapeutul semnatar. |
| `updated_at` | `datetime(6)` | `DEFAULT NULL` | Data modificării notei. |
| `created_by` | `varchar(36)` | `DEFAULT 'SYSTEM'` | Terapeutul care a documentat evoluția. |
| `last_modified_by` | `varchar(36)` | `DEFAULT 'SYSTEM'` | Modificatorul notei. |

#### Indexuri:
* `PRIMARY KEY (id)`
* `KEY idx_evolutie_pacient (pacient_keycloak_id)`
* `KEY idx_evolutie_terapeut (terapeut_keycloak_id)`
* `KEY idx_evolutie_pacient_terapeut (pacient_keycloak_id, terapeut_keycloak_id, created_at)` - Filtrare rapidă a notelor de evoluție dintr-o relație terapeutică directă.

---

### Tabelul `relatie_pacient_terapeut`

Modelul contractual / legătura activă dintre un pacient și un terapeut coordonator.

| Coloană | Tip de date | Constrângeri / Relații | Descriere |
| :--- | :--- | :--- | :--- |
| `id` | `bigint` | `NOT NULL`, `AUTO_INCREMENT`, `PRIMARY KEY` | Identificator unic al relației. |
| `activa` | `bit(1)` | `NOT NULL` | Indică dacă relația terapeutică este activă (1 = Da, 0 = Încheiată). |
| `created_at` | `datetime(6)` | `NOT NULL` | Data asocierii. |
| `data_inceput` | `date` | `NOT NULL` | Data de la care a început colaborarea clinică. |
| `data_sfarsit` | `date` | `DEFAULT NULL` | Data încheierii colaborării terapeutice. |
| `pacient_keycloak_id` | `varchar(36)` | `NOT NULL` | Pacientul. |
| `terapeut_keycloak_id` | `varchar(36)` | `NOT NULL` | Terapeutul alocat. |
| `updated_at` | `datetime(6)` | `NOT NULL` | Data modificării stării. |
| `created_by` | `varchar(36)` | `DEFAULT 'SYSTEM'` | Utilizatorul care a inițiat asocierea. |
| `last_modified_by` | `varchar(36)` | `DEFAULT 'SYSTEM'` | Utilizatorul care a modificat asocierea. |

#### Indexuri:
* `PRIMARY KEY (id)`
* `KEY idx_rel_pacient (pacient_keycloak_id)`
* `KEY idx_rel_terapeut (terapeut_keycloak_id)`
* `KEY idx_rel_activa (activa)`
* `KEY idx_rel_pacient_activa (pacient_keycloak_id, activa)` - Verificarea instantă dacă un pacient are deja un terapeut alocat activ.
* `KEY idx_rel_terapeut_activa (terapeut_keycloak_id, activa)` - Listarea tuturor pacienților activi monitorizați de un terapeut.

---

## 5. 📦 SERVICII_SERVICE Database

Gestionează catalogul de prețuri, durate și tipuri de terapii oferite de platforma clinicii kinetice.

### Tabelul `servicii`

| Coloană | Tip de date | Constrângeri / Relații | Descriere |
| :--- | :--- | :--- | :--- |
| `id` | `bigint` | `NOT NULL`, `AUTO_INCREMENT`, `PRIMARY KEY` | Identificator unic al serviciului. |
| `active` | `bit(1)` | `NOT NULL` | Disponibilitatea serviciului (1 = Activ, 0 = Retras din ofertă). |
| `created_at` | `datetime(6)` | `NOT NULL` | Data introducerii în catalog. |
| `durata_minute` | `int` | `NOT NULL` | Durata standard a terapiei. |
| `nume` | `varchar(255)` | `DEFAULT NULL` | Denumirea serviciului (ex: Kinetoterapie Individuală). |
| `pret` | `decimal(10,2)` | `NOT NULL` | Tariful standard în RON. |
| `updated_at` | `datetime(6)` | `NOT NULL` | Data ultimei revizuiri a serviciului. |
| `tip_serviciu_id` | `bigint` | `NOT NULL`, `FOREIGN KEY` | Referință la categoria din tabelul `tip_serviciu(id)`. |
| `created_by` | `varchar(36)` | `DEFAULT 'SYSTEM'` | Administratorul creator. |
| `last_modified_by` | `varchar(36)` | `DEFAULT 'SYSTEM'` | Administratorul care a editat serviciul. |

#### Indexuri și Constrângeri:
* `PRIMARY KEY (id)`
* `KEY idx_servicii_tip (tip_serviciu_id)`
* `KEY idx_serviciu_nume (nume)`
* `CONSTRAINT FK_servicii_tip FOREIGN KEY (tip_serviciu_id) REFERENCES tip_serviciu (id)` - Integritate referențială rigidă la nivel de bază de date.

---

### Tabelul `tip_serviciu`

Categoria macro a serviciilor (ex: Kinetoterapie, Fizioterapie, Masaj, Evaluare).

| Coloană | Tip de date | Constrângeri / Relații | Descriere |
| :--- | :--- | :--- | :--- |
| `id` | `bigint` | `NOT NULL`, `AUTO_INCREMENT`, `PRIMARY KEY` | Identificator unic al categoriei. |
| `active` | `bit(1)` | `NOT NULL` | Starea categoriei (1 = Activă, 0 = Inactivă). |
| `created_at` | `datetime(6)` | `NOT NULL` | Data creării. |
| `descriere` | `text` | `DEFAULT NULL` | Prezentarea generală a categoriei de servicii. |
| `nume` | `varchar(100)` | `NOT NULL`, `UNIQUE` | Numele categoriei (ex: Fizioterapie). |
| `updated_at` | `datetime(6)` | `NOT NULL` | Data actualizării categoriei. |
| `created_by` | `varchar(36)` | `DEFAULT 'SYSTEM'` | Creatorul. |
| `last_modified_by` | `varchar(36)` | `DEFAULT 'SYSTEM'` | Cel care a modificat. |

#### Indexuri:
* `PRIMARY KEY (id)`
* `UNIQUE KEY uk_tip_serviciu_nume (nume)`
* `KEY idx_tip_serviciu_nume (nume)`

---

## 6. 📦 CHAT_SERVICE Database

Gestionează canalul de comunicare instant securizat dintre pacienți și terapeuții lor coordonatori. Datele cu caracter personal (mesajele) sunt izolate în această bază de date dedicată.

### Tabelul `conversatii`

| Coloană | Tip de date | Constrângeri / Relații | Descriere |
| :--- | :--- | :--- | :--- |
| `id` | `bigint` | `NOT NULL`, `AUTO_INCREMENT`, `PRIMARY KEY` | Identificator unic al canalului de chat. |
| `created_at` | `datetime(6)` | `NOT NULL` | Data inițierii chat-ului. |
| `pacient_keycloak_id` | `varchar(36)` | `NOT NULL` | UUID Keycloak al pacientului participant. |
| `terapeut_keycloak_id` | `varchar(36)` | `NOT NULL` | UUID Keycloak al terapeutului participant. |
| `ultimul_mesaj_la` | `datetime(6)` | `DEFAULT NULL` | Timestamp-ul ultimului mesaj trimis (pentru sortare rapidă în inbox). |
| `updated_at` | `datetime(6)` | `NOT NULL` | Data actualizării canalului. |

#### Indexuri:
* `PRIMARY KEY (id)`
* `KEY idx_conv_pacient (pacient_keycloak_id)`
* `KEY idx_conv_terapeut (terapeut_keycloak_id)`
* `KEY idx_conv_participants (pacient_keycloak_id, terapeut_keycloak_id)` - Căutare instanță la selectarea chat-ului dintre un pacient și terapeut.
* `KEY idx_conv_ultim_mesaj (ultimul_mesaj_la)` - Sortarea inbox-ului de la cele mai recente conversații în jos.

---

### Tabelul `mesaje`

| Coloană | Tip de date | Constrângeri / Relații | Descriere |
| :--- | :--- | :--- | :--- |
| `id` | `bigint` | `NOT NULL`, `AUTO_INCREMENT`, `PRIMARY KEY` | Identificator unic al mesajului. |
| `citit_la` | `datetime(6)` | `DEFAULT NULL` | Data și ora la care destinatarul a deschis mesajul. |
| `continut` | `text` | `NOT NULL` | Textul propriu-zis al mesajului. |
| `conversatie_id` | `bigint` | `NOT NULL` | Referință la ID-ul conversației mamă. |
| `este_citit` | `bit(1)` | `NOT NULL` | Flag de confirmare a citirii (1 = Citit, 0 = Necitit). |
| `expeditor_keycloak_id` | `varchar(36)` | `NOT NULL` | UUID Keycloak al expeditorului. |
| `tip_expeditor` | `enum('PACIENT','TERAPEUT')` | `NOT NULL` | Tipul expeditorului (pentru direcționarea vizuală în UI). |
| `trimis_la` | `datetime(6)` | `NOT NULL` | Timestamp-ul trimiterii. |

#### Indexuri:
* `PRIMARY KEY (id)`
* `KEY idx_mesaj_conversatie (conversatie_id)`
* `KEY idx_mesaj_expeditor (expeditor_keycloak_id)`
* `KEY idx_mesaj_trimis_la (trimis_la)`
* `KEY idx_mesaj_conv_list (conversatie_id, trimis_la)` - Optimizare critică pentru încărcarea paginată a istoricului de mesaje în UI.

---

## 7. 📦 NOTIFICARI_SERVICE Database

Gestionează stările notificărilor de tip In-App generate automat de server sau la declanșarea timerelor cron (reminder-e de ședințe, evaluări recomandate, completări de jurnale).

### Tabelul `notificari`

| Coloană | Tip de date | Constrângeri / Relații | Descriere |
| :--- | :--- | :--- | :--- |
| `id` | `bigint` | `NOT NULL`, `AUTO_INCREMENT`, `PRIMARY KEY` | Identificator unic al notificării. |
| `citita_la` | `datetime(6)` | `DEFAULT NULL` | Data confirmării vizualizării notificării în UI. |
| `created_at` | `datetime(6)` | `NOT NULL` | Data generării notificării. |
| `entitate_legata_id` | `bigint` | `DEFAULT NULL` | ID-ul resursei legate (ex: ID-ul programării legate). |
| `este_citita` | `bit(1)` | `NOT NULL` | Flag de stare (1 = Citită, 0 = Nouă/Necitită). |
| `mesaj` | `text` | `NOT NULL` | Conținutul vizual detaliat al notificării. |
| `tip` | `enum(...)` | `NOT NULL` | Categoria notificării (ex: `REMINDER_24H`, `PROGRAMARE_NOUA`). |
| `tip_entitate_legata` | `varchar(50)` | `DEFAULT NULL` | Clasa entității legate (ex: `Programare`, `Mesaj`). |
| `tip_user` | `enum('PACIENT','TERAPEUT')` | `NOT NULL` | Destinația contului (dacă e pacient sau terapeut). |
| `titlu` | `varchar(500)` | `NOT NULL` | Titlul scurt al notificării (pentru Push Notifications / UI). |
| `url_actiune` | `varchar(500)` | `DEFAULT NULL` | Ruta de navigare internă la click (ex: `/appointments/12`). |
| `user_keycloak_id` | `varchar(36)` | `NOT NULL` | UUID Keycloak al utilizatorului destinatar. |

#### Indexuri:
* `PRIMARY KEY (id)`
* `KEY idx_notif_user_kc (user_keycloak_id)`
* `KEY idx_notif_citita (este_citita)`
* `KEY idx_notif_creata (created_at)`
* `KEY idx_notif_user_query (user_keycloak_id, tip_user, este_citita, created_at)` - Optimizare ideală pentru interogarea notificărilor necitite în bara de navigare a aplicației pentru un utilizator specific.
