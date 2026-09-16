## 6.7 Topologia RabbitMQ cu *Dead Letter Exchange*

Această secțiune detaliază topologia de mesagerie asincronă bazată pe *RabbitMQ* implementată în cadrul platformei KinetoCare. Sunt analizate mecanismele de rutare semantică prin evenimente, carantina mesajelor toxice prin intermediul cozilor de tip *Dead Letter* și strategiile de garantare a consistenței eventuale prin deduplicarea mesajelor la nivel de consumator.

### 6.7.1 Motivarea arhitecturii de mesagerie asincronă distribuite

În cadrul unui sistem bazat pe microservicii, decuplarea componentelor reprezintă o cerință fundamentală pentru asigurarea disponibilității ridicate și a toleranței la erori. Spre deosebire de comunicarea sincronă realizată prin protocoale HTTP (unde componenta apelantă este blocată în așteptarea unui răspuns), utilizarea unei arhitecturi orientate pe evenimente (*Event-Driven Architecture* — EDA) permite componentelor operaționale să continue execuția fără a depinde de starea de funcționare a modulelor consumatoare.

Cu toate acestea, introducerea mesageriei asincrone aduce provocări specifice de inginerie software. Mesajele pot eșua în timpul procesării din motive diverse:
- Defecțiuni tranzitorii de infrastructură (o cădere temporară a bazei de date a consumatorului).
- Erori de *business* neprevăzute (o eroare de deserializare cauzată de o discrepanță de versiune a conținutului JSON).
- Blocaje la nivel de rețea.

În contextul platformei medicale KinetoCare, garantarea livrării notificărilor este crucială. O notificare de tip reminder medical omisă poate duce la pierderea ședinței de către un pacient cu afecțiuni severe, afectând direct actul terapeutic.

Pentru a preveni pierderea silențioasă a mesajelor și, în același timp, a evita congestionarea serverelor prin bucle infinite de reîncercare, platforma KinetoCare implementează o topologie de mesagerie avansată bazată pe **RabbitMQ**, ce reunește rutare semantică (*Topic Exchange*), carantină controlată (*Dead Letter Exchange*) și auto-vindecare defensivă (`DeadLetterConsumer`).

### 6.7.2 Structura și componentele topologiei RabbitMQ

Topologia *RabbitMQ* este proiectată pe trei paliere independente, fiecare cu un rol bine definit:

**Rutarea semantică prin *Topic Exchange* (`notificari.exchange`).** Pentru propagarea evenimentelor de notificare, platforma utilizează un `TopicExchange`. Spre deosebire de un *exchange* de tip *Direct* (care necesită o potrivire exactă a cheii de rutare) sau de tip *Fanout* (care difuzează orb mesajul către toate cozile legate), `TopicExchange` permite o rutare dinamică și semantică.

Producătorii de evenimente (de exemplu, `programari-service` sau `chat-service`) publică evenimente imutabile utilizând chei de rutare structurate ierarhic: `notificare.<domeniu_clinic>.<actiune_specifica>` (de exemplu, `notificare.programare.noua` sau `notificare.mesaj.nou`).

**Coada principală (`notificari.queue.v2`) cu argumente *Dead Letter*.** Coada principală ascultă evenimentele publicate pe *exchange*-ul central printr-un *binding* cu *wildcard* definit ca `notificare.#`.

În protocolul *AMQP*, caracterul `#` potrivește zero sau mai multe cuvinte separate prin punct. Acest detaliu tehnic implementează principiul **Open/Closed** din setul de principii SOLID la nivel de mesagerie: dacă în viitor este adăugat un nou serviciu (de exemplu, `evaluare-service`) care publică evenimentul `notificare.evaluare.noua`, coada principală îl va captura în mod automat, fără a fi necesară modificarea definițiilor de infrastructură sau repornirea brokerului *RabbitMQ*.

Pentru a asigura reziliența, coada `notificari.queue.v2` este declarată defensiv cu argumentul: `x-dead-letter-exchange: notificari.dlx`. Această instrucțiune obligă brokerul să captureze orice mesaj respins cu o confirmare negativă (*NACK*) de către consumator și să îl redirecționeze automat către *exchange*-ul de *Dead Letter*, în loc să îl șteargă sau să îl blocheze în capul cozii.

**Carantina prin *Dead Letter Exchange* (`notificari.dlx`) și coada dedicată (`notificari.queue.dead`).** *Exchange*-ul `notificari.dlx` este configurat ca *Fanout*. În această fază de carantină, rutarea semantică nu mai este necesară; scopul este simpla dirijare a tuturor mesajelor toxice într-o singură coadă de siguranță.

Mesajele ajung în coada `notificari.queue.dead`, unde sunt stocate pe termen lung pentru **audit și depanare manuală**. Niciun mesaj din această coadă nu este retrimis automat în coada principală, prevenind degradarea performanței sistemului din cauza unor date corupte recurente.

### 6.7.3 Diagrama arhitecturală a topologiei de mesagerie

