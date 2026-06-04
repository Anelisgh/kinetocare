Functionalitati
## Lista Funcționalități - Aplicație Kinetoterapie
	- Rol de "Admin" care:
		- Gestioneaza serviciile
		- Gestionează lista de locații (CRUD)
		- Poate vedea statistici generale
		- Poate dezactiva conturi (soft delete)
	1. Când pornești aplicația prima dată, existe un script
care creează automat un user Admin cu credențiale hardcodate:
		Email: admin@kinetoterapie.ro
Parolă: Admin123! 
	2. Adminul adaugă locații, servicii.
	Dashboard Admin:
	📍 LOCAȚII
	- Adaugă locație nouă (nume, adresă, oraș, județ)
	- Editează locații existente
	- Dezactivează locații
	💼 SERVICII
	- Adaugă serviciu nou (tip, preț, durată)
	- Editează prețuri
	- Dezactivează servicii
	👥 UTILIZATORI
	- Vede lista terapeuți/pacienți
	- Poate dezactiva conturi
	- Vede statistici
	📊 STATISTICI
	- Programări pe lună per locație
	- Venituri totale per locație
	- Terapeuți activi per locație
	- Rata de anulări per locație
	- Pacienți noi pe lună per locație
	- Programări per terapeut (luna curentă)
	### 🔐 AUTENTIFICARE & AUTORIZARE
	#### Înregistrare & Login - KeyCloak
	- [ ] Înregistrare utilizator cu alegerea rolului (Pacient/Terapeut)
	- [ ] Input-uri specifice pe rol la înregistrare
	- [ ] Login cu email/parolă
	- [ ] Logout și gestionare sesiune
	#### Gestionare Conturi
	- [ ] Profil utilizator (editare date personale)
	- [ ] Schimbare parolă
	### 👨‍⚕️ FUNCȚIONALITĂȚI TERAPEUT
	#### Dashboard Terapeut
	- [ ] Programări calendar
	- [ ] Lista pacienți activi
	- [ ] Statistici rapide (număr pacienți, ședințe luna aceasta) ← neimplementat
	#### Gestionare Pacienți
	- [ ] Lista tuturor pacienților (cei cu evaluări)
	- [ ] Căutare și filtrare pacienți
	- [ ] Vizualizare detalii pacient (date personale, istoric)
	- [ ] Adăugare automată în listă la prima evaluare
	- [ ] Vizualizare evoluție durerii în timp (grafice → in functie de jurnalul adaugat de pacient)
	#### Calendar & Programări
	- [ ] Calendar interactiv cu programări (FullCalendar pentru React sau React-Calendar)
	- [ ] Vizualizare programări (toate statusurile)
	- [ ] Modificare status programare: Programată → Anulată/Finalizată
	- [ ] Finalizare automată după trecerea datei
	- [ ] Adăugarea concediului - zile blocate 
	#### Evaluări & Re-evaluări
	- [ ] Adăugare evaluare inițială pentru pacient nou
	- [ ] Adăugare re-evaluări periodice
	- [ ] Template-uri pentru evaluări (formulare structurate)
	- [ ] Istoricul evaluărilor per pacient
	#### Evoluții & Notițe
	- [ ] Adăugare evoluții după ședințe
	- [ ] Editare/ștergere evoluții proprii
	### 🤕 FUNCȚIONALITĂȚI PACIENT
	#### Dashboard Pacient
	- [ ] Următoarea programare (data, ora, terapeut)
	- [ ] Mesaje noi de la terapeut
	- [ ] Daca nu are diagnostic (caci inca nu are o evaluare initiala) → ⏳ În așteptarea evaluării inițiale
	- [ ] 📊 Progres tratament: ████████░░ 60% (3/5 ședințe) (in loc de sedinte pana la reevaluare)
	#### Programări
	- [ ] Adăugare programare nouă (calendar picker)
	- [ ] Editare programări existente (doar cele viitoare)
	- [ ] Anulare programări
	- [ ] Verificare disponibilitate terapeut în timp real
	- [ ] Mesaj eroare dacă terapeutul nu e disponibil
	#### Istoricul Ședințelor
	- [ ] Lista completă ședințe (finalizate, programate, anulate)
	- [ ] Detalii per ședință: dată, tip, preț
	- [ ] Calculatoare: total cheltuit pe perioade
	#### Jurnalul Personal
	- [ ] Adăugare jurnal după fiecare ședință
		- [ ] Evaluare nivel durere (slider 1-10)
		- [ ] Nivelul de dificultate al exercițiilor
		- [ ] Starea de oboseală/energie
		- [ ] Comentarii libere despre cum s-a simțit


## Functionalitati
	1. Autentificare și securitate (Keycloak)
	- Formular de înregistrare pentru rolurile Pacient și Terapeut, colectând date de bază (Nume, Prenume, Email, Parolă, Telefon, Gen);
	- Autorizare bazată pe roluri (Pacient, Terapeut, Admin) pentru accesul la diferite funcționalități.
	2. Modul Pacient
	- După prima autentificare, pacientul este direcționat să își completeze profilul cu informații esențiale (ex. Data nașterii, CNP, istoric sportiv sumar);
	- Editarea datelor personale și selectarea/modificarea terapeutului (pe bază de criterii: locație, gen);
	- În pagina principală va putea vizualiza diagnosticul și progresul tratamentului (numărul de ședințe rămase până la reevaluare), precum și crea sau vizualiza programarea viitoare;
	- Posibilitatea de a adăuga feedback (jurnal) după fiecare ședință (ex. nivel durere, dificultate exerciții, oboseală, comentarii).
	3. Modul Terapeut
	- Editarea datelor personale și a specializării (adulți/pediatrie), dar și setarea programului de lucru (disponibilitate pe zile, intervale orare și locații), permițând astfel blocarea timpului pentru concedii;
	- În pagina principală va avea un calendar interactiv cu programările, dar și statistici rapide (nr. programări azi/săptămână, pacienți activi, pacienți ce necesită reevaluare);
	- Vizualizarea listei de pacienți (activi și arhivați), cu funcții de căutare și filtrare;
	- Adăugarea unei evaluări inițiale după prima programare (diagnostic, serviciu recomandat, nr. ședințe până la reevaluare, observații), dar și a reevaluărilor după finalizarea ședințelor recomandate;
	- Adăugarea unor notițe private (evoluții) despre progresul pacientului;
	- Dosar al fiecărui pacient pentru vizualizarea istoricului complet.
	4. Modul Administrativ (Admin)
	- Va gestiona locațiile clinicilor, serviciile oferite, vizualizarea și gestionarea conturilor de Terapeut și Pacient;
	- Statistici la nivel de clinică (venituri, număr total de programări, terapeuți activi).
	5. Module comune
	- Sistem de mesagerie pentru comunicarea directă între Pacient și Terapeutul curent;
	- Notificări pentru programări, mesaje noi în chat, remindere pentru adăugarea evaluărilor (Terapeuți) și a feedback-ului (Pacienți), dar și alerte pentru modificările programărilor.
	