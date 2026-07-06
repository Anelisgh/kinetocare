# Explicație RabbitMQ — KinetoCare (pentru disertație)

---

## 1. Ce este RabbitMQ și de ce l-ai folosit?

**RabbitMQ** este un **message broker** — adică un intermediar care primește mesaje de la un serviciu și le livrează altuia, fără ca cele două să comunice direct între ele.

### De ce nu ai folosit pur și simplu un apel HTTP direct?

Dacă `programari-service` ar fi chemat direct `notificari-service` prin HTTP (REST/Feign), ar apărea o problemă: **cuplaj strâns** (*tight coupling*).

| HTTP direct | RabbitMQ (mesaje) |
|---|---|
| Dacă `notificari-service` cade, notificarea se pierde | Mesajul rămâne în coadă până când serviciul revine |
| Serviciul expeditor **așteaptă** răspunsul | Serviciul expeditor **nu așteaptă** — trimite și continuă |
| Un serviciu știe de celălalt | Serviciile sunt complet decuplate |

Ai ales RabbitMQ pentru **comunicare asincronă** și **reziliență** — dacă `notificari-service` pică temporar, mesajele nu se pierd.

---

## 2. Termeni esențiali explicați simplu

### Exchange
Un **exchange** este ca o cutie poștală centrală. El primește mesajele de la expeditor și decide **în ce coadă să le trimită**, pe baza unui **routing key**.

> În codul tău: `notificari.exchange` (de tip **Topic**).

### Queue (Coadă)
O **coadă** este locul unde mesajele stau la rând până când un consumer le preia. E persistentă (`durable`) — supraviețuiește unui restart al RabbitMQ.

> În codul tău: `notificari.queue.v2`

### Routing Key
O **cheie de rutare** este o etichetă atașată mesajului (ex: `notificare.programare.noua`). Exchange-ul o folosește pentru a decide în ce coadă merge mesajul.

### Binding
Un **binding** este regula care leagă un exchange de o coadă. Spune: *„dacă routing key-ul se potrivește cu pattern-ul X, trimite în coada Y"*.

> În codul tău: pattern `notificare.#` — `#` înseamnă „orice urmează după". Deci prinde `notificare.programare.noua`, `notificare.reminder.2h`, etc.

### Producer / Publisher
Serviciul care **trimite** mesaje în exchange. La tine: `programari-service` și `pacienti-service`.

### Consumer
Serviciul care **ascultă** coada și procesează mesajele. La tine: `notificari-service`.

### Dead Letter Queue (DLQ)
O coadă specială unde ajung mesajele **care nu au putut fi procesate**. În loc să se piardă, ele sunt „parcate" pentru inspecție manuală.

---

## 3. Arhitectura fluxului complet

```
programari-service
  └─ NotificarePublisher
       └─ RabbitTemplate.convertAndSend(
            exchange = "notificari.exchange",
            routingKey = "notificare.programare.noua",
            body = NotificareEvent { ... }
          )
             │
             ▼
    [notificari.exchange]  ← Topic Exchange
             │
     binding: notificare.#
             │
             ▼
    [notificari.queue.v2]  ← Coada principală (durable)
             │
    (dacă procesarea eșuează)
             ├──────────────────────────────────────────►  [notificari.dlx]
             │                                               FanoutExchange
             │                                                     │
             │                                                     ▼
             │                                          [notificari.queue.dead]
             │                                           DeadLetterConsumer
             │                                           (loghează, nu reprocesează)
             │
    (dacă procesarea reușește)
             ▼
    notificari-service
      └─ NotificareConsumer.primesteMesaj()
           ├─ Verificare idempotență (mesaje duplicate)
           └─ NotificareService.proceseazaEveniment()
                └─ Salvare în MySQL (tabel `notificari`)
```

---

## 4. Componentele din cod, explicate una câte una

### 4.1 `RabbitMQConfig.java` — Infrastructura

Aceasta este clasa care **declară** toate componentele RabbitMQ la startup. Spring le creează automat în broker dacă nu există.

```java
public static final String EXCHANGE_NAME = "notificari.exchange";
public static final String QUEUE_NAME    = "notificari.queue.v2";
public static final String ROUTING_KEY_PATTERN = "notificare.#";
public static final String DLX_EXCHANGE_NAME   = "notificari.dlx";
public static final String DLQ_NAME            = "notificari.queue.dead";
```

**De ce `v2` în numele cozii?**
RabbitMQ nu permite schimbarea argumentelor unei cozi existente (ex: adăugarea DLQ-ului pe o coadă deja creată). Soluția: redenumire cu sufixul `v2` pentru a crea o coadă nouă cu parametrii corecți.

**Topic Exchange** — ales pentru că permite **pattern matching** pe routing key:
```java
@Bean
public TopicExchange notificariExchange() {
    return new TopicExchange(EXCHANGE_NAME);
}
```

