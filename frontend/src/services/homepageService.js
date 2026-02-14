import api, { handleApiError } from './api';

export const homepageService = {
  getDashboardData: async () => {
    try {
      const response = await api.get('/api/homepage');
      return response.data;
    } catch (error) {
      handleApiError(error, 'Eroare la încărcarea dashboard-ului');
    }
  }
};