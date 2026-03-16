import { useState } from 'react';
import { authService } from '../../services/authService';

// Modal pentru resetarea parolei pt useri neautentificati
// Folosit in LoginPage.jsx
export default function ForgotPasswordModal({ onClose }) {
    const [email, setEmail] = useState('');
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState('');
    const [success, setSuccess] = useState(false);

    const handleSubmit = async (e) => {
        e.preventDefault();
        setError('');

        if (!email) {
            setError('Introdu adresa de email.');
            return;
        }

        try {
            setLoading(true);
            await authService.forgotPassword(email);
            setSuccess(true);
        } catch (err) {
            // Afisam eroarea doar daca email-ul nu exista (404 ResourceNotFoundException)
            // Pentru alte erori, afisam mesaj generic
            if (err?.status === 404) {
                setError('Nu există niciun cont asociat acestui email.');
            } else {
                setError(err?.message || 'A apărut o eroare. Încearcă din nou.');
            }
        } finally {
            setLoading(false);
        }
    };

    return (
        // Overlay care inchide modalul la click in afara
        <div
            className="modal-overlay"
            onClick={(e) => { if (e.target === e.currentTarget) onClose(); }}
            role="dialog"
            aria-modal="true"
            aria-labelledby="forgot-password-title"
        >
            <div className="modal-container">
                <button
                    className="modal-close-btn"
                    onClick={onClose}
                    aria-label="Închide"
                >
                    ×
                </button>

                <h2 id="forgot-password-title">Am uitat parola</h2>

                {success ? (
                    // Mesaj de succes neutru din punct de vedere al securitatii
                    <div>
                        <div className="success-message">
                            Dacă adresa de email există în sistem, vei primi un link de resetare în câteva minute.
                            Verifică și dosarul Spam.
                        </div>
                        <button className="auth-btn" onClick={onClose} style={{ marginTop: '1rem' }}>
                            Închide
                        </button>
                    </div>
                ) : (
                    <form onSubmit={handleSubmit} className="auth-form">
                        <p style={{ marginBottom: '1rem', color: '#666' }}>
                            Introdu adresa de email asociată contului tău și îți vom trimite un link de resetare a parolei.
                        </p>

                        {error && <div className="error-message">{error}</div>}

                        <input
                            className="auth-input"
                            type="email"
                            id="forgot-email"
                            name="email"
                            placeholder="Adresa ta de email"
                            value={email}
                            onChange={(e) => { setEmail(e.target.value); setError(''); }}
                            disabled={loading}
                            required
                            autoFocus
                        />

                        <button
                            className="auth-btn"
                            type="submit"
                            disabled={loading || !email}
                        >
                            {loading ? 'Se trimite...' : 'Trimite link de resetare'}
                        </button>
                    </form>
                )}
            </div>
        </div>
    );
}
