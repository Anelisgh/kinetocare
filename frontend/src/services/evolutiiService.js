import api, { handleApiError } from './api';

export const evolutiiService = {
    // Adaugare evolutie
    addEvolutie: async (data) => {
        try {
            const response = await api.post('/api/evolutii', data);
            return response.data;
        } catch (error) {
            handleApiError(error, 'Nu s-a putut salva evoluția.');
        }
    },

    // Incarcare istoric pentru un pacient
    getIstoric: async (pacientId, terapeutId) => {
        try {
            const response = await api.get(`/api/evolutii?pacientId=${pacientId}&terapeutId=${terapeutId}`);
            return response.data;
        } catch (error) {
            handleApiError(error, 'Nu s-a putut încărca istoricul.');
        }
    }
};