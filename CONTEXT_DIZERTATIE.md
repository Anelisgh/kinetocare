# EXECUTION RULES FOR THE AGENT
1. CODE READING: Constantly validate the specifications below by reading the actual source code from the workspace. Do not assume or hallucinate functionalities that do not exist in the code.
2. DISCREPANCIES: If the actual code implementation differs from this document, explicitly flag the difference and document the version found in the codebase.
---
# KINETOCARE CONTEXT

## 1. ENTITIES PER SERVICE (Database Schema)

📦 USER SERVICE

├── User
│ ├── id (Long, PK, auto-increment)
│ ├── keycloakId (String, unique, 36 chars, not null)
│ ├── email (String, unique, 100 chars, not null)
│ ├── nume (String, 100 chars, not null)
│ ├── prenume (String, 100 chars, not null)
│ ├── telefon (String, 20 chars, not null)
│ ├── gen (Gen enum, not null)
│ ├── role (UserRole enum, not null)
│ ├── active (Boolean, default true, not null)
│ └── createdAt (OffsetDateTime, not null)
│
├── UserRole enum (ADMIN, TERAPEUT, PACIENT)
└── Gen enum (MASCULIN, FEMININ)

📦 TERAPEUTI SERVICE

├── Terapeut
│ ├── id (Long, PK, auto-increment)
│ ├── keycloakId (String, unique, 36 chars, not null)
│ ├── specializare (Specializare enum)
│ ├── pozaProfil (MEDIUMTEXT, Base64)
│ ├── active (Boolean, default true, not null)
│ ├── createdAt (OffsetDateTime, not null)
│ └── updatedAt (OffsetDateTime, not null)
│
├── Specializare enum (ADULTI, PEDIATRIE)
│
├── Locatie
│ ├── id (Long, PK, auto-increment)
│ ├── nume (String, 200 chars, not null)
│ ├── adresa (String, 300 chars, not null)
│ ├── oras (String, 100 chars, not null)
│ ├── judet (String, 100 chars, not null)
│ ├── codPostal (String, 10 chars)
│ ├── telefon (String, 20 chars)
│ ├── active (Boolean, default true, not null)
│ ├── createdAt (OffsetDateTime, not null)
│ └── updatedAt (OffsetDateTime, not null)
│
├── DisponibilitateTerapeut
│ ├── id (Long, PK, auto-increment)
│ ├── terapeutId (Long, not null)
│ ├── ziSaptamana (Integer, not null)
│ ├── locatieId (Long, not null)
│ ├── oraInceput (LocalTime, not null)
│ ├── oraSfarsit (LocalTime, not null)
│ ├── active (Boolean, default true, not null)
│ ├── createdAt (OffsetDateTime, not null)
│ └── updatedAt (OffsetDateTime, not null)
│
└── ConcediuTerapeut
    ├── id (Long, PK, auto-increment)
    ├── terapeutId (Long, not null)
    ├── dataInceput (LocalDate, not null)
    ├── dataSfarsit (LocalDate, not null)
    └── createdAt (OffsetDateTime, not null)

📦 PACIENTI SERVICE

├── Pacient
│ ├── id (Long, PK, auto-increment)
│ ├── keycloakId (String, unique, 36 chars, not null)
│ ├── dataNasterii (LocalDate)
│ ├── cnp (String, unique, 13 chars)
│ ├── faceSport (FaceSport enum)
│ ├── detaliiSport (String, 500 chars)
│ ├── orasPreferat (String, 100 chars)
│ ├── locatiePreferataId (Long)
│ ├── terapeutKeycloakId (String, 36 chars)
│ ├── active (Boolean, default true, not null)
│ ├── createdAt (OffsetDateTime, not null)
│ └── updatedAt (OffsetDateTime, not null)
│
├── FaceSport enum (DA, NU)
│
└── JurnalPacient
    ├── id (Long, PK, auto-increment)
    ├── pacientId (Long, not null)
    ├── programareId (Long, not null)
    ├── data (LocalDate, not null)
    ├── nivelDurere (Integer, not null)
    ├── dificultateExercitii (Integer, not null)
    ├── nivelOboseala (Integer, not null)
    ├── comentarii (TEXT)
    ├── createdAt (OffsetDateTime, not null)
    └── updatedAt (OffsetDateTime)

