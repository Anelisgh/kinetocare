import axios from 'axios';
import { authService } from './authService.js';

const api = axios.create({
    baseURL: '',
    withCredentials: true, // trimite cookies la fiecare request
});

// inainte de fiecare request adauga access_token in header
api.interceptors.request.use(
    (config) => {
        const token = localStorage.getItem('access_token');
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
                const newToken = localStorage.getItem('access_token');
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

// Functie centralizata de tratare a erorilor API
export const handleApiError = (error, defaultMessage) => {
  console.error('API Error:', error);
  const msg =
    error.response?.data?.message ||
    error.response?.data?.error ||
    error.message ||
    defaultMessage;
  throw new Error(msg);
};