## Secțiunea 2: Autentificare și Autorizare (Authentication & Authorization)
### 2.1 Configurarea Keycloak
Keycloak rulează ca un serviciu containerizat în Docker Compose. Realm-ul este pre-configurat prin **import**: la pornirea containerului, Keycloak primește fișierul `kinetocare-realm.json` prin flag-ul `--import-realm`, ceea ce configurează automat întregul realm fără intervenție manuală în interfața grafică.
**Realm:** `kinetocare`
**Roluri la nivel de Realm** (nu roluri de client — această distincție este importantă pentru calea claim-urilor din JWT):
| Nume Rol | Nume Afișat | Utilizat De |
| --- | --- | --- |
| `pacient` | Pacient | Toate endpoint-urile destinate pacienților |
| `terapeut` | Terapeut | Toate endpoint-urile destinate terapeuților |
| `admin` | Administrator | Endpoint-urile administrative de management |
Aceste roluri apar în JWT sub `realm_access.roles` ca șiruri de caractere cu litere mici (e.g., `"pacient"`, `"terapeut"`, `"admin"`).
**Client:** `react-client`
- Tip client: **public** (fără client secret — adecvat pentru aplicații de tip SPA)
- Tipul de acces permite tipul de grant `password` (Resource Owner Password Credentials) pentru fluxul de autentificare
- URI-urile de redirecționare și originile CORS sunt configurate pentru aplicația frontend
**Client Admin:** `user-service` folosește un client Keycloak separat, cu privilegii de administrator (`admin-cli`), autentificat cu credențiale de admin în realm-ul `master`. Acest lucru oferă `user-service` capacitatea de a apela API-ul REST Keycloak Admin pentru a crea utilizatori, a aloca roluri, a reseta parole și a dezactiva conturi. Configurarea se află în `KeycloakConfig.java`:
```java
return KeycloakBuilder.builder()
    .serverUrl(authServerUrl)   // http://localhost:8080
    .realm(adminRealm)          // "master"
    .username(adminUsername)    // "admin"
    .password(adminPassword)    // "admin"
    .clientId(adminClientId)    // "admin-cli"
    .build();
```
**Auto-provisioning pentru Admin:** `AdminInitializer` implementează `CommandLineRunner` și se execută la pornirea `user-service`. Verifică dacă adresa de email de admin exists în baza de date locală; dacă nu, rulează o buclă de reîncercare (20 de încercări, la interval de 5 secunde) pentru a crea administratorul atât în Keycloak, cât și în baza de date locală `user_db`. Această buclă de reîncercare este necesară în mediile Docker Compose unde este posibil ca Keycloak să nu fie complet pregătit în momentul în care `user-service` pornește.
### 2.2 Fluxul Tokenului JWT: Autentificare → API Gateway → Microserviciu
Ciclul de viață al tokenului implică o diviziune deliberată între **browser** (care păstrează în memorie doar tokenul de acces - access token - cu durată scurtă de viață) și **Gateway** (care gestionează tokenul de refresh - refresh token - cu durată lungă de viață într-un cookie administrat de server).
**Fluxul de autentificare pas cu pas:**
1. Utilizatorul își introduce datele de conectare în `LoginPage.jsx`.
2. `authService.login()` trimite prin POST `grant_type=password&username=...&password=...` sub formă de date de formular codificate URL către `POST /api/auth/token` cu parametrul `credentials: 'include'`.
3. `KeycloakProxyController.handleTokenRequest()` din API Gateway primește cererea. Adaugă `client_id=react-client` și o redirecționează către endpoint-ul de token al Keycloak: `http://keycloak:8080/realms/kinetocare/protocol/openid-connect/token`.
4. Keycloak autentifică utilizatorul și returnează un corp JSON care conține `access_token`, `refresh_token`, `expires_in` etc.
5. Gateway-ul extrage `refresh_token`-ul din corpul răspunsului Keycloak, îl setează ca `null` în JSON-ul pe care îl returnează browserului și îl scrie sub forma unui antet `Set-Cookie: refresh_token=...; HttpOnly; SameSite=Lax; Path=/; Max-Age=2592000`.
6. Browserul primește doar `access_token`-ul în corpul răspunsului JSON.
7. `authService.setToken(tokenData.access_token)` stochează tokenul în variabila la nivel de modul `inMemoryToken` — niciodată în `localStorage` sau `sessionStorage`.
8. `AuthContext` apelează `authService.getUserInfo()`, care efectuează `JSON.parse(atob(token.split('.')[1]))` pentru a extrage `email`, `realm_access.roles` și `sub` (mapat ca `keycloakId`) direct din payload-ul JWT-ului, fără a mai face vreun apel la server.
9. `AuthContext` setează `isAuthenticated=true` și `userInfo={email, roles, keycloakId}`.
**Fluxul cererilor pas cu pas (apeluri ulterioare):**
1. Un component React apelează o funcție dintr-un serviciu, de exemplu, `programariService.getProgramari()`.
2. Instanța Axios din `api.js` rulează un interceptor de cereri care citește `authService.getToken()` și injectează antetul `Authorization: Bearer <access_token>` în cerere.
3. Cererea ajunge la API Gateway. `SecurityWebFilterChain` (WebFlux) din Gateway validează JWT-ul contactând endpoint-ul JWK al Keycloak (`/realms/kinetocare/protocol/openid-connect/certs`) pentru a verifica semnătura.
4. Convertorul `grantedAuthoritiesExtractor` citește rolurile din `jwt.getClaim("realm_access").roles`, adaugă prefixul `ROLE_` fiecărui rol (e.g., `"pacient"` → `ROLE_pacient`) și creează obiecte `SimpleGrantedAuthority` — în acest fel funcționează verificările Spring de tipul `hasRole("pacient")`.
5. Dacă regula la nivel de cale din `SecurityWebFilterChain` este respectată, Gateway-ul trimite mai departe cererea originală (inclusiv antetul `Authorization`) către microserviciul downstream.
6. Fiecare microserviciu downstream are propriul `SecurityFilterChain` configurat ca un `oauth2ResourceServer`. Acesta **validează independent** semnătura JWT folosind același URI JWK — fără a contacta Gateway-ul sau vreun spațiu comun de stocare a sesiunilor.
### 2.3 Modelul Zero-Trust: Validare JWT Independentă per Serviciu
Fiecare microserviciu este configurat ca un **OAuth2 Resource Server** independent. Acesta este principiul Zero-Trust aplicat microserviciilor: niciun serviciu nu se bazează pe "cuvântul" Gateway-ului că o cerere este autentificată — fiecare serviciu își validează singur tokenul.
Configurația este identică în toate microserviciile (prezentată aici din `user-service/SecurityConfig.java`):
```java
.oauth2ResourceServer(oauth2 -> oauth2
    .jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter())))
```
`jwtAuthenticationConverter` citește `realm_access.roles` din claim-urile JWT-ului și mapează fiecare rol la un `SimpleGrantedAuthority("ROLE_" + role)`. Cheia publică a JWT-ului este preluată de la endpoint-ul JWK al Keycloak:
```yaml
spring:
  security:
    oauth2:
      resourceserver:
        jwt:
          issuer-uri: http://localhost:8080/realms/kinetocare
          jwk-set-uri: http://localhost:8080/realms/kinetocare/protocol/openid-connect/certs
```
Spring Security păstrează în cache setul JWK și îl reîmprospătează periodic. Acest lucru înseamnă că:
- Dacă Keycloak este temporar indisponibil, cheile stocate anterior în cache continuă să funcționeze.
- Tokenurile revocate nu sunt respinse imediat (JWT-ul fiind stateless), dar valabilitatea scurtă (de regulă 5 minute) limitează fereastra de expunere.
- Fiecare serviciu își poate impune propriile reguli independent de oricare alt serviciu.
**API Gateway utilizează varianta WebFlux** (`@EnableWebFluxSecurity`, `ServerHttpSecurity`) deoarece rulează pe stiva reactivă. Toate serviciile downstream folosesc varianta blocantă MVC (`@EnableWebSecurity`, `HttpSecurity`). Logica de extragere a rolurilor este identică din punct de vedere funcțional, dar folosește clase API diferite: `Converter<Jwt, Mono<AbstractAuthenticationToken>>` pe Gateway, respectiv `JwtAuthenticationConverter` în cadrul serviciilor.
### 2.4 RBAC la Nivel de Endpoint
### Stratul API Gateway (`SecurityConfig.java` — WebFlux)
```java
.pathMatchers("/api/auth/token", "/api/auth/logout").permitAll()
.pathMatchers("/api/users/auth/**").permitAll()
.pathMatchers("/api/chat/ws-chat/**").permitAll()
.pathMatchers(HttpMethod.POST, "/api/locatii/**").hasRole("admin")
.pathMatchers(HttpMethod.PATCH, "/api/locatii/**").hasRole("admin")
.pathMatchers(HttpMethod.DELETE, "/api/locatii/**").hasRole("admin")
.pathMatchers("/api/locatii/all").hasRole("admin")
.pathMatchers(HttpMethod.GET, "/api/locatii/**").authenticated()
.anyExchange().authenticated()
```
Gateway-ul impune un control de acces grosier (coarse-grained). Calea `/api/chat/ws-chat/**` este permisă la nivel HTTP deoarece SockJS trebuie să finalizeze o negociere HTTP (handshake) înainte de a face trecerea la WebSocket — autentificarea propriu-zisă fiind apoi impusă la nivel de cadru STOMP de către `StompSecurityInterceptor`.
### Stratul `user-service` (`SecurityConfig.java` — MVC)
```java
.requestMatchers("/actuator/health").permitAll()
.requestMatchers("/users/auth/**").permitAll()
.requestMatchers(HttpMethod.PATCH, "/users/*/toggle-active").hasRole("admin")
.requestMatchers(HttpMethod.GET, "/users").hasRole("admin")
.anyRequest().authenticated()
```
Endpoint-ul pentru lista `/users` (care returnează toți utilizatorii cu filtre opționale după rol/status activ) este accesibil doar administratorilor. Endpoint-ul PATCH `toggle-active` este, de asemenea, doar pentru administratori. Toate celelalte endpoint-uri de utilizatori necesită un utilizator autentificat (care, de regulă, își accesează propriile date, având verificări de proprietate la nivelul logicii de business din stratul de servicii).
### Stratul `chat-service` (`SecurityConfig.java` — MVC)
```java
.requestMatchers("/actuator/health").permitAll()
.requestMatchers("/chat/ws-chat/**").permitAll()
.anyRequest().authenticated()
```
Calea pentru negocierea WebSocket este permisă la nivel HTTP. Toate endpoint-urile REST (lista de conversații, istoricul de mesaje, marcarea ca citit) necesită autentificare. Verificarea mai profundă a validității relației se face la nivelul logicii de business prin apelul Feign către `programari-service`.
### `@EnableMethodSecurity` și `@PreAuthorize`
Atât `user-service`, cât și `chat-service` declară `@EnableMethodSecurity`. Aceasta permite utilizarea adnotărilor de securitate la nivel de metodă, cum ar fi `@PreAuthorize("hasRole('terapeut')")` pe metode individuale din servicii sau controllere, oferind un control mai fin decât regulile bazate pe șabloane de URL-uri. Serviciul `programari-service` folosește intens acest model pentru a diferenția endpoint-urile accesibile pacienților de cele destinate terapeuților (e.g., doar terapeuții pot crea evaluări, doar pacienții pot scrie în jurnal pentru propriile programări).
### 2.5 Autentificarea WebSocket: `StompSecurityInterceptor`
Filtrele de securitate HTTP standard nu se aplică cadrelor WebSocket după negocierea HTTP inițială. `StompSecurityInterceptor` implementează `ChannelInterceptor` și este înregistrat pe canalul `clientInboundChannel` în `WebSocketConfig`:
```java
@Override
public void configureClientInboundChannel(ChannelRegistration registration) {
    registration.interceptors(securityInterceptor);
}
```
**Execuția `preSend()` (se lansează la fiecare cadru STOMP de intrare):**
1. Interceptorul citește comanda `StompCommand` din antetul cadrului.
2. Acționează asupra comenzilor `CONNECT` și `SEND` (cele două comenzi care trebuie autentificate).
3. Pentru `CONNECT`: browserul trebuie să includă antetul STOMP `Authorization: Bearer <token>`. Interceptorul îl extrage, apelează `jwtDecoder.decode(token)` (același decodificator bazat pe JWK ca la nivelul HTTP), creează un `JwtAuthenticationToken`, apelează `accessor.setUser(authentication)` pentru a asocia identitatea cu **sesiunea WebSocket** și populează `SecurityContextHolder` pentru thread-ul curent.
4. Pentru `SEND`: dacă cadrul conține un nou antet `Authorization`, se re-autentifică. Dacă nu, recurge la utilizatorul deja asociat cu accesorul de sesiune (`accessor.getUser()`).
5. `SecurityContextHolder.clearContext()` este apelat în `postSend()` pentru a preveni contaminarea thread pool-ului (reutilizarea thread pool-ului ar putea altfel scurge contextul de securitate al utilizatorului anterior).
**What happens if authentication fails?** Interceptorul înregistrează un avertisment (warning) în loguri și apelează `SecurityContextHolder.clearContext()`. Cadrul STOMP `CONNECT` este în continuare trimis către broker, dar fără a avea un utilizator autentificat setat pe sesiune. Orice handler ulterior de tip `@MessageMapping` care apelează `SecurityContextHolder.getContext().getAuthentication()` va primi `null`, determinând logica de business să respingă mesajul.
**Configurarea brokerului WebSocket (`WebSocketConfig.java`):**
- `enableSimpleBroker("/queue")` — broker în memorie pentru livrări specifice utilizatorilor (punct-la-punct)
- `setApplicationDestinationPrefixes("/app")` — prefix pentru mesajele rutate către handlere `@MessageMapping`
- Endpoint: `/chat/ws-chat` cu suport pentru fallback SockJS
### 2.6 Fluxul de Înregistrare: Scriere Dublă (Dual Write)
Procesul de înregistrare garantează consistența dintre Keycloak (baza de date de identități) și `user_db` (înregistrarea utilizatorului din aplicație) printr-o scriere dublă tranzacțională cu un mecanism de rollback compensatoriu.
**Pașii de la un capăt la altul:**
1. Pacientul completează formularul de înregistrare în `RegisterPage.jsx` și îl trimite. `authService.register()` trimite prin POST `{email, password, nume, prenume, telefon, gen, role: "PACIENT"}` sub formă de JSON la `POST /api/users/auth/register`.
2. API Gateway rutează cererea către `user-service` la `/users/auth/register` (permis fără autentificare).
3. `AuthController.register()` deleagă execuția către `KeycloakService.registerUser()`, metodă adnotată cu `@Transactional`.
4. **Validare:** Metoda verifică dacă `request.role() == UserRole.ADMIN` și aruncă `ForbiddenOperationException` în caz afirmativ — conturile de administrator nu pot fi create prin auto-înregistrare. De asemenea, verifică `userRepository.existsByEmail()` în baza de date locală.
5. **Crearea în Keycloak (`createUserInKeycloak`):** Folosește clientul Keycloak de admin pentru a apela API-ul REST Admin. Construiește un obiect `UserRepresentation` cu username=email, setează parola ca fiind una permanentă (non-temporary) în `CredentialRepresentation`, apoi apelează `usersResource.create(user)`. UUID-ul noului utilizator este extras din antetul de răspuns `Location`: `locationHeader.substring(locationHeader.lastIndexOf('/') + 1)`.
6. **Alocarea rolului în Keycloak (`assignRoleInKeycloak`):** Apelează `realmResource.roles().get(roleName)` (unde `roleName` este `"pacient"` sau `"terapeut"` — cu litere mici) și îl asociază noului utilizator prin `roles().realmLevel().add(...)`.
7. **Scrierea în baza de date locală:** Mapează `RegisterRequestDTO` la o entitate `User` prin `UserRegisterMapper`, setează `active=true` și salvează prin `userRepository.save()`. Valoarea `keycloakId` returnată la pasul 5 este stocată ca referință universală imutabilă între servicii.
8. **Inițializarea profilului de bază (`initializeRoleSpecificProfile`):** Efectuează un apel sincron `RestTemplate.postForEntity()` fie către `pacienti-service/pacient/initialize/{keycloakId}`, fie către `terapeuti-service/terapeut/initialize/{keycloakId}`. Aceasta creează o linie goală în baza de date a serviciului corespunzător, asigurând existența profilului înainte ca utilizatorul să se conecteze pentru prima dată.
9. **Rollback compensatoriu:** Întregul bloc este cuprins într-o structură `try/catch`. Dacă orice pas de după crearea în Keycloak eșuează (scrierea în DB locală sau inițializarea profilului extern eșuează), este apelată metoda `deleteUserInKeycloak(keycloakId)` pentru a elimina utilizatorul orfan din Keycloak. Aceasta este o **tranzacție compensatorie bazată pe efort maxim (best-effort)** — nu un protocol 2PC distribuit. Dacă ștergerea din Keycloak eșuează la rândul ei, se înregistrează un avertisment în loguri și este necesară intervenția manuală.
**Finalizarea profilului la prima conectare (`ProfileGuard`):**
După ce un PACIENT se conectează pentru prima dată, `AuthContext` marchează utilizatorul ca fiind autentificat. Înainte de a randa orice pagină clinică, se execută `ProfileGuard`:
```jsx
const profile = await profileService.getProfile();
if (profile.profileIncomplete === true) { setProfileComplete(false); }
else if (!profile.cnp || !profile.dataNasterii) { setProfileComplete(false); }
```
Flag-ul `profileIncomplete: true` este setat de `ProfileService` (în Gateway) atunci când `pacienti-service` returnează un cod 404 pentru profilul pacientului — ceea ce s-ar întâmpla doar dacă inițializarea ar fi eșuat. Verificarea secundară pentru `cnp` și `dataNasterii` acoperă cazul normal: profilul există, dar câmpurile clinice obligatorii sunt nule. Dacă profilul este incomplet, pacientul este redirecționat către `/pacient/complete-profile` — nicio altă rută de pacient nefiind accesibilă până când această verificare nu este trecută.
### 2.7 Fluxul de Recuperare a Parolei (Forgot Password)
Sistemul implementează un flux de recuperare a parolei asimetric pentru utilizatorii neautentificați, integrat direct cu serverul de identitate Keycloak. În loc să dezvolte un mecanism local, complex și susceptibil la erori de securitate (generare de token-uri tranzitorii, stocare în baza de date locală, expirare și programare manuală de ștergere, alături de template-uri de email localizate trimise din Spring), microserviciul `user-service` utilizează capabilitățile native de trimitere de email-uri administrative expuse de API-ul Keycloak.

