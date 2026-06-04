# Capitolul 9. Limitări Arhitecturale și Direcții Viitoare

Pe parcursul dezvoltării platformei KinetoCare, trei categorii de compromisuri tehnice au fost asumate în mod deliberat: limitări de consistență distribuită în scenariile de scriere duală, limitări de performanță generate de asamblarea secvențială a dosarului clinic și limitări de stocare introduse de persistența resurselor binare în baza de date relațională. Secțiunile următoare documentează riguros fiecare dintre aceste datorii tehnice, cu trimitere directă la implementarea concretă, și propun traiectoria de evoluție arhitecturală necesară pentru transformarea platformei dintr-o soluție funcțională la nivelul unui cabinet într-o platformă de nivel enterprise, comercializabilă ca serviciu (*Software-as-a-Service*).

## 9.1 Limitări Arhitecturale și Tehnice Asumate

Proiectarea unui sistem distribuit presupune gestionarea constantă a unor compromisuri între consistența datelor, disponibilitatea sistemului și complexitatea operațională. Următoarele limitări sunt recunoscute și documentate ca datorii tehnice (*technical debt*) asumate, fiecare fundamentată de o decizie pragmatică justificată de contextul de utilizare curent al platformei.

### 9.1.1 Consistența distribuită și riscul rezidual al scrierilor duale (*Dual-Write*)

Arhitectura de înregistrare a utilizatorilor, implementată în clasa *KeycloakService*, respectă o ordine strictă de tip *Keycloak-first*, structurată în patru faze: crearea contului în Keycloak (Faza 1), alocarea rolului de securitate în Keycloak (Faza 2), persistența în baza de date locală MySQL prin *userRepository.save()* (Faza 3) și inițializarea profilului clinic în serviciile din aval prin *RestTemplate* (Faza 4).

Metoda este protejată de un mecanism de compensare sincronă: dacă inițializarea din Faza 4 eșuează, blocul *catch* apelează *deleteUserInKeycloak()*, iar excepția re-aruncată declanșează *rollback*-ul automat al tranzacției *@Transactional* din Spring, anulând persistența locală din Faza 3.

**Fereastra de inconsistență reziduală.** Scenariul de risc apare atunci când apelul de compensare *deleteUserInKeycloak()* însuși eșuează — de exemplu, din cauza unui *timeout* de rețea între aplicație și Keycloak. În acest caz, tranzacția locală MySQL este anulată cu succes (utilizatorul nu există în *user_db*), dar contul creat în Faza 1 rămâne activ în Keycloak, generând un „cont fantomă" în sistemul IAM. Persoana va putea obține un token JWT valid, dar orice acces la resursele platformei va returna erori de tip *404 Not Found*, deoarece profilul local nu există.

Frecvența de apariție a acestui scenariu este practic neglijabilă în contextul curent al unei singure clinici, întrucât ratele de înregistrare simultană sunt reduse și *timeout*-urile Keycloak sunt extrem de rare în rețele interne containerizate. Mecanismul de alertă este deja implementat: metoda *deleteUserInKeycloak()* loghează explicit mesajul *„S-ar putea să fie nevoie de eliminare manuală"* (linia 142), oferind administratorului un punct de audit clar pentru curățarea manuală a conturilor fantomă reziduale. S-a preferat această soluție operațională față de un protocol distribuit riguros (detaliat în §9.2.2), deoarece adaugă o complexitate disproporționată față de frecvența reală de apariție a eșecului.

### 9.1.2 Agregarea fișei clinice și limitarea asamblării secvențiale

Microserviciul *programari-service* acționează ca orchestrator în construirea dosarului clinic integrat al pacientului, executând un număr variabil de apeluri de rețea descris de formula $3 + 2 \times N$, unde N reprezintă numărul de evaluări istorice înregistrate (detaliat în §6.5.3).

O precizare arhitecturală importantă: limitarea nu constă în lipsa infrastructurii de procesare în lot. Componenta *user-service* expune deja metoda *getUsersByKeycloakIds(List<String> keycloakIds)*, care rezolvă o colecție de identificatori printr-o singură interogare SQL cu clauza *WHERE keycloak_id IN (...)*. Datoria tehnică reală constă în faptul că *FisaPacientService* nu integrează acest endpoint în fluxul de construire a listei de evaluări, apelând în continuare individual serviciul pentru fiecare evaluare din buclă.

