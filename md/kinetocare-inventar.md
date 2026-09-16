# KinetoCare — Inventar de proiect

Document de lucru intern (nu text de portofoliu). Scop: fapte brute despre platformă, organizate clar, pentru a fi folosite ulterior la scrierea unui text de portofoliu.

Surse folosite: codul din `backend/` și `frontend/`, `README.md`, `docker-compose.yml`, `db-init/`, și documentul de disertație din `Thesis/` (în special `Thesis/Lucrare Scrisa/`).

---

## 1. Funcționalități, pe înțelesul unui om de business

### Pentru pacient

- **Programare online cu selecție automată a tipului de ședință.** Pacientul alege doar terapeutul, locația și ora — sistemul decide singur dacă ședința e "Evaluare inițială", un tratament obișnuit sau o "Reevaluare", pe baza istoricului lui. Elimină o sursă comună de eroare umană (pacientul sau recepția alegând greșit tipul de ședință).
- **Vizualizare ore disponibile în timp real.** La rezervare, pacientul vede doar orele chiar libere ale terapeutului ales — nu poate suprapune două programări, nu poate rezerva în concediul terapeutului.
- **Pagina principală (dashboard) cu situația curentă.** Diagnostic, serviciul activ și un indicator vizual de progres (ex. "3 din 5 ședințe până la reevaluare").
- **Jurnal de recuperare după fiecare ședință.** Pacientul notează, printr-un cursor simplu (scală 1-10), nivelul de durere, oboseală și dificultatea exercițiilor. Terapeutul vede aceste date centralizate ca grafic de evoluție.
- **Alegerea și schimbarea terapeutului preferat**, filtrabil după locație și specializare. Istoricul clinic (evaluări, jurnal) rămâne intact la schimbare — nu se pierde nimic din ce s-a documentat anterior.
- **Istoric complet al ședințelor** (programate, finalizate, anulate), cu tip și cost per ședință.
- **Chat direct cu terapeutul**, disponibil automat cât timp relația terapeutică e activă.
- **Notificări** pentru programări noi/anulate, remindere înainte de ședință, mesaje noi, recomandare de reevaluare.

### Pentru terapeut

- **Calendar interactiv** cu programările zilnice/săptămânale, cu marcaje vizuale pentru prima întâlnire cu un pacient și pentru neprezentări.
- **Fișa completă a fiecărui pacient** într-un singur loc: evaluări, reevaluări, notițe clinice proprii și — cel mai vizibil element — **grafice de evoluție generate automat** din jurnalele completate de pacient (durere, oboseală, dificultate exerciții, pe aceeași axă de timp).
- **Setarea programului de lucru** pe zile, ore și locație, plus blocarea concediilor — pacienții nu mai pot rezerva în acele intervale.
- **Statistici rapide**: programări active, pacienți care necesită reevaluare.
- **Listă de pacienți** (activi/arhivați), cu căutare și filtrare.
- **Notițe clinice private** — vizibile doar terapeutului care le-a scris (spre deosebire de evaluări, care sunt vizibile tuturor terapeuților implicați în îngrijirea pacientului, pentru continuitate).

### Pentru administrator (recepție/management clinică)

- **Gestiunea locațiilor clinicii** (adăugare, editare, dezactivare — istoricul rămâne intact, nu se șterge nimic din baza de date).
- **Catalogul de servicii și tarife** (tip, preț, durată). Modificarea unui preț nu schimbă retroactiv facturile/rapoartele vechi — prețul e "înghețat" la momentul programării.
- **Suspendarea conturilor**, cu anularea automată a programărilor viitoare aferente.
- **Panou de statistici per locație**: programări/lună, venituri, terapeuți activi, rată de anulări, pacienți noi/lună, programări per terapeut.
- La prima pornire a platformei se creează automat un cont de admin implicit — nu e nevoie de configurare manuală inițială.

### Funcționalități comune (toate rolurile)

- **Chat în timp real** pacient-terapeut (nu e nevoie să existe deja un mesaj ca să apară conversația — se creează automat de îndată ce relația clinică există).
- **Centru de notificări** cu istoric și marcare ca citit.
- **Resetare parolă** prin email, fără intervenție de administrator.

---

## 2. Arhitectura tehnică (referință internă — nu pentru text public)

Sistemul e o arhitectură de microservicii cu bază de date separată per serviciu (7 microservicii + un API Gateway + o librărie comună de cod partajat):

