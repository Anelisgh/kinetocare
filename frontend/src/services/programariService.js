import api from './api';

const handleError = (error, defaultMessage) => {
  console.error('API Error:', error);
  // extragem mesajul de eroare din backend
  const msg = error.response?.data?.message || error.response?.data?.error || defaultMessage;
  throw new Error(msg);
};
// PENTRU PACIENT
export const programariService = {
  createProgramare: async (data) => {
    try {
      //{ pacientId, terapeutId, locatieId, serviciuId, data, oraInceput }
      const response = await api.post('/api/programari', data);
      return response.data;
    } catch (error) {
      handleError(error, 'Nu s-a putut crea programarea');
    }
  },

  // extrage serviciul recomandat de terapeut pentru pacient
  getServiciuRecomandat: async (pacientId) => {
    try {
      const response = await api.get('/api/programari/serviciu-recomandat', {
        params: { pacientId }
      });
      return response.data; // { id, nume, durataMinute, pret }
    } catch (error) {
      handleError(error, 'Nu s-a putut determina serviciul necesar');
    }
  },

  // sloturi libere
  getDisponibilitate: async (terapeutId, locatieId, dataStr, serviciuId) => {
    try {
      const response = await api.get('/api/programari/disponibilitate', {
        params: {
          terapeutId,
          locatieId,
          data: dataStr,
          serviciuId
        }
      });
      return response.data;
    } catch (error) {
      handleError(error, 'Eroare la verificarea disponibilității');
    }
  },

  cancelProgramare: async (programareId) => {
    try {
      await api.patch(`/api/programari/${programareId}/cancel`);
    } catch (error) {
      handleError(error, 'Nu s-a putut anula programarea');
    }
  },

  // PENTRU TERAPEUT
  getCalendarAppointments: async (terapeutId, startStr, endStr, locatieId = null) => {
    try {
      const params = new URLSearchParams();
      params.append('terapeutId', terapeutId);
      params.append('start', startStr); //YYYY-MM-DD
      params.append('end', endStr);     //YYYY-MM-DD

      if (locatieId) {
        params.append('locatieId', locatieId);
      }

      const response = await api.get(`/api/programari/calendar?${params.toString()}`);
      return response.data;
    } catch (error) {
      console.error("Eroare la incarcarea calendarului:", error);
      throw error;
    }
  },

  cancelProgramareTerapeut: async (programareId, terapeutId) => {
    try {
      const params = new URLSearchParams();
      params.append('terapeutId', terapeutId);

      await api.patch(`/api/programari/${programareId}/cancel-terapeut?${params.toString()}`);
    } catch (error) {
      throw new Error(error.response?.data?.message || 'Eroare la anularea programării.');
    }
  },

  markNeprezentare: async (programareId, terapeutId) => {
    try {
      const params = new URLSearchParams();
      params.append('terapeutId', terapeutId);
      // Apelăm endpoint-ul de Neprezentare
      await api.patch(`/api/programari/${programareId}/neprezentare?${params.toString()}`);
    } catch (error) {
      // Extragem mesajul de eroare frumos
      throw new Error(error.response?.data?.message || 'Eroare la marcarea neprezentării.');
    }
  }

};