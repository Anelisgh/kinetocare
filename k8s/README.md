# Containerizare: Docker & Kubernetes

## Arhitectura generală

Kinetocare este construit ca o arhitectură de **microservicii** — fiecare componentă (autentificare, pacienți, terapeuti etc.) este o aplicație independentă. Pentru a le putea rula și gestiona împreună, am folosit două tehnologii complementare: **Docker** și **Kubernetes**.

```
[Browser]
    │
    ▼
[Frontend - React/Nginx]  ──→  [API Gateway]  ──→  [user-service]
                                                  ├─→  [pacienti-service]
                                                  ├─→  [terapeuti-service]
                                                  └─→  [...]
                                    │
                              [Keycloak] ──→ [MySQL]
```

---

## Docker — Containerizarea fiecărui serviciu

**Docker** rezolvă problema clasică *"merge pe calculatorul meu, dar nu și pe al tău"*. Fiecare microserviciu are propriul `Dockerfile` care descrie exact mediul în care rulează.

### Frontend (multi-stage build)
Dockerfile-ul de frontend folosește **două etape**:
1. **Etapa de build**: pornind de la o imagine de Node.js, compilăm codul React/Vite în fișiere statice (HTML/JS/CSS)
2. **Etapa de producție**: luăm doar rezultatul compilat și îl punem într-o imagine de Nginx, care servește fișierele static extrem de eficient

Motivul pentru care separăm cele două etape: imaginea finală nu mai conține Node.js (sute de MB), ci doar Nginx + câteva fișiere. Rezultatul este o imagine de producție **mică, rapidă și sigură**.

### Backend (servicii Spring Boot)
Fiecare microserviciu Java are un Dockerfile simplu:
- Pornind de la o imagine minimală de Java (JRE Alpine — doar runtime, fără uneltele de compilare)
- Copiind fișierul `.jar` deja compilat de Maven
- Rulând aplicația

### Docker Compose — orchestrare locală
`docker-compose.yml` pornește **toate** serviciile împreună pe un singur calculator, conectate pe o rețea internă (`kineto-network`). Serviciile se "văd" între ele prin nume (ex: `user-service` poate accesa baza de date la `mysql:3306`). Aceasta este configurația folosită în **development**.

---

## Kubernetes — De ce și ce am inclus

**Kubernetes (k8s)** este sistemul standard în industrie pentru a rula aplicații containerizate la scară, cu auto-recovery, load balancing și gestionarea configurațiilor.

### Ce am implementat în k8s

| Fișier | Ce face |
|---|---|
| `namespace.yaml` | Creează un spațiu izolat (`kinetocare`) în cluster |
| `configmap.yaml` | Stochează setările comune (URL-uri, configurări) |
| `secrets.yaml` | Stochează datele sensibile (parole) |
| `mysql.yaml` | Baza de date MySQL cu date persistente (PVC) |
| `keycloak.yaml` | Serverul de autentificare cu realm importat automat |
| `api-gateway.yaml` | Punctul central de intrare al backend-ului |
| `user-service.yaml` | Serviciul de gestionare utilizatori |
| `pacienti-service.yaml` | Serviciul de gestionare pacienți |
| `terapeuti-service.yaml` | Serviciul de gestionare terapeuti |
| `frontend.yaml` | Aplicația React servită prin Nginx |
| `ingress.yaml` | Punct unic de intrare (/realms, /api, /) |

---

### Ingress Gateway (Punct unic de intrare)

Pentru a oferi o experiență de producție reală, am adăugat un **Ingress Controller**. Acesta ascultă pe portul 80 și rutează traficul astfel:
- `/realms`, `/resources`, `/admin` → **Keycloak** (Autentificare și Admin)
- `/api` → **API Gateway** (Tot traficul de microservicii)
- `/` → **Frontend** (Aplicația React)

Acest setup elimină nevoia de a expune porturi manuale (NodePort) pentru fiecare serviciu în parte.

---

### De ce k8s acoperă doar o parte din microservicii

Arhitectura completă include și servicii de notificări, chat și programări. Totuși, configurile Kubernetes de față acoperă în mod deliberat **nucleul aplicației** — infrastructura de autentificare și serviciile principale.

Motivul este practic: Kubernetes este o platformă destinată mediilor de producție cu resurse de server generoase. Rularea unui cluster complet local necesită resurse semnificative. Scopul acestui setup este de a **demonstra conceptele și buna practică** a containerizării cu Kubernetes, nu de a simula un cluster de producție real.
Adăugarea celorlalte servicii în k8s ar urma același pattern și ar fi trivială — dovada că arhitectura este corectă și scalabilă.

### Ce funcționează în acest setup restrâns (Graceful Degradation)

Deoarece arhitectura este strict decuplată, chiar și cu `programari-service` sau `chat-service` absente din clusterul k8s, aplicația demonstrează funcționalități critice prin **izolarea defecțiunilor (Graceful Degradation)**:
- **Autentificarea și Înregistrarea** funcționează perfect (via `keycloak` și `user-service`).
- **Onboarding-ul și Profilul** (completare date, setare preferințe) sunt complet operaționale (via `pacienti-service`).
- **Motorul de căutare a terapeuților** funcționează (via `terapeuti-service`).

**Dovada Toleranței la Defecte:** Dacă un pacient își schimbă terapeutul preferat, `pacienti-service` face un apel asincron (Feign) către `programari-service` pentru a anula programările viitoare. Deoarece `programari-service` lipsește din cluster, apelul Feign va eșua. Totuși, deoarece apelul este protejat de un bloc `try-catch` în codul sursă, eroarea este izolată și ignorată: actualizarea profilului pacientului se va salva cu succes în baza de date. Paginile care depind strict de serviciile lipsă (cum ar fi Dashboard-ul cu programări) vor afișa erori de rețea gestionate de Error Boundaries în React, demonstrând că o "cădere" a domeniului de programări nu distruge domeniile de identitate și profil.

### Concepte cheie demonstrate

- **Service** (ClusterIP vs NodePort) — segregarea traficului intern de cel extern
- **Deployment** cu `replicas` — baza pentru scalare orizontală
- **ReadinessProbe + LivenessProbe** — auto-recovery și zero-downtime deployments
- **PersistentVolumeClaim** — persistarea datelor în afara ciclului de viață al containerului
- **ConfigMap + Secrets** — separarea configurației de cod (principiu 12-Factor App)
- **Ingress** — un singur punct de intrare extern, ca un router/reverse-proxy

---

## Deploy

```bash
# 1. Aplică namespace-ul mai întâi
kubectl apply -f k8s/namespace.yaml

# 2. Aplică toate celelalte resurse în namespace-ul kinetocare
kubectl apply -f k8s/ -n kinetocare

# 3. Verifică că toate pod-urile sunt Running
kubectl get pods -n kinetocare
```

> **Notă**: Imaginile Docker trebuie să fie construite local înainte de deploy:
> `docker build -t kinetocare/user-service:latest ./backend/user-service`
