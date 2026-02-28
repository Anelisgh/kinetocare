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
    <div className="auth-container">
      <h1 className="auth-title">Înregistrare</h1>
      
      {successMessage && (
        <div className="success-message">{successMessage}</div>
      )}
      
      {errors.submit && (
        <div className="error-message">{errors.submit}</div>
      )}
      
      <form className="auth-form" onSubmit={handleSubmit}>

        <div className={errors.nume ? 'error-field' : ''}>
          <input
            className="auth-input"
            type="text"
            name="nume"
            placeholder="Nume"
            value={formData.nume}
            onChange={handleChange}
            disabled={isSubmitting}
          />
          {errors.nume && <small className="error-text">{errors.nume}</small>}
        </div>

        <div className={errors.prenume ? 'error-field' : ''}>
          <input
            className="auth-input"
            type="text"
            name="prenume"
            placeholder="Prenume"
            value={formData.prenume}
            onChange={handleChange}
            disabled={isSubmitting}
          />
          {errors.prenume && <small className="error-text">{errors.prenume}</small>}
        </div>

        <div className={errors.gen ? 'error-field' : ''}>
          <select
            className="auth-select"
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

        <div className={errors.telefon ? 'error-field' : ''}>
          <input
            className="auth-input"
            type="tel"
            name="telefon"
            placeholder="Telefon (ex: 0712345678)"
            value={formData.telefon}
            onChange={handleChange}
            disabled={isSubmitting}
          />
          {errors.telefon && <small className="error-text">{errors.telefon}</small>}
        </div>

        <div className={errors.email ? 'error-field' : ''}>
          <input
            className="auth-input"
            type="email"
            name="email"
            placeholder="Email"
            value={formData.email}
            onChange={handleChange}
            disabled={isSubmitting}
          />
          {errors.email && <small className="error-text">{errors.email}</small>}
        </div>

        <div className={errors.password ? 'error-field' : ''}>
          <input
            className="auth-input"
            type="password"
            name="password"
            placeholder="Parolă"
            value={formData.password}
            onChange={handleChange}
            disabled={isSubmitting}
          />
          {errors.password && <small className="error-text">{errors.password}</small>}
        </div>

        <div className={errors.role ? 'error-field' : ''}>
          <select
            className="auth-select"
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
          {isSubmitting ? 'Se înregistrează...' : 'Înregistrează-te'}
        </button>
      </form>
      
      <Link className="auth-link" to="/login">Ai deja cont? Conectează-te</Link>
    </div>
  );
}