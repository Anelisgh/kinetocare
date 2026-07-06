# Capitolul 9. Limitări Arhitecturale și Direcții Viitoare

Pe parcursul dezvoltării platformei KinetoCare, au fost asumate în mod deliberat șase categorii de compromisuri tehnice: limitări de consistență distribuită în scenariile de scriere duală, limitări de performanță generate de asamblarea secvențială a dosarului clinic, limitări de stocare introduse de persistența resurselor binare în baza de date relațională, limitări de integrare fiscală și clinică, absența unui mecanism formal de versionare a schemelor relaționale și executarea operațiunilor de rețea în interiorul granițelor tranzacționale active. Secțiunile următoare documentează riguros fiecare dintre aceste datorii tehnice (*technical debt*), cu trimitere directă la implementarea concretă, și propun traiectoria de evoluție arhitecturală necesară pentru transformarea platformei dintr-o soluție funcțională validată la nivelul unui *Minimum Viable Product* (*MVP*) într-o platformă de nivel *enterprise*, comercializabilă ca serviciu (*Software-as-a-Service* — *SaaS*).

## 9.1 Limitări Arhitecturale și Tehnice Asumate

Proiectarea unui sistem distribuit presupune gestionarea constantă a compromisurilor între consistența datelor, disponibilitatea sistemului și complexitatea operațională. Următoarele limitări sunt recunoscute și documentate ca datorii tehnice asumate, fiecare fiind fundamentată pe o decizie pragmatică justificată de contextul de utilizare curent al platformei.

### 9.1.1 Consistența distribuită și riscul rezidual al scrierilor duale (*Dual-Write*)

Arhitectura de înregistrare a utilizatorilor, implementată în clasa `KeycloakService`, respectă o ordine strictă de tip *Keycloak-first*, structurată în patru faze: crearea contului în Keycloak (Faza 1), alocarea rolului de securitate în Keycloak (Faza 2), persistența în baza de date locală MySQL prin `userRepository.save()` (Faza 3) și inițializarea profilului clinic în serviciile din aval prin `RestTemplate` (Faza 4).

Metoda este protejată de un mecanism de compensare sincronă: dacă inițializarea din Faza 4 eșuează, blocul `catch` apelează `deleteUserInKeycloak()`, iar excepția re-aruncată declanșează anularea automată (*rollback*) a tranzacției `@Transactional` din Spring, anulând persistența locală din Faza 3.

**Fereastra de inconsistență reziduală.** Scenariul de risc apare atunci când apelul de compensare `deleteUserInKeycloak()` însuși eșuează — de exemplu, din cauza unui *timeout* de rețea între aplicație și Keycloak. În acest caz, tranzacția locală MySQL este anulată cu succes (utilizatorul nu există în `user_db`), dar contul creat în Faza 1 rămâne activ în Keycloak, generând un „cont fantomă" în sistemul *IAM* (*Identity and Access Management*). Persoana va putea obține un jeton *JWT* valid, dar orice acces la resursele platformei va returna erori HTTP de tip *404 Not Found*, deoarece profilul local nu există.

Frecvența de apariție a acestui scenariu este practic neglijabilă în contextul curent al unei singure clinici, întrucât ratele de înregistrare simultană sunt reduse și *timeout*-urile Keycloak sunt extrem de rare în rețele interne containerizate. Un mecanism de alertă este deja implementat: metoda `deleteUserInKeycloak()` loghează explicit un mesaj de avertizare administrativă, oferind un punct de audit clar pentru curățarea manuală a conturilor fantomă reziduale. Această soluție operațională a fost preferată unui protocol distribuit riguros (detaliat în Secțiunea 9.2.2), deoarece acesta din urmă ar adăuga o complexitate disproporționată față de frecvența reală de apariție a eșecului.

### 9.1.2 Agregarea fișei clinice și limitarea asamblării secvențiale

Microserviciul `programari-service` acționează ca orchestrator în construirea dosarului clinic integrat al pacientului, executând multiple apeluri de rețea. O optimizare importantă a fost deja realizată prin integrarea endpoint-ului *batch* `getUsersByKeycloakIds` din `user-service` în logica de listare a evaluărilor din metoda ajutătoare `buildEvaluariList`, eliminând interogările secvențiale ale identităților terapeuților.

