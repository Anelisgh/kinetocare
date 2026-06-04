# Capitolul 2. Analiza Domeniului și Cerințele Sistemului

Acest capitol fundamentează necesitatea și sfera de cuprindere a sistemului KinetoCare prin parcurgerea a patru etape analitice succesive. În prima parte sunt identificate provocările operaționale specifice managementului clinic în kinetoterapie. Pe această bază, secțiunea a doua evaluează critic soluțiile software existente pe piață, evidențiind lacunele funcționale neadresate și poziționând comparativ sistemul propus printr-o analiză SWOT. Ultimele două secțiuni formalizează, respectiv, cerințele funcționale și non-funcționale derivate din această analiză, constituind baza de referință pentru deciziile de proiectare arhitecturală din capitolele următoare.

### 2.1 Provocările din managementul clinic

Sectorul de recuperare medicală și kinetoterapie se distinge de alte ramuri medicale prin natura sa iterativă. Tratamentul nu constă, de regulă, într-o singură intervenție izolată, ci într-un traseu terapeutic ce implică multiple ședințe, evaluări și reevaluări periodice. Această recurență generează o serie de provocări operaționale complexe, care sunt dificil de gestionat în absența unui sistem informatic integrat. Clinicile de kinetoterapie din România operează în marea majoritate cu instrumente generice (agende fizice, foi de calcul, aplicații de calendar) care nu au fost concepute pentru specificul domeniului medical recuperator.   
### 2.1.1 Fragmentarea comunicării și a fluxului informațional   
În absența unei platforme centralizate, comunicarea dintre clinici, terapeuți și pacienți este ineficientă și predispusă la erori:   
- **Gestiunea rudimentară a programărilor:** Planificarea se realizează frecvent prin metode tradiționale (telefonic sau pe agende fizice), ceea ce introduce un risc ridicat de eroare umană, ducând la dublă rezervare sau la intervale neproductive în orarul terapeuților.   
- **Procese manuale de notificare:** Informarea pacienților cu privire la programări sau anulări se realizează manual, consumând timp administrativ și crescând rata de neprezentare atunci când notificările sunt omise.   
- **Lipsa buclei de feedback post-ședință:** Nu există un canal structurat prin care pacientul să raporteze nivelul de durere resimțit, dificultatea exercițiilor sau gradul de oboseală între ședințe. Absența acestor date reduce capacitatea terapeutului de a ajusta planul de tratament pe baza evoluției reale a pacientului.   
   
### 2.1.2 Lipsa trasabilității clinice și a monitorizării continue   
Kinetoterapia modernă necesită decizii clinice fundamentate pe o practică medicală bazată pe dovezi. Managementul manual al dosarelor face ca această monitorizare să fie sistematic deficitară:   
- **Urmărirea deficitară a planului terapeutic:** Nu există un mecanism automat care să monitorizeze numărul de ședințe efectuate în raport cu numărul recomandat. Terapeuții sunt nevoiți să calculeze manual progresul, cu riscul ratării momentului optim pentru reevaluarea clinică.   
- **Pierderea contextului la schimbarea terapeutului:** Fără un dosar medical electronic unificat care să conțină evaluările, istoricul ședințelor și notițele de progres, un terapeut care preia un pacient de la un coleg pierde contextul clinic, afectând continuitatea actului medical.   
- **Implicarea scăzută a pacientului:** Când pacientul nu are vizibilitate asupra propriului progres, aderența la tratament pe termen lung scade semnificativ.   
   
### 2.1.3 Dificultăți în managementul disponibilității și al resurselor   
Resursele unei clinici (timpul terapeuților și spațiile de tratament) sunt limitate și necesită o alocare dinamică:   
- **Gestiunea multi-locație:** Terapeuții pot activa în locații diferite în zile diferite ale săptămânii. Sistemele tradiționale nu sunt capabile să reprezinte fidel această matrice complexă de disponibilitate, obligând pacienții să contacteze recepția pentru orice verificare de program.   
- **Sincronizarea concediilor:** Perioadele de absență ale personalului medical nu sunt vizibile pacienților în timp real, generând programări invalide care necesită reprogramare ulterioară.   
   
### 2.1.4 Ineficiențe în managementul administrativ și decizional   
Din perspectiva administratorilor de clinică, lipsa unui sistem digitalizat limitează capacitatea de optimizare a activității:   
- **Lipsa vizibilității asupra indicatorilor de performanță:** Fără date centralizate, este dificil să se evalueze cu precizie veniturile per locație, volumul de programări, rata de anulări sau performanța individuală a terapeuților.   
- **Gestiunea greoaie a catalogului de servicii:** Ajustarea prețurilor sau adăugarea de servicii noi se face disparat, cu riscul neconcordanțelor între informațiile disponibile la recepție și cele comunicate pacienților.   
   
### 2.1.5 Protecția datelor medicale sensibile   
Dosarele de recuperare medicală conțin informații cu caracter personal sensibil (diagnostic, istoric medical, CNP), a căror protecție este reglementată prin Regulamentul General privind Protecția Datelor (GDPR). În absența unui sistem informatic specializat, clinicile se expun unor riscuri concrete:   
- **Control insuficient al accesului:** În evidențele fizice sau în documentele partajate de tip tabelar, toți angajații au frecvent acces nerestricționat la baza de date completă a pacienților, fără posibilitatea de a delimita accesul în funcție de rol (pacient, terapeut, administrator).   
- **Absența unui jurnal de trasabilitate (*audit trail*):** Orice modificare a unui dosar medical ar trebui înregistrată cu marcaj temporal și identitatea celui care a efectuat-o. Soluțiile nespecializate nu oferă această trasabilitate, îngreunând demonstrarea conformității GDPR în cazul unui control.   
