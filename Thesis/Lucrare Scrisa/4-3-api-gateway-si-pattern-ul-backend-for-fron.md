## 4.3 API *Gateway* și tiparul *Backend-For-Frontend* (*BFF*)

Această secțiune analizează în detaliu rolurile îndeplinite de componenta API *Gateway* la marginea sistemului distribuit. Sunt prezentate mecanismele de rutare a traficului, politicile de izolare a jetoanelor de securitate și modul în care stiva reactivă WebFlux facilitează agregarea performantă de date prin tiparul *Backend-For-Frontend* (*BFF*).

### 4.3.1 Rolurile duale ale API *Gateway*-ului

În arhitectura platformei KinetoCare, componenta API *Gateway* nu funcționează ca un simplu rutor de rețea, ci implementează simultan două roluri arhitecturale distincte, fundamentate pe stiva reactivă Spring WebFlux:

- ***Proxy* invers transparent (*Edge Router*):** Pentru marea majoritate a cererilor, *Gateway*-ul acționează ca o barieră de trecere. Căile de acces sunt normalizate prin eliminarea prefixelor externe, iar cererile sunt direcționate către componentele responsabile pentru domeniul respectiv.
- **Orchestrator *Backend-For-Frontend* (*BFF*):** Pentru interfețele complexe care necesită asamblarea datelor din multiple domenii, *Gateway*-ul funcționează ca un agregator activ. Sunt orchestrate apeluri paralele către microservicii, iar răspunsurile sunt consolidate într-o structură unică de date, livrată clientului.

Această separare a responsabilităților garantează un nivel ridicat de mentenabilitate: logica pură de rutare este definită declarativ prin configurații externe, în timp ce logica de agregare *BFF* este izolată programmatic în controlere și componente dedicate.

### 4.3.2 Strategia de rutare și normalizarea traficului

Mecanismul de rutare aplică un model de evaluare ordonată și secvențială, prevenind anomaliile de tip potrivire ambiguă. O consecință directă a proiectării bazate pe *Domain-Driven Design* se reflectă în expunerea componentei `terapeuti-service`. Deși entitățile de specialitate (profil profesional, locații fizice, matrice de disponibilitate și concedii) aparțin aceluiași context delimitat, ele sunt expuse ca resurse *REST* distincte. Rutarea impune evaluarea prioritizată a căilor specifice înaintea identificatorului general al componentei, prevenind coliziunile de potrivire.

La nivel global, *Gateway*-ul centralizează normalizarea politicilor *Cross-Origin Resource Sharing* (*CORS*). Prin filtrele de deduplicare aplicate la marginea rețelei, arhitectura previne coruperea răspunsurilor HTTP — o problemă recurentă în sistemele distribuite în care componentele din aval adaugă redundant propriile directive *CORS*.

### 4.3.3 Medierea securității: izolarea jetoanelor criptografice

O responsabilitate critică a *Gateway*-ului, anterioară oricărei agregări de date, este medierea procesului de autentificare. *Endpoint*-urile de obținere și revocare a sesiunii sunt exceptate de la filtrele standard de validare JWT, constituind tocmai punctele de generare a acestora.

Pentru a neutraliza vulnerabilitățile de tip *Cross-Site Scripting* (*XSS*) inerente aplicațiilor *SPA*, *Gateway*-ul implementează separarea și izolarea jetoanelor:

1. *Proxy*-ul primește credențialele brute, asamblează o cerere securizată cu datele clientului intern și o expediază către serverul Keycloak.
2. La primirea răspunsului de la furnizorul de identitate, *Gateway*-ul interceptează structura JSON ce conține atât jetonul de acces cu viață scurtă, cât și jetonul de reîmprospătare cu viață lungă.
3. Jetonul de reîmprospătare (*refresh token*) este extras din corpul răspunsului și injectat într-un antet `Set-Cookie` marcat cu directivele `HttpOnly` și `SameSite=Lax`.

