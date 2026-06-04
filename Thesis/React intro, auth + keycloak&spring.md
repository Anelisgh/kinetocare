# Explicație Detaliată: Sistem de Autentificare React cu Keycloak (Model Hibrid BFF Securizat)

## Cuprins
1. [Concepte Fundamentale React](#concepte-fundamentale-react)
2. [Modelul de Securitate Hibrid: In-Memory Token + HttpOnly Cookie](#modelul-de-securitate-hibrid-in-memory-token--httponly-cookie)
3. [Explicarea authService.js](#explicarea-authservicejs)
4. [Explicarea AuthContext.jsx](#explicarea-authcontextjsx)
5. [Explicarea LoginPage.jsx](#explicarea-loginpagejsx)
6. [Explicarea RegisterPage.jsx](#explicarea-registerpagejsx)
7. [Explicarea UnauthorizedPage.jsx](#explicarea-unauthorizedpagejsx)
8. [Fluxul Complet de Autentificare și Reîmprospătare](#fluxul-complet-de-autentificare-și-reîmprospătare)
9. [De ce Validare pe Frontend și Backend](#de-ce-validare-pe-frontend-și-backend)
10. [Concepte Avansate și Detalii Tehnice](#concepte-avansate-și-detalii-tehnice)
11. [Best Practices și Îmbunătățiri Posibile](#best-practices-și-îmbunătățiri-posibile)
12. [Rezumat Final](#rezumat-final)

---

## Concepte Fundamentale React

Înainte de a intra în cod, este esențial să înțelegem conceptele fundamentale din ecosistemul React care guvernează acest modul de securitate.

### Ce este React?

React este o librărie JavaScript declarativă pentru construirea de interfețe utilizator (UI). Spre deosebire de dezvoltarea web tradițională unde DOM-ul este manipulat manual (`document.getElementById()`), în React interfața este descrisă ca o funcție de stare: **UI = f(state)**. Când starea se modifică, React recalculează diferențele și actualizează eficient ecranul.

### Componentele și JSX

O **componentă** React este o funcție JavaScript pură care returnează un element JSX. **JSX (JavaScript XML)** este o extensie de sintaxă ce permite scrierea de markup asemănător cu HTML direct în interiorul codului JavaScript:

```javascript
function Salut({ nume }) {
  return <h1 className="titlu">Bună ziua, {nume}!</h1>;
}
```

În culise, compilatoarele (ex. Babel) transformă JSX în apeluri de funcție pure React: `React.createElement('h1', { className: 'titlu' }, 'Bună ziua, ', nume)`.

### Hook-uri Principale: `useState` și `useEffect`

1. **`useState` (Gestiunea Stării)**: React păstrează starea componentelor între randări succesive. `useState` primește valoarea inițială și returnează un tuplu format din valoarea curentă a stării și o funcție de mutare:
   ```javascript
   const [contor, setContor] = useState(0);
   ```
   *Regulă de aur*: Mutarea stării în React este **imutabilă**. Nu modificăm niciodată starea direct (`contor = 5`), ci apelăm întotdeauna funcția de setare (`setContor(5)`), determinând React să re-randeze componenta.

2. **`useEffect` (Efecte Secundare)**: Permite rularea de cod ca reacție la schimbări din mediu (apeluri API, ascultători de evenimente, timere). Primește o funcție de execuție și un tablou de dependențe:
   ```javascript
   useEffect(() => {
     document.title = `Contor: ${contor}`;
     return () => console.log("Curățare!"); // Funcție de cleanup
   }, [contor]); // Rulează doar când 'contor' se schimbă
   ```

### Context API

**Context API** rezolvă problema transmiterii repetitive de proprietăți de-a lungul arborelui de componente (*props drilling*). Acesta creează un "depozit" global de date la care orice componentă copil se poate abona direct, indiferent de nivelul său de adâncime:

```
Fără Context (Props Drilling):   App ──► Navbar ──► UserMenu (necesită user prop)
Cu Context API:                  App (pune user în Context) ──► UserMenu (citește direct)
```

---

## Modelul de Securitate Hibrid: In-Memory Token + HttpOnly Cookie

Sistemul KinetoCare respinge stocarea simplistă a jetoanelor JWT în `localStorage`. Această alegere arhitecturală este critică pentru securitatea aplicației:

### De ce este `localStorage` periculos?
`localStorage` este o stocare persistentă a browserului la care **orice cod JavaScript ce rulează pe același domeniu are acces total**. În cazul unui atac de tip **XSS (Cross-Site Scripting)** — declanșat prin vulnerabilități în biblioteci terțe sau scripturi injectate — un atacator poate rula `localStorage.getItem('access_token')` și exfiltra instantaneu acreditările utilizatorului, compromițând complet contul.

### Soluția KinetoCare: Arhitectura Hibridă
Pentru a bloca atacurile XSS și CSRF, KinetoCare implementează o strategie de securitate avansată:

1. **Access Token în Memorie (In-Memory)**: Jetonul de acces (`access_token`), care are o durată scurtă de viață (ex. 5 minute), este stocat exclusiv într-o variabilă locală privată din interiorul bundle-ului JavaScript (`let inMemoryToken = null`). Niciun script extern nu poate citi această variabilă din afara closure-ului modulului.
2. **Refresh Token într-un Cookie HttpOnly**: Jetonul de lungă durată (`refresh_token`) este stocat pe browser într-un cookie setat de API Gateway cu directive restrictive:
   - `HttpOnly`: Cookie-ul este complet invizibil pentru JavaScript. Apelurile `document.cookie` nu îl pot citi sau modifica.
   - `Secure`: Transmis exclusiv peste conexiuni criptate HTTPS.
   - `SameSite=Lax`: Blochează transmiterea automată a cookie-ului în request-uri cross-origin, oferind o protecție robustă împotriva atacurilor de tip **CSRF (Cross-Site Request Forgery)**.

Prin acest mecanism hibrid, chiar dacă aplicația ar suferi o breșă XSS, atacatorul nu poate extrage nici jetonul de acces din memorie (datorită izolării closure-ului), nici jetonul de reîmprospătare din cookie (fiind HttpOnly).

---

## Explicarea authService.js

`authService.js` este un modul JavaScript pur care încapsulează comunicarea asincronă cu serviciile de identitate prin API Gateway, gestionând și starea în memorie a jetonului.

### Codul Sursă și Explicația Configurațiilor

```javascript
import { AppError } from '../utils/AppError.js';

const API_GATEWAY_URL = '/api/auth';
const CLIENT_ID = 'react-client'; 
const USER_API_URL = '/api/users'; // Folosim prefixul de proxy al Vite

let inMemoryToken = null; // Stocarea in siguranță, exclusiv in memorie

export const authService = {
  // Register -> creeaza cont in keycloak + DB
  register: async (userData) => {
    const response = await fetch(`${USER_API_URL}/auth/register`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify(userData),
    });
    
    if (!response.ok) {
      const errorData = await response.json(); 
      // Parsare RFC 7807
      const msg = errorData?.detail || errorData?.message || 'Eroare la înregistrare';
      const eroriCampuri = errorData?.erori_campuri || null;
      throw new AppError(msg, response.status, eroriCampuri, errorData?.detail);
    }
    
    return response.json();
  },

  // Forgot Password -> trimite email de resetare parola catre Keycloak
  // folosit in ForgotPasswordModal.jsx
  forgotPassword: async (email) => {
    const response = await fetch(`${USER_API_URL}/auth/forgot-password`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify({ email }),
    });

    if (!response.ok) {
      const errorData = await response.json();
      const msg = errorData?.detail || errorData?.message || 'Eroare la trimiterea email-ului';
      throw new AppError(msg, response.status);
    }
    // 204 No Content -> succes, fara body de returnat
  },

  // Metode noi pentru managementul in-memory al token-ului
  setToken: (token) => {
    inMemoryToken = token;
  },

  getToken: () => {
    return inMemoryToken;
  },

  clearToken: () => {
    inMemoryToken = null;
  },

  // Login -> prin Gateway, primeste refresh token in cookie
  login: async (email, password) => {
    const formData = new URLSearchParams();
    formData.append('grant_type', 'password');
    formData.append('username', email);
    formData.append('password', password);
    
    const response = await fetch(`${API_GATEWAY_URL}/token`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
      credentials: 'include', // -> trimite cookies la server
      body: formData,
    });
    
    if (!response.ok) {
      const errorData = await response.json();
      throw new AppError(
        errorData.error_description || 'Email sau parolă incorectă!',
        response.status
      );
    }
    
    const tokenData = await response.json();
    authService.setToken(tokenData.access_token);
    return tokenData;
  },

  // crearea unui refresh token
  refreshToken: async () => {
    const formData = new URLSearchParams();
    formData.append('grant_type', 'refresh_token');
    
    const response = await fetch(`${API_GATEWAY_URL}/token`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
      credentials: 'include',
      body: formData,
    });
    
    if (!response.ok) {
      authService.logout();
      throw new Error('Sesiune expirată. Autentificarea este necesară.');
    }
    
    const tokenData = await response.json();
    authService.setToken(tokenData.access_token);
    return authService.getUserInfo();
  },
  
  // sterge token-urile
  logout: async () => {
    try {
      await fetch(`${API_GATEWAY_URL}/logout`, {
        method: 'POST',
        credentials: 'include', // trimitem cookie-ul pentru a fi sters
      });
    } catch (error) {
      console.error('Eroare la logout:', error);
    }
    
    authService.clearToken();
  },
  
  isAuthenticated: () => {
    const token = authService.getToken();
    if (!token) return false;
    
    try {
      const payload = JSON.parse(atob(token.split('.')[1]));
      return Date.now() < payload.exp * 1000; 
    } catch {
      return false;
    }
  },
  
  // extrage info din JWT
  getUserInfo: () => {
    const token = authService.getToken();
    if (!token) return null;
    
    try {
      const payload = JSON.parse(atob(token.split('.')[1]));
      const roles = payload.realm_access?.roles || [];
      
      return {
        email: payload.email,
        roles: roles.map(role => role.toUpperCase()), 
        keycloakId: payload.sub,
      };
    } catch {
      return null;
    }
  },

  // returnează Keycloak ID (sub) direct din userInfo pentru a curăța codul API
  getUserId: () => {
    const userInfo = authService.getUserInfo();
    return userInfo ? userInfo.keycloakId : null;
  },
};
```

---

## Explicarea AuthContext.jsx

`AuthContext.jsx` administrează starea reactivă a autentificării și oferă o barieră de inițializare securizată pe perioada procesului de *Silent Refresh*.

```jsx
import React, { createContext, useContext, useState } from 'react';
import { authService } from '../services/authService';

// cream contextul
const AuthContext = createContext(null);

// hook personalizat
export const useAuth = () => {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error('useAuth trebuie folosit în interiorul unui AuthProvider');
  }
  return context;
};

// providerul care va incapsula aplicatia
export const AuthProvider = ({ children }) => {
  const [isAuthenticated, setIsAuthenticated] = useState(authService.isAuthenticated());
  const [userInfo, setUserInfo] = useState(authService.getUserInfo());
  const [isInitializing, setIsInitializing] = useState(true);

  // Silent refresh pe load: daca dam refresh la pagina, se pierde token-ul in-memory.
  // Dar backend-ul are cookie-ul HttpOnly cu refresh_token. Tragem de acolo noul access_token automat.
  React.useEffect(() => {
    const initAuth = async () => {
      try {
        await authService.refreshToken();
        setIsAuthenticated(true);
        setUserInfo(authService.getUserInfo());
      } catch (error) {
        // failed silent refresh - probabil refresh token expirat sau inexistent
        setIsAuthenticated(false);
        setUserInfo(null);
      } finally {
        setIsInitializing(false);
      }
    };

    initAuth();
  }, []);

  if (isInitializing) {
    // Returneaza un fallback (ex. spinner) cat timp verifica starea cookie-ului
    return <div className="flex h-screen items-center justify-center">Se încarcă profilul...</div>;
  }

  const login = async (email, password) => {
    try {
      const tokenData = await authService.login(email, password);
      
      setIsAuthenticated(true);
      setUserInfo(authService.getUserInfo());
      
      return tokenData;
    } catch (error) {
      throw error; 
    }
  };

  const logout = async () => {
    try {
      await authService.logout();
    } finally {
      setIsAuthenticated(false);
      setUserInfo(null);
    }
  };

  const value = {
    isAuthenticated,
    userInfo,
    login,
    logout,
  };

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
};
```

---

## Explicarea LoginPage.jsx

Această componentă colectează credențialele, apelează funcția `login` din Context-ul global și expune erorile captate în urma procesului de autentificare.

```jsx
import { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

export default function LoginPage() {
  const [formData, setFormData] = useState({ email: '', password: '' });
  const [error, setError] = useState('');
  const [isSubmitting, setIsSubmitting] = useState(false);
  
  const { login } = useAuth();
  const navigate = useNavigate();

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData(prev => ({ ...prev, [name]: value }));
    setError(''); // Curățare eroare la tastare
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!formData.email || !formData.password) {
      setError('Te rugăm să completezi toate câmpurile.');
      return;
    }

    setIsSubmitting(true);
    try {
      await login(formData.email, formData.password);
      navigate('/homepage', { replace: true });
    } catch (err) {
      setError(err.message || 'Autentificare eșuată.');
    } finally {
      setIsSubmitting(false);
    }
  };

  return (
    <div className="auth-container">
      <form onSubmit={handleSubmit} className="auth-form">
        <h2>Conectare</h2>
        {error && <div className="error-message">{error}</div>}
        
        <input 
          type="email" 
          name="email" 
          placeholder="Email" 
          value={formData.email} 
          onChange={handleChange} 
          disabled={isSubmitting} 
        />
        <input 
          type="password" 
          name="password" 
          placeholder="Parolă" 
          value={formData.password} 
          onChange={handleChange} 
          disabled={isSubmitting} 
        />
        
        <button type="submit" disabled={isSubmitting}>
          {isSubmitting ? 'Se conectează...' : 'Conectează-te'}
        </button>
      </form>
    </div>
  );
}
```

---

## Explicarea RegisterPage.jsx

`RegisterPage.jsx` folosește un formular complex și oferă suport complet pentru **maparea erorilor pe câmpuri** trimise de backend conform standardului RFC 7807 (Problem Details).

```jsx
import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { authService } from '../services/authService';

export default function RegisterPage() {
  const [formData, setFormData] = useState({
    nume: '', prenume: '', gen: '', telefon: '', email: '', password: '', role: ''
  });
  const [errors, setErrors] = useState({});
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [successMessage, setSuccessMessage] = useState('');
  const navigate = useNavigate();

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData(prev => ({ ...prev, [name]: value }));
    if (errors[name]) setErrors(prev => ({ ...prev, [name]: '' }));
  };

  const validateForm = () => {
    const newErrors = {};
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    
    if (!formData.email) newErrors.email = 'Email-ul este obligatoriu';
    else if (!emailRegex.test(formData.email)) newErrors.email = 'Format email invalid';

    if (!formData.password) newErrors.password = 'Parola este obligatorie';
    else if (formData.password.length < 6) newErrors.password = 'Parola trebuie să aibă minim 6 caractere';

    if (!formData.nume.trim()) newErrors.nume = 'Numele este obligatoriu';
    if (!formData.prenume.trim()) newErrors.prenume = 'Prenumele este obligatoriu';
    if (!formData.gen) newErrors.gen = 'Selectează genul';

    const phoneRegex = /^07[0-9]{8}$/;
    if (!formData.telefon) newErrors.telefon = 'Telefonul este obligatoriu';
    else if (!phoneRegex.test(formData.telefon.replace(/\s/g, ''))) {
      newErrors.telefon = 'Numărul de telefon trebuie să conțină exact 10 cifre';
    }

    if (!formData.role) newErrors.role = 'Selectează tipul contului';

    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!validateForm()) return;

    setIsSubmitting(true);
    setErrors({});
    try {
      const response = await authService.register(formData);
      setSuccessMessage(response.message || 'Înregistrare finalizată cu succes!');
      setTimeout(() => navigate('/login'), 2000);
    } catch (error) {
      if (error.eroriCampuri) {
        // Maparea directă a erorilor de validare Spring (@Valid) peste câmpurile formularului
        setErrors({ ...error.eroriCampuri, submit: error.message });
      } else {
        setErrors({ submit: error.message });
      }
    } finally {
      setIsSubmitting(false);
    }
  };

  return (
    <div className="auth-container">
      <form onSubmit={handleSubmit} className="auth-form">
        <h2>Creare Cont</h2>
        {successMessage && <div className="success-message">{successMessage}</div>}
        {errors.submit && <div className="error-message">{errors.submit}</div>}

        <input type="text" name="nume" placeholder="Nume" value={formData.nume} onChange={handleChange} />
        {errors.nume && <small className="error-text">{errors.nume}</small>}

        <input type="text" name="prenume" placeholder="Prenume" value={formData.prenume} onChange={handleChange} />
        {errors.prenume && <small className="error-text">{errors.prenume}</small>}

        <select name="gen" value={formData.gen} onChange={handleChange}>
          <option value="">Selectează Genul</option>
          <option value="MASCULIN">Masculin</option>
          <option value="FEMININ">Feminin</option>
        </select>
        {errors.gen && <small className="error-text">{errors.gen}</small>}

        <input type="tel" name="telefon" placeholder="Telefon" value={formData.telefon} onChange={handleChange} />
        {errors.telefon && <small className="error-text">{errors.telefon}</small>}

        <input type="email" name="email" placeholder="Email" value={formData.email} onChange={handleChange} />
        {errors.email && <small className="error-text">{errors.email}</small>}

        <input type="password" name="password" placeholder="Parolă" value={formData.password} onChange={handleChange} />
        {errors.password && <small className="error-text">{errors.password}</small>}

        <select name="role" value={formData.role} onChange={handleChange}>
          <option value="">Tip Cont</option>
          <option value="TERAPEUT">Terapeut</option>
          <option value="PACIENT">Pacient</option>
        </select>
        {errors.role && <small className="error-text">{errors.role}</small>}

        <button type="submit" disabled={isSubmitting}>
          {isSubmitting ? 'Se procesează...' : 'Înregistrează-te'}
        </button>
      </form>
    </div>
  );
}
```

---

## Explicarea UnauthorizedPage.jsx

O componentă simplă, declarativă, afișată atunci când sistemul detectează o tentativă de accesare a unei rute fără ca rolul utilizatorului (extras securizat din JWT de către `ProtectedRoute`) să dețină permisiunile necesare.

```jsx
import { Link } from 'react-router-dom';

export default function UnauthorizedPage() {
  return (
    <div className="auth-container error-page">
      <h1 className="text-danger">Acces Restricționat</h1>
      <p className="error-description">
        Nu aveți privilegiile necesare pentru a accesa această secțiune a aplicației.
      </p>
      <div className="navigation-links">
        <Link to="/homepage" className="btn">Pagina Principală</Link>
        <Link to="/login" className="btn secondary">Conectare</Link>
      </div>
    </div>
  );
}
```

---

## Fluxul Complet de Autentificare și Reîmprospătare

Arhitectura hibridă rulează în culise trei fluxuri fundamentale de date:

### Scenariul 1: Autentificarea Inițială (Login)
1. Utilizatorul introduce email-ul și parola și apasă pe butonul "Conectează-te".
2. Frontend-ul apelează `authService.login()` trimitând credențialele în format URL-encoded.
3. Cererea ajunge la **API Gateway** (`/api/auth/token`).
4. Gateway-ul contactează Keycloak în rețeaua privată, validând credențialele.
5. Keycloak returnează jetoanele către Gateway.
6. **Securizarea pe Edge (Gateway)**:
   - Gateway-ul preia `refresh_token`-ul și îl injectează într-un antet `Set-Cookie` securizat: `refresh_token=...; HttpOnly; SameSite=Lax; Path=/; Max-Age=2592000; Secure`.
   - Gateway-ul curăță corpul JSON al răspunsului, setând `refresh_token: null`, prevenind astfel citirea sa din JavaScript.
   - Gateway-ul returnează răspunsul ce conține doar `access_token` în corpul JSON către frontend.
7. Frontend-ul citește `access_token` și îl stochează exclusiv în memorie (`inMemoryToken`).

### Scenariul 2: Încărcarea Aplicației / Refresh Pagină (F5)
1. Utilizatorul reîncarcă pagina. Memoria firului de JavaScript se resetează complet (jetonul de acces din memorie este pierdut).
2. `AuthProvider` pornește având starea `isInitializing: true`, randând un spinner de încărcare.
3. `useEffect` declanșează asincron `authService.refreshToken()`.
4. Browser-ul transmite cererea către `/api/auth/token` atașând automat cookie-ul securizat `refresh_token` (deoarece s-a folosit directiva `credentials: 'include'`).
5. Gateway-ul citește cookie-ul, validează semnătura și trimite o cerere către Keycloak pentru a genera un nou set de jetoane.
6. Gateway-ul returnează noul `access_token` în format JSON și actualizează opțional cookie-ul de reîmprospătare.
7. Frontend-ul preia noul jeton, îl salvează în memorie și setează `isInitializing: false`.
8. Dashboard-ul este randat fără ca utilizatorul să sesizeze vreo întrerupere (experiență nativă de tip *Silent Refresh*).

### Scenariul 3: Expirarea Jetonului de Acces în timpul navigării
Jetonul de acces în memorie expiră după câteva minute. La următorul apel API (ex. `GET /api/profile`):
1. Microserviciul downstream (Resource Server) detectează un jeton expirat și returnează un cod HTTP `401 Unauthorized`.
2. Interceptorul HTTP de pe frontend (Axios/Fetch Wrapper) prinde codul de eroare `401`.
3. Interceptorul blochează temporar coada de apeluri și solicită asincron `authService.refreshToken()`.
4. Dacă reîmprospătarea reușește (datorită cookie-ului HttpOnly încă valid), se preia noul jeton de acces și se reîncearcă automat cererea inițială eșuată.
5. Utilizatorul nu observă nicio eroare de sistem sau deconectare.
6. Dacă și reîmprospătarea eșuează (cookie-ul de reîmprospătare a expirat după 30 de zile), utilizatorul este delogat automat și trimis la ecranul `/login`.

---

## De ce Validare pe Frontend și Backend

O regulă de aur în securitatea aplicațiilor web este: **Niciodată nu avem încredere în datele provenite de la client.**

```
 ┌───────────────┐
 │   FRONTEND    │  ◄── Validare pentru Experiență Utilizator (UX)
 └───────┬───────┘      - Feedback rapid (< 50ms) fără apeluri de rețea.
         │              - Ghidaj dinamic (butoane blocate, mesaje sub câmpuri).
         ▼
 ┌───────────────┐
 │   INTERNET    │  ◄── Sursă de vulnerabilități
 └───────┬───────┘      - Un atacator poate simula cereri folosind Postman sau cURL.
         │              - Poate ocoli codul React modificând fișierele în DevTools.
         ▼
 ┌───────────────┐
 │   BACKEND     │  ◄── Validare pentru Securitate și Integritate
 └───────────────┘      - Apără baza de date de date corupte sau atacuri de injectare.
                        - Singura sursă de adevăr sigură la nivel tranzacțional.
```

### De ce este crucială validarea pe ambele straturi în KinetoCare?

1. **La nivel de Înregistrare (`RegisterPage.jsx` vs. `user-service`):**
   - *Frontend*: Verifică formatul corect de email printr-o expresie regulată simplă pentru a ghida utilizatorul în timp real.
   - *Backend*: Adnotarea `@Email` pe DTO blochează orice cerere directă de tip API neconformă. Cel mai important, backend-ul interoghează baza de date pentru a verifica dacă adresa de email este unică (`existsByEmail`), o operațiune imposibil de executat securizat direct pe frontend fără a expune date.
2. **La nivel de Fișă Medicală (`CompleteProfile.jsx` vs. `pacienti-service`):**
   - *Frontend*: Ascunde dinamic câmpul `detaliiSport` dacă utilizatorul bifează "Face sport: NU".
   - *Backend*: Verifică tranzacțional la nivel de business că, în cazul în care `faceSport` este setat pe `DA`, câmpul `detaliiSport` este obligatoriu completat și nu conține doar spații libere, anulând salvarea dacă condițiile nu sunt îndeplinite.

---

## Concepte Avansate și Detalii Tehnice

### 1. Mecanismul JWT în Spring Security
Când un microserviciu configurat ca **Resource Server** primește un request, securitatea downstream execută următorii pași:
1. Citește headerul `Authorization` și validează formatul `Bearer <JWT>`.
2. Extrage cheia publică configurată la adresa `jwk-set-uri` a Keycloak pentru a valida asimetric semnătura digitală a jetonului.
3. Dacă semnătura este validă și jetonul nu este expirat, `JwtDecoder` creează un obiect `Jwt`.
4. `JwtAuthenticationConverter` preia claim-urile din jeton (cum ar fi `realm_access.roles`) și mapează colecția într-un set de obiecte `SimpleGrantedAuthority` (prefixate implicit cu `ROLE_`).
5. Obiectul rezultat este salvat în contextul de rulare al firului de execuție curent (`SecurityContextHolder`), permițând utilizarea regulilor de securitate precum `.hasRole("PACIENT")`.

### 2. Axios Interceptors pentru Token-ul în Memorie
Pentru a injecta automat jetonul de acces în antetele fiecărui request extern fără a expune manual variabila în memorie în fiecare pagină, se folosește un interceptor Axios global:

```javascript
import axios from 'axios';
import { authService } from './services/authService';

const api = axios.create({
  baseURL: '/',
});

// Interceptor de Request: injectează automat Bearer Token-ul din memorie
api.interceptors.request.use(config => {
  const token = authService.getToken();
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

// Interceptor de Response: gestionează automat erorile 401 (Silent Refresh)
api.interceptors.response.use(
  response => response,
  async error => {
    const originalRequest = error.config;
    
    if (error.response?.status === 401 && !originalRequest._retry) {
      originalRequest._retry = true;
      try {
        // Încearcă obținerea unui nou access_token folosind cookie-ul de refresh
        const userInfo = await authService.refreshToken();
        if (userInfo) {
          // Reîncearcă request-ul inițial cu noul token injectat în header
          originalRequest.headers.Authorization = `Bearer ${authService.getToken()}`;
          return axios(originalRequest);
        }
      } catch (refreshError) {
        // Refresh-ul a eșuat (sesiune complet expirată), deconectăm utilizatorul
        authService.logout();
        window.location.href = '/login';
      }
    }
    return Promise.reject(error);
  }
);

export default api;
```

---

## Best Practices și Îmbunătățiri Posibile

1. **Evitarea XSS prin Sanitizare Riguroasă**: Deși stocarea tokenului în memorie previne furtul direct de jetoane din local storage, o breșă XSS poate fi folosită de atacatori pentru a face request-uri direct în numele utilizatorului prin API-ul configurat. Astfel, sanitizarea intrărilor cu librării precum `DOMPurify` la randarea de date introduse de utilizatori (ex. în modulul de chat) rămâne o prioritate critică.
2. **Utilizarea clauzei `SameSite=Strict` pentru Cookie-uri**: Trecerea de la `SameSite=Lax` la `SameSite=Strict` blochează transmiterea cookie-urilor chiar și la navigarea din link-uri externe către aplicație. Aceasta oferă un nivel de securitate suplimentar, cu prețul reautentificării silențioase atunci când utilizatorul deschide o pagină KinetoCare dintr-un email sau site extern.
3. **Criptarea suplimentară a memoriei (Web Cryptography API)**: Jetonul de acces stocat în variabila `let inMemoryToken` poate fi stocat opțional în format criptat în interiorul memoriei JS folosind chei efemere generate la startup, îngreunând citirea sa directă din memoria heap în eventualitatea unor atacuri de tip mem-dump realizate prin extensii de browser malițioase.

---

## Rezumat Final

### Componentele sistemului de autentificare KinetoCare:

1. **`authService.js` (JavaScript Pur)**: Gestionează exclusiv logica de comunicare HTTP cu API Gateway, stocarea/ștergerea jetonului de acces din memoria heap și configurarea apelurilor cu suport de cookie-uri (`credentials: 'include'`).
2. **`AuthContext.jsx` (React Context)**: Păstrează starea reactivă globală de autentificare a utilizatorului, oprește accesul eronat al vizitatorilor neautentificați prin starea de inițializare (`isInitializing`) și oferă metode de login/logout ușor accesibile prin hook-ul personalizat `useAuth()`.
3. **`LoginPage.jsx` & `RegisterPage.jsx` (Interfața Utilizator)**: Formulare de colectare securizate, având validare detaliată pe client și mapare dinamică a erorilor de validare Spring.
4. **`API Gateway` (Edge Server)**: Acționează ca un proxy securizat, extrage jetonul de refresh din Keycloak, îl injectează într-un cookie protejat `HttpOnly` și îl elimină din corpul JSON returnat către frontend pentru a garanta o imunitate totală la nivel de XSS.