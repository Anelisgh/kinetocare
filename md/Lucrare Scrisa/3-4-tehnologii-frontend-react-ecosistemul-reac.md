## 3.4 Tehnologii *frontend*: ecosistemul React și arhitectura interfeței

### 3.4.1 Paradigma *Single Page Application* (SPA) și separarea responsabilităților

Aplicația client KinetoCare a fost proiectată conform paradigmei *Single Page Application* (*SPA*), utilizând biblioteca React în conjuncție cu sistemul de rutare React Router v7. Această decizie arhitecturală modifică fundamental interacțiunea tradițională client-server: serverul nu mai generează și nu mai returnează documente HTML complete la fiecare navigare. În schimb, aplicația este descărcată integral la prima vizită, iar interacțiunile ulterioare presupun exclusiv schimburi asincrone de date (format JSON) cu API *Gateway*-ul, structura vizuală a paginii (DOM-ul) fiind actualizată selectiv în browser, fără reîncărcări complete.

Pentru a stăpâni complexitatea interfeței medicale, codul a fost structurat pe baza Principiului Responsabilității Unice (SRP) și al Separării Preocupărilor (*Separation of Concerns*), rezultând o arhitectură stratificată pe patru niveluri:

- **Stratul de rutare și persistență vizuală (*Layouts & Pages*):** Gestionează structurile statice persistente (bare de navigare, meniuri) și orchestrează componentele copii prin mecanismul `<Outlet />`, prevenind re-randările costisitoare ale întregului arbore vizual la fiecare navigare.
- **Stratul prezentării (*Components*):** Încapsulează fragmente de interfață reutilizabile și independente, organizate strict pe domenii de *business* (pacient, terapeut, *chat*).
- **Stratul de integrare (*Services*):** Abstractizează complet comunicarea de rețea. Componentele vizuale nu dețin detalii despre adresele URL sau contractele de date (DTO) ale *backend*-ului, interacționând exclusiv prin apeluri de funcții asincrone puternic tipizate.
- **Stratul stării globale (*Context*):** Acționează ca furnizor central de adevăr pentru aspectele transversale ale platformei, cum ar fi identitatea și permisiunile utilizatorului conectat.

### 3.4.2 Managementul sesiunii și decodificarea criptografică locală

Securitatea pe partea de client este gestionată printr-un context global care expune starea curentă de autentificare întregului arbore de componente. O decizie de optimizare relevantă este decodificarea pur locală a jetonului JWT.

În loc de a efectua un apel HTTP suplimentar către componenta de identitate pentru a obține detaliile utilizatorului la momentul conectării, *frontend*-ul extrage și decodifică direct *payload*-ul jetonului de acces primit (*Base64Url*). Acest mecanism extrage instantaneu identificatorul unic, adresa de e-mail și rolurile asociate, decuplând logic faza de *bootstrap* a *frontend*-ului de disponibilitatea *endpoint*-urilor de profil din *backend*.

Pentru menținerea continuității operaționale, a fost implementat tiparul de Reîmprospătare Silențioasă (*Silent Refresh*). La inițializarea aplicației, sistemul blochează temporar randarea interfeței (starea de inițializare) și inițiază o cerere de reîmprospătare către API *Gateway*, browserul atașând automat cookie-ul *HttpOnly* persistent în cerere, fără ca codul JavaScript să aibă acces direct la acesta. Această abordare garantează o protecție robustă împotriva atacurilor de tip *XSS*, deoarece jetonul de reîmprospătare nu este expus în codul client, asigurând în același timp o experiență fluidă în care utilizatorii cu o sesiune validă pot închide și redeschide aplicația fără a fi forțați să își reintroducă explicit credențialele.

### 3.4.3 Interceptarea traficului și normalizarea erorilor

Întreaga comunicare de rețea este orchestrată printr-un client HTTP centralizat, extins prin aplicarea tiparului *Decorator* prin intermediul interceptorilor. Această infrastructură rezolvă două probleme majore ale aplicațiilor distribuite:

1. **Bucla de recuperare la expirarea autorizării:** Interceptorul de răspuns detectează proactiv erorile de tip `401 Unauthorized`. La apariția acestei erori, interceptorul suspendă temporar cererea originală eșuată, declanșează fluxul asincron de reîmprospătare a jetonului, iar la obținerea succesului injectează noul jeton și reia cererea suspendată. Totul se desfășoară transparent, fără ca utilizatorul să piardă datele introduse în formulare.

2. **Normalizarea erorilor conform standardului RFC 9457:** Deoarece microserviciile KinetoCare returnează erori structurate sub forma formatului *Problem Details*, interceptorul de client mapează automat răspunsurile la un model de eroare standardizat local. Pentru validările eșuate, harta câmpurilor invalide este extrasă și propagată direct către componentele vizuale, permițând asocierea erorilor direct cu elementele de intrare (*input*), fără logică duplicată de parsare în fiecare componentă în parte.

### 3.4.4 Autorizarea la nivelul prezentării și barierele clinice (*Guards*)

Controlul accesului la nivelul interfeței utilizator este asigurat prin implementarea tiparului *Higher-Order Component* (*HOC*) integrat direct în graful de rutare. Componentele acționează ca *proxy*-uri invizibile: interceptează tentativa de navigare, validează concordanța dintre rolul extras din JWT și rolurile permise ale rutei și redirecționează utilizatorii neautorizați, izolând complet modulele critice.