**Coada principală cu Dead Letter Exchange configurat:**
```java
@Bean
public Queue notificariQueue() {
    return QueueBuilder.durable(QUEUE_NAME)
            .withArgument("x-dead-letter-exchange", DLX_EXCHANGE_NAME)
            .build();
}
```
Argumentul `x-dead-letter-exchange` spune RabbitMQ: *„dacă un mesaj este NACK-at (respins), rutează-l automat în acest exchange"*.

**Convertor JSON:**
```java
@Bean
public MessageConverter jackson2JsonMessageConverter() {
    return new Jackson2JsonMessageConverter();
}
```
Fără acesta, mesajele ar fi serializate binar (Java). Cu el, sunt trimise/primite ca **JSON**, ceea ce le face inspecabile în interfața RabbitMQ Management.

---

### 4.2 `NotificareEvent.java` — Contractul mesajului

```java
public record NotificareEvent(
    String tipNotificare,       // ex: "PROGRAMARE_NOUA"
    String userKeycloakId,      // destinatarul notificării
    String tipUser,             // "PACIENT" sau "TERAPEUT"
    String titlu,
    String mesaj,
    Long entitateLegataId,      // ex: id-ul programarii
    String tipEntitateLegata,   // "PROGRAMARE", "EVALUARE", etc.
    String urlActiune           // unde redirecționează click-ul
) implements Serializable {}
```

Acesta este **contractul** dintre producer și consumer. Un `record` Java este imutabil și concis — perfect pentru DTO-uri de tip mesaj.

> **Întrebare posibilă:** *De ce `Serializable`?*
> Pentru compatibilitate cu serializarea Java, deși în practică se folosește conversia JSON configurată.

---

### 4.3 `NotificarePublisher.java` (în `programari-service`) — Producătorul

```java
private void trimite(String routingKey, NotificareEvent event) {
    rabbitTemplate.convertAndSend(
        RabbitMQConfig.EXCHANGE_NAME,
        routingKey,
        event,
        message -> {
            message.getMessageProperties().setMessageId(UUID.randomUUID().toString());
            return message;
        }
    );
}
```

**`RabbitTemplate`** este componenta Spring AMQP care face trimiterea efectivă.

**UUID pe fiecare mesaj** — acesta este `messageId`-ul unic, folosit mai târziu pentru a preveni procesarea duplicată (idempotență). Este setat înainte de trimitere.

**Exemple de routing keys folosite:**
- `notificare.programare.noua` → terapeut primește notificare că are o programare nouă
- `notificare.programare.anulata.terapeut` → pacient primește notificare că programarea a fost anulată
- `notificare.reminder.24h` → pacient primește reminder cu 24h înainte
- `notificare.jurnal.completat` → terapeut primește notificare că pacientul a completat jurnalul

Toate se potrivesc cu pattern-ul `notificare.#` din binding.

---

### 4.4 `NotificareConsumer.java` — Consumatorul

```java
@RabbitListener(queues = RabbitMQConfig.QUEUE_NAME)
public void primesteMesaj(
    NotificareEvent event,
    @Header(value = AmqpHeaders.MESSAGE_ID, required = false) String messageId
) {
    // 1. Verificare idempotență
    if (messageId != null) {
        int rowsAffected = mesajProcesatRepository.insertIdempotent(messageId);
        if (rowsAffected == 0) {
            log.info("Mesaj duplicat detectat și ignorat: messageId={}", messageId);
            return; // mesajul a mai fost procesat
        }
    }

    // 2. Procesare efectivă
    notificareService.proceseazaEveniment(event);
}
```

**`@RabbitListener`** — Spring ascultă coada în mod continuu. Când apare un mesaj, metoda este apelată automat.

**Deserializare automată** — Spring vede că parametrul este de tip `NotificareEvent` și folosește `Jackson2JsonMessageConverter` pentru a converti JSON-ul primit în obiect Java.

**Dacă metoda aruncă o excepție** → Spring face **NACK** (negative acknowledgement) → RabbitMQ rutează mesajul automat în **DLX** → ajunge în **DLQ**.

---

### 4.5 Idempotența — mecanism anti-duplicate

**Problema:** Într-un sistem distribuit, un mesaj poate fi livrat de **mai multe ori** (ex: consumer-ul cade după procesare dar înainte de ACK → RabbitMQ retrimite mesajul).

**Soluția implementată:** tabel `mesaje_procesate` în MySQL cu o cheie primară pe `message_id`.

```sql
INSERT INTO mesaje_procesate (message_id, processed_at)
VALUES (:messageId, NOW())
ON DUPLICATE KEY UPDATE message_id = message_id
```

- Dacă `messageId` **nu există** în tabel → inserare reușește → `rowsAffected = 1` → procesăm mesajul.
- Dacă `messageId` **există deja** → `ON DUPLICATE KEY` nu face nimic → `rowsAffected = 0` → ignorăm mesajul silențios.

Aceasta garantează că fiecare notificare este **salvată o singură dată**, indiferent de câte ori este livrat mesajul.

> **Termenul tehnic:** *at-least-once delivery* cu *idempotent consumer*.

---

### 4.6 `DeadLetterConsumer.java` — Gestionarea eșecurilor

