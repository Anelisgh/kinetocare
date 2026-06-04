## 6.7 Topologia RabbitMQ cu *Dead Letter Exchange*

Această secțiune detaliază topologia de mesagerie asincronă bazată pe *RabbitMQ* implementată în cadrul platformei KinetoCare. Sunt analizate mecanismele de rutare semantică prin evenimente, carantina mesajelor toxice prin intermediul cozilor de tip *Dead Letter* și strategiile de garantare a consistenței eventuale prin deduplicarea mesajelor la nivel de consumator.

### 6.7.1 Motivarea arhitecturii de mesagerie asincronă distribuite

În cadrul unui sistem bazat pe microservicii, decuplarea componentelor reprezintă o cerință fundamentală pentru a asigura disponibilitatea ridicată și toleranța la erori. Spre deosebire de comunicarea sincronă realizată prin protocoale HTTP (unde serviciul apelant este blocat în așteptarea unui răspuns de la destinație), utilizarea unei arhitecturi orientate pe evenimente (*Event-Driven Architecture* - EDA) permite serviciilor operaționale să își continue execuția fără a depinde de starea de funcționare a modulelor consumatoare.

Cu toate acestea, introducerea mesageriei asincrone aduce o serie de provocări de inginerie software complexe. Mesajele pot eșua în timpul procesării din motive diverse:
- Defecțiuni tranzitorii de infrastructură (cum ar fi o cădere temporară a bazei de date a consumatorului).
- Erori de business neprevăzute (de exemplu, o eroare de deserializare cauzată de o discrepanță de versiune a conținutului JSON).
- Blocaje la nivel de rețea.

În contextul platformei medicale **KinetoCare**, garantarea livrării notificărilor este crucială. O notificare de tip reminder medical omisă poate duce la pierderea ședinței de către un pacient cu afecțiuni severe, afectând direct actul terapeutic.

Pentru a preveni pierderea silențioasă a mesajelor și pentru a evita, în același timp, congestionarea serverelor prin bucle infinite de reîncercare, KinetoCare implementează o topologie de mesagerie avansată bazată pe **RabbitMQ**, ce reunește concepte de rutare semantică (*Topic Exchange*), carantină controlată (*Dead Letter Exchange*) și auto-vindecare defensivă (`DeadLetterConsumer`).

### 6.7.2 Structura și componentele topologiei RabbitMQ

Topologia *RabbitMQ* din cadrul platformei este proiectată pe trei paliere independente, fiecare având un rol bine definit:

**Rutarea semantică prin Topic Exchange (`notificari.exchange`).** Pentru a propaga evenimentele de notificare în sistem, KinetoCare utilizează un `TopicExchange`. Spre deosebire de un exchange de tip *Direct* (care necesită o potrivire exactă a cheii de rutare) sau de tip *Fanout* (care difuzează în mod orb mesajul către toate cozile legate), `TopicExchange` permite o rutare dinamică și semantică.

Producătorii de evenimente (cum ar fi `programari-service` sau `chat-service`) publică evenimente imutabile folosind chei de rutare structurate ierarhic, sub forma: `notificare.<domeniu_clinic>.<actiune_specifica>` (de exemplu, `notificare.programare.noua` sau `notificare.mesaj.nou`).

**Coada principală (`notificari.queue.v2`) cu argumente *Dead Letter*.** Coada principală ascultă evenimentele publicate pe exchange-ul central prin intermediul unui binding cu *wildcard* definit ca: `notificare.#`

În protocolul *AMQP*, caracterul `#` potrivește zero sau mai multe cuvinte separate prin punct. Acest detaliu tehnic implementează principiul **Open/Closed** din setul de principii SOLID la nivel de mesagerie. Dacă în viitor se adaugă un nou serviciu (de exemplu, `evaluare-service`) care publică evenimentul `notificare.evaluare.noua`, coada principală îl va captura în mod automat, fără a fi necesară modificarea definițiilor de infrastructură sau repornirea brokerului *RabbitMQ*.

Pentru a asigura reziliența, coada `notificari.queue.v2` este declarată în mod defensiv cu argumentul special: `x-dead-letter-exchange: notificari.dlx`

Această instrucțiune obligă brokerul *RabbitMQ* să captureze orice mesaj care este respins cu o confirmare negativă (*NACK*) de către consumator și să îl redirecționeze automat către exchange-ul de *Dead Letter*, în loc să îl șteargă sau să îl blocheze în capul cozii.

**Carantina prin *Dead Letter Exchange* (`notificari.dlx`) și coada dedicată (`notificari.queue.dead`).** Exchange-ul `notificari.dlx` este configurat ca un exchange de tip *Fanout*. În această fază de carantină, rutarea semantică nu mai este necesară; scopul este simpla dirijare a tuturor mesajelor toxice (mesaje care eșuează repetat la procesare) într-o singură coadă de siguranță.

