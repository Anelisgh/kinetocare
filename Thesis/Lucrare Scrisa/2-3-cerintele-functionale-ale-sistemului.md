### 2.3 Cerințele funcționale ale sistemului
Cerințele funcționale prezentate în această secțiune descriu comportamentele pe care platforma KinetoCare trebuie să le manifeste din perspectiva utilizatorilor finali. Ele au fost derivate din analiza provocărilor operaționale identificate în secțiunea 2.1, din lacunele soluțiilor existente discutate în secțiunea 2.2 și din constrângerile logice specifice domeniului kinetoterapiei (secvențialitatea obligatorie evaluare inițială → tratament → reevaluare). Cerințele sunt organizate pe module funcționale corespunzătoare rolurilor din sistem și prioritizate conform metodologiei MoSCoW.   
### 2.3.1 Modulul de Autentificare și Autorizare   
- **Înregistrarea utilizatorilor**: Permite crearea de conturi noi cu selectarea explicită a rolului (*Pacient* sau *Terapeut*) și colectarea setului diferențiat de date de profil (nume, prenume, adresă de e-mail, parolă, număr de telefon, gen).   
- **Autentificarea securizată**: Asigură autentificarea prin adresă de e-mail și parolă, menținând sesiunea utilizatorului activă fără a expune credențialele sau jetoanele de acces în medii client vulnerabile.   
- **Controlul accesului bazat pe roluri (RBAC)**: Restricționează accesul la resurse și operațiuni pe baza rolurilor atribuite (*Pacient*, *Terapeut*, *Administrator*), garantând izolarea datelor specifice fiecărei categorii.   
- **Revocarea sesiunii (Logout)**: Permite deconectarea explicită a utilizatorului prin invalidarea jetonului de reîmprospătare persistent (refresh token) în serverul de identitate și eliminarea jetonului de acces din memoria clientului, restricționând accesul la intervalul de valabilitate rezidual al jetonului activ.   
- **Recuperarea autonomă a credențialelor**: Oferă un flux automatizat de resetare a parolei uitate prin intermediul unui jeton securizat transmis pe e-mail, eliminând necesitatea asistenței administrative manuale.   
    
### 2.3.2 Modulul Pacient   
#### Gestionarea profilului și a relației terapeutice
Acest grup de cerințe reglementează ciclul de viață al profilului pacientului și al relației sale cu terapeutul alocat:   
- **Completarea obligatorie a profilului clinic**: Direcționează pacientul, la prima autentificare, către completarea datelor clinice obligatorii (data nașterii, CNP), restricționând accesul la funcționalitățile medicale până la finalizarea acestui pas.   
- **Actualizarea datelor de profil**: Permite vizualizarea și actualizarea în orice moment a datelor personale și a preferințelor de tratament de către pacient.   
- **Căutarea și selecția terapeutului**: Oferă posibilitatea de a căuta și selecta un terapeut dintr-un catalog filtrabil după criterii precum specializare, locație și județ.   
- **Modificarea relației terapeutice**: Permite schimbarea terapeutului activ, determinând anularea automată a programărilor viitoare cu terapeutul anterior, cu păstrarea integrală a istoricului clinic în sistem.   
   
#### Gestionarea programărilor
Cerințele din această categorie acoperă întregul ciclu al unei rezervări, de la generarea sloturilor disponibile până la anulare:   
- **Crearea programărilor cu alocare automată de servicii**: Permite rezervarea unei programări prin selectarea terapeutului, a locației clinicii și a intervalului orar liber; sistemul determină și alocă automat tipul de serviciu medical aplicabil (evaluare inițială, tratament sau reevaluare) pe baza istoricului clinic al pacientului.   
- **Vizualizarea sloturilor disponibile**: Prezintă exclusiv intervalele orare de disponibilitate ale terapeutului, excluzând perioadele deja ocupate și perioadele de absență sau concediu înregistrate.   
- **Validarea suprapunerilor**: Garantează integritatea planificării prin blocarea oricărei încercări de suprapunere a intervalelor orare în momentul confirmării rezervării.   
- **Anularea programărilor**: Oferă pacientului posibilitatea de a anula propriile programări viitoare.   
- **Consultarea istoricului clinic**: Permite vizualizarea istoricului detaliat al tuturor ședințelor programate, incluzând starea acestora, tipul de serviciu medical și costul aferent.   
   
