## 7.3 Panoul de Administrare și Agregarea Datelor Statistice

Această secțiune descrie arhitectura și logica din spatele panoului administrativ al platformei KinetoCare. Sunt analizate mecanismele de securitate bazate pe controlul accesului bazat pe roluri (*Role-Based Access Control* - *RBAC*), implementarea rezilienței la pornirea sistemului, conservarea istorică a datelor prin tiparul *Snapshot* și tehnicile avansate de optimizare a randării interfeței utilizate în agregarea datelor de *Business Intelligence*.

### 7.3.1 Securitatea rolului de administrator și reziliența provizionării

Contul de administrator reprezintă autoritatea de guvernanță operațională și comercială a platformei KinetoCare. Din rațiuni de securitate, acest rol este complet izolat: sistemul nu permite auto-înregistrarea cu privilegii administrative, iar interfața de guvernanță React este protejată de o componentă de protecție a rutelor (*route guard*) care validează strict prezența rolului `ROLE_ADMIN` în secțiunea de revendicări (*claims*) a jetonului *JWT*, blocând accesul persoanelor neautorizate.

Crearea primului cont administrativ (*bootstrap provisioning*) este un proces automatizat, executat la pornirea platformei de către o componentă dedicată din *backend* (`user-service`). Într-un ecosistem containerizat (Docker, Kubernetes), unde serviciile pornesc în paralel, microserviciul de identitate poate deveni activ înaintea serverului Keycloak. Pentru a garanta crearea contului, componenta implementează un mecanism defensiv de reîncercare cu interval fix (20 de tentative la intervale de 5 secunde), asigurând finalizarea cu succes a procesului de provizionare fără a declanșa o prăbușire a aplicației la inițializare.

### 7.3.2 Managementul locațiilor și principiul apărării stratificate

Modulul de gestiune a locațiilor clinice permite administratorului definirea și actualizarea punctelor de lucru. O decizie arhitecturală critică este tehnica de eliminare a unei locații: sistemul utilizează exclusiv **ștergerea logică** (*soft-delete*). În loc să execute comanda SQL `DELETE`, sistemul comută o stare booleană (`isActive = false`), prevenind astfel coruperea istoricului programărilor și încălcarea constrângerilor de integritate referențială din baza de date. Locațiile inactive sunt automat excluse din interogările de disponibilitate prezentate pacienților de către `programari-service`.

Modificarea acestor resurse este protejată prin principiul apărării stratificate (*Defense in Depth*):
* **La nivel de margine:** API *Gateway* blochează orice cerere de modificare care nu provine de la o sesiune autentificată cu rolul administrativ.
* **La nivel de serviciu:** Metodele din microserviciul destinație integrează propriile adnotări de securitate (`@PreAuthorize("hasRole('ADMIN')")`) pentru a respinge cererile neautorizate.

Această redundanță garantează că o eventuală configurare eronată a regulilor de rutare în *Gateway* nu va compromite securitatea datelor din rețeaua internă.

### 7.3.3 Catalogul de servicii clinice și aplicarea tiparului Snapshot

Interfața grafică dedicată catalogului de servicii clinice permite administratorului actualizarea nomenclatorului și a tarifelor. Modificările tarifare nu afectează integritatea rapoartelor contabile istorice, deoarece la nivel de *backend* este aplicat tiparul *Snapshot* (detaliat în Secțiunea 4.5.4), care conservă prețurile programărilor efectuate deja ca un instantaneu imutabil.

### 7.3.4 Panoul de Business Intelligence: Optimizarea performanței interfeței

Interfața de statistici reprezintă cel mai complex modul de agregare din aplicația client, necesitând randarea rapidă a multiplelor grafice evolutive (venituri, achiziție pacienți, distribuția serviciilor). Pentru a preveni degradarea severă a performanței, aplicația implementează două tehnici majore de optimizare la nivel de interfață grafică:

1. **Concurența cererilor HTTP:** La încărcarea ecranului, interfața lansează șase cereri HTTP simultane prin intermediul funcției `Promise.all` către endpoint-urile de agregare statistică din `programari-service`. Această abordare paralelă reduce timpul total de încărcare al paginii la durata celui mai lent apel individual, evitând cumularea latențelor de rețea specifice apelurilor secvențiale.
2. **Memoizarea algoritmică a structurilor de date:** Calcularea indicatorilor de performanță globali (KPIs) — precum generarea rapoartelor agregate din mii de înregistrări — consumă resurse computaționale semnificative pe firul de execuție principal (*main thread*) al browserului. Pentru a preveni fenomenul de înghețare a interfeței grafice (*UI freezing*) în timpul interacțiunilor minore, aplicația aplică tehnica memoizării native. Funcțiile de agregare matematică sunt încapsulate, iar rezultatele lor sunt păstrate în cache-ul memoriei client. Reevaluarea lor este strict interzisă de motorul de randare, exceptând cazurile în care se detectează o modificare a egalității referențiale (*reference equality*) a matricei datelor brute venite de la server.

Datele sunt ulterior transmise către biblioteca de grafice integrată în aplicația React (*Recharts*), care generează reprezentări vizuale de tip SVG (*Scalable Vector Graphics*) — format vectorial independent de rezoluție, ideal pentru dispozitivele cu densitate înaltă de pixeli. Spre deosebire de fluxurile pacienților și terapeuților, care utilizează comunicare în timp real prin *WebSocket* și mesagerie asincronă prin RabbitMQ, modulul administrativ operează exclusiv prin cereri HTTP sincrone REST.

### 7.3.5 Arhitectura panoului administrativ și topologia datelor

Diagrama de mai jos ilustrează modul în care acțiunile administrative din interfața de utilizator sunt asigurate, validate de stratul de margine și procesate de microserviciile corespunzătoare:

```mermaid
graph TD
    subgraph AdminPanel["Modul Administrare (Aplicație Client SPA)"]
        AL[Modul Locații Fizice]
        AS[Modul Servicii și Tarifare]
        AU[Modul Utilizatori și Suspendări]
        AST[Modul Business Intelligence]
    end

    subgraph Edge["Strat de Margine (API Gateway)"]
        RBAC["Validare Rol Administrativ<br/>Protecție la nivel de rută"]
    end

    subgraph Backend["Microservicii de Domeniu"]
        TS["terapeuti-service<br/>(terapeuti_db)"]
        SS["servicii-service<br/>(servicii_db)"]
        US["user-service<br/>(user_db + IAM)"]
        PS["programari-service<br/>(programari_db)"]
    end

    AL -->|Cereri securizate HTTP| RBAC
    AS -->|Cereri securizate HTTP| RBAC
    AU -->|Cereri securizate HTTP| RBAC
    AST -->|Interogări HTTP concurente| RBAC

    RBAC -->|Ștergere logică (*soft-delete*)| TS
    RBAC -->|Mutații de catalog| SS
    RBAC -->|Gestionare conturi| US
    RBAC -->|Agregări și extrageri date| PS
```