Modul în care mesajele circulă de la microserviciile producătoare, prin *exchange*-urile și cozile brokerului *RabbitMQ*, până la consumatorii de succes și de carantină, este reprezentat în diagrama de mai jos:

```mermaid
flowchart LR
    subgraph Producatori["Producători de Evenimente (Microservicii)"]
        PS[programari-service]
        CS[chat-service]
    end

    subgraph Broker["RabbitMQ Message Broker Cluster"]
        direction TB
        EX["[Topic Exchange] notificari.exchange<br>Binding: notificare.#"]
        MQ["[Main Queue] notificari.queue.v2<br>x-dead-letter-exchange: notificari.dlx"]
        DLX["[DLX Fanout] notificari.dlx"]
        DLQ["[DLQ] notificari.queue.dead"]
        
        EX -->|"Rutare wildcard: notificare.#"| MQ
        MQ -. "Trimitere la NACK (requeue=false)" .-> DLX
        DLX --> DLQ
    end

    subgraph Consumatori["Consumatori (notificari-service)"]
        NC["NotificareConsumer [OK]<br>Salvează și persistă notificarea în aplicație"]
        DC["DeadLetterConsumer [Audit]<br>Înregistrează erori și alertează"]
    end

    PS -->|"notificare.programare.noua"| EX
    CS -->|"notificare.mesaj.nou"| EX
    MQ -->|Procesare cu succes (ACK)| NC
    DLQ -->|Procesare erori (confirmare pozitivă forțată)| DC
```

### 6.7.4 Mecanismul de confirmare negativă (*NACK*) și fluxul de eșec

Atunci când un mesaj este extras din coada principală `notificari.queue.v2`, componenta `NotificareConsumer` încearcă să execute logica de trimitere a alertelor.

Dacă în timpul execuției este generată o excepție (de exemplu, o eroare internă din MySQL sau o eroare de rețea), stiva Spring AMQP interceptează eroarea. În configurația standard, Spring AMQP reintroduce mesajul eșuat în aceeași coadă (`requeue = true`).

Într-un mediu *enterprise*, acest comportament este periculos: dacă eroarea este permanentă (un format JSON invalid), mesajul este reintrodus în coadă, extras din nou de consumator, eșuează din nou și reintră în coadă, generând o **buclă infinită de procesare** care epuizează resursele de calcul ale serverului.

Platforma KinetoCare previne acest comportament prin configurarea fabricii de containere Spring AMQP să emită confirmări negative (*NACK*) cu parametrul `requeue = false` în caz de eroare.

Calea parcursă de un mesaj eșuat este următoarea:
1. `NotificareConsumer` generează o excepție.
2. Interceptorul Spring AMQP trimite un semnal de tip *NACK* cu `requeue = false` către broker.
3. Brokerul *RabbitMQ* extrage mesajul toxic din coada principală și verifică argumentul `x-dead-letter-exchange`.
4. Mesajul este rutat către `notificari.dlx` și depus în siguranță în coada de carantină `notificari.queue.dead`.
5. Coada principală `notificari.queue.v2` continuă să proceseze restul mesajelor fără întârzieri sau blocaje.

### 6.7.5 Prevenirea recursiei cozilor de carantină

O eroare critică de proiectare, frecvent omisă în implementările comerciale, este apariția scenariului de **recursie a cozilor de carantină** (*Dead Letter Queue recursion*). Dacă însuși consumatorul de mesaje eșuate, `DeadLetterConsumer`, întâmpină o problemă în timp ce procesează sau jurnalizează un mesaj toxic, sistemul ar putea intra într-o buclă infinită în interiorul cozii de carantină.

Pentru a elimina această vulnerabilitate de infrastructură, este implementată o strategie de **absorbție explicită a excepțiilor** la nivelul `DeadLetterConsumer`:

```java
@RabbitListener(queues = RabbitMQConfig.DLQ_NAME)
public void proceseazaMesajEsuat(Message message) {
    try {
        String body = new String(message.getBody(), StandardCharsets.UTF_8);
        log.error("[DLQ ALERT] Mesaj clinic eșuat definitiv. Antete: {}, Payload: {}", 
            message.getMessageProperties().getHeaders(), body);
    } catch (Exception e) {
        // Absorbție absolută a excepțiilor pentru a opri orice recursivitate AMQP
        log.error("[DLQ CRITICAL ERROR] Eșec sever la procesarea mesajului din coada de carantină: {}", 
            e.getMessage());
    }
}
```

Prin utilizarea unui bloc global `try/catch` care capturează clasa generică `Exception`, consumatorul garantează că:
- Orice eroare la decodificarea corpului mesajului (de exemplu, un set de caractere UTF-8 corupt) este prinsă și jurnalizată în siguranță.
- Metoda se finalizează întotdeauna cu succes din perspectiva Spring AMQP, trimițând un semnal de tip *ACK* implicit către broker.
- Mesajul este eliminat definitiv din coada de carantină, prevenind complet riscul blocării brokerului prin apeluri recursive.

