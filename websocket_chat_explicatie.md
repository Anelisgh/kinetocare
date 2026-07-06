# Explicație WebSocket / STOMP — Chat în timp real (KinetoCare)

---

## 1. Contextul problemei: de ce nu e suficient HTTP?

HTTP este un protocol **request-response**: clientul trimite o cerere, serverul răspunde, conexiunea se închide. Pentru chat, asta ar însemna că ar trebui să intrebi serverul la fiecare câteva secunde „ai mesaje noi?" — o tehnică numită **polling**, care e ineficientă și introducea latență.

**WebSocket** rezolvă asta prin o conexiune **persistentă, bidirecțională**: odată deschisă, serverul poate trimite date la client **oricând**, fără ca acesta să ceară.

| HTTP Polling | WebSocket |
|---|---|
| Client întreabă din 2 în 2 secunde | Server trimite instant când apare ceva nou |
| Latență: până la 2000ms | Latență: ~millisecunde |
| Trafic inutil (header-e la fiecare request) | Conexiune deschisă, overhead minim |
| Simplu de implementat | Necesită gestiunea sesiunilor |

---

## 2. Termeni esențiali explicați simplu

### WebSocket
Un **protocol de comunicare** peste TCP care permite o conexiune persistentă între browser și server. Odată stabilită (prin un „handshake" HTTP care se „upgradează" la WebSocket), ambele capete pot trimite date oricând.

### STOMP (Simple Text Oriented Messaging Protocol)
WebSocket e doar un „tub" de date brute — nu știe nimic despre destinație sau format. **STOMP** este un protocol de mesagerie care rulează **pe deasupra** WebSocket și adaugă:
- **destinations** (adrese) — ex: `/queue/conversatii/5`
- **comenzi** — `CONNECT`, `SEND`, `SUBSCRIBE`, `DISCONNECT`
- **headers** — inclusiv `Authorization: Bearer <token>`

> Analogie: WebSocket e ca un cablu telefonic. STOMP e ca limba în care vorbești.

### SockJS
Un **fallback library** pentru browsere sau rețele care nu suportă WebSocket nativ (ex: proxy-uri corporate care blochează conexiunile upgrade). SockJS încearcă mai întâi WebSocket și, dacă nu merge, fallback la polling lung (long-polling) sau iframe. Utilizatorul nu vede diferența.

### Broker (Message Broker in-memory)
Componenta din server care **rutează mesajele** de la expeditor la abonații canalului corect. La tine e un broker simplu in-memory (nu extern, nu RabbitMQ), configurat pe prefix `/queue`.

### `@MessageMapping`
Echivalentul `@RequestMapping` / `@PostMapping` din REST, dar pentru WebSocket. O metodă adnotată cu `@MessageMapping("/chat.send")` este apelată când un client trimite un frame STOMP la destinația `/app/chat.send`.

### `SimpMessagingTemplate`
Componenta Spring cu care **serverul trimite activ** mesaje la clienți (push), pe un topic sau unui utilizator specific.

### `Principal`
În Spring Security, obiectul `Principal` reprezintă **utilizatorul autentificat curent**. În contextul WebSocket/STOMP, este setat din token-ul JWT la momentul conexiunii.

---

## 3. Arhitectura completă a fluxului

