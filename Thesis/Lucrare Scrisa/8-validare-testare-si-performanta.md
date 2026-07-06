# Capitolul 8. Validare, Testare și Performanță

Într-o arhitectură distribuită bazată pe microservicii, simpla corectitudine logică a unui singur serviciu este o condiție necesară, dar insuficientă. Defectele cele mai grave apar nu în logica izolată a unei metode, ci la intersecția comportamentelor: la granița dintre doi consumatori concurenți care accesează aceeași resursă, la granița dintre un sistem de mesagerie asincronă și un consumator care poate fi repornit, sau la granița dintre un serviciu de identitate extern și baza de date locală. Strategia de validare adoptată în platforma KinetoCare este construită explicit în jurul acestor granițe.

Piramida clasică a testării este adaptată contextului distribuit: corectitudinea logicii de *business* pure este verificată prin teste unitare izolate, care validează comportamentul determinist al algoritmilor critici independent de orice infrastructură externă; rezistența mecanismelor de concurență tranzacțională este evaluată prin teste de integrare pe baza de date reală, care replică scenariile de acces simultan; validarea funcțională a fluxurilor de sistem — rutarea mesajelor, securitatea protocolului *WebSocket*, *rollback*-ul compensatoriu — este realizată prin analiza comportamentală a componentelor în configurații de producție; iar reziliența la nivel de infrastructură este confirmată de mecanismele Kubernetes de supraveghere a stării containerelor. Fiecare nivel adresează o categorie distinctă de risc și, în ansamblu, oferă o acoperire structurată a incertitudinilor arhitecturale fundamentale ale sistemului.

---

## 8.1 Testarea unitară a componentelor algoritmice critice

Testele unitare din platforma KinetoCare nu validează fluxuri de tip *happy-path*, ci contractele comportamentale ale celor mai critice componente algoritmice: mașina de stări finite și algoritmul *Greedy* de planificare. Ambele sunt izolate de orice infrastructură prin înlocuirea sistematică a tuturor celor unsprezece dependențe injectate ale microserviciului `programari-service` cu dubluri de test (*mocks*) administrate prin cadrul *Mockito*.

Clasa de test, `ProgramareServiceTest`, este adnotată cu `@ExtendWith(MockitoExtension.class)`, fără a porni niciun context *Spring*, ceea ce asigură un timp de execuție neglijabil și o izolare perfectă a comportamentului testat. Valorile injectate prin mecanismul `@Value` sunt aplicate retroactiv prin `ReflectionTestUtils.setField`, respectând același comportament ca în mediul de producție, fără a necesita un fișier de configurare extern.

### 8.1.1 Validarea automatului finit determinist (*FSM*)

Metoda `determinaServiciulCorect()` din microserviciul `programari-service` implementează un automat finit cu trei stări, descris detaliat în Secțiunea 6.1. Logica acestuia depinde de starea bazei de date (existența și conținutul evaluărilor clinice anterioare), ceea ce o transformă într-o funcție cu efecte laterale ale căror ramuri trebuie verificate exhaustiv și controlat.

Sunt definite trei cazuri de test care acoperă fiecare tranziție de stare posibilă:

**Cazul 1 — Starea `FARA_EVALUARE`:** Atunci când apelul spre `evaluareRepository` nu returnează nicio înregistrare (stare `Optional.empty()`), este așteptat ca metoda să solicite serviciul cu denumirea de „Evaluare Inițială", prin intermediul clientului `serviciiClient`. Testul verifică atât valoarea returnată, cât și interacțiunile exacte cu dublurile de test: se asigură că `evaluareRepository` este interogat și că `programareRepository` nu este apelat, confirmând că nu există accese redundante la persistență.

**Cazul 2 — Starea `IN_TRATAMENT`:** Când există o evaluare anterioară, iar numărul de ședințe efectuate de la data evaluării este strict mai mic decât numărul de ședințe recomandate (5 din 10), metoda trebuie să returneze serviciul recomandat explicit în evaluare, identificat prin `serviciuRecomandatId`. Testul validează că decizia este determinată de constrângerea numerică și că este apelat `serviciiClient.getServiciuById()`, nu metoda de căutare după denumire.

