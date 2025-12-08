import { useState, useEffect } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import '../styles/auth.css';

export default function LoginPage() {
    const [formData, setFormData] = useState({
        email: '',
        password: '',
    });
    
    const { isAuthenticated, login } = useAuth();
    
    const [error, setError] = useState('');
    const [isSubmitting, setIsSubmitting] = useState(false);
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
    <div className="auth-container">
      <h1 className="auth-title">Conectează-te</h1>
      
      {error && <div className="error-message">{error}</div>}
      
      <form className="auth-form" onSubmit={handleSubmit}>
        <input
          className="auth-input"
          type="email"
          name="email"
          placeholder="Email"
          value={formData.email}
          onChange={handleChange}
          disabled={isSubmitting}
          required
        />
        
        <input
          className="auth-input"
          type="password"
          name="password"
          placeholder="Parolă"
          value={formData.password}
          onChange={handleChange}
          disabled={isSubmitting}
          required
        />
        
        <button className="auth-btn" type="submit" disabled={isSubmitting}>
          {isSubmitting ? 'Se conectează...' : 'Conectează-te'}
        </button>
      </form>
      
      <Link className="auth-link" to="/register">Înregistrează-te</Link>
    </div>
  );
}