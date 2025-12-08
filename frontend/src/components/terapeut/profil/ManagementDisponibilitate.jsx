import { useState, useEffect } from 'react';
import { profileService } from '../../../services/profileService';

const ManagementDisponibilitate = () => {
  const [disponibilitati, setDisponibilitati] = useState([]);
  const [locatii, setLocatii] = useState([]);
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
      await profileService.addDisponibilitate(formData);
      fetchDate(); // Refetch datele
      setFormData({
        ziSaptamana: '1',
        locatieId: locatii[0]?.id || '',
        oraInceput: '09:00',
        oraSfarsit: '17:00',
      });
    } catch (err) {
      setError(err.message);
    }
  };
// Handle stergerea unei perioade de disponibilitate
  const handleDelete = async (id) => {
    if (window.confirm('Sunteți sigur că doriți să ștergeți acest interval?')) {
      try {
        setError(null);
        await profileService.deleteDisponibilitate(id);
        setDisponibilitati(prev => prev.filter(d => d.id !== id));
      } catch (err) {
        setError(err.message);
      }
    }
  };

  return (
    <div className="info-section">
      <h3>Gestionează Disponibilitatea</h3>
      {error && <div className="error-message">{error}</div>}

      <form onSubmit={handleSubmit} className="disponibilitate-form">
        <select name="ziSaptamana" value={formData.ziSaptamana} onChange={handleChange}>
          <option value="1">Luni</option>
          <option value="2">Marți</option>
          <option value="3">Miercuri</option>
          <option value="4">Joi</option>
          <option value="5">Vineri</option>
          <option value="6">Sâmbătă</option>
          <option value="7">Duminică</option>
        </select>
        <select name="locatieId" value={formData.locatieId} onChange={handleChange} required>
          <option value="">Alege locația</option>
          {locatii.map(loc => (
            <option key={loc.id} value={loc.id}>{loc.nume}</option>
          ))}
        </select>
        <input type="time" name="oraInceput" value={formData.oraInceput} onChange={handleChange} required />
        <input type="time" name="oraSfarsit" value={formData.oraSfarsit} onChange={handleChange} required />
        <button type="submit" className="btn-add">+</button>
      </form>

      <ul className="item-list">
        {disponibilitati.map(d => (
          <li key={d.id}>
            <span>
              <strong>{d.ziSaptamanaNume}</strong> ({d.oraInceput} - {d.oraSfarsit})
              <br />
              <em>{d.locatieNume}</em>
            </span>
            <button onClick={() => handleDelete(d.id)} className="btn-delete">Șterge</button>
          </li>
        ))}
      </ul>
    </div>
  );
};

export default ManagementDisponibilitate;