**Cazul 3 — Starea `NECESITA_REEVALUARE`:** Când numărul ședințelor efectuate este egal sau mai mare decât cel recomandat (10 din 10), sistemul trebuie să încadreze automat pacientul în serviciul de reevaluare. Testul confirmă că metoda solicită `serviciiClient.gasesteServiciuDupaNume("Reevaluare")`, validând că pragul de epuizare a planului terapeutic activează corect tranziția de stare.

### 8.1.2 Validarea algoritmului *Greedy* pentru generarea ferestrelor orare

Metoda `getSloturiDisponibile()` implementează un algoritm de tip fereastră glisantă (*sliding window*), descris în Secțiunea 6.2. Corectitudinea sa depinde de interacțiunea dintre mai mulți parametri simultan: intervalul orar al terapeutului, durata serviciului solicitat, programările existente și *buffer*-ul inter-ședință de 10 minute. Un test narativ este insuficient; este necesară verificarea derivată prin urmărirea pas cu pas a execuției algoritmului.

Scenariul de test este construit cu parametri aleși deliberat pentru a valida atât includerea, cât și excluderea unui slot:
- Intervalul de lucru al terapeutului: 08:00 – 12:00.
- Serviciu cu durata de 60 de minute.
- O programare existentă, deja ocupată: 09:00 – 10:00.

Evoluția așteptată a algoritmului este următoarea:
- *Iterația 1:* cursorul pornește la 08:00; sfârșitul intervalului (08:00 + 60 min) este 09:00; intervalul [08:00, 09:00) nu se suprapune cu [09:00, 10:00), deci slotul 08:00 este adăugat; cursorul avansează la 08:00 + 60 min + 10 min (buffer) = **09:10**.
- *Iterația 2:* cursorul este la 09:10; sfârșitul intervalului (09:10 + 60 min) este 10:10; intervalul [09:10, 10:10) se suprapune cu [09:00, 10:00), deci slotul este **respins**; cursorul avansează la **10:20**.
- *Iterația 3:* cursorul este la 10:20; sfârșitul intervalului (10:20 + 60 min) este 11:20; intervalul [10:20, 11:20) nu se suprapune cu nicio programare existentă, deci slotul 10:20 este **adăugat**; cursorul avansează la **11:30**.
- *Iterația 4:* cursorul este la 11:30; sfârșitul intervalului (11:30 + 60 min) ar fi 12:30, care depășește limita de 12:00; bucla se **oprește**.

Testul verifică faptul că lista returnată conține exact două elemente: `LocalTime.of(8, 0)` și `LocalTime.of(10, 20)`, confirmând că atât logica de includere, cât și mecanismul de avansare a cursorului cu *buffer* integrat sunt implementate corect.

---

## 8.2 Testarea de integrare a concurenței tranzacționale (Double-Booking)

Testarea mecanismelor de concurență nu poate fi realizată prin simulare la nivel de test unitar: utilizarea dublurilor de test (*mocks*) elimină tocmai comportamentul tranzacțional al bazei de date reale pe care se bazează garanțiile de corectitudine. Prin urmare, este aplicată o testare de integrare completă (*Full Integration Test*), configurată cu adnotările `@SpringBootTest` și `@ActiveProfiles("test")`, care pornește un context *Spring* complet și se conectează la o instanță MySQL reală, configurată prin profilul `application-test.properties`.

### 8.2.1 Metodologia testului de stres concurent

Clasa `DoubleBookingIntegrationTest` validează că mecanismul de blocare pesimistă (*Pessimistic Locking*), implementat prin instrucțiunea SQL `SELECT ... FOR UPDATE` în stratul de persistență al `programariRepository`, previne în mod fiabil înregistrarea concurentă a două programări suprapuse pentru același terapeut, la aceeași dată și oră.

Scenariul de stres este construit în jurul unui singur interval orar-țintă: o dată în viitor și ora 10:00. Sunt lansate simultan 50 de fire de execuție (*threads*), fiecare reprezentând un pacient distinct care încearcă să rezerve același interval. Sincronizarea perfectă a pornirii simultane este asigurată prin mecanismul `CountDownLatch`: toate cele 50 de fire de execuție se inițializează complet și se suspendă la un obstacol (`latch.await()`), iar apoi sunt eliberate simultan printr-un singur semnal (`latch.countDown()`). Acest mecanism creează condiția de cursă (*race condition*) maximă în mod reproductibil, transformând scenariul într-un test determinist al comportamentului sub sarcină extremă.

