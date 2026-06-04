## 5.4 Strategia de indexare și optimizarea interogărilor critice

Performanța sistemelor distribuite este direct limitată de latența operațiunilor de persistență. În platforma **KinetoCare**, unde fiecare microserviciu operează pe o bază de date proprie, utilizarea corectă a **indecșilor secundari** reprezintă principala metodă de optimizare, prevenind congestionarea conexiunilor la nivel de rețea. 

### 5.4.1 Indexuri pentru prevenirea double-booking

În cadrul `programari_db`, tabela `programari` este supusă unor interogări intense de verificare a disponibilității terapeutice. Pentru a asigura corectitudinea algoritmului de generare a sloturilor (Secțiunea 6.2) și a preveni dubla rezervare concurentă, se execută interogarea critică `existaSuprapunere`:

```sql
SELECT COUNT(*) FROM programari p
WHERE p.terapeut_keycloak_id = :terapeutKeycloakId
  AND p.data = :data
  AND p.status = 'PROGRAMATA'
  AND p.ora_inceput < :oraSfarsitNoua
  AND p.ora_sfarsit > :oraInceputNoua;
```

Pentru a menține latența interogării de verificare în limite acceptabile în producție și a preveni degradarea sa liniară sub sarcină concurentă, pe tabelă s-a definit indexul compus:
`KEY idx_prog_overlap (terapeut_keycloak_id, data, ora_inceput, ora_sfarsit)`

#### Funcționarea sub structura B+Tree și Regula Prefixului Stâng

MySQL InnoDB implementează indecșii sub formă de arbori căutați echilibrați de tip **B-Tree** (mai precis, B+Tree). Într-un index compus (multi-coloană), ordinea coloanelor în definiție este vitală din cauza **regulii prefixului stâng** (*leftmost prefix rule*). Motorul de interogare poate folosi indexul doar dacă criteriile din clauza `WHERE` acoperă coloanele indexului în ordinea strictă de la stânga la dreapta, fără goluri.

În cazul `idx_prog_overlap`:
1. Prima coloană, `terapeut_keycloak_id`, permite motorului SQL să izoleze instantaneu sub-arborele ce aparține exclusiv terapeutului vizat (reducând spațiul de căutare de la zeci de mii de rânduri la maximum câteva sute).
2. A doua coloană, `data`, restrânge căutarea în nodurile imediat următoare la ziua calendaristică solicitată.
3. Coloanele `ora_inceput` și `ora_sfarsit` permit motorului să execute o scanare pe un interval restrâns de index (*index range scan*) pentru a evalua condițiile de suprapunere orară (`<` și `>`).

Fără acest index compus configurat pe regula prefixului stâng, motorul SQL ar fi obligat să execute o **scanare completă de tabelă** (*full table scan*), încărcând în memoria RAM toate programările clinicii pentru a găsi suprapunerile, un comportament inacceptabil în producție.

---

### 5.4.2 Indexuri compuse pentru raportare statistică

Panoul de administrare (BFF / Dashboard) agregă date financiare și operaționale masive la nivel de locație clinică. Interogarea de raportare rulează pe tabela `programari`:

```sql
SELECT SUM(pret) FROM programari 
WHERE locatie_id = :locatieId AND data BETWEEN :start AND :end;
```

Pentru a accelera aceste agregări fără a citi rândurile din tabela fizică, s-a declarat indexul:
`KEY idx_prog_stats (locatie_id, data, pret)`

Prin includerea coloanei `pret` în definiția indexului, acesta devine un **index de acoperire** (*covering index*) pentru interogarea de raportare. Un covering index conține toate coloanele accesate de o interogare — atât filtrele din clauza `WHERE`, cât și valorile solicitate în `SELECT`; prin urmare, motorul de optimizare MySQL poate calcula suma veniturilor citind exclusiv paginile de index din *buffer pool* (RAM), ocolind complet accesarea tabelei fizice de pe disc. Deși adăugarea de noi programări implică scurte operațiuni suplimentare de reechilibrare a arborelui indexului în fundal, volumul zilnic redus de scrieri dintr-o clinică justifică pe deplin acest mic compromis în favoarea performanței instantanee a raportărilor administrative.

---

### 5.4.3 Indexuri pentru latența paginilor de inbox

În microserviciul `chat-service`, extragerea listei de mesaje dintr-o conversație reprezintă cea mai frecventă interogare. Aceasta necesită paginare și sortare descrescătoare după timpul trimiterii:

```sql
SELECT * FROM mesaje 
WHERE conversatie_id = :conversatieId 
ORDER BY trimis_la DESC 
LIMIT :size;
```

Pentru a optimiza latența paginii de inbox, s-a creat indexul compus:
`KEY idx_mesaj_conv_list (conversatie_id, trimis_la)`

Acest index permite motorului InnoDB să rezolve două operații costisitoare într-o singură trecere:
1. **Filtrarea** după `conversatie_id` prin localizarea directă a punctului de start în index.
2. **Sortarea** implicită. Deoarece arborele B+Tree păstrează valorile ordonate, motorul extrage elementele direct în ordinea cronologică stocată, evitând operația de sortare post-fetch (*filesort*) care devine dominantă ca timp de execuție pe seturi de rezultate de dimensiuni semnificative.