Cu toate acestea, datoria tehnică reziduală legată de interogările de tip *N+1 Query Problem* persistă în alte două puncte ale aceleiași componente:
1. **În `buildEvaluariList` pentru servicii:** Pentru fiecare dintre cele *N* evaluări, sistemul execută în buclă un apel sincron către `serviciiClient.getServiciuById` pentru a traduce identificatorul serviciului recomandat în denumirea sa comercială.
2. **În `buildPatientList` pentru listarea pacienților unui terapeut:** Pentru fiecare pacient din listă (*N* în total), serviciul apelează individual `userClient.getUserByKeycloakId` pentru a citi numele pacientului și `programareService.getSituatiePacient` pentru a determina progresul planului terapeutic curent, generând latențe liniare cumulate la nivel de rețea.

Deși impactul asupra performanței este redus în contextul clinic curent, această asamblare secvențială devine un blocaj semnificativ în scenariile cu date longitudinale masive, caracteristice unei platforme cu sute de pacienți și ani de istoric clinic.

### 9.1.3 Ineficiența stocării resurselor binare în baza de date

O decizie de simplificare asumată în mediul de dezvoltare a constat în stocarea fotografiilor de profil ale terapeuților direct în schema `terapeuti_db`, codificate ca șiruri *Base64* în coloane de tip `MEDIUMTEXT` (așa cum este detaliat în Secțiunea 5.3.2). Această abordare constituie un *anti-pattern* arhitectural cu implicații directe asupra performanței motorului InnoDB.

Datele binare voluminoase stocate în `MEDIUMTEXT` sunt gestionate de InnoDB printr-un mecanism de stocare în pagini excedentare (*overflow pages*), ceea ce fragmentează paginile de date principale și reduce eficiența accesului secvențial. O problemă și mai critică este aceea că datele nesemantice populează inutil *Buffer Pool*-ul motorului InnoDB — mecanismul principal de *cache* — concurând pentru memorie cu paginile tabelelor tranzacționale frecvent accesate. Orice interogare care returnează o listă de terapeuți va transfera și volumul masiv al imaginilor *Base64* prin stratul de rețea internă, degradând atât latența rețelei, cât și performanța de serializare JSON.

Soluția arhitecturală propusă pentru această limitare este detaliată în Secțiunea 9.2.5.

### 9.1.4 Limitări de integrare: Interoperabilitatea fiscală și clinică

Analiza SWOT din Secțiunea 2.2.4 a identificat ca punct slab major absența unui modul de facturare și decontare. Această limitare transcende simpla lipsă a unor tabele adiționale — integrarea cu sistemele naționale (sistemul e-Factura gestionat de ANAF sau decontările CNAS) presupune redesenarea arhitecturală a granițelor de domeniu.

Un modul fiscal funcțional ar impune introducerea unui microserviciu complet izolat (`facturare-service`), conectat asincron prin RabbitMQ la componenta `programari-service`. Această decuplare este obligatorie pentru a garanta că disponibilitatea temporară redusă a API-urilor guvernamentale (o caracteristică recurent documentată a infrastructurii fiscale naționale) nu blochează fluxul clinic principal. Componenta de programări ar publica un eveniment de tip `ProgramareFinalizata` pe magistrala *AMQP*, iar serviciul fiscal ar consuma asincron evenimentul, generând documentul fiscal fără a introduce latență în confirmarea ședinței clinice.

De asemenea, o a doua lacună identificată în analiza SWOT — absența validării interfețelor printr-un ciclu de testare cu utilizatori reali — limitează certitudinea că fluxurile de lucru implementate sunt pe deplin adaptate nevoilor clinice efective, independent de corectitudinea lor tehnică și algoritmică.

### 9.1.5 Managementul ciclului de viață al schemelor relaționale

