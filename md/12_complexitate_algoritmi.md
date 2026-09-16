## Secțiunea 12: Complexitatea Algoritmilor și Evidențieri Tehnice (Algorithmic Complexity & Engineering Highlights)
Această secțiune prezintă o analiză consolidată a celor mai importanți algoritmi și funcții din baza de cod KinetoCare — implementări care depășesc operațiunile standard CRUD și demonstrează rezolvarea reală a unor probleme complexe de inginerie software. Acestea sunt clasificate în funcție de complexitatea lor algoritmică, nu după lungimea codului sau numărul de linii.
### 12.1 Automat Finit: `determinaServiciulCorect`
**Locație:** `ProgramareService.determinaServiciulCorect()` (programari-service)
**Model (Pattern):** Automat Finit Implicit (Finite State Machine - FSM) / Arbore de Decizie
Acesta este cel mai semnificativ algoritm din sistem. La baza sa, implementează un **automat finit cu trei stări** care determină în mod automat traiectoria terapeutică a unui pacient — fără nicio intervenție manuală din partea terapeutului sau a pacientului. De fiecare dată când un pacient își programează o ședință, sistemul deduce ce serviciu clinic ar trebui aplicat în funcție de starea curentă din ciclul său de recuperare:
```
Starea A: [Nu există nicio evaluare]                  -> Serviciu: "Evaluare Initiala" (Initial Assessment)
Starea B: [Există evaluare, ședințe efectuate < cotă] -> Serviciu: evaluare.serviciuRecomandatId (Tratament Activ)
Starea C: [Există evaluare, ședințe efectuate >= cotă]-> Serviciu: "Reevaluare" (Reassessment)
```
Tranzițiile de stare sunt conduse de două elemente de date: (1) dacă există o evaluare înregistrată pentru pacient și (2) o comparație între rezultatul `countSedintePacientDupaData` (ședințele finalizate de la data evaluării) și `evaluare.getSedinteRecomandate()` (cota prescrisă de terapeut). Automatul Finit are un **caracter auto-ciclat (self-cycling)**: după o reevaluare, se creează o nouă înregistrare `Evaluare` cu o cotă proaspătă `sedinteRecomandate`, iar pacientul reintră în Starea B — generând o buclă de tratament care se închide doar atunci când terapeutul decide să nu mai prescrie alte ședințe.
Acest design elimina o întreagă clasă de erori umane — de exemplu, un recepționer care selectează greșit tipul de serviciu sau un pacient care este facturat pentru o evaluare când ar trebui să primească o ședință obișnuită. Sistemul impune corectitudinea clinică direct la nivel de infrastructură.
*(Walkthrough-ul complet al algoritmului se află în Secțiunea 3.4)*
### 12.2 Planificarea Sloturilor prin Algoritm Greedy: `getSloturiDisponibile`
**Locație:** `ProgramareService.getSloturiDisponibile()` (programari-service)
**Model (Pattern):** Algoritm Greedy / Planificarea Intervalelor pe Bază de Constrângeri
Această metodă rezolvă o problemă complexă de planificare: date fiind programul de lucru al terapeutului, programările existente și durata serviciului solicitat, calculează toate sloturile de timp valide unde poate fi plasată o nouă programare. Algoritmul operează sub forma unui **cursor de tip fereastră glisantă (sliding window cursor)** care parcurge ziua de lucru a terapeutului:
1. **Faza de colectare a constrângerilor:** Algoritmul adună mai întâi toate constrângerile — perioadele de concediu (care blochează întreaga zi), disponibilitățile (care definesc ferestrele de lucru per locație și zi a săptămânii) și rezervările existente (care ocupă intervale specifice).
2. **Faza de generare a sloturilor:** Un cursor pornește de la `orar.oraInceput()` și avansează cu `durataMinute + 10` minute la fiecare iterație (buffer-ul de 10 minute oferă terapeutului timp de tranziție între pacienți). La fiecare poziție, verifică:
   - **Constrângerea de graniță:** Slotul se încadrează în orele de lucru? (`!cursor.plusMinutes(durataMinute).isAfter(limitaSfarsit)`)
   - **Constrângerea de coliziune:** Slotul se suprapune cu vreo programare existentă? (Folosind testul canonic de suprapunere a intervalelor: `startNou < p.oraSfarsit && endNou > p.oraInceput`)
   - **Constrângerea temporală:** Dacă data este astăzi, slotul se află încă în viitor? (`cursor.isAfter(LocalTime.now())`)
