## 2.3 Cerințele funcționale ale sistemului

Cerințele funcționale prezentate în această secțiune descriu comportamentele pe care platforma KinetoCare trebuie să le manifeste din perspectiva utilizatorilor finali. Ele au fost derivate din analiza provocărilor operaționale identificate în secțiunea 2.1, din lacunele soluțiilor existente discutate în secțiunea 2.2 și din constrângerile logice specifice domeniului kinetoterapiei (secvențialitatea obligatorie evaluare inițială → tratament → reevaluare). Cerințele sunt organizate pe module funcționale corespunzătoare rolurilor din sistem și prioritizate conform metodologiei MoSCoW.

### 2.3.1 Modulul de autentificare și autorizare

- **Înregistrarea utilizatorilor:** Crearea de conturi noi cu selectarea explicită a rolului (*Pacient* sau *Terapeut*) și colectarea setului diferențiat de date de profil (nume, prenume, adresă de e-mail, parolă, număr de telefon, gen).
- **Autentificarea securizată:** Autentificarea prin adresă de e-mail și parolă, cu menținerea sesiunii utilizatorului fără expunerea credențialelor sau a jetoanelor de acces în medii client vulnerabile.
- **Controlul accesului bazat pe roluri (RBAC):** Restricționarea accesului la resurse și operațiuni pe baza rolurilor atribuite (*Pacient*, *Terapeut*, *Administrator*), cu garantarea izolării datelor specifice fiecărei categorii.
- **Revocarea sesiunii (*Logout*):** Deconectarea explicită a utilizatorului prin invalidarea jetonului de reîmprospătare persistent (*refresh token*) în serverul de identitate și eliminarea jetonului de acces din memoria clientului, restricționând accesul la intervalul de valabilitate rezidual al jetonului activ.
- **Recuperarea autonomă a credențialelor:** Flux automatizat de resetare a parolei uitate prin intermediul unui jeton securizat transmis pe e-mail, eliminând necesitatea asistenței administrative manuale.

### 2.3.2 Modulul Pacient

#### Gestionarea profilului și a relației terapeutice

- **Completarea obligatorie a profilului clinic:** Direcționarea pacientului, la prima autentificare, către completarea datelor clinice obligatorii (data nașterii, cod numeric personal), cu restricționarea accesului la funcționalitățile medicale până la finalizarea acestui pas.
- **Actualizarea datelor de profil:** Vizualizarea și actualizarea datelor personale și a preferințelor de tratament în orice moment.
- **Căutarea și selecția terapeutului:** Posibilitatea de a căuta și selecta un terapeut dintr-un catalog filtrabil după criterii precum specializare, locație și județ.
- **Modificarea relației terapeutice:** Schimbarea terapeutului activ, cu anularea automată a programărilor viitoare cu terapeutul anterior și cu păstrarea integrală a istoricului clinic în sistem.

#### Gestionarea programărilor

- **Crearea programărilor cu alocare automată de servicii:** Rezervarea unei programări prin selectarea terapeutului, a locației clinicii și a intervalului orar liber; tipul de serviciu medical aplicabil (evaluare inițială, tratament sau reevaluare) este determinat și alocat automat pe baza istoricului clinic al pacientului.
- **Vizualizarea sloturilor disponibile:** Prezentarea exclusivă a intervalelor orare de disponibilitate ale terapeutului, cu excluderea perioadelor deja ocupate și a perioadelor de absență sau concediu înregistrate.
- **Validarea suprapunerilor:** Garantarea integrității planificării prin blocarea oricărei încercări de suprapunere a intervalelor orare în momentul confirmării rezervării.
- **Anularea programărilor:** Posibilitatea pacientului de a anula propriile programări viitoare.
- **Consultarea istoricului clinic:** Vizualizarea istoricului detaliat al tuturor ședințelor programate, incluzând starea acestora, tipul de serviciu medical și costul aferent.

#### Tabloul de bord și urmărirea progresului

