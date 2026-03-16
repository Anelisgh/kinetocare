import { useState, useEffect } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import ForgotPasswordModal from '../components/shared/ForgotPasswordModal';
import '../styles/auth.css';

export default function LoginPage() {
    const [formData, setFormData] = useState({
        email: '',
        password: '',
    });
    
    const { isAuthenticated, login } = useAuth();
    
    const [error, setError] = useState('');
    const [isSubmitting, setIsSubmitting] = useState(false);
    const [showForgotPassword, setShowForgotPassword] = useState(false);
    const navigate = useNavigate();
    
    useEffect(() => {
        if (isAuthenticated) {
            navigate('/homepage', { replace: true });
        }
    }, [isAuthenticated, navigate]); 

    const handleChange = (e) => {
        const { name, value } = e.target;
        setFormData(prev => ({ ...prev, [name]: value }));
        setError('');
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        
        if (!formData.email || !formData.password) {
            setError('Completează toate câmpurile');
            return;
        }
        
        setIsSubmitting(true);
        setError('');
        
        try {
            await login(formData.email, formData.password); 
        } catch (err) {
            setError(err.message); 
        } finally {
            setIsSubmitting(false);
        }
    };

  return (
    <div className="auth-page">
      {/* ── Left: Branding Panel ── */}
      <div className="auth-brand-panel">
        <div className="auth-brand-content">
          <img
            src="/images/Logo.png"
            alt="KinetoCare Logo"
            className="auth-brand-logo"
          />
          <p className="auth-brand-tagline">
            Platforma completă pentru gestionarea clinicii tale de kinetoterapie.
            Programări, evaluări și comunicare — totul într-un singur loc.
          </p>
          <hr className="auth-brand-separator" />
        </div>

        {/* Hero Image la bază cu fade (gestionat din CSS) */}
        <div className="auth-brand-image-wrapper">
          <img src="/images/auth-hero.png" alt="Recuperare și Progres" />
        </div>

        <p className="auth-brand-footer">Recuperare · Încredere · Progres</p>
      </div>

      {/* ── Right: Form Panel ── */}
      <div className="auth-form-panel">
        <div className="auth-form-wrapper">
          <div className="auth-container">
            <h1 className="auth-title">Bine ai revenit!</h1>
            <p className="auth-subtitle">
              Conectează-te la contul tău KinetoCare.
              <br />
              Nu ai cont? <Link to="/register">Înregistrează-te</Link>
            </p>
            
            {error && <div className="error-message">{error}</div>}
            
            <form className="auth-form" onSubmit={handleSubmit}>
              <div className="auth-field-group">
                <label className="auth-field-label" htmlFor="login-email">Email</label>
                <div className="auth-input-wrapper">
                  <svg className="auth-input-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
                    <rect x="2" y="4" width="20" height="16" rx="2" />
                    <path d="m22 7-8.97 5.7a1.94 1.94 0 0 1-2.06 0L2 7" />
                  </svg>
                  <input
                    className="auth-input"
                    id="login-email"
                    type="email"
                    name="email"
                    placeholder="exemplu@email.com"
                    value={formData.email}
                    onChange={handleChange}
                    disabled={isSubmitting}
                    required
                  />
                </div>
              </div>

              <div className="auth-field-group">
                <label className="auth-field-label" htmlFor="login-password">Parolă</label>
                <div className="auth-input-wrapper">
                  <svg className="auth-input-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
                    <rect x="3" y="11" width="18" height="11" rx="2" ry="2" />
                    <path d="M7 11V7a5 5 0 0 1 10 0v4" />
                  </svg>
                  <input
                    className="auth-input"
                    id="login-password"
                    type="password"
                    name="password"
                    placeholder="Introdu parola"
                    value={formData.password}
                    onChange={handleChange}
                    disabled={isSubmitting}
                    required
                  />
                </div>
              </div>

              <button className="auth-btn" type="submit" disabled={isSubmitting}>
                {isSubmitting ? 'Se conectează...' : 'Conectează-te'}
              </button>
            </form>

            <div className="auth-links">
              <button
                type="button"
                className="auth-link"
                onClick={() => setShowForgotPassword(true)}
              >
                Am uitat parola
              </button>
            </div>
          </div>
        </div>
      </div>

      {showForgotPassword && (
        <ForgotPasswordModal onClose={() => setShowForgotPassword(false)} />
      )}
    </div>
  );
}