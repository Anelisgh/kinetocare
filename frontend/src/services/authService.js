const API_GATEWAY_URL = '/api/auth';
const CLIENT_ID = 'react-client'; 
const USER_API_URL = '/api/users'; // Folosim prefixul de proxy al Vite

export const authService = {
  // Register -> creează cont in keycloak + DB
  register: async (userData) => {
    const response = await fetch(`${USER_API_URL}/auth/register`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify(userData),
    });
    
    if (!response.ok) {
      const error = await response.json(); 
      throw new Error(error.message || 'Eroare la înregistrare');
    }
    
    return response.json();
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
      throw new Error(errorData.error_description || 'Email sau parolă incorectă!');
    }
    
    const tokenData = await response.json();
    
    return tokenData;
  },

  // crearea unui refresh token, pentru ca access token are o viata scurta. refresh token -> va cere un nou access token atunci cand primul expira. 
  refreshToken: async () => {
    const formData = new URLSearchParams();
    formData.append('grant_type', 'refresh_token');
    
    const response = await fetch(`${API_GATEWAY_URL}/token`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
      credentials: 'include', // -> browser-ul trimite automat cookie-ul cu refresh_token, iar backend-ul il trage din cookie
      body: formData,
    });
    
    if (!response.ok) {
      authService.logout();
      throw new Error('Sesiune expirată. Autentificarea este necesară.');
    }
    
    const tokenData = await response.json();
    
    localStorage.setItem('access_token', tokenData.access_token);
    
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
    
    localStorage.removeItem('access_token');
  },
  
  isAuthenticated: () => {
    const token = localStorage.getItem('access_token');
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
    const token = localStorage.getItem('access_token');
    if (!token) return null;
    
    try {
      // pentru a acces la email, roles, keycloakId din keycloak
      const payload = JSON.parse(atob(token.split('.')[1]));
      
      // extrage rolurile din realm_access
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
};