O aplicație distinctă a acestui tipar este componenta de barieră clinică (*Clinical Guard*). Constrângerile de *business* dictează că un pacient proaspăt înregistrat nu poate beneficia de servicii medicale fără a furniza datele legale și clinice minime (cod numeric personal, data nașterii). Bariera clinică interoghează starea de completitudine a dosarului și blochează tranzitul către zonele operaționale ale platformei, forțând captarea datelor necesare într-un spațiu izolat — modulul de profilare — inaccesibil ocolirii prin manipularea manuală a adresei URL.

### 3.4.5 Arhitectura de comunicare în timp real: STOMP și negocierea protocolului

Subsistemul de comunicații instantanee este implementat utilizând o ierarhie de protocoale de rețea. La nivelul de bază, protocolul *WebSocket* este preferat soluțiilor tradiționale de tip *HTTP Polling* deoarece asigură un canal TCP bidirecțional, persistent și cu latență minimă, eliminând redundanța antetelor HTTP de mari dimensiuni la fiecare schimb de mesaje.

Deoarece *WebSocket* este un protocol brut (care nu definește reguli stricte de rutare sau formatare a mesajelor), deasupra sa a fost aplicat protocolul *STOMP* (*Simple Text Oriented Messaging Protocol*). Acesta oferă o semantică avansată de mesagerie (canale, destinații, subscripții de tip publicare-abonare), permițând clientului React să se aboneze la cozi specifice (de exemplu, conversația activă) și să primească exclusiv pachetele destinate lui de către brokerul central.

Pentru a garanta robustețea în medii restrictive (rețele corporative cu reguli de *firewall* care blochează *WebSocket* nativ) și pentru a permite injectarea controlată a antetului de autorizare în timpul stabilirii conexiunii, procesul de negociere a rețelei este administrat prin *SockJS*. Acesta inițiază negocierea ca o simplă conexiune HTTP, ridicând-o la nivel de *WebSocket* exclusiv după validarea capabilității rețelei.

Din perspectiva arhitecturii interfeței, fluxul de *chat* beneficiază de conceptul de generare la cerere a resurselor (Tiparul *Virtual Proxy*) pentru conversațiile virtuale. La crearea unei relații clinice, sistemul nu populează anticipat baza de date cu înregistrări vide de *chat*. Agregarea realizată de *Gateway* identifică asocierile active fără un istoric de mesaje și generează conversații sintetice la momentul redării, conversația apărând imediat în lista de *chat*. Instanțierea fizică a conversației în baza de date se realizează prin Inițializare Leneșă (*Lazy Initialization*), abia la primul mesaj expediat.

### 3.4.6 Ecosistemul tehnologic și optimizarea livrării (*build pipeline*)

Integrarea bibliotecilor specializate a fost justificată de necesitatea reducerii timpului de dezvoltare fără a sacrifica performanța clientului:

- Componenta de calendar medical interoghează resursele *backend*-ului dinamic, strict pe baza coordonatelor vizuale active (*viewport-based data fetching*), transmițând datele în format ISO-8601. La vizualizarea lunii curente nu sunt încărcate programările istorice, reducând astfel volumul de date transferat.
- Sistemele de grafice pentru evoluția clinică utilizează randare nativă bazată pe standardul *SVG*, integrată direct în arborele React, facilitând o personalizare vizuală completă prin CSS și evitând izolarea tehnică specifică elementelor *Canvas*.

Livrarea finală a aplicației în mediul de producție utilizează o strategie *Docker Multi-Stage Build*:

1. **Faza de asamblare:** Un container cu ecosistem complet (Node.js) compilează și minimizează întregul cod JavaScript într-un pachet static optimizat.
2. **Faza de livrare:** Fișierele rezultate sunt extrase și găzduite exclusiv de un server Nginx ultraperformant.

Această topologie garantează că imaginea finală livrată nu include mediul de execuție Node.js, codul sursă necompilat sau instrumentele de analiză, minimizând drastic dimensiunea containerului și suprafața de atac.

### 3.4.7 Reziliența interfeței: toleranța la erori și *smart polling*

Reziliența arhitecturală a microserviciilor *backend* este reflectată simetric în arhitectura *frontend*-ului. Pentru a preveni colapsul general al aplicației la eșecul procesării datelor dintr-un singur serviciu, a fost aplicat tiparul *Bulkhead* prin intermediul Granițelor de Erori (*Error Boundaries*) din React. Inspirat din principiul pereților etanși dintr-un vas naval — unde inundarea unui compartiment nu afectează celelalte — acest tipar capturează excepțiile aruncate pe parcursul fazei de randare și izolează avaria la nivelul strict al componentei afectate. Restul aplicației rămâne complet funcțional, iar zona afectată oferă un mecanism de reluare cu interfață de rezervă (*fallback UI*).

Aplicația optimizează interogările asincrone periodice (cum ar fi verificarea numărului de notificări necitite) prin tehnica de *Smart Polling*, asistată de *Page Visibility API*-ul nativ al browserului. Când utilizatorul navighează către o altă filă, aplicația detectează starea latentă a documentului și suspendă ciclurile de interogare. La revenirea vizibilității paginii, sistemul reia automat interogările, declanșând imediat o sincronizare a stării. Această optimizare reduce consumul de baterie pe dispozitivele mobile și traficul inutil spre API *Gateway*.
