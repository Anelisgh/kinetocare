import api, { handleApiError } from './api';

export const notificariService = {
  // lista notificarile unui user
  getNotificari: async (userKeycloakId, tipUser) => {
    try {
      const response = await api.get('/api/notificari', {
        params: { userKeycloakId, tipUser }
      });
      return response.data;
    } catch (error) {
      handleApiError(error, 'Eroare la încărcarea notificărilor');
    }
  },

  // marcheaza o notificare ca citita
  marcheazaCitita: async (notificareId) => {
    try {
      await api.put(`/api/notificari/${notificareId}/citita`);
    } catch (error) {
      handleApiError(error, 'Eroare la marcarea notificării');
    }
  },

  // numarul de notificari necitite
  getNumarNecitite: async (userKeycloakId, tipUser) => {
    try {
      const response = await api.get('/api/notificari/necitite/count', {
        params: { userKeycloakId, tipUser }
      });
      return response.data.count;
    } catch (error) {
      handleApiError(error, 'Eroare la verificarea notificărilor');
    }
  },

  // marcheaza toate notificarile ca citite
  marcheazaToateCitite: async (userKeycloakId, tipUser) => {
    try {
      await api.put('/api/notificari/citite-toate', null, {
        params: { userKeycloakId, tipUser }
      });
    } catch (error) {
      handleApiError(error, 'Eroare la marcarea notificărilor');
    }
  },
};