#### Tabloul de bord și urmărirea progresului
Următoarele cerințe guvernează modul în care pacientul își monitorizează evoluția clinică prin intermediul interfeței principale a aplicației:   
- **Afișarea stării curente**: Prezintă în mod centralizat detaliile următoarei ședințe planificate și indicatorii grafici de progres ai planului terapeutic activ (numărul de ședințe efectuate raportat la cel prescris).   
- **Controlul fluxului de tratament**: Condiționează rezervarea ședințelor de tratament de existența unei evaluări inițiale active, ghidând pacientul către programarea evaluării inițiale în absența acesteia.   
- **Colectarea feedback-ului post-ședință**: Solicită completarea unui jurnal de feedback subiectiv după finalizarea fiecărei ședințe clinice, limitând înregistrarea la un singur jurnal per ședință.   
    
### 2.3.3 Modulul Terapeut   
#### Gestionarea profilului profesional și a disponibilității
Următoarele cerințe acoperă configurația profesională a terapeutului și mecanismele de gestionare a planificării proprii:   
- **Configurarea profilului profesional**: Permite terapeutului să își definească specializările clinice și matricea orarului de lucru săptămânal alocat fiecărei locații fizice.   
- **Gestiunea absențelor**: Permite înregistrarea perioadelor de concediu sau indisponibilitate temporară, blocând automat posibilitatea pacienților de a rezerva sloturi în aceste intervale.   
   
#### Calendarul de programări și operațiuni
Acest grup definește funcționalitățile de vizualizare și gestiune a calendarului clinic al terapeutului:   
- **Vizualizarea calendaristică**: Oferă o interfață interactivă de vizualizare a programărilor sub formă de calendar, permițând examinarea detaliată a fiecărei ședințe clinice.   
- **Actualizarea stării programărilor**: Permite modificarea manuală de către terapeut a stării unei programări (finalizare, anulare sau marcare ca neprezentare).   
- **Procesarea automată a ședințelor expirate**: Rulează un mecanism de fundal care identifică și marchează automat ca finalizate programările al căror interval orar a expirat.   
   
#### Gestionarea pacienților și documentarea clinică
Cerințele clinice centrale ale rolului de terapeut privesc accesul la dosarul medical al pacientului și instrumentele de documentare:   
- **Registrul pacienților arondați**: Permite accesarea registrului de pacienți alocați, cu posibilități de filtrare după starea relației clinice (activi sau arhivați); asocierea pacient-terapeut este creată automat la prima programare validată.   
- **Documentarea evaluării inițiale**: Oferă șabloane clinice pentru redactarea evaluării inițiale, incluzând diagnosticul, serviciul prescris, numărul de ședințe recomandat și observații clinice.   
- **Documentarea reevaluărilor periodice**: Permite redactarea de fișe de reevaluare clinică la epuizarea pachetului de ședințe prescris anterior.   
- **Note de evoluție intermediare**: Permite redactarea, editarea sau ștergerea notițelor de progres adăugate între ședințe.   
- **Vizualizarea dosarului clinic unificat**: Reunește într-o singură fișă medicală evaluările clinice, notele de evoluție, istoricul programărilor și tendințele înregistrate în jurnalele de feedback subiectiv ale pacientului.   
- **Monitorizarea pragului de reevaluare**: Notifică proactiv terapeutul în momentul în care pacientul își rezervă o ședință de reevaluare clinică (evenimentul `reevaluareNecesara`), semnalând necesitatea revizuirii dosarului clinic pentru actualizarea planului terapeutic.   
    
