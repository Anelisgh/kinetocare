# Capitolul 15: Auditul Tehnic și Detalii de Implementare a Sistemului KinetoCare

Acest document reprezintă o culegere de note tehnice rezultate în urma auditului arhitectural și de cod al platformei **KinetoCare**. Scopul său este de a detalia deciziile luate la nivel de design de cod, mecanismele de securitate active, consistența distribuită și logica temporală critică, exemplificând cu secvențe directe de cod din implementare.

---

## 1. Securitate și Gestiunea Identității

### 1.1 Integrarea Keycloak și Fluxul de Autentificare Headless
KinetoCare folosește Keycloak drept Identity Provider extern pentru a asigura delegarea completă a autentificării și stocării sigure a credențialelor. Integrarea se realizează printr-un model headless:
* Utilizatorul își introduce credențialele direct în clientul React (în ecranul de Login).
* Clientul trimite cererea la API Gateway.
* Gateway-ul acționează ca un proxy securizat (BFF Pattern), trimițând cererea către endpoint-ul de token al Keycloak (`/realms/kinetocare/protocol/openid-connect/token`).
* **Refresh Token-ul** obținut este extras de către API Gateway și plasat într-un cookie securizat de tip `HttpOnly` cu opțiunile `Secure` și `SameSite=Lax`. Acest lucru face ca token-ul de reîmprospătare să fie complet inaccesibil scripturilor JavaScript din browser (prevenind atacurile de tip XSS).
* **Access Token-ul** (scurt-trăitor, ~5 minute) este trimis în corpul răspunsului HTTP și stocat în memoria RAM a aplicației React, fiind atașat ulterior fiecărei cereri prin interceptorii Axios.

### 1.2 Securizarea Conexiunilor WebSocket/STOMP și Protecția Contextului
În mod implicit, filtrele Spring Security (`OncePerRequestFilter`) sunt legate direct de ciclul HTTP Request-Response. Odată ce o conexiune WebSocket este inițiată și se face upgrade-ul protocolului HTTP la TCP persistent, cererile ulterioare (frame-uri STOMP de tip `CONNECT` sau `SEND`) nu mai trec prin filtrele HTTP clasice.