```
FRONTEND (React)
──────────────────────────────────────────────────────
[ChatPacient.jsx]
  │
  ├─ 1. Creează client STOMP cu SockJS
  │       new Client({ webSocketFactory: () => new SockJS('.../ws-chat') })
  │
  ├─ 2. client.activate() → CONNECT frame cu header Authorization: Bearer <JWT>
  │
  └─ 3. La onConnect → trimite clientul în [FereastraChat] ca prop

[FereastraChat.jsx]
  │
  ├─ 4. stompClient.subscribe('/queue/conversatii/5')
  │       → ascultă mesajele noi din această conversație
  │
  └─ 5. La submit form → stompClient.publish({
              destination: '/app/chat.send',
              headers: { Authorization: Bearer <JWT> },
              body: JSON.stringify({ conversatieId, expeditorKeycloakId, ... })
            })

──────────────────────────────────────────────────────
BACKEND (Spring Boot - chat-service)
──────────────────────────────────────────────────────

STOMP frame CONNECT:
  → StompSecurityInterceptor.preSend()
       └─ extrage JWT din header
       └─ jwtDecoder.decode(token)
       └─ accessor.setUser(authentication)  ← Principal setat pe sesiune

STOMP frame SEND la /app/chat.send:
  → StompSecurityInterceptor.preSend()  ← verificare JWT din nou
  → WebSocketChatController.trimiteMesaj(request, principal)
       ├─ Validare: principal.getName() == request.expeditorKeycloakId
       ├─ chatService.salveazaSiNotifica(request)
       │    ├─ Verificare relație terapeutică activă (Feign → programari-service)
       │    ├─ Lazy init conversație (dacă nu există, creează)
       │    ├─ Salvare Mesaj în MySQL
       │    ├─ Update ultimulMesajLa pe Conversatie
       │    └─ trimiteNotificareSpreRabbitMQ()
       │         └─ rabbitTemplate.convertAndSend("notificari.exchange", "notificare.mesaj.nou", event)
       │
       └─ messagingTemplate.convertAndSend("/queue/conversatii/5", mesajDTO)
            └─ brokerul in-memory trimite mesajul la toți abonații la /queue/conversatii/5

STOMP frame SUBSCRIBE la /queue/conversatii/5:
  → brokerul in-memory înregistrează abonatul
  → la orice mesaj pe acest topic, îl livrează instant

Erori (@MessageExceptionHandler):
  → @SendToUser("/queue/errors")
       └─ mesajul de eroare ajunge DOAR la utilizatorul care a cauzat eroarea
```

---

## 4. Componentele din cod, explicate una câte una

### 4.1 `WebSocketConfig.java` — Configurarea serverului

```java
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // Broker in-memory pe prefixul /queue
        config.enableSimpleBroker("/queue");
        // Destinațiile cu /app sunt rutate la @MessageMapping
        config.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/chat/ws-chat")
                .setAllowedOriginPatterns("*")
                .withSockJS(); // fallback pentru browsere fără WebSocket
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(securityInterceptor); // autentificare pe fiecare frame
    }
}
```

**Cum funcționează rutarea:**
- Un client publică la `/app/chat.send` → Spring caută `@MessageMapping("/chat.send")`
- Serverul trimite la `/queue/conversatii/5` → brokerul in-memory livrează la toți abonații

**De ce `/queue` și nu `/topic`?**
- `/topic` = broadcast (toți abonații la același topic primesc același mesaj — ex: știri live)
- `/queue` = mai potrivit pentru mesaje private/conversații individuale

---

### 4.2 `StompSecurityInterceptor.java` — Autentificarea pe WebSocket

Aceasta este componenta cea mai complexă și critică din implementare.

**Problema:** Spring Security funcționează pe HTTP. Conexiunile WebSocket sunt persistente și nu au request HTTP la fiecare mesaj — deci filtrele de securitate normale nu rulează.

**Soluția:** Un `ChannelInterceptor` care interceptează fiecare **frame STOMP** (inclusiv `CONNECT` și `SEND`) și validează JWT-ul manual.

```java
@Override
public Message<?> preSend(Message<?> message, MessageChannel channel) {
    StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

    if (StompCommand.CONNECT.equals(accessor.getCommand())
        || StompCommand.SEND.equals(accessor.getCommand())) {

        String authHeader = accessor.getFirstNativeHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            Jwt jwt = jwtDecoder.decode(token);  // validare semnătură + expirare
            JwtAuthenticationToken authentication = new JwtAuthenticationToken(jwt, ...);

            accessor.setUser(authentication);   // setăm Principal pe sesiunea WebSocket
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }
    }
    return message;
}

@Override
public void postSend(Message<?> message, MessageChannel channel, boolean sent) {
    SecurityContextHolder.clearContext(); // curățare obligatorie — thread pool refolosit
}
```

**De ce `clearContext()` în `postSend`?**
Spring folosește un **thread pool** pentru procesarea mesajelor. Dacă nu curățăm `SecurityContextHolder`, un alt request ulterior pe același thread ar moșteni identitatea utilizatorului anterior — o vulnerabilitate de securitate gravă.