### 2.3.4 Modulul Administrativ   
- **Bootstrap automat al administratorului**: Inițializează în mod automat un cont implicit cu privilegii depline de administrator la prima rulare a mediului de execuție a platformei.   
- **Gestiunea structurii organizatorice**: Oferă funcționalități de adăugare, editare sau dezactivare a locațiilor fizice ale clinicilor din rețea.   
- **Gestiunea catalogului de servicii**: Permite administrarea serviciilor medicale oferite, incluzând definirea tarifelor și a duratei standard pentru fiecare tip de ședință.   
- **Controlul administrativ al conturilor**: Permite suspendarea sau reactivarea conturilor de utilizator; acțiunea de suspendare dezactivează contul în furnizorul de identitate (Keycloak), împiedicând orice autentificare sau reîmprospătare ulterioară a sesiunii, și propagă automat anularea programărilor viitoare.   
- **Raportarea statistică consolidată**: Expune un panou de monitorizare cu date agregate per locație, acoperind volumul de programări, indicatorii financiari, încărcarea personalului medical și rata de anulare a ședințelor.   
    
### 2.3.5 Module transversale   
#### Sistemul de comunicații directe
Aceste cerințe definesc canalul de mesagerie clinică în timp real, guvernat de logica relației terapeutice:   
- **Canal de mesagerie securizat în timp real**: Asigură comunicarea bidirecțională instantanee între pacient și terapeutul său activ.   
- **Validarea clinică a dreptului de comunicare**: Verifică existența unei relații terapeutice active înainte de livrarea fiecărui mesaj; la încheierea relației clinice, conversațiile sunt arhivate, fiind blocat orice schimb de mesaje noi.   
- **Inițializarea automată a canalului**: Creează în mod automat o interfață de conversație imediat ce relația clinică dintre pacient și terapeut este stabilită în sistem, fără a necesita inițiere manuală.   
   
#### Sistemul de notificări
Cerințele de notificare acoperă atât declanșarea bazată pe evenimente a alertelor, cât și persistența lor:   
- **Notificări declanșate de evenimente**: Generează automat mesaje de notificare la apariția evenimentelor critice: rezervarea sau anularea unei programări, remindere pre-ședință, alerte pentru completarea jurnalului de feedback, recomandarea de reevaluare la epuizarea ședințelor prescrise (corespunzător evenimentului `reevaluareRecomandata`) și recepționarea de mesaje noi în chat.   
- **Centru de notificări persistent**: Stochează și pune la dispoziție notificările printr-o interfață dedicată în aplicația client, permițând vizualizarea istoricului și marcarea individuală sau în bloc a mesajelor ca citite.   
    
### 2.3.6 Prioritizarea cerințelor — MoSCoW   
Cerințele funcționale identificate au fost clasificate conform metodologiei MoSCoW pentru a delimita clar scopul implementării în cadrul acestei lucrări.   
- **Must Have** — cerințe fără de care platforma nu poate îndeplini obiectivul principal de trasabilitate clinică automatizată. Includ: înregistrarea și autentificarea utilizatorilor, completarea obligatorie a profilului clinic, determinarea automată a tipului de serviciu la rezervare, generarea și afișarea sloturilor disponibile, prevenirea suprapunerii programărilor prin tranzacționalitate robustă, finalizarea automată a ședințelor expirate, crearea evaluărilor și reevaluărilor clinice, vizualizarea fișei pacientului, notificările, canalul de chat în timp real și validarea clinică a dreptului de comunicare. Acestea acoperă exact 28 de cerințe enunțate, constituind nucleul dur al platformei.   
- **Should Have** — funcționalități cu valoare adăugată ridicată a căror absență nu compromite direct tratamentul, dar îmbunătățește experiența utilizatorului sau capacitățile administrative (cum ar fi statisticile administrative consolidate, istoricul clinic al pacientului, suspendarea conturilor de către administratori și mecanismele automate de inițializare a canalului de comunicare). Acestea reprezintă 10 cerințe detaliate.   
- **Could Have** — funcționalități dezirabile pe termen mediu, identificate în analiza de domeniu dar excluse din scopul actual: un asistent de recomandare a terapeutului bazat pe preferințe extinse și integrarea cu sisteme externe de calendare.   
- **Won't Have** — integrarea cu sisteme de decontare medicală directă (CNAS), gestiunea fiscală avansată sau conformitatea cu reglementări legislative internaționale; aceste aspecte depășesc perimetrul unui sistem de management clinic și reprezintă limitări asumate și documentate ale lucrării.   