Toate dependențele externe (clienți *Feign*, publicatorul de notificări, decodorul *JWT*) sunt înlocuite cu dubluri de test prin adnotarea `@MockitoBean`, izolând comportamentul testat exclusiv la nivelul interacțiunii dintre microserviciu și baza de date MySQL.

Fiecare fir de execuție categorisește rezultatul apelului către `programareService.creeazaProgramare()` în unul din trei contoare atomice:
- **`successCount`** — apelul s-a finalizat cu succes (programarea a fost creată).
- **`conflictCount`** — a fost aruncată o excepție de tip `ResourceAlreadyExistsException` sau `DataIntegrityViolationException`, indicând detectarea unui conflict de rezervare.
- **`otherErrors`** — a apărut o eroare neașteptată, necorespunzătoare niciunei categorii prevăzute.

Aserțiunile finale validează că:
1. Exact **1** fir de execuție a reușit (`successCount == 1`).
2. Exact **49** fire de execuție au eșuat cu un conflict (`conflictCount == 49`).
3. **0** erori neașteptate s-au produs (`otherErrors == 0`).
4. Baza de date conține **exact 1** înregistrare (`programareRepository.count() == 1`).

Combinarea acestor patru aserțiuni confirmă că sistemul garantează unicitatea rezervărilor (*double-booking prevention*) chiar și sub condiții de concurență maximă.

### 8.2.2 Managementul tranzacțiilor și ordinea de curățare a datelor

O constrângere arhitecturală distinctivă, impusă de specificul testelor de concurență, este incompatibilitatea cu strategia clasică de *rollback* a datelor de test. În testele *Spring* standard, adnotarea `@Transactional` la nivel de clasă de test înfășoară fiecare metodă `@Test` într-o tranzacție care este anulată automat la finalizare, lăsând baza de date curată. Această abordare este invalidă în contextul testelor de concurență: tranzacțiile *Spring* sunt stocate într-un *ThreadLocal*, iar firele de execuție secundare, pornite din corpul testului prin intermediul unui `ExecutorService`, nu moștenesc tranzacția firului principal. Operațiunile de scriere efectuate de cele 50 de fire de execuție se desfășoară, prin urmare, în afara oricărei tranzacții de test și nu pot fi anulate automat.

Curățarea bazei de date între execuțiile testelor se realizează manual, prin metode dedicate adnotate cu `@BeforeEach` și `@AfterEach`, care invocă explicit `deleteAll()` pe ambele *repository*-uri implicate.

Ordinea de ștergere respectă o constrângere critică impusă de integritatea referențială a schemei `programari_db`: tabelele `evolutii` și `evaluari` conțin chei externe care referențiază tabelul `programari`. Dacă ștergerea ar fi inițiată direct pe tabelul `programari`, motorul InnoDB ar respinge operațiunea cu o eroare de tip `ConstraintViolationException`, detectând înregistrări copil orfane. Ordinea corectă impune ștergerea entităților dependente înaintea entității referențiate:

```
evaluareRepository.deleteAll()  →  programareRepository.deleteAll()
```

Această ordine este implementată în ambele metode de ciclu de viață, asigurând că baza de date se află într-o stare complet curată la începutul fiecărei rulări de test, indiferent de ce a lăsat execuția anterioară.

### 8.2.3 Mecanismul de blocare pesimistă și rezultatele aserțiunilor

Mecanismul care face posibilă trecerea cu succes a tuturor celor patru aserțiuni este `SELECT ... FOR UPDATE`, o instrucțiune SQL care achiziționează un bloc exclusiv la nivel de rând (sau de *gap*, în funcție de granularitatea interogării motorului InnoDB). Aceasta transformă verificarea existenței unui conflict dintr-o simplă citire concurentă care ar admite anomalia *phantom read* într-o operațiune serializată, garantând că un singur fir de execuție deține controlul interogării la un moment dat.

Consecința directă este că, din cele 50 de fire de execuție lansate simultan, doar unul reușește să traverseze granița de verificare și să persiste înregistrarea. Restul de 49, pe măsură ce încearcă să achiziționeze blocul pe același rând sau set de rânduri vizate de interogare, primesc fie excepții de tip *deadlock detection* (în cazul conflictelor de blocare circulară), fie erori de tip *constraint violation* (în cazul constrângerii de unicitate compozite din indexul bazei de date). Ambele tipuri de excepție sunt tratate uniform în blocul `catch` dedicat din corpul *thread*-ului, incrementând contorul `conflictCount` și finalizând firul de execuție în mod controlat.

