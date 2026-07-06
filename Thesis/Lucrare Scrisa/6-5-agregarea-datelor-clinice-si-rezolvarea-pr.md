## 6.5 Agregarea Datelor Clinice și Rezolvarea Problemei N+1 Inter-servicii

Această secțiune investigează arhitectura de agregare a dosarului clinic integrat al pacientului și tratează problema clasică *N+1 Query Problem* propagată la nivelul rețelei în sistemele distribuite. Sunt prezentate strategiile de degradare grațioasă implementate pentru a asigura disponibilitatea serviciilor critice și este realizată o comparație cu agregarea reactivă din marginea sistemului.

### 6.5.1 Contextul: Valoarea clinică a dosarului integrat (*Fișa Pacientului*)

În cadrul platformei KinetoCare, interfața dedicată terapeuților, denumită *Fișa Pacientului*, reprezintă pilonul central al procesului de monitorizare clinică. Platforma este proiectată pentru a oferi o perspectivă clinică integrată, eliminând necesitatea navigării între ecrane multiple.

Anterior începerii unei ședințe fizice, terapeutul trebuie să vizualizeze instantaneu un tablou de date complet: identitate din `user-service`, date medicale extinse din `pacienti-service`, istoricul evaluărilor clinice, notele de evoluție și istoricul programărilor din baza locală a `programari-service`, precum și jurnalul subiectiv al pacientului din `pacienti-service` — în total trei microservicii externe și baza de date locală a componentei `programari-service`.

Această agregare masivă este orchestrată de metoda `FisaPacientService.getFisaPacient()` din microserviciul `programari-service`. Din punct de vedere al complexității arhitecturale, această componentă acționează ca un orchestrator centralizat care interogează sincron trei microservicii externe și realizează interogări locale complexe în baza de date.

### 6.5.2 Harta completă a orchestrării inter-servicii

Construirea dosarului clinic consolidat se realizează printr-o succesiune coordonată de pași, descrisă în tabelul de mai jos:

| Etapă | Sursă date | Informații colectate | Mecanism tehnic |
|:---|:---|:---|:---|
| **Pasul 1** | `user-service` | Identitate de bază (nume, prenume, telefon, e-mail, gen) | Apel sincron *OpenFeign* (HTTP `GET`) |
| **Pasul 2** | `pacienti-service` | Profil medical (data nașterii, detalii sport) | Apel sincron *OpenFeign* (HTTP `GET`) |
| **Pasul 3** | Baza locală | Diagnostic activ, contorizarea bugetului de ședințe | Interogare JPA locală |
| **Pasul 4** | Baza locală | Istoricul tuturor evaluărilor clinice persistate | Interogare JPA locală |
| **Pasul 4a** | `user-service` | Numele complet al terapeutului care a semnat fiecare evaluare | Apel *OpenFeign* per evaluare (N ori) |
| **Pasul 4b** | `servicii-service` | Denumirea serviciului clinic recomandat în evaluare | Apel *OpenFeign* per evaluare (N ori) |
| **Pasul 5** | Baza locală | Notele de evoluție scrise de kinetoterapeuți | Interogare JPA locală |
| **Pasul 6** | Baza locală | Istoricul complet al programărilor trecute/viitoare | Interogare JPA locală |
| **Pasul 7** | `pacienti-service` | Jurnalul subiectiv de durere completat de pacient | Apel sincron *OpenFeign* (HTTP `GET`) |

### 6.5.3 Manifestarea problemei N+1 la nivel inter-servicii și optimizarea implementată

Problema *N+1 Query Problem* reprezintă un *anti-pattern* arhitectural clasic în sistemele cu baze de date relaționale. Se manifestă atunci când sistemul execută o interogare principală care returnează N rânduri (de exemplu, o listă de evaluări), iar apoi, pentru fiecare dintre aceste N rânduri, este lansată o nouă interogare suplimentară pentru a extrage detalii (de exemplu, numele terapeutului), rezultând 1 + N interogări în loc de una singură optimizată.

În arhitecturile bazate pe microservicii, acest fenomen se propagă din stratul de baze de date în stratul de comunicare prin rețea, cu consecințe severe asupra performanței. Fiecare apel extern HTTP suplimentar introduce latență de rețea, negocieri de conexiune TCP/TLS, serializare și deserializare JSON și consum de fire de execuție în componentele destinatare.

În cadrul platformei KinetoCare, problema se manifesta la procesarea istoricului de evaluări din metoda ajutătoare `buildEvaluariList`:

```java
// Pentru fiecare evaluare din istoricul local (N in total), sistemul apela:
UserDisplayCalendarDTO terapeutDetails = userClient.getUserByKeycloakId(eval.getTerapeutKeycloakId());
DetaliiServiciuDTO serviciu = serviciiClient.getServiciuById(eval.getServiciuRecomandatId());
```

**Soluția de optimizare prin procesare în lot (*Batch Processing*):**
Latența redundantă este eliminată prin introducerea unui *endpoint* de tip *batch* în `user-service`: `@PostMapping("/users/batch")`. În `FisaPacientService.buildEvaluariList()`, sunt colectate mai întâi toate identificatorii unici ai terapeuților din cele N evaluări, efectuându-se o singură interogare colectivă:

```java
List<UserDisplayCalendarDTO> users = userClient.getUsersByKeycloakIds(terapeutKeycloakIds);
```

Această optimizare reduce numărul de interogări pentru terapeuți de la N apeluri la **1 singur apel de rețea**, rezultatele fiind mapate local într-un *hash map* în memorie pentru un *lookup* instantaneu.

