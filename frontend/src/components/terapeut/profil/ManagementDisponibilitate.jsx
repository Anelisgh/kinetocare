import { useState, useEffect } from 'react';
import { profileService } from '../../../services/profileService';

// Componenta care gestioneaza disponibilitatea terapeutului (backend-ul va genera sloturile libere pentru apcienti)
const ManagementDisponibilitate = () => {
  const [disponibilitati, setDisponibilitati] = useState([]);
  const [locatii, setLocatii] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);
  const [formData, setFormData] = useState({
    ziSaptamana: '1',
    locatieId: '',
    oraInceput: '09:00',
    oraSfarsit: '17:00',
  });

  useEffect(() => {
    fetchDate();
  }, []);
// Fetch datele de disponibilitati si locatii
  const fetchDate = async () => {
    try {
      setError(null);
      const [dispData, locatiiData] = await Promise.all([
        profileService.getDisponibilitati(),
        profileService.getLocatii(),
      ]);
      setDisponibilitati(dispData);
      setLocatii(locatiiData);
      if (locatiiData.length > 0) {
        setFormData(prev => ({ ...prev, locatieId: locatiiData[0].id }));
      }
    } catch (err) {
      setError(err.message);
    }
  };
  // Handle schimbarea input-urilor formularului
  const handleChange = (e) => {
    setFormData(prev => ({ ...prev, [e.target.name]: e.target.value }));
  };
// Handle submit formular
  const handleSubmit = async (e) => {
    e.preventDefault();
    setError(null);
    try {
      setLoading(true);
      await profileService.addDisponibilitate(formData);
      await fetchDate(); // Refetch datele
      setFormData({
        ziSaptamana: '1',
        locatieId: locatii[0]?.id || '',
        oraInceput: '09:00',
        oraSfarsit: '17:00',
      });
    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  };
// Handle stergerea unei perioade de disponibilitate
  const handleDelete = async (id) => {
    if (window.confirm('Sunteți sigur că doriți să ștergeți acest interval?')) {
      try {
        setLoading(true);
        setError(null);
        await profileService.deleteDisponibilitate(id);
        setDisponibilitati(prev => prev.filter(d => d.id !== id));
      } catch (err) {
        setError(err.message);
      } finally {
        setLoading(false);
      }
    }
  };

  return (
    <div className="info-section">
      <h3>Gestionează Disponibilitatea</h3>
      {error && <div className="error-message">{error}</div>}

      <form onSubmit={handleSubmit} className="disponibilitate-form">
        <select name="ziSaptamana" value={formData.ziSaptamana} onChange={handleChange} disabled={loading}>
          <option value="1">Luni</option>
          <option value="2">Marți</option>
          <option value="3">Miercuri</option>
          <option value="4">Joi</option>
          <option value="5">Vineri</option>
          <option value="6">Sâmbătă</option>
          <option value="7">Duminică</option>
        </select>
        <select name="locatieId" value={formData.locatieId} onChange={handleChange} required disabled={loading}>
          <option value="">Alege locația</option>
          {locatii.map(loc => (
            <option key={loc.id} value={loc.id}>{loc.nume}</option>
          ))}
        </select>
        <input type="time" name="oraInceput" value={formData.oraInceput} onChange={handleChange} required disabled={loading} />
        <input type="time" name="oraSfarsit" value={formData.oraSfarsit} onChange={handleChange} required disabled={loading} />
        <button type="submit" className="btn-add" disabled={loading}>{loading ? '...' : '+'}</button>
      </form>

      <ul className="item-list">
        {disponibilitati.map(d => (
          <li key={d.id}>
            <span>
              <strong>{d.ziSaptamanaNume}</strong> ({d.oraInceput} - {d.oraSfarsit})
              <br />
              <em>{d.locatieNume}</em>
            </span>
            <button onClick={() => handleDelete(d.id)} className="btn-delete" disabled={loading}>Șterge</button>
          </li>
        ))}
      </ul>
    </div>
  );
};

export default ManagementDisponibilitate;