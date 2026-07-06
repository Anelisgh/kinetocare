## Capitole

### Capitolul 1: Introducere
1.1 Motivația și contextul proiectului
1.2 Obiectivele lucrării
1.3 Contribuțiile originale ale proiectului
1.4 Structura lucrării

### Capitolul 2: Analiza domeniului și a necesităților
2.1 Provocările din managementul clinic
    2.1.1 Fragmentarea comunicării și a fluxului informațional
    2.1.2 Lipsa trasabilității clinice și a monitorizării continue
    2.1.3 Dificultăți în managementul disponibilității și al resurselor
    2.1.4 Ineficiențe în managementul administrativ și decizional
    2.1.5 Protecția datelor medicale sensibile
2.2 Analiza soluțiilor existente și limitele acestora
    2.2.1 Analiza comparativă a soluțiilor existente
    2.2.2 Lacunele comune identificate
    2.2.3 Limitările KinetoCare față de soluțiile mature
    2.2.4 Analiza SWOT a sistemului propus
2.3 Cerințele funcționale ale sistemului
    2.3.1 Modulul de Autentificare și Autorizare
    2.3.2 Modulul Pacient
    2.3.3 Modulul Terapeut
    2.3.4 Modulul Administrativ
    2.3.5 Module transversale
    2.3.6 Prioritizarea cerințelor — MoSCoW
    2.3.7 Maparea explicită și prioritizarea MoSCoW a cerințelor funcționale
2.4 Cerințele non-funcționale ale sistemului
    2.4.1 Securitate și protecția datelor
    2.4.2 Scalabilitate și performanță
    2.4.3 Integritatea și consistența datelor
    2.4.4 Reziliență și toleranță la erori
    2.4.5 Auditabilitate și trasabilitate
    2.4.6 Portabilitate și mentenabilitate
    2.4.7 Uzabilitate

### Capitolul 3: Alegerea Tehnologiilor și a Stivei Software
3.1 Alegerea arhitecturii bazate pe microservicii
    3.1.1 Contextul deciziei arhitecturale
    3.1.2 Evaluarea critică a alternativei monolitice
    3.1.3 Structura microserviciilor KinetoCare
    3.1.4 Topologia dependențelor și rolurile structurale
    3.1.5 Compromisurile arhitecturale asumate
3.2 Stiva *backend*: Spring Boot, Spring Cloud *Gateway*, OpenFeign și RabbitMQ
    3.2.1 Spring Boot — Infrastructură standardizată și principiul DRY
    3.2.2 Spring Cloud *Gateway* — Punct unic de intrare și tiparul *BFF*
    3.2.3 Spring Cloud OpenFeign — Contracte API declarative
    3.2.4 Arhitectura orientată pe evenimente și RabbitMQ
3.3 Securitate și Managementul Identității: Keycloak și JWT
    3.3.1 Argumentarea delegării autentificării către un *Identity Provider* dedicat
    3.3.2 Integrarea Keycloak: rolul de orchestrator al microserviciului de identitate
    3.3.3 Ciclul de viață al jetoanelor: mitigarea riscurilor XSS prin tiparul *BFF*
    3.3.4 Modelul *Zero-Trust* și validarea descentralizată
    3.3.5 Autorizarea pe două niveluri (*Defense in Depth*)
    3.3.6 Extinderea securității peste protocolul WebSocket (STOMP)
    3.3.7 Problema scrierii duale și tranzacțiile compensatorii
3.4 Tehnologii *frontend*: ecosistemul React și arhitectura interfeței
    3.4.1 Paradigma *Single Page Application* (SPA) și separarea responsabilităților
    3.4.2 Managementul sesiunii și decodificarea criptografică locală
    3.4.3 Interceptarea traficului și normalizarea erorilor
    3.4.4 Autorizarea la nivelul prezentării și barierele clinice (*Guards*)
    3.4.5 Arhitectura de comunicare în timp real: STOMP și negocierea protocolului
    3.4.6 Ecosistemul tehnologic și optimizarea livrării (*build pipeline*)
    3.4.7 Reziliența interfeței: toleranța la erori și *smart polling*
