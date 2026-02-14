import api, { handleApiError } from './api';

export const jurnalService = {

    // 1. Obține ședințele finalizate care nu au feedback
    getSedinteNecompletate: async (pacientId) => {
        try {
            const response = await api.get(`/api/programari/pacient/${pacientId}/necompletate`);
            return response.data; // List<ProgramareJurnalDTO>
        } catch (error) {
            handleApiError(error, 'Nu s-au putut încărca ședințele necompletate.');
        }
    },

    // 2. Salvează un jurnal nou
    addJurnal: async (pacientId, jurnalData) => {
        try {
            // jurnalData = { programareId, nivelDurere, dificultateExercitii, nivelOboseala, comentarii }
            await api.post(`/api/pacienti/${pacientId}/jurnal`, jurnalData);
        } catch (error) {
            handleApiError(error, 'Nu s-a putut salva jurnalul.');
        }
    },

    // 3. Obține istoricul jurnalelor
    getIstoric: async (pacientId) => {
        try {
            const response = await api.get(`/api/pacienti/${pacientId}/jurnal/istoric`);
            return response.data; // List<JurnalIstoricDTO>
        } catch (error) {
            handleApiError(error, 'Nu s-a putut încărca istoricul.');
        }
    }
};