📦 PROGRAMARI SERVICE

├── Programare
│ ├── id (Long, PK, auto-increment)
│ ├── pacientKeycloakId (String, 36 chars, not null)
│ ├── terapeutKeycloakId (String, 36 chars, not null)
│ ├── locatieId (Long, not null)
│ ├── serviciuId (Long, not null)
│ ├── tipServiciu (String, 100 chars, not null)
│ ├── pret (BigDecimal, not null)
│ ├── durataMinute (Integer, not null)
│ ├── primaIntalnire (Boolean)
│ ├── data (LocalDate, not null)
│ ├── oraInceput (LocalTime, not null)
│ ├── oraSfarsit (LocalTime, not null)
│ ├── status (StatusProgramare enum, not null)
│ ├── motivAnulare (MotivAnulare enum)
│ ├── areEvaluare (Boolean, default false, not null)
│ ├── areJurnal (Boolean, default false, not null)
│ ├── createdAt (OffsetDateTime, not null)
│ └── updatedAt (OffsetDateTime, not null)
│
├── StatusProgramare enum (PROGRAMATA, FINALIZATA, ANULATA)
│
├── MotivAnulare enum (ANULAT_DE_PACIENT, ANULAT_DE_TERAPEUT, NEPREZENTARE, ADMINISTRATIV)
│
├── Evaluare
│ ├── id (Long, PK, auto-increment)
│ ├── pacientKeycloakId (String, 36 chars, not null)
│ ├── terapeutKeycloakId (String, 36 chars, not null)
│ ├── programareId (Long)
│ ├── tip (TipEvaluare enum, not null)
│ ├── data (LocalDate, not null)
│ ├── diagnostic (TEXT, not null)
│ ├── sedinteRecomandate (Integer)
│ ├── serviciuRecomandatId (Long)
│ ├── observatii (TEXT)
│ ├── createdAt (OffsetDateTime, not null)
│ └── updatedAt (OffsetDateTime, not null)
│
├── TipEvaluare enum (INITIALA, REEVALUARE)
│
├── RelatiePacientTerapeut
│ ├── id (Long, PK, auto-increment)
│ ├── pacientKeycloakId (String, 36 chars, not null)
│ ├── terapeutKeycloakId (String, 36 chars, not null)
│ ├── dataInceput (LocalDate, not null)
│ ├── dataSfarsit (LocalDate)
│ ├── activa (Boolean, default true, not null)
│ ├── createdAt (OffsetDateTime, not null)
│ └── updatedAt (OffsetDateTime, not null)
│
└── Evolutie
    ├── id (Long, PK, auto-increment)
    ├── pacientKeycloakId (String, 36 chars, not null)
    ├── terapeutKeycloakId (String, 36 chars, not null)
    ├── observatii (TEXT, not null)
    ├── createdAt (OffsetDateTime, not null)
    └── updatedAt (OffsetDateTime, not null)

📦 SERVICII SERVICE

├── Serviciu
│ ├── id (Long, PK, auto-increment)
│ ├── nume (String, not null)
│ ├── tipServiciuId (Long, not null)
│ ├── pret (BigDecimal, not null)
│ ├── durataMinute (Integer, not null)
│ ├── active (Boolean, default true, not null)
│ ├── createdAt (OffsetDateTime, not null)
│ └── updatedAt (OffsetDateTime, not null)
│
└── TipServiciu
    ├── id (Long, PK, auto-increment)
    ├── nume (String, unique, not null)
    ├── descriere (TEXT)
    ├── active (Boolean, default true, not null)
    ├── createdAt (OffsetDateTime, not null)
    └── updatedAt (OffsetDateTime, not null)

📦 CHAT SERVICE