#### **A. Arhitectura și Fluxul de Date pe Backend**
1. **Punctul de intrare (Controller):** Endpoint-ul `/users/auth/forgot-password` expus de `AuthController` preia cererea asincronă ce conține adresa de email a utilizatorului:
   ```java
   @PostMapping("/forgot-password")
   public ResponseEntity<Void> forgotPassword(@Valid @RequestBody ForgotPasswordRequestDTO request) {
       log.info("Cerere resetare parola pentru email: {}", request.email());
       keycloakService.sendForgotPasswordEmail(request.email());
       return ResponseEntity.noContent().build();
   }
   ```
2. **Identificarea și Delegarea (Service):** Clasa `KeycloakService` folosește clientul administrativ (`admin-cli`) pentru a interoga conturile din realm:
   - Caută utilizatorul după email: `usersResource.search(email, true)`.
   - Dacă utilizatorul nu este înregistrat, se aruncă o excepție `ResourceNotFoundException` (ceea ce se traduce pe client într-o eroare `404 Not Found`).
   - Dacă utilizatorul este găsit, se apelează metoda nativă a API-ului Keycloak Admin pe resursa specifică a utilizatorului:
     ```java
     usersResource.get(userId).executeActionsEmail(List.of("UPDATE_PASSWORD"));
     ```