O limitare asumată a versiunii curente este absența unui instrument formal de versionare și migrare a bazelor de date (precum *Flyway* sau *Liquibase*). În stadiul actual, inițializarea schemelor se realizează prin scripturi statice la crearea volumelor Docker, iar integritatea structurală este verificată la pornire prin mecanismul Hibernate `ddl-auto=validate`. Acest mecanism respinge inițializarea și oprește execuția dacă detectează orice discrepanță între modelul de entități JPA compilat și schema fizică, prevenind lansarea microserviciului cu o schemă incompatibilă.

Această abordare este suficientă în contextul unui mediu de dezvoltare containerizat, unde schemele pot fi recreate la fiecare ciclu de pornire fără consecințe asupra datelor persistate. Într-un mediu de producție, unde datele clinice sunt ireversibile și tabelele nu pot fi distruse și recreate, absența unui instrument de migrare incrementală devine un risc operațional semnificativ. Instrumentele de migrare incrementală (*Flyway* sau *Liquibase*) versionează modificările de schemă ca fișiere SQL numerotate secvențial, executate o singură dată per mediu de execuție. Aceasta garantează trasabilitatea completă a evoluției structurale a bazei de date și permite alterarea tabelelor operaționale — adăugarea de coloane, modificarea indecșilor, introducerea constrângerilor noi — fără a distruge datele clinice persistate anterior.

### 9.1.6 Operațiuni sincrone de I/O în interiorul granițelor tranzacționale

O limitare de performanță recunoscută ca *anti-pattern* în arhitectura de microservicii constă în executarea apelurilor de rețea sincrone (prin *OpenFeign*) în interiorul unei granițe `@Transactional` active. În platforma KinetoCare, metoda `ProgramareService.creeazaProgramare()` este adnotată cu `@Transactional`, iar în interiorul ei, logica decizională efectuează apeluri sincrone către `servicii-service` pentru a prelua prețul și durata serviciului medical aplicabil.

Din perspectiva motorului de conexiuni HikariCP, această secvență generează un blocaj structural: la deschiderea graniței `@Transactional`, o conexiune din *pool*-ul bazei de date este achiziționată și reținută pe toată durata metodei. Dacă microserviciul destinație înregistrează latențe ridicate sau dacă rețeaua introduce întârzieri, conexiunea rămâne blocată în așteptarea răspunsului HTTP. Într-un scenariu de încărcare ridicată cu cereri concurente multiple, *pool*-ul de conexiuni al serviciului apelant se poate epuiza complet, determinând eșecuri în cascadă.

Soluția arhitecturală riguroasă constă în extragerea tuturor apelurilor externe înaintea deschiderii graniței tranzacționale, prin introducerea unui strat intermediar de tip *Facade*. Acesta ar colecta și rezolva datele externe necesare, transmițând rezultatele ca argumente explicite unei metode `@Transactional` strict delimitate la operațiunile de bază de date. Această refactorizare a fost identificată, dar amânată deliberat, deoarece încărcarea operațională estimată a unui cabinet kinetoterapeutic standard este insuficientă pentru a atinge pragul de epuizare al *pool*-ului de conexiuni în configurația HikariCP implicită.

## 9.2 Direcții Viitoare de Dezvoltare și Scalare

Transformarea platformei KinetoCare dintr-o soluție funcțională validată la nivelul unui *MVP* într-o platformă de nivel *enterprise*, capabilă să deservească simultan zeci de clinici independente la nivel național, implică implementarea a șase direcții strategice de evoluție arhitecturală.

### 9.2.1 Evoluția către o arhitectură Multi-Tenant (*SaaS*)

Arhitectura curentă modelează un sistem *single-tenant*, proiectat exclusiv pentru o singură clinică. Extinderea la nivel național presupune adoptarea unui model *Multi-Tenant*, în care zeci de clinici independente coexistă pe aceeași infrastructură, cu o izolare strictă a datelor.

