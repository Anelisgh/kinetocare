## 6.5 Agregarea Datelor Clinice și Rezolvarea Problemei N+1 Inter-servicii

Această secțiune investigează arhitectura de agregare a dosarului clinic integrat al pacientului și tratează problema clasică *N+1 Query Problem* propagată la nivelul rețelei în sistemele distribuite. Sunt prezentate strategiile de degradare grațioasă implementate pentru a asigura disponibilitatea serviciilor critice și se realizează o comparație cu agregarea reactivă din marginea sistemului.

### 6.5.1 Contextul: Valoarea clinică a dosarului integrat (Fișa Pacientului)

În cadrul platformei **KinetoCare**, interfața dedicată terapeutilor, denumită *Fișa Pacientului*, reprezintă pilonul central al procesului de monitorizare clinică. KinetoCare este proiectat pentru a oferi o perspectivă clinică integrată, eliminând necesitatea navigării între ecrane multiple.

Un kinetoterapeut are nevoie să vizualizeze instantaneu un tablou complet înainte de începerea ședinței fizice. Această vedere integrată reunește date de identitate din `user-service`, date medicale extinse din `pacienti-service`, istoricul evaluărilor clinice, notele de evoluție și istoricul programărilor din baza locală a `programari-service`, precum și jurnalul subiectiv al pacientului din `pacienti-service` — în total trei microservicii externe (user-service, pacienti-service, servicii-service) și baza de date locală a microserviciului programari-service.

Această agregare masivă este orchestrată de metoda `FisaPacientService.getFisaPacient()` din microserviciul `programari-service`. Din punct de vedere al complexității arhitecturale, acest serviciu acționează ca un orchestrator centralizat care interoghează sincron trei microservicii externe diferite și realizează interogări locale complexe în baza de date.

### 6.5.2 Harta completă a orchestrării inter-servicii

Pentru a construi acest dosar clinic consolidat, sistemul execută o succesiune coordonată de pași, descrisă în tabelul de mai jos:

| Etapă | Sursă date | Informații colectate | Mecanism tehnic |
| :--- | :--- | :--- | :--- |
| **Pasul 1** | `user-service` | Identitate de bază (nume, prenume, telefon, e-mail, gen). | Apel sincron OpenFeign (HTTP `GET`) |
| **Pasul 2** | `pacienti-service` | Profil medical (data nașterii, detalii sport, dacă face sport). | Apel sincron OpenFeign (HTTP `GET`) |
| **Pasul 3** | Baza locală | Diagnostic activ, contorizarea bugetului de ședințe. | Interogare JPA locală |
| **Pasul 4** | Baza locală | Istoricul tuturor evaluărilor clinice persistate. | Interogare JPA locală |
| **Pasul 4a** | `user-service` | Numele complet al terapeutului care a semnat fiecare evaluare. | Apel OpenFeign per evaluare (N ori) |
| **Pasul 4b** | `servicii-service` | Denumirea serviciului clinic recomandat în evaluare. | Apel OpenFeign per evaluare (N ori) |
| **Pasul 5** | Baza locală | Notele de evoluție scrise de kinetoterapeuți. | Apel JPA local |
| **Pasul 6** | Baza locală | Istoricul complet al programărilor trecute/viitoare. | Apel JPA local |
| **Pasul 7** | `pacienti-service` | Jurnalul subiectiv de durere completat de pacient. | Apel sincron OpenFeign (HTTP `GET`) |

### 6.5.3 Manifestarea problemei N+1 la nivel inter-servicii

Problema *N+1 Query Problem* reprezintă un *anti-pattern* arhitectural clasic în sistemele cu baze de date relaționale. Aceasta se manifestă atunci când sistemul execută o interogare principală care returnează N rânduri (de exemplu, o listă de evaluări), iar apoi, pentru fiecare dintre aceste N rânduri, rulează o nouă interogare suplimentară pentru a extrage detalii (de exemplu, numele terapeutului), rezultând un total de 1 + N interogări în loc de o singură interogare optimizată cu clauze de tip `JOIN`.