---

### 4.3 `WebSocketChatController.java` — Punctul de intrare al mesajelor

```java
@MessageMapping("/chat.send")  // se activează când un client publică la /app/chat.send
public void trimiteMesaj(@Payload @Valid TrimitereMesajRequest request, Principal principal) {

    // Verificare identitate: nu poți trimite în numele altui user
    if (!request.expeditorKeycloakId().equals(principal.getName())) {
        throw new AccessDeniedException("Nu poți trimite mesaje în numele altui utilizator!");
    }

    MesajDTO mesajSalvat = chatService.salveazaSiNotifica(request);

    // Push instant la toți abonații la /queue/conversatii/{id}
    messagingTemplate.convertAndSend(
        "/queue/conversatii/" + request.conversatieId(),
        mesajSalvat
    );
}

// Handler de erori — trimite eroarea DOAR utilizatorului care a cauzat-o
@MessageExceptionHandler
@SendToUser("/queue/errors")
public Map<String, String> handleWebSocketException(Exception ex, Principal principal) {
    return Map.of("error", "Nu s-a putut trimite mesajul: " + ex.getMessage());
}
```

**`@SendToUser("/queue/errors")`** — Aceasta e o destinație specială Spring. Mesajul nu merge la toți, ci **exclusiv** la utilizatorul care a cauzat eroarea (identificat prin `Principal`).

---

### 4.4 `ChatService.salveazaSiNotifica()` — Logica de business

La fiecare mesaj primit prin WebSocket, serviciul face **4 lucruri**:

1. **Verificare de securitate**: relația terapeutică trebuie să fie activă (apel Feign la `programari-service`). Dacă relația e arhivată, mesajul e blocat.

2. **Lazy initialization a conversației**: dacă conversația nu există încă (primul mesaj), e creată automat. Nu există un endpoint separat de „inițializare conversație" — conversația apare la primul mesaj.

3. **Salvare în MySQL**: mesajul e persistat cu `expeditorKeycloakId`, `tipExpeditor`, `continut`, `trimisLa`, `esteCitit = false`.

4. **Notificare RabbitMQ**: după salvare, se publică un eveniment în `notificari.exchange` cu routing key `notificare.mesaj.nou` → `notificari-service` salvează o notificare persistentă pentru destinatar.

```java
private void trimiteNotificareSpreRabbitMQ(TrimitereMesajRequest cerere, Conversatie conversatie, ...) {
    String recipientKeycloakId = (cerere.tipExpeditor() == TipExpeditor.PACIENT)
            ? conversatie.getTerapeutKeycloakId()
            : conversatie.getPacientKeycloakId();

    NotificareEvent event = NotificareEvent.builder()
            .tipNotificare(eventType)           // "MESAJ_DE_LA_PACIENT" sau "MESAJ_DE_LA_TERAPEUT"
            .userKeycloakId(recipientKeycloakId)
            .entitateLegataId(conversatie.getId())
            .tipEntitateLegata("CONVERSATIE")
            .urlActiune("/chat/" + conversatie.getId())
            .build();

    rabbitTemplate.convertAndSend("notificari.exchange", "notificare.mesaj.nou", event, ...);
}
```

---

### 4.5 Frontend — `ChatPacient.jsx` (inițializarea conexiunii)

```javascript
const client = new Client({
    // SockJS ca transport (WebSocket cu fallback)
    webSocketFactory: () => new SockJS('http://localhost:8081/api/chat/ws-chat'),

    // JWT trimis în header-ul CONNECT
    connectHeaders: {
        Authorization: `Bearer ${authService.getToken()}`
    },

    reconnectDelay: 5000,       // reconectare automată după 5s dacă se pierde conexiunea
    heartbeatIncoming: 4000,    // server trimite heartbeat la 4s
    heartbeatOutgoing: 4000,    // client trimite heartbeat la 4s

    onConnect: () => {
        setStompClient(client); // clientul e gata, îl pasăm în FereastraChat
    }
});

client.activate(); // pornește conexiunea
```