Pentru a asigura controlul accesului (RBAC) și propagarea corectă a contextului de securitate către apelurile downstream de tip OpenFeign în interiorul microserviciului `chat-service`, s-a implementat clasa [StompSecurityInterceptor.java](file:///c:/Users/Aneliss/Desktop/kinetocare/backend/chat-service/src/main/java/com/example/chat_service/config/StompSecurityInterceptor.java):

```java
package com.example.chat_service.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class StompSecurityInterceptor implements ChannelInterceptor {

    private final JwtDecoder jwtDecoder;
    private final JwtAuthenticationConverter jwtAuthenticationConverter;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        
        if (accessor != null && accessor.getCommand() != null) {
            if (StompCommand.CONNECT.equals(accessor.getCommand()) || StompCommand.SEND.equals(accessor.getCommand())) {
                String authHeader = accessor.getFirstNativeHeader("Authorization");
                
                if (authHeader != null && authHeader.startsWith("Bearer ")) {
                    String token = authHeader.substring(7);
                    try {
                        Jwt jwt = jwtDecoder.decode(token);
                        JwtAuthenticationToken authentication = new JwtAuthenticationToken(
                                jwt, 
                                jwtAuthenticationConverter.convert(jwt).getAuthorities()
                        );
                        
                        // Setează utilizatorul la nivelul sesiunii WebSocket
                        accessor.setUser(authentication);
                        
                        // Populează contextul pe firul de execuție curent (necesar pentru OpenFeign downstream)
                        SecurityContextHolder.getContext().setAuthentication(authentication);
                        
                        log.debug("Securitatea STOMP (JWT) mapată cu succes pentru comanda {}", accessor.getCommand());
                    } catch (Exception e) {
                        log.warn("Preluare JWT eșuată în STOMP preSend: {}", e.getMessage());
                        SecurityContextHolder.clearContext();
                    }
                } else if (accessor.getUser() instanceof org.springframework.security.core.Authentication auth) {
                    // Dacă utilizatorul a fost deja setat în sesiunea curentă în timpul frame-ului CONNECT
                    SecurityContextHolder.getContext().setAuthentication(auth);
                    log.debug("Securitatea STOMP preluată automat din accesoriu pentru comanda {}", accessor.getCommand());
                }
            }
        }
        return message;
    }

    @Override
    public void postSend(Message<?> message, MessageChannel channel, boolean sent) {
        // CURĂȚARE CRITICĂ: Previne scurgerea contextului de securitate (Security Context Leakage)
        // în pool-ul de fire de execuție reutilizate de serverul de WebSocket
        SecurityContextHolder.clearContext();
    }
}
```

> [!WARNING]
> Metoda `postSend` apelând `SecurityContextHolder.clearContext()` este vitală. Thread-urile folosite de motorul Spring Messaging pentru a procesa mesajele STOMP sunt obținute dintr-un pool comun (`ThreadPoolTaskExecutor`). Dacă contextul nu ar fi curățat la finalul fiecărui cadru trimis, un alt utilizator care trimite un mesaj procesat de același thread ar putea moșteni din greșeală identitatea precedentă.

---

## 2. Tranzacții și Consistență Distribuită

### 2.1 Tranzacții de Compensare la Înregistrare (Dual-Write)
Înregistrarea unui nou cont implică scrierea în două medii distincte: crearea contului în Keycloak și inserarea profilului în baza de date locală `user_service_db`, urmată de inițializarea unui profil gol în microserviciul dedicat rolului (`pacienti-service` sau `terapeuti-service`).

Deoarece nu putem înrola un sistem extern de IAM (Keycloak) într-o tranzacție globală XA (Two-Phase Commit), consistența se obține prin implementarea unui **mecanism de compensare best-effort** în clasa [KeycloakService.java](file:///c:/Users/Aneliss/Desktop/kinetocare/backend/user-service/src/main/java/com/example/user_service/service/KeycloakService.java):

```java
@Transactional
public RegisterResponseDTO registerUser(RegisterRequestDTO request) {
    if (request.role() == UserRole.ADMIN) {
        log.warn("Încercare de înregistrare a unui cont ADMIN eșuată. Email: {}", request.email());
        throw new ForbiddenOperationException("Înregistrarea conturilor de admin nu este permisă.");
    }
    if (userRepository.existsByEmail(request.email())) {
        throw new ResourceAlreadyExistsException("Email-ul este deja înregistrat");
    }

    String keycloakId = null;
    try {
        // Pasul 1: Crearea utilizatorului în Keycloak
        keycloakId = createUserInKeycloak(request);

        // Pasul 2: Atribuirea rolului în Keycloak
        assignRoleInKeycloak(keycloakId, request.role());

        // Pasul 3: Crearea utilizatorului în baza de date locală (SQL)
        User user = userRegisterMapper.toEntity(request, keycloakId);
        user.setActive(true);
        userRepository.save(user);
        log.info("Utilizator înregistrat cu succes: {} cu rolul: {}", request.email(), request.role());

        // Pasul 4: Inițializarea profilului gol (sincron, prin HTTP)
        initializeRoleSpecificProfile(keycloakId, request.role());

        return userRegisterMapper.toRegisterResponse(user, "Cont creat cu succes!");
    } catch (Exception e) {
        // LOGICĂ DE COMPENSARE (Rollback)
        log.error("Eroare înregistrare utilizator, se revine la starea inițială în Keycloak la nevoie", e);
        if (keycloakId != null) {
            log.warn("Se încearcă ștergerea utilizatorului Keycloak {} din cauza erorii de înregistrare locală.", keycloakId);
            try {
                deleteUserInKeycloak(keycloakId);
            } catch (Exception ex) {
                log.error("CRITICAL: Ștergerea utilizatorului Keycloak {} a eșuat în timpul rollback-ului. " +
                        "Este necesară eliminarea sa manuală din consola Keycloak.", keycloakId, ex);
            }
        }
        throw new ExternalServiceException("Eroare la înregistrare: " + e.getMessage(), e);
    }
}
```

### 2.2 Utilizarea RestTemplate vs. OpenFeign
Pentru inițializarea profilelor în microserviciile downstream (`pacienti-service` sau `terapeuti-service`), `user-service` face o cerere HTTP sincronă.
* **De ce RestTemplate și nu OpenFeign?** În momentul înregistrării utilizatorului, fluxul de securitate se află într-o fază preliminară. Niciun utilizator nu este autentificat oficial, prin urmare nu există un token JWT în context. Clasa `FeignClientConfig` este configurată să extragă automat JWT-ul din `SecurityContextHolder` și să îl propage în headers. Dacă s-ar folosi Feign, apelul inter-servicii ar fi respins cu `401 Unauthorized` de către microserviciile destinație.
* `RestTemplate` este configurat local în `user-service` ca un utilitar fără interceptor de securitate, permițând scrierea directă a profilului de tip schiță:

```java
private void initializeRoleSpecificProfile(String keycloakId, UserRole role) {
    try {
        if (role == UserRole.PACIENT) {
            String url = org.springframework.web.util.UriComponentsBuilder
                    .fromUriString(pacientiServiceUrl)
                    .path("/pacient/initialize/{id}")
                    .buildAndExpand(keycloakId)
                    .toUriString();
            restTemplate.postForEntity(url, null, Void.class);
        } else if (role == UserRole.TERAPEUT) {
            String url = org.springframework.web.util.UriComponentsBuilder
                    .fromUriString(terapeutiServiceUrl)
                    .path("/terapeut/initialize/{id}")
                    .buildAndExpand(keycloakId)
                    .toUriString();
            restTemplate.postForEntity(url, null, Void.class);
        }
    } catch (Exception e) {
        log.error("Eroare la inițializarea profilului pentru rolul {} și id {}: {}", role, keycloakId, e.getMessage());
        throw e;
    }
}
```

---

## 3. Trasabilitate (Audit Trail) și Modele Clinice

### 3.1 Gestiunea Audit Trail cu JPA Auditing
Pentru a asigura conformitatea cu normele de trasabilitate clinică (cine a creat, modificat sau șters o înregistrare și când), toate entitățile sensibile din bazele de date KinetoCare moștenesc clasa abstractă [BaseAuditableEntity.java](file:///c:/Users/Aneliss/Desktop/kinetocare/backend/user-service/src/main/java/com/example/user_service/entity/BaseAuditableEntity.java):

```java
package com.example.user_service.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import java.time.OffsetDateTime;
import lombok.experimental.SuperBuilder;

@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@SuperBuilder
@lombok.NoArgsConstructor
public abstract class BaseAuditableEntity {

    @CreatedBy
    @Column(name = "created_by", updatable = false, length = 36)
    private String createdBy;

    @LastModifiedBy
    @Column(name = "last_modified_by", length = 36)
    private String lastModifiedBy;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;
}
```

Identitatea din spatele operațiunilor este obținută printr-o configurare a auditorului, descrisă în clasa [JpaAuditingConfig.java](file:///c:/Users/Aneliss/Desktop/kinetocare/backend/user-service/src/main/java/com/example/user_service/config/JpaAuditingConfig.java):

```java
package com.example.user_service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import java.util.Optional;

@Configuration
@EnableJpaAuditing(auditorAwareRef = "auditorProvider")
public class JpaAuditingConfig {

    @Bean
    public AuditorAware<String> auditorProvider() {
        return () -> {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !authentication.isAuthenticated()) {
                // Fallback pentru sarcini în background sau receptori asincroni (AMQP)
                return Optional.of("SYSTEM");
            }
            Object principal = authentication.getPrincipal();
            if (principal instanceof Jwt) {
                Jwt jwt = (Jwt) principal;
                String sub = jwt.getSubject(); // Extrage UUID-ul unic din Keycloak
                if (sub != null && !sub.isEmpty()) {
                    return Optional.of(sub);
                }
            } else if (principal instanceof String) {
                String pStr = (String) principal;
                if (!"anonymousUser".equals(pStr)) {
                    return Optional.of(pStr);
                }
            }
            return Optional.of("SYSTEM");
        };
    }
}
```

### 3.2 Structura Clinica: Evoluții ca Text Liber
La nivel de bază de date, notele de evoluție clinică completate de terapeuți după ședințe nu urmează o structură rigidă (de exemplu, câmpuri dedicate pentru SOAP - Subiectiv, Obiectiv, Evaluare, Plan). Auditul bazei de date relevă faptul că tabela `evolutii` folosește un singur câmp `observatii` de tip text (`TEXT`/`VARCHAR`). Structura clinică standardizată SOAP este recomandată ca metodologie de lucru documentară la nivel de procedură medicală, dar stocarea fizică este de text liber pentru a nu îngrădi libertatea de descriere a kinetoterapeutului.

De asemenea, securitatea notelor de evoluție nu este marcată printr-un flag specific (cum ar fi `is_confidential`), ci este **impusă direct la nivel de interogare**. Aplicația filtrează notele după `terapeut_keycloak_id` la interogarea listei din `programari-service`, restricționând vizibilitatea doar pentru terapeutul curant desemnat și pacient.

---

## 4. Algoritmica Temporală și Managementul Miezului Nopții

Sistemul automat de remindere din KinetoCare rulează ca un set de sarcini planificate (`@Scheduled`). Acestea verifică periodic dacă există programări în intervalele de timp stabilite pentru alerte (24 de ore înaintea ședinței, respectiv 2 ore înaintea ședinței).

Provocarea majoră la nivel de algoritmică temporală este **trecerea miezului nopții**. Dacă fereastra glisantă calculată pe baza orei curente și a marjei depășește ora `23:59:59` (de exemplu, o verificare realizată la ora `23:50` cu o marjă de `15 minute` ce caută intervalul `[23:35, 00:05]`), interogarea SQL nativă de tip `BETWEEN` pe un câmp de timp (`LocalTime`) ar returna eroare sau date vide, deoarece `00:05` este considerat anterior orei `23:35`.

Clasa [ReminderScheduler.java](file:///c:/Users/Aneliss/Desktop/kinetocare/backend/programari-service/src/main/java/com/example/programari_service/service/ReminderScheduler.java) rezolvă această problemă prin **split-uirea interogării în două sub-ferestre**:

```java
private List<Programare> gasesteInFereastra(int oreInainte, int marjaMinute) {
    LocalDateTime acum = LocalDateTime.now();
    LocalDateTime centruFereastra = acum.plusHours(oreInainte);
    LocalDateTime startFereastra = centruFereastra.minusMinutes(marjaMinute);
    LocalDateTime endFereastra = centruFereastra.plusMinutes(marjaMinute);

    // Caz standard: Începutul și sfârșitul ferestrei sunt în aceeași zi calendaristică
    if (startFereastra.toLocalDate().equals(endFereastra.toLocalDate())) {
        return programareRepository.findProgramariInFereastra(
                startFereastra.toLocalDate(),
                startFereastra.toLocalTime(),
                endFereastra.toLocalTime()
        );
    }

    // Caz limită: Fereastra traversează miezul nopții (ex: 23:50 a zilei curente -> 00:20 a zilei următoare)
    // Se împarte logic căutarea în 2 sub-interogări distincte pe baze de date diferite/aceleași
    List<Programare> rezultat = new ArrayList<>();
    
    // Sub-fereastra 1: De la startul ferestrei până la ora maximă a primei zile (23:59:59.999)
    rezultat.addAll(programareRepository.findProgramariInFereastra(
            startFereastra.toLocalDate(),
            startFereastra.toLocalTime(),
            LocalTime.MAX
    ));
    
    // Sub-fereastra 2: De la ora minimă a zilei următoare (00:00:00) până la finalul marjei
    rezultat.addAll(programareRepository.findProgramariInFereastra(
            endFereastra.toLocalDate(),
            LocalTime.MIN,
            endFereastra.toLocalTime()
    ));
    
    return rezultat;
}
```

---

## 5. Topologia RabbitMQ cu Dead Letter Exchange (DLQ)

Pentru trimiterea alertelor prin email și SMS, sistemul folosește un flux asincron delegat către `notificari-service` prin intermediul RabbitMQ. Pentru a ne asigura că niciun mesaj nu este pierdut în caz de defecțiune sau căderi ale sistemelor externe, s-a implementat o arhitectură bazată pe **Dead Letter Exchange (DLX)** în [RabbitMQConfig.java](file:///c:/Users/Aneliss/Desktop/kinetocare/backend/notificari-service/src/main/java/com/example/notificari_service/config/RabbitMQConfig.java).

### 5.1 Configurația Cozilor și a Schimburilor (Exchanges)
* Coada principală (`notificari.queue.v2`) este declarată cu argumentul `x-dead-letter-exchange` îndreptat către `notificari.dlx`.
* Dacă procesarea unui mesaj în `NotificareConsumer` eșuează (se aruncă o excepție nebifată și nu este interceptată manual), Spring AMQP trimite o comandă de rejectare fără re-înrolare (`basic.nack` cu `requeue=false`).
* Brokerul RabbitMQ trimite automat mesajul respins către `notificari.dlx`, care îl redirecționează fără routing key (fiind un Fanout Exchange) în coada de eșecuri `notificari.queue.dead`.

```java
package com.example.notificari_service.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String EXCHANGE_NAME = "notificari.exchange";
    public static final String QUEUE_NAME = "notificari.queue.v2"; // V2 previne PRECONDITION_FAILED la adăugarea argumentelor DLQ
    public static final String ROUTING_KEY_PATTERN = "notificare.#";

    public static final String DLX_EXCHANGE_NAME = "notificari.dlx";
    public static final String DLQ_NAME = "notificari.queue.dead";

    @Bean
    public TopicExchange notificariExchange() {
        return new TopicExchange(EXCHANGE_NAME);
    }

    @Bean
    public FanoutExchange deadLetterExchange() {
        return new FanoutExchange(DLX_EXCHANGE_NAME);
    }

    @Bean
    public Queue notificariQueue() {
        return QueueBuilder.durable(QUEUE_NAME)
                .withArgument("x-dead-letter-exchange", DLX_EXCHANGE_NAME)
                .build();
    }

    @Bean
    public Queue deadLetterQueue() {
        return QueueBuilder.durable(DLQ_NAME).build();
    }

    @Bean
    public Binding binding(Queue notificariQueue, TopicExchange notificariExchange) {
        return BindingBuilder.bind(notificariQueue)
                .to(notificariExchange)
                .with(ROUTING_KEY_PATTERN);
    }

    @Bean
    public Binding deadLetterBinding(Queue deadLetterQueue, FanoutExchange deadLetterExchange) {
        return BindingBuilder.bind(deadLetterQueue).to(deadLetterExchange);
    }

    @Bean
    public MessageConverter jackson2JsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}
```

### 5.2 Prevenirea Buclelor Infinite de Reîncercare (DLQ-ception)
În clasa `DeadLetterConsumer`, mesajele ajunse în `notificari.queue.dead` sunt înregistrate în baza de date cu statusul `ESUAT` și afișate în consolă pentru inspecție manuală de către administratori.
Este esențial ca metoda de ascultare a DLQ să **înghită toate excepțiile** (printr-un bloc general `try-catch`). Dacă o excepție ar fi propagată în afara ascultătorului DLQ, Spring AMQP ar trimite un NACK și brokerul ar putea retrimite mesajul în aceeași coadă la infinit, ducând la blocarea thread-ului și consumarea resurselor sistemului (bucla infinită AMQP).
