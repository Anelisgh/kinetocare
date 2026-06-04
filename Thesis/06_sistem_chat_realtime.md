## Secțiunea 6: Sistemul de Chat în Timp Real (Real-Time Chat System)
### 6.1 Prezentare Generală
Funcționalitatea de chat în timp real permite comunicarea activă între pacienți și terapeuții asociați acestora. Implementată în cadrul `chat-service`, aceasta utilizează protocolul WebSocket peste STOMP (Simple Text Oriented Messaging Protocol) pentru comunicarea bidirecțională, cuplată cu verificări sincrone prin Feign pentru securitatea clinică și expedierea asincronă de evenimente prin RabbitMQ pentru notificările inter-servicii.
### 6.2 Configurarea WebSocket & STOMP
Clasa: `com.example.chat_service.config.WebSocketConfig`
Adnotări: `@Configuration`, `@EnableWebSocketMessageBroker`
Brokerul de mesaje WebSocket este configurat pentru a gestiona rutarea:
- **Prefixul Brokerului (`/queue`)**: Activează un Simple Broker (în memorie) dedicat rutării mesajelor către clienți conectați specifici.
- **Prefixul Aplicației (`/app`)**: Rutează mesajele primite de la clienți către metode adnotate cu `@MessageMapping` din controllerele Spring.
- **Endpoint (`/chat/ws-chat`)**: URI-ul specific unde clienții inițiază negocierea inițială WebSocket (WebSocket handshake).
- **Fallback SockJS**: Sistemul are configurat explicit `.withSockJS()`. Dacă mediul clientului (e.g., rețele corporative cu restricții sau browsere vechi) blochează conexiunile native WebSocket, SockJS retrogradează automat conexiunea la protocoalele de tip HTTP long-polling sau HTTP streaming, menținând în același timp interfața identică STOMP.
### 6.3 Validarea Securității: `StompSecurityInterceptor`
Spre deosebire de cererile HTTP standard interceptate de lanțul de filtre Spring Security, cadrele WebSocket STOMP operează post-negociere pe o conexiune TCP persistentă. Securitatea trebuie impusă nativ la nivelul canalului de cadre (frame channel level).
Clasa: `com.example.chat_service.config.StompSecurityInterceptor`
Implementare: `ChannelInterceptor`
1. **Interceptare**: Metoda `preSend` interceptează cadrele brute STOMP înainte ca acestea să ajungă la logica de rutare a aplicației. Aceasta filtrează în mod specific comenzile STOMP `CONNECT` și `SEND`.
2. **Extragerea Tokenului**: Extrage tokenul Bearer JWT din antetul nativ `Authorization` al cadrului STOMP.
3. **Validare și Stabilire Context**:
    - `jwtDecoder.decode(token)` validează criptografic tokenul.
    - Tokenul verificat este transformat într-un `JwtAuthenticationToken`.
    - **Mecanism Critic**: Autentificarea este asociată cu sesiunea STOMP (`accessor.setUser(authentication)`) și injectată forțat în contextul thread-local (`SecurityContextHolder.getContext().setAuthentication(authentication)`). Acest lucru este strict necesar pentru ca logica din aval (cum ar fi cererile sincrone prin Feign) executată pe thread-ul de execuție activ (worker thread) să moștenească contextul de securitate.
4. **Curățarea Contextului**: Metoda `postSend` impune apelul `SecurityContextHolder.clearContext()`. Deoarece thread-urile care procesează STOMP provin dintr-un thread pool comun, necurățarea contextului ar duce la poluarea thread-urilor (thread pollution), scurgând credențiale de securitate între sesiunile diferiților utilizatori.
### 6.4 Fluxul de Gestionare și Validare a Mesajelor
### 6.4.1 Punctul de Intrare în Controller
Payload-urile trimise de clienți către destinația `/app/chat.send` sunt rutate către:
```java
@MessageMapping("/chat.send")
public void trimiteMesaj(@Payload @Valid TrimitereMesajRequest request)
```
Payload-ul intră în granița tranzacțională a metodei `ChatService.salveazaSiNotifica`.
### 6.4.2 Validarea Relației Clinice (Sincronă)
Înainte ca orice înregistrare de chat să fie salvată, `chat-service` execută o regulă strictă de business: mesajele pot fi schimbate doar dacă relația terapeutică este activă.
```java
Boolean isActiva = programariClient.getRelatieStatusByKeycloak(pacientKeycloakId, terapeutKeycloakId);
if (isActiva == null || !isActiva) {
    throw new ForbiddenOperationException("Nu poți trimite mesaje într-o conversație arhivată.");
}
```
Aceasta reprezintă o cerere Feign GET sincronă (`/relatii/status-keycloak`) către `programari-service`. Dacă un pacient a schimbat terapeutul, vechea relație este arhivată, iar acest apel Feign returnează `false`, blocând activ tentativele de comunicare pe firele istorice arhivate.
### 6.4.3 Inițializarea Leneșă a Conversațiilor (Lazy Initialization)
```java
Conversatie conversatie = conversatieRepository
    .findByPacientKeycloakIdAndTerapeutKeycloakId(...)
    .orElseGet(() -> { ... return conversatieRepository.save(noua); });
```
În loc de a crea preventiv înregistrări `Conversatie` la înregistrarea pacientului sau la alocarea terapeutului, sistemul folosește inițializarea leneșă. Entitatea `Conversatie` din baza de date este instanțiată doar la prima tentativă de schimb de mesaje, reducând astfel efortul bazei de date.
### 6.4.4 Persistență și Broadcast (Difuzare)
1. Entitatea `Mesaj` este construită și salvată prin `mesajRepository.save(mesajNou)`. Data și ora pentru `Conversatie` (`ultimulMesajLa`) sunt actualizate.
2. **Notificare Asincronă**: Metoda `ChatService.trimiteNotificareSpreRabbitMQ` construiește un `NotificareEvent` cu `tipNotificare` („MESAJ_DE_LA_PACIENT” sau „MESAJ_DE_LA_TERAPEUT”) și îl trimite către `notificari.exchange` prin `RabbitTemplate`. Acest lucru gestionează notificările de tip push sau alertele pe email prin `notificari-service` pentru utilizatorii offline.
3. **Difuzare în Timp Real (Real-Time Broadcast)**: În controller, mesajul este trimis către broker:
```java
messagingTemplate.convertAndSend("/queue/conversatii/" + request.conversatieId(), mesajSalvat);
```
Toți clienții WebSocket activi abonați la canalul specific al conversației `/queue/conversatii/{id}` primesc instantaneu payload-ul `MesajDTO` nou persistat.
### 6.4.5 Gestionarea Excepțiilor peste STOMP
Dacă este aruncată o excepție de tip `ForbiddenOperationException` (relație arhivată) sau orice altă excepție runtime în timpul rulării metodei adnotate cu `@MessageMapping`, metodele standard `@ExceptionHandler` din Spring sunt ocolite. În schimb, o metodă `@MessageExceptionHandler` interceptează eroarea și trimite un cadru de eroare STOMP dedicat exclusiv utilizatorului care a inițiat cererea:
```java
@MessageExceptionHandler
@SendToUser("/queue/errors")
public Map<String, String> handleWebSocketException(...)
```
Aceasta garantează că aplicația client primește un feedback clar despre eroare (e.g., randarea unui banner de eroare) fără a deconecta conexiunea WebSocket sau a bloca aplicația.