Acesta este un **algoritm greedy** clasic — procesează candidații de la stânga la dreapta și emite sloturile valide fără backtracking. Verificarea coliziunilor cu programările existente rulează în O(N) per slot (unde N = numărul de programări din acea zi), rezultând o complexitate generală O(S x N), unde S = numărul de sloturi candidate. Având în vedere parametrii clinici obișnuiți (zi de lucru de 8 ore, ședințe de 60 de minute), S ~= 8 și N < 10, algoritmul fiind extrem de eficient în practică.
*(Walkthrough-ul complet al algoritmului se află în Secțiunea 3.3, Faza 2)*
### 12.3 Fereastră Temporală cu Graniță la Miezul Nopții: `ReminderScheduler`
**Locație:** `ReminderScheduler.gasesteInFereastra()` (programari-service)
**Model (Pattern):** Căutare pe Fereastră Temporală cu Divizare la Graniță
Acest scheduler bazat pe cron trimite notificări proactive (remindere cu 24h și 2h înainte) pacienților. Eleganța algoritmică constă în gestionarea **cazului limită de la miezul nopții**: când fereastra de căutare (e.g., `[23:50, 00:10]`) trece dintr-o zi calendaristică în alta, o singură interogare SQL cu semantici `BETWEEN` ar eșua, deoarece condiția `23:50 < oraInceput < 00:10` este o imposibilitate logică pentru tipul `LocalTime`.
Soluția constă în divizarea ferestrei în două sub-interogări: `[23:50-23:59]` în ziua 1 și `[00:00-00:10]` în ziua 2, urmată de îmbinarea (merge) rezultatelor. Acest lucru garantează trimiterea tuturor notificărilor, indiferent de ora la care pacienții își stabilesc programările. Multe sisteme de planificare din producție eșuează în mod silențios la trecerea peste miezul nopții — această implementare rezolvă problema în mod explicit.
*(Walkthrough-ul complet al algoritmului se află în Secțiunea 3.6)*
### 12.4 Cron în Cascadă cu Re-verificare FSM: `finalizeazaProgramariExpirate`
**Locație:** `ProgramareService.finalizeazaProgramariExpirate()` + `trimiteNotificariDupaFinalizare()` (programari-service)
**Model (Pattern):** Procesare Batch Programată cu Efecte Secundare în Cascadă
Acest job cron adnotat cu `@Scheduled` face mult mai mult decât să marcheze programările ca fiind finalizate. După fiecare finalizare, declanșează o cascadă de efecte secundare în cadrul aceleiași granițe `@Transactional`:
1. **Activarea relației:** `asiguraRelatieActiva()` — garantează că relația pacient-terapeut există și este marcată ca activă (creând-o dacă aceasta este prima ședință finalizată cu succes între ei).
2. **Memento pentru jurnal:** Trimite evenimentul `notificare.reminder.jurnal` în RabbitMQ, invitând pacientul să își completeze jurnalul de ședință.
3. **Re-verificare FSM:** Rulează din nou logica de contorizare a ședințelor din `determinaServiciulCorect` pentru a detecta dacă pacientul și-a epuizat pachetul de tratament. Dacă `sedinteEfectuate >= sedinteRecomandate`, trimite evenimentul `notificare.reevaluare.recomandata` — informând proactiv pacientul că planul său de tratament este finalizat și că este necesară o reevaluare.
Acest al treilea pas este deosebit de elegant: jobul cron **re-evaluează efectiv starea FSM a pacientului** după finalizarea fiecărei ședințe, asigurându-se că sistemul deține o imagine mereu actualizată asupra progresului terapeutic — fără a fi nevoie de intervenție manuală din partea terapeutului.
*(Walkthrough-ul complet al algoritmului se află în Secțiunea 3.5)*
### 12.5 Saga Distribuită: `UserService.toggleUserActive`
**Locație:** `UserService.toggleUserActive()` (user-service)
**Model (Pattern):** Modelul Saga Orchestrat / Tranzacții Compensatorii
Atunci când un administrator dezactivează contul unui utilizator, sistemul trebuie să propage această schimbare de stare în **patru sisteme independente**, într-o succesiune coordonată:
1. **DB Local:** `user.setActive(false)` — actualizează baza de date locală.
2. **Keycloak IAM:** `keycloakSyncService.setUserEnabled(keycloakId, false)` — dezactivează contul în furnizorul de identitate, invalidând imediat toate tokenurile JWT active și blocând conectările viitoare. Dacă acest pas eșuează, metoda aruncă `ExternalServiceException`, ceea ce declanșează un rollback `@Transactional` în Spring pentru Pasul 1 — implementând o **tranzacție compensatorie** care previne apariția inconsistențelor între DB locală și Keycloak.
3. **Serviciul de Profil:** `pacientiClient.toggleActive()` sau `terapeutiClient.toggleActive()` — propagă starea dezactivată în serviciul dedicat rolului, împiedicând apariția contului în căutări sau liste de pacienți activi.
4. **Anularea programărilor:** `programariClient.cancelByTerapeut()` sau `cancelByPacient()` — anulează toate programările viitoare ale utilizatorului dezactivat, trimițând notificări prin RabbitMQ către toți partenerii afectați.
Aceasta este o implementare clasică a **modelului de design Saga** într-o arhitectură de microservicii. Decizia cheie de design este **granița de eșec (failure boundary)**: Pașii 1-2 sunt atomici (eșecul Keycloak anulează scrierea în DB), în timp ce Pașii 3-4 sunt bazați pe efort maxim (eșecurile sunt înregistrate în loguri, dar nu anulează dezactivarea). Această asimetrie reflectă o decizie asumată de business: este acceptabil ca un utilizator dezactivat să mai apară temporar într-o listă de pacienți (consistență eventuală), dar este inacceptabil ca un utilizator să fie dezactivat în Keycloak, dar să figureze ca activ în baza de date locală (consistență puternică pentru granița de autentificare).
*(Referință în Secțiunea 8.1.3)*
### 12.6 Puntea de Autentificare WebSocket: `StompSecurityInterceptor`
**Locație:** `StompSecurityInterceptor.preSend()` (chat-service)
**Model (Pattern):** Punte de Protocol (Protocol Bridge) / Injectarea Contextului de Securitate Thread-Local
Endpoint-urile HTTP beneficiază automat de lanțul de filtre din Spring Security — validarea JWT, extragerea rolurilor și popularea `SecurityContextHolder` având loc transparent. **Cadrele STOMP/WebSocket ocolesc complet acest flux.** `StompSecurityInterceptor` rezolvă această lacună arhitecturală prin implementarea unui `ChannelInterceptor` care interceptează fiecare cadru STOMP de intrare și construiește manual o punte pentru contextul de securitate:
1. **La cadrele CONNECT și SEND:** Extrage antetul `Authorization` din antetele native STOMP (nu HTTP), decodifică JWT-ul prin `jwtDecoder.decode(token)`, îl convertește într-un `JwtAuthenticationToken` și îl injectează atât în sesiunea STOMP (`accessor.setUser()`), cât și în contextul thread-local `SecurityContextHolder`.
2. **De ce este important `SecurityContextHolder`:** Metoda `ChatService.salveazaSiNotifica` din chat service apelează `programariClient.getRelatieStatusByKeycloak()` — un apel sincron Feign care are nevoie de un JWT valid pentru a se autentifica în `programari-service`. Interceptorul Spring `RequestInterceptor` configurat pe clientul Feign citește JWT-ul din `SecurityContextHolder.getContext()`. Fără popularea acestui context de către interceptorul STOMP, orice apel Feign lansat dintr-un handler WebSocket ar eșua cu eroarea `401 Unauthorized`.
3. **Prevenirea poluării thread pool-ului:** `postSend()` apelează `SecurityContextHolder.clearContext()` după fiecare cadru procesat, împiedicând scurgerea credențialelor unui utilizator către următorul mesaj procesat de același thread din pool.
Aceasta este o provocare complexă de integrare care apare în mod specific în arhitecturile ce combină **mesageria în timp real prin WebSocket** cu **autentificarea sincronă inter-servicii** — un scenariu pe care majoritatea documentațiilor standard nu îl acoperă.
*(Referință în Secțiunile 2.5 și 6.3)*
### 12.7 Cea mai Complexă Agregare de Date: `FisaPacientService.getFisaPacient()`
**Locație:** `FisaPacientService.getFisaPacient()` (programari-service)
**Model (Pattern):** Orchestrator Inter-Servicii cu Îmbogățire Imbricată N+1
Această metodă construiește cel mai bogat răspuns din întregul sistem — un dosar clinic complet al pacientului care reunește date de identitate, profil medical, evaluări clinice, note de evoluție, istoricul programărilor și feedback-ul subiectiv din jurnal. Orchestrează **4 microservicii diferite** și **4 interogări în baza de date locală**, având o buclă imbricată N+1 pentru evaluări care generează `3 + 2xN` apeluri HTTP externe la fiecare invocare. Fiecare apel extern este învelit defensiv în blocuri `try/catch` pentru o degradare elegantă a performanței, asigurându-se că eșecul temporar al unui serviciu afectează doar randarea unei singure secțiuni din dosarul pacientului, fără a bloca vizualizarea generală a fișei.
*(Analiza completă a agregării se află în Secțiunea 8.4.4)*
*— End of Section 12 —*