Prin această arhitectură, mediul JavaScript din browser primește și manipulează exclusiv jetonul volatil de acces, în timp ce jetonul responsabil pentru menținerea sesiunii rămâne invizibil codului client, gestionat exclusiv de mecanismele interne ale browserului la instrucțiunile *Gateway*-ului.

### 4.3.4 Arhitectura BFF și execuția asincronă prin tiparul *Scatter-Gather*

Tiparul *Backend-For-Frontend* răspunde direct problemei penalizărilor de latență din rețelele publice (*N+1 round-trips*). Dacă un panou de bord clinic ar solicita date de identitate, programări și profil medical prin cereri individuale ale browserului, costul negocierii TCP/TLS s-ar multiplica corespunzător numărului de apeluri.

*Gateway*-ul neutralizează această latență mutând faza de colectare a datelor în interiorul rețelei virtuale a *cluster*-ului, unde latența inter-servicii se situează la ordinul microsecundelor. Prin intermediul bibliotecii *Project Reactor*, *Gateway*-ul implementează tiparul *Scatter-Gather*: cereri paralele sunt lansate către componentele țintă, finalizarea tuturor este așteptată non-blocant, iar răspunsurile sunt consolidate. O proprietate arhitecturală esențială a acestei implementări este **degradarea grațioasă**: în cazul în care un microserviciu secundar raportează o eroare, fluxul reactiv interceptează eroarea, o absoarbe și asamblează un răspuns parțial valid, prevenind colapsul întregii interfețe.

### 4.3.5 Componentele strategice de agregare

Logica de consolidare a datelor este distribuită pe patru controllere specializate:

1. **Agregatorul tabloului de bord (*Homepage*):** Furnizează datele contextualizate pentru pagina de start. Pentru pacienți, este executată o orchestrare asincronă în două faze: profilul clinic consolidat este preluat concomitent cu determinarea celei mai apropiate programări și a stadiului din planul curent de recuperare medicală.

2. **Consolidatorul de profil (*Profile*):** Cea mai complexă asamblare statică, necesitând până la 5 apeluri interne paralele. Datele de identitate sunt reunite asincron cu istoricul clinic, detaliile terapeutului alocat și locația fizică a clinicii, coliziunile de identificatori fiind rezolvate înainte de livrarea structurii plate către interfață. Metoda funcționează bidirecțional, gestionând decompunerea și distribuția paralelă a actualizărilor profilului.

3. **Orchestratorul interfeței de mesagerie (*Chat*):** Generează conversații virtuale la nivel de *Gateway* utilizând tiparul *Virtual Proxy* — un obiect surogat în memorie pentru canalul de comunicare. Persistența fizică a conversației în baza de date este amânată prin Inițializare Leneșă (*Lazy Initialization*) în `chat-service`, până la expedierea primului mesaj efectiv. Rezoluția în masă (*batch resolution*) a identităților este inclusă pentru a minimiza interogările suplimentare.

4. **Motorul de căutare (*Search*):** Optimizează procesul de alocare a terapeutului combinând rezultatele de filtrare geografică și profesională cu rezoluția identităților prin procesare în lot, esențială pentru eficiența la scară largă.

### 4.3.6 Asimetria paradigmelor tehnologice: WebFlux vs. MVC

Stiva reactivă Spring WebFlux este adoptată exclusiv la nivelul API *Gateway*-ului, în timp ce microserviciile din aval rulează pe un model imperativ, blocant — Spring MVC.

