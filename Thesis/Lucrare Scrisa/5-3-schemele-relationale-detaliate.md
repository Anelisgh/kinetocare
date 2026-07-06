## 5.3 Schemele relaționale detaliate

Această secțiune detaliază structura fizică a tabelelor, tipurile de date alese și constrângerile de integritate definite pentru fiecare dintre cele șapte micro-baze de date ale platformei KinetoCare.

> *Notă de convenție:* În textul lucrării, din motive de claritate academică, bazele de date sunt denumite cu sufixul `_db` (de exemplu, `user_db`, `terapeuti_db`). În configurațiile fizice ale platformei (`application.properties`), aceste baze de date MySQL sunt denumite în formatul `nume_service` (de exemplu, `user_service`, `terapeuti_service`).

### 5.3.1 `user_db` — gestiunea identității și rolurilor

Baza de date `user_db` stochează atributele generale ale conturilor de utilizatori și coordonează maparea acestora pe rolurile de acces. Identificatorul unic `keycloak_id` reprezintă pivotul prin care profilurile specifice (terapeut, pacient) extrag detaliile de contact.

#### Tabela `users`
- `id` (`bigint`): Cheie primară, *auto-increment*.
- `keycloak_id` (`varchar(36)`): UUID unic furnizat de Keycloak. Indexat unic.
- `email` (`varchar(100)`): Adresă unică de e-mail a utilizatorului. Indexat unic.
- `nume` (`varchar(100)`): Numele de familie.
- `prenume` (`varchar(100)`): Prenumele utilizatorului.
- `telefon` (`varchar(20)`): Număr de telefon.
- `gen` (`enum('FEMININ','MASCULIN')`): Set de valori determinat de cadrul legal-administrativ românesc, în care documentele oficiale de identitate (inclusiv CNP-ul) codifică sexul biologic binar. Limitarea la aceste două valori reflectă constrângerile sistemului de sănătate și ale registraturilor medicale din România.
- `role` (`enum('ADMIN','PACIENT','TERAPEUT')`): Rolul de acces, utilizat în verificările de securitate.
- `active` (`bit(1)`): Indicator de ștergere logică (*soft-delete*).
- **[Metadate audit]** `created_at`, `updated_at`, `created_by`, `last_modified_by` — colectează istoricul operațiunilor de provizionare (detaliat în §4.5.6).

---

### 5.3.2 `terapeuti_db` — resurse clinice și disponibilitate

Găzduiește profilul profesional al terapeuților, orarul de lucru săptămânal alocat fiecărei clinici și cererile de concediu validate.

#### Tabela `terapeuti`
- `id` (`bigint`): Cheie primară, *auto-increment*.
- `keycloak_id` (`varchar(36)`): Cheia logică de legătură cu `user_db.users`. Indexat unic.
- `specializare` (`enum('ADULTI','PEDIATRIE')`): Segmentul de pacienți deservit.
- `poza_profil` (`mediumtext`): Imaginea de profil codificată în format *Base64*. Stocarea în baza de date reprezintă un compromis față de un serviciu de stocare obiect dedicat (S3/MinIO), justificat de simplificarea *deployment*-ului prin eliminarea dependenței de infrastructura externă de stocare. Traiectoria naturală de evoluție a acestei coloane este înlocuirea conținutului *Base64* cu un URL al imaginii găzduite pe un serviciu dedicat.
- `active` (`bit(1)`): Disponibilitatea profilului în căutări.

#### Tabela `locatii`
- `id` (`bigint`): Cheie primară, *auto-increment*.
- `nume` (`varchar(200)`), `adresa` (`varchar(300)`), `oras` (`varchar(100)`), `judet` (`varchar(100)`), `cod_postal` (`varchar(10)`), `telefon` (`varchar(20)`).
- `active` (`bit(1)`): Starea locației (1 = activă, 0 = închisă logic).

#### Tabela `disponibilitate_terapeut`
- `id` (`bigint`): Cheie primară, *auto-increment*.
- `terapeut_id` (`bigint`): Cheie externă fizică către `terapeuti(id)`.
- `locatie_id` (`bigint`): Cheie externă fizică către `locatii(id)`.
- `zi_saptamana` (`int`): Ziua de lucru (1 — Luni, ..., 7 — Duminică).
- `ora_inceput` (`time(6)`), `ora_sfarsit` (`time(6)`): Programul de lucru alocat.
- `active` (`bit(1)`): Permite ștergerea logică a ferestrei orare.

#### Tabela `concediu_terapeut`
- `id` (`bigint`): Cheie primară, *auto-increment*.
- `terapeut_id` (`bigint`): Cheie externă fizică către `terapeuti(id)`.
- `data_inceput` (`date`), `data_sfarsit` (`date`): Intervalul calendaristic de concediu.

---

### 5.3.3 `pacienti_db` — dosarul medical și jurnalul subiectiv

Administrează dosarul pacientului și stările clinice raportate de acesta în jurnalele post-ședință.