```java
@RabbitListener(queues = RabbitMQConfig.DLQ_NAME)
public void processeazaMesajEsuat(Message message) {
    try {
        String body = new String(message.getBody(), StandardCharsets.UTF_8);
        log.error("[DLQ] Mesaj eșuat: Body: {} | Headers: {}",
            body, message.getMessageProperties().getHeaders());
    } catch (Exception e) {
        // Nu re-aruncăm excepția — ar provoca buclă infinită în DLQ
        log.error("[DLQ] Eroare la logarea mesajului: {}", e.getMessage());
    }
}
```

**De ce nu aruncăm excepție din DLQ consumer?**
Dacă am arunca excepție, mesajul ar fi NACK-at din DLQ și ar reintra în DLQ → buclă infinită. DLQ-ul este **terminal** — mesajele ajunse acolo sunt logate pentru inspecție manuală.

---

### 4.7 `application.properties` — configurare

```properties
spring.rabbitmq.host=${RABBITMQ_HOST:localhost}
spring.rabbitmq.port=5672
spring.rabbitmq.username=${RABBITMQ_USER:guest}
spring.rabbitmq.password=${RABBITMQ_PASS:guest}
spring.rabbitmq.listener.simple.default-requeue-rejected=false
```

**`default-requeue-rejected=false`** — Dacă un consumer aruncă excepție, mesajul **nu** este repus în aceeași coadă (ar intra în buclă), ci este trimis la DLX/DLQ.

---

## 5. Tipuri de notificări implementate

### Notificări pentru PACIENT:
| Tip | Declanșator |
|---|---|
| `PROGRAMARE_ANULATA_DE_TERAPEUT` | Terapeutul anulează programarea |
| `REMINDER_24H` | Job scheduler, cu 24h înainte |
| `REMINDER_2H` | Job scheduler, cu 2h înainte |
| `MESAJ_DE_LA_TERAPEUT` | Terapeut trimite mesaj în chat |
| `REMINDER_JURNAL` | Job scheduler, după programare |
| `REEVALUARE_RECOMANDATA` | Terapeut recomandă reevaluare |

### Notificări pentru TERAPEUT:
| Tip | Declanșator |
|---|---|
| `PROGRAMARE_NOUA` | Pacient face programare |
| `EVALUARE_INITIALA_NOUA` | Pacient cere evaluare inițială |
| `PROGRAMARE_ANULATA_DE_PACIENT` | Pacient anulează programarea |
| `JURNAL_COMPLETAT` | Pacient completează jurnalul de recuperare |
| `MESAJ_DE_LA_PACIENT` | Pacient trimite mesaj în chat |
| `REEVALUARE_NECESARA` | Sistem detectează necesitate reevaluare |

---

## 6. Întrebări posibile de la profesor și răspunsuri

**Q: De ce ai ales RabbitMQ față de Kafka sau HTTP?**
> Kafka e optim pentru volume mari de date și event sourcing (ex: analytics). RabbitMQ e mai simplu, mai potrivit pentru notificări din aplicații CRUD, are DLQ nativ și este mai ușor de administrat pentru scopul acestei aplicații.

**Q: Ce se întâmplă dacă `notificari-service` este oprit?**
> Mesajele rămân persistate în coada `notificari.queue.v2` (durable). La repornire, consumer-ul le preia automat, în ordinea în care au venit.

**Q: Ce este idempotența și de ce e importantă?**
> Idempotența înseamnă că procesarea unui mesaj de mai multe ori are același efect ca procesarea lui o singură dată. E importantă pentru că în sisteme distribuite, rețeaua poate cauza relivrarea mesajelor. Fără idempotență, utilizatorul ar primi notificări duplicate.

**Q: Ce este un Topic Exchange și de ce nu ai folosit Direct sau Fanout?**
> **Direct** ar necesita routing key exact — rigid, nu scalabil. **Fanout** trimite în TOATE cozile legate, indiferent de conținut — prea permisiv. **Topic** permite pattern matching (`notificare.#`) — flexibil și extensibil: pot adăuga noi tipuri de notificări fără să schimb binding-ul.

**Q: De ce ai redenumit coada în `v2`?**
> RabbitMQ nu permite modificarea argumentelor unei cozi existente (restricție la nivel de broker). Când am adăugat Dead Letter Queue-ul ca argument, singura soluție validă a fost crearea unei cozi noi cu numele `notificari.queue.v2`.

**Q: Ce se întâmplă cu un mesaj malformat (JSON invalid)?**
> Deserializarea eșuează → metoda consumer-ului aruncă excepție → Spring face NACK → mesajul este rutat automat la `notificari.dlx` → ajunge în `notificari.queue.dead` → `DeadLetterConsumer` îl loghează. Mesajul nu se pierde.

**Q: Cum funcționează `ON DUPLICATE KEY UPDATE`?**
> Este o sintaxă MySQL care face un INSERT atomic: dacă `message_id` există deja (PRIMARY KEY duplicat), în loc să arunce eroare, nu face nimic și returnează 0 rows affected. Astfel, verificăm dacă mesajul a mai fost procesat fără a fi nevoie de un SELECT separat (evităm race condition).
