import { useState, useEffect, useMemo } from 'react';
import { adminService } from '../../services/adminService';
import '../../styles/adminShared.css';


const AdminServicii = () => {
    const [activeTab, setActiveTab] = useState('servicii'); // 'servicii' or 'tipuri'
    const [servicii, setServicii] = useState([]);
    const [tipuri, setTipuri] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);

    // Modal State
    const [isModalOpen, setIsModalOpen] = useState(false);
    const [editingItem, setEditingItem] = useState(null); // Item being edited (or null for new)
    const [formData, setFormData] = useState({});

    // Sorting & Filtering State
    const [sortConfig, setSortConfig] = useState({ key: 'id', direction: 'ascending' });
    const [searchTerm, setSearchTerm] = useState('');
    const [statusFilter, setStatusFilter] = useState('all');

    useEffect(() => {
        fetchData();
    }, [activeTab]); // Refetch when tab changes

    const fetchData = async () => {
        try {
            setLoading(true);
            setError(null);
            // Fetch Tipuri always needed for dropdown in Servicii
            const typesData = await adminService.getAllTipuriServicii();
            setTipuri(typesData);

            if (activeTab === 'servicii') {
                const servicesData = await adminService.getAllServiciiAdmin();
                setServicii(servicesData);
            }
        } catch (err) {
            setError(err.message);
        } finally {
            setLoading(false);
        }
    };

    // --- SORTING & FILTERING LOGIC ---
    const sortedItems = useMemo(() => {
        let items = activeTab === 'servicii' ? [...servicii] : [...tipuri];

        // 1. Filter
        if (searchTerm) {
            const lowerTerm = searchTerm.toLowerCase();
            items = items.filter(item => {
                if (activeTab === 'servicii') {
                    // Search in Servicii columns
                    return (
                        (item.numeTip && item.numeTip.toLowerCase().includes(lowerTerm)) || 
                        (item.pret && item.pret.toString().includes(lowerTerm))
                    );
                } else {
                    // Search in Tipuri columns
                    return (
                        item.nume.toLowerCase().includes(lowerTerm) ||
                        (item.descriere && item.descriere.toLowerCase().includes(lowerTerm))
                    );
                }
            });
        }

        if (statusFilter !== 'all') {
            const isActive = statusFilter === 'active';
            items = items.filter(item => item.active === isActive);
        }

        // 2. Sort
        if (sortConfig.key !== null) {
            items.sort((a, b) => {
                let valA = a[sortConfig.key];
                let valB = b[sortConfig.key];
                
                // Handle nulls
                if (valA === undefined || valA === null) valA = '';
                if (valB === undefined || valB === null) valB = '';

                // String comparison case insensitive
                if (typeof valA === 'string') valA = valA.toLowerCase();
                if (typeof valB === 'string') valB = valB.toLowerCase();

                if (valA < valB) return sortConfig.direction === 'ascending' ? -1 : 1;
                if (valA > valB) return sortConfig.direction === 'ascending' ? 1 : -1;
                return 0;
            });
        }
        return items;
    }, [servicii, tipuri, activeTab, sortConfig, searchTerm, statusFilter]);

    const requestSort = (key) => {
        let direction = 'ascending';
        if (sortConfig.key === key && sortConfig.direction === 'ascending') {
            direction = 'descending';
        }
        setSortConfig({ key, direction });
    };

    // --- CRUD OPERATIONS ---
    const handleInputChange = (e) => {
        const { name, value, type, checked } = e.target;
        // For checkboxes (active) handle boolean
        const val = type === 'checkbox' ? checked : value;
        setFormData(prev => ({ ...prev, [name]: val }));
    };

    const openAddModal = () => {
        setEditingItem(null);
        if (activeTab === 'servicii') {
            setFormData({ tipServiciuId: '', nume: '', pret: '', durataMinute: '', active: true });
        } else {
            setFormData({ nume: '', descriere: '', active: true });
        }
        setIsModalOpen(true);
    };

    const openEditModal = (item) => {
        setEditingItem(item);
        if (activeTab === 'servicii') {
            setFormData({
                tipServiciuId: item.tipServiciuId,
                nume: item.nume || '',
                pret: item.pret,
                durataMinute: item.durataMinute,
                active: item.active
            });
        } else {
            setFormData({
                nume: item.nume,
                descriere: item.descriere,
                active: item.active
            });
        }
        setIsModalOpen(true);
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        try {
            if (activeTab === 'servicii') {
                if (editingItem) {
                    await adminService.updateServiciu(editingItem.id, formData);
                } else {
                    await adminService.createServiciu(formData);
                }
            } else {
                if (editingItem) {
                    await adminService.updateTipServiciu(editingItem.id, formData);
                } else {
                    await adminService.createTipServiciu(formData);
                }
            }
            setIsModalOpen(false);
            fetchData();
        } catch (err) {
            alert(err.message);
        }
    };

    const handleToggleStatus = async (id) => {
        if (window.confirm('Sigur dorești să schimbi statusul?')) {
            try {
                if (activeTab === 'servicii') {
                    await adminService.toggleServiciuStatus(id);
                } else {
                    await adminService.toggleTipServiciuStatus(id);
                }
                fetchData();
            } catch (err) {
                alert(err.message);
            }
        }
    };

    // --- RENDER HELPERS ---
    const renderSortArrow = (key) => {
        return sortConfig.key === key 
            ? (sortConfig.direction === 'ascending' ? ' ▲' : ' ▼') 
            : '';
    };

    if (loading && !servicii.length && !tipuri.length) return <div className="loading-spinner">Se încarcă datele...</div>;

    return (
        <div className="admin-container">
            <div className="admin-header">
                <h1>Administrare Servicii</h1>
                <button className="btn-save-header" onClick={openAddModal}>
                    {activeTab === 'servicii' ? '+ Adaugă Serviciu' : '+ Adaugă Tip Serviciu'}
                </button>
            </div>

            {/* TABS */}
            <div className="admin-tabs">
                <button 
                    className={`tab-button ${activeTab === 'servicii' ? 'active' : ''}`}
                    onClick={() => { setActiveTab('servicii'); setSearchTerm(''); setSortConfig({key: 'id', direction: 'ascending'}); }}
                >
                    Servicii
                </button>
                <button 
                    className={`tab-button ${activeTab === 'tipuri' ? 'active' : ''}`}
                    onClick={() => { setActiveTab('tipuri'); setSearchTerm(''); setSortConfig({key: 'id', direction: 'ascending'}); }}
                >
                    Tipuri de Servicii
                </button>
            </div>

            {/* FILTER BAR */}
            <div className="filter-bar">
                <input 
                    type="text" 
                    placeholder={activeTab === 'servicii' ? "Caută după tip..." : "Caută după nume..."}
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
                        {activeTab === 'servicii' ? (
                            <tr>
                                <th onClick={() => requestSort('id')}>ID{renderSortArrow('id')}</th>
                                <th onClick={() => requestSort('numeTip')}>Tip Serviciu{renderSortArrow('numeTip')}</th>
                                <th onClick={() => requestSort('pret')}>Preț (RON){renderSortArrow('pret')}</th>
                                <th onClick={() => requestSort('durataMinute')}>Durată (min){renderSortArrow('durataMinute')}</th>
                                <th onClick={() => requestSort('active')}>Status{renderSortArrow('active')}</th>
                                <th>Acțiuni</th>
                            </tr>
                        ) : (
                            <tr>
                                <th onClick={() => requestSort('id')}>ID{renderSortArrow('id')}</th>
                                <th onClick={() => requestSort('nume')}>Nume Tip{renderSortArrow('nume')}</th>
                                <th onClick={() => requestSort('descriere')}>Descriere{renderSortArrow('descriere')}</th>
                                <th onClick={() => requestSort('active')}>Status{renderSortArrow('active')}</th>
                                <th>Acțiuni</th>
                            </tr>
                        )}
                    </thead>
                    <tbody>
                        {sortedItems.map(item => (
                            <tr key={item.id}>
                                <td>#{item.id}</td>
                                {activeTab === 'servicii' ? (
                                    <>
                                        <td>
                                            <strong>{item.numeTip || 'N/A'}</strong>
                                            {item.nume && <div style={{fontSize: '0.85rem', color: '#64748b'}}>{item.nume}</div>}
                                        </td>
                                        <td>{item.pret} RON</td>
                                        <td>{item.durataMinute} min</td>
                                    </>
                                ) : (
                                    <>
                                        <td><strong>{item.nume}</strong></td>
                                        <td>{item.descriere ? (item.descriere.length > 50 ? item.descriere.substring(0, 50) + '...' : item.descriere) : '-'}</td>
                                    </>
                                )}
                                <td>
                                    <span className={`status-badge ${item.active ? 'active' : 'inactive'}`}>
                                        {item.active ? 'Activ' : 'Inactiv'}
                                    </span>
                                </td>
                                <td>
                                    <button className="admin-btn-edit" onClick={() => openEditModal(item)}>Edit</button>
                                    <button 
                                        className={`action-btn ${item.active ? 'deactivate-btn' : 'activate-btn'}`}
                                        onClick={() => handleToggleStatus(item.id)}
                                    >
                                        {item.active ? 'Dezactivează' : 'Activează'}
                                    </button>
                                </td>
                            </tr>
                        ))}
                        {sortedItems.length === 0 && (
                            <tr>
                                <td colSpan="6" style={{textAlign: 'center', padding: '2rem', color: '#64748b'}}>
                                    Nu s-au găsit rezultate.
                                </td>
                            </tr>
                        )}
                    </tbody>
                </table>
            </div>

            {/* MODAL */}
            {isModalOpen && (
                <div className="modal-overlay" onClick={() => setIsModalOpen(false)}>
                    <div className="modal-content" onClick={e => e.stopPropagation()}>
                        <h2>
                            {editingItem ? 'Editează ' : 'Adaugă '}
                            {activeTab === 'servicii' ? 'Serviciu' : 'Tip Serviciu'}
                        </h2>
                        
                        <form onSubmit={handleSubmit} className="admin-form">
                            {activeTab === 'servicii' ? (
                                <>
                                    <div className="form-group">
                                        <label>Tip Serviciu</label>
                                        <select 
                                            name="tipServiciuId" 
                                            value={formData.tipServiciuId} 
                                            onChange={handleInputChange} 
                                            required
                                        >
                                            <option value="">Alege tipul...</option>
                                            {tipuri.filter(t => t.active).map(t => (
                                                <option key={t.id} value={t.id}>{t.nume}</option>
                                            ))}
                                        </select>
                                    </div>
                                    <div className="form-group">
                                        <label>Nume Specific (Opțional)</label>
                                        <input 
                                            type="text" 
                                            name="nume" 
                                            value={formData.nume || ''} 
                                            onChange={handleInputChange} 
                                            placeholder="Ex: Ședință scurtă, Abonament 10 ședințe"
                                        />
                                    </div>
                                    <div className="form-row">
                                        <div className="form-group">
                                            <label>Preț (RON)</label>
                                            <input 
                                                type="number" 
                                                name="pret" 
                                                value={formData.pret} 
                                                onChange={handleInputChange} 
                                                required 
                                                min="0"
                                                step="0.01"
                                            />
                                        </div>
                                        <div className="form-group">
                                            <label>Durată (Minute)</label>
                                            <input 
                                                type="number" 
                                                name="durataMinute" 
                                                value={formData.durataMinute} 
                                                onChange={handleInputChange} 
                                                required 
                                                min="1"
                                            />
                                        </div>
                                    </div>
                                </>
                            ) : (
                                <>
                                    <div className="form-group">
                                        <label>Nume Tip</label>
                                        <input 
                                            type="text" 
                                            name="nume" 
                                            value={formData.nume} 
                                            onChange={handleInputChange} 
                                            required 
                                        />
                                    </div>
                                    <div className="form-group">
                                        <label>Descriere</label>
                                        <textarea 
                                            name="descriere" 
                                            value={formData.descriere || ''} 
                                            onChange={handleInputChange} 
                                        />
                                    </div>
                                </>
                            )}

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
};

export default AdminServicii;
