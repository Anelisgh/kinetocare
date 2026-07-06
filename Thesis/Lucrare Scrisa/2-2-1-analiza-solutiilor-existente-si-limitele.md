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
