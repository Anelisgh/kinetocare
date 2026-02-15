import api, { handleApiError } from './api';

export const adminService = {
  // Obtine TOATE locatiile (inclusiv inactive)
  getAllLocatii: async () => {
    try {
      const response = await api.get('/api/locatii/all');
      return response.data;
    } catch (error) {
      handleApiError(error, 'Eroare la încărcarea locațiilor');
    }
  },

  createLocatie: async (data) => {
    try {
      const response = await api.post('/api/locatii', data);
      return response.data;
    } catch (error) {
      handleApiError(error, 'Eroare la crearea locației');
    }
  },

  updateLocatie: async (id, data) => {
    try {
      const response = await api.patch(`/api/locatii/${id}`, data);
      return response.data;
    } catch (error) {
      handleApiError(error, 'Eroare la actualizarea locației');
    }
  },

  // Toggle Active/Inactive
  toggleLocatieStatus: async (id) => {
    try {
      await api.delete(`/api/locatii/${id}`);
    } catch (error) {
      handleApiError(error, 'Eroare la schimbarea statusului locației');
    }
  },

  // --- SERVICII ---
  getAllServiciiAdmin: async () => {
    try {
      const response = await api.get('/api/servicii/admin');
      return response.data;
    } catch (error) {
      handleApiError(error, 'Eroare la încărcarea serviciilor');
    }
  },

  createServiciu: async (data) => {
    try {
      const response = await api.post('/api/servicii', data);
      return response.data;
    } catch (error) {
      handleApiError(error, 'Eroare la crearea serviciului');
    }
  },

  updateServiciu: async (id, data) => {
    try {
      const response = await api.put(`/api/servicii/${id}`, data);
      return response.data;
    } catch (error) {
      handleApiError(error, 'Eroare la actualizarea serviciului');
    }
  },

  toggleServiciuStatus: async (id) => {
    try {
      const response = await api.patch(`/api/servicii/${id}/active`);
      return response.data;
    } catch (error) {
      handleApiError(error, 'Eroare la schimbarea statusului serviciului');
    }
  },

  // --- TIPURI SERVICII ---
  getAllTipuriServicii: async () => {
    try {
      const response = await api.get('/api/servicii/tipuri');
      return response.data;
    } catch (error) {
      handleApiError(error, 'Eroare la încărcarea tipurilor de servicii');
    }
  },

  createTipServiciu: async (data) => {
    try {
      const response = await api.post('/api/servicii/tipuri', data);
      return response.data;
    } catch (error) {
      handleApiError(error, 'Eroare la crearea tipului de serviciu');
    }
  },

  updateTipServiciu: async (id, data) => {
    try {
      const response = await api.put(`/api/servicii/tipuri/${id}`, data);
      return response.data;
    } catch (error) {
      handleApiError(error, 'Eroare la actualizarea tipului de serviciu');
    }
  },

  toggleTipServiciuStatus: async (id) => {
    try {
      const response = await api.patch(`/api/servicii/tipuri/${id}/active`);
      return response.data;
    } catch (error) {
      handleApiError(error, 'Eroare la schimbarea statusului tipului de serviciu');
    }
  }
};