În arhitecturile bazate pe microservicii, acest fenomen se propagă din stratul de bază de date în stratul de comunicare prin rețea, având consecințe severe asupra performanței. Fiecare apel extern HTTP suplimentar introduce latență de rețea, negocieri de conexiune TCP/TLS, serializare și deserializare JSON pe ambele capete și consum de fire de execuție în serverul de aplicații destinatar.

În cadrul platformei KinetoCare, problema N+1 se manifestă în momentul procesării istoricului de evaluări din metoda ajutătoare `buildEvaluariList`:

```java
// Pentru fiecare evaluare din istoricul local (N in total), sistemul apeleaza:
UserDisplayCalendarDTO terapeutDetails = userClient.getUserByKeycloakId(eval.getTerapeutKeycloakId());
DetaliiServiciuDTO serviciu = serviciiClient.getServiciuById(eval.getServiciuRecomandatId());
```

**Calculul volumului de apeluri externe.** Volumul total de apeluri prin rețea (*OpenFeign* client) efectuate de server pentru a construi o singură fișă de pacient poate fi calculat prin formula:

$$\text{Total apeluri externe} = 3 + 2 \times N$$

Unde:
- **3** reprezintă apelurile fixe, independente de istoricul clinic (identitatea din `user-service`, profilul medical din `pacienti-service` și istoricul jurnalului din `pacienti-service`).
- **2 × N** reprezintă apelurile variabile (pentru fiecare din cele N evaluări înregistrate, se face 1 apel la `user-service` pentru a afla numele terapeutului și 1 apel la `servicii-service` pentru a afla denumirea serviciului recomandat).

Pentru un pacient al clinicii care a adunat un istoric de **5 evaluări** pe parcursul a 6 luni de tratament, numărul de apeluri de rețea efectuate de server la o singură solicitare este:

$$3 + (2 \times 5) = 13 \text{ apeluri HTTP}$$

Această dependență directă a performanței rețelei de volumul de date clinice istorice reprezintă o constrângere de proiectare în sistemele distribuite.

### 6.5.4 Strategia defensivă de degradare grațioasă (Graceful Degradation)

În medii distribuite, principiul fundamental de securitate și reziliență afirmă că defecțiunile de rețea sau de infrastructură sunt inevitabile. Deoarece agregarea fișei de pacient depinde în mod sincron de trei microservicii externe, indisponibilitatea temporară a oricăruia dintre acestea (cauzată de o cădere de rețea, un restart pentru punere în producție sau o pauză prelungită pentru colectarea deșeurilor de memorie — *Garbage Collection*) ar putea determina eșecul întregii solicitări, soldat cu o eroare *500 Internal Server Error*.

În domeniul medical, un astfel de comportament este inacceptabil. Un terapeut aflat în fața unui pacient în sala de tratament are nevoie critică de istoricul medical (note clinice, diagnostice), chiar dacă sistemul nu poate afișa temporar numele exact al terapeutului care a semnat o evaluare trecută.

KinetoCare abordează această problemă prin implementarea modelului de **degradare grațioasă** (*graceful degradation*). Fiecare apel extern *OpenFeign* din bucla N+1 este izolat și protejat de blocuri defensive `try/catch` independente:

```java
String numeTerapeut = null;
try {
    UserDisplayCalendarDTO terapeutDetails = userClient.getUserByKeycloakId(eval.getTerapeutKeycloakId());
    numeTerapeut = terapeutDetails.nume() + " " + terapeutDetails.prenume();
} catch (Exception e) {
    // Fallback: utilizeaza identificatorul brut stocat local
    log.warn("Serviciul user-service este indisponibil temporar pentru KeycloakID: {}. Fallback la ID.", eval.getTerapeutKeycloakId());
    numeTerapeut = "Terapeut (ID: " + eval.getTerapeutKeycloakId() + ")";
}
```