Deoarece motorul de baze de date ales, MySQL, nu suportă nativ politici de securitate la nivel de rând (*Row-Level Security* — o caracteristică specifică PostgreSQL), strategia recomandată este **Schema-per-Tenant**: la procesul de înrolare (*onboarding*) al unei clinici noi, sistemul generează dinamic o schemă MySQL dedicată (de exemplu, `kinetocare_clinic_42`), izolând complet datele la nivel de schemă logică. Această abordare este preferată filtrării programatice (prin injectarea unui *tenant_id* în clauzele `WHERE` prin filtre Hibernate) deoarece elimină riscul de expunere accidentală a datelor *cross-tenant* printr-o eroare de filtrare — în modelul Schema-per-Tenant, o astfel de eroare logică nu poate traversa granița fizică a schemei. Costul operațional suplimentar (gestionarea zecilor de scheme în același cluster MySQL) este pe deplin justificat de garanția de securitate oferită în contextul datelor medicale protejate sub incidența Art. 9 GDPR.

Un identificator unic (`tenant_id`) va fi emis în jetonul *JWT* la autentificare și propagat în fiecare cerere prin antetele HTTP interne, permițând rutarea corectă a conexiunilor JDBC către schema corespunzătoare. La nivelul furnizorului de identitate, fiecare *tenant* va beneficia de un *Realm* Keycloak dedicat, asigurând izolarea completă a utilizatorilor, a rolurilor și a politicilor de autentificare.

### 9.2.2 Refactorizarea integrării prin tiparul *Transactional Outbox*

Eliminarea definitivă a riscului rezidual al conturilor fantomă din procesul de scriere duală (descris la Secțiunea 9.1.1) se realizează prin implementarea tiparului *Transactional Outbox*. Modificarea conceptuală este fundamentală: în loc ca componenta `user-service` să apeleze direct și sincron API-ul Keycloak, aceasta va salva datele utilizatorului concomitent cu un eveniment de tip `UserCreated` într-o tabelă locală `outbox`, în cadrul aceleiași tranzacții ACID MySQL. Atomicitatea este garantată de baza de date — ambele scrieri reușesc simultan sau niciuna nu este persistată.

Un proces de fundal independent — sau un instrument specializat de tip *Change Data Capture* (CDC) precum Debezium — va monitoriza continuu tabela `outbox` și va propaga garantat evenimentul către Keycloak, aplicând o semantică de tip *at-least-once delivery* cu reîncercări automate. Această arhitectură elimină complet fereastra de inconsistență din implementarea curentă, oferind consistență absolută (*Strong Consistency*) între sistemul de management al identității și baza de date locală.

### 9.2.3 Optimizarea orchestrării datelor prin utilizarea endpoint-urilor *Batch*

Rezolvarea limitării de performanță din `FisaPacientService` (Secțiunea 9.1.2) presupune refactorizarea arhitecturală a apelurilor secvențiale rămase:

1. **Pentru servicii în `buildEvaluariList`:** Identificatorii serviciilor recomandate vor fi colectați, duplicatele eliminate, urmate de invocarea unui singur endpoint de tip *batch* pe componenta `servicii-service` (`/servicii/batch`) pentru încărcarea detaliilor în lot, finalizată cu o mapare locală în memorie.
2. **Pentru listarea pacienților în `buildPatientList`:** Identificatorii Keycloak ai pacienților asociați terapeutului vor fi colectați într-un lot comun, declanșând un singur apel de tip *batch fetch* către `user-service`. Metoda `getSituatiePacient` va fi refactorizată pentru a primi o listă de identificatori și a realiza agregarea statistică direct în baza de date printr-o singură interogare SQL *bulk*.

Această optimizare reduce numărul de interacțiuni de rețea (*round-trips*) de la *N* apeluri secvențiale la un singur apel per resursă externă. Latența componentelor de rețea din agregare devine independentă de volumul de date istorice, asigurând o scalabilitate liniară.

### 9.2.4 Asistență AI pentru documentarea clinică (LLM *Self-Hosted*)

O lacună funcțională identificată față de soluțiile concurente mature este absența asistenței automate în documentarea clinică. Modulul actual stochează observațiile clinice sub formă de text liber nestructurat. Direcția viitoare propusă adaugă un strat de structurare automată în format SOAP, fără a necesita modificarea radicală a schemei existente. Platforma va integra un model lingvistic de dimensiuni mari (*Large Language Model* — *LLM*) pentru transcrierea și structurarea automată a observațiilor terapeutului în format **SOAP** (*Subjective, Objective, Assessment, and Plan*).