**Volumul curent de apeluri externe:**
Pentru serviciile recomandate, apelul individual pe rețea în buclă este menținut ca un compromis asumat (serviciile sunt rar modificate și adesea redundante). Numărul total de apeluri prin rețea efectuate de server pentru a construi o singură fișă de pacient este:

$$\text{Total apeluri externe} = 4 + N$$

Unde:
- **4** reprezintă apelurile fixe (identitatea din `user-service`, profilul medical din `pacienti-service`, istoricul jurnalului din `pacienti-service` și apelul *batch* colectiv de utilizatori).
- **N** reprezintă cele N apeluri individuale către `servicii-service` pentru detaliile fiecărui serviciu recomandat.

Pentru un pacient al clinicii care a adunat un istoric de **5 evaluări** pe parcursul a 6 luni de tratament, numărul de apeluri de rețea este redus la $4 + 5 = 9$, față de cele 13 apeluri anterioare optimizării.

### 6.5.4 Strategia defensivă de degradare grațioasă (*Graceful Degradation*)

În medii distribuite, defecțiunile de rețea sau de infrastructură sunt inevitabile. Deoarece agregarea fișei de pacient depinde sincron de trei microservicii externe, indisponibilitatea temporară a oricăruia dintre acestea (cauzată de o cădere de rețea, un *restart* pentru punere în producție sau o pauză prelungită pentru colectarea deșeurilor de memorie — *Garbage Collection*) ar putea determina eșecul întregii solicitări, cu o eroare de tip *500 Internal Server Error*.

În domeniul medical, un astfel de comportament este inacceptabil. Un terapeut aflat în fața unui pacient în sala de tratament are nevoie critică de istoricul medical (note clinice, diagnostice), chiar dacă sistemul nu poate afișa temporar numele exact al terapeutului care a semnat o evaluare trecută.

Această problemă este adresată prin implementarea modelului de **degradare grațioasă** (*graceful degradation*). Fiecare apel extern *OpenFeign* din bucla N+1 este izolat și protejat de blocuri defensive `try/catch` independente:

```java
String numeTerapeut = null;
UserDisplayCalendarDTO terapeutDetails = terapeutiMap.get(eval.getTerapeutKeycloakId());
if (terapeutDetails != null) {
    numeTerapeut = terapeutDetails.nume() + " " + terapeutDetails.prenume();
} else {
    // Fallback: utilizeaza identificatorul brut stocat local
    log.warn("Serviciul user-service este temporar indisponibil pentru KeycloakID: {}. Fallback la ID.", eval.getTerapeutKeycloakId());
    numeTerapeut = "Terapeut (ID: " + eval.getTerapeutKeycloakId() + ")";
}
```

Prin această abordare:
- Dacă `user-service` suferă o întrerupere, fișa pacientului se încarcă în continuare cu succes.
- În secțiunea de evaluări, în loc de întreruperea execuției, numele terapeutului evaluator este afișat sub formă de *fallback*, utilizând identificatorul brut stocat local.
- Terapeutul curent poate accesa notele clinice, diagnosticul și recomandările, prioritizând actul medical în fața detaliilor secundare de interfață.

### 6.5.5 Compromisul arhitectural asumat și optimizarea teoretică

Manifestarea problemei N+1 inter-servicii pentru serviciile recomandate reprezintă o **limitare arhitecturală recunoscută și asumată** în proiectarea curentă a platformei KinetoCare, bazată pe decizii pragmatice. Această limitare este catalogată în Capitolul 9 alături de soluțiile arhitecturale propuse pentru scalarea platformei.

**Acceptabilitatea compromisului curent.** În contextul clinic real al unui cabinet de kinetoterapie, un pacient nu acumulează un istoric extins de evaluări. O evaluare se efectuează la începutul tratamentului (de obicei o dată la câteva săptămâni sau luni) și la finalul acestuia (reevaluare).

Prin urmare, valoarea lui N depășește rar cifra de 3 sau 4 în decursul unui an. Latența suplimentară introdusă de câteva apeluri HTTP sincrone în rețeaua internă este redusă (sub 30–50 milisecunde), iar impactul de performanță perceput de terapeuți este minim.

### 6.5.6 Comparație critică: Agregarea din *Gateway* (BFF) vs. *Fișa Pacientului*

Tabelul următor compară agregarea din `FisaPacientService` cu controlorul reactiv de agregare a profilului aflat în API *Gateway* (`ProfileService.getProfile()`):

| Dimensiune tehnică | Agregare reactivă *BFF* (*Gateway*) | Agregare sincronă clinică (*Fișa Pacientului*) |
|:---|:---|:---|
| **Locație în stivă** | API *Gateway* (*Edge Layer*) | `programari-service` (*Core Service Layer*) |
| **Model concurență** | Reactiv, non-blocant (*Spring WebFlux*) | Sincron, blocant (*Spring MVC* + *OpenFeign*) |
| **Volum apeluri externe** | Fix (maximum 5 apeluri paralele prin `Mono.zip`) | Variabil ($4 + N$ în funcție de numărul de evaluări) |
| **Bază de date locală** | Fără acces la baza de date locală | Interogări JPA complexe (diagnostice, note) |
| **Justificare tehnologică** | Frecvență ridicată de acces, latență minimă necesară | Frecvență redusă de acces, complexitate clinică ridicată |

Această asimetrie reflectă aplicarea principiilor de proiectare pragmatică, în care evitarea complexității accidentale în zonele cu trafic redus primează în fața optimizărilor premature, facilitând mentenabilitatea codului sursă.