Deși impactul de performanță este minim în contextul clinic curent (valoarea lui N depășește rar 3–4 evaluări pe an per pacient, generând sub 80 ms latență adițională în rețeaua internă), această limitare devine un blocaj semnificativ în scenariile cu date longitudinale masive, caracteristice unei platforme cu sute de pacienți și ani de istoric clinic.

### 9.1.3 Ineficiența stocării resurselor binare în baza de date

O decizie de simplificare a mediului de dezvoltare a fost stocarea fotografiilor de profil ale terapeuților direct în schema *terapeuti_db*, codificate ca șiruri *Base64* în coloane de tip `MEDIUMTEXT` (§5.3.2). Această abordare constituie un *anti-pattern* arhitectural cu implicații directe asupra performanței motorului InnoDB.

Datele binare voluminoase stocate în `MEDIUMTEXT` sunt gestionate de InnoDB printr-un mecanism de stocare *off-page* (*overflow pages*), ceea ce fragmentează paginile de date principale și reduce eficiența accesului secvențial. Mai critic, aceste date nesemantice populează inutil *Buffer Pool*-ul InnoDB — mecanismul de *cache* al motorului — concurând pentru memorie cu paginile frecvent accesate ale tabelelor tranzacționale. Orice interogare care returnează o listă de terapeuți va transfera și volumul imaginilor *Base64* prin stratul de rețea internă, degradând atât latența rețelei, cât și performanța de serializare JSON.

Soluția arhitecturală propusă pentru această limitare este detaliată în §9.2.5.

### 9.1.4 Limitări de integrare: Interoperabilitatea fiscală și clinică

Analiza SWOT din §2.2.4 a identificat ca punct slab major (**W1**) absența unui modul de facturare și decontare. Această limitare transcende simpla lipsă a unor tabele noi — integrarea cu sistemele naționale (e-Factura ANAF sau decontările CNAS) presupune redesenarea arhitecturală a granițelor de domeniu.

Un modul fiscal funcțional ar impune introducerea unui microserviciu complet izolat (*facturare-service*), conectat asincron prin RabbitMQ la *programari-service*. Această decuplare este obligatorie pentru a garanta că disponibilitatea temporară redusă a API-urilor guvernamentale (o caracteristică documentată a infrastructurii ANAF) nu blochează fluxul clinic principal. *programari-service* ar publica un eveniment de tip *ProgramareFinalizata* pe magistrala AMQP, iar *facturare-service* ar consuma asincron evenimentul, generând documentul fiscal fără a introduce latență în confirmarea ședinței clinice.

De asemenea, a doua lacună identificată în SWOT (**W3**) — absența validării interfețelor printr-un ciclu de testare cu utilizatori reali — limitează certitudinea că fluxurile de lucru implementate sunt adaptate nevoilor clinice efective, independent de corectitudinea lor tehnică.

## 9.2 Direcții Viitoare de Dezvoltare și Scalare

Transformarea KinetoCare dintr-o soluție funcțională la nivel de cabinet într-o platformă de nivel enterprise, capabilă să deservească simultan zeci de clinici independente la nivel național, implică șase direcții strategice de evoluție arhitecturală.

### 9.2.1 Evoluția către o arhitectură Multi-Tenant (*SaaS*)

Arhitectura curentă modelează un sistem *single-tenant*, proiectat pentru o singură clinică. Extinderea la nivel național presupune adoptarea unui model *Multi-Tenant*, în care zeci de clinici independente coexistă pe aceeași infrastructură, cu izolare strictă a datelor.

Deoarece motorul de baze de date ales, MySQL, nu suportă nativ politici de securitate la nivel de rând (*Row-Level Security* — caracteristică specifică PostgreSQL), strategia recomandată este **Schema-per-Tenant**: la procesul de *onboarding* al unei clinici noi, sistemul generează dinamic o schemă MySQL dedicată (de exemplu, *kinetocare_clinic_42*), izolând complet datele la nivel de schemă logică. Această abordare este preferată filtrării programatice prin *tenant_id* (injectare în clauze `WHERE` prin filtre Hibernate), deoarece elimină riscul de expunere accidentală a datelor *cross-tenant* printr-un *bug* de filtrare — în Schema-per-Tenant, un astfel de *bug* nu poate traversa granița de schemă. Costul operațional suplimentar (gestionarea zecilor de scheme în același cluster MySQL) este justificat de garanția de securitate oferită în contextul datelor medicale, care intră sub incidența Art. 9 GDPR.

