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

    // Stare editare jurnal existent
    const [editingJurnalId, setEditingJurnalId] = useState(null);
    const [editJurnalData, setEditJurnalData] = useState({});
    const [savingEdit, setSavingEdit] = useState(false);

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

    // Incarcare date jurnal cand avem pacientId
    const fetchData = async (id) => {
        try {
            setLoading(true);
            const [listNecompletate, listIstoric] = await Promise.all([
                jurnalService.getSedinteNecompletate(id),
                jurnalService.getIstoric(id)
            ]);

            setNecompletate(listNecompletate || []);
            setIstoric(listIstoric || []);

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
            setFormData({ nivelDurere: 5, dificultateExercitii: 5, nivelOboseala: 5, comentarii: '' });
            setSelectedProgramareId('');
            fetchData(pacientId);
            setTimeout(() => setMessage({ type: '', text: '' }), 3000);
        } catch (err) {
            setMessage({ type: 'error', text: err.message });
        }
    };

    // Editare jurnal existent
    const startEditJurnal = (j) => {
        setEditingJurnalId(j.id);
        setEditJurnalData({
            nivelDurere: j.nivelDurere || 5,
            dificultateExercitii: j.dificultateExercitii || 5,
            nivelOboseala: j.nivelOboseala || 5,
            comentarii: j.comentarii || ''
        });
    };

    const cancelEditJurnal = () => {
        setEditingJurnalId(null);
        setEditJurnalData({});
    };

    const saveEditJurnal = async (j) => {
        try {
            setSavingEdit(true);
            await jurnalService.updateJurnal(pacientId, j.id, editJurnalData);
            setMessage({ type: 'success', text: 'Jurnal actualizat!' });
            setEditingJurnalId(null);
            fetchData(pacientId);
            setTimeout(() => setMessage({ type: '', text: '' }), 3000);
        } catch (err) {
            setMessage({ type: 'error', text: err.message });
        } finally {
            setSavingEdit(false);
        }
    };

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

            {/* --- ZONA 1: ADAUGARE --- */}
            {necompletate.length > 0 ? (
                <div className="jurnal-card form-card">
                    <h2 className="card-title">Jurnal Programare</h2>

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
                        <div className="sliders-wrapper">
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

            {/* --- ZONA 2: ISTORIC JURNALE --- */}
            {istoric.length > 0 && (
                <div className="jurnal-istoric-section">
                    <h2 className="section-title">Istoric Jurnale</h2>
                    <div className="jurnal-istoric-list">
                        {istoric.map((j, idx) => {
                            const isEditing = editingJurnalId === j.id;

                            return (
                                <div key={j.id || idx} className={`jurnal-istoric-card ${isEditing ? 'editing' : ''}`}>
                                    <div className="jurnal-istoric-header">
                                        <div className="jurnal-istoric-date">
                                            <span className="jurnal-data">
                                                {j.dataJurnal ? new Date(j.dataJurnal).toLocaleDateString('ro-RO') : '—'}
                                            </span>
                                            {j.oraSedinta && (
                                                <span className="jurnal-ora-badge">Ora: {j.oraSedinta?.substring(0, 5)}</span>
                                            )}
                                        </div>
                                        {!isEditing && (
                                            <button className="jurnal-edit-btn" onClick={() => startEditJurnal(j)} title="Editează">
                                                ✏️
                                            </button>
                                        )}
                                    </div>

                                    {isEditing ? (
                                        <div className="jurnal-edit-form">
                                            <div className="slider-group compact">
                                                <div className="slider-header">
                                                    <label>Durere</label>
                                                    <span className="slider-value value-red">{editJurnalData.nivelDurere}</span>
                                                </div>
                                                <input type="range" min="1" max="10"
                                                    value={editJurnalData.nivelDurere}
                                                    onChange={(e) => setEditJurnalData({ ...editJurnalData, nivelDurere: Number(e.target.value) })}
                                                    className="slider-input slider-red"
                                                />
                                            </div>
                                            <div className="slider-group compact">
                                                <div className="slider-header">
                                                    <label>Dificultate</label>
                                                    <span className="slider-value value-yellow">{editJurnalData.dificultateExercitii}</span>
                                                </div>
                                                <input type="range" min="1" max="10"
                                                    value={editJurnalData.dificultateExercitii}
                                                    onChange={(e) => setEditJurnalData({ ...editJurnalData, dificultateExercitii: Number(e.target.value) })}
                                                    className="slider-input slider-yellow"
                                                />
                                            </div>
                                            <div className="slider-group compact">
                                                <div className="slider-header">
                                                    <label>Oboseală</label>
                                                    <span className="slider-value value-blue">{editJurnalData.nivelOboseala}</span>
                                                </div>
                                                <input type="range" min="1" max="10"
                                                    value={editJurnalData.nivelOboseala}
                                                    onChange={(e) => setEditJurnalData({ ...editJurnalData, nivelOboseala: Number(e.target.value) })}
                                                    className="slider-input slider-blue"
                                                />
                                            </div>
                                            <div className="form-group">
                                                <label>Comentarii</label>
                                                <textarea
                                                    rows="2"
                                                    value={editJurnalData.comentarii}
                                                    onChange={(e) => setEditJurnalData({ ...editJurnalData, comentarii: e.target.value })}
                                                    className="form-textarea"
                                                />
                                            </div>
                                            <div className="jurnal-edit-actions">
                                                <button className="btn-save-edit" onClick={() => saveEditJurnal(j)} disabled={savingEdit}>
                                                    {savingEdit ? 'Se salvează...' : '💾 Salvează'}
                                                </button>
                                                <button className="btn-cancel-edit" onClick={cancelEditJurnal}>
                                                    ✕ Anulează
                                                </button>
                                            </div>
                                        </div>
                                    ) : (
                                        <>
                                            <div className="jurnal-istoric-metrics">
                                                <div className="istoric-metric">
                                                    <span className="metric-label-sm">Durere</span>
                                                    <div className="metric-bar-sm-container">
                                                        <div className="metric-bar-sm durere" style={{ width: `${(j.nivelDurere || 0) * 10}%` }}></div>
                                                    </div>
                                                    <span className="metric-val">{j.nivelDurere}/10</span>
                                                </div>
                                                <div className="istoric-metric">
                                                    <span className="metric-label-sm">Dificultate</span>
                                                    <div className="metric-bar-sm-container">
                                                        <div className="metric-bar-sm dificultate" style={{ width: `${(j.dificultateExercitii || 0) * 10}%` }}></div>
                                                    </div>
                                                    <span className="metric-val">{j.dificultateExercitii}/10</span>
                                                </div>
                                                <div className="istoric-metric">
                                                    <span className="metric-label-sm">Oboseală</span>
                                                    <div className="metric-bar-sm-container">
                                                        <div className="metric-bar-sm oboseala" style={{ width: `${(j.nivelOboseala || 0) * 10}%` }}></div>
                                                    </div>
                                                    <span className="metric-val">{j.nivelOboseala}/10</span>
                                                </div>
                                            </div>
                                            {j.comentarii && (
                                                <div className="jurnal-istoric-comentarii">
                                                    <p>{j.comentarii}</p>
                                                </div>
                                            )}
                                        </>
                                    )}
                                </div>
                            );
                        })}
                    </div>
                </div>
            )}

        </div>
    );
};

export default JurnalPacient;