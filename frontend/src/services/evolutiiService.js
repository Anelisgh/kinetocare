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

    // Editare evolutie
    updateEvolutie: async (id, data) => {
        try {
            const response = await api.put(`/api/evolutii/${id}`, data);
            return response.data;
        } catch (error) {
            handleApiError(error, 'Nu s-a putut actualiza evoluția.');
        }
    },

    // Incarcare istoric pentru un pacient
    getIstoric: async (pacientKeycloakId, terapeutKeycloakId) => {
        try {
            const params = new URLSearchParams();
            params.append('pacientKeycloakId', pacientKeycloakId);
            if (terapeutKeycloakId) {
                params.append('terapeutKeycloakId', terapeutKeycloakId);
            }
            
            const response = await api.get(`/api/evolutii?${params.toString()}`);
            return response.data;
        } catch (error) {
            handleApiError(error, 'Nu s-a putut încărca istoricul.');
        }
    }
};