- **Afișarea stării curente:** Prezentarea centralizată a detaliilor următoarei ședințe planificate și a indicatorilor grafici de progres ai planului terapeutic activ (numărul de ședințe efectuate raportat la cel prescris).
- **Controlul fluxului de tratament:** Condiționarea rezervării ședințelor de tratament de existența unei evaluări inițiale active, cu ghidarea pacientului către programarea evaluării inițiale în absența acesteia.
- **Colectarea feedback-ului post-ședință:** Solicitarea completării unui jurnal de feedback subiectiv după finalizarea fiecărei ședințe clinice, cu limitarea înregistrării la un singur jurnal per ședință.

### 2.3.3 Modulul Terapeut

#### Gestionarea profilului profesional și a disponibilității

- **Configurarea profilului profesional:** Definirea specializărilor clinice și a matricei orarului de lucru săptămânal alocat fiecărei locații fizice.
- **Gestiunea absențelor:** Înregistrarea perioadelor de concediu sau de indisponibilitate temporară, cu blocarea automată a posibilității pacienților de a rezerva sloturi în aceste intervale.

#### Calendarul de programări și operațiuni

- **Vizualizarea calendaristică:** Interfață interactivă de vizualizare a programărilor sub formă de calendar, cu posibilitatea examinării detaliate a fiecărei ședințe clinice.
- **Actualizarea stării programărilor:** Modificarea manuală de către terapeut a stării unei programări (finalizare, anulare sau marcare ca neprezentare).
- **Procesarea automată a ședințelor expirate:** Mecanism de fundal care identifică și marchează automat ca finalizate programările al căror interval orar a expirat.

#### Gestionarea pacienților și documentarea clinică

- **Registrul pacienților arondați:** Accesul la registrul pacienților alocați, cu filtrare după starea relației clinice (activi sau arhivați); asocierea pacient-terapeut este constituită automat la prima programare validată.
- **Documentarea evaluării inițiale:** Șabloane clinice pentru redactarea evaluării inițiale, incluzând diagnosticul, serviciul prescris, numărul de ședințe recomandat și observațiile clinice.
- **Documentarea reevaluărilor periodice:** Redactarea fișelor de reevaluare clinică la epuizarea pachetului de ședințe prescris anterior.
- **Note de evoluție intermediare:** Redactarea, editarea sau ștergerea notițelor de progres adăugate între ședințe.
- **Vizualizarea dosarului clinic unificat:** Reunirea, într-o singură fișă medicală, a evaluărilor clinice, a notelor de evoluție, a istoricului programărilor și a tendințelor înregistrate în jurnalele de feedback subiectiv ale pacientului.
- **Monitorizarea pragului de reevaluare:** Notificarea proactivă a terapeutului în momentul în care pacientul rezervă o ședință de reevaluare clinică (evenimentul `reevaluareNecesara`), semnalând necesitatea revizuirii dosarului clinic pentru actualizarea planului terapeutic.

### 2.3.4 Modulul Administrativ

- **Bootstrap automat al administratorului:** Inițializarea automată a unui cont implicit cu privilegii depline de administrator la prima rulare a mediului de execuție al platformei.
- **Gestiunea structurii organizatorice:** Adăugarea, editarea sau dezactivarea locațiilor fizice ale clinicilor din rețea.
- **Gestiunea catalogului de servicii:** Administrarea serviciilor medicale oferite, incluzând definirea tarifelor și a duratei standard pentru fiecare tip de ședință.
- **Controlul administrativ al conturilor:** Suspendarea sau reactivarea conturilor de utilizator; acțiunea de suspendare dezactivează contul în furnizorul de identitate (Keycloak), împiedică orice autentificare sau reîmprospătare ulterioară a sesiunii și propagă automat anularea programărilor viitoare.
- **Raportarea statistică consolidată:** Panou de monitorizare cu date agregate per locație, acoperind volumul de programări, indicatorii financiari, încărcarea personalului medical și rata de anulare a ședințelor.

### 2.3.5 Module transversale