Această asimetrie reflectă profilurile de operare structural diferite ale componentelor. *Gateway*-ul execută operațiuni cu intensitate ridicată pe rețea: lansează cereri HTTP multiple și așteaptă răspunsuri concurente. Modelul reactiv bazat pe bucla de evenimente gestionează eficient acest tipar cu un consum minim de fire de execuție. Microserviciile de domeniu execută operațiuni tranzacționale asupra bazelor de date prin drivere relaționale blocante (JDBC); introducerea programării reactive la acel nivel nu ar aduce beneficii reale de performanță, ci ar crește artificial complexitatea codului și dificultatea depanării. Restrângerea stivei reactive strict la nivelul de agregare demonstrează o aplicare controlată a principiului de minimizare a complexității accidentale.

### 4.3.7 Analiza arhitecturală a amplasării agregării de date

Arhitectura platformei prezintă două instanțe de agregare complexă, plasate deliberat în niveluri arhitecturale diferite:

| **Criteriu de evaluare** | **Tipar BFF în API *Gateway*** | **Agregarea locală în `programari-service`** |
|:---|:---|:---|
| **Profil de execuție** | Asincron, non-blocant, concurent | Secvențial, blocant, intensiv pe baza de date |
| **Sfera interogărilor** | Determinată, număr fix de apeluri (max. 5) | Variabilă, proporțională cu volumul datelor clinice |
| **Aria de acoperire** | 3 microservicii distincte decuplate | 4 contexte tranzacționale aparținând aceluiași domeniu |
| **Problema N+1 interogări** | Rezolvată prin interogări de tip *batch* | Prezentă, gestionată prin *fallback* defensiv per apel |
| **Justificare arhitecturală** | Consolidarea datelor disparate pentru prezentarea vizuală | Necesitatea garantării consistenței locale a istoricului clinic |

Construirea dosarului clinic complet a fost menținută în cadrul `programari-service` (în loc să fie mutată în *Gateway*) din rațiuni de coeziune a datelor. Accesul direct la baza de date locală este esențial pentru eficiența extragerii evaluărilor și a notelor de evoluție. Preluarea individuală a acestora prin conexiuni HTTP în interiorul *Gateway*-ului ar fi transformat o problemă de interogare internă (*N+1* local) într-un fenomen distructiv de tip *N+1* distribuit la nivel de rețea, cu impact sever asupra performanței platformei.

### 4.3.8 Reprezentarea vizuală a agregării BFF

Diagrama de secvență de mai jos ilustrează procesul de orchestrare reactivă (tiparul *Scatter-Gather*) executat de API *Gateway* pentru generarea tabloului de bord, evidențiind capacitatea sa de a abstractiza complexitatea microserviciilor față de aplicația client.

```mermaid
sequenceDiagram
    autonumber
    participant Client as React SPA (Browser)
    participant GW as API Gateway (WebFlux / BFF)
    participant US as user-service
    participant PS as pacienti-service
    participant PR as programari-service

    Client->>GW: GET /api/homepage (cu JWT in-memory)

    rect rgb(232, 245, 233)
        Note over GW,PS: Faza 1: Construirea Profilului Reactiv
        par Cereri Concurente (Mono.zip)
            GW->>US: GET /users/by-keycloak/{id}
            GW->>PS: GET /pacient/by-keycloak/{id}
        end
        US-->>GW: Răspuns: {Nume, Prenume, Rol, Email...}
        PS-->>GW: Răspuns: {LocatiePreferataId, Date Medicale...}
        GW->>GW: Consolidare obiect Profil
    end

    rect rgb(227, 242, 253)
        Note over GW,PR: Faza 2: Tiparul Scatter-Gather (Mono.zip)
        par Cereri Concomitente (Mono.zip)
            GW->>PR: GET /programari/pacient/by-keycloak/{id}/next
            GW->>PR: GET /programari/pacient/by-keycloak/{id}/situatie
        end

        PR-->>GW: Răspuns: {Următoarea ședință}
        PR-->>GW: Răspuns: {Diagnostic, Progres Plan Terapeutic}
    end

    Note over GW: Fuzionare finală asincronă în map-ul reactiv
    GW-->>Client: HTTP 200 OK: {Profil, urmatoareaProgramare, situatie}
```
