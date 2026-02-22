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
