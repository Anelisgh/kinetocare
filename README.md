# KinetoCare — Platformă Clinică cu Arhitectură Microservicii

Platformă software distribuită pentru gestionarea activității clinicilor de kinetoterapie, dezvoltată pornind de la analiza unei aplicații reale din domeniu și a nevoilor identificate împreună cu un kinetoterapeut.

Conectează pacienții cu terapeuții, acoperind end-to-end fluxul clinic: programări, evaluări, monitorizarea progresului și comunicare în timp real.

---

## 🎯 Funcționalități principale

### 👤 Modul Pacient
- **Programare online** cu selecție automată a serviciului (în funcție de etapa tratamentului) și vizualizarea sloturilor disponibile în timp real.
- **Pagina principală** cu diagnosticul curent, serviciul activ și numărul de ședințe rămase până la re-evaluare.
- **Jurnal de recuperare** completat după fiecare ședință (nivel durere, oboseală, dificultate exerciții).
- **Selectarea terapeutului preferat** pe baza locației și specializării.

### 👨‍⚕️ Modul Terapeut
- **Calendar interactiv** cu programările zilnice și săptămânale (FullCalendar).
- **Fișa completă a fiecărui pacient**: evaluări inițiale, re-evaluări și grafice de evoluție generate automat din jurnalele de recuperare.
- **Setarea programului de lucru** per locație și gestionarea concediilor.
- **Statistici rapide**: programări active, pacienți ce necesită re-evaluare.

### ⚙️ Modul Administrativ
- **Gestiunea locațiilor** și a catalogului de servicii (tip, preț, durată).
- **Dezactivarea conturilor** cu anularea automată a programărilor viitoare aferente.
- **Statistici agregate per locație**: programări, venituri, terapeuți activi, rată anulări.

### 🔄 Module comune
- **Chat bidirecțional** pacient-terapeut în timp real.
- **Sistem de notificări in-app** pentru programări, mesaje noi și remindere.

---

## 🏛 Arhitectură și Decizii Tehnice

### Arhitectură Microservicii (Database-per-Service)
Sistemul este compus din **7 microservicii independente**, fiecare cu propria sa bază de date MySQL. Această decuplare completă la nivel de date permite scalarea și dezvoltarea independentă a fiecărui modul.

| Microserviciu | Responsabilitate |
| :--- | :--- |
| `user-service` | Înregistrare și sincronizare identități cu Keycloak |
| `pacienti-service` | Profile pacienți și jurnale de recuperare |
| `terapeuti-service` | Profile terapeuți, program de lucru, locații |
| `programari-service` | Booking, logică de business centrală, evaluări clinice |
| `servicii-service` | Catalogul serviciilor medicale |
| `chat-service` | Mesagerie în timp real prin WebSockets/STOMP |
| `notificari-service` | Consumer RabbitMQ — management alertare in-app |

### API Gateway & BFF (Backend-For-Frontend)
**Spring Cloud Gateway** acționează ca punct unic de intrare. Folosește WebFlux pentru agregarea asincronă a cererilor (rezolvând problema N+1 în rețea) și reducând latența percepută de frontend.

### Securitate Zero-Trust (OAuth2/JWT + Keycloak)
Niciun microserviciu nu are încredere implicită în apelurile primite. Fiecare extrage și validează matematic identitatea din token-ul JWT folosind semnătura **Keycloak** (JWK URI). Validarea se aplică inclusiv pe conexiunile persistente WebSocket (STOMP), la nivel de cadru, prin interceptoare customizate ale canalelor de mesagerie.

Accesul la nivel de resurse este strict autorizat prin **RBAC** (`ROLE_PACIENT`, `ROLE_TERAPEUT`, `ROLE_ADMIN`).

### Logică de Business — Selecție Automată a Serviciului
Prevenind erorile umane, la procesul de creare a unei programări, platforma determină automat serviciul necesar pacientului:
- **Fără evaluare anterioară** → Aplică automat "Evaluare Inițială".
- **Ședințe rămase în planul activ** → Aplică serviciul recomandat de terapeut.
- **Ședințe epuizate** → Impune o "Reevaluare" (trimițând și notificare către terapeut).

### Comunicare Asincronă (Event-Driven cu RabbitMQ)
Pentru scalabilitate, evenimentele non-critice temporal (*programare nouă*, *jurnal completat*, *mesaj offline necitit*) sunt emise pe un Exchange **RabbitMQ** si procesate asincron de `notificari-service`, lăsând fluxul principal liber.

---

## 🏗 Infrastructură & Stack Tehnologic

### Infrastructură
- Containerizare completă cu **Docker** (utilizând `docker-compose` mediul local).
- Arhitectură pregătită pentru orchestrare prin **Kubernetes**.

### Stack Tehnologic
- **Backend:** Java 21, Spring Boot 3.x, Spring Cloud Gateway, WebFlux, OpenFeign, Spring Security (OAuth2 Resource Server), Spring AMQP, Spring Data JPA / Hibernate, MapStruct, Lombok
- **Frontend:** React 19, Vite, React Router DOM v7, Axios, SockJS & StompJS, Recharts, FullCalendar
- **Infrastructură & DB:** MySQL, RabbitMQ, Keycloak, Docker, Kubernetes