#### Tabela `pacienti`
- `id` (`bigint`): Cheie primară, *auto-increment*.
- `keycloak_id` (`varchar(36)`): Cheia logică de legătură cu `user_db.users`. Indexat unic.
- `cnp` (`varchar(13)`): Codul Numeric Personal. Indexat unic.
- `data_nasterii` (`date`): Data nașterii.
- `face_sport` (`enum('DA','NU')`), `detalii_sport` (`varchar(500)`): Date demografice utile terapiei.
- `oras_preferat` (`varchar(100)`), `locatie_preferata_id` (`bigint`): Preferințe de programare.
- `terapeut_keycloak_id` (`varchar(36)`): UUID-ul terapeutului curent. Indexat.
- `active` (`bit(1)`): Indicator de stare dosar.

#### Tabela `jurnal_pacient`
- `id` (`bigint`): Cheie primară, *auto-increment*.
- `pacient_id` (`bigint`): Cheie externă fizică către `pacienti(id)`.
- `programare_id` (`bigint`): Referință logică spre ședința pentru care se completează jurnalul.
- `data` (`date`): Data de referință. Deși derivabilă din programarea referențiată prin `programare_id`, coloana este stocată explicit pentru a asigura autonomia înregistrării în contextul arhitecturii distribuite: jurnalul trebuie să rămână interpretabil fără un apel *cross-service* către `programari-service`.
- `nivel_durere` (`int`): Ratingul durerii resimțite conform scalei NRS (*Numeric Rating Scale*: 1 — durere minimă, 10 — durere maximă).
- `dificultate_exercitii` (`int`), `nivel_oboseala` (`int`): Indici de toleranță la efort (1–10, conform aceleiași convenții NRS).
- `comentarii` (`text`): Observații clinice adăugate de pacient.

---

### 5.3.4 `programari_db` — nucleul tranzacțional clinic

Această schemă constituie nucleul tranzacțional al sistemului. Sunt grupate patru tabele puternic coezive din punct de vedere clinic: `programari`, `evaluari`, `evolutii` și `relatie_pacient_terapeut`.

**Justificarea coeziunii tranzacționale interne:** Deși o arhitectură distribuită pură ar putea izola notele de progres și evaluările în componente separate, aceste entități sunt co-localizate în aceeași schemă fizică pentru a permite tranzacții ACID locale gestionate direct de Spring JPA. Procesul de creare a unei evaluări modifică simultan indicatorul `areEvaluare` din tabela `programari`, înregistrează fișa de evaluare și activează relația pacient-terapeut în tabela `relatie_pacient_terapeut`. Rularea acestui flux sub o singură graniță `@Transactional` locală MySQL elimină necesitatea unor protocoale de coordonare distribuită costisitoare (de tipul *2PC*), asigurând consistența atomică a stării clinice la un cost computațional minim.

#### Tabela `programari`
- `id` (`bigint`): Cheie primară, *auto-increment*.
- `pacient_keycloak_id` (`varchar(36)`), `terapeut_keycloak_id` (`varchar(36)`): Chei externe logice de corelare.
- `locatie_id` (`bigint`), `serviciu_id` (`bigint`): Corelări logice inter-schemă.
- `tip_serviciu` (`varchar(100)`), `pret` (`decimal(10,2)`), `durata_minute` (`int`): Atribute denormalizate conform tiparului *Snapshot*, a cărui justificare arhitecturală este detaliată în secțiunea 4.5.4.
- `data` (`date`), `ora_inceput` (`time(6)`), `ora_sfarsit` (`time(6)`): Coordonatele slotului rezervat.
- `status` (`enum('PROGRAMATA','FINALIZATA','ANULATA')`): Starea rezervării.
- `motiv_anulare` (`enum('ADMINISTRATIV','ANULAT_DE_PACIENT','ANULAT_DE_TERAPEUT','NEPREZENTARE')`).
- `prima_intalnire` (`bit(1)`): Marchează dacă sesiunea reprezintă prima interacțiune clinică a pacientului cu terapeutul curent. Valoarea este calculată la momentul rezervării pe baza istoricului de programări și este utilizată de interfața calendarului pentru a semnala vizual terapeutului că ședința necesită aplicarea protocolului de Evaluare Inițială.
- `are_evaluare` (`bit(1)`), `are_jurnal` (`bit(1)`): Indicatori de urmărire a fluxului de date clinice.

> **Notă:** Implementarea tiparului *Snapshot* pentru atributele preț/durată și mecanismul de audit al entității sunt discutate detaliat în secțiunile 4.5.4 și 4.5.6; structura tabelei reflectă rezultatul aplicării acestor tipare.

#### Tabela `evaluari`
- `id` (`bigint`): Cheie primară, *auto-increment*.
- `pacient_keycloak_id` (`varchar(36)`), `terapeut_keycloak_id` (`varchar(36)`): Legături clinice logice.
- `programare_id` (`bigint`): Cheie externă fizică către `programari(id)`.
- `tip` (`enum('INITIALA','REEVALUARE')`): Tipul fișei.
- `data` (`date`): Data emiterii diagnosticului.
- `diagnostic` (`text`), `observatii` (`text`): Date clinice textuale introduse de specialist.
- `sedinte_recomandate` (`int`): Volumul de ședințe de tratament prescrise.
- `serviciu_recomandat_id` (`bigint`): Identificatorul serviciului din catalog recomandat pentru tratament.

