import api, { handleApiError } from './api';

export const jurnalService = {

    // Obtine sedintele finalizate care nu au feedback
    getSedinteNecompletate: async (pacientId) => {
        try {
            const response = await api.get(`/api/programari/pacient/${pacientId}/necompletate`);
            return response.data;
        } catch (error) {
            handleApiError(error, 'Nu s-au putut încărca ședințele necompletate.');
        }
    },

    // Salveaza un jurnal nou
    addJurnal: async (pacientId, jurnalData) => {
        try {
            // jurnalData = { programareId, nivelDurere, dificultateExercitii, nivelOboseala, comentarii }
            await api.post(`/api/jurnal/${pacientId}`, jurnalData);
        } catch (error) {
            handleApiError(error, 'Nu s-a putut salva jurnalul.');
        }
    },

    // Editeaza un jurnal existent
    updateJurnal: async (pacientId, jurnalId, data) => {
        try {
            await api.put(`/api/jurnal/${pacientId}/${jurnalId}`, data);
        } catch (error) {
            handleApiError(error, 'Nu s-a putut actualiza jurnalul.');
        }
    },

    // Obtine istoricul jurnalelor
    getIstoric: async (pacientId) => {
        try {
            const response = await api.get(`/api/jurnal/${pacientId}/istoric`);
            return response.data;
        } catch (error) {
            handleApiError(error, 'Nu s-a putut încărca istoricul.');
        }
    }
};