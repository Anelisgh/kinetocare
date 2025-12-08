import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import api from '../../services/api';

export default function CompleteProfile() {
  const navigate = useNavigate();
  const [formData, setFormData] = useState({
    dataNasterii: '',
    cnp: '',
    faceSport: 'NU',
    detaliiSport: '',
  });
  const [error, setError] = useState('');
  const [isSubmitting, setIsSubmitting] = useState(false);

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData(prev => ({
      ...prev,
      [name]: value
    }));
    setError('');
  };

  const validateCNP = (cnp) => {
    if (cnp.length !== 13) return false;
    if (!/^\d+$/.test(cnp)) return false;
    return true;
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');

    if (!validateCNP(formData.cnp)) {
      setError('CNP-ul trebuie să aibă exact 13 cifre');
      return;
    }

    if (!formData.dataNasterii) {
      setError('Data nașterii este obligatorie');
      return;
    }

    if (formData.faceSport === 'DA' && !formData.detaliiSport.trim()) {
      setError('Te rugăm să specifici ce sport practici');
      return;
    }

    setIsSubmitting(true);

    try {
      await api.patch('/api/profile', formData);
      navigate('/pacient/homepage', { replace: true });
    } catch (err) {
      console.error('Eroare la completarea profilului:', err);
      setError(err.response?.data?.message || 'Nu s-a putut salva profilul. Încearcă din nou.');
    } finally {
      setIsSubmitting(false);
    }
  };

  return (
    <div className="complete-profile-container">
      <div className="complete-profile-card">
        <h1>Bun venit! 👋</h1>
        
        <p className="welcome-text">
          Pentru a putea continua, te rugăm să completezi câteva informații esențiale despre tine. 
          Aceste date ne ajută să îți oferim cele mai bune servicii de kinetoterapie.
        </p>

        {error && (
          <div className="error-message">{error}</div>
        )}

        <form onSubmit={handleSubmit} className="complete-profile-form">
          <div className="form-group">
            <label htmlFor="dataNasterii">Data nașterii *</label>
            <input
              type="date"
              id="dataNasterii"
              name="dataNasterii"
              value={formData.dataNasterii}
              onChange={handleChange}
              max={new Date().toISOString().split('T')[0]}
              required
            />
          </div>

          <div className="form-group">
            <label htmlFor="cnp">CNP (Cod Numeric Personal) *</label>
            <input
              type="text"
              id="cnp"
              name="cnp"
              value={formData.cnp}
              onChange={handleChange}
              maxLength={13}
              required
              placeholder="1234567890123"
            />
            <small>Introdu cele 13 cifre ale CNP-ului tău</small>
          </div>

          <div className="form-group">
            <label htmlFor="faceSport">Practici vreun sport? *</label>
            <select
              id="faceSport"
              name="faceSport"
              value={formData.faceSport}
              onChange={handleChange}
              required
            >
              <option value="NU">Nu</option>
              <option value="DA">Da</option>
            </select>
          </div>

          {formData.faceSport === 'DA' && (
            <div className="form-group">
              <label htmlFor="detaliiSport">Ce sport practici? *</label>
              <textarea
                id="detaliiSport"
                name="detaliiSport"
                value={formData.detaliiSport}
                onChange={handleChange}
                maxLength={500}
                rows={4}
                placeholder="Ex: Fotbal - 2 ori pe săptămână, Alergare - dimineața..."
              />
              <small>{formData.detaliiSport.length}/500 caractere</small>
            </div>
          )}

          <button type="submit" disabled={isSubmitting} className="btn-submit">
            {isSubmitting ? 'Se salvează...' : 'Completează profilul'}
          </button>
        </form>

        <p className="required-note">* Câmpuri obligatorii</p>
      </div>
    </div>
  );
}