---

## 8.3 Validarea funcțională a infrastructurii distribuite

Dincolo de testele automatizate, o serie de mecanisme critice ale sistemului sunt validate funcțional prin analiza comportamentală a componentelor în configurația de producție. Aceste mecanisme vizează trei categorii de riscuri sistemice: pierderea silențioasă a mesajelor asincrone, penetrarea canalelor de comunicare persistente și inconsistența distribuită în scenariile de scriere duală.

### 8.3.1 Carantina mesajelor asincrone (*Dead Letter Queue*)

Topologia *RabbitMQ* implementată în platforma KinetoCare, descrisă în detaliu în Secțiunea 6.7, este proiectată explicit pentru a preveni pierderea silențioasă a mesajelor clinice. Mecanismul de carantinare poate fi declanșat prin publicarea deliberată a unui mesaj JSON malformat pe schimbătorul (*exchange*) `notificari.exchange`.

Atunci când `NotificareConsumer` încearcă să deserializeze un *payload* incompatibil cu contractul de tip `NotificareEvent`, *Jackson Object Mapper* aruncă o excepție de tip `MessageConversionException`. Stiva *Spring AMQP* interceptează această eroare și trimite un semnal de confirmare negativă (*NACK*) cu parametrul `requeue = false` către broker. Brokerul *RabbitMQ* consultă argumentul `x-dead-letter-exchange` al cozii principale `notificari.queue.v2` și redirecționează mesajul toxic către schimbătorul de carantinare `notificari.dlx`, de unde ajunge în coada `notificari.queue.dead`. `DeadLetterConsumer` preia mesajul din coada de carantinare, îl jurnalizează complet (antet și *payload*) și trimite un semnal *ACK* explicit, eliminând mesajul din coadă și prevenind orice recursivitate. Coada principală `notificari.queue.v2` continuă procesarea mesajelor valide fără nicio întrerupere de flux.

Interfața de administrare a brokerului *RabbitMQ* confirmă vizual: coada `notificari.queue.dead` incrementează numărul de mesaje carantinate, în timp ce coada principală rămâne operațională, validând corectitudinea topologiei de reziliență.

### 8.3.2 Validarea modelului *Zero-Trust* și a securității canalului *STOMP*

Securizarea canalului *WebSocket* *STOMP* reprezintă o provocare arhitecturală distinctă față de securizarea *endpoint*-urilor HTTP standard, deoarece *Spring Security* nu poate aplica în mod nativ filtrele *JWT* asupra comenzilor *STOMP*, care se propagă printr-o conexiune TCP persistentă. Validarea funcțională a mecanismului de securitate implementat în `StompSecurityInterceptor` este realizată prin verificarea comportamentului sistemului în două scenarii complementare.

**Scenariul de conectare legitimă:** Un client care furnizează un jeton *JWT* valid în antetul `Authorization` al cadrului *STOMP CONNECT* traversează interceptorul cu succes. `StompSecurityInterceptor` decodează jetonul prin `JwtDecoder`, construiește obiectul `JwtAuthenticationToken` și îl setează atât ca utilizator al sesiunii *WebSocket* (`accessor.setUser()`), cât și în `SecurityContextHolder`, propagând identitatea spre toate componentele *Spring Security* din aval. Clientul poate ulterior publica mesaje pe destinația `/app/chat.send` fără erori de autorizare.

**Scenariul de trimitere cu jeton expirat sau invalid:** Dacă un cadru *STOMP SEND* conține un jeton care nu poate fi decodat (expirat, semnat cu o cheie incorectă sau malformat), `StompSecurityInterceptor` capturează excepția, apelează `SecurityContextHolder.clearContext()` pentru a evita contaminarea *thread*-ului din *pool* și lasă atributul `user` al accesorului nedefinit. Controlerele *Spring WebSocket* încearcă să rezolve contextul de securitate al mesajului și, în absența unui principal valid, aruncă o excepție de autorizare. `WebSocketChatController`, prin adnotarea `@MessageExceptionHandler` combinată cu `@SendToUser("/queue/errors")`, capturează această excepție și rutează un mesaj de eroare structurat (un obiect JSON cu câmpul `error`) direct pe coada personalizată a utilizatorului. Componenta *frontend* `FereastraChat.jsx`, abonată la destinația `/user/queue/errors`, recepționează mesajul de eroare și îl afișează utilizatorului ca un banner de notificare, cu eliminare automată după 5 secunde. Conținutul mesajului nu ajunge niciodată persitat în sistem.