3.5 Infrastructură și containerizare: Docker, Kubernetes și persistența datelor
    3.5.1 Containerizarea și paritatea mediilor de execuție (Docker)
    3.5.2 Kubernetes: orchestrarea și scalabilitatea în mediul de producție
    3.5.3 Arhitectura datelor: MySQL, izolare și denormalizare controlată
    3.5.4 Sinergia componentelor: apărare stratificată în profunzime

### Capitolul 4: Arhitectura Sistemului
4.1 Harta microserviciilor și delimitarea contextelor (*Bounded Contexts*)
    4.1.1 Structura de ansamblu a sistemului
    4.1.2 Delimitarea contextelor de domeniu (Bounded Contexts)
    4.1.3 Cheia universală de pivotare: Identificatorul extern
    4.1.4 Topologia arhitecturală a sistemului
4.2 Fluxul de comunicare inter-servicii: sincron vs. asincron
    4.2.1 Principiul de selecție a stilului de comunicare
    4.2.2 Comunicarea sincronă și rezoluția dependențelor
    4.2.3 Comunicarea asincronă și tiparul *Event-Driven* (*EDA*)
    4.2.4 Analiza decizională a topologiei de integrare
4.3 API *Gateway* și tiparul *Backend-For-Frontend* (*BFF*)
    4.3.1 Rolurile duale ale API *Gateway*-ului
    4.3.2 Strategia de rutare și normalizarea traficului
    4.3.3 Medierea securității: Izolarea jetoanelor criptografice
    4.3.4 Arhitectura BFF și execuția asincronă prin tiparul *Scatter-Gather*
    4.3.5 Componentele strategice de agregare
    4.3.6 Asimetria paradigmelor tehnologice: WebFlux vs. MVC
    4.3.7 Analiza arhitecturală a amplasării agregării de date
    4.3.8 Reprezentarea vizuală a Agregării BFF
4.4 Modelul de securitate Zero-Trust distribuit
    4.4.1 Principiul lipsei de încredere implicită (*de-perimeterization*)
    4.4.2 Descentralizarea validării criptografice (JWKS)
    4.4.3 Autorizare stratificată și Apărare în Profunzime (Defense in Depth)
    4.4.4 Complexitatea securizării canalelor persistente (STOMP/WebSocket)
    4.4.5 Reprezentarea vizuală a securității asincrone (STOMP)
4.5 Izolarea datelor și modelul persistenței (Database-per-Service)
    4.5.1 Arhitectura descentralizată a stratului de date
    4.5.2 Beneficiile decuplării la nivel de domeniu
    4.5.3 Integritatea relațională prin chei externe logice
    4.5.4 Denormalizarea controlată și tiparul Snapshot
    4.5.5 Reprezentarea vizuală a tiparului *Snapshot* în arhitectura distribuită
    4.5.6 Trasabilitatea modificărilor clinice și mecanismul de Audit Trail (GDPR)

### Capitolul 5: Proiectarea și Modelarea Bazelor de Date Relaționale
5.1 Justificarea modelului relațional față de alternativele NoSQL
    5.1.1 Caracteristicile domeniului care impun ACID
    5.1.2 Evaluarea alternativelor (MongoDB, Redis, PostgreSQL vs. MySQL)
    5.1.3 Configurarea motorului InnoDB și nivelul de izolare ales
5.2 Modelul conceptual al datelor — harta schemelor
    5.2.1 Harta globală a schemelor (Diagramă Mermaid)
    5.2.2 Diferența dintre cheile externe fizice și referințele aplicative