### 6.7.6 Imuabilitatea AMQP și justificarea sufixului `v2` al cozii

Prezența sufixului `v2` în denumirea cozii `notificari.queue.v2` documentează conformarea la o constrângere structurală majoră a protocolului AMQP 0-9-1. Conform specificației tehnice oficiale, **argumentele de configurare ale unei cozi sunt imutabile după declarare**.

Atunci când o coadă este declarată pentru prima dată în *RabbitMQ* (de exemplu, simpla coadă `notificari.queue`), parametrii săi constitutivi sunt persistați în starea brokerului. Dacă ulterior argumentul `x-dead-letter-exchange` este adăugat și microserviciul este repornit, stiva Spring AMQP va încerca să redeclare coada existentă cu noile argumente.

Brokerul *RabbitMQ* va detecta această tentativă de modificare a parametrilor imutabili și va respinge cererea cu excepția: `PRECONDITION_FAILED (406) - inequivalent arg 'x-dead-letter-exchange' for queue`.

Aceasta determină închiderea instantanee a canalului *AMQP* și prăbușirea microserviciului la pornire. Pentru a rezolva această problemă în medii de producție, fără a șterge coada veche (ceea ce ar duce la pierderea mesajelor neprocesate aflate în tranzit), cea mai bună practică inginerească constă în **declararea unei cozi noi cu un nume actualizat** — `notificari.queue.v2` — care include de la bun început argumentul DLX. Această decizie garantează o migrare fără întreruperi de serviciu și fără pierderi de date medicale sau operaționale.

### 6.7.7 Modelul Observer distribuit la nivel arhitectural

Topologia *RabbitMQ* din platforma KinetoCare reprezintă implementarea la scară largă a tiparului structural **Observer distribuit**, transpus peste o rețea de microservicii:

| Concept din tiparul *Observer* | Componentă KinetoCare | Responsabilitate arhitecturală |
|:---|:---|:---|
| **Subiectul observat (*Observable*)** | `programari-service` și `chat-service` | Publică evenimente imutabile (fapte clinice petrecute) pe magistrala de mesaje, fără a cunoaște cine le va consuma. |
| **Magistrala (*Event Bus*)** | `notificari.exchange` (*RabbitMQ*) | Gestionează rutarea semantică și decuplarea ierarhică, asigurând livrarea sigură a evenimentelor. |
| **Observatorul concret** | `NotificareConsumer` | Monitorizează fluxul de date principale și execută sarcinile de salvare a notificărilor în aplicație. |
| **Observatorul de erori** | `DeadLetterConsumer` | Monitorizează coada de carantină pentru a genera alerte administrative în caz de eșec sever. |

Această decuplare completă garantează că, dacă serviciul de notificări este oprit pentru mentenanță sau baza sa de date este blocată, activitatea principală a clinicii (crearea de programări, completarea fișelor) continuă neafectată. Mesajele se acumulează în siguranță în coada *RabbitMQ* și sunt procesate automat în momentul în care consumatorul devine din nou activ, asigurând toleranța la defecțiuni și continuitatea operațională.

### 6.7.8 Implementarea deduplicării și a idempotenței consumatorilor

Deoarece marjele de căutare ale planificatoarelor temporale se pot suprapune ușor în cazul unor variații de sincronizare a ceasului serverului sau rulărilor consecutive rapide, există riscul ca o programare aflată la granița exactă a minutelor de scanare să fie publicată sub formă de evenimente multiple în brokerul *RabbitMQ*. De asemenea, reîncercările la nivel de rețea din topologia de mesagerie pot introduce mesaje duplicate în flux.

Pentru a garanta procesarea unică a fiecărei notificări, este implementat un mecanism robust de deduplicare direct la nivelul consumatorului din `notificari-service`:

1. **Injectarea identificatorului de mesaj de către producători:** Componentele care publică evenimente pe magistrala de mesaje (`programari-service`, `pacienti-service`, `chat-service`) configurează proprietățile de mesaj AMQP (*MessageProperties*) injectând un identificator unic universal (UUID) în antetul standard `messageId`.

2. **Intercepția și înregistrarea atomică:** La consumare, `NotificareConsumer` din `notificari-service` extrage antetul `messageId` și apelează o metodă tranzacțională din `MesajProcesatRepository` care execută o interogare SQL nativă:
   ```sql
   INSERT INTO mesaje_procesate (message_id, processed_at)
   VALUES (:messageId, NOW())
   ON DUPLICATE KEY UPDATE message_id = message_id;
   ```

3. **Absorbirea duplicatelor:** Dacă instrucțiunea SQL returnează `0` rânduri afectate (*rows affected*), se deduce că mesajul cu acel ID a fost deja procesat anterior. Consumatorul blochează fluxul, ignoră duplicatul și trimite un semnal *ACK* către broker pentru a elimina mesajul din coadă, asigurând idempotența procesării fără a re-expedia notificarea.