Mecanismul `postSend()` al interceptorului, responsabil cu apelul obligatoriu `SecurityContextHolder.clearContext()` după fiecare mesaj procesat, garantează că contextul de securitate nu se propagă inadvertent spre cereri ulterioare din același *thread* al *pool*-ului gestionat de *Spring WebSocket*.

### 8.3.3 Validarea mecanismului compensatoriu la înregistrare

Scenariul de consistență distribuită în care mecanismul compensatoriu este relevant este cel al eșecului parțial în procesul de înregistrare a unui utilizator nou, implementat în metoda `KeycloakService.registerUser()`. Logica de compensare este descrisă în detaliu în Secțiunea 6.6.2 și poate fi verificată funcțional prin inducerea controlată a unui eșec în Faza 4 a procesului (inițializarea profilului clinic prin `RestTemplate`).

Dacă apelul HTTP intern spre `pacienti-service` sau `terapeuti-service` (Faza 4) generează o excepție (de exemplu, prin oprirea deliberată a microserviciului destinație în mediul de dezvoltare), blocul `catch` global din `registerUser()` preia controlul și invocă `deleteUserInKeycloak()`, eliminând contul creat în Keycloak (Faza 1). Concomitent, propagarea excepției înspre exteriorul metodei `@Transactional` declanșează anularea automată a persistenței locale din Faza 3, prin mecanismul de *rollback* al *Spring Transaction Manager*.

Rezultatul observabil este consistent cu așteptările arhitecturale: utilizatorul nu există nici în Keycloak, nici în baza de date locală `user_db`. Mesajul de avertizare administrativă jurnalizat de `deleteUserInKeycloak()` oferă un punct de audit clar pentru identificarea manuală a oricăror conturi reziduale în scenariile rare în care chiar și apelul compensatoriu ar eșua. Acest risc rezidual și direcțiile arhitecturale de eliminare a sa sunt discutate în Secțiunile 9.1.1 și 9.2.2.

---

## 8.4 Validarea rezilienței în interfața utilizatorului (*Frontend*)

Reziliența unui sistem distribuit se măsoară nu doar prin corectitudinea componentelor individuale, ci și prin capacitatea interfeței utilizatorului de a izola defecțiunile și de a evita ca un eșec dintr-un modul să propage un ecran alb sau o pierdere completă de interactivitate. Platforma KinetoCare implementează două mecanisme de reziliență la nivelul *frontend*-ului, validate funcțional prin observarea comportamentului sistemului în scenarii de eșec deliberat.

### 8.4.1 Optimizarea traficului de rețea prin *Smart Polling*

Componenta `NotificationBell.jsx` implementează un mecanism de interogare periodică (*polling*) a *endpoint*-ului de notificări, cu un interval configurat de 30 de secunde. Fără o strategie de optimizare, acest interval ar genera cereri HTTP redundante pe toată durata de viață a sesiunii utilizatorului, inclusiv atunci când tab-ul de browser este inactiv și utilizatorul nu este prezent în față ecranului.

Mecanismul de optimizare utilizează *Page Visibility API* — o interfață standardizată a platformei web, expusă prin proprietatea `document.visibilityState` — pentru a sincroniza activitatea intervalului de *polling* cu vizibilitatea efectivă a tab-ului. Logica este implementată prin înregistrarea unui ascultător de eveniment pe `document.addEventListener('visibilitychange', handleVisibilityChange)`.

Când starea de vizibilitate trece la `'hidden'` (tab-ul este minimizat sau utilizatorul comută la alt tab), `handleVisibilityChange` anulează intervalul activ prin `clearInterval()` și eliberează referința sa stocată în `intervalRef.current`. Când starea de vizibilitate revine la `'visible'`, funcția execută imediat o interogare de sincronizare (`fetchCount()`) pentru a recupera notificările acumulate în perioada de inactivitate și pornește un nou interval de *polling* cu `setInterval()`. Mecanismul de curățare din funcția de *cleanup* a efectului *React* (`useEffect`) asigură că atât ascultătorul de eveniment cât și orice interval activ sunt eliberate la demontarea componentei, prevenind scurgerile de memorie.