├── Conversatie
│ ├── id (Long, PK, auto-increment)
│ ├── pacientKeycloakId (String, 36 chars, not null)
│ ├── terapeutKeycloakId (String, 36 chars, not null)
│ ├── ultimulMesajLa (OffsetDateTime)
│ ├── createdAt (OffsetDateTime, not null)
│ └── updatedAt (OffsetDateTime, not null)
│
├── Mesaj
│ ├── id (Long, PK, auto-increment)
│ ├── conversatieId (Long, not null)
│ ├── expeditorKeycloakId (String, 36 chars, not null)
│ ├── tipExpeditor (TipExpeditor enum, not null)
│ ├── continut (TEXT, not null)
│ ├── esteCitit (Boolean, default false, not null)
│ ├── cititLa (OffsetDateTime)
│ └── trimisLa (OffsetDateTime, not null)
│
└── TipExpeditor enum (PACIENT, TERAPEUT)

📦 NOTIFICARI SERVICE

├── Notificare
│ ├── id (Long, PK, auto-increment)
│ ├── userKeycloakId (String, 36 chars, not null)
│ ├── tipUser (TipUser enum, not null)
│ ├── tip (TipNotificare enum, not null)
│ ├── titlu (String, 500 chars, not null)
│ ├── mesaj (TEXT, not null)
│ ├── entitateLegataId (Long)
│ ├── tipEntitateLegata (String, 50 chars)
│ ├── urlActiune (String, 500 chars)
│ ├── esteCitita (Boolean, default false, not null)
│ ├── cititaLa (OffsetDateTime)
│ └── createdAt (OffsetDateTime, not null)
│
├── TipUser enum (PACIENT, TERAPEUT)
└── TipNotificare enum (PROGRAMARE_ANULATA_DE_TERAPEUT, REMINDER_24H, REMINDER_2H, MESAJ_DE_LA_TERAPEUT, REMINDER_JURNAL, REEVALUARE_RECOMANDATA, PROGRAMARE_NOUA, EVALUARE_INITIALA_NOUA, PROGRAMARE_ANULATA_DE_PACIENT, JURNAL_COMPLETAT, MESAJ_DE_LA_PACIENT, REEVALUARE_NECESARA)

## 2. CORE BUSINESS LOGIC

### Appointments (Programări)
- **Status Transitions:** Automatically transition from `PROGRAMATA` to `FINALIZATA` via a Cron Job running every 30s if the appointment's end time has passed. Can also be manually marked as `ANULATA`.
- **Automatic Service Determination:** At creation, the system determines the required service:
  - If the patient has no previous evaluation -> automatically assigns "Evaluare Inițială".
  - If the number of `FINALIZATA` sessions since the last evaluation is >= `sedinte_recomandate` -> automatically assigns "Reevaluare".
  - Otherwise -> assigns the recommended service from the last evaluation.
- **First Meeting:** `primaIntalnire` is set to `true` if there are no other active/finalized appointments between that specific patient and therapist.

### Evaluations and Progress Notes (Evaluări & Evoluții)
- **Evaluations:** Added manually by the therapist and explicitly linked to a `programare_id`. Saving an evaluation sets `are_evaluare = true` on the corresponding appointment, which excludes this session from the standard treatment session count.
- **Progress Notes (Evoluții):** Free-text entries written by the therapist. Unlike evaluations, they are not tied directly to a specific appointment ID, but only to the patient-therapist pair.

### Patient-Therapist Relationship (RelatiePacientTerapeut)
- Only **one** `activa = true` relationship is allowed per patient at any given time.
- **Activation:** Triggered automatically upon the first finalized appointment or the first created evaluation.
- **Therapist Change:** If a patient changes their preferred therapist in `pacienti-service`:
  - The old relationship in `programari-service` is archived (`activa = false`, `dataSfarsit` is set).
  - Future scheduled appointments with the old therapist are automatically canceled.
  - **Crucial:** The treatment session count is **NOT** reset. The new therapist will continue the previous therapist's plan until the recommended sessions are exhausted, triggering a "Reevaluare".

### Patient Journal (Jurnal Pacient)
- Can only be added after an appointment is finalized.
- Always strictly linked to a `programare_id`.
- Successfully saving a journal updates `are_jurnal = true` on the corresponding appointment in `programari-service`.

## 3. COMMUNICATION ARCHITECTURE (CHAT & NOTIFICATIONS)