| Serviciu | Rol |
|---|---|
| `api-gateway` | Punct unic de intrare (Spring Cloud Gateway, WebFlux). Agregă cereri către mai multe servicii (tipar Backend-For-Frontend), proxy securizat către Keycloak la login. |
| `user-service` | Identitate: înregistrare, sincronizare cu Keycloak, bootstrap cont admin la prima pornire. |
| `pacienti-service` | Profil pacient + jurnalul de recuperare (durere/oboseală/dificultate). |
| `terapeuti-service` | Profil terapeut, program de lucru, locații, concedii, poză de profil. |
| `programari-service` | Cel mai complex — booking, automatul de stări pentru tipul de serviciu, evaluări/reevaluări clinice, statistici, joburi programate (cron) pentru finalizarea automată a ședințelor expirate. |
| `servicii-service` | Catalogul serviciilor medicale (tip, preț, durată). |
| `chat-service` | Mesagerie în timp real (WebSocket/STOMP). |
| `notificari-service` | Consumator RabbitMQ — trimite și gestionează notificările in-app. |
| `common` | Cod Java partajat între servicii (DTO-uri, utilitare). |

**Stack tehnologic:**
- Backend: Java 21, Spring Boot 3.x, Spring Cloud Gateway, WebFlux, OpenFeign, Spring Security (OAuth2 Resource Server), Spring AMQP, Spring Data JPA/Hibernate, MapStruct, Lombok.
- Frontend: React 19, Vite, React Router DOM v7, Axios, SockJS + StompJS (chat), Recharts (grafice), FullCalendar (calendar terapeut).
- Infrastructură: MySQL (o bază per serviciu), RabbitMQ (cu Dead Letter Queue pentru mesaje eșuate), Keycloak (identitate/OAuth2/JWT), Docker + docker-compose (mediu local complet), Kubernetes (orchestrare parțială — vezi mai jos).
- Frontend-ul de producție e livrat printr-un container Nginx separat (multi-stage Docker build).

**Decizii arhitecturale notabile** (utile pentru a explica "de ce a fost greu de construit", fără a fi folosite ca atare în text de portofoliu):
- **Zero-trust**: fiecare microserviciu validează independent JWT-ul (inclusiv pe conexiunile WebSocket/STOMP, prin interceptor custom), nu are încredere implicită în alte servicii.
- **Automat finit (FSM)** pentru determinarea automată a tipului de serviciu la rezervare — testat cu teste unitare pentru toate cele 3 stări.
- **Algoritm greedy (sliding window)** pentru generarea sloturilor orare disponibile, cu buffer de 10 minute între ședințe — testat pas cu pas.
- **Prevenire double-booking** prin blocare pesimistă la nivel de bază de date (`SELECT ... FOR UPDATE`), validată printr-un test de integrare cu 50 de fire de execuție concurente (49 eșuează cu conflict controlat, exact 1 reușește).
- **RabbitMQ cu Dead Letter Exchange** — mesajele eșuate (ex. JSON malformat) sunt carantinate, nu pierdute și nu blochează coada.
- **Tranzacții de compensare** la înregistrarea unui cont nou (scriere duală Keycloak + MySQL, cu rollback manual dacă un pas eșuează).
- **Soft-delete** pentru locații (niciodată `DELETE` — doar `isActive = false`), pentru a nu corupe istoricul programărilor.
- **Tipar snapshot** pentru prețuri — o modificare de tarif nu afectează retroactiv programările deja făcute.
- **Kubernetes** e configurat doar pentru un subset de servicii (`keycloak`, `user-service`, `pacienti-service`, `terapeuti-service`, `api-gateway`, `frontend`) — decizie asumată din motive de resurse locale limitate, nu o limitare de arhitectură.
- **Erori standardizate** (RFC 9457 / `ProblemDetail`) pe tot backend-ul, plus Error Boundaries și "smart polling" (oprire automată a interogărilor când tab-ul browserului e inactiv) pe frontend.

---

## 3. Ecrane recomandate pentru portofoliu (fără screenshot-uri — listă de referință)

Nu s-au făcut screenshot-uri reale în această sesiune (decizie explicită: aplicația nu a fost pornită local, ca să nu consume resurse/timp). Mai jos e o listă a ecranelor cu cel mai mare potențial vizual, identificate din componentele React existente, în ordinea probabilă a impactului într-un portofoliu:

