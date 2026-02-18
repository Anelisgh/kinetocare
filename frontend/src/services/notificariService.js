import api, { handleApiError } from './api';

export const notificariService = {
  // lista notificarile unui user
  getNotificari: async (userId, tipUser) => {
    try {
      const response = await api.get('/api/notificari', {
        params: { userId, tipUser }
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
  getNumarNecitite: async (userId, tipUser) => {
    try {
      const response = await api.get('/api/notificari/necitite/count', {
        params: { userId, tipUser }
      });
      return response.data.count;
    } catch (error) {
      handleApiError(error, 'Eroare la verificarea notificărilor');
    }
  },

  // marcheaza toate notificarile ca citite
  marcheazaToateCitite: async (userId, tipUser) => {
    try {
      await api.put('/api/notificari/citite-toate', null, {
        params: { userId, tipUser }
      });
    } catch (error) {
      handleApiError(error, 'Eroare la marcarea notificărilor');
    }
  },
};
