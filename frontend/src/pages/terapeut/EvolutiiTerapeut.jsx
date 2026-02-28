import React, { useState, useEffect } from 'react';
import { profileService } from '../../services/profileService';
import { evaluariService } from '../../services/evaluariService';
import { evolutiiService } from '../../services/evolutiiService';
import '../../styles/evolutiiTerapeut.css';

const EvolutiiTerapeut = () => {
    const [pacienti, setPacienti] = useState([]);

    // Stare formular
    const [selectedPacientKeycloakId, setSelectedPacientKeycloakId] = useState('');
    const [selectedPacientId, setSelectedPacientId] = useState('');
    const [observatii, setObservatii] = useState('');

    // Stare date
    const [istoric, setIstoric] = useState([]);
    const [loading, setLoading] = useState(true);
    const [submitting, setSubmitting] = useState(false);
    const [message, setMessage] = useState(null);

    // 1. Inițializare (Pacienti dropdown)
    useEffect(() => {
        const initData = async () => {
            try {
                // keycloakId-ul terapeutului e extras din JWT pe backend
                const pacientiList = await evaluariService.getPacientiRecenti();
                setPacienti(pacientiList);
            } catch (err) {
                console.error(err);
                setMessage({ type: 'error', text: 'Nu s-au putut încărca datele inițiale.' });
            } finally {
                setLoading(false);
            }
        };
        initData();
    }, []);

    // 2. Încărcare Istoric când se selectează un pacient
    useEffect(() => {
        if (!selectedPacientKeycloakId) {
            setIstoric([]);
            return;
        }

        const fetchIstoric = async () => {
            try {
                const data = await evolutiiService.getIstoric(selectedPacientKeycloakId, null);
                setIstoric(data);
            } catch (err) {
                console.error(err);
            }
        };
        fetchIstoric();
    }, [selectedPacientKeycloakId]);

    // 3. Submit
    const handleSubmit = async (e) => {
        e.preventDefault(); // Prevent default form submission
        if (!selectedPacientKeycloakId || !observatii.trim()) return;

        try {
            setSubmitting(true);
            await evolutiiService.addEvolutie({
                pacientKeycloakId: selectedPacientKeycloakId,
                // terapeutKeycloakId e extras din JWT pe backend
                observatii
            });

            setMessage({ type: 'success', text: 'Notă adăugată cu succes!' });
            setObservatii('');

            // Refresh la listă (opțional, dar bun pentru UI)
            const updatedIstoric = await evolutiiService.getIstoric(selectedPacientKeycloakId, null);
            setIstoric(updatedIstoric);

            // Șterge mesajul după 3 secunde
            setTimeout(() => setMessage(null), 3000);

        } catch (err) {
            setMessage({ type: 'error', text: err.message });
        } finally {
            setSubmitting(false);
        }
    };

    // Get selected patient name for header
    const getSelectedPatientName = () => {
        const pacient = pacienti.find(p => p.keycloakId === selectedPacientKeycloakId);
        return pacient ? `- ${pacient.nume} ${pacient.prenume}` : '';
    };

    if (loading) return <div className="evolutii-loading">Se încarcă...</div>;

    return (
        <div className="evolutii-terapeut-container">
            <div className="evolutii-card">
                <h1 className="evolutii-header">
                    Evoluții și Notițe Pacienți
                </h1>

                {message && (
                    <div className={`evolutii-message ${message.type}`}>
                        {message.text}
                    </div>
                )}

                {/* FORMULAR */}
                <div className="evolutii-form-section">
                    <div className="evolutii-form-group">
                        <label className="evolutii-label">Selectează Pacient</label>
                        <select
                            value={selectedPacientKeycloakId}
                            onChange={(e) => {
                                const keycloakId = e.target.value;
                                setSelectedPacientKeycloakId(keycloakId);
                                const found = pacienti.find(p => p.keycloakId === keycloakId);
                                setSelectedPacientId(found?.id || '');
                            }}
                            className="evolutii-select"
                        >
                            <option value="">Alege Pacient</option>
                            {pacienti.map(p => (
                                <option key={p.keycloakId} value={p.keycloakId}>{p.nume} {p.prenume}</option>
                            ))}
                        </select>
                    </div>

                    <div className="evolutii-form-group">
                        <form onSubmit={handleSubmit}>
                            <label className="evolutii-label">Adaugă Notă</label>
                            <textarea
                                value={observatii}
                                onChange={(e) => setObservatii(e.target.value)}
                                rows="3"
                                placeholder="Scrie observațiile tale despre progresul pacientului..."
                                className="evolutii-textarea"
                                disabled={!selectedPacientKeycloakId}
                            ></textarea>
                            <button
                                type="submit"
                                disabled={!selectedPacientKeycloakId || !observatii.trim() || submitting}
                                className={`evolutii-submit-btn ${submitting ? 'submitting' : ''}`}
                            >
                                {submitting ? 'Se salvează...' : 'Adaugă Notă'}
                            </button>
                        </form>
                    </div>
                </div>

                {/* ISTORIC */}
                <div className="evolutii-istoric-section">
                    <h3 className="evolutii-istoric-header">
                        Istoric {getSelectedPatientName()}
                    </h3>

                    {!selectedPacientKeycloakId ? (
                        <p className="evolutii-empty-message">Selectează un pacient pentru a vedea istoricul.</p>
                    ) : istoric.length === 0 ? (
                        <p className="evolutii-empty-message">Nu există notițe anterioare pentru acest pacient.</p>
                    ) : (
                        <div className="evolutii-istoric-list">
                            {istoric.map((nota) => (
                                <div key={nota.id} className="evolutii-nota-card">
                                    <div className="evolutii-nota-date">
                                        {new Date(nota.createdAt).toLocaleDateString('ro-RO', {
                                            day: 'numeric', month: 'long', year: 'numeric', hour: '2-digit', minute: '2-digit'
                                        })}
                                    </div>
                                    <div className="evolutii-nota-content">
                                        {nota.observatii}
                                    </div>
                                </div>
                            ))}
                        </div>
                    )}
                </div>
            </div>
        </div>
    );
};

export default EvolutiiTerapeut;