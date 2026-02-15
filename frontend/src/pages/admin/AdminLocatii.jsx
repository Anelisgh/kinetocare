import { useState, useEffect, useMemo } from 'react';
import { adminService } from '../../services/adminService';
import '../../styles/adminLocatii.css';

// Pagina de administrare a locatiilor (doar adminul are acces)
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

  // Sorting & Filtering Logic
  const [sortConfig, setSortConfig] = useState({ key: 'id', direction: 'ascending' });
  const [searchTerm, setSearchTerm] = useState('');
  const [statusFilter, setStatusFilter] = useState('all'); // 'all', 'active', 'inactive'

  const sortedLocatii = useMemo(() => {
    let sortableItems = [...locatii];

    // 1. Filtrare
    if (searchTerm) {
      const lowerTerm = searchTerm.toLowerCase();
      sortableItems = sortableItems.filter(loc => 
        loc.nume.toLowerCase().includes(lowerTerm) ||
        loc.oras.toLowerCase().includes(lowerTerm) ||
        loc.judet.toLowerCase().includes(lowerTerm) ||
        loc.adresa.toLowerCase().includes(lowerTerm)
      );
    }

    if (statusFilter !== 'all') {
      const isActive = statusFilter === 'active';
      sortableItems = sortableItems.filter(loc => loc.active === isActive);
    }

    // 2. Sortare
    if (sortConfig.key !== null) {
      sortableItems.sort((a, b) => {
        if (a[sortConfig.key] < b[sortConfig.key]) {
          return sortConfig.direction === 'ascending' ? -1 : 1;
        }
        if (a[sortConfig.key] > b[sortConfig.key]) {
          return sortConfig.direction === 'ascending' ? 1 : -1;
        }
        return 0;
      });
    }
    return sortableItems;
  }, [locatii, sortConfig, searchTerm, statusFilter]);

  const requestSort = (key) => {
    let direction = 'ascending';
    if (sortConfig.key === key && sortConfig.direction === 'ascending') {
      direction = 'descending';
    }
    setSortConfig({ key, direction });
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
    <div className="admin-container">
      <div className="admin-header">
        <h1>Administrare Locații</h1>
        <button className="btn-save-header" onClick={openAddModal}>+ Adaugă Locație</button>
      </div>

      {/* FILTER BAR */}
      <div className="filter-bar">
        <input 
          type="text" 
          placeholder="Caută după nume, oraș, județ..." 
          value={searchTerm}
          onChange={(e) => setSearchTerm(e.target.value)}
          className="search-input"
        />
        <select 
          value={statusFilter} 
          onChange={(e) => setStatusFilter(e.target.value)}
          className="filter-select"
        >
          <option value="all">Toate Statusurile</option>
          <option value="active">Doar Active</option>
          <option value="inactive">Doar Inactive</option>
        </select>
      </div>

      {error && <div className="error-message">{error}</div>}

      <div className="table-container">
        <table>
          <thead>
            <tr>
              <th onClick={() => requestSort('id')} style={{ cursor: 'pointer' }}>
                ID {sortConfig.key === 'id' ? (sortConfig.direction === 'ascending' ? '▲' : '▼') : ''}
              </th>
              <th onClick={() => requestSort('nume')} style={{ cursor: 'pointer' }}>
                Nume {sortConfig.key === 'nume' ? (sortConfig.direction === 'ascending' ? '▲' : '▼') : ''}
              </th>
              <th onClick={() => requestSort('oras')} style={{ cursor: 'pointer' }}>
                Oraș {sortConfig.key === 'oras' ? (sortConfig.direction === 'ascending' ? '▲' : '▼') : ''}
              </th>
              <th onClick={() => requestSort('judet')} style={{ cursor: 'pointer' }}>
                Județ {sortConfig.key === 'judet' ? (sortConfig.direction === 'ascending' ? '▲' : '▼') : ''}
              </th>
              <th onClick={() => requestSort('adresa')} style={{ cursor: 'pointer' }}>
                Adresă {sortConfig.key === 'adresa' ? (sortConfig.direction === 'ascending' ? '▲' : '▼') : ''}
              </th>
              <th onClick={() => requestSort('active')} style={{ cursor: 'pointer' }}>
                Status {sortConfig.key === 'active' ? (sortConfig.direction === 'ascending' ? '▲' : '▼') : ''}
              </th>
              <th>Acțiuni</th>
            </tr>
          </thead>
          <tbody>
            {sortedLocatii.map(loc => (
              <tr key={loc.id}>
                <td>#{loc.id}</td>
                <td><strong>{loc.nume}</strong></td>
                <td>{loc.oras}</td>
                <td>{loc.judet}</td>
                <td>{loc.adresa}</td>
                <td>
                  <span className={loc.active ? 'status-active' : 'status-inactive'}>
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
                    className="btn-toggle"
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
            <form onSubmit={handleSubmit} className="admin-form">
              <div className="form-group">
                <label>Nume Locație *</label>
                <input type="text" name="nume" value={formData.nume} onChange={handleInputChange} required placeholder="Ex: Clinica Centru" />
              </div>
              <div className="form-group">
                <label>Adresă *</label>
                <input type="text" name="adresa" value={formData.adresa} onChange={handleInputChange} required placeholder="Strada, Număr, Bloc..." />
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
                  <input type="text" name="telefon" value={formData.telefon} onChange={handleInputChange} placeholder="07xx xxx xxx" />
                </div>
              </div>
              
              <div className="modal-actions">
                <button type="button" className="btn-modal-cancel" onClick={() => setIsModalOpen(false)}>Anulează</button>
                <button type="submit" className="btn-modal-save">Salvează</button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
}