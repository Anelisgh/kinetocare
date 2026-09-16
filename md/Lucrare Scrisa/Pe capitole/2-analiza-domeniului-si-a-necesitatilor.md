# Capitolul 2. Analiza Domeniului și Cerințele Sistemului

Acest capitol fundamentează necesitatea și sfera de cuprindere a sistemului KinetoCare prin parcurgerea a patru etape analitice succesive. În prima parte sunt identificate provocările operaționale specifice managementului clinic în kinetoterapie. Pe această bază, secțiunea a doua evaluează critic soluțiile *software* existente pe piață, evidențiind lacunele funcționale neadresate și poziționând comparativ sistemul propus printr-o analiză SWOT. Ultimele două secțiuni formalizează, respectiv, cerințele funcționale și non-funcționale derivate din această analiză, constituind baza de referință pentru deciziile de proiectare arhitecturală din capitolele următoare.

## 2.1 Provocările din managementul clinic

Sectorul de recuperare medicală și kinetoterapie se distinge de alte ramuri medicale prin natura sa iterativă. Tratamentul nu constă, de regulă, într-o singură intervenție izolată, ci într-un traseu terapeutic ce implică multiple ședințe, evaluări și reevaluări periodice. Această recurență generează o serie de provocări operaționale complexe, dificil de gestionat în absența unui sistem informatic integrat. Clinicile de kinetoterapie din România operează în marea majoritate cu instrumente generice — agende fizice, foi de calcul, aplicații de calendar — care nu au fost concepute pentru specificul domeniului medical recuperator.

### 2.1.1 Fragmentarea comunicării și a fluxului informațional

În absența unei platforme centralizate, comunicarea dintre clinici, terapeuți și pacienți este ineficientă și predispusă la erori:

- **Gestiunea rudimentară a programărilor:** Planificarea se realizează frecvent prin metode tradiționale — telefonic sau prin agende fizice — ceea ce introduce un risc ridicat de eroare umană, concretizat în dublă rezervare sau în intervale neproductive în orarul terapeuților.
- **Procese manuale de notificare:** Informarea pacienților cu privire la programări sau anulări se realizează manual, consumând timp administrativ și crescând rata de neprezentare atunci când notificările sunt omise.
- **Lipsa buclei de feedback post-ședință:** Nu există un canal structurat prin care pacientul să raporteze nivelul de durere resimțit, dificultatea exercițiilor sau gradul de oboseală între ședințe. Absența acestor date limitează capacitatea terapeutului de a ajusta planul de tratament pe baza evoluției reale a pacientului.

### 2.1.2 Lipsa trasabilității clinice și a monitorizării continue

Kinetoterapia modernă necesită decizii clinice fundamentate pe o practică medicală bazată pe dovezi. Managementul manual al dosarelor face ca această monitorizare să fie sistematic deficitară:

- **Urmărirea deficitară a planului terapeutic:** Nu există un mecanism automat care să monitorizeze numărul de ședințe efectuate în raport cu numărul recomandat. Terapeuții sunt nevoiți să calculeze manual progresul, cu riscul ratării momentului optim pentru reevaluarea clinică.
- **Pierderea contextului la schimbarea terapeutului:** Fără un dosar medical electronic unificat — care să conțină evaluările, istoricul ședințelor și notițele de progres — un terapeut care preia un pacient de la un coleg pierde contextul clinic, afectând continuitatea actului medical.
- **Implicarea scăzută a pacientului:** Absența vizibilității asupra propriului progres diminuează semnificativ aderența la tratament pe termen lung.

### 2.1.3 Dificultăți în managementul disponibilității și al resurselor

Resursele unei clinici — timpul terapeuților și spațiile de tratament — sunt limitate și necesită o alocare dinamică:

- **Gestiunea multi-locație:** Terapeuții pot activa în locații diferite în zile diferite ale săptămânii. Sistemele tradiționale nu sunt capabile să reprezinte fidel această matrice complexă de disponibilitate, obligând pacienții să contacteze recepția pentru orice verificare de program.
- **Sincronizarea concediilor:** Perioadele de absență ale personalului medical nu sunt vizibile pacienților în timp real, generând programări invalide care necesită reprogramare ulterioară.

