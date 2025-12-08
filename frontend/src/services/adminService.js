import api from './api';

const handleError = (error, defaultMessage) => {
  console.error('API Error:', error);
  const message =
    error.response?.data?.message ||
    error.message ||
    defaultMessage;
  throw new Error(message);
};

export const adminService = {
  // Obține TOATE locatiile (inclusiv inactive)
  getAllLocatii: async () => {
    try {
      const response = await api.get('/api/locatii/all');
      return response.data;
    } catch (error) {
      handleError(error, 'Eroare la încărcarea locațiilor');
    }
  },

  createLocatie: async (data) => {
    try {
      const response = await api.post('/api/locatii', data);
      return response.data;
    } catch (error) {
      handleError(error, 'Eroare la crearea locației');
    }
  },

  updateLocatie: async (id, data) => {
    try {
      const response = await api.patch(`/api/locatii/${id}`, data);
      return response.data;
    } catch (error) {
      handleError(error, 'Eroare la actualizarea locației');
    }
  },

  // Toggle Active/Inactive
  toggleStatus: async (id) => {
    try {
      await api.delete(`/api/locatii/${id}`);
    } catch (error) {
      handleError(error, 'Eroare la schimbarea statusului locației');
    }
  }
};