Prin această abordare:
- Dacă `user-service` suferă o întrerupere, fișa pacientului se va încărca în continuare cu succes.
- În secțiunea de evaluări, în loc ca aplicația să întrerupă execuția, numele terapeutului evaluator va fi afișat sub formă de *fallback*, utilizând identificatorul său brut stocat local.
- Terapeutul curent poate accesa notele clinice, diagnosticul și recomandările, prioritizând actul medical în fața detaliilor secundare de interfață.

### 6.5.5 Compromisul arhitectural asumat și optimizarea teoretică

Proiectarea sistemelor software presupune gestionarea constantă a compromisurilor. Manifestarea problemei N+1 inter-servicii este o **limitare arhitecturală recunoscută și asumată** în designul curent al platformei KinetoCare, bazată pe decizii pragmatice. Această limitare este catalogată în capitolul 9 alături de soluțiile arhitecturale propuse pentru scalarea platformei la volume mari de date clinice.

**De ce este acceptabil compromisul curent.** În contextul clinic real al unui cabinet de kinetoterapie, un pacient nu acumulează un istoric extins de evaluări. O evaluare se efectuează la începutul tratamentului (de obicei o dată la câteva săptămâni sau luni) și la finalul acestuia (reevaluare).

Prin urmare, valoarea lui N depășește rar cifra de 3 sau 4 în decursul unui an. Latența suplimentară introdusă de cele câteva apeluri HTTP sincrone în rețeaua internă rapidă a clinicii este redusă (sub 50-80 milisecunde), făcând ca impactul de performanță să fie minim pentru terapeuți.

**Cum s-ar optimiza la scară largă.** Dacă platforma ar scala la nivel național, deservind zeci de mii de pacienți cu istorice clinice extinse pe parcursul multor ani, această limitare ar deveni un blocaj de performanță. Optimizarea tehnică ar presupune utilizarea unui **endpoint de procesare în lot** (*batch processing*) în modulul de personal.

În loc ca sistemul să facă N apeluri succesive în buclă, procesul ar fi structurat în trei pași:

1. Colectarea tuturor identificatorilor unici de terapeuți din lista locală de evaluări.
2. Lansează un singur apel bulk către microserviciul de utilizatori:
```json
POST /api/users/batch
{
  "keycloakIds": ["uuid-1", "uuid-2", "uuid-3"]
}
```
3. Serviciul `user-service` ar returna o listă consolidată cu numele tuturor terapeuților într-o singură tranzacție, conform tiparului de procesare în lot.

### 6.5.6 Comparație critică: Agregarea din *Gateway* (BFF) vs. Fișa Pacientului

Pentru a înțelege mai bine deciziile inginerești din spatele KinetoCare, tabelul următor compară agregarea din `FisaPacientService` cu controller-ul reactiv de agregare a profilului aflat în API *Gateway* (`ProfileService.getProfile()`):

| Dimensiune tehnică | Agregare reactivă BFF (*Gateway*) | Agregare sincronă clinică (Fișa Pacientului) |
| :--- | :--- | :--- |
| **Locație în stivă** | API *Gateway* (Edge Layer) | `programari-service` (Core Service Layer) |
| **Model concurență** | Reactiv, non-blocant (*Spring WebFlux*) | Sincron, blocant (*Spring MVC* + *OpenFeign*) |
| **Volum apeluri externe** | Fix (maximum 5 apeluri paralele prin `Mono.zip`) | Variabil ($3 + 2N$ în funcție de numărul de evaluări) |
| **Bază de date locală** | Fără acces la baza de date locală | Interogări JPA complexe concomitente (diagnostice, note) |
| **Justificare tehnologică** | Frecvență ridicată de acces, latență minimă necesară. | Frecvență redusă de acces, complexitate clinică ridicată. |

Această asimetrie reflectă aplicarea principiilor de proiectare pragmatică, unde evitarea complexității accidentale în zonele cu trafic redus primează în fața optimizărilor premature, facilitând mentenabilitatea codului sursă.