Mesajele ajung în coada `notificari.queue.dead`, unde sunt stocate pe termen lung pentru **audit și depanare manuală**. Niciun mesaj din această coadă nu este retrimis în mod automat în coada principală, prevenind degradarea performanței sistemului din cauza unor date corupte recurente.

### 6.7.3 Diagrama arhitecturală a topologiei de mesagerie

Modul în care mesajele circulă de la microserviciile producătoare, prin exchange-urile și cozile brokerului *RabbitMQ*, până la consumatorii de succes și de carantină este reprezentat în diagrama de mai jos:

```mermaid
flowchart LR
    subgraph Producatori["Producători de Evenimente (Microservicii)"]
        PS[programari-service]
        CS[chat-service]
    end

    subgraph Broker["RabbitMQ Message Broker Cluster"]
        direction TB
        EX["[Topic Exchange] notificari.exchange<br/>Binding: notificare.#"]
        MQ["[Main Queue] notificari.queue.v2<br/>x-dead-letter-exchange: notificari.dlx"]
        DLX["[DLX Fanout] notificari.dlx"]
        DLQ["[DLQ] notificari.queue.dead"]
        
        EX -->|"Rutare wildcard: notificare.#"| MQ
        MQ -. "Trimitere la NACK (requeue=false)" .-> DLX
        DLX --> DLQ
    end

    subgraph Consumatori["Consumatori (notificari-service)"]
        NC["NotificareConsumer [OK]<br/>Salvează și persistă notificarea în aplicație"]
        DC["DeadLetterConsumer [Audit]<br/>Înregistrează erori și alertează"]
    end

    PS -->|"notificare.programare.noua"| EX
    CS -->|"notificare.mesaj.nou"| EX
    MQ -->|Procesare cu succes (ACK)| NC
    DLQ -->|Procesare erori (confirmare pozitivă forțată)| DC
```

### 6.7.4 Mecanismul de confirmare negativă (NACK) și fluxul de eșec

Atunci când un mesaj este extras din coada principală `notificari.queue.v2`, componenta `NotificareConsumer` încearcă să execute logica de trimitere a alertelor.

Dacă în timpul execuției se aruncă o excepție (de exemplu, o eroare internă din MySQL sau o eroare de rețea la transmiterea evenimentului), stiva Spring AMQP interceptează eroarea. În configurația standard, Spring AMQP reintroduce mesajul eșuat în aceeași coadă din care a plecat (`requeue = true`).

Într-un mediu *enterprise*, acest comportament standard este periculos. Dacă eroarea este una permanentă (cum ar fi un format JSON invalid), mesajul este reintrodus în coadă, extras din nou de consumator, eșuează din nou și reintră în coadă, generând o **buclă infinită de procesare** care poate consuma resursele de calcul ale serverului.

KinetoCare previne acest comportament prin configurarea fabricii de containere Spring AMQP să emită confirmări negative (*NACK*) cu parametrul `requeue = false` în caz de eroare.

Calea parcursă de un mesaj eșuat este următoarea:

1. `NotificareConsumer` aruncă o excepție.
2. Interceptorul Spring AMQP prinde excepția și trimite un semnal de tip *NACK* cu `requeue = false` către broker.
3. Brokerul *RabbitMQ* extrage mesajul toxic din coada principală și verifică argumentul `x-dead-letter-exchange`.
4. Mesajul este rutat către `notificari.dlx` și depus în siguranță în coada de carantină `notificari.queue.dead`.
5. Coada principală `notificari.queue.v2` continuă să proceseze restul mesajelor fără întârzieri sau blocaje.

### 6.7.5 Prevenirea recursiei cozilor de carantină

O eroare critică de proiectare, adesea trecută cu vederea în implementările comerciale, este apariția scenariului de **recursie a cozilor de carantină** (*Dead Letter Queue recursion*). Dacă însuși consumatorul de mesaje eșuate, `DeadLetterConsumer`, întâmpină o problemă în timp ce încearcă să proceseze sau să logheze un mesaj toxic, sistemul ar putea intra într-o buclă infinită în interiorul cozii de carantină.

Pentru a elimina această vulnerabilitate de infrastructură, KinetoCare implementează o strategie de **absorbție explicită a excepțiilor** la nivelul `DeadLetterConsumer`:

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

Prin utilizarea unui bloc global de tip `try/catch` care capturează clasa generică `Exception`, consumatorul garantează că:
- Orice eroare care ar putea apărea la decodificarea corpului mesajului (cum ar fi un set de caractere UTF-8 corupt) este prinsă și logată în siguranță.
- Metoda se finalizează întotdeauna cu succes din perspectiva Spring AMQP, trimițând un semnal de tip *ACK* implicit către broker.
- Mesajul este eliminat definitiv din coada de carantină, eliminând complet riscul blocării brokerului prin apeluri recursive.