### Real-Time Chat System
- **Core:** WebSockets with STOMP protocol, using SockJS as a fallback.
- **Routing:** Uses `SimpMessagingTemplate` to push saved messages to specific destinations (e.g., `/queue/conversatii/{id}`).
- **Inter-Service Integration:**
  - **Asynchronous:** `ChatService` publishes a `NotificareEvent` to RabbitMQ to trigger cross-service alerts.
  - **Synchronous Security:** Uses FeignClient (`ProgramariClient`) to verify if the therapeutic relationship is currently active *before* allowing a message to be sent.
- **Security:** Functions as an OAuth2 Resource Server. Users are identified securely via `userKeycloakId`. A `StompSecurityInterceptor` ensures only authenticated users can open connections.

### Event-Driven Notifications
- **Message Broker:** Uses RabbitMQ (AMQP) acting as a Central Hub (Observer Pattern). Other services throw messages into queues instead of making direct HTTP calls.
- **Consumer:** `NotificareConsumer` listens to queue events (`@RabbitListener`) and persists notifications to the MySQL database for historical tracking.
- **Smart Payload:** Contains a `urlActiune` field allowing frontend navigation directly from the notification (e.g., redirecting to the chat window).

## 4. RESILIENCY AND ERROR HANDLING

### Backend Level
- **RFC 9457 Standard:** Implements `ProblemDetail` to provide structured REST API error responses (e.g., converting generic 500 errors into explicit 404, 409, or 403 codes).
- **Transactionality:** `@Transactional` is rigorously applied to critical methods (e.g., `creeazaProgramare`) to ensure atomicity and prevent race conditions.
- **Feign Error Propagation:** A `CustomErrorDecoder` translates external network status codes (e.g., 401, 403 from Keycloak or other services) into readable internal business exceptions.
- **WebSocket Feedback:** A global `@MessageExceptionHandler` intercepts STOMP errors and routes them to a private queue (`/user/queue/errors`).

### RabbitMQ Reliability
- **Dead Letter Exchange (DLX):** If a notification fails processing multiple times, it is routed to a Dead Letter Queue (DLQ).
- **Benefits:** Prevents "poison messages" from blocking queues, allows centralized error monitoring via `DeadLetterConsumer`, and enables manual message retransmission.

### Frontend Level (React)
- **Error Boundaries:** `ErrorBoundary.jsx` intercepts runtime exceptions in the component tree, displaying a friendly fallback UI and preventing the "White Screen of Death".
- **Real-Time Error UI:** `FereastraChat.jsx` actively subscribes to the `/user/queue/errors` WebSocket channel to display immediate, interactive alert banners if a message fails to send.

---

## 5. FRONTEND ARCHITECTURE (REACT)

### Core Tech Stack
*   **Runtime & Build Engine:** React 19 and Vite as the primary development and build platform. Vite is configured with an active reverse proxy mapping all `/api` requests to the Spring Cloud API Gateway (running at `http://localhost:8081` in local environments).
*   **Routing System:** React Router DOM v7 for declarative, nested routing and unified layout structures.
*   **HTTP Client:** Axios for asynchronous HTTP communication, customized with request and response interceptors.
*   **Real-time Communication:** SockJS-client and `@stomp/stompjs` for establishing persistent STOMP-over-WebSocket connections, allowing fallback communication protocols when standard WebSockets are restricted.
*   **Clinical UI Components:** FullCalendar library components (`@fullcalendar/react`, `@fullcalendar/daygrid`, `@fullcalendar/timegrid`, `@fullcalendar/interaction`) to render interactive therapist schedules, and Recharts for patient physical recovery progression graphs.

### State Management & Data Fetching
*   **Stateless Component Design:** The application avoids heavy, complex global state libraries (such as Redux or Zustand) to minimize bundle sizes and simplify data flows.
*   **React Context API:** `AuthContext.jsx` acts as the single source of truth for global authentication states, maintaining the `isAuthenticated` status, `userInfo` payload, and orchestrating the initial boot authentication state (`isInitializing`).
*   **Stateless Service Architecture:** A dedicated service layer (e.g., `programariService.js`, `profileService.js`, `chatService.js`) encapsulates REST endpoints as modular JavaScript services, providing a clean separation between UI components and raw HTTP requests.
*   **Event-Driven UI Subscriptions:** Real-time data streams for the chat and notifications systems bypass REST polling entirely by establishing persistent WebSocket subscriptions directly inside active components.

