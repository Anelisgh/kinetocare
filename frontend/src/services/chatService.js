import api from './api';
import { handleApiError } from './api';

const chatService = {
  obtineConversatii: async (userKeycloakId, tipUser) => {
    try {
      const response = await api.get('/api/chat/conversatii', {
        params: { userKeycloakId, tipUser }
      });
      return response.data;
    } catch (error) {
      handleApiError(error, 'Eroare la preluarea conversațiilor');
    }
  },

  getConversatiiAgregate: async (userKeycloakId, tipUser) => {
    try {
      const response = await api.get('/api/chat/conversatii/agregat', {
        params: { userKeycloakId, tipUser }
      });
      return response.data;
    } catch (error) {
      handleApiError(error, 'Eroare la obținerea conversațiilor agregate');
      throw error;
    }
  },

  creeazaSauObtineConversatie: async (pacientKeycloakId, terapeutKeycloakId) => {
    try {
      const response = await api.post('/api/chat/conversatii', null, {
        params: { pacientKeycloakId, terapeutKeycloakId }
      });
      return response.data;
    } catch (error) {
      handleApiError(error, 'Eroare la crearea sau obținerea conversației');
    }
  },

  obtineMesajeDinConversatie: async (conversatieId) => {
    try {
      const response = await api.get(`/api/chat/conversatii/${conversatieId}/mesaje`);
      return response.data;
    } catch (error) {
      handleApiError(error, 'Eroare la preluarea mesajelor');
    }
  },

  marcheazaMesajeleCaCitite: async (conversatieId, userKeycloakId, tipUser) => {
    try {
      await api.put(`/api/chat/conversatii/${conversatieId}/citit`, null, {
        params: { userKeycloakId, tipUser }
      });
    } catch (error) {
      handleApiError(error, 'Eroare la marcarea mesajelor ca citite');
    }
  }
};

export default chatService;
