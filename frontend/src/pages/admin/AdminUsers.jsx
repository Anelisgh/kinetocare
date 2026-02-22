import React, { useState, useEffect } from 'react';
import { adminService } from '../../services/adminService';
import { useAuth } from '../../context/AuthContext';
import '../../styles/adminShared.css';
 

export default function AdminUsers() {
  const [users, setUsers] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  // Filtre
  const [roleFilter, setRoleFilter] = useState(''); // '' = All, 'TERAPEUT', 'PACIENT'
  const [activeFilter, setActiveFilter] = useState(''); // '' = All, 'true', 'false'
  const [searchTerm, setSearchTerm] = useState('');

  // Confirmare Modal
  const [showConfirm, setShowConfirm] = useState(false);
  const [userToToggle, setUserToToggle] = useState(null);

  useEffect(() => {
    fetchUsers();
  }, [roleFilter, activeFilter]);

  const fetchUsers = async () => {
    setLoading(true);
    setError(null);
    try {
      // transformam activeFilter in boolean sau undefined pt service
      let activeVal = undefined;
      if (activeFilter === 'true') activeVal = true;
      if (activeFilter === 'false') activeVal = false;

      const data = await adminService.getAllUsers(roleFilter, activeVal);
      setUsers(data || []);
    } catch (err) {
      setError('Eroare la încărcarea utilizatorilor.');
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  const [sortConfig, setSortConfig] = useState({ key: 'createdAt', direction: 'descending' });

  const sortedUsers = React.useMemo(() => {
    let sortableUsers = [...users];

    // 1. Filter by search term
    if (searchTerm) {
      const lowerTerm = searchTerm.toLowerCase();
      sortableUsers = sortableUsers.filter(user => 
        (user.nume?.toLowerCase() || '').includes(lowerTerm) ||
        (user.prenume?.toLowerCase() || '').includes(lowerTerm) ||
        (user.email?.toLowerCase() || '').includes(lowerTerm) ||
        (user.telefon?.toLowerCase() || '').includes(lowerTerm)
      );
    }

    // 2. Sort
    if (sortConfig.key !== null) {
      sortableUsers.sort((a, b) => {
        let valA = a[sortConfig.key];
        let valB = b[sortConfig.key];

        // Handle nested properties or specific fields
        if (sortConfig.key === 'nume') {
             valA = (a.nume + ' ' + a.prenume).toLowerCase();
             valB = (b.nume + ' ' + b.prenume).toLowerCase();
        } else if (typeof valA === 'string') {
            valA = valA.toLowerCase();
            valB = valB.toLowerCase();
        }

        if (valA < valB) {
          return sortConfig.direction === 'ascending' ? -1 : 1;
        }
        if (valA > valB) {
          return sortConfig.direction === 'ascending' ? 1 : -1;
        }
        return 0;
      });
    }
    return sortableUsers;
  }, [users, sortConfig, searchTerm]);

  const requestSort = (key) => {
    let direction = 'ascending';
    if (sortConfig.key === key && sortConfig.direction === 'ascending') {
      direction = 'descending';
    }
    setSortConfig({ key, direction });
  };

  const handleToggleClick = (user) => {
    // Daca userul este activ si vrem sa il dezactivam -> cerem confirmare
    if (user.active) {
      setUserToToggle(user);
      setShowConfirm(true);
    } else {
      // Reactivarea se face direct (sau tot cu confirmare daca preferi, dar e mai safe)
      // Totusi, pentru consistenta, cerem confirmare si aici, dar mesajul difera
      setUserToToggle(user);
      setShowConfirm(true);
    }
  };

  const confirmToggle = async () => {
    if (!userToToggle) return;

    try {
      const updatedUser = await adminService.toggleUserActive(userToToggle.id);
      
      // Actualizam lista local
      setUsers(prevUsers => prevUsers.map(u => 
        u.id === userToToggle.id ? { ...u, active: updatedUser.active } : u
      ));

      setShowConfirm(false);
      setUserToToggle(null);
    } catch (err) {
      alert('Eroare la modificarea statusului: ' + err.message);
    }
  };

  const cancelToggle = () => {
    setShowConfirm(false);
    setUserToToggle(null);
  };

  return (
    <div className="admin-container">
      <div className="admin-header">
        <h2>Administrare Utilizatori</h2>
      </div>

      {/* Filtre */}
      {/* Filtre */}
      <div className="filter-bar">
        <div className="filter-group" style={{ flex: '2 1 0' }}>
          <label>Căutare</label>
          <input 
            type="text" 
            placeholder="Caută după nume, email..." 
            className="search-input"
            value={searchTerm}
            onChange={(e) => setSearchTerm(e.target.value)}
          />
        </div>
        <div className="filter-group" style={{ flex: '1 1 0' }}>
          <label>Rol</label>
          <select 
            value={roleFilter} 
            onChange={(e) => setRoleFilter(e.target.value)}
            className="filter-select"
            style={{ width: '100%' }}
          >
            <option value="">Toți</option>
            <option value="TERAPEUT">Terapeuți</option>
            <option value="PACIENT">Pacienți</option>
          </select>
        </div>
        
        <div className="filter-group" style={{ flex: '1 1 0' }}>
          <label>Status</label>
          <select 
            value={activeFilter} 
            onChange={(e) => setActiveFilter(e.target.value)}
            className="filter-select"
            style={{ width: '100%' }}
          >
            <option value="">Toți</option>
            <option value="true">Activi</option>
            <option value="false">Inactivi</option>
          </select>
        </div>
      </div>

      {error && <div className="error-message">{error}</div>}

      {loading ? (
        <div className="loading">Se încarcă...</div>
      ) : (
        <div className="table-container">
          <table className="users-table">
            <thead>
              <tr>
                <th onClick={() => requestSort('nume')} style={{ cursor: 'pointer' }}>
                  Nume {sortConfig.key === 'nume' ? (sortConfig.direction === 'ascending' ? '▲' : '▼') : ''}
                </th>
                <th onClick={() => requestSort('email')} style={{ cursor: 'pointer' }}>
                  Email {sortConfig.key === 'email' ? (sortConfig.direction === 'ascending' ? '▲' : '▼') : ''}
                </th>
                <th>Telefon</th>
                <th>Rol</th>
                <th onClick={() => requestSort('createdAt')} style={{ cursor: 'pointer' }}>
                  Data Creării {sortConfig.key === 'createdAt' ? (sortConfig.direction === 'ascending' ? '▲' : '▼') : ''}
                </th>
                <th>Status</th>
                <th>Acțiuni</th>
              </tr>
            </thead>
            <tbody>
              {sortedUsers.length > 0 ? (
                sortedUsers.map(user => (
                  <tr key={user.id} className={!user.active ? 'inactive-row' : ''}>
                    <td>{user.nume} {user.prenume}</td>
                    <td>{user.email}</td>
                    <td>{user.telefon || '-'}</td>
                    <td>
                      <span className={`badge role-${user.role.toLowerCase()}`}>
                        {user.role}
                      </span>
                    </td>
                    <td>{new Date(user.createdAt).toLocaleDateString('ro-RO')}</td>
                    <td>
                      <span className={`status-badge ${user.active ? 'active' : 'inactive'}`}>
                        {user.active ? 'Activ' : 'Inactiv'}
                      </span>
                    </td>
                    <td>
                      <button 
                        className={`action-btn ${user.active ? 'deactivate-btn' : 'activate-btn'}`}
                        onClick={() => handleToggleClick(user)}
                      >
                        {user.active ? 'Dezactivează' : 'Reactivează'}
                      </button>
                    </td>
                  </tr>
                ))
              ) : (
                <tr>
                  <td colSpan="7" className="no-data">Nu au fost găsiți utilizatori.</td>
                </tr>
              )}
            </tbody>
          </table>
        </div>
      )}

      {/* Confirmation Modal */}
      {showConfirm && userToToggle && (
        <div className="modal-overlay">
          <div className="modal-content">
            <h3>Confirmare {userToToggle.active ? 'Dezactivare' : 'Reactivare'}</h3>
            <p>
              Ești sigur că vrei să {userToToggle.active ? 'dezactivezi' : 'reactivezi'} utilizatorul <strong>{userToToggle.nume} {userToToggle.prenume}</strong>?
            </p>
            {userToToggle.active && (
              <div className="warning-box">
                <p>⚠️ <strong>Atenție!</strong> Dezactivarea va duce la:</p>
                <ul>
                  <li>Blocarea accesului în aplicație.</li>
                  <li><strong>Anularea automată</strong> a tuturor programărilor viitoare.</li>
                </ul>
              </div>
            )}
            
            <div className="modal-actions">
              <button className="cancel-btn" onClick={cancelToggle}>Anulează</button>
              <button 
                className={`confirm-btn ${userToToggle.active ? 'danger' : 'success'}`} 
                onClick={confirmToggle}
              >
                Da, {userToToggle.active ? 'Dezactivează' : 'Reactivează'}
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}