### 2.1.4 Ineficiențe în managementul administrativ și decizional

Din perspectiva administratorilor de clinică, absența unui sistem digitalizat limitează capacitatea de optimizare a activității:

- **Lipsa vizibilității asupra indicatorilor de performanță:** Fără date centralizate, evaluarea cu precizie a veniturilor per locație, a volumului de programări, a ratei de anulări sau a performanței individuale a terapeuților nu este realizabilă sistematic.
- **Gestiunea greoaie a catalogului de servicii:** Ajustarea prețurilor sau adăugarea de servicii noi se face disparat, cu riscul neconcordanțelor între informațiile disponibile la recepție și cele comunicate pacienților.

### 2.1.5 Protecția datelor medicale sensibile

Dosarele de recuperare medicală conțin informații cu caracter personal sensibil — diagnostic, istoric medical, cod numeric personal — a căror protecție este reglementată prin Regulamentul General privind Protecția Datelor (GDPR). În absența unui sistem informatic specializat, clinicile se expun unor riscuri concrete:

- **Control insuficient al accesului:** În evidențele fizice sau în documentele partajate de tip tabelar, toți angajații dispun frecvent de acces nerestricționat la baza de date completă a pacienților, fără posibilitatea de a delimita accesul în funcție de rol (pacient, terapeut, administrator).
- **Absența unui jurnal de trasabilitate (*audit trail*):** Orice modificare a unui dosar medical ar trebui înregistrată cu marcaj temporal și cu identitatea celui care a efectuat-o. Soluțiile nespecializate nu oferă această trasabilitate, îngreunând demonstrarea conformității GDPR în cazul unui control extern.

## 2.2 Analiza soluțiilor existente și limitele acestora

### 2.2.1 Analiza comparativă a soluțiilor existente

Piața *software*-ului pentru managementul clinicilor de kinetoterapie și fizioterapie este structurată în două segmente distincte: soluții internaționale mature, proiectate preponderent pentru piața americană sau britanică, și soluții românești adaptate contextului legislativ local. O observație comună tuturor produselor analizate este că niciuna nu implementează un mecanism automat de determinare a serviciului clinic pe baza traiectoriei terapeutice a pacientului — această decizie rămâne în toate cazurile manuală, la latitudinea terapeutului.

#### Soluții internaționale

**1. WebPT**

WebPT este cel mai utilizat *software* de tip dosar medical electronic (EMR) dedicat terapiei fizice din Statele Unite, cu peste 20.000 de clinici cliente. Este o platformă *cloud* construită specific pentru fizioterapeuți, terapeuți ocupaționali și logopezi.

*Funcționalități principale:* EMR specializat cu șabloane SOAP și fișe de evoluție clinică; gestiunea programărilor cu suport pentru sesiuni recurente și de grup; facturare și gestionarea ciclului de venituri; portal pacient cu program de exerciții la domiciliu; rapoarte de performanță clinică.

*Recenzii (Capterra / G2 / SoftwareAdvice):* Platforma este apreciată pentru specializarea pe terapia fizică și conformitatea cu reglementările americane. Criticile recurente vizează creșterile anuale de prețuri (7–10% în 2024), calitatea în declin a suportului tehnic și complexitatea integrării cu modulul de facturare.

*Poziționare comparativă:* WebPT reprezintă soluția de referință pentru piața americană, cu avantaje clare în facturarea medicală complexă. Nu este configurat pentru specificul pieței românești și al reglementărilor europene aplicabile (GDPR, nomenclator medical național); de asemenea, nu rezolvă problema traiectoriei terapeutice automate, iar jurnalul de feedback al pacientului și mesageria directă pacient-terapeut lipsesc din platformă.

**2. Pabau**

Pabau este un *software* de management clinic destinat practicilor multi-specialitate și multi-locație, popular în Marea Britanie, Australia și Orientul Mijlociu.

