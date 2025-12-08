import api from './api';

const handleError = (error, defaultMessage) => {
  console.error('API Error:', error);
  throw new Error(error.response?.data?.error || defaultMessage);
};

export const homepageService = {
  getDashboardData: async () => {
    try {
      const response = await api.get('/api/homepage');
      return response.data;
    } catch (error) {
      handleError(error, 'Eroare la încărcarea dashboard-ului');
    }
  }
};