**Constrângere de conformitate GDPR:** Datele medicale prelucrate de platformă intră sub incidența restricțiilor stricte asupra transferurilor de date sensibile în afara infrastructurii Uniunii Europene. Din această cauză, utilizarea API-urilor comerciale externe (precum soluțiile bazate pe cloud public) este exclusă, deoarece procesarea implică infrastructuri terțe. Se va implementa un model cu ponderi deschise (*open-weight*, din familia Llama 3 sau echivalent), găzduit intern pe infrastructura proprie (*self-hosted*), utilizând un motor de inferență local.

Integrarea va fi realizată asincron: terapeutul va introduce observațiile brute (text sau voce), iar un apel asincron către motorul LLM intern va structura și returna documentul SOAP generat pentru revizuirea umană. Impactul asupra schemei bazei de date se rezumă la adăugarea coloanei `sursa_generare ENUM('MANUAL', 'AI')` în tabela `evolutii`, o condiție necesară pentru a garanta trasabilitatea și auditul actului decizional clinic.

### 9.2.5 Externalizarea stocării media (*Object Storage*)

Eliminarea ineficienței descrise la Secțiunea 9.1.3 se realizează prin decuplarea completă a resurselor binare de baza de date relațională, prin integrarea unui serviciu de stocare compatibil cu protocolul **S3** (de exemplu, o instanță MinIO *self-hosted* desfășurată în rețeaua internă, preferabilă din rațiuni de conformitate GDPR).

Modificarea arhitecturală presupune eliminarea coloanei `poza_profil MEDIUMTEXT` din tabela `terapeuti` și înlocuirea ei cu o referință externă `poza_profil_url VARCHAR(500)`. Microserviciul va salva resursa binară direct în stocarea de tip obiect (*Object Storage*) și va persista în MySQL exclusiv identificatorul uniform de resursă (URL). Această separare oferă trei avantaje imediate: eliminarea presiunii pe memoria tampon a motorului InnoDB, reducerea dimensiunii răspunsurilor API prin descărcarea imaginilor direct din stocarea externă (sau dintr-un *CDN*) și deschiderea infrastructurii pentru atașarea pe viitor a documentelor clinice complexe — investigații imagistice, rapoarte scanate sau analize medicale.

### 9.2.6 Garantarea idempotenței prin cache în memorie (*Deduplication Key*)

În arhitectura curentă, deduplicarea mesajelor AMQP a fost implementată cu succes prin utilizarea tabelei dedicate de deduplicare `mesaje_procesate`. La consumarea fiecărui mesaj, serviciul execută o instrucțiune SQL:

```sql
INSERT INTO mesaje_procesate (message_id, processed_at)
VALUES (:messageId, NOW())
ON DUPLICATE KEY UPDATE message_id = message_id;
```

Această metodă, bazată pe clauza `ON DUPLICATE KEY UPDATE` în MySQL InnoDB, oferă garanții tranzacționale de consistență puternică. Cu toate acestea, în condiții de sarcină extremă sau în scenarii de distribuție geografică a microserviciilor, interogările frecvente de inserare și actualizare pentru deduplicare pot genera o suprasolicitare a operațiunilor de I/O pe discul bazei de date relaționale.

Direcția viitoare de optimizare constă în **migrarea deduplicării către un strat de cache distribuit în memorie** (cum ar fi **Redis**). La recepționarea unui mesaj, consumatorul va verifica și seta atomicitatea cheii unice de deduplicare direct în Redis cu o valoare de expirare temporizată (*Time-To-Live* — *TTL*), de exemplu de 24 de ore (utilizând comanda `SET message_id 1 NX EX 86400`). Această schimbare va reloca verificările de idempotență de pe mediul de stocare persistent pe disc direct în memoria volatilă de înaltă performanță, reducând latența de la nivel de milisecunde la microsecunde și eliberând motorul MySQL de tranzacții neesențiale.