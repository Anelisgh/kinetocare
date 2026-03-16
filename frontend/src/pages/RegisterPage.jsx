import { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import { authService } from '../services/authService';
import '../styles/auth.css';

export default function RegisterPage() {
  const [formData, setFormData] = useState({
    nume: '',
    prenume: '',
    gen: '',
    telefon: '',
    email: '',
    password: '',
    role: '',
  });
  
  const [errors, setErrors] = useState({});
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [successMessage, setSuccessMessage] = useState('');
  const navigate = useNavigate();

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData(prev => ({ ...prev, [name]: value }));
    
    // daca campul are o eroare o afiseaza
    if (errors[name]) {
      setErrors(prev => ({ ...prev, [name]: '' }));
    }
  };

  const validateForm = () => {
    // colectam erorile
    const newErrors = {};
    
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    if (!formData.email) {
      newErrors.email = 'Email-ul este obligatoriu';
    } else if (!emailRegex.test(formData.email)) {
      newErrors.email = 'Email invalid';
    }
  
    if (!formData.password) {
      newErrors.password = 'Parola este obligatorie';
    } else if (formData.password.length < 6) {
      newErrors.password = 'Parola trebuie să aibă minim 6 caractere';
    }
    
    if (!formData.nume.trim()) {
      newErrors.nume = 'Numele este obligatoriu';
    }
    
    if (!formData.prenume.trim()) {
      newErrors.prenume = 'Prenumele este obligatoriu';
    }

    if (!formData.gen) {
      newErrors.gen = 'Selectează genul';
    }
    
    const phoneRegex = /^07[0-9]{8}$/;
    if (!formData.telefon) {
      newErrors.telefon = 'Telefonul este obligatoriu';
    } else if (!phoneRegex.test(formData.telefon.replace(/\s/g, ''))) {
      newErrors.telefon = 'Telefon invalid (trebuie să aibă 10 cifre)';
    }

    if (!formData.role) {
      newErrors.role = 'Selectează un rol';
    }
    
    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    
    if (!validateForm()) {
      return;
    }
    
    setIsSubmitting(true);
    setErrors({});
    
    try {
      const response = await authService.register(formData);
      setSuccessMessage(response.message || 'Cont creat cu succes!');
      
      // dupa 2 secunde, redirectioneaza la login
      setTimeout(() => {
        navigate('/login');
      }, 2000);
      
    } catch (error) {
      if (error.eroriCampuri) {
        // Mapăm erorile primite de la backend peste starea de erori
        // Dacă există și un mesaj general, îl punem în 'submit'
        setErrors({
          ...error.eroriCampuri,
          submit: error.message
        });
      } else {
        setErrors({ submit: error.message });
      }
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
            Alătură-te platformei și descoperă o experiență modernă
            de management al terapiei — rapidă, sigură, inteligentă.
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
            <h1 className="auth-title">Creează-ți contul</h1>
            <p className="auth-subtitle">
              Completează datele de mai jos pentru a te înregistra.
              <br />
              Ai deja cont? <Link to="/login">Conectează-te</Link>
            </p>

            {successMessage && (
              <div className="success-message">{successMessage}</div>
            )}
            
            {errors.submit && (
              <div className="error-message">{errors.submit}</div>
            )}
            
            <form className="auth-form" onSubmit={handleSubmit}>
              {/* Nume + Prenume pe același rând */}
              <div className="auth-form-row">
                <div className={`auth-field-group ${errors.nume ? 'error-field' : ''}`}>
                  <label className="auth-field-label" htmlFor="reg-nume">Nume</label>
                  <div className="auth-input-wrapper">
                    <svg className="auth-input-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
                      <path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2" />
                      <circle cx="12" cy="7" r="4" />
                    </svg>
                    <input
                      className="auth-input"
                      id="reg-nume"
                      type="text"
                      name="nume"
                      placeholder="Popescu"
                      value={formData.nume}
                      onChange={handleChange}
                      disabled={isSubmitting}
                    />
                  </div>
                  {errors.nume && <small className="error-text">{errors.nume}</small>}
                </div>

                <div className={`auth-field-group ${errors.prenume ? 'error-field' : ''}`}>
                  <label className="auth-field-label" htmlFor="reg-prenume">Prenume</label>
                  <div className="auth-input-wrapper">
                    <svg className="auth-input-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
                      <path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2" />
                      <circle cx="12" cy="7" r="4" />
                    </svg>
                    <input
                      className="auth-input"
                      id="reg-prenume"
                      type="text"
                      name="prenume"
                      placeholder="Maria"
                      value={formData.prenume}
                      onChange={handleChange}
                      disabled={isSubmitting}
                    />
                  </div>
                  {errors.prenume && <small className="error-text">{errors.prenume}</small>}
                </div>
              </div>

              {/* Gen */}
              <div className={`auth-field-group ${errors.gen ? 'error-field' : ''}`}>
                <label className="auth-field-label" htmlFor="reg-gen">Gen</label>
                <select
                  className="auth-select no-icon"
                  id="reg-gen"
                  name="gen"
                  value={formData.gen}
                  onChange={handleChange}
                  disabled={isSubmitting}
                >
                  <option value="">Selectează genul</option>
                  <option value="MASCULIN">Masculin</option>
                  <option value="FEMININ">Feminin</option>
                </select>
                {errors.gen && <small className="error-text">{errors.gen}</small>}
              </div>

              {/* Telefon */}
              <div className={`auth-field-group ${errors.telefon ? 'error-field' : ''}`}>
                <label className="auth-field-label" htmlFor="reg-telefon">Telefon</label>
                <div className="auth-input-wrapper">
                  <svg className="auth-input-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
                    <path d="M22 16.92v3a2 2 0 0 1-2.18 2 19.79 19.79 0 0 1-8.63-3.07 19.5 19.5 0 0 1-6-6 19.79 19.79 0 0 1-3.07-8.67A2 2 0 0 1 4.11 2h3a2 2 0 0 1 2 1.72 12.84 12.84 0 0 0 .7 2.81 2 2 0 0 1-.45 2.11L8.09 9.91a16 16 0 0 0 6 6l1.27-1.27a2 2 0 0 1 2.11-.45 12.84 12.84 0 0 0 2.81.7A2 2 0 0 1 22 16.92z" />
                  </svg>
                  <input
                    className="auth-input"
                    id="reg-telefon"
                    type="tel"
                    name="telefon"
                    placeholder="0712 345 678"
                    value={formData.telefon}
                    onChange={handleChange}
                    disabled={isSubmitting}
                  />
                </div>
                {errors.telefon && <small className="error-text">{errors.telefon}</small>}
              </div>

              {/* Email */}
              <div className={`auth-field-group ${errors.email ? 'error-field' : ''}`}>
                <label className="auth-field-label" htmlFor="reg-email">Email</label>
                <div className="auth-input-wrapper">
                  <svg className="auth-input-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
                    <rect x="2" y="4" width="20" height="16" rx="2" />
                    <path d="m22 7-8.97 5.7a1.94 1.94 0 0 1-2.06 0L2 7" />
                  </svg>
                  <input
                    className="auth-input"
                    id="reg-email"
                    type="email"
                    name="email"
                    placeholder="maria@email.com"
                    value={formData.email}
                    onChange={handleChange}
                    disabled={isSubmitting}
                  />
                </div>
                {errors.email && <small className="error-text">{errors.email}</small>}
              </div>

              {/* Parolă */}
              <div className={`auth-field-group ${errors.password ? 'error-field' : ''}`}>
                <label className="auth-field-label" htmlFor="reg-password">Parolă</label>
                <div className="auth-input-wrapper">
                  <svg className="auth-input-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
                    <rect x="3" y="11" width="18" height="11" rx="2" ry="2" />
                    <path d="M7 11V7a5 5 0 0 1 10 0v4" />
                  </svg>
                  <input
                    className="auth-input"
                    id="reg-password"
                    type="password"
                    name="password"
                    placeholder="Minim 6 caractere"
                    value={formData.password}
                    onChange={handleChange}
                    disabled={isSubmitting}
                  />
                </div>
                {errors.password && <small className="error-text">{errors.password}</small>}
              </div>

              {/* Tip cont */}
              <div className={`auth-field-group ${errors.role ? 'error-field' : ''}`}>
                <label className="auth-field-label" htmlFor="reg-role">Tip cont</label>
                <select
                  className="auth-select no-icon"
                  id="reg-role"
                  name="role"
                  value={formData.role}
                  onChange={handleChange}
                  disabled={isSubmitting}
                >
                  <option value="">Selectează tipul de cont</option>
                  <option value="TERAPEUT">Terapeut</option>
                  <option value="PACIENT">Pacient</option>
                </select>
                {errors.role && <small className="error-text">{errors.role}</small>}
              </div>

              <button className="auth-btn" type="submit" disabled={isSubmitting}>
                {isSubmitting ? 'Se înregistrează...' : 'Creează contul'}
              </button>
            </form>
          </div>
        </div>
      </div>
    </div>
  );
}