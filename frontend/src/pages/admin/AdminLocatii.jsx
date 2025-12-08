import { useState, useEffect } from 'react';
import { adminService } from '../../services/adminService';
import '../../styles/profil.css';

export default function AdminLocatii() {
  const [locatii, setLocatii] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  const [isModalOpen, setIsModalOpen] = useState(false);
  const [editingLocatie, setEditingLocatie] = useState(null);
  const [formData, setFormData] = useState({
    nume: '',
    adresa: '',
    oras: '',
    judet: '',
    codPostal: '',
    telefon: ''
  });

  useEffect(() => {
    fetchLocatii();
  }, []);

  const fetchLocatii = async () => {
    try {
      setLoading(true);
      const data = await adminService.getAllLocatii();
      setLocatii(data);
    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  };

  // handle form input changes
  const handleInputChange = (e) => {
    const { name, value } = e.target;
    setFormData(prev => ({ ...prev, [name]: value }));
  };
  // adaugare locatie
  const openAddModal = () => {
    setEditingLocatie(null);
    setFormData({ nume: '', adresa: '', oras: '', judet: '', codPostal: '', telefon: '' });
    setIsModalOpen(true);
  };
  // editare locatie
  const openEditModal = (locatie) => {
    setEditingLocatie(locatie);
    setFormData({
      nume: locatie.nume,
      adresa: locatie.adresa,
      oras: locatie.oras,
      judet: locatie.judet,
      codPostal: locatie.codPostal || '',
      telefon: locatie.telefon || ''
    });
    setIsModalOpen(true);
  };
  // submit form
  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      if (editingLocatie) {
        await adminService.updateLocatie(editingLocatie.id, formData);
      } else {
        await adminService.createLocatie(formData);
      }
      setIsModalOpen(false);
      fetchLocatii();
    } catch (err) {
      alert(err.message);
    }
  };
  // toggle status locatie
  const handleToggleStatus = async (id) => {
    if (window.confirm('Sigur dorești să schimbi statusul acestei locații?')) {
      try {
        await adminService.toggleStatus(id);
        fetchLocatii();
      } catch (err) {
        alert(err.message);
      }
    }
  };

  if (loading) return <div className="loading-spinner">Se încarcă locațiile...</div>;

  return (
    <div className="profil-container">
      <div className="profil-header">
        <h1>Administrare Locații</h1>
        <button className="btn-save" onClick={openAddModal}>+ Adaugă Locație</button>
      </div>

      {error && <div className="error-message">{error}</div>}

      <div className="table-container">
        <table>
          <thead>
            <tr>
            <th>ID</th>
            <th>Nume</th>
            <th>Oraș/Județ</th>
            <th>Adresă</th>
            <th>Status</th>
            <th>Acțiuni</th>
          </tr>
          </thead>
          <tbody>
            {locatii.map(loc => (
              <tr key={loc.id}>
                <td>{loc.id}</td>
                <td>{loc.nume}</td>
                <td>{loc.oras}, {loc.judet}</td>
                <td>{loc.adresa}</td>
                <td>
                  <span>
                    {loc.active ? 'Activ' : 'Inactiv'}
                  </span>
                </td>
                <td>
                  <button 
                    className="btn-edit" 
                    onClick={() => openEditModal(loc)}
                  >
                    Edit
                  </button>
                  <button 
                    className="btn-cancel"
                    onClick={() => handleToggleStatus(loc.id)}
                  >
                    {loc.active ? 'Dezactivează' : 'Activează'}
                  </button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>

      {/* MODAL */}
      {isModalOpen && (
        <div className="modal-overlay">
          <div className="modal-content">
            <h2>{editingLocatie ? 'Editează Locația' : 'Adaugă Locație Nouă'}</h2>
            <form onSubmit={handleSubmit} className="profil-form">
              <div className="form-group">
                <label>Nume Locație *</label>
                <input type="text" name="nume" value={formData.nume} onChange={handleInputChange} required />
              </div>
              <div className="form-group">
                <label>Adresă *</label>
                <input type="text" name="adresa" value={formData.adresa} onChange={handleInputChange} required />
              </div>
              <div className="form-row">
                <div className="form-group">
                  <label>Oraș *</label>
                  <input type="text" name="oras" value={formData.oras} onChange={handleInputChange} required />
                </div>
                <div className="form-group">
                  <label>Județ *</label>
                  <input type="text" name="judet" value={formData.judet} onChange={handleInputChange} required />
                </div>
              </div>
              <div className="form-row">
                <div className="form-group">
                  <label>Cod Poștal</label>
                  <input type="text" name="codPostal" value={formData.codPostal} onChange={handleInputChange} />
                </div>
                <div className="form-group">
                  <label>Telefon</label>
                  <input type="text" name="telefon" value={formData.telefon} onChange={handleInputChange} />
                </div>
              </div>
              
              <div className="form-actions">
                <button type="submit" className="btn-save">Salvează</button>
                <button type="button" className="btn-cancel" onClick={() => setIsModalOpen(false)}>Anulează</button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
}