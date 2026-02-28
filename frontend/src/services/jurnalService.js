import api, { handleApiError } from './api';

export const jurnalService = {

    // Obtine sedintele finalizate care nu au feedback
    getSedinteNecompletate: async () => {
        try {
            const response = await api.get('/api/programari/necompletate');
            return response.data;
        } catch (error) {
            handleApiError(error, 'Nu s-au putut încărca ședințele necompletate.');
        }
    },

    // Salveaza un jurnal nou
    addJurnal: async (jurnalData) => {
        try {
            // jurnalData = { programareId, nivelDurere, dificultateExercitii, nivelOboseala, comentarii }
            await api.post('/api/jurnal', jurnalData);
        } catch (error) {
            handleApiError(error, 'Nu s-a putut salva jurnalul.');
        }
    },

    // Editeaza un jurnal existent
    updateJurnal: async (jurnalId, data) => {
        try {
            await api.put(`/api/jurnal/${jurnalId}`, data);
        } catch (error) {
            handleApiError(error, 'Nu s-a putut actualiza jurnalul.');
        }
    },

    // Obtine istoricul jurnalelor
    getIstoric: async () => {
        try {
            const response = await api.get('/api/jurnal/istoric');
            return response.data;
        } catch (error) {
            handleApiError(error, 'Nu s-a putut încărca istoricul.');
        }
    }
};