*Funcționalități principale:* Calendar unificat multi-locație; dosar medical electronic cu asistent de documentare bazat pe inteligență artificială; portal pacient cu programare *online* autonomă; automatizări de marketing; rapoarte de performanță consolidate per locație; control al accesului bazat pe roluri.

*Poziționare comparativă:* Pabau constituie o soluție *enterprise* solidă pentru clinici multi-locație, cu funcționalități de *business* superioare. Nu este specializat pe kinetoterapie: conceptele de plan terapeutic cu număr de ședințe recomandate, alertă automată de reevaluare și feedback subiectiv post-ședință lipsesc complet din arhitectura produsului.

#### Soluții românești

**3. FizioHub (fiziohub.ro)**

FizioHub este cea mai specializată platformă românească pentru fizioterapie și recuperare medicală, cu un modul dedicat denumit „reHab" care include trasabilitate clinică și prescripție digitală. Reprezintă cel mai direct competitor al KinetoCare pe piața locală.

*Funcționalități principale:* Sistem de gestiune a programărilor; fișă pacient cu istoricul evaluărilor și planuri de tratament; modulul reHab cu prescripție digitală de proceduri și marcare în timp real a procedurilor efectuate; panou de rapoarte clinice și conformitate GDPR.

*Recenzii:* Platforma este menționată pozitiv în comunitatea kinetoterapeuților români pentru modulul de urmărire a procedurilor. Absența recenziilor pe platformele internaționale reflectă prezența preponderent locală a soluției.

*Poziționare comparativă:* FizioHub este soluția românească cea mai apropiată ca domeniu de aplicare, cu avantajul urmăririi granulare a procedurilor în timp real. Cu toate acestea, decizia clinică privind serviciul aplicat la fiecare programare nu este automatizată, feedback-ul subiectiv al pacientului nu este colectat și mesageria integrată în timp real este absentă.

**4. ReKi (rekiapp.ro)**

ReKi este o platformă SaaS românească relativ nouă, dedicată clinicilor de recuperare și terapeuților independenți, cu accent pe simplitate operațională și integrare fiscală locală.

*Funcționalități principale:* Calendar și gestiunea programărilor; planuri de tratament prescrise; portal pacient; integrare e-Factură ANAF; notificări de confirmare.

*Poziționare comparativă:* ReKi deține avantajul integrării cu e-Factura ANAF, relevant pentru clinicile românești care facturează electronic. Funcționalitățile clinice avansate — jurnalul de feedback, mesageria directă și automatizarea traiectoriei terapeutice — lipsesc complet din oferta actuală.

#### Tabel comparativ global

|                                              Criteriu | WebPT | Pabau | FizioHub | ReKi | KinetoCare |
|:------------------------------------------------------|:------|:------|:---------|:-----|:-----------|
| Gestiunea programărilor cu detectare suprapunere | Integrat | Integrat | Integrat | Integrat | Integrat |
| Calendar interactiv | Integrat | Integrat | Integrat | Integrat | Integrat |
| Notificări și remindere automate | Integrat¹ | Integrat | Parțial | Parțial | Integrat |
| Documentare clinică | Integrat | Integrat | Integrat | Integrat | Integrat |
| Portal pacient | Integrat | Integrat | Integrat | Integrat | Integrat |
| Mesagerie directă pacient-terapeut în timp real | Absent | Absent | Absent | Absent | Integrat |
| Jurnal de feedback post-ședință | Absent | Absent | Absent | Absent | Integrat |
| Urmărirea planului terapeutic cu număr de ședințe | Absent | Absent | Parțial | Parțial | Integrat |
| Determinare automată a serviciului clinic | Absent | Absent | Absent | Absent | Integrat |
| Alertă automată la epuizarea planului terapeutic | Absent | Absent | Absent | Absent | Integrat |
| Facturare / decontare asigurări | Integrat | Integrat | Parțial | Integrat | Absent² |
| Asistent de documentare cu inteligență artificială | Integrat | Integrat | Absent | Absent | Absent² |
| Adaptat pieței românești | Absent | Parțial | Integrat | Integrat | Integrat |
| Conformitate GDPR declarată | Parțial | Parțial | Integrat | Integrat | Integrat |