Validarea funcțională constă în observarea traficului de rețea prin instrumentele de inspecție ale browser-ului: în tab-ul activ, cererile HTTP spre `/notificari/necitite` sunt emise regulat la 30 de secunde; în momentul comutării tab-ului în fundal, cererile încetează complet; la revenirea în tab, o cerere imediată de sincronizare este emisă, urmată de reluarea intervalului normal.

### 8.4.2 Izolarea eșecurilor prin *Error Boundaries* (tiparul *Bulkhead*)

În absența unui mecanism de izolare, o eroare de *runtime* JavaScript apărută în timpul redării (*render*) unei componente — de exemplu, o excepție de tip *TypeError* cauzată de o referință `null` pe datele primite de la un *API* — provoacă propagarea erorii în sus pe întregul arbore de componente *React*, ceea ce duce la demontarea completă a interfeței și afișarea unui ecran complet alb, fără niciun mesaj informativ pentru utilizator.

Componenta `ErrorBoundary`, implementată ca o clasă *React* extinsă din `React.Component`, utilizează metodele specifice de ciclu de viață `getDerivedStateFromError()` și `componentDidCatch()` pentru a intercepta erorile de *render* din subarborii pe care îi învelește. Aceasta implementează tiparul *Bulkhead* din ingineria rezilienței: izolează defecțiunile unui compartiment de celelalte, prevenind propagarea în cascadă.

Metoda `getDerivedStateFromError()` actualizează starea componentei cu `{ hasError: true, error }`, determinând randarea alternativă (*fallback UI*) la următorul ciclu de redare. Metoda `componentDidCatch()` jurnalizează eroarea și *stack trace*-ul de componente pentru instrumentele de diagnosticare. Interfața de rezervă afișată utilizatorului conține un mesaj de eroare clar și un buton de reîncărcare a paginii, înlocuind ecranul alb cu o experiență utilizator gestionabilă.

În configurația curentă a platformei, `ErrorBoundary` învelește componenta rădăcină `<App />` în `main.jsx`, oferind protecție globală pentru întreaga aplicație. Principiul de izolare granulară al tiparului *Bulkhead* poate fi aplicat la nivel de modul (de exemplu, învelind separat componenta de chat sau modulul de calendar), astfel încât un eșec în modulul de mesagerie să nu perturbe vizibilitatea calendarului clinic sau a panoului de statistici, o evoluție arhitecturală discutată în Secțiunea 9.2.

---

## 8.5 Validarea rezilienței în infrastructura containerizată (Docker & Kubernetes)

Validarea corectitudinii unui sistem distribuit se extinde dincolo de codul aplicației până la infrastructura de execuție. Kubernetes, platforma de orchestrare adoptată de KinetoCare, integrează mecanisme native de supraveghere și auto-recuperare a containerelor care constituie, în sine, o formă de testare continuă a sănătății sistemului în mediul de producție. Aceste mecanisme sunt configurate în manifestele de deployment ale serviciilor care formează nucleul de producție al platformei.

### 8.5.1 Sănătatea containerelor prin mecanisme de auto-vindecare (*Liveness* și *Readiness Probes*)

Fiecare microserviciu Spring Boot integrat în clusterul Kubernetes este configurat cu două categorii distincte de sonde de sănătate (*health probes*), ambele conectate la *endpoint*-ul `/actuator/health` expus de modulul *Spring Boot Actuator*:

**Sonda de pregătire (*Readiness Probe*)** adresează o problemă specifică mașinilor virtuale Java: durata semnificativă de inițializare a contextului *Spring* (*application context startup*), care include conectarea la baza de date, inițializarea clientului *Keycloak* și compilarea filtrelor de securitate. Fără o sondă de pregătire, *kube-proxy* ar putea direcționa cereri HTTP reale spre un container al cărui microserviciu nu a finalizat încă inițializarea, rezultând în erori de tip *Connection Refused* sau *Service Unavailable* vizibile utilizatorilor finali.

Configurarea sondei de pregătire pentru `user-service` introduce un parametru `initialDelaySeconds: 60`, acordând procesului Java un interval de pornire fără intervenție. Ulterior, sonda interoghează `/actuator/health` la fiecare 10 secunde (`periodSeconds: 10`), cu un prag de eșec de 5 interogări consecutive eșuate (`failureThreshold: 5`) înainte ca serviciul să fie declarat nepregătit. Atâta vreme cât starea *Readiness Probe* este `FAILED`, *Kubernetes Service* exclude *pod*-ul respectiv din balansatorul de sarcină intern, asigurând că traficul este redirecționat exclusiv spre instanțele complet inițializate.

