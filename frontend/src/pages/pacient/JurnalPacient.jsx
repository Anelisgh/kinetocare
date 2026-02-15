import React, { useState, useEffect } from 'react';
import { profileService } from '../../services/profileService';
import { jurnalService } from '../../services/jurnalService';
import { useAuth } from '../../context/AuthContext';
import '../../styles/jurnalPacient.css';

const JurnalPacient = () => {
    const { userInfo } = useAuth();
    const [pacientId, setPacientId] = useState(null);
    const [loading, setLoading] = useState(true);
    const [necompletate, setNecompletate] = useState([]);
    const [istoric, setIstoric] = useState([]);

    // Stare Formular
    const [selectedProgramareId, setSelectedProgramareId] = useState('');
    const [formData, setFormData] = useState({
        nivelDurere: 5,
        dificultateExercitii: 5,
        nivelOboseala: 5,
        comentarii: ''
    });

    const [message, setMessage] = useState({ type: '', text: '' });

    // Initializare - Luam ID-ul pacientului din profil
    useEffect(() => {
        const initData = async () => {
            try {
                const profile = await profileService.getProfile();
                const id = profile.id;
                setPacientId(id);
            } catch (err) {
                console.error('Eroare la încărcarea profilului:', err);
                setMessage({ type: 'error', text: 'Nu s-a putut încărca profilul.' });
                setLoading(false);
            }
        };
        initData();
    }, []);

    // Incarcare date jurnal cand avem pacientId -> lista de programari care au trecut, dar nu au feedback
    const fetchData = async (id) => {
        try {
            setLoading(true);
            const [listNecompletate, listIstoric] = await Promise.all([
                jurnalService.getSedinteNecompletate(id),
                jurnalService.getIstoric(id)
            ]);

            setNecompletate(listNecompletate || []);
            setIstoric(listIstoric || []);

            // Selectam automat prima sedinta daca exista
            if (listNecompletate && listNecompletate.length > 0 && !selectedProgramareId) {
                setSelectedProgramareId(listNecompletate[0].id);
            }
        } catch (err) {
            console.error('Eroare la încărcarea jurnalului:', err);
            setMessage({ type: 'error', text: err.message });
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        if (pacientId) {
            fetchData(pacientId);
        }
    }, [pacientId]);

    // Handler Submit
    const handleSubmit = async (e) => {
        e.preventDefault();
        if (!selectedProgramareId || !pacientId) return;

        try {
            await jurnalService.addJurnal(pacientId, {
                programareId: Number(selectedProgramareId),
                ...formData
            });

            setMessage({ type: 'success', text: 'Jurnal salvat cu succes!' });

            // Reset Form
            setFormData({ nivelDurere: 5, dificultateExercitii: 5, nivelOboseala: 5, comentarii: '' });
            setSelectedProgramareId('');

            // Refresh date
            fetchData(pacientId);

            // Ascunde mesajul dupa 3 sec
            setTimeout(() => setMessage({ type: '', text: '' }), 3000);

        } catch (err) {
            setMessage({ type: 'error', text: err.message });
        }
    };

    // Programarea curenta selectata (pentru afisare detalii read-only)
    const activeProgramare = necompletate.find(p => p.id === Number(selectedProgramareId));

    if (loading && necompletate.length === 0 && istoric.length === 0) {
        return <div className="p-10 text-center">Se încarcă jurnalul...</div>;
    }

    return (
        <div className="jurnal-container">
            <h1 className="page-title">Jurnal Recuperare</h1>

            {message.text && (
                <div className={`message-banner ${message.type}`}>
                    {message.text}
                </div>
            )}

            {/* --- ZONA 1: ADUGARE (Doar daca exista sedinte necompletate) --- */}
            {necompletate.length > 0 ? (
                <div className="jurnal-card form-card">
                    <h2 className="card-title">Jurnal Programare</h2>

                    {/* Dropdown selectie sedinta (daca sunt mai multe) */}
                    {necompletate.length > 1 && (
                        <div className="form-group">
                            <label>Alege sedinta pentru care completezi:</label>
                            <select
                                value={selectedProgramareId}
                                onChange={(e) => setSelectedProgramareId(e.target.value)}
                                className="form-select"
                            >
                                {necompletate.map(p => (
                                    <option key={p.id} value={p.id}>
                                        {new Date(p.data).toLocaleDateString('ro-RO')} - {p.tipServiciu}
                                    </option>
                                ))}
                            </select>
                        </div>
                    )}

                    <form onSubmit={handleSubmit}>
                        {/* SLIDERS */}
                        <div className="sliders-wrapper">

                            {/* 1. DURERE */}
                            <div className="slider-group">
                                <div className="slider-header">
                                    <label>Nivel Durere</label>
                                    <span className="slider-value value-red">{formData.nivelDurere}</span>
                                </div>
                                <input
                                    type="range" min="1" max="10"
                                    value={formData.nivelDurere}
                                    onChange={(e) => setFormData({ ...formData, nivelDurere: Number(e.target.value) })}
                                    className="slider-input slider-red"
                                />
                                <div className="slider-labels">
                                    <span>1 (Deloc)</span>
                                    <span>10 (Insuportabil)</span>
                                </div>
                            </div>

                            {/* 2. DIFICULTATE */}
                            <div className="slider-group">
                                <div className="slider-header">
                                    <label>Dificultate Exerciții</label>
                                    <span className="slider-value value-yellow">{formData.dificultateExercitii}</span>
                                </div>
                                <input
                                    type="range" min="1" max="10"
                                    value={formData.dificultateExercitii}
                                    onChange={(e) => setFormData({ ...formData, dificultateExercitii: Number(e.target.value) })}
                                    className="slider-input slider-yellow"
                                />
                                <div className="slider-labels">
                                    <span>1 (Ușor)</span>
                                    <span>10 (Foarte Greu)</span>
                                </div>
                            </div>

                            {/* 3. OBOSEALA */}
                            <div className="slider-group">
                                <div className="slider-header">
                                    <label>Nivel Oboseală</label>
                                    <span className="slider-value value-blue">{formData.nivelOboseala}</span>
                                </div>
                                <input
                                    type="range" min="1" max="10"
                                    value={formData.nivelOboseala}
                                    onChange={(e) => setFormData({ ...formData, nivelOboseala: Number(e.target.value) })}
                                    className="slider-input slider-blue"
                                />
                                <div className="slider-labels">
                                    <span>1 (Energic)</span>
                                    <span>10 (Epuizat)</span>
                                </div>
                            </div>
                        </div>

                        {/* COMENTARII */}
                        <div className="form-group mt-4">
                            <label>Comentarii (opțional)</label>
                            <textarea
                                rows="3"
                                value={formData.comentarii}
                                onChange={(e) => setFormData({ ...formData, comentarii: e.target.value })}
                                className="form-textarea"
                                placeholder="Cum te-ai simțit? Ai avut dureri specifice?"
                            />
                        </div>

                        {/* DETALII READ-ONLY */}
                        {activeProgramare && (
                            <div className="readonly-details">
                                <div className="detail-item">
                                    <span className="detail-label">Serviciu:</span>
                                    <span className="detail-content">{activeProgramare.tipServiciu}</span>
                                </div>
                                <div className="detail-item">
                                    <span className="detail-label">Terapeut:</span>
                                    <span className="detail-content">{activeProgramare.numeTerapeut}</span>
                                </div>
                                <div className="detail-item">
                                    <span className="detail-label">Data:</span>
                                    <span className="detail-content">
                                        {new Date(activeProgramare.data).toLocaleDateString('ro-RO')} - {activeProgramare.ora.substring(0, 5)}
                                    </span>
                                </div>
                                <div className="detail-item">
                                    <span className="detail-label">Locație:</span>
                                    <span className="detail-content">{activeProgramare.numeLocatie}</span>
                                </div>
                            </div>
                        )}

                        <button type="submit" className="btn-submit">Salvează Jurnal</button>
                    </form>
                </div>
            ) : (
                <div className="empty-state-card">
                    <h3>🎉 La zi!</h3>
                    <p>Ai completat jurnalul pentru toate ședințele finalizate.</p>
                </div>
            )}

            {/* --- ZONA 2: ISTORIC --- */}
            <div className="history-section">
                <h2 className="section-title">Istoric Jurnale</h2>
                {istoric.length === 0 ? (
                    <p className="text-gray-500">Nu ai completat niciun jurnal încă.</p>
                ) : (
                    <div className="history-list">
                        {istoric.map(item => (
                            <div key={item.id} className="history-card">
                                <div className="history-header">
                                    <span className="history-date">
                                        {item.dataJurnal ? new Date(item.dataJurnal).toLocaleDateString('ro-RO') : 'Dată necunoscută'}
                                    </span>
                                    <span className="history-service">{item.tipServiciu || 'Serviciu'}</span>
                                </div>

                                <div className="history-metrics">
                                    <div className="metric-badge metric-red">Durere: {item.nivelDurere}</div>
                                    <div className="metric-badge metric-yellow">Efort: {item.dificultateExercitii}</div>
                                    <div className="metric-badge metric-blue">Oboseală: {item.nivelOboseala}</div>
                                </div>

                                <div className="history-details">
                                    <small>Terapeut: {item.numeTerapeut}</small>
                                </div>

                                {item.comentarii && (
                                    <div className="history-comment">
                                        {item.comentarii}
                                    </div>
                                )}
                            </div>
                        ))}
                    </div>
                )}
            </div>
        </div>
    );
};

export default JurnalPacient;