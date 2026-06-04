# Explicație Tehnică Completă: Sistemul de Profil pentru Aplicația de Kinetoterapie

## Cuprins
1. [Viziune de Ansamblu](#1-viziune-de-ansamblu)
2. [Arhitectura Sistemului](#2-arhitectura-sistemului)
3. [Flow-ul de Date](#3-flow-ul-de-date)
4. [Componente Backend](#4-componente-backend)
5. [Componente Frontend](#5-componente-frontend)
6. [Sincronizarea cu Keycloak](#6-sincronizarea-cu-keycloak)
7. [Cazuri Speciale și Logica de Business](#7-cazuri-speciale-și-logica-de-business)
8. [Securitate și Validări](#8-securitate-și-validări)
9. [Diagrame de Secvență Detaliate](#9-diagrame-de-secvență-detaliate)
10. [Best Practices Implementate](#10-best-practices-implementate)
11. [Posibile Îmbunătățiri](#11-posibile-îmbunătățiri)
12. [Rezumat Final](#12-rezumat-final)

---

## 1. Viziune de Ansamblu

### Ce face sistemul de profil?

Sistemul de profil gestionează datele de identificare, datele de contact și datele clinico-profesionale ale utilizatorilor din cadrul aplicației KinetoCare, oferind suport personalizat pentru două roluri distincte:
- **Profilul Terapeut**: Conține date personale (`nume`, `prenume`, `email`, `telefon`, `gen`) și detalii profesionale specifice (`specializare`, `pozaProfil`).
- **Profilul Pacient**: Reunește datele personale (`nume`, `prenume`, `email`, `telefon`, `gen`) cu date medicale și preferințe terapeutice (`dataNasterii`, `cnp`, `faceSport`, `detaliiSport`, `orasPreferat`, `locatiePreferataId`, `terapeutKeycloakId`).

### De ce avem nevoie de acest sistem?

1. **Separarea strictă a responsabilităților (Separation of Concerns)**: Profilurile de pacienți și cele de terapeuți implică seturi de date fundamental diferite. Izolarea lor previne încărcarea tabelelor cu atribute redundante sau nerelevante.
2. **Integrare și consistență cu Keycloak**: Sistemul de autentificare este legat direct de stocarea locală a utilizatorilor, datele comune de cont (email, nume, prenume) fiind sincronizate automat.
3. **Profilare progresivă**: Pacienții se pot înregistra utilizând doar credențialele de bază, fișa lor medicală și preferințele de tratament fiind completate ulterior într-o etapă dedicată, fără a bloca fluxul inițial de înregistrare.

---

## 2. Arhitectura Sistemului

### 2.1 Microservicii Implicate

```
┌─────────────────────────────────────────────────────────────┐
│                        KEYCLOAK                              │
│            (Sistem de autentificare extern)                  │
│         Stochează: email, firstName, lastName                │
└─────────────────────────────────────────────────────────────┘
                               ▲
                               │ Sincronizare
                               │
┌─────────────────────────────┼─────────────────────────────┐
│                    API GATEWAY                              │
│                (http://localhost:8080)                      │
│                                                             │
│  • Primește cereri de la frontend                          │
│  • Agregă date din mai multe servicii                      │
│  • Endpoint unic: /api/profile                             │
└─────────────────────────────────────────────────────────────┘
          │                    │                    │
          ▼                    ▼                    ▼
┌──────────────────┐ ┌──────────────────┐ ┌──────────────────┐
│   USER SERVICE   │ │ PACIENTI SERVICE │ │TERAPEUTI SERVICE │
│ (Port: 8082)     │ │ (Port: 8083)     │ │ (Port: 8084)     │
│                  │ │                  │ │                  │
│ Date pentru TOȚI │ │ Date medicale și │ │ Date specifice   │
│ utilizatorii:    │ │ preferințe doar  │ │ terapeutilor:    │
│ • nume, prenume  │ │ pentru pacienți: │ │ • specializare   │
│ • email, telefon │ │ • dataNasterii   │ │ • pozaProfil     │
│ • gen            │ │ • cnp, faceSport │ │                  │
│ • role, active   │ │ • detaliiSport   │ │                  │
└──────────────────┘ └──────────────────┘ └──────────────────┘
```

### 2.2 Justificarea Arhitecturii

- **Descentralizare pe modelul Database-per-Service**: 
  - `user-service` manipulează profilul general (comun) și stările de activare.
  - `pacienti-service` administrează date sensibile de natură clinică și preferențială.
  - `terapeuti-service` deține modelul dedicat medicilor și asistenților de recuperare.
  - *Avantaj*: Datele de profil sunt strict protejate și segmentate, evitându-se tabelele denormalizate de dimensiuni colosale sau interogările de tip `JOIN` trans-domeniu în baza de date SQL.
- **Gateway-ul ca Aggregator și Facade**:
  - Interfața utilizator (frontend-ul) execută un singur apel HTTP (`GET /api/profile`).
  - Gateway-ul orchestrează asincron cererile downstream către microservicii, combinând răspunsurile într-un singur obiect JSON unitar.
  - *Avantaj*: Reducerea numărului de conexiuni active deschise de browser și decuplarea totală a frontend-ului de topologia internă a microserviciilor.

---

## 3. Flow-ul de Date

### 3.1 Flow de Înregistrare (Registration)

```
1. Utilizatorul trimite datele prin formularul de înregistrare
   ↓
2. Backend-ul creează contul în KEYCLOAK (email, prenume, nume, parolă)
   ↓
3. Keycloak generează și returnează un keycloakId (UUID unic)
   ↓
4. USER SERVICE salvează utilizatorul local:
   (keycloakId, nume, prenume, email, telefon, gen, role, active)
   ↓
5. Dacă rolul este "PACIENT":
   Se inițializează automat o înregistrare goală (cu restul atributelor null) în PACIENTI SERVICE
   sub cheia pivot keycloakId, garantând prezența profilului la prima accesare.
```

### 3.2 Flow de Autentificare și Determinarea Profilului Incomplet

```
1. Utilizatorul se autentifică și primește jetonul JWT de la Keycloak
   ↓
2. Frontend-ul solicită: GET /api/profile (cu JWT în header)
   ↓
3. API Gateway citește keycloakId și rolul utilizatorului
   ↓
4. Gateway-ul solicită asincron datele din USER SERVICE
   ↓
5. Evaluare în funcție de ROL:
   - Pentru PACIENT: 
     a. Interoghează PACIENTI SERVICE. Dacă răspunsul întoarce 404 (NotFound), deduce că profilul este incomplet.
     b. Dacă profilul clinic este valid, verifică prezența terapeutKeycloakId și locatiePreferataId pentru a executa apeluri paralele (Zip) către TERAPEUTI SERVICE și USER SERVICE, obținând detaliile descriptive ale terapeutului curent (nume, prenume, specializare) și ale locației alese.
   - Pentru TERAPEUT:
     a. Interoghează TERAPEUTI SERVICE. Dacă nu este găsit profilul profesional, marchează contul cu profileIncomplete: true.
   ↓
6. Gateway-ul asamblează datele într-un singur răspuns. Dacă s-a dedus o lipsă a datelor clinico-profesionale, inserează atributul { profileIncomplete: true }.
   ↓
7. Frontend-ul reacționează:
   - Dacă profileIncomplete = true → redirecționare automată către /pacient/complete-profile (sau rutele aferente terapeutului).
   - Dacă profilul este complet → permite accesul securizat către dashboard.
```

---

## 4. Componente Backend

### 4.1 User Service - Gestiunea Contului de Bază

#### `UserController.java` (Fragmente de interes)
```java
@GetMapping("/by-keycloak/{keycloakId}")
public ResponseEntity<UserDTO> getUserByKeycloakId(@PathVariable String keycloakId) {
    return ResponseEntity.ok(userService.getUserByKeycloakId(keycloakId));
}

@PatchMapping("/{keycloakId}")
public ResponseEntity<UserDTO> updateUser(@PathVariable String keycloakId, @Valid @RequestBody UpdateUserDTO updateDTO) {
    return ResponseEntity.ok(userService.updateUser(keycloakId, updateDTO));
}
```

#### `UserService.java` (Tranzacționalitate și Sincronizare)
```java
@Transactional
public UserDTO updateUser(String keycloakId, UpdateUserDTO updateDTO) {
    User user = userRepository.findByKeycloakId(keycloakId)
            .orElseThrow(() -> new EntityNotFoundException("Utilizatorul nu există"));

    // Actualizare date prin mapare automată IGNORE_NULL
    userMapper.updateEntity(user, updateDTO);
    User updatedUser = userRepository.save(user);

    // Propagarea datelor în Keycloak dacă s-au modificat atribute de autentificare
    keycloakSyncService.updateKeycloakUser(
            keycloakId,
            updateDTO.getEmail(),
            updateDTO.getPrenume(),
            updateDTO.getNume()
    );

    return userMapper.toDTO(updatedUser);
}
```

#### `KeycloakSyncService.java` (Sincronizare de Identitate)
```java
public void updateKeycloakUser(String keycloakId, String email, String firstName, String lastName) {
    try {
        UserResource userResource = keycloak.realm(realm).users().get(keycloakId);
        UserRepresentation userRep = userResource.toRepresentation();
        boolean hasChanges = false;

        if (email != null && !email.equalsIgnoreCase(userRep.getEmail())) {
            userRep.setEmail(email);
            userRep.setUsername(email); // Username-ul este email-ul utilizatorului
            userRep.setEmailVerified(true);
            hasChanges = true;
        }
        if (firstName != null && !firstName.equals(userRep.getFirstName())) {
            userRep.setFirstName(firstName);
            hasChanges = true;
        }
        if (lastName != null && !lastName.equals(userRep.getLastName())) {
            userRep.setLastName(lastName);
            hasChanges = true;
        }

        if (hasChanges) {
            userResource.update(userRep);
            log.info("Sincronizare Keycloak reușită pentru UUID: {}", keycloakId);
        }
    } catch (Exception e) {
        log.error("Eroare la sincronizarea Keycloak pentru: {}. Datele locale rămân valide.", keycloakId, e);
    }
}
```

---

### 4.2 Pacienti Service - Gestiunea Fișei Medicale

#### `PacientController.java`
```java
@PostMapping("/initialize/{keycloakId}")
public ResponseEntity<Void> initializePacient(@PathVariable String keycloakId) {
    pacientService.initializeEmptyPacient(keycloakId);
    return ResponseEntity.status(HttpStatus.CREATED).build();
}

@PatchMapping("/{keycloakId}")
public ResponseEntity<PacientResponse> updatePacient(@PathVariable String keycloakId, @Valid @RequestBody PacientRequest request) {
    return ResponseEntity.ok(pacientService.updatePacient(keycloakId, request));
}
```

#### `PacientService.java`
```java
@Transactional
public void initializeEmptyPacient(String keycloakId) {
    if (pacientRepository.existsByKeycloakId(keycloakId)) {
        return;
    }
    Pacient emptyPacient = Pacient.builder()
            .keycloakId(keycloakId)
            .build();
    pacientRepository.save(emptyPacient);
}

@Transactional
public PacientResponse updatePacient(String keycloakId, PacientRequest request) {
    Pacient pacient = pacientRepository.findByKeycloakId(keycloakId)
            .orElseThrow(() -> new EntityNotFoundException("Profilul clinic nu există"));

    // Validare CNP unic la nivel de sistem
    if (request.getCnp() != null && !request.getCnp().equals(pacient.getCnp())) {
        if (pacientRepository.existsByCnp(request.getCnp())) {
            throw new IllegalArgumentException("Acest CNP este deja înregistrat în sistem");
        }
    }

    // Mapare
    pacientMapper.updateEntity(pacient, request);
    return pacientMapper.toResponse(pacientRepository.save(pacient));
}
```

---

### 4.3 API Gateway - Agregarea și Compoziția Jetoanelor

Gateway-ul folosește instanțe dedicate și injectate calificate de `WebClient` pentru a comunica eficient cu microserviciile interne, evitând crearea repetitivă de clienți.

#### `WebClientConfig.java` (Definirea Bean-urilor)
```java
@Configuration
public class WebClientConfig {
    @Bean
    public WebClient.Builder webClientBuilder() {
        return WebClient.builder().codecs(c -> c.defaultCodecs().maxInMemorySize(10 * 1024 * 1024));
    }

    @Bean("userWebClient")
    public WebClient userWebClient(@Value("${application.urls.user-service}") String url, WebClient.Builder builder) {
        return builder.baseUrl(url).build();
    }

    @Bean("pacientiWebClient")
    public WebClient pacientiWebClient(@Value("${application.urls.pacienti-service}") String url, WebClient.Builder builder) {
        return builder.baseUrl(url).build();
    }

    @Bean("terapeutiWebClient")
    public WebClient terapeutiWebClient(@Value("${application.urls.terapeuti-service}") String url, WebClient.Builder builder) {
        return builder.baseUrl(url).build();
    }
}
```

#### `ProfileService.java` (Logica de Compoziție Asincronă)
```java
@Service
public class ProfileService {
    private final WebClient userWebClient;
    private final WebClient pacientiWebClient;
    private final WebClient terapeutiWebClient;
    private final SecurityUtils securityUtils;

    public ProfileService(@Qualifier("userWebClient") WebClient userWebClient,
                          @Qualifier("pacientiWebClient") WebClient pacientiWebClient,
                          @Qualifier("terapeutiWebClient") WebClient terapeutiWebClient,
                          SecurityUtils securityUtils) {
        this.userWebClient = userWebClient;
        this.pacientiWebClient = pacientiWebClient;
        this.terapeutiWebClient = terapeutiWebClient;
        this.securityUtils = securityUtils;
    }

    public Mono<Map<String, Object>> getProfile(String keycloakId, String role) {
        return securityUtils.getJwtToken().flatMap(token -> {
            
            // 1. Preluare date de bază comune din user-service
            Mono<Map<String, Object>> userDataMono = userWebClient.get()
                    .uri("/users/by-keycloak/{id}", keycloakId)
                    .header("Authorization", "Bearer " + token)
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {});

            // 2. Logica de agregare specifică rolului Pacient
            if ("PACIENT".equals(role)) {
                Mono<Map<String, Object>> pacientDataMono = pacientiWebClient.get()
                        .uri("/pacient/by-keycloak/{id}", keycloakId)
                        .header("Authorization", "Bearer " + token)
                        .retrieve()
                        .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                        .onErrorResume(WebClientResponseException.NotFound.class, e -> {
                            Map<String, Object> incomplete = new HashMap<>();
                            incomplete.put("profileIncomplete", true);
                            return Mono.just(incomplete);
                        });

                return Mono.zip(userDataMono, pacientDataMono).flatMap(tuple -> {
                    Map<String, Object> userData = tuple.getT1();
                    Map<String, Object> pacientData = tuple.getT2();

                    if (Boolean.TRUE.equals(pacientData.get("profileIncomplete"))) {
                        userData.put("profileIncomplete", true);
                        return Mono.just(userData);
                    }

                    pacientData.remove("keycloakId");
                    pacientData.remove("id");
                    userData.putAll(pacientData);

                    // Agregare Terapeut și Locație dacă acestea există în fișa pacientului
                    String terapeutId = (String) pacientData.get("terapeutKeycloakId");
                    Mono<Map<String, Object>> terapeutMono = Mono.just(Map.of());
                    if (terapeutId != null && !terapeutId.isEmpty()) {
                        Mono<Map<String, Object>> infoProf = terapeutiWebClient.get()
                                .uri("/terapeut/by-keycloak/{id}", terapeutId)
                                .header("Authorization", "Bearer " + token)
                                .retrieve()
                                .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                                .onErrorResume(err -> Mono.just(Map.of()));

                        Mono<Map<String, Object>> infoUser = userWebClient.get()
                                .uri("/users/by-keycloak/{id}", terapeutId)
                                .header("Authorization", "Bearer " + token)
                                .retrieve()
                                .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                                .onErrorResume(err -> Mono.just(Map.of()));

                        terapeutMono = Mono.zip(infoProf, infoUser).map(tTuple -> {
                            Map<String, Object> merged = new HashMap<>(tTuple.getT1());
                            merged.put("nume", tTuple.getT2().get("nume"));
                            merged.put("prenume", tTuple.getT2().get("prenume"));
                            return merged;
                        });
                    }

                    Number locIdNum = (Number) pacientData.get("locatiePreferataId");
                    Mono<Map<String, Object>> locatieMono = Mono.just(Map.of());
                    if (locIdNum != null) {
                        locatieMono = terapeutiWebClient.get()
                                .uri("/locatii/{id}", locIdNum.longValue())
                                .header("Authorization", "Bearer " + token)
                                .retrieve()
                                .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                                .onErrorResume(err -> Mono.just(Map.of()));
                    }

                    return Mono.zip(terapeutMono, locatieMono).map(detailsTuple -> {
                        if (!detailsTuple.getT1().isEmpty()) userData.put("terapeutDetalii", detailsTuple.getT1());
                        if (!detailsTuple.getT2().isEmpty()) userData.put("locatieDetalii", detailsTuple.getT2());
                        return userData;
                    });
                });
            }

            // 3. Logica de agregare specifică rolului Terapeut
            if ("TERAPEUT".equals(role)) {
                Mono<Map<String, Object>> terapeutDataMono = terapeutiWebClient.get()
                        .uri("/terapeut/by-keycloak/{id}", keycloakId)
                        .header("Authorization", "Bearer " + token)
                        .retrieve()
                        .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                        .onErrorResume(e -> {
                            Map<String, Object> incomplete = new HashMap<>();
                            incomplete.put("profileIncomplete", true);
                            return Mono.just(incomplete);
                        });

                return Mono.zip(userDataMono, terapeutDataMono).map(tuple -> {
                    Map<String, Object> userData = tuple.getT1();
                    Map<String, Object> terapeutData = tuple.getT2();

                    if (terapeutData.containsKey("id")) {
                        userData.put("terapeutId", terapeutData.get("id"));
                    }
                    if (Boolean.TRUE.equals(terapeutData.get("profileIncomplete"))) {
                        userData.put("profileIncomplete", true);
                        return userData;
                    }

                    terapeutData.remove("keycloakId");
                    terapeutData.remove("id");
                    userData.putAll(terapeutData);
                    return userData;
                });
            }

            return userDataMono;
        });
    }

    public Mono<Map<String, Object>> updateProfile(String keycloakId, String role, Map<String, Object> updateData) {
        return securityUtils.getJwtToken().flatMap(token -> {
            Map<String, Object> userUpdate = extractUserFields(updateData);
            Map<String, Object> pacientUpdate = extractPacientFields(updateData);
            Map<String, Object> terapeutUpdate = extractTerapeutFields(updateData);

            List<Mono<Void>> updates = new ArrayList<>();

            if (!userUpdate.isEmpty()) {
                updates.add(userWebClient.patch().uri("/users/{id}", keycloakId)
                        .header("Authorization", "Bearer " + token)
                        .bodyValue(userUpdate).retrieve().bodyToMono(Void.class));
            }
            if ("PACIENT".equals(role) && !pacientUpdate.isEmpty()) {
                updates.add(pacientiWebClient.patch().uri("/pacient/{id}", keycloakId)
                        .header("Authorization", "Bearer " + token)
                        .bodyValue(pacientUpdate).retrieve().bodyToMono(Void.class));
            }
            if ("TERAPEUT".equals(role) && !terapeutUpdate.isEmpty()) {
                updates.add(terapeutiWebClient.patch().uri("/terapeut/{id}", keycloakId)
                        .header("Authorization", "Bearer " + token)
                        .bodyValue(terapeutUpdate).retrieve().bodyToMono(Void.class));
            }

            if (updates.isEmpty()) {
                return getProfile(keycloakId, role);
            }

            return Mono.when(updates).then(getProfile(keycloakId, role));
        });
    }

    private Map<String, Object> extractUserFields(Map<String, Object> data) {
        Map<String, Object> fields = new HashMap<>();
        List.of("nume", "prenume", "gen", "email", "telefon").forEach(k -> {
            if (data.containsKey(k)) fields.put(k, data.get(k));
        });
        return fields;
    }

    private Map<String, Object> extractPacientFields(Map<String, Object> data) {
        Map<String, Object> fields = new HashMap<>();
        List.of("dataNasterii", "cnp", "faceSport", "detaliiSport", "orasPreferat", "locatiePreferataId", "terapeutKeycloakId")
                .forEach(k -> {
                    if (data.containsKey(k)) fields.put(k, data.get(k));
                });
        return fields;
    }

    private Map<String, Object> extractTerapeutFields(Map<String, Object> data) {
        Map<String, Object> fields = new HashMap<>();
        List.of("specializare", "pozaProfil").forEach(k -> {
            if (data.containsKey(k)) fields.put(k, data.get(k));
        });
        return fields;
    }
}
```

---

## 5. Componente Frontend

### 5.1 Serviciul de Client HTTP: `profileService.js`

```javascript
import api, { handleApiError } from './api';

export const profileService = {
  getProfile: async () => {
    try {
      const response = await api.get('/api/profile');
      return response.data;
    } catch (error) {
      handleApiError(error, 'Eroare la încărcarea profilului');
    }
  },

  updateProfile: async (data) => {
    try {
      const response = await api.patch('/api/profile', data);
      return response.data;
    } catch (error) {
      handleApiError(error, 'Eroare la actualizarea profilului');
    }
  }
};
```

### 5.2 Gărzile de Securitate în Rute: `ProfileGuard.jsx`

Acest paznic de rută interceptează tranzitul utilizatorului și blochează accesul la dashboard dacă datele medicale (cum ar fi CNP sau Data Nașterii) lipsesc, forțând redirecționarea acestuia către ecranul de completare a profilului.

```jsx
import { useEffect, useState } from 'react';
import { Navigate, Outlet } from 'react-router-dom';
import { profileService } from '../../services/profileService';

export default function ProfileGuard() {
  const [loading, setLoading] = useState(true);
  const [profileComplete, setProfileComplete] = useState(false);
  const [error, setError] = useState(null);

  useEffect(() => {
    checkProfile();
  }, []);

  const checkProfile = async () => {
    try {
      const profile = await profileService.getProfile();
      
      if (profile.profileIncomplete === true) {
        setProfileComplete(false);
      }
      // Validare de fallback pe atributele pivot
      else if (profile.profileIncomplete == null && (!profile.cnp || !profile.dataNasterii)) {
        setProfileComplete(false);
      }
      else {
        setProfileComplete(true);
      }
    } catch (error) {
      console.error('Eroare verificare profil:', error);
      setError("Serviciul de verificare este momentan indisponibil.");
    } finally {
      setLoading(false);
    }
  };

  if (loading) return <div className="loading-spinner">Se validează contul...</div>;
  if (error) return <div className="error-message">{error}</div>;

  return profileComplete ? <Outlet /> : <Navigate to="/pacient/complete-profile" replace />;
}
```

### 5.3 ProfilPacient.jsx (Editare Diferențială Inteligentă)

Pentru a nu irosi bandă și resurse și a nu introduce riscuri de suprascriere accidentală în alte microservicii, interfața determină câmpurile strict modificate față de starea inițială și expediază doar diferențele (delta):

```javascript
const handleSubmit = async (e) => {
  e.preventDefault();
  
  const normalizedOriginal = {
    ...profile,
    dataNasterii: profile.dataNasterii ? profile.dataNasterii.split('T')[0] : ''
  };

  const dataToSend = {};
  let hasChanges = false;

  Object.keys(formData).forEach(key => {
    if (formData[key] !== normalizedOriginal[key]) {
      dataToSend[key] = formData[key];
      hasChanges = true;
    }
  });

  // Business Logic: dacă sportul este anulat, detaliile trebuie resetate explicit la null pe backend
  if (dataToSend.faceSport === 'NU') {
    dataToSend.detaliiSport = null;
    hasChanges = true;
  }

  if (!hasChanges) {
    setIsEditing(false);
    return;
  }

  const updatedProfile = await profileService.updateProfile(dataToSend);
  setProfile(updatedProfile);
  setIsEditing(false);
};
```

---

## 6. Sincronizarea cu Keycloak

Keycloak funcționează ca un server de identitate centralizat care administrează fluxurile de autentificare direct pe baza atributelor `email`, `firstName` (prenume) și `lastName` (nume). Dacă utilizatorul decide să își schimbe adresa de email în cadrul panoului său de control, sistemul trebuie să reflecte modificarea în Keycloak pentru a asigura funcționarea corectă a procesului de login.

```
                  ┌──────────────────────┐
                  │   Profil Utilizator  │ (Schimbă email-ul în interfață)
                  └──────────┬───────────┘
                             │ PATCH /api/profile
                             ▼
                  ┌──────────────────────┐
                  │      API Gateway     │ (Direcționează către User Service)
                  └──────────┬───────────┘
                             │ PATCH /users/{keycloakId}
                             ▼
                  ┌──────────────────────┐
                  │     User Service     │ (Actualizează baza de date)
                  └──────────┬───────────┘
                             │
            ┌────────────────┴────────────────┐
            ▼ (asincron / try-catch)          ▼
┌────────────────────────┐        ┌────────────────────────┐
│     Baza de Date       │        │  KeycloakSyncService   │
│   (Consistență SQL)    │        │ (Update email & u-name)│
└────────────────────────┘        └────────────────────────┘
```

Tranzacția locală SQL nu depinde direct de răspunsul asincron al rețelei externe către Keycloak. În eventualitatea în care rețeaua Keycloak experimentează o întrerupere temporară, excepția este captată, se emite un avertisment în loguri, dar profilul din baza de date a `user-service` rămâne salvat cu succes, respectând principiul toleranței la erori.

---

## 7. Cazuri Speciale și Logica de Business

### 7.1 Cazul: Pacient cu Profil Incomplet
- **Scenariu**: Un utilizator își înregistrează contul. Din motive de UX rapid, nu i se cere CNP-ul sau data nașterii pe loc.
- **Soluție**: La înregistrare, serviciul `pacienti-service` creează o înregistrare minimă stocând doar atributul `keycloakId`. Atunci când API Gateway interoghează fișa, detectează valorile lipsă (sau flagul returnat 404/NotFound transformat în indicatorul `profileIncomplete: true`). Rutarea pe frontend blochează restul activităților utilizatorului până la completarea formularului de fișă medicală.

### 7.2 Cazul: Update cu Sport "NU" → "DA" → "NU"
- **Scenariu**: Pacientul bifează "Fac sport: DA" și adaugă detalii: "Tenis de câmp, 2 ori pe săptămână". Ulterior revine și modifică la "Fac sport: NU".
- **Soluție**: Pe frontend, trecerea la "NU" curăță instantaneu bufferul de text. La trimitere, cheia `detaliiSport` este forțată la valoarea `null`. Pe backend, maparea cu `NullValuePropertyMappingStrategy.SET_NULL` suprascrie valoarea anterioară din coloană, garantând că nu vor rămâne reziduuri medicale false în baza de date.

### 7.3 Cazul: Prevenirea CNP-urilor Duplicate
- **Scenariu**: Doi utilizatori complet diferiți încearcă să salveze aceeași valoare CNP în contul lor.
- **Soluție**: Pe lângă constrângerea de unicitate de la nivel fizic din baza de date (`UNIQUE KEY`), microserviciul `pacienti-service` execută o interogare preventivă în `updatePacient` apelând `pacientRepository.existsByCnp(cnp)`. În caz de coliziune, procesul este anulat tranzacțional și se returnează o excepție clară cu mesaj de eroare prietenos.

---

## 8. Securitate și Validări

Sistemul aplică un model de validare pe trei niveluri (Defense in Depth):

### Nivelul 1: Validare Declarativă și Strictă în Backend
Fiecare DTO folosește adnotări specifice din `jakarta.validation.constraints` pentru a asigura integritatea datelor înainte de procesare:

```java
public class PacientRequest {
    @NotNull(message = "CNP-ul nu poate lipsi")
    @Pattern(regexp = "^[0-9]{13}$", message = "CNP-ul trebuie să conțină exact 13 cifre")
    private String cnp;

    @NotNull(message = "Data nașterii este obligatorie")
    @Past(message = "Data nașterii trebuie să fie în trecut")
    private LocalDate dataNasterii;
}
```

### Nivelul 2: Securizarea Jetoanelor și Validarea din Rute
Fiecare request direcționat către endpoint-ul `/api/profile` conține header-ul `Authorization: Bearer <JWT>`.
1. API Gateway validează semnătura jetonului.
2. Extrage claim-ul `sub` (corespunzător cu `keycloakId` global).
3. Transmite securizat prin antetul `Authorization` token-ul de acces către microserviciile downstream, unde fiecare interogare se face pe baza UUID-ului din token, prevenind tentativele de tip IDOR (Insecure Direct Object Reference) în care un utilizator ar încerca să citească sau să modifice profilul altui pacient.

### Nivelul 3: Validarea Datelor pe Frontend
Interfața folosește proprietăți HTML5 native (`required`, `pattern`, `max` pe elemente de tip dată) combinate cu validări JavaScript la submit pentru a oferi un feedback UX optim și instantaneu:

```javascript
const validateCNP = (cnp) => {
  return /^[0-9]{13}$/.test(cnp);
};
```

---

## 9. Diagrame de Secvență Detaliate

### 9.1 Secvența: GET Profile (Pentru Pacientul Autentificat)

Această diagramă ilustrează modul în care API Gateway orchestrează interogările downstream asincrone în paralel pentru a compune profilul complet, inclusiv detaliile despre terapeut și locație:

```
┌─────────┐       ┌─────────┐       ┌──────────┐       ┌──────────┐       ┌──────────┐
│ Browser │       │ Gateway │       │   User   │       │ Pacienti │       │Terapeuti │
│         │       │         │       │ Service  │       │ Service  │       │ Service  │
└────┬────┘       └────┬────┘       └────┬─────┘       └────┬─────┘       └────┬─────┘
     │                 │                 │                  │                  │
     │ GET /api/profile│                 │                  │                  │
     │ (cu Bearer JWT) │                 │                  │                  │
     ├────────────────>│                 │                  │                  │
     │                 │                 │                  │                  │
     │                 │ 1. Extrage keycloakId & rol        │                  │
     │                 │                 │                  │                  │
     │                 │ 2. GET /users/by-keycloak/{id}     │                  │
     │                 ├────────────────>│                  │                  │
     │                 │                 │                  │                  │
     │                 │ 3. GET /pacient/by-keycloak/{id}   │                  │
     │                 ├───────────────────────────────────>│                  │
     │                 │                 │                  │                  │
     │                 │ 4. Întoarce date de bază           │                  │
     │                 │<────────────────┤                  │                  │
     │                 │                 │                  │                  │
     │                 │ 5. Întoarce date medicale          │                  │
     │                 │<───────────────────────────────────┤                  │
     │                 │                                    │                  │
     │                 │ ─── DACĂ ARE TERAPEUT SELECTAT ─────────────────────┐ │
     │                 │                                                     │ │
     │                 │ 6. GET /terapeut/by-keycloak/{id}                   │ │
     │                 ├────────────────────────────────────────────────────>│ │
     │                 │ 7. GET /users/by-keycloak/{idTerapeut}              │ │
     │                 ├────────────────>│                                   │ │
     │                 │                 │                                   │ │
     │                 │ 8. Întoarce date profesionale                       │ │
     │                 │<────────────────────────────────────────────────────┤ │
     │                 │ 9. Întoarce date personale                          │ │
     │                 │<────────────────┤                                   │ │
     │                 │ ────────────────────────────────────────────────────┘ │
     │                 │                                                       │
     │                 │ ─── DACĂ ARE LOCAȚIE PREFERATĂ ─────────────────────┐ │
     │                 │ 10. GET /locatii/{locatieId}                        │ │
     │                 ├────────────────────────────────────────────────────>│ │
     │                 │ 11. Întoarce detalii locație                        │ │
     │                 │<────────────────────────────────────────────────────┤ │
     │                 │ ────────────────────────────────────────────────────┘ │
     │                 │                                                       │
     │                 │ 12. Agregă & combină maps în JSON final               │
     │                 │                                                       │
     │ Răspuns profil  │                                                       │
     │ complet payload │                                                       │
     │<────────────────┤                                                       │
     │                 │                                                       │
```

---

## 10. Best Practices Implementate

1. **Modelare DTO distinctă**:
   - `PacientRequest`: DTO curat dedicat exclusiv datelor de intrare (modificabile).
   - `PacientResponse`: Structură securizată pentru output-uri.
2. **Procesare Reactivă Asincronă**:
   - Gateway-ul folosește `Mono.zip()` pentru a executa în paralel apelurile HTTP downstream către `user-service`, `pacienti-service` și `terapeuti-service`. Această abordare reduce latența generală de la o sumă a latențelor ($L_{user} + L_{pacient}$) la valoarea latenței celei mai lente cereri ($\max(L_{user}, L_{pacient})$).
3. **Decuplare tranzacțională pentru Keycloak**:
   - Eșecurile de conexiune sau erorile temporare din Keycloak nu blochează salvarea datelor în baza de date MySQL locală, crescând disponibilitatea sistemului (fault-tolerance).
4. **Validări pe Niveluri Multiple (Defense in Depth)**:
   - Validări HTML5 pe client, reguli dinamice de interfață (ascunderea câmpului de detalii sport dacă nu se face sport) și validări declarative stricte prin Jakarta Validation pe backend.

---

## 11. Posibile Îmbunătățiri

1. **Algoritm de Validare Matematică a CNP-ului**:
   - Înlocuirea validării simple prin expresii regulate a lungimii de 13 cifre cu algoritmul național matematic de validare (calculat pe baza constantei de control `279146358279`).
2. **Coadă de Mesaje asincronă pentru Sincronizarea Keycloak**:
   - Utilizarea brokerului de mesaje RabbitMQ pentru a trimite actualizările de email către Keycloak prin cozi persistente. În acest mod, dacă serverul Keycloak este oprit pentru mentenanță, mesajul rămâne blocat securizat în coadă și se reîncearcă automat sincronizarea la pornire, obținându-se consistență eventuală garantată.
3. **Caching distribuit (Redis)**:
   - Cache-ul profilului de bază (comun) la nivel de Gateway pentru a reduce interogările repetate în `user-service` în timpul navigărilor rapide ale utilizatorului.

---

## 12. Rezumat Final

Sistemul de profil din aplicația KinetoCare reprezintă o implementare avansată și stabilă a tiparului de agregare pe microservicii:
- **Datele sunt izolate logic și fizic** în 3 scheme distincte (`user_db`, `pacienti_db`, `terapeuti_db`), eliminând cuplajul strâns de date.
- **Gateway-ul funcționează ca agregator reactiv**, eliberând complet frontend-ul de complexitatea rutării și simplificând structura de interogare.
- **Validările solide pe multiple straturi** garantează securitatea datelor de identificare sensibile ale pacienților (cum ar fi CNP).
- **Tranzacționalitatea robustă** asigură persistența locală fără a depinde critic de disponibilitatea momentană a serverelor Keycloak externe.