Un identificator de tip *tenant_id* va fi emis în jetonul JWT la autentificare și propagat în fiecare cerere prin antetele HTTP interne, permițând rutarea corectă a conexiunilor către schema corespunzătoare.

### 9.2.2 Refactorizarea integrării prin tiparul *Transactional Outbox*

Eliminarea definitivă a riscului rezidual al conturilor fantomă din procesul de *dual-write* (descris la §9.1.1) se realizează prin implementarea tiparului *Transactional Outbox*. Modificarea conceptuală este fundamentală: în loc să apeleze direct API-ul Keycloak sincron, *user-service* va salva datele utilizatorului și un eveniment de tip *UserCreated* într-o tabelă locală *outbox*, în cadrul aceleiași tranzacții ACID MySQL locale. Atomicitatea este garantată de baza de date — fie ambele scrieri reușesc, fie niciuna nu este persistată.

Un proces de fundal independent — sau un instrument de tip *Change Data Capture* (CDC) precum **Debezium** — va monitoriza tabela *outbox* și va propaga garantat evenimentul către Keycloak, cu reîncercări automate și semantică de tip *at-least-once delivery*. Această arhitectură elimină complet fereastra de inconsistență din implementarea curentă, oferind consistență absolută (*Strong Consistency*) între IAM și baza de date locală.

### 9.2.3 Optimizarea orchestrării datelor prin utilizarea *endpoint*-urilor *Batch*

Rezolvarea limitării de performanță din *FisaPacientService* (§9.1.2) presupune o intervenție arhitecturală minoră cu impact major. Logica din metoda *buildEvaluariList* trebuie refactorizată în trei pași:

1. Colectarea tuturor identificatorilor unici *terapeutKeycloakId* din lista locală de evaluări, eliminând duplicatele.
2. Invocarea o singură dată a *endpoint*-ului de procesare în lot existent în *user-service*:
```json
POST /api/users/batch
{
  "keycloakIds": ["uuid-terapeut-1", "uuid-terapeut-2", "uuid-terapeut-3"]
}
```
3. Construirea unui *Map\<String, UserDisplayCalendarDTO\>* din răspuns și rezolvarea fiecărei evaluări prin *lookup* local în hartă, fără niciun apel de rețea suplimentar.

Această optimizare reduce numărul de *round-trip*-uri HTTP de la N apeluri secvențiale la un singur apel de rețea, indiferent de dimensiunea istoricului clinic al pacientului. Latența componentei de rețea din agregarea fișei devine astfel independentă de volumul de date istorice, scalând corect inclusiv pentru dosare cu zeci de evaluări longitudinale.

### 9.2.4 Asistență AI pentru documentarea clinică (LLM *Self-Hosted*)

O lacună funcțională identificată față de soluțiile concurente mature (§2.2.1, **W2**) este absența asistenței automate la documentarea clinică. Platforma va integra un model de tip *Large Language Model* (LLM) pentru transcrierea și structurarea automată a observațiilor terapeutului în format **SOAP** (*Subjective, Objective, Assessment, and Plan*), populând câmpul *observatii* din tabela *evolutii*.

**Constrângere de conformitate GDPR:** Datele medicale prelucrate de platforma KinetoCare intră sub incidența Art. 9 din Regulamentul (UE) 2016/679, care impune restricții stricte asupra transferurilor de date sensibile în afara infrastructurii Uniunii Europene. Din această cauză, utilizarea API-urilor comerciale externe (precum OpenAI sau Google Gemini) este exclusă, deoarece acestea procesează datele pe infrastructuri terțe. Se va opta pentru un model *open-weight* (familia **Llama 3** sau echivalent) găzduit intern (*self-hosted*) pe serverele platformei, utilizând o soluție de inferență locală de tip **Ollama** sau **llama.cpp**.

