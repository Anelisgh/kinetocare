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
  // starea initiala este verificata la fiecare reload al paginii
  const [isAuthenticated, setIsAuthenticated] = useState(authService.isAuthenticated());
  const [userInfo, setUserInfo] = useState(authService.getUserInfo());

  const login = async (email, password) => {
    try {
      const tokenData = await authService.login(email, password);
      
      localStorage.setItem('access_token', tokenData.access_token);
      
      setIsAuthenticated(true);
      setUserInfo(authService.getUserInfo());
      
      return tokenData;
    } catch (error) {
      throw error; 
    }
  };

  const logout = () => {
    authService.logout();
    setIsAuthenticated(false);
    setUserInfo(null);
  };

  const value = {
    isAuthenticated,
    userInfo,
    login,
    logout,
  };

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
};
