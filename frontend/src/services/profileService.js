import api, { handleApiError } from './api';

export const profileService = {
  //  PROFIL 
  getProfile: async () => {
    try {
      const response = await api.get('/api/profile');
      return response.data;
    } catch (error) {
      handleApiError(error, 'Eroare la încărcarea profilului');
    }
  },

  updateProfile: async (data) => {
    try {
      const response = await api.patch('/api/profile', data);
      return response.data;
    } catch (error) {
      handleApiError(error, 'Eroare la actualizarea profilului');
    }
  },

  //  TERAPEUT - CAUTARE 
  searchTerapeuti: async (filters) => {
    try {
      const queryParams = new URLSearchParams();
      queryParams.append('specializare', filters.specializare);
      if (filters.judet) queryParams.append('judet', filters.judet);
      if (filters.oras) queryParams.append('oras', filters.oras);
      if (filters.locatieId) queryParams.append('locatieId', filters.locatieId);
      if (filters.gen) queryParams.append('gen', filters.gen);
      const response = await api.get(`/api/terapeut/search-terapeuti?${queryParams.toString()}`);
    
    return response.data;
    } catch (error) {
      handleApiError(error, 'Eroare la căutarea terapeuților');
    }
  },

  //  ALEGEREA TERAPEUTULUI
  chooseTerapeut: async (terapeutKeycloakId, locatieId) => {
    try {
      let url = `/api/terapeut/choose-terapeut/${terapeutKeycloakId}`;
      // fiind @RequestParam e necesar sa-l adaugam asa, altfel se pierde pe drum
      if (locatieId) {
          url += `?locatieId=${locatieId}`;
      }
      const response = await api.post(url);
      return response.data;
    } catch (error) {
      handleApiError(error, 'Eroare la alegerea terapeutului');
    }
  },

  removeTerapeut: async () => {
    try {
      const response = await api.delete('/api/terapeut/remove-terapeut');
      return response.data;
    } catch (error) {
      handleApiError(error, 'Eroare la ștergerea terapeutului');
    }
  },

  getMyTerapeut: async () => {
    try {
      const response = await api.get('/api/terapeut/my-terapeut');
      return response.data;
    } catch (error) {
      handleApiError(error, 'Eroare la încărcarea terapeutului');
    }
  },

  getTerapeutNumeDupaId: async (terapeutId) => {
    try {
      const response = await api.get(`/api/terapeut/nume-dupa-id/${terapeutId}`);
      return response.data;
    } catch (error) {
      handleApiError(error, 'Eroare la încărcarea numelui terapeutului');
    }
  },

  //  LOCAȚII 
  getLocatii: async () => {
    try {
      const response = await api.get('/api/locatii');
      return response.data;
    } catch (error) {
      handleApiError(error, 'Eroare la încărcarea locațiilor');
    }
  },

  //  DISPONIBILITATE (pentru terapeuti) 
  getDisponibilitati: async () => {
    try {
      const response = await api.get('/api/disponibilitate');
      return response.data;
    } catch (error) {
      handleApiError(error, 'Eroare la încărcarea disponibilităților');
    }
  },

  addDisponibilitate: async (data) => {
    try {
      const response = await api.post('/api/disponibilitate', data);
      return response.data;
    } catch (error) {
      handleApiError(error, 'Eroare la adăugarea disponibilității');
    }
  },

  deleteDisponibilitate: async (id) => {
    try {
      await api.delete(`/api/disponibilitate/${id}`);
    } catch (error) {
      handleApiError(error, 'Eroare la ștergerea disponibilității');
    }
  },

  // CONCEDII (pentru terapeuti)
  getConcedii: async () => {
    try {
      const response = await api.get('/api/concediu');
      return response.data;
    } catch (error) {
      handleApiError(error, 'Eroare la încărcarea concediilor');
    }
  },

  addConcediu: async (data) => {
    try {
      const response = await api.post('/api/concediu', data);
      return response.data;
    } catch (error) {
      handleApiError(error, 'Eroare la adăugarea concediului');
    }
  },

  deleteConcediu: async (id) => {
    try {
      await api.delete(`/api/concediu/${id}`);
    } catch (error) {
      handleApiError(error, 'Eroare la ștergerea concediului');
    }
  },
};