**De ce `heartbeat`?**
Conexiunile WebSocket inactive pot fi închise de proxy-uri sau load balancere care nu detectează activitate. Heartbeat-ul trimite periodic un frame gol pentru a menține conexiunea deschisă.

**De ce `reconnectDelay: 5000`?**
Dacă conexiunea se pierde (server restart, rețea instabilă), clientul încearcă automat reconectarea după 5 secunde — transparent pentru utilizator.

---

### 4.6 Frontend — `FereastraChat.jsx` (abonare + trimitere)

```javascript
// Abonare la mesajele conversației curente
const sub = stompClient.subscribe(`/queue/conversatii/${conversatieId}`, (message) => {
    const mesajPrimit = JSON.parse(message.body);
    setIstoric(prev => [...prev, mesajPrimit]);   // adaugă în UI instant
    executaScrollJos();

    // Dacă mesajul e de la celălalt, marchează ca citit
    if (mesajPrimit.expeditorKeycloakId !== userId && conversatieId) {
        chatService.marcheazaMesajeleCaCitite(conversatieId, userId, tipUser);
    }
});

// Abonare la canalul de erori personal
const subErori = stompClient.subscribe('/user/queue/errors', (frame) => {
    const eroare = JSON.parse(frame.body);
    setEroareWs(eroare?.error);
    setTimeout(() => setEroareWs(null), 5000); // banner eroare dispare după 5s
});
```

```javascript
// Trimitere mesaj
stompClient.publish({
    destination: '/app/chat.send',
    headers: { Authorization: `Bearer ${authService.getToken()}` },
    body: JSON.stringify({
        conversatieId: conversatieActiva.id,
        expeditorKeycloakId: userId,
        destinatarKeycloakId: partnerId,
        tipExpeditor: tipUser,
        continut: mesajInput.trim()
    })
});
```

**De ce trimitem JWT și la `SEND`?**
`StompSecurityInterceptor` validează JWT-ul la fiecare frame `SEND`. Dacă token-ul expiră în timp ce utilizatorul e conectat, mesajul e respins și utilizatorul primește o eroare pe `/user/queue/errors`.

---

## 5. Modelul de date (Entitățile)

### `Conversatie`
Reprezintă o relație de chat între un pacient și un terapeut. Există **cel mult o conversație** pentru fiecare pereche pacient-terapeut.

| Câmp | Tip | Descriere |
|---|---|---|
| `id` | Long | PK auto-incrementat |
| `pacientKeycloakId` | String | ID Keycloak al pacientului |
| `terapeutKeycloakId` | String | ID Keycloak al terapeutului |
| `ultimulMesajLa` | OffsetDateTime | Folosit pentru sortare listă conversații |

### `Mesaj`
Fiecare rând = un mesaj individual.

| Câmp | Tip | Descriere |
|---|---|---|
| `conversatieId` | Long | FK la Conversatie |
| `expeditorKeycloakId` | String | Cine a trimis |
| `tipExpeditor` | Enum | `PACIENT` sau `TERAPEUT` |
| `continut` | TEXT | Textul mesajului |
| `esteCitit` | Boolean | Indicator citit/necitit |
| `cititLa` | OffsetDateTime | Timestamp marcare citit |
| `trimisLa` | OffsetDateTime | Timestamp trimitere |

---

## 6. Dual-channel: WebSocket + REST

Implementarea folosește **două canale de comunicare** în paralel, fiecare cu rolul lui:

| Canal | Utilizat pentru |
|---|---|
| **WebSocket / STOMP** | Trimitere mesaje în timp real (push instant) |
| **REST (HTTP)** | Încărcare istorică mesaje, marcare citite, lista conversații |

La deschiderea chat-ului:
1. `GET /chat/conversatii/{id}/mesaje` → încarcă mesajele existente (REST)
2. `SUBSCRIBE /queue/conversatii/{id}` → ascultă mesajele noi (WebSocket)
3. Mesajele noi sosesc via WebSocket și sunt adăugate în UI fără reload

---

## 7. Întrebări posibile de la profesor și răspunsuri

