import api, { handleApiError } from './api';

export const evaluariService = {

    // Obtine lista de pacienti
    getPacientiRecenti: async (terapeutId) => {
        try {
            const response = await api.get(`/api/evaluari/pacienti-recenti?terapeutId=${terapeutId}`);
            return response.data;
        } catch (error) {
            handleApiError(error, 'Nu s-a putut încărca lista de pacienți.');
        }
    },

    // Salveaza evaluarea
    creeazaEvaluare: async (evaluareData) => {
        try {
            const response = await api.post('/api/evaluari', evaluareData);
            return response.data;
        } catch (error) {
            handleApiError(error, 'Nu s-a putut salva evaluarea.');
        }
    },

    // Editeaza evaluare
    updateEvaluare: async (id, data) => {
        try {
            const response = await api.put(`/api/evaluari/${id}`, data);
            return response.data;
        } catch (error) {
            handleApiError(error, 'Nu s-a putut actualiza evaluarea.');
        }
    },

    // Obtine lista de servicii
    getAllServicii: async () => {
        try {
            const response = await api.get('/api/servicii');
            return response.data;
        } catch (error) {
            console.error("Eroare la preluarea serviciilor:", error);
            throw new Error("Serviciul de Nomenclatoare este momentan indisponibil. Nu se pot încărca tipurile de servicii.");
        }
    }
};