#### Sistemul de comunicații directe

- **Canal de mesagerie securizat în timp real:** Comunicarea bidirecțională instantanee între pacient și terapeutul său activ.
- **Validarea clinică a dreptului de comunicare:** Verificarea existenței unei relații terapeutice active înainte de livrarea fiecărui mesaj; la încheierea relației clinice, conversațiile sunt arhivate și orice schimb de mesaje noi este blocat.
- **Inițializarea automată a canalului:** Crearea automată a unei interfețe de conversație imediat ce relația clinică dintre pacient și terapeut este stabilită în sistem, fără a necesita inițiere manuală.

#### Sistemul de notificări

- **Notificări declanșate de evenimente:** Generarea automată de mesaje de notificare la apariția evenimentelor critice: rezervarea sau anularea unei programări, remindere pre-ședință, alerte pentru completarea jurnalului de feedback, recomandarea de reevaluare la epuizarea ședințelor prescrise (evenimentul `reevaluareRecomandata`) și recepționarea de mesaje noi în *chat*.
- **Centru de notificări persistent:** Stocarea și punerea la dispoziție a notificărilor printr-o interfață dedicată în aplicația client, cu posibilitatea vizualizării istoricului și marcării individuale sau în bloc a mesajelor ca citite.

### 2.3.6 Prioritizarea cerințelor — MoSCoW

Cerințele funcționale identificate au fost clasificate conform metodologiei MoSCoW pentru a delimita clar scopul implementării în cadrul acestei lucrări.

- ***Must Have*** — cerințe fără de care platforma nu poate îndeplini obiectivul principal de trasabilitate clinică automatizată. Sunt incluse: înregistrarea și autentificarea utilizatorilor, completarea obligatorie a profilului clinic, determinarea automată a tipului de serviciu la rezervare, generarea și afișarea sloturilor disponibile, prevenirea suprapunerii programărilor prin tranzacționalitate robustă, finalizarea automată a ședințelor expirate, crearea evaluărilor și reevaluărilor clinice, vizualizarea fișei pacientului, notificările, canalul de *chat* în timp real și validarea clinică a dreptului de comunicare. Acestea acoperă 28 de cerințe enunțate, constituind nucleul dur al platformei.
- ***Should Have*** — funcționalități cu valoare adăugată ridicată a căror absență nu compromite direct tratamentul, dar îmbunătățește experiența utilizatorului sau capacitățile administrative (statistici administrative consolidate, istoricul clinic al pacientului, suspendarea conturilor de către administratori și mecanismele automate de inițializare a canalului de comunicare). Această categorie cuprinde 10 cerințe detaliate.
- ***Could Have*** — funcționalități dezirabile pe termen mediu, identificate în analiza de domeniu, dar excluse din scopul actual: un asistent de recomandare a terapeutului bazat pe preferințe extinse și integrarea cu sisteme externe de calendare.
- ***Won't Have*** — integrarea cu sisteme de decontare medicală directă (CNAS), gestiunea fiscală avansată sau conformitatea cu reglementări legislative internaționale; aceste aspecte depășesc perimetrul unui sistem de management clinic și reprezintă limitări asumate și documentate ale lucrării.

### 2.3.7 Maparea și prioritizarea MoSCoW a cerințelor funcționale

Pentru a asigura o verificare riguroasă a acoperirii cerințelor pe parcursul dezvoltării și testării sistemului, tabelul de mai jos mapează fiecare cerință definită anterior la o prioritate MoSCoW clară, oferind un instrument sistematic de urmărire a stadiului implementării.