Integrarea va fi realizată asincron: terapeutul va dicta sau va introduce observațiile brute, iar un apel asincron la LLM-ul intern va structura și va returna documentul SOAP generat pentru revizuire și aprobare. Impactul asupra schemei bazei de date va fi minim, necesitând doar adăugarea coloanei `sursa_generare ENUM('MANUAL', 'AI')` în tabela *evolutii*, pentru a garanta trasabilitatea și auditul actului decizional clinic.

### 9.2.5 Externalizarea stocării media (*Object Storage*)

Eliminarea ineficienței descrise la §9.1.3 se realizează prin decuplarea completă a resurselor binare de baza de date relațională, prin integrarea unui serviciu de stocare compatibil cu protocolul **S3** (AWS S3 pentru instanțe cloud sau **MinIO** pentru instanțe *self-hosted* interne, preferabil din motive de conformitate GDPR).

Modificarea arhitecturală implică eliminarea coloanei `poza_profil MEDIUMTEXT` din tabela *terapeuti* și înlocuirea sa cu o coloană `poza_profil_url VARCHAR(500)`. Microserviciul va încărca imaginea direct pe *Object Storage* și va persista în MySQL exclusiv un URL opac. Această separare oferă trei beneficii imediate: eliminarea presiunii pe *Buffer Pool*-ul InnoDB, reducerea dimensiunii răspunsurilor API prin servirea imaginilor direct din CDN și deschiderea infrastructurii pentru atașarea la dosarul electronic al pacientului a documentelor clinice complexe — investigații imagistice RMN, electromiograme sau rapoarte medicale scanate.


### 9.2.6 Garantarea idempotenței prin tabelă dedicată de deduplicare (*Idempotency Key*)

Secțiunea §6.7.8 a descris o strategie de deduplicare bazată pe interogarea tabelei operaționale *notificari* pentru a verifica existența unei notificări cu același *tip* și *entitate_legata_id*. Această abordare, deși funcțional corectă, prezintă o limitare de scalabilitate: tabela *notificari* este o tabelă operațională cu volum crescut și acces concurent ridicat, iar interogările de deduplicare concurează cu operațiunile de scriere și citire ale fluxului principal.

Direcția de optimizare propusă constă în introducerea unei tabele dedicate *mesaje_procesate*, cu câmpurile `message_id VARCHAR(36) UNIQUE` și `processed_at DATETIME`, al cărui volum este menținut redus printr-un *job* periodic de curățare bazat pe *Time-To-Live*. Fiecare mesaj RabbitMQ va include un identificator unic (*message_id*), generat de producător la publicare. La consumare, *notificari-service* va tenta inserția noului identificator prin instrucțiunea:

```sql
INSERT INTO mesaje_procesate (message_id, processed_at)
VALUES (?, NOW())
ON DUPLICATE KEY UPDATE message_id = message_id;
```

Utilizarea clauzei `ON DUPLICATE KEY UPDATE` este deliberată și prezintă o garanție tehnică superioară față de alternativa `INSERT IGNORE`. Aceasta din urmă suprimă *orice* eroare SQL apărută la inserție — inclusiv trunchieri de date sau incompatibilități de tip de coloană — mascând potențiale defecte de integritate și transformându-le în simple avertismente (*warnings*) invizibile aplicației. `ON DUPLICATE KEY UPDATE` acționează exclusiv la conflictul de constrângere unică, lăsând toate celelalte erori să se propage normal către nivelul aplicației. Dacă instrucțiunea nu inserează niciun rând nou (zero *rows affected*), aplicația detectează duplicatul și absoarbe silențios mesajul, trimițând un semnal *ACK* către broker. Idempotența este astfel garantată fără niciun impact asupra tabelei operaționale principale.

Alternativ, în scenariile cu volum foarte ridicat, tabela *mesaje_procesate* poate fi înlocuită cu un cache **Redis** configurat cu un *TTL* corespunzător ferestrei de deduplicare, oferind verificări de idempotență în memorie cu latență de ordinul microsecundelor.

Prin combinarea acestor șase direcții de evoluție, platforma KinetoCare poate trece de la un prototip funcțional validat la un produs software de nivel enterprise, capabil să gestioneze date medicale la scară națională, cu garanții de consistență, securitate și performanță corespunzătoare standardelor industriei.