3. **Mecanismul Keycloak:** Serverul Keycloak generează criptografic un link securizat, cu durată de viață limitată și utilizare unică, asamblează un email de actualizare a parolei bazat pe un template localizat și îl expediază prin serverul SMTP configurat. La accesarea link-ului, utilizatorul este redirecționat către o pagină securizată găzduită direct de Keycloak unde își poate seta noua parolă, garantând că parolele în clar nu tranzitează și nu sunt stocate în microserviciile KinetoCare.

#### **B. Fluxul pe Frontend și UI**
Pe frontend, comportamentul este încapsulat în `ForgotPasswordModal.jsx` și apelat prin clientul `authService.js`:
1. Utilizatorul apasă link-ul „Ai uitat parola?” din ecranul de login, deschizându-se o fereastră modală.
2. La trimiterea formularului, se apelează `authService.forgotPassword(email)` realizând o cerere `POST` asincronă către proxy-ul `/api/users/auth/forgot-password`.
3. Dacă adresa de email este validă, modalul afișează un mesaj de succes: *„Un email de resetare a fost trimis!”*.
4. Dacă backend-ul returnează o eroare 404 (email inexistent), eroarea este mapată prin clasa `AppError` și afișată în interfața modalului sub formă de avertisment.

*— Sfârșitul Secțiunilor 1 și 2 —*
*Se așteaptă comanda dumneavoastră pentru a continua cu Secțiunea 3: Logica Principală de Business — Fluxul de Rezervare a Programărilor (Core Business Logic — Appointment Booking Flow).*