### 2.3.7 Maparea explicită și prioritizarea MoSCoW a cerințelor funcționale
Pentru a asigura o verificare riguroasă a acoperirii cerințelor pe parcursul dezvoltării și testării sistemului, tabelul de mai jos mapează fiecare cerință definită anterior la o prioritate MoSCoW clară. Această structură oferă evaluatorului un instrument riguros de urmărire a stadiului implementării.

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
| Pacient | Validarea suprapunerilor la nivel tranzacțional (prevenire double-booking) | **Must Have** |
| Pacient | Anularea programărilor viitoare | **Must Have** |
| Pacient | Controlul fluxului de tratament (Evaluare activă obligatorie) | **Must Have** |
| Pacient | Colectarea feedback-ului post-ședință (Jurnal subiectiv durere) | **Must Have** |
| Terapeut | Configurarea profilului profesional și a orarului multi-locație | **Must Have** |
| Terapeut | Gestiunea perioadelor de concediu și absențe | **Must Have** |
| Terapeut | Vizualizarea calendaristică a programărilor active | **Must Have** |
| Terapeut | Actualizarea stării programărilor (Finalizare manuală/Anulare) | **Must Have** |
| Terapeut | Procesarea automată în fundal a ședințelor expirate (*cron* job-ul) | **Must Have** |
| Terapeut | Documentarea evaluării inițiale (Diagnostic/Plan recuperare) | **Must Have** |
| Terapeut | Documentarea reevaluărilor periodice | **Must Have** |
| Terapeut | Note de evoluție intermediare (de progres) adăugate manual | **Must Have** |
| Terapeut | Vizualizarea dosarului clinic unificat (Fișa Pacientului) | **Must Have** |
| Terapeut | Monitorizarea pragului de reevaluare (Notificare reevaluare necesară) | **Must Have** |
| Transversal | Canal de mesagerie securizat în timp real (WebSocket/STOMP) | **Must Have** |
| Transversal | Validarea clinică a dreptului de comunicare (Arhivare istoric chat) | **Must Have** |
| Transversal | Notificări declanșate automat de evenimentele din sistem | **Must Have** |
| Transversal | Centru de notificări persistent în interfața grafică | **Must Have** |
| Autentificare | Recuperarea autonomă a credențialelor (Resetare parolă) | *Should Have* |
| Pacient | Consultarea istoricului clinic extins și a jurnalelor anterioare | *Should Have* |
| Pacient | Afișarea stării curente și indicatorilor de progres pe dashboard | *Should Have* |
| Terapeut | Registrul pacienților arondați (listare activi/arhivați) | *Should Have* |
| Administrativ | Bootstrap automat al contului implicit de administrator | *Should Have* |
| Administrativ | Gestiunea structurii organizatorice (Locații fizice clinică) | *Should Have* |
| Administrativ | Gestiunea catalogului de servicii medicale și a tarifelor | *Should Have* |
| Administrativ | Controlul administrativ al conturilor (Suspendare/Reactivare) | *Should Have* |
| Administrativ | Raportarea statistică consolidată pe clinici (Financiar/Volume) | *Should Have* |
| Transversal | Inițializarea automată a canalului de chat la stabilirea relației | *Should Have* |
| Pacient | Recomandarea inteligentă a terapeuților pe criterii clinice | Could Have |
| Transversal | Sincronizarea cu sisteme externe de calendare (Google/Outlook) | Could Have |
| Administrativ | Integrarea cu sisteme de facturare și decontare directă CNAS | Won't Have |

Prin această segmentare, toate cele 28 de cerințe **Must Have** sunt reprezentate direct în implementarea tehnică prezentată în Capitolul 6 și Capitolul 7, garantând conformitatea completă a platformei cu obiectivele stabilite.   