5.3 Schemele relaționale detaliate
    5.3.1 user_db — gestiunea identității și rolurilor
    5.3.2 terapeuti_db — resurse clinice și disponibilitate
    5.3.3 pacienti_db — dosarul medical și jurnalul subiectiv
    5.3.4 Schema programari_db — Nucleul tranzacțional clinic
    5.3.5 servicii_db — catalogul tarifar
    5.3.6 chat_db — mesageria clinică
    5.3.7 notificari_db — alertele și centrul de notificări
5.4 Strategia de indexare și optimizarea interogărilor critice
    5.4.1 Indexuri pentru prevenirea double-booking
    5.4.2 Indexuri compuse pentru raportare statistică
    5.4.3 Indexuri pentru latența paginilor de inbox
5.5 Decizii de tipizare și constrângeri la nivel de schemă
    5.5.1 Managementul UUID-urilor și corelarea cu sistemele IAM
    5.5.2 Precizia financiară și controlul erorilor de rotunjire
    5.5.3 Controlul stărilor prin tipul ENUM la nivel de motor SQL

### Capitolul 6: Implementări Tehnice și Algoritmi de Decizie
6.1 Automatul finit determinist pentru determinarea traiectoriei clinice
    6.1.1 Motivarea și problema clinică adresată
    6.1.2 Modelul conceptual al automatului finit clinic
    6.1.3 Analiza detaliată a stărilor și implementarea tranzițiilor
    6.1.4 Diagrama de tranziție a automatului clinic
    6.1.5 Proprietatea de auto-ciclare și reziliența datelor
6.2 Algoritmul Greedy de generare a ferestrelor de disponibilitate terapeutică
    6.2.1 Enunțul problemei de planificare clinică
    6.2.2 Fazele premergătoare: Colectarea defensivă a constrângerilor
    6.2.3 Nucleul algoritmului: Fereastra glisantă cronologică
    6.2.4 Diagrama de secvență a generării de sloturi
    6.2.5 Analiza complexității asimptotice și decizia de design
    6.2.6 Toleranța defensivă la absența datelor
6.3 Gestionarea Partiționării Temporale la Granița Zilei în Planificatorul de Remindere
    6.3.1 Contextul și motivarea problemei: Proiectarea defensivă și granița temporală
    6.3.2 Eșecul logic al interogării SQL standard (Naive Query)
    6.3.3 Soluția de partiționare binară a ferestrei
    6.3.4 Diagrama de decizie a partiționării temporale
    6.3.5 Frecvența scheduler-ului și marja de siguranță
    6.3.6 Concluzii privind arhitectura temporal-defensivă
6.4 Propagarea Contextului de Securitate în Sesiunile WebSocket STOMP
    6.4.1 Decalajul arhitectural de protocol HTTP → TCP și natura problemei
    6.4.2 Puntea de protocol prin interceptarea canalului STOMP
    6.4.3 Mecanismele de interceptare: preSend și postSend
    6.4.4 Diagrama secvențială a propagării contextului de securitate
    6.4.5 Robustețea și tratarea excepțiilor de autentificare
6.5 Agregarea Datelor Clinice și Rezolvarea Problemei N+1 Inter-servicii
    6.5.1 Contextul: Valoarea clinică a dosarului integrat (Fișa Pacientului)
    6.5.2 Harta completă a orchestrării inter-servicii
    6.5.3 Manifestarea problemei N+1 la nivel inter-servicii
    6.5.4 Strategia defensivă de degradare grațioasă (Graceful Degradation)
    6.5.5 Compromisul arhitectural asumat și optimizarea teoretică
    6.5.6 Comparație critică: Agregarea din *Gateway* (BFF) vs. Fișa Pacientului
6.6 Implementarea Tranzacțiilor de Compensare (Dual-write Keycloak + DB)
    6.6.1 Problema consistenței distribuite în scenariile de identitate
    6.6.2 Înregistrarea utilizatorului: Strategia de rollback compensatoriu sincron
    6.6.3 Diagrama secvențială a înregistrării cu compensare
    6.6.4 Dezactivarea contului: Mecanism de compensare sincronă cu consistență asimetrică
    6.6.5 Evaluarea alternativelor arhitecturale
    6.6.6 Idempotența și reziliența tranzacțiilor compensatorii
