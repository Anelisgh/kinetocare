import api, { handleApiError } from './api';

export const evaluariService = {

    // 1. Obține lista de pacienți
    getPacientiRecenti: async (terapeutId) => {
        try {
            const response = await api.get(`/api/evaluari/pacienti-recenti?terapeutId=${terapeutId}`);
            return response.data;
        } catch (error) {
            handleApiError(error, 'Nu s-a putut încărca lista de pacienți.');
        }
    },

    // 2. Salvează evaluarea
    creeazaEvaluare: async (evaluareData) => {
        try {
            const response = await api.post('/api/evaluari', evaluareData);
            return response.data;
        } catch (error) {
            handleApiError(error, 'Nu s-a putut salva evaluarea.');
        }
    },

    // 3. Obține lista de servicii (MODIFICAT: Fără hardcodare)
    getAllServicii: async () => {
        try {
            // Frontend-ul cere asta prin Gateway
            const response = await api.get('/api/servicii');
            return response.data;
        } catch (error) {
            // Aici e modificarea: Nu mai returnăm lista falsă.
            // Aruncăm o eroare specifică pe care o prinde pagina EvaluariTerapeut.jsx
            console.error("Eroare la preluarea serviciilor:", error);
            throw new Error("Serviciul de Nomenclatoare este momentan indisponibil. Nu se pot încărca tipurile de servicii.");
        }
    }
};