*¹ Disponibil ca modul adițional, neincludat în planul de bază.*
*² Funcționalitate absentă în versiunea actuală.*

### 2.2.2 Lacunele comune identificate

Analiza comparativă relevă patru lacune funcționale sistematice, prezente în totalitatea soluțiilor evaluate, pe care platforma KinetoCare le adresează prin mecanisme arhitecturale specifice.

**Lacuna #1 — Automatizarea traiectoriei clinice**

Cel mai semnificativ gol identificat la nivelul întregii piețe constă în faptul că decizia privind serviciul medical aplicat la o programare rămâne manuală în toate soluțiile analizate. KinetoCare adresează această lacună prin determinarea automată a serviciului clinic pe baza istoricului terapeutic al pacientului, eliminând o întreagă clasă de erori umane.

**Lacuna #2 — Mesagerie directă pacient-terapeut**

Niciuna dintre soluțiile analizate nu oferă mesagerie directă și permanentă între pacient și terapeut ca funcționalitate nativă. În KinetoCare, un canal de comunicare în timp real este implementat cu verificarea automată a relației terapeutice active și arhivarea conversațiilor la încheierea acesteia.

**Lacuna #3 — Jurnalul subiectiv al pacientului**

Niciuna dintre soluțiile evaluate nu colectează structurat evaluarea subiectivă a pacientului după fiecare ședință. Platforma KinetoCare implementează un jurnal cu niveluri de durere, dificultate și oboseală, declanșat automat la finalizarea ședinței, cu vizualizare de tip *trend* în dosarul pacientului.

**Lacuna #4 — Alertă automată la epuizarea planului terapeutic**

În soluțiile existente, terapeutul identifică manual momentul optim pentru reevaluare. KinetoCare detectează automat condiția de epuizare a numărului de ședințe recomandate și transmite o notificare proactivă pacientului, eliminând dependența de vigilența manuală a personalului medical.

### 2.2.3 Limitările KinetoCare față de soluțiile mature

|                               Funcționalitate absentă | Soluții care o dețin | Motivul absenței |
|:------------------------------------------------------|:---------------------|:-----------------|
| Facturare medicală / decontare asigurări | WebPT, Pabau, ReKi | Funcționalitatea se situează în afara perimetrului arhitectural definit pentru lucrarea de disertație |
| Asistent de documentare cu inteligență artificială | WebPT, Pabau | Necesită integrarea unui model lingvistic extern, în afara scopului definit |
| Programare *online* fără autentificare prealabilă | Toate | Modelul platformei presupune existența unui cont de pacient pentru trasabilitate clinică |
| Integrare e-Factură ANAF | ReKi, FizioHub | Specific pieței românești; depășește scopul academic al proiectului |

### 2.2.4 Analiza SWOT a sistemului propus

Analiza SWOT sintetizează poziționarea strategică a sistemului KinetoCare față de piața existentă și față de contextul tehnic al proiectului, servind ca sinteză finală a analizei competitive și ca punte între lacunele identificate în secțiunile anterioare și cerințele funcționale și non-funcționale detaliate ulterior.

#### Puncte tari

Sistemul KinetoCare prezintă următoarele avantaje competitive și arhitecturale față de soluțiile existente analizate:

- **S1 — Automatizarea traiectoriei clinice (contribuție originală):** Niciuna dintre soluțiile analizate nu implementează selecția automată a serviciului clinic pe baza istoricului terapeutic. Aceasta constituie contribuția algoritmică centrală a proiectului.
- **S2 — Arhitectură distribuită cu izolare completă a domeniilor:** Structura pe module funcționale independente cu baze de date separate permite scalarea independentă a oricărei componente și limitează impactul unei defecțiuni la un singur domeniu.
- **S3 — Securitate de tip *Zero-Trust*:** Fiecare modul al sistemului validează independent identitatea solicitantului la fiecare cerere, fără a presupune că autentificarea a fost verificată de o altă componentă.
- **S4 — Comunicare în timp real cu validare clinică:** Canalul de mesagerie verifică existența relației terapeutice active înainte de orice schimb de mesaje, integrând logica clinică în stratul de comunicare.
- **S5 — Feedback subiectiv structurat al pacientului:** Jurnalul post-ședință cu niveluri de durere, oboseală și dificultate, vizualizat ca *trend* în dosarul pacientului, reprezintă o funcționalitate absentă în toate soluțiile concurente analizate.
- **S6 — Portabilitate și independență de mediu:** Containerizarea completă asigură paritatea comportamentului între mediile de dezvoltare și producție, eliminând dependențele de infrastructura unui furnizor specific.