6.7 Topologia RabbitMQ cu *Dead Letter Exchange*
    6.7.1 Motivarea arhitecturii de mesagerie asincronă distribuite
    6.7.2 Structura și componentele topologiei RabbitMQ
    6.7.3 Diagrama arhitecturală a topologiei de mesagerie
    6.7.4 Mecanismul de confirmare negativă (NACK) și fluxul de eșec
    6.7.5 Prevenirea recursiei cozilor de carantină
    6.7.6 Imuabilitatea AMQP și justificarea sufixului (v2) al cozii
    6.7.7 Modelul Observer distribuit la nivel arhitectural
    6.7.8 Direcție de optimizare: Deduplicarea și idempotența consumatorilor

### Capitolul 7: Interfața cu Utilizatorul și Fluxurile Operaționale
7.1 Fluxul Pacientului: De la Profilare Clinică la Monitorizare
    7.1.1 Configurarea inițială și securizarea prin bariere de rutare
    7.1.2 Selectarea terapeutului și rezoluția datelor prin BFF
    7.1.3 Fluxul de programare și rezoluția autonomă a serviciului
    7.1.4 Jurnalul post-ședință: Colectarea datelor longitudinale
    7.1.5 Modulul de mesagerie: Generarea la cerere a conversațiilor și securitatea clinică
    7.1.6 Harta fluxului de navigare și decizie al pacientului
7.2 Fluxul Operațional al Terapeutului: Calendar și Documentare
    7.2.1 Configurarea profilului și managementul disponibilităților
    7.2.2 Calendarul clinic activ: Încărcare dinamică bazată pe vizor
    7.2.3 Documentarea clinică: Fișa integrată și diagnosticarea vizuală
    7.2.4 Schema fluxului de lucru al terapeutului
7.3 Panoul de Administrare și Agregarea Datelor Statistice
    7.3.1 Securitatea rolului de administrator și reziliența provizionării
    7.3.2 Managementul locațiilor și principiul apărării stratificate
    7.3.3 Catalogul de servicii clinice și aplicarea tiparului Snapshot
    7.3.4 Panoul de Business Intelligence: Optimizarea performanței interfeței
    7.3.5 Arhitectura panoului administrativ și topologia datelor

### Capitolul 8: Validare, Testare și Performanță
8.1 Testarea unitară a componentelor algoritmice critice
8.2 Validarea concurenței la nivelul nucleului tranzacțional
8.3 Optimizarea performanței rețelei și eliminarea interogărilor N+1
8.4 Asigurarea idempotenței consumatorilor AMQP

### Capitolul 9: Limitări Arhitecturale și Direcții Viitoare
9.1 Limitări Arhitecturale și Tehnice Asumate
    9.1.1 Consistența distribuită și riscul rezidual al scrierilor duale (*Dual-Write*)
    9.1.2 Agregarea fișei clinice și limitarea asamblării secvențiale
    9.1.3 Ineficiența stocării resurselor binare în baza de date
    9.1.4 Limitări de integrare: Interoperabilitatea fiscală și clinică
9.2 Direcții Viitoare de Dezvoltare și Scalare
    9.2.1 Evoluția către o arhitectură Multi-Tenant (*SaaS*)
    9.2.2 Refactorizarea integrării prin tiparul *Transactional Outbox*
    9.2.3 Optimizarea orchestrării datelor prin utilizarea *endpoint*-urilor *Batch*
    9.2.4 Asistență AI pentru documentarea clinică (LLM *Self-Hosted*)
    9.2.5 Externalizarea stocării media (*Object Storage*)
    9.2.6 Garantarea idempotenței prin tabelă dedicată de deduplicare (*Idempotency Key*)

### Capitolul 10: Concluzii
Bibliografie
Anexe