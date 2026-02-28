import { useState, useEffect } from 'react';
import { useAuth } from '../../context/AuthContext';
import { profileService } from '../../services/profileService';

import PozaProfil from '../../components/terapeut/profil/PozaProfil';
import ManagementDisponibilitate from '../../components/terapeut/profil/ManagementDisponibilitate';
import ManagementConcedii from '../../components/terapeut/profil/ManagementConcedii';
import '../../styles/profil.css';

// Pagina profilului terapeutului -> afiseaza profilul terapeutului cu date personale, disponibilitatea si concediile
export default function ProfilTerapeut() {
  const { userInfo } = useAuth();
  const [profile, setProfile] = useState(null);
  const [loading, setLoading] = useState(true);
  const [isEditing, setIsEditing] = useState(false);
  const [saving, setSaving] = useState(false);

  const [formData, setFormData] = useState({
    nume: '',
    prenume: '',
    email: '',
    telefon: '',
    gen: '',
    specializare: '',
    pozaProfil: '',
  });

  const [errors, setErrors] = useState({});
  const [successMessage, setSuccessMessage] = useState('');

  useEffect(() => {
    fetchProfile();
  }, []);

  const fetchProfile = async () => {
    try {
      setLoading(true);
      const data = await profileService.getProfile();
      setProfile(data);

      setFormData({
        nume: data.nume || '',
        prenume: data.prenume || '',
        email: data.email || '',
        telefon: data.telefon || '',
        gen: data.gen || '',
        specializare: data.specializare || '',
        pozaProfil: data.pozaProfil || '',
      });
    } catch (error) {
      console.error('Eroare la încărcarea profilului:', error);
      setErrors({ submit: 'Nu s-a putut încărca profilul. Încearcă din nou.' });
    } finally {
      setLoading(false);
    }
  };

  const handleChange = (e) => {
    const { name, value } = e.target;

    if (errors[name]) {
      setErrors(prev => ({ ...prev, [name]: '' }));
    }

    setFormData(prev => ({
      ...prev,
      [name]: value
    }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError(null);
    setSuccessMessage('');

    try {
      setSaving(true);
      const updatedProfile = await profileService.updateProfile(formData);
      setProfile(updatedProfile);
      setFormData(prev => ({ ...prev, pozaProfil: updatedProfile.pozaProfil }));

      setIsEditing(false);
      setSuccessMessage('Profil actualizat cu succes!');
      setTimeout(() => setSuccessMessage(''), 3000);
    } catch (error) {
      console.error('Eroare la actualizare:', error);
      if (error.eroriCampuri) {
        setErrors({
          ...error.eroriCampuri,
          submit: error.message
        });
      } else {
        setErrors({ submit: error.message || 'Nu s-a putut actualiza profilul.' });
      }
    } finally {
      setSaving(false);
    }
  };

  const handleCancel = () => {
    setFormData({
      nume: profile.nume || '',
      prenume: profile.prenume || '',
      email: profile.email || '',
      telefon: profile.telefon || '',
      gen: profile.gen || '',
      specializare: profile.specializare || '',
      pozaProfil: profile.pozaProfil || '',
    });
    setIsEditing(false);
    setError(null);
  };

  if (loading) {
    return (
      <div className="profil-container">
        <div className="loading-spinner">Se încarcă...</div>
      </div>
    );
  }

  if (!profile) {
    return (
      <div className="profil-container">
        <div className="error-message">Nu s-a putut încărca profilul.</div>
      </div>
    );
  }

  return (
    <div className="profil-container">
      <div className="profil-header">
        <h1>Profil Terapeut</h1>
        {!isEditing && (
          <button className="btn-edit" onClick={() => setIsEditing(true)}>
            Editează Profilul
          </button>
        )}
      </div>

      {successMessage && <div className="success-message">{successMessage}</div>}
      {errors.submit && <div className="error-message">{errors.submit}</div>}

      {/* --- ZONA DE DATE PERSONALE --- */}
      <div className="profil-layout">
        <PozaProfil
          pozaProfil={formData.pozaProfil}
          isEditing={isEditing}
          onChange={handleChange}
        />

        {/* --- EDITARE DATE PERSONALE --- */}
        {isEditing ? (
          <form onSubmit={handleSubmit} className="profil-form-principal">
            <div className="form-group">
              <label htmlFor="nume">Nume *</label>
              <input type="text" id="nume" name="nume" value={formData.nume} onChange={handleChange} required className={errors.nume ? 'input-error' : ''} />
              {errors.nume && <small className="error-text">{errors.nume}</small>}
            </div>
            <div className="form-group">
              <label htmlFor="prenume">Prenume *</label>
              <input type="text" id="prenume" name="prenume" value={formData.prenume} onChange={handleChange} required className={errors.prenume ? 'input-error' : ''} />
              {errors.prenume && <small className="error-text">{errors.prenume}</small>}
            </div>
            <div className="form-group">
              <label htmlFor="gen">Gen *</label>
              <select id="gen" name="gen" value={formData.gen} onChange={handleChange} required className={errors.gen ? 'input-error' : ''}>
                <option value="" disabled>Selectează genul</option>
                <option value="MASCULIN">Masculin</option>
                <option value="FEMININ">Feminin</option>
              </select>
              {errors.gen && <small className="error-text">{errors.gen}</small>}
            </div>
            <div className="form-group">
              <label htmlFor="email">Email *</label>
              <input type="email" id="email" name="email" value={formData.email} onChange={handleChange} required className={errors.email ? 'input-error' : ''} />
              {errors.email && <small className="error-text">{errors.email}</small>}
            </div>
            <div className="form-group">
              <label htmlFor="telefon">Telefon *</label>
              <input type="tel" id="telefon" name="telefon" value={formData.telefon} onChange={handleChange} required pattern="[0-9]{10}" className={errors.telefon ? 'input-error' : ''} />
              {errors.telefon && <small className="error-text">{errors.telefon}</small>}
            </div>
            <div className="form-group">
              <label htmlFor="specializare">Specializare *</label>
              <select id="specializare" name="specializare" value={formData.specializare} onChange={handleChange} required>
                <option value="" disabled>Selectează specializarea</option>
                <option value="ADULTI">Adulți</option>
                <option value="PEDIATRIE">Pediatrie</option>
              </select>
            </div>

            <div className="form-actions">
              <button type="submit" className="btn-save" disabled={saving}>
                {saving ? 'Se salvează...' : 'Salvează'}
              </button>
              <button type="button" className="btn-cancel" onClick={handleCancel} disabled={saving}>Anulează</button>
            </div>
          </form>
        ) : (
          // --- VIEW DATE PERSONALE ---
          <div className="profil-info">
            <div className="info-item"><span className="info-label">Nume:</span><span className="info-value">{profile.nume}</span></div>
            <div className="info-item"><span className="info-label">Prenume:</span><span className="info-value">{profile.prenume}</span></div>
            <div className="info-item">
              <span className="info-label">Gen:</span>
              <span className="info-value">
                {profile.gen === 'MASCULIN' ? 'Masculin' : (profile.gen === 'FEMININ' ? 'Feminin' : 'Nedefinit')}
              </span>
            </div>
            <div className="info-item">
              <span className="info-label">Email:</span><span className="info-value">{profile.email}</span>
            </div>
            <div className="info-item">
              <span className="info-label">Telefon:</span><span className="info-value">{profile.telefon}</span>
            </div>
            <div className="info-item">
              <span className="info-label">Specializare:</span>
              <span className="info-value">
                {profile.specializare === 'ADULTI' ? 'Adulți' : (profile.specializare === 'PEDIATRIE' ? 'Pediatrie' : 'Nedefinit')}
              </span>
            </div>
          </div>
        )}
      </div>

      <hr className="section-divider" />

      {/* --- ZONA DE DISPONIBILITATE --- */}
      <ManagementDisponibilitate />

      <hr className="section-divider" />

      {/* --- ZONA DE CONCEDII --- */}
      <ManagementConcedii />
    </div>
  );
}
