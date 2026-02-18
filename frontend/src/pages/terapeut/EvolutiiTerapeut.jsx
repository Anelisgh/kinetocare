import React, { useState, useEffect } from 'react';
import { profileService } from '../../services/profileService';
import { evaluariService } from '../../services/evaluariService';
import { evolutiiService } from '../../services/evolutiiService';
import '../../styles/evolutiiTerapeut.css';

const EvolutiiTerapeut = () => {
    const [terapeutId, setTerapeutId] = useState(null);
    const [pacienti, setPacienti] = useState([]);

    // Stare formular
    const [selectedPacientId, setSelectedPacientId] = useState('');
    const [observatii, setObservatii] = useState('');

    // Stare date
    const [istoric, setIstoric] = useState([]);
    const [loading, setLoading] = useState(true);
    const [message, setMessage] = useState(null);

    // 1. Inițializare (Terapeut + Dropdown Pacienți)
    useEffect(() => {
        const initData = async () => {
            try {
                const profile = await profileService.getProfile();
                const tId = profile.terapeutId || profile.id;
                setTerapeutId(tId);

                // Refolosim endpoint-ul de la evaluări pentru a lua pacienții cu care a lucrat
                const pacientiList = await evaluariService.getPacientiRecenti(tId);
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
        if (!selectedPacientId || !terapeutId) {
            setIstoric([]);
            return;
        }

        const fetchIstoric = async () => {
            try {
                const data = await evolutiiService.getIstoric(selectedPacientId, terapeutId);
                setIstoric(data);
            } catch (err) {
                console.error(err);
            }
        };
        fetchIstoric();
    }, [selectedPacientId, terapeutId]);

    // 3. Submit
    const handleSubmit = async (e) => {
        e.preventDefault();
        if (!selectedPacientId || !observatii.trim()) return;

        try {
            await evolutiiService.addEvolutie({
                terapeutId,
                pacientId: selectedPacientId,
                observatii
            });

            setMessage({ type: 'success', text: 'Notă adăugată cu succes!' });
            setObservatii('');

            // Refresh la listă
            const updatedIstoric = await evolutiiService.getIstoric(selectedPacientId, terapeutId);
            setIstoric(updatedIstoric);

            // Șterge mesajul după 3 secunde
            setTimeout(() => setMessage(null), 3000);

        } catch (err) {
            setMessage({ type: 'error', text: err.message });
        }
    };

    // Get selected patient name for header
    const getSelectedPatientName = () => {
        const pacient = pacienti.find(p => p.id === Number(selectedPacientId));
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
                            value={selectedPacientId}
                            onChange={(e) => setSelectedPacientId(e.target.value)}
                            className="evolutii-select"
                        >
                            <option value="">Alege Pacient</option>
                            {pacienti.map(p => (
                                <option key={p.id} value={p.id}>{p.nume} {p.prenume}</option>
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
                                disabled={!selectedPacientId}
                            ></textarea>
                            <button
                                type="submit"
                                disabled={!selectedPacientId || !observatii.trim()}
                                className="evolutii-submit-btn"
                            >
                                Adaugă Notă
                            </button>
                        </form>
                    </div>
                </div>

                {/* ISTORIC */}
                <div className="evolutii-istoric-section">
                    <h3 className="evolutii-istoric-header">
                        Istoric {getSelectedPatientName()}
                    </h3>

                    {!selectedPacientId ? (
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