### Authentication Flow (Keycloak & Route Protection)
*   **Secure In-Memory Token Management:** To mitigate Cross-Site Scripting (XSS) risks, the application stores JWT Access Tokens strictly in-memory (`inMemoryToken` variable in `authService.js`). Tokens are never persisted in insecure mediums like `localStorage` or `sessionStorage`.
*   **HttpOnly Cookie Refresh Strategy:** The Refresh Token is stored exclusively in a secure, `HttpOnly`, `SameSite=Strict` cookie managed by the Spring Cloud Gateway. Upon application load or access token expiration, a silent token refresh (`authService.refreshToken()`) is triggered via a `/api/auth/token` request to retrieve a new short-lived JWT Access Token.
*   **JWT Extraction and Claim Parsing:** The client extracts user details on successful token load by parsing the JWT payload using native Base64 decoding (`atob`). This dynamically populates roles (e.g., `ROLE_PACIENT`, `ROLE_TERAPEUT`, `ROLE_ADMIN`) and the Keycloak subject identifier (`sub` mapped as `keycloakId`).
*   **Declarative Route Protection (`App.jsx`):**
    *   **`ProtectedRoute`:** A React Router route wrapper that checks authentication state and enforces role-based access control (RBAC). If a user is not authenticated, they are redirected to `/login`. If they do not possess the necessary role, they are redirected to `/unauthorized`.
    *   **`ProfileGuard`:** A specialized route wrapper for patient accounts. It calls `profileService.getProfile()` to check for an incomplete profile state. If mandatory fields (e.g., CNP, date of birth) are missing, it redirects the patient to `/pacient/complete-profile` before granting access to clinical sections.
    *   **Public Routes:** Paths like `/login`, `/register`, and `/unauthorized` bypass guards.

### API Integration (Interceptors, Tokens, and Error Handling)
*   **Axios Client Instance:** Configured with `withCredentials: true` to ensure the gateway's HttpOnly session cookie is automatically transmitted with every request.
*   **Global Request Interceptor:** Automatically injects the active in-memory JWT Access Token as a `Bearer` token inside the HTTP `Authorization` header for all outgoing Axios requests.
*   **Global Response Interceptor (Self-Healing Pipelines):**
    *   **Automatic Token Refresh:** On a `401 Unauthorized` status response, the interceptor pauses the request pipeline, executes a silent refresh (`authService.refreshToken()`), updates the `Authorization` header with the new token, and replays the original failed request seamlessly.
    *   **Automatic Session Expiry Redirect:** If the silent refresh fails (indicating an expired or deleted refresh token), the interceptor automatically calls `authService.logout()` to clear memory, and forces a hard redirect to `/login`.
    *   **Centralized Access Denied Redirect:** Intercepts `403 Forbidden` responses and forces a redirect to `/unauthorized`.
*   **RFC 7807 Compliance & Unified Error Parsing:**
    *   **Standardized AppError:** The helper function `handleApiError` parses RFC 7807-compliant backend `ProblemDetail` payloads, extracting root messages from `detail` or `message`, alongside fine-grained field-level violations from `erori_campuri`, packing them into a standard `AppError` wrapper.
    *   **React Error Boundary (`ErrorBoundary.jsx`):** Provides a global fallback interface for unexpected runtime client-side exceptions, preventing blank-screen lockups and offering a single-button page reload.
    *   **WebSocket Failure Interception:** Directly handles real-time messaging pipeline issues by subscribing to `/user/queue/errors` STOMP destinations to display immediate, actionable error alerts in the active chat view.

---