#### Tabela `evolutii`
- `id` (`bigint`): Cheie primară, *auto-increment*.
- `pacient_keycloak_id` (`varchar(36)`), `terapeut_keycloak_id` (`varchar(36)`): Legături logice inter-schemă.
- `observatii` (`text`): Observații clinice în format text liber, structurabile conform convențiilor SOAP.

#### Tabela `relatie_pacient_terapeut`
- `id` (`bigint`): Cheie primară, *auto-increment*.
- `pacient_keycloak_id` (`varchar(36)`), `terapeut_keycloak_id` (`varchar(36)`): Corelarea relației clinice.
- `data_inceput` (`date`), `data_sfarsit` (`date`): Durata temporală a asocierii.
- `activa` (`bit(1)`): Indică dacă relația reprezintă calea terapeutică curentă a pacientului.

---

### 5.3.5 `servicii_db` — catalogul tarifar

Stochează nomenclatorul de servicii medicale oferite de clinică, prețurile și duratele acestora.

#### Tabela `servicii`
- `id` (`bigint`): Cheie primară, *auto-increment*.
- `nume` (`varchar(255)`): Denumirea comercială (de exemplu: Evaluare Inițială, Kinetoterapie Individuală).
- `tip_serviciu_id` (`bigint`): Cheie externă fizică către `tip_serviciu(id)`.
- `pret` (`decimal(10,2)`): Prețul de bază în unitate monetară locală.
- `durata_minute` (`int`): Durata standard a procedurii.
- `active` (`bit(1)`): Permite retragerea serviciului fără ștergere fizică.

#### Tabela `tip_serviciu`
- `id` (`bigint`): Cheie primară, *auto-increment*.
- `nume` (`varchar(100)`): Categoria serviciului (de exemplu: Kinetoterapie, Fizioterapie). Indexat unic.
- `descriere` (`text`).
- `active` (`bit(1)`).

---

### 5.3.6 `chat_db` — mesageria clinică

Găzduiește istoricul conversațiilor în timp real dintre pacienți și terapeuți.

#### Tabela `conversatii`
- `id` (`bigint`): Cheie primară, *auto-increment*.
- `pacient_keycloak_id` (`varchar(36)`), `terapeut_keycloak_id` (`varchar(36)`): Identificatorii participanților. Index compus unic.
- `ultimul_mesaj_la` (`datetime(6)`): *Timestamp*-ul ultimului mesaj expediat.
- `created_at` (`datetime(6)`), `updated_at` (`datetime(6)`).

#### Tabela `mesaje`
- `id` (`bigint`): Cheie primară, *auto-increment*.
- `conversatie_id` (`bigint`): Cheie externă fizică către `conversatii(id)`.
- `expeditor_keycloak_id` (`varchar(36)`): UUID-ul expeditorului.
- `tip_expeditor` (`enum('PACIENT','TERAPEUT')`).
- `continut` (`text`): Textul transmis.
- `este_citit` (`bit(1)`), `citit_la` (`datetime(6)`), `trimis_la` (`datetime(6)`).

---

### 5.3.7 `notificari_db` — centrul de alerte

Gestionează istoricul alertelor interne primite de utilizatori.

#### Tabela `notificari`
- `id` (`bigint`): Cheie primară, *auto-increment*.
- `user_keycloak_id` (`varchar(36)`): Utilizatorul țintă. Indexat.
- `tip_user` (`enum('PACIENT','TERAPEUT')`).
- `tip` (`enum('EVALUARE_INITIALA_NOUA', 'JURNAL_COMPLETAT', 'MESAJ_DE_LA_PACIENT', 'MESAJ_DE_LA_TERAPEUT', 'PROGRAMARE_ANULATA_DE_PACIENT', 'PROGRAMARE_ANULATA_DE_TERAPEUT', 'PROGRAMARE_NOUA', 'REEVALUARE_NECESARA', 'REEVALUARE_RECOMANDATA', 'REMINDER_24H', 'REMINDER_2H', 'REMINDER_JURNAL')`): Tipul notificării, utilizat pentru a diferenția acțiunile asociate fiecărei alerte.
- `titlu` (`varchar(500)`): Textul de titlu pentru notificarea de tip *push*.
- `mesaj` (`text`): Conținutul detaliat al notificării.
- `entitate_legata_id` (`bigint`), `tip_entitate_legata` (`varchar(50)`): Cheie logică polimorfică — permite atașarea notificării de o programare specifică sau de o evaluare, furnizând contextul de acțiune.
- `url_actiune` (`varchar(500)`): Ruta de navigare internă declanșată la interacțiunea cu notificarea în interfață.
- `este_citita` (`bit(1)`), `citita_la` (`datetime(6)`), `created_at` (`datetime(6)`).
