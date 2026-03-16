import { useState } from 'react';
import { profileService } from '../../services/profileService';

// Componenta reutilizabila pentru schimbarea parolei (logat)
// Folosita in ProfilPacient.jsx si ProfilTerapeut.jsx
export default function SchimbareParola() {
    const [formData, setFormData] = useState({
        parolaNoua: '',
        confirmaParola: '',
    });
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState('');
    const [success, setSuccess] = useState('');

    const handleChange = (e) => {
        const { name, value } = e.target;
        setFormData(prev => ({ ...prev, [name]: value }));
        setError('');
        setSuccess('');
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        setError('');
        setSuccess('');

        // Validare client
        if (formData.parolaNoua.length < 6) {
            setError('Parola trebuie să aibă minim 6 caractere.');
            return;
        }
        if (formData.parolaNoua !== formData.confirmaParola) {
            setError('Parolele nu coincid.');
            return;
        }

        try {
            setLoading(true);
            await profileService.changePassword(formData.parolaNoua);
            setSuccess('Parola a fost schimbată cu succes!');
            setFormData({ parolaNoua: '', confirmaParola: '' });
        } catch (err) {
            setError(err?.message || 'A apărut o eroare. Încearcă din nou.');
        } finally {
            setLoading(false);
        }
    };

    return (
        <form onSubmit={handleSubmit} className="profil-form">
            <div className="form-section">
                <h2>Schimbare Parolă</h2>

                {error && <div className="error-message">{error}</div>}
                {success && <div className="success-message">{success}</div>}

                <div className="form-group">
                    <label htmlFor="parolaNoua">Parolă Nouă *</label>
                    <input
                        type="password"
                        id="parolaNoua"
                        name="parolaNoua"
                        value={formData.parolaNoua}
                        onChange={handleChange}
                        minLength={6}
                        placeholder="Minim 6 caractere"
                        required
                        disabled={loading}
                    />
                </div>

                <div className="form-group">
                    <label htmlFor="confirmaParola">Confirmă Parola *</label>
                    <input
                        type="password"
                        id="confirmaParola"
                        name="confirmaParola"
                        value={formData.confirmaParola}
                        onChange={handleChange}
                        minLength={8}
                        placeholder="Repetă parola nouă"
                        required
                        disabled={loading}
                    />
                </div>

                <div className="form-actions">
                    <button
                        type="submit"
                        className="btn-save"
                        disabled={loading || !formData.parolaNoua || !formData.confirmaParola}
                    >
                        {loading ? 'Se salvează...' : 'Schimbă Parola'}
                    </button>
                </div>
            </div>
        </form>
    );
}
