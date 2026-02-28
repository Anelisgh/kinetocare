import { useState, useEffect } from 'react';
import { profileService } from '../../../services/profileService';

// Componenta care gestioneaza concediile terapeutului (blocheaza zilele)
const ManagementConcedii = () => {
  const [concedii, setConcedii] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);
  const [formData, setFormData] = useState({
    dataInceput: '',
    dataSfarsit: '',
  });

  useEffect(() => {
    fetchDate();
  }, []);
  // Fetch datele de concedii
  const fetchDate = async () => {
    try {
      setError(null);
      const data = await profileService.getConcedii();
      setConcedii(data);
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
      await profileService.addConcediu(formData);
      await fetchDate(); // Refetch datele
      setFormData({ dataInceput: '', dataSfarsit: '' }); // Reseteaza formularul
    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  };
  // Handle stergerea unei perioade de concediu
  const handleDelete = async (id) => {
    if (window.confirm('Sunteți sigur că doriți să ștergeți această perioadă de concediu?')) {
      try {
        setLoading(true);
        setError(null);
        await profileService.deleteConcediu(id);
        setConcedii(prev => prev.filter(c => c.id !== id));
      } catch (err) {
        setError(err.message);
      } finally {
        setLoading(false);
      }
    }
  };
  // Formateaza data in format ro
  const formatDate = (dateString) => new Date(dateString).toLocaleDateString('ro-RO');

  return (
    <div className="info-section">
      <h3>Gestionează Concediile</h3>
      {error && <div className="error-message">{error}</div>}

      <form onSubmit={handleSubmit} className="concediu-form">
        <input type="date" name="dataInceput" value={formData.dataInceput} onChange={handleChange} required disabled={loading} />
        <input type="date" name="dataSfarsit" value={formData.dataSfarsit} onChange={handleChange} required disabled={loading} />
        <button type="submit" className="btn-add" disabled={loading}>{loading ? '...' : '+'}</button>
      </form>

      <ul className="item-list">
        {concedii.map(c => (
          <li key={c.id}>
            <span>
              <strong>De la:</strong> {formatDate(c.dataInceput)} <strong>până la:</strong> {formatDate(c.dataSfarsit)}
            </span>
            <button onClick={() => handleDelete(c.id)} className="btn-delete" disabled={loading}>Șterge</button>
          </li>
        ))}
      </ul>
    </div>
  );
};

export default ManagementConcedii;