#### Puncte slabe

La stadiul actual al proiectului, au fost identificate următoarele limitări funcționale cu impact direct asupra gradului de pregătire pentru producție:

- **W1 — Absența modulului de facturare și decontare:** Sistemul nu suportă emiterea de facturi sau decontarea cu casele de asigurări. Aceasta este cea mai semnificativă lacună față de soluțiile comerciale mature și limitează aplicabilitatea directă în producție fără o integrare externă.
- **W2 — Absența unui asistent de documentare bazat pe inteligență artificială:** Completarea evaluărilor și a notițelor de progres se realizează exclusiv manual, spre deosebire de soluțiile concurente mature care oferă generare automată de notițe clinice.
- **W3 — Sistem nevalidat cu utilizatori reali din domeniu:** Platforma nu a parcurs un ciclu de testare cu terapeuți și pacienți reali, ceea ce limitează certitudinea că interfețele sunt adaptate fluxurilor de lucru clinice efective.

#### Oportunități

Contextul de piață și dinamica ecosistemului tehnologic medical configurează următorii vectori de creștere și extindere a platformei:

- **O1 — Deficitul de soluții specializate pe piața locală:** Absența unor platforme locale integrate, care să unifice managementul programărilor, automatizarea traiectoriei clinice și comunicarea securizată, indică oportunitatea ocupării unui segment de piață neacoperit din punct de vedere tehnologic.
- **O2 — Cadrul macroeconomic favorabil digitalizării sectorului de sănătate:** Inițiativele naționale și europene de digitalizare — precum dosarul electronic al pacientului sau e-Factura — stimulează adoptarea sistemelor informatice dedicate în clinicile de recuperare medicală, facilitând tranziția către fluxuri complet digitalizate.
- **O3 — Scalabilitatea funcțională prin arhitectura orientată pe microservicii:** Decuplarea strictă a domeniilor permite integrarea ulterioară a unor module adiționale — precum sisteme de facturare sau procesare avansată a datelor prin inteligență artificială — cu un impact minim asupra nucleului tranzacțional existent.
- **O4 — Evoluția către o arhitectură *multi-tenant*:** Modelul conceptual actual permite adaptarea arhitecturii pentru a deservi instanțe izolate din punct de vedere logic (multi-clinică), asigurând segregarea strictă a datelor sensibile conform standardelor *enterprise*.

#### Amenințări

Următorii factori externi și structurali reprezintă riscuri identificate care pot afecta viabilitatea pe termen lung a soluției:

- **T1 — Presiunea concurențială din partea platformelor consacrate:** Soluțiile *software* internaționale mature, dispunând de resurse substanțiale de dezvoltare, pot implementa module specializate similare, diminuând decalajul tehnologic identificat și valoarea adăugată a propunerii curente.
- **T2 — Dinamica reglementărilor legislative și de conformitate medicală:** Modificările frecvente ale normelor privind protecția datelor sensibile de sănătate și alinierea la standardele viitoare ale Spațiului European al Datelor de Sănătate (EHDS) pot impune eforturi majore de refactorizare pentru menținerea conformității.
- **T3 — Complexitatea operațională a infrastructurii distribuite:** Administrarea, monitorizarea și orchestrarea unui ecosistem bazat pe microservicii implică costuri operaționale și o expertiză tehnică ce pot depăși capacitățile logistice ale clinicilor de dimensiuni mici și medii.
- **T4 — Dependența critică de furnizorul extern de identitate (*Identity Provider*):** Orice indisponibilitate sau compromitere a serviciului de autentificare extern suspendă accesul utilizatorilor în platformă, iar o eventuală migrare tehnologică ar presupune modificări la nivelul întregii stive de securitate.

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

