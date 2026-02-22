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
      throw new Error(errorData.error_description || 'Email sau parolă incorectă!');
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
};