1. **`AdminStatistici.jsx`** — panoul de business intelligence al adminului. Cel mai bogat vizual: mai multe grafice Recharts (venituri, achiziție pacienți, distribuția serviciilor) încărcate concurent. Cel mai bun candidat pentru un "hero shot".
2. **`FisaPacient.jsx` + `JurnalEvolutieChart.jsx`** — fișa pacientului din perspectiva terapeutului, cu graficul de evoluție a durerii/oboselii/dificultății suprapuse pe aceeași axă de timp. Cel mai reprezentativ ecran pentru "valoarea clinică" a produsului.
3. **`HomepageTerapeut.jsx` + `TerapeutCalendar.jsx`** — calendarul interactiv (FullCalendar) cu programările zilnice/săptămânale.
4. **`HomepagePacient.jsx`** — dashboard-ul pacientului, cu indicatorul de progres tratament și programarea următoare.
5. **`ProgramariPacient.jsx`** — fluxul de rezervare, cu sloturile disponibile afișate în timp real.
6. **`JurnalPacient.jsx`** — completarea jurnalului cu cursoare (sliders) 1-10.
7. **`ChatPacient.jsx` / `ChatTerapeut.jsx` + `FereastraChat.jsx`** — interfața de chat în timp real.
8. **`AdminLocatii.jsx` / `AdminServicii.jsx` / `AdminUsers.jsx`** — panourile administrative CRUD, utile ca ecrane secundare "de gestiune".
9. **`ProfilTerapeut.jsx` + `ManagementDisponibilitate.jsx` / `ManagementConcedii.jsx`** — configurarea programului de lucru și a concediilor.
10. **`LoginPage.jsx` / `RegisterPage.jsx`** — ecrane standard, utile doar ca "bookend" la începutul unui tur vizual.

**Cum se pornește local, pentru capturi ulterioare:**
```bash
docker-compose up --build
```
Fișierul `.env` există deja în rădăcina proiectului (necesar pentru parolele MySQL/RabbitMQ). Frontend-ul e expus pe `http://localhost:3000`. Contul de admin implicit, creat automat la prima pornire:
- Email: `admin@kinetoterapie.ro`
- Parolă: `Admin123!`

Atenție: build-ul complet (9 servicii Java + MySQL + Keycloak + RabbitMQ + frontend) poate dura 15-30+ minute la prima rulare și necesită resurse notabile de RAM/CPU.

---

## 4. Context de folosire reală

- **Punctul de plecare al proiectului e o clinică reală.** Conform README și capitolului 2 din disertație, platforma a fost "dezvoltată pornind de la analiza unei aplicații reale din domeniu și a nevoilor identificate împreună cu un kinetoterapeut" — cerințele funcționale sunt derivate din provocări operaționale reale (programări pe hârtie/telefon, lipsa feedback-ului între ședințe, pierderea contextului clinic la schimbarea terapeutului etc.), documentate explicit în secțiunea 2.1 a lucrării.
- **Nu există dovadă, în fișierele analizate, a unei validări cu utilizatori reali (terapeuți/pacienți) sau a unei susțineri publice deja avute.** Analiza SWOT din disertație (secțiunea 2.2.4) listează explicit ca punct slab asumat: *"W3 — Sistem nevalidat cu utilizatori reali din domeniu: Platforma nu a parcurs un ciclu de testare cu terapeuți și pacienți reali"*. Deci proiectul e fundamentat pe nevoi reale identificate împreună cu un kinetoterapeut, dar nu (încă) testat live cu pacienți.
- **Date demo existente, relativ elaborate — nu doar un seed generic.** În `db-init/` există:
  - `mock-data-june-july.sql` — un set de date demo acoperind un interval calendaristic (iunie-iulie), nu doar câteva rânduri aleatorii.
  - `add_alexandru_mock_data.py` — script dedicat pentru generarea de date pentru un pacient specific ("Alexandru"), sugerând un scenariu demo pregătit cu un "personaj" recurent.
  - `add_all_patients_mock_data.py` — generare de date mock pentru toți pacienții.
  - Acest nivel de detaliu (date pe interval de timp, pacient dedicat) indică pregătire pentru o demonstrație vizuală coerentă (grafice de evoluție cu istoric real, nu doar un rând gol).
- **Rigoare de testare peste medie pentru un proiect de disertație**, utilă ca argument de calitate: teste unitare pentru logica critică (automatul de decizie a serviciului, algoritmul greedy de sloturi), plus un test de integrare cu 50 de fire de execuție concurente care validează prevenirea suprapunerii de programări (double-booking) — documentat în capitolul 8 al disertației.
- **Limitări asumate explicit** (utile pentru onestitate în orice text public, ca să nu se supra-promită): fără modul de facturare/decontare CNAS, fără validare cu utilizatori reali, stocare pozelor de profil direct în baza de date (nu storage extern), fără migrare formală de schemă (Flyway/Liquibase) — toate documentate ca "datorii tehnice" în capitolul 9.
- **Documentul de disertație e organizat pe 9 capitole complete** (analiza domeniului, alegerea tehnologiilor, arhitectura sistemului, modelarea bazelor de date, algoritmi de decizie, interfața și fluxurile operaționale, validare/testare/performanță, limitări și direcții viitoare), plus anexe — deci proiectul are deja o documentare tehnică foarte completă, dincolo de cod.
