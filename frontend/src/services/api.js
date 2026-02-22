import axios from 'axios';
import { authService } from './authService.js';
import { AppError } from '../utils/AppError.js';

const api = axios.create({
    baseURL: '',
    withCredentials: true, // trimite cookies la fiecare request
});

// inainte de fiecare request adauga access_token in header
api.interceptors.request.use(
    (config) => {
        const token = authService.getToken();
        if (token) {
            config.headers.Authorization = `Bearer ${token}`;
        }
        return config;
    },
    (error) => Promise.reject(error)
);

api.interceptors.response.use(
    (response) => response, // doar returneaza raspunsul
    async (error) => {
        const originalRequest = error.config;

        if (error.response?.status === 401 && !originalRequest._retry) {
            originalRequest._retry = true; // daca refresh-ul esueaza si primim alta eroare, atunci nu mai incercam din nou
            // browser-ul trimite cookie cu refresh_token pentru a primi de la backend un nou access token
            try {
                await authService.refreshToken();
                const newToken = authService.getToken();
                originalRequest.headers.Authorization = `Bearer ${newToken}`;
                return api(originalRequest); // trimite din nou request-ul original, dar acum cu access_token pentru ca utilizatorul sa nu sesizeze nimic
            } catch (refreshError) {
                authService.logout();
                window.location.href = '/login';
                return Promise.reject(refreshError);
            }
        }
        return Promise.reject(error);
    }
);

export default api;

export const handleApiError = (error, defaultMessage) => {
  console.error('API Error:', error);
  
  const responseData = error.response?.data;
  
  // Conform RFC 7807 (ProblemDetail), mesajul principal este in "detail", 
  // iar erorile la nivel de field in "erori_campuri".
  const msg = 
    responseData?.detail ||
    responseData?.message ||
    responseData?.error_description ||
    responseData?.error ||
    error.message ||
    defaultMessage;

  const status = error.response?.status || 500;
  const eroriCampuri = responseData?.erori_campuri || null;

  throw new AppError(msg, status, eroriCampuri, responseData?.detail);
};