### 6.7.6 Imuabilitatea AMQP și justificarea sufixului (v2) al cozii

Prezența sufixului `v2` în denumirea cozii `notificari.queue.v2` reprezintă dovada conformării la o constrângere structurală majoră a protocolului AMQP 0-9-1. Conform specificației tehnice oficiale a protocolului *AMQP*, **argumentele de configurare ale unei cozi sunt imutabile după declarare**.

Atunci când o coadă este declarată pentru prima dată în *RabbitMQ* (de exemplu, simpla coadă `notificari.queue`), parametrii săi constitutivi sunt persistați în starea brokerului. Dacă ulterior echipa de dezvoltare decide să adauge argumentul `x-dead-letter-exchange` și repornește serviciul, stiva Spring AMQP va încerca să redeclare coada existentă cu noile argumente.

Brokerul *RabbitMQ* va detecta această tentativă de modificare a parametrilor imutabili și va respinge cererea aruncând o excepție de tipul: `PRECONDITION_FAILED (406) - inequivalent arg 'x-dead-letter-exchange' for queue`.

Aceasta determină închiderea instantanee a canalului *AMQP* și prăbușirea microserviciului la pornire. Pentru a rezolva această problemă în medii de producție, fără a șterge coada veche (ceea ce ar duce la pierderea imediată a tuturor mesajelor neprocesate aflate în tranzit), cea mai bună practică inginerească constă în **declararea unei cozi noi cu un nume actualizat**, respectiv `notificari.queue.v2`, care include de la bun început argumentul DLX. Această decizie garantează o migrare fără întreruperi de serviciu și fără pierderi de date comerciale sau medicale.

### 6.7.7 Modelul Observer distribuit la nivel arhitectural

Topologia *RabbitMQ* din platforma KinetoCare reprezintă implementarea la scară largă a tiparului structural **Observer distribuit**, transpus peste o rețea de microservicii:

| Concept din tiparul Observer | Componentă KinetoCare | Responsabilitate Arhitecturală |
| :--- | :--- | :--- |
| **Subiectul Observat (Observable)** | `programari-service` și `chat-service` | Publică evenimente imutabile (fapte clinice petrecute) pe magistrala de mesaje, fără a cunoaște cine le va consuma. |
| **Magistrala (Event Bus)** | `notificari.exchange` (*RabbitMQ*) | Gestionează rutarea semantică și decuplarea ierarhică, asigurând livrarea sigură a evenimentelor. |
| **Observatorul Concret** | `NotificareConsumer` | Monitorizează fluxul de date principale și execută sarcinile de salvare a notificărilor în aplicație și persistență. |
| **Observatorul de Erori** | `DeadLetterConsumer` | Monitorizează coada de carantină pentru a genera alerte administrative în caz de eșec sever. |

Această decuplare completă garantează că, dacă serviciul de notificări este oprit pentru mentenanță sau are baza de date blocată, activitatea principală a clinicii (crearea de programări, completarea fișelor) continuă să funcționeze neafectată. Mesajele se acumulează în siguranță în coada *RabbitMQ* și sunt procesate automat în momentul în care consumatorul devine din nou activ, asigurând toleranța la defecțiuni și continuitatea operațională.

### 6.7.8 Direcție de optimizare: Deduplicarea și idempotența consumatorilor

Deoarece marjele de căutare ale planificatoarelor temporale se pot suprapune ușor în cazul unor variații de sincronizare a ceasului serverului sau rulărilor consecutive rapide, există un risc ca o programare aflată la granița exactă a minutelor de scanare să fie publicată sub formă de evenimente multiple în brokerul *RabbitMQ*.

Deși în versiunea curentă a platformei deduplicarea nu este implementată activ (evenimentele fiind procesate direct prin salvarea în baza de date), o optimizare viitoare recomandată constă în delegarea responsabilității de deduplicare la nivelul consumatorului:

1. Serviciul de programări (`programari-service`) identifică programările candidate și emite evenimente de tip `notificare.reminder.nou` pe magistrala de mesaje asincrone.
2. Evenimentele sunt consumate asincron de către consumatorul din `notificari-service`.
3. Înainte de a stoca și expedia notificarea, `notificari-service` interoghează tabela `notificari` pentru a verifica dacă există deja o înregistrare cu același tip (de exemplu, `REMINDER_24H` sau `REMINDER_2H`) și `entitate_legata_id` egal cu identificatorul programării respective.
4. Dacă înregistrarea există, mesajul este absorbit în mod silențios la nivel de consumator (trimițând *ACK* direct brokerului), garantând idempotența procesării și eliminând redundanța în comunicarea cu utilizatorii finali.