Prioritizarea cerințelor sistemului KinetoCare utilizează metodologia MoSCoW pentru a structura riguros efortul de dezvoltare. Această clasificare separă funcționalitățile critice (*Must Have*) de cele secundare (*Should Have*), oportunități opționale (*Could Have*) și limitări asumate (*Won't Have*), conform detaliilor structurate în tabelul din Secțiunea 2.3.7.

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

## 2.4 Cerințele non-funcționale ale sistemului

Cerințele non-funcționale definesc atributele de calitate pe care platforma KinetoCare trebuie să le respecte dincolo de comportamentele funcționale descrise în secțiunea anterioară. Ele fundamentează deciziile de proiectare arhitecturală detaliate în capitolele 3 și 4 și reprezintă criteriile față de care implementarea poate fi evaluată obiectiv.

### 2.4.1 Securitate și protecția datelor

- **Autentificare externalizată:** Delegarea procesului de autentificare către un furnizor extern de identitate (*Identity Provider*), cu excluderea stocării sau prelucrării parolelor în bazele de date operaționale ale platformei.
- **Validare descentralizată (*Zero-Trust*):** Obligația fiecărui microserviciu intern de a valida independent identitatea și permisiunile solicitantului la fiecare cerere primită, eliminând presupunerea unei autentificări prealabile globale.
- **Securizarea jetoanelor pe client:** Stocarea securizată a jetoanelor de acces pe partea de client prin mecanisme inaccesibile contextului JavaScript (cookie-uri *HttpOnly*, *Secure*), prevenind riscul exfiltrării prin atacuri de tip *Cross-Site Scripting* (XSS).
- **Segregarea la nivel de schemă a bazelor de date:** Izolarea logică a bazelor de date (cu posibilitatea segregării fizice în producție prin instanțe dedicate) pentru identități, date clinice și mesageria de *chat*, asigurând că un compromis la nivelul unui subsistem nu afectează integritatea celorlalte module.
- **Autorizare timpurie la nivelul API *Gateway*:** Blocarea cererilor neautentificate și validarea timpurie a permisiunilor bazate pe roluri la punctul unic de intrare (*API Gateway*), înainte de rutarea solicitărilor către serviciile interne.

### 2.4.2 Scalabilitate și performanță

- **Scalabilitate orizontală independentă:** Proiectarea decuplată a microserviciilor pentru a permite scalarea orizontală și alocarea de resurse computaționale specifice fiecărui modul funcțional, fără a afecta disponibilitatea celorlalte componente.
- **Agregarea datelor prin tiparul BFF (*Backend-for-Frontend*):** Consolidarea datelor din multiple microservicii într-un singur răspuns unificat la nivelul *API Gateway*-ului, cu minimizarea numărului de conexiuni HTTP inițiate de aplicația client.
- **Optimizarea consumului de resurse pe client:** Suspendarea automată a operațiunilor asincrone de fundal (precum interogările periodice pentru notificări) atunci când interfața aplicației nu se află în focusul activ al utilizatorului.
- **Performanță și latență orientativă (*Design Target*):** Arhitectura sistemului este proiectată pentru a susține timpi de răspuns optimi, cu o țintă internă de sub 2 secunde pentru operațiunile standard de interogare. Acest obiectiv este urmărit strict arhitectural prin procesare asincronă, tiparul *BFF* și agregare concurentă a datelor; validarea sub sarcină masivă reprezintă o etapă ulterioară de evoluție a platformei.

### 2.4.3 Integritatea și consistența datelor

- **Tranzacționalitate locală ACID:** Garantarea execuției atomice, consistente, izolate și durabile (ACID) a operațiunilor ce modifică starea clinică sau alocă resurse concurente (precum ocuparea unui interval orar), cu asigurarea rollback-ului complet în caz de eroare.
- **Consistență eventuală prin mecanisme de compensare:** Implementarea tiparului *Saga* sau a tranzacțiilor compensatorii pentru fluxurile distribuite multi-servicii, asigurând revenirea la o stare consistentă în caz de eșec parțial, fără blocaje sincrone de durată.
- **Decuplarea proceselor asincrone:** Separarea fluxurilor secundare (cum ar fi expedierea notificărilor) de tranzacția principală prin intermediul unei cozi de mesaje, garantând că un eșec pe canalul de notificare nu blochează finalizarea cu succes a operațiunii clinice de bază.

### 2.4.4 Reziliență și toleranță la erori

- **Degradare controlată a serviciului (*Graceful Degradation*):** Prevenirea propagării în lanț a erorilor; indisponibilitatea unui microserviciu secundar nu trebuie să afecteze restul platformei, sistemul oferind funcționalitate parțială și informând utilizatorul într-un mod inteligibil.
- **Tratarea uniformă și mascată a erorilor:** Standardizarea formatului de eroare transmis către client și maparea excepțiilor tehnice interne în mesaje cu context de *business* inteligibil, cu blocarea scurgerii detaliilor de infrastructură sau a urmelor de stivă (*stack traces*).
- **Izolarea erorilor în interfață (*Error Boundaries*):** Implementarea de bariere de eroare la nivelul componentelor din *frontend*, cu prevenirea prăbușirii întregii pagini în cazul defectării unei singure secțiuni vizuale.
- **Gestiunea mesajelor eșuate (*Dead Letter Queue* — DLQ):** Direcționarea automată a mesajelor asincrone corupte sau neprocesabile într-o coadă de carantină după epuizarea numărului stabilit de reîncercări, prevenind blocarea cozii principale de distribuție.

### 2.4.5 Auditabilitate și trasabilitate

- **Jurnalizarea modificărilor de stare clinică:** Înregistrarea automată a oricărei modificări aduse dosarului medical al pacientului — crearea sau actualizarea fișelor, modificarea relației terapeutice, anulări — cu marcaj temporal de precizie și identitatea operatorului, asigurând trasabilitatea auditabilă a datelor clinice.
- **Prevenirea alterării datelor tarifare istorice:** Păstrarea asocierii imutabile dintre o programare și prețul, durata sau tipul serviciului de la data rezervării, izolând tranzacțiile finalizate de actualizările ulterioare ale catalogului global de servicii.

### 2.4.6 Portabilitate și mentenabilitate

- **Containerizare și paritate a mediilor:** Pachetizarea fiecărui serviciu sub formă de container Docker, cu garantarea consistenței absolute a comportamentului aplicației între mediile locale de dezvoltare, testare și producție.
- **Configurabilitate externă (*12-Factor App*):** Externalizarea completă a parametrilor de mediu (adrese de rețea, credențiale, secrete API) din codul sursă și injectarea acestora dinamic la pornirea containerului, eliminând necesitatea recompilării.
- **Cuplaj slab și autonomia datelor:** Restricționarea accesului la date exclusiv prin API-urile expuse oficial de fiecare microserviciu, cu interzicerea interogărilor directe *cross*-baze de date, în vederea menținerii unei separări riguroase a responsabilităților (*Bounded Contexts*).

### 2.4.7 Uzabilitate

- **Design receptiv (*Responsive Web Design*):** Optimizarea completă a interfeței grafice pentru dispozitive mobile, cu asigurarea unei experiențe tactile excelente în special pentru fluxurile critice ale pacientului (creare programări, completarea feedback-ului și citirea notificărilor).
- **Validare contextuală a formularelor:** Afișarea erorilor de validare a datelor de intrare în imediata proximitate a câmpului care le-a generat, cu evitarea utilizării casetelor de erori generalizate la nivel de ecran.
- **Prevenirea acțiunilor duplicate (*Request Idempotency UI*):** Dezactivarea elementelor interactive de control și afișarea unui indicator vizual de încărcare în timpul operațiunilor tranzacționale de durată, eliminând riscul transmiterii de cereri duplicate către *backend*.
