### 2.4 Cerințele non-funcționale ale sistemului
Cerințele non-funcționale definesc atributele de calitate pe care platforma KinetoCare trebuie să le respecte dincolo de comportamentele funcționale descrise în secțiunea anterioară. Ele fundamentează deciziile de proiectare arhitecturală detaliate în capitolele 3 și 4 și reprezintă criteriile față de care implementarea poate fi evaluată.   
### 2.4.1 Securitate și protecția datelor   
- **Autentificare externalizată**: Delegarea procesului de autentificare către un furnizor extern de identitate (Identity Provider), excluzând stocarea sau prelucrarea parolelor în bazele de date operaționale ale platformei.   
- **Validare descentralizată (Zero-Trust)**: Obligația fiecărui microserviciu intern de a valida independent identitatea și permisiunile solicitantului la fiecare cerere primită, eliminând presupunerea unei autentificări prealabile globale.   
- **Securizarea jetoanelor pe client**: Stocarea securizată a jetoanelor de acces pe partea de client prin mecanisme inaccesibile contextului JavaScript (de exemplu, cookie-uri *HttpOnly*, *Secure*), prevenind riscul exfiltrării prin atacuri de tip Cross-Site Scripting (XSS).   
- **Segregarea la nivel de schemă a bazelor de date**: Izolarea logică a bazelor de date (cu posibilitatea segregării fizice în producție prin instanțe de baze de date dedicate) pentru identități, date clinice și mesageria de chat, asigurând că un compromis la nivelul unui subsistem nu afectează integritatea celorlalte module.   
- **Autorizare timpurie la nivel de API *Gateway***: Blocarea cererilor neautentificate și validarea timpurie a permisiunilor bazate pe roluri la punctul unic de intrare (API *Gateway*), înainte de rutarea solicitărilor către serviciile interne.   
    
### 2.4.2 Scalabilitate și performanță   
- **Scalabilitate orizontală independentă**: Proiectarea decuplată a microserviciilor pentru a permite scalarea orizontală și alocarea de resurse computaționale specifice fiecărui modul funcțional, fără a afecta disponibilitatea celorlalte componente.   
- **Agregarea datelor prin tiparul BFF (*Backend-for-Frontend*)**: Consolidarea datelor din multiple microservicii într-un singur răspuns unificat la nivelul API *Gateway*-ului, minimizând numărul de conexiuni HTTP inițiate de aplicația client.   
- **Optimizarea consumului de resurse pe client**: Suspendarea automată a operațiunilor asincrone de fundal (precum interogările periodice pentru notificări) atunci când interfața aplicației nu se află în focusul activ al utilizatorului.   
- **Performanță și latență orientativă (Design Target)**: Arhitectura sistemului este proiectată pentru a susține timpi de răspuns optimi (cu o țintă internă de sub 2 secunde pentru operațiunile standard de interogare). Acest obiectiv este urmărit strict arhitectural prin procesare asincronă, tipare *BFF* și agregare concurentă a datelor, validarea sub sarcină masivă reprezentând o etapă ulterioară de evoluție a platformei.   
    
### 2.4.3 Integritatea și consistența datelor   
- **Tranzacționalitate locală ACID**: Garantarea execuției atomice, consistente, izolate și durabile (ACID) a operațiunilor ce modifică starea clinică sau alocă resurse concurente (cum ar fi ocuparea unui interval orar), asigurând rollback-ul complet în caz de eroare.   
- **Consistență eventuală prin mecanisme de compensare**: Implementarea tiparului *Saga* sau a tranzacțiilor compensatorii pentru fluxurile distribuite multi-servicii, asigurând revenirea la o stare consistentă în caz de eșec parțial, fără blocaje sincrone de durată.   
- **Decuplarea proceselor asincrone**: Separarea fluxurilor secundare (cum ar fi expedierea notificărilor) de tranzacția principală prin intermediul unei cozi de mesaje, garantând că un eșec pe canalul de notificare nu blochează finalizarea cu succes a operațiunii clinice de bază.   
    
### 2.4.4 Reziliență și toleranță la erori   
- **Degradare controlată a serviciului (Graceful Degradation)**: Prevenirea propagării în lanț a erorilor; indisponibilitatea unui microserviciu secundar nu trebuie să afecteze restul platformei, sistemul oferind funcționalitate parțială și informând utilizatorul într-un mod prietenos.   
- **Tratarea uniformă și mascată a erorilor**: Standardizarea formatului de eroare transmis către client și maparea excepțiilor tehnice interne în mesaje cu context de business inteligibil, blocând scurgerea detaliilor de infrastructură sau a urmelor de stivă (stack traces).   
- **Izolarea erorilor în interfață (Error Boundaries)**: Implementarea de bariere de eroare la nivelul componentelor din front-end, prevenind prăbușirea întregii pagini în cazul defectării unei singure secțiuni vizuale.   
- **Gestiunea mesajelor eșuate (*Dead Letter Queue* - DLQ)**: Direcționarea automată a mesajelor asincrone corupte sau neprocesabile într-o coadă de carantină după epuizarea numărului stabilit de reîncercări, prevenind blocarea cozii principale de distribuție.   
    
### 2.4.5 Auditabilitate și trasabilitate   
- **Jurnalizarea modificărilor de stare clinică**: Înregistrarea automată a oricărei modificări aduse dosarului medical al pacientului (crearea sau actualizarea fișelor, modificarea relației terapeutice, anulări) cu marcaj temporal de precizie și identitatea operatorului, asigurând trasabilitatea auditabilă a datelor clinice.   
- **Prevenirea alterării datelor tarifare istorice**: Păstrarea asocierii imutabile dintre o programare și prețul, durata sau tipul serviciului de la data rezervării, izolând tranzacțiile finalizate de actualizările ulterioare ale catalogului global de servicii.   
    
### 2.4.6 Portabilitate și mentenabilitate   
- **Containerizare și paritate a mediilor**: Pachetizarea fiecărui serviciu sub formă de container Docker, garantând consistența absolută a comportamentului aplicației între mediile locale de dezvoltare, testare și producție.   
- **Configurabilitate externă (12-Factor App)**: Externalizarea completă a parametrilor de mediu (adrese de rețea, credențiale, secrete API) din codul sursă și injectarea acestora dinamic la pornirea containerului, eliminând necesitatea recompilării.   
- **Cuplaj slab și autonomie a datelor**: Restricționarea accesului la date exclusiv prin API-urile expuse oficial de fiecare microserviciu, interzicând interogările directe cross-baze de date pentru menținerea unei separări riguroase a responsabilităților (Bounded Contexts).   
    
### 2.4.7 Uzabilitate   
- **Design receptiv (Responsive Web Design)**: Optimizarea completă a interfeței grafice pentru dispozitive mobile, asigurând o experiență tactilă excelentă în special pentru fluxurile critice ale pacientului (creare programări, completarea feedback-ului și citirea notificărilor).   
- **Validare contextuală a formularelor**: Afișarea erorilor de validare a datelor de intrare în imediata proximitate a câmpului care le-a generat, evitând utilizarea casetelor de erori generalizate la nivel de ecran.   
- **Prevenirea acțiunilor duplicate (Request Idempotency UI)**: Dezactivarea elementelor interactive de control și afișarea unui indicator vizual de încărcare în timpul operațiunilor tranzacționale de durată, eliminând riscul transmiterii de cereri duplicate către *backend*.   
