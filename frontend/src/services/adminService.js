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
  },

  // --- ADMINISTRATE UTILIZATORI ---
  getAllUsers: async (role, active) => {
    try {
      // contruim query params
      const params = new URLSearchParams();
      if (role) params.append('role', role);
      if (active !== undefined && active !== null && active !== '') {
        params.append('active', active);
      }

      const response = await api.get(`/api/users?${params.toString()}`);
      return response.data;
    } catch (error) {
      handleApiError(error, 'Eroare la încărcarea utilizatorilor');
    }
  },

  toggleUserActive: async (userId) => {
    try {
      const response = await api.patch(`/api/users/${userId}/toggle-active`);
      return response.data;
    } catch (error) {
      handleApiError(error, 'Eroare la schimbarea statusului utilizatorului');
      throw error;
    }
  },

  // --- STATISTICI ---
  getProgramariLunare: async (startDate, endDate) => {
    try {
      const response = await api.get(`/api/programari/statistici/locatii/programari-lunar?startDate=${startDate}&endDate=${endDate}`);
      return response.data;
    } catch (error) {
      handleApiError(error, 'Eroare la încărcarea statisticilor programărilor');
    }
  },

  getVenituriLocatie: async (startDate, endDate) => {
    try {
      const response = await api.get(`/api/programari/statistici/locatii/venituri?startDate=${startDate}&endDate=${endDate}`);
      return response.data;
    } catch (error) {
      handleApiError(error, 'Eroare la încărcarea veniturilor');
    }
  },

  getRataAnulare: async (startDate, endDate) => {
    try {
      const response = await api.get(`/api/programari/statistici/locatii/rata-anulare?startDate=${startDate}&endDate=${endDate}`);
      return response.data;
    } catch (error) {
      handleApiError(error, 'Eroare la încărcarea ratei de anulare');
    }
  },

  getPacientiNoi: async (startDate, endDate) => {
    try {
      const response = await api.get(`/api/programari/statistici/locatii/pacienti-noi?startDate=${startDate}&endDate=${endDate}`);
      return response.data;
    } catch (error) {
      handleApiError(error, 'Eroare la încărcarea pacienților noi');
    }
  },

  getProgramariTerapeut: async (startDate, endDate) => {
    try {
      const response = await api.get(`/api/programari/statistici/terapeuti/programari?startDate=${startDate}&endDate=${endDate}`);
      return response.data;
    } catch (error) {
      handleApiError(error, 'Eroare la încărcarea statisticilor pe terapeuți');
    }
  },

  getTerapeutiActivi: async () => {
    try {
      const response = await api.get('/api/terapeut/statistici/locatii/terapeuti-activi');
      return response.data;
    } catch (error) {
      handleApiError(error, 'Eroare la încărcarea terapeuților activi');
    }
  }
};