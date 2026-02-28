import api, { handleApiError } from './api';

// PENTRU PACIENT
export const programariService = {
  createProgramare: async (data) => {
    try {
      const response = await api.post('/api/programari', data);
      return response.data;
    } catch (error) {
      handleApiError(error, 'Nu s-a putut crea programarea');
    }
  },

  getIstoricPacient: async (keycloakId) => {
    try {
      const response = await api.get(`/api/programari/pacient/by-keycloak/${keycloakId}/istoric`);
      return response.data;
    } catch (error) {
      handleApiError(error, 'Eroare la preluarea istoricului pacientului');
    }
  },

  // extrage serviciul recomandat de terapeut pentru pacient
  // keycloakId-ul pacientului e extras din JWT pe backend, nu e nevoie sa-l trimitem
  getServiciuRecomandat: async () => {
    try {
      const response = await api.get('/api/programari/serviciu-recomandat');
      return response.data;
    } catch (error) {
      handleApiError(error, 'Nu s-a putut determina serviciul necesar');
    }
  },

  // sloturi libere — terapeutKeycloakId in loc de terapeutId
  getDisponibilitate: async (terapeutKeycloakId, locatieId, dataStr, serviciuId) => {
    try {
      const response = await api.get('/api/programari/disponibilitate', {
        params: {
          terapeutKeycloakId,
          locatieId,
          data: dataStr,
          serviciuId
        }
      });
      return response.data;
    } catch (error) {
      handleApiError(error, 'Eroare la verificarea disponibilității');
    }
  },

  cancelProgramare: async (programareId) => {
    try {
      await api.patch(`/api/programari/${programareId}/cancel`);
    } catch (error) {
      handleApiError(error, 'Nu s-a putut anula programarea');
    }
  },

  // PENTRU TERAPEUT — extrage keycloakId-ul terapeutului din JWT pe backend, nu mai trebuie trimis
  getCalendarAppointments: async (startStr, endStr, locatieId = null) => {
    try {
      const params = new URLSearchParams();
      params.append('start', startStr); //YYYY-MM-DD
      params.append('end', endStr);     //YYYY-MM-DD

      if (locatieId) {
        params.append('locatieId', locatieId);
      }

      const response = await api.get(`/api/programari/calendar?${params.toString()}`);
      return response.data;
    } catch (error) {
      handleApiError(error, 'Eroare la încărcarea calendarului');
    }
  },

  // Anulare de terapeut — keycloakId-ul terapeutului e extras din JWT pe backend
  cancelProgramareTerapeut: async (programareId) => {
    try {
      await api.patch(`/api/programari/${programareId}/cancel-terapeut`);
    } catch (error) {
      handleApiError(error, 'Eroare la anularea programării');
    }
  },

  // Neprezentare — keycloakId-ul terapeutului e extras din JWT pe backend
  markNeprezentare: async (programareId) => {
    try {
      await api.patch(`/api/programari/${programareId}/neprezentare`);
    } catch (error) {
      handleApiError(error, 'Eroare la marcarea neprezentării');
    }
  },

  // FISA PACIENT - lista pacienti terapeut — keycloakId-ul terapeutului e extras din JWT pe backend
  getListaPacienti: async () => {
    try {
      const response = await api.get('/api/fisa-pacient/lista');
      return response.data;
    } catch (error) {
      handleApiError(error, 'Eroare la preluarea listei de pacienți');
    }
  },

  // FISA PACIENT - detalii pacient — ambele keycloakId-uri sunt trimise explicit
  getFisaPacient: async (pacientKeycloakId, terapeutKeycloakId) => {
    try {
      const response = await api.get(`/api/fisa-pacient/pacient/by-keycloak/${pacientKeycloakId}`, {
        params: { terapeutKeycloakId }
      });
      return response.data;
    } catch (error) {
      handleApiError(error, 'Eroare la preluarea fișei pacientului');
    }
  }

};