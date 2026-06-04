## 5.5 Decizii de tipizare și constrângeri la nivel de schemă

Alegerea riguroasă a tipurilor de date la nivelul schemelor fizice MySQL reprezintă o etapă esențială în prevenirea anomaliilor de stocare, conservarea spațiului pe disc și menținerea unor timpi stabili de căutare în indecși.

### 5.5.1 Managementul UUID-urilor și corelarea cu sistemele IAM

În cadrul platformei **KinetoCare**, corelarea utilizatorilor între baze de date diferite se bazează pe identificatorul unic generat de Keycloak. Coloanele ce stochează acest identificator (ex: `keycloak_id`, `pacient_keycloak_id`, `terapeut_keycloak_id`) au fost configurate cu tipul de date **`VARCHAR(36)`**.

#### Justificarea alegerii și analiza compromisurilor:
1. **VARCHAR(36) vs. TEXT:** Tipul `TEXT` în MySQL este stocat în afara paginilor normale de date (*off-page storage*) dacă depășește o anumită dimensiune, iar indecșii pe coloane `TEXT` necesită specificarea unei lungimi de prefix fixe, împiedicând validarea unicității stricte la nivel de motor de baze de date. `VARCHAR(36)` alocă spațiul direct în cadrul paginii B-Tree, permițând căutări binare instantanee și aplicarea constrângerilor native `UNIQUE`.
2. **VARCHAR(36) vs. BINARY(16):** Din punct de vedere matematic, un UUID este un număr de 128 de biți și ar putea fi stocat mult mai eficient într-o coloană de tipul `BINARY(16)`. Stocarea sub formă binară reduce dimensiunea indexului la jumătate și sporește viteza de sortare. Cu toate acestea, s-a optat pentru `VARCHAR(36)` (reprezentarea textuală standard de 36 de caractere, de tipul `8-4-4-4-12`) ca un **compromis conștient în favoarea mentenabilității**, recunoscând că această decizie constituie o datorie tehnică asumată (*technical debt*):
   * Elimină *overhead*-ul de conversie din codul Spring Java (unde UUID-ul Keycloak este manipulat ca `String`).
   * Permite depanarea (*debugging*) directă și interogarea facilă a tabelelor prin CLI sau clienți SQL fără a necesita conversii hexazecimale manuale (`HEX()` / `UNHEX()`), simplificând substanțial mentenanța de lungă durată.

*Notă tehnică și datorie tehnică asumată:* Fragmentarea arborelui B+Tree cauzată de UUIDv4 (generate aleatoriu, fără monotonie cronologică) afectează în mod egal ambele forme de stocare: atât `VARCHAR(36)`, cât și `BINARY(16)`. MySQL 8.0 pune la dispoziție funcția `UUID_TO_BIN(uuid, 1)` cu parametrul `swap_flag=1`, care reordonează octeții componentei de timp, producând identificatori cu prefix monoton și reducând fragmentarea pentru `BINARY(16)`. Alegerea `VARCHAR(36)` în această platformă este justificată exclusiv prin **mentenabilitate și simplitate operațională**, nu printr-o superioritate de performanță față de stocarea binară. În eventualitatea unei scalări masive a volumului de date, migrarea către stocarea de tip `BINARY(16)` ar deveni necesară pentru a reduce amprenta de memorie ocupată de indecși și a limita impactul I/O.

---

### 5.5.2 Precizia financiară și controlul erorilor de rotunjire

Pentru stocarea tarifelor ședințelor și prețurilor serviciilor clinice (coloana `pret` în `servicii_db.servicii` și `programari_db.programari`), s-a impus utilizarea tipului de date **`DECIMAL(10,2)`** (reprezentare exactă cu 10 cifre din care 2 zecimale).

#### Evitarea reprezentării în virgulă mobilă (IEEE 754):
Utilizarea tipurilor de date precum `FLOAT` sau `DOUBLE` introduce reprezentări binare aproximative ale numerelor reale conform standardului internațional **IEEE 754**. Deoarece fracțiile zecimale comune (cum ar fi `0.1` sau `0.15`) nu pot fi reprezentate exact ca sume de puteri negative ale lui 2, calculele financiare repetitive (ex: însumarea veniturilor unei locații din mii de programări) ar acumula erori de precizie zecimală (rezultând valori de tipul `150.000000000004` în loc de `150.00`). 
`DECIMAL` este stocat de MySQL sub formă de șiruri binare compactate ce simulează aritmetica în bază 10, garantând precizia matematică absolută la nivel zecimal, vitală pentru integritatea contabilă a clinicii.

---

### 5.5.3 Controlul stărilor prin tipul ENUM la nivel de motor SQL

Stările programărilor, rolurile de acces ale utilizatorilor și categoriile de evaluări sunt limitate la un set fix și stabil de valori logice. Pentru persistența acestora s-a folosit tipul de date **`ENUM`** (ex: `role enum('ADMIN','PACIENT','TERAPEUT')` sau `status enum('PROGRAMATA','FINALIZATA','ANULATA')`).

#### Avantaje față de tabelele de lookup și limitări asumate:
1. **Validarea la nivel de schemă:** MySQL InnoDB respinge la scriere orice valoare din afara setului declarat, prevenind alterarea stărilor din cauza unor erori logice din codul de *backend* Spring.
2. **Optimizarea spațiului și a memoriei:** Intern, MySQL stochează valorile `ENUM` ca numere întregi mici pe 1 sau 2 octeți, reducând drastic dimensiunea înregistrărilor comparativ cu stocarea repetată a textelor sub formă de `VARCHAR`.
3. **Eliminarea operațiunilor de tip JOIN:** Alternativa clasică ar fi presupus crearea unor tabele de lookup (de exemplu, o tabelă `statusuri` legată prin cheie străină). Într-un sistem de microservicii predominant interogat la citire, această abordare ar fi adăugat operațiuni de `JOIN` în cadrul interogărilor locale din `programari-service` pentru a rezolva denumirea statusului. Utilizarea `ENUM` permite stocarea compactă și afișarea directă a textului asociat stării, optimizând viteza de execuție a bazelor de date.

**Limitare asumată:** Tipul `ENUM` presupune că setul de valori este **stabil și închis prin proiectare** (*closed-domain*). Adăugarea unui nou statut aplicativ (ex: `'REPROGRAMATA'`) necesită un `ALTER TABLE`, o operație DDL care — chiar beneficiind de suportul *Online DDL* al motorului InnoDB din MySQL 8.0 — poate genera blocaje tranzitorii pe tabele cu milioane de rânduri în producție. Alegerea `ENUM` este justificată tocmai de natura *closed-domain* a stărilor reprezentate: tranzacțile de stare ale unui flux de programare clinică sunt finite și definite structural prin logica de business, nu prin configurare dinamică.
