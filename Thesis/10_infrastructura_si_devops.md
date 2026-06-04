## Secțiunea 10: Infrastructură și DevOps (Infrastructure & DevOps)
### 10.1 API Gateway și Arhitectura de Rutare
Serviciul `api-gateway` funcționează ca un Backend API Gateway / BFF pentru microserviciile din backend. În timp ce controlerul Kubernetes Ingress acționează ca Global Edge Router / Reverse Proxy pentru întreaga platformă, API Gateway este responsabil pentru preluarea întregului trafic destinat backend-ului (`/api/**`) și implementarea **modelului Backend-for-Frontend (BFF)**. Construit pe baza **Spring Cloud Gateway** și **Spring WebFlux**, utilizează un model reactiv asincron și non-blocant pentru a gestiona un volum ridicat de cereri concurente.
### 10.1.1 Definiții de Rutare (`application.yml`)
Rutarea este configurată declarativ utilizând predicate și filtre:
```yaml
- id: terapeuti-disponibilitate
  uri: ${application.urls.terapeuti-service}
  predicates:
    - Path=/api/disponibilitate/**
  filters:
    - StripPrefix=1
```
- **Predicat (`Path`)**: Potrivește cererile de intrare.
- **Filtru (`StripPrefix=1`)**: Elimină prima parte a căii (e.g., `/api`) înainte de a trimite mai departe cererea către microserviciul downstream.
- **Prioritatea de Rutare**: Configurația ordonează în mod strict rutele specifice (cum ar fi `/api/disponibilitate/**` și `/api/locatii/**`) *înaintea* rutelor generice cu wildcard (cum ar fi `/api/terapeut/**`) pentru a preveni coliziunile de rutare în cadrul `terapeuti-service`.
### 10.1.2 Filtre Globale
Pentru a preveni duplicarea antetelor Cross-Origin Resource Sharing (CORS)—o problemă frecventă când atât gateway-ul, cât și serviciile downstream încearcă să adauge aceste antete—gateway-ul aplică un filtru implicit `DedupeResponseHeader`:
```yaml
default-filters:
  - DedupeResponseHeader=Access-Control-Allow-Origin Access-Control-Allow-Credentials, RETAIN_UNIQUE
```
### 10.2 Securitatea la Marginea Sistemului & WebFlux (Edge Security & WebFlux)
Securitatea este impusă la nivel de Gateway, împiedicând traficul neautentificat să ajungă la microserviciile downstream.
Clasa: `com.example.api_gateway.config.SecurityConfig`
- **`authorizeExchange`**: Definește regulile perimetrului. Căile `/api/auth/**` și negocierile WebSocket (`/api/chat/ws-chat/**`) are permisiunea de a fi accesate. Rutele specifice de administrare (e.g., `POST /api/locatii/**`) necesită în mod strict rolurile corespunzătoare (`hasRole("admin")`).
- **Reactive JWT Extraction**: Keycloak încorporează rolurile utilizatorilor într-un claim personalizat `realm_access.roles`. Gateway-ul folosește un `Converter<Jwt, Mono<AbstractAuthenticationToken>>` pentru a extrage aceste valori, a le adăuga prefixul `ROLE_` și a le emite printr-un apel non-blocant `Mono.just(new JwtAuthenticationToken(...))`.
### 10.3 Orchestrarea Containerelor & Kubernetes
KinetoCare folosește **Kubernetes (k8s)** pentru orchestrare, auto-recuperare și managementul configurațiilor. Arhitectura de deployment se bazează pe câteva componente declarative:
1. **Spații de Nume & Izolare (Namespaces & Isolation)**: Toate resursele sunt izolate în mod logic în spațiul de nume `kinetocare` (`namespace.yaml`).
2. **Configurații & Secrete**: Constantele de mediu (URL-uri, porturi) sunt decuplate de cod prin intermediul `configmap.yaml`, conform principiilor aplicațiilor de tip 12-Factor. Parolele și datele sensibile sunt stocate criptat în `secrets.yaml`.
3. **Sarcini de Lucru cu Stare (Stateful Workloads)**: Baza de date MySQL (`mysql.yaml`) este implementată cu un `PersistentVolumeClaim` (PVC) pentru a garanta persistența datelor dincolo de ciclul de viață al pod-ului.
4. **Edge Router Global**: Descriptorul `ingress.yaml` definește un Ingress Controller ce acționează ca punct unic de intrare al platformei pe portul 80. Acesta segmentează curat traficul: `/realms` direcționează către Keycloak, `/api` direcționează către Spring Cloud Gateway, iar `/` direcționează către frontend-ul Nginx.
**Justificare Academică pentru Rularea Parțială a Clusterului:**
Deși KinetoCare este format din 7 microservicii, manifestele Kubernetes furnizate rulează în mod deliberat doar arhitectura de bază: `api-gateway`, `user-service`, `pacienti-service`, `terapeuti-service`, `frontend` și `keycloak`. Aceasta este o decizie arhitecturală pragmatică. Kubernetes este un mare consumator de resurse; rularea locală a unui cluster complet cu 7 servicii, brokeri de mesaje și baze de date multiple depășește capacitățile unei stații de lucru standard. Prin orchestrarea nucleului infrastructurii, sistemul dovedește cu succes conceptele de scalare `Deployment`, Descoperire de Servicii (ClusterIP), verificări de tipul `Readiness/Liveness` și rutare prin Ingress, fără a cauza epuizarea resurselor hardware locale. Extinderea acestui model pentru restul serviciilor este facilă și respectă exact același șablon.
**Comportamentul Sistemului în Rulare Parțială (Degradare Elegantă):**
Deoarece arhitectura este strict decuplată, rularea doar a acestui subset de servicii permite funcționarea perfectă a anumitor domenii, demonstrând eficient conceptul de **degradare elegantă (graceful degradation)**:
- **Autentificarea și Înregistrarea** funcționează perfect (prin `keycloak` și `user-service`).
- **Completarea Profilului și Datele Pacienților** (actualizare CNP, preferințe) sunt pe deplin operaționale (prin `pacienti-service`).
- **Motorul de Căutare al Terapeutaților** este funcțional (prin `terapeuti-service`).
- **Demonstrarea Toleranței la Erori:** Dacă un pacient își schimbă terapeutul preferat, `pacienti-service` încearcă un apel Feign către `programari-service` pentru a anula vechile programări. Deoarece `programari-service` nu este pornit, acest apel Feign eșuează. Cu toate acestea, deoarece apelul este învelit într-un bloc `try-catch` în interiorul `PacientService.chooseTerapeut`, tranzacția nu este anulată — actualizarea profilului reușește, iar eșecul este izolat. 
Bara de navigare din Dashboard (Pagina principală) și widget-urile de rezervare vor eșua în mod natural la încărcare (returnând erori de tipul 502/503 prinse de interfața React prin ErrorBoundary).
### 10.4 Strategia de Containerizare
Docker este folosit pentru containerizarea aplicațiilor și asigurarea parității mediilor între dezvoltare și producție.
- **Backend (Spring Boot)**: Microserviciile folosesc un `Dockerfile` optimizat care împachetează artefactul pre-compilat `.jar` într-o imagine ușoară JRE Alpine. Acest lucru reduce dimensiunea imaginii și suprafața de atac.
- **Orchestrare Locală**: Pentru dezvoltarea locală, `docker-compose.yml` pornește toate cele 7 servicii, Keycloak, RabbitMQ și MySQL în cadrul unei rețele partajate `kineto-network`, permițând descoperirea serviciilor pe bază de DNS (e.g., `mysql:3306`).