## 6. API GATEWAY ARCHITECTURE
- **Single Entry Point:** All client-side requests from the React frontend are routed through Spring Cloud Gateway.
- **WebFlux-Based Core:** Built on Spring Boot WebFlux (reactive, non-blocking engine), which allows highly efficient request forwarding and low resource utilization.
- **BFF (Backend-For-Frontend) Pattern:** Resolves client-side N+1 network latency by aggregating multiple microservice calls (e.g., combining profile data from `user-service` and appointment history from `programari-service`) into a single, cohesive response payload for the homepage.
- **Token Exchange & Silent Refresh:** Intercepts and handles POST `/api/auth/token` requests to retrieve fresh JWT Access Tokens from Keycloak without exposing user credentials after session initiation.
- **Secure Cookie Management:** Manages the lifecycle of the `refresh_token` cookie. The Gateway extracts the refresh token from the browser request, issues Keycloak token exchanges, and returns the new Access Token while maintaining the refresh token strictly inside a secure, HttpOnly, SameSite=Lax cookie.
- **Gateway Exception Handling:** Utilizes a reactive `GlobalExceptionHandler` to intercept and translate downstream microservice errors and connection timeouts into structured, RFC 7807-compliant Problem Details.

## 7. INFRASTRUCTURE
- **Full Docker Containerization:** Orchestrated via `docker-compose` for the local development environment.
- **Services Stack:** Spin-up includes all 7 microservices (`user-service`, `pacienti-service`, `terapeuti-service`, `programari-service`, `servicii-service`, `chat-service`, `notificari-service`), the `api-gateway`, a single `mysql` instance (configured with 7 logically separated database schemas), `rabbitmq` (AMQP Broker), and `keycloak` (IAM).
- **Kubernetes Architecture:** Fully prepared with K8s deployment manifests (`k8s/`) demonstrating production-ready practices such as horizontal pod scaling, self-healing liveness/readiness probes, Ingress rules, resource requests/limits, and ConfigMaps/Secrets.
- **Auto-Provisioned Keycloak Realm:** Automated container-level setup importing the `kinetocare-realm.json` to pre-configure clients (`react-client`), user roles, and security realms on container boot.

## 8. REGISTRATION FLOW (Dual Write)
- **Form Submission:** The patient fills the register form on the React frontend.
- **Gateway Routing:** Frontend POSTs a payload to Gateway → `user-service` (`/users/auth/register`).
- **Keycloak Registration & Local DB Sync (Dual Write):**
  - `user-service` registers the user in Keycloak using the Keycloak Admin REST API.
  - Upon successful creation, the generated `keycloakId` (Keycloak subject identifier `sub`) is returned.
  - `user-service` immediately inserts a corresponding user record into its own MySQL table, storing `keycloakId` as the immutable, unified user reference across all database shards.
- **Profile Initialization:** During the registration transaction, `user-service` synchronously invokes downstream services (`pacienti-service` or `terapeuti-service`) via RestTemplate to initialize an empty profile shell with the matching `keycloakId`.
- **First-Login Completion:** On the patient's first login, the frontend's `ProfileGuard` detects an incomplete clinical profile and forces them to complete mandatory data fields (CNP, birthdate, sports background) before granting app access.

## 9. SERVICII-SERVICE INTEGRATION
- **Medical Catalog Authority:** Acts as the single source of truth for clinical medical services, pricing packages, and session durations (e.g., "Evaluare Initiala", "Reevaluare").
- **Feign Client Integration & Denormalization:** During appointment creation, `programari-service` invokes `servicii-service` via a Feign Client (`ServiciiClient`) to retrieve active pricing and duration metadata. This info is explicitly *denormalized* and written directly into the `Programare` entity to shield historical booking/billing records from future catalog price edits.
- **Administrative Catalog Control:** Access to service creation, modifications, or deactivation is restricted to Admin accounts via dedicated endpoints.

## 10. ADMIN STATISTICS
- **Centralized Service Authority:** Exposed directly within `programari-service` under `StatisticiController` (endpoints `/programari/statistici/...`).
- **Database-Level Aggregation:** Aggregations (monthly appointments, cancellation rates, total revenue, and therapist workloads) are queried directly from the `programari` database table since historical transaction details (prices, durations, statuses) are stored on the Programare entity.
- **Optimized Inter-Service Feign Calls:** Dependencies like human-readable location names and therapist details are fetched via Spring Cloud OpenFeign calls to `terapeuti-service` and `user-service`.
- **Performance Optimization:** Redundant network hops are minimized using Spring's caching abstraction (`@Cacheable` in `StatisticiCacheService`), keeping locations and therapist metadata cached locally.