**Q: Ce diferență este între WebSocket și HTTP?**
> HTTP e unidirecțional și fără stare (stateless) — clientul cere, serverul răspunde, conexiunea se închide. WebSocket e bidirecțional și persistent — odată stabilită conexiunea, ambele capete pot trimite date oricând, fără overhead de header-e la fiecare mesaj.

**Q: De ce ai folosit STOMP peste WebSocket în loc de WebSocket raw?**
> WebSocket raw transmite bytes/string-uri brute fără un protocol de mesagerie — ai fi trebuit să implementezi manual rutarea, destinațiile, subscripțiile. STOMP oferă acest protocol standardizat: `SUBSCRIBE`, `SEND`, `CONNECT`, destinații cu prefix. Spring are suport nativ pentru STOMP, ceea ce a simplificat dramatic implementarea.

**Q: Ce este SockJS și de ce e necesar?**
> SockJS e un layer de compatibilitate. Unele rețele corporative (proxy-uri, firewall-uri) blochează conexiunile WebSocket pentru că nu sunt HTTP standard. SockJS încearcă mai întâi WebSocket nativ și, dacă eșuează, folosește alternative (HTTP long-polling, iframe transport). Utilizatorul nu observă diferența.

**Q: Cum ai securizat conexiunea WebSocket? Nu se pot aplica filtrele HTTP normale?**
> Exact, filtrele de securitate HTTP nu se aplică pe conexiunile WebSocket persistente. Am implementat un `ChannelInterceptor` — `StompSecurityInterceptor` — care interceptează fiecare frame STOMP la nivel de canal. La `CONNECT` și `SEND`, extrage JWT-ul din header-ul `Authorization`, îl validează cu `jwtDecoder`, și setează `Principal` pe sesiunea STOMP. Am adăugat și `clearContext()` în `postSend()` pentru a evita scurgerea contextului de securitate între thread-uri.

**Q: Ce se întâmplă dacă un utilizator încearcă să trimită mesaje în numele altcuiva?**
> Am adăugat o verificare explicită în controller: `request.expeditorKeycloakId()` trebuie să fie egal cu `principal.getName()` (ID-ul din token-ul JWT validat). Dacă nu se potrivesc, se aruncă `AccessDeniedException`. Această verificare previne atacuri de tip spoofing în care un utilizator malițios ar modifica payload-ul.

**Q: Ce se întâmplă dacă chat-service cade? Se pierd mesajele în tranzit?**
> Mesajele WebSocket în tranzit (care nu au ajuns încă la server) se pierd — e o limitare acceptată pentru comunicare real-time. Clientul primește o eroare și se reconectează automat după 5 secunde (`reconnectDelay`). Mesajele deja salvate în MySQL sunt persistate și se reîncarcă la reconectare prin REST. Notificările sunt gestionate separat prin RabbitMQ, care e durabil.

**Q: De ce ai ales un broker in-memory în loc de RabbitMQ ca broker WebSocket?**
> Spring suportă RabbitMQ și ca broker extern pentru STOMP (STOMP-over-RabbitMQ), ceea ce ar permite scalare orizontală. Am ales brokerul in-memory pentru simplitate — aplicația rulează pe un singur nod, deci nu e nevoie de coordonare între instanțe. RabbitMQ e deja folosit pentru notificări, separat. Dacă aș scala horizontal, broker extern ar fi alegerea corectă.

**Q: Cum ai implementat „Văzut" la mesaje?**
> La primirea unui mesaj WebSocket de la celălalt participant, se face automat un apel REST `PUT /chat/conversatii/{id}/citit`. Acesta setează `esteCitit = true` și `cititLa = NOW()` pe toate mesajele necitite din conversație. Frontend-ul afișează „Văzut" / „Trimis" pe ultimul mesaj al utilizatorului curent, bazat pe câmpul `esteCitit` din `MesajDTO`.

**Q: Ce este Lazy Initialization în contextul conversației?**
> Nu există un flow explicit de „creare conversație". Prima dată când un pacient sau terapeut trimite un mesaj, `ChatService` verifică dacă există deja o conversație între ei. Dacă nu există, o creează automat. Aceasta simplifică UX-ul și evită endpoint-uri suplimentare. Pattern-ul se numește *lazy initialization* — resursa e creată la primul acces, nu în avans.