**Sonda de viabilitate (*Liveness Probe*)** adresează scenariile de blocare internă (*deadlock*, bucle infinite, epuizarea *pool*-ului de fire de execuție): situații în care procesul Java rulează, dar nu mai poate procesa cereri. Sonda de viabilitate pornește mai târziu decât cea de pregătire (`initialDelaySeconds: 90`) și interoghează același *endpoint* cu o perioadă mai mare (`periodSeconds: 15`). Dacă sunt detectate 3 eșecuri consecutive (`failureThreshold: 3`), *Kubernetes* termină forțat *pod*-ul și îl repornește automat, aplicând politica `restartPolicy: Always` implicită. Această capacitate de auto-vindecare conferă platformei reziliența necesară în mediile de producție cu sarcini variabile, eliminând necesitatea intervenției manuale pentru repornirea serviciilor blocate.

Combinarea celor două tipuri de sonde materializează, la nivel de infrastructură, conceptul de *Defense in Depth*: dacă un container nu este pregătit, nu primește trafic; dacă devine blocat, este reciclat automat.

### 8.5.2 Toleranța la defecte în scenarii de funcționare parțială (*Graceful Degradation*)

Configurația Kubernetes a platformei KinetoCare acoperă deliberat un subset al microserviciilor — nucleul de autentificare și identitate (`keycloak`, `user-service`), serviciile de profil (`pacienti-service`, `terapeuti-service`), *gateway*-ul API și *frontend*-ul — excluzând serviciile de orchestrare complexă (`programari-service`, `chat-service`, `notificari-service`). Această decizie de *design* este justificată în contextul unui mediu de demonstrație cu resurse de calcul locale limitate: Kubernetes este destinat mediilor de producție cu resurse generoase de server, iar rularea unui cluster complet local necesită o investiție semnificativă în memorie RAM și CPU, disproporționată față de scopul de validare conceptuală.

Importanța arhitecturală a acestei configurații parțiale rezidă în faptul că ea constituie un test de *Graceful Degradation* în condiții reale: validează că absența unor servicii secundare nu compromite funcționalitatea domeniilor izolate.

Această comportare este posibilă datorită decuplării stricte implementate pe mai multe niveluri:

**La nivel de *backend*:** Apelurile *Feign* spre serviciile lipsă (de exemplu, apelul din `pacienti-service` spre `programari-service` pentru anularea programărilor la schimbarea terapeutului preferat) sunt înfășurate în blocuri `try/catch` care absorb excepțiile de rețea. Serviciul apelant continuă execuția și persistă propria modificare (actualizarea profilului pacientului), ignorând în mod controlat eșecul apelului extern. Datele de profil sunt salvate cu succes; programările existente rămân neatinse în serviciul inaccesibil, o inconsistență temporară acceptată ca datorie tehnică în contextul MVP-ului, cu direcție de remediere prin tiparul *Transactional Outbox* (Secțiunea 9.2.2).

**La nivel de *frontend*:** Paginile care depind de serviciile absente (panoul de programări al pacientului, calendarul terapeutului) primesc răspunsuri de eroare HTTP de la *API Gateway*, care nu poate ruta cererile spre servicii nedisponibile. Componentele *React* tratează aceste răspunsuri prin blocuri de gestionare a erorilor la nivelul *hook*-urilor de tip `useEffect` și afișează mesaje informative utilizatorului, fără a provoca un eșec la nivel de aplicație. Dacă eroarea este de natura unui eșec de *render*, componenta globală `ErrorBoundary` interceptează excepția și redă *fallback*-ul de rezervă, limitând impactul la modulul respectiv și lăsând restul interfeței (autentificarea, profilul, motorul de căutare a terapeuților) complet operaționale.

Rezultatul observabil validează că arhitectura bazată pe separarea strictă a contextelor de domeniu (*Bounded Contexts*) și pe izolarea bazelor de date (*Database-per-Service*) conferă sistemului o reziliență naturală: defecțiunile rămân conținute în perimetrul domeniului afectat, fără efect de undă (*blast radius*) asupra domeniilor independente.