| Modul / Subsistem | Denumire Cerință Funcțională | Prioritate MoSCoW |
| :--- | :--- | :--- |
| Autentificare | Înregistrarea utilizatorilor (Pacient / Terapeut) | **Must Have** |
| Autentificare | Autentificarea securizată (JWT) | **Must Have** |
| Autentificare | Controlul accesului bazat pe roluri (RBAC) | **Must Have** |
| Autentificare | Revocarea sesiunii (Logout) | **Must Have** |
| Pacient | Completarea obligatorie a profilului clinic la prima autentificare | **Must Have** |
| Pacient | Actualizarea datelor personale de profil | **Must Have** |
| Pacient | Căutarea și selecția terapeutului | **Must Have** |
| Pacient | Modificarea relației terapeutice (Schimbare terapeut) | **Must Have** |
| Pacient | Crearea programărilor cu alocare automată de servicii clinice | **Must Have** |
| Pacient | Vizualizarea sloturilor disponibile ale terapeuților | **Must Have** |
| Pacient | Validarea suprapunerilor la nivel tranzacțional (prevenire *double-booking*) | **Must Have** |
| Pacient | Anularea programărilor viitoare | **Must Have** |
| Pacient | Controlul fluxului de tratament (Evaluare activă obligatorie) | **Must Have** |
| Pacient | Colectarea feedback-ului post-ședință (Jurnal subiectiv durere) | **Must Have** |
| Terapeut | Configurarea profilului profesional și a orarului multi-locație | **Must Have** |
| Terapeut | Gestiunea perioadelor de concediu și absențe | **Must Have** |
| Terapeut | Vizualizarea calendaristică a programărilor active | **Must Have** |
| Terapeut | Actualizarea stării programărilor (Finalizare manuală / Anulare) | **Must Have** |
| Terapeut | Procesarea automată în fundal a ședințelor expirate (*cron job*-ul) | **Must Have** |
| Terapeut | Documentarea evaluării inițiale (Diagnostic / Plan recuperare) | **Must Have** |
| Terapeut | Documentarea reevaluărilor periodice | **Must Have** |
| Terapeut | Note de evoluție intermediare (de progres) adăugate manual | **Must Have** |
| Terapeut | Vizualizarea dosarului clinic unificat (Fișa Pacientului) | **Must Have** |
| Terapeut | Monitorizarea pragului de reevaluare (Notificare reevaluare necesară) | **Must Have** |
| Transversal | Canal de mesagerie securizat în timp real (WebSocket/STOMP) | **Must Have** |
| Transversal | Validarea clinică a dreptului de comunicare (Arhivare istoric *chat*) | **Must Have** |
| Transversal | Notificări declanșate automat de evenimentele din sistem | **Must Have** |
| Transversal | Centru de notificări persistent în interfața grafică | **Must Have** |
| Autentificare | Recuperarea autonomă a credențialelor (Resetare parolă) | *Should Have* |
| Pacient | Consultarea istoricului clinic extins și a jurnalelor anterioare | *Should Have* |
| Pacient | Afișarea stării curente și a indicatorilor de progres pe *dashboard* | *Should Have* |
| Terapeut | Registrul pacienților arondați (listare activi / arhivați) | *Should Have* |
| Administrativ | Bootstrap automat al contului implicit de administrator | *Should Have* |
| Administrativ | Gestiunea structurii organizatorice (Locații fizice clinică) | *Should Have* |
| Administrativ | Gestiunea catalogului de servicii medicale și a tarifelor | *Should Have* |
| Administrativ | Controlul administrativ al conturilor (Suspendare / Reactivare) | *Should Have* |
| Administrativ | Raportarea statistică consolidată pe clinici (Financiar / Volume) | *Should Have* |
| Transversal | Inițializarea automată a canalului de *chat* la stabilirea relației | *Should Have* |
| Pacient | Recomandarea inteligentă a terapeuților pe criterii clinice | Could Have |
| Transversal | Sincronizarea cu sisteme externe de calendare (Google / Outlook) | Could Have |
| Administrativ | Integrarea cu sisteme de facturare și decontare directă CNAS | Won't Have |

Prin această segmentare, toate cele 28 de cerințe *Must Have* sunt reflectate direct în implementarea tehnică prezentată în Capitolul 6 și Capitolul 7, garantând conformitatea completă a platformei cu obiectivele stabilite.
