import React, { useState, useEffect } from 'react';
import { profileService } from '../../services/profileService';
import { evaluariService } from '../../services/evaluariService';
import '../../styles/evaluariTerapeut.css'

const EvaluariTerapeut = () => {
    const [terapeutId, setTerapeutId] = useState(null);
    const [pacienti, setPacienti] = useState([]);
    const [servicii, setServicii] = useState([]);

    const [formData, setFormData] = useState({
        pacientId: '',
        tip: 'INITIALA', // Default
        diagnostic: '',
        sedinteRecomandate: 10, // Default
        serviciuRecomandatId: '',
        observatii: ''
    });

    const [loading, setLoading] = useState(true);
    const [submitting, setSubmitting] = useState(false);
    const [message, setMessage] = useState({ type: '', text: '' }); // type: 'success' | 'error'

    // 1. Încărcare Inițială (Profil -> TerapeutID -> Pacienți & Servicii)
    useEffect(() => {
        const initData = async () => {
            try {
                setLoading(true);
                // A. Aflăm cine e terapeutul logat
                const profile = await profileService.getProfile();
                const tId = profile.terapeutId || profile.id;
                setTerapeutId(tId);

                if (!tId) throw new Error("Nu s-a găsit ID-ul terapeutului.");

                // B. Încărcăm listele necesare în paralel
                const [pacientiList, serviciiList] = await Promise.all([
                    evaluariService.getPacientiRecenti(tId),
                    evaluariService.getAllServicii()
                ]);

                setPacienti(pacientiList);
                // eliminam evaluarile din servicii
                const serviciiTratament = serviciiList.filter(s => !s.nume.toLowerCase().includes('evaluare'));
                setServicii(serviciiTratament);

                // Preselectăm primul serviciu dacă există
                if (serviciiList.length > 0) {
                    setFormData(prev => ({ ...prev, serviciuRecomandatId: serviciiList[0].id }));
                }

            } catch (err) {
                setMessage({ type: 'error', text: err.message });
            } finally {
                setLoading(false);
            }
        };

        initData();
    }, []);

    // 2. Handler schimbare input-uri
    const handleChange = (e) => {
        const { name, value } = e.target;
        setFormData(prev => ({
            ...prev,
            [name]: value
        }));
    };

    // 3. Handler Submit
    const handleSubmit = async (e) => {
        e.preventDefault();
        setMessage({ type: '', text: '' });

        // Validare simplă
        if (!formData.pacientId) {
            setMessage({ type: 'error', text: 'Te rog selectează un pacient.' });
            return;
        }
        if (!formData.diagnostic.trim()) {
            setMessage({ type: 'error', text: 'Diagnosticul este obligatoriu.' });
            return;
        }

        try {
            setSubmitting(true);

            const payload = {
                ...formData,
                terapeutId: terapeutId,
                // Convertim string-urile numerice din form în numere reale
                pacientId: Number(formData.pacientId),
                sedinteRecomandate: Number(formData.sedinteRecomandate),
                serviciuRecomandatId: Number(formData.serviciuRecomandatId),
                // programareId îl lăsăm null, se ocupă backend-ul (Magic Link)
            };

            await evaluariService.creeazaEvaluare(payload);

            setMessage({ type: 'success', text: 'Evaluarea a fost salvată cu succes!' });

            // Resetăm parțial formularul (păstrăm serviciul și tipul poate)
            setFormData(prev => ({
                ...prev,
                pacientId: '',
                diagnostic: '',
                observatii: '',
                sedinteRecomandate: 10
            }));

        } catch (err) {
            setMessage({ type: 'error', text: err.message });
        } finally {
            setSubmitting(false);
        }
    };

    if (loading) return <div className="evaluari-loading">Se încarcă datele...</div>;

    return (
        <div className="evaluari-terapeut-container">
            <div className="evaluari-card">
                <h1 className="evaluari-header">
                    Adaugă Evaluare Nouă
                </h1>

                {/* Mesaje de feedback */}
                {message.text && (
                    <div className={`evaluari-message ${message.type}`}>
                        {message.text}
                    </div>
                )}

                <form onSubmit={handleSubmit} className="evaluari-form">

                    {/* GRID: Pacient & Tip */}
                    <div className="evaluari-form-grid">
                        {/* Select Pacient */}
                        <div className="evaluari-form-group">
                            <label className="evaluari-label">Pacient</label>
                            <select
                                name="pacientId"
                                value={formData.pacientId}
                                onChange={handleChange}
                                className="evaluari-select"
                            >
                                <option value="">-- Selectează Pacient --</option>
                                {pacienti.map(p => (
                                    <option key={p.id} value={p.id}>
                                        {p.nume} {p.prenume}
                                    </option>
                                ))}
                            </select>
                            <p className="evaluari-hint">
                                Sunt afișați doar pacienții cu programări recente.
                            </p>
                        </div>

                        {/* Select Tip Evaluare */}
                        <div className="evaluari-form-group">
                            <label className="evaluari-label">Tip Evaluare</label>
                            <select
                                name="tip"
                                value={formData.tip}
                                onChange={handleChange}
                                className="evaluari-select"
                            >
                                <option value="INITIALA">Evaluare Inițială</option>
                                <option value="REEVALUARE">Reevaluare (Periodic)</option>
                            </select>
                        </div>
                    </div>

                    {/* Textarea Diagnostic */}
                    <div className="evaluari-form-group">
                        <label className="evaluari-label">Diagnostic & Concluzii</label>
                        <textarea
                            name="diagnostic"
                            value={formData.diagnostic}
                            onChange={handleChange}
                            rows="4"
                            placeholder="Descrie diagnosticul, simptomele și concluziile clinice..."
                            className="evaluari-textarea"
                        ></textarea>
                    </div>

                    {/* GRID: Plan Tratament */}
                    <div className="evaluari-tratament-section">
                        <h3 className="evaluari-tratament-title">Recomandări Tratament</h3>
                        <div className="evaluari-form-grid">

                            {/* Nr Sedinte */}
                            <div className="evaluari-form-group">
                                <label className="evaluari-label">Ședințe Recomandate</label>
                                <input
                                    type="number"
                                    name="sedinteRecomandate"
                                    value={formData.sedinteRecomandate}
                                    onChange={handleChange}
                                    min="1"
                                    max="50"
                                    className="evaluari-input"
                                />
                            </div>

                            {/* Serviciu Recomandat */}
                            <div className="evaluari-form-group">
                                <label className="evaluari-label">Tip Serviciu Recomandat</label>
                                <select
                                    name="serviciuRecomandatId"
                                    value={formData.serviciuRecomandatId}
                                    onChange={handleChange}
                                    className="evaluari-select"
                                >
                                    <option value="">-- Alege Serviciul --</option>
                                    {servicii.map(s => (
                                        <option key={s.id} value={s.id}>
                                            {s.nume} {s.durataMinute ? `(${s.durataMinute} min)` : ''}
                                        </option>
                                    ))}
                                </select>
                            </div>
                        </div>
                    </div>

                    {/* Textarea Observatii */}
                    <div className="evaluari-form-group">
                        <label className="evaluari-label">Alte Observații (Opțional)</label>
                        <textarea
                            name="observatii"
                            value={formData.observatii}
                            onChange={handleChange}
                            rows="2"
                            placeholder="Ex: Pacientul acuză dureri la efort, se recomandă pauză..."
                            className="evaluari-textarea"
                        ></textarea>
                    </div>

                    {/* Buton Submit */}
                    <div className="evaluari-submit-wrapper">
                        <button
                            type="submit"
                            disabled={submitting}
                            className={`evaluari-submit-btn ${submitting ? 'submitting' : ''}`}
                        >
                            {submitting ? 'Se salvează...' : 'Salvează Evaluarea'}
                        </button>
                    </div>

                </form>
            </div>
        </div>
    );
};

export default EvaluariTerapeut;