import api from './api';

const handleError = (error, defaultMessage) => {
    console.error('API Error:', error);
    const msg = error.response?.data?.message || defaultMessage;
    throw new Error(msg);
};

export const evolutiiService = {
    // Adăugare notă
    addEvolutie: async (data) => {
        try {
            const response = await api.post('/api/evolutii', data);
            return response.data;
        } catch (error) {
            handleError(error, 'Nu s-a putut salva evoluția.');
        }
    },

    // Încărcare istoric pentru un pacient
    getIstoric: async (pacientId, terapeutId) => {
        try {
            const response = await api.get(`/api/evolutii?pacientId=${pacientId}&terapeutId=${terapeutId}`);
            return response.data;
        } catch (error) {
            handleError(error, 'Nu s-a putut încărca istoricul.');
        }
    }
};