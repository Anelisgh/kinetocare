import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { adminService } from '../../services/adminService';
import '../../styles/adminShared.css';

export default function AdminHomepage() {
  const [stats, setStats] = useState({
    usersCount: 0,
    locationsCount: 0,
    servicesCount: 0,
    activeUsers: 0
  });
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const fetchDashboardData = async () => {
      try {
        setLoading(true);
        // Putem face request-urile in paralel pentru viteza
        const [users, locations, services] = await Promise.all([
          adminService.getAllUsers(),
          adminService.getAllLocatii(),
          adminService.getAllServiciiAdmin()
        ]);

        // Calculam statisticile
        const totalUsers = users ? users.length : 0;
        const activeUsersCount = users ? users.filter(u => u.active).length : 0;
        const totalLocations = locations ? locations.length : 0;
        const totalServices = services ? services.length : 0;

        setStats({
          usersCount: totalUsers,
          activeUsers: activeUsersCount,
          locationsCount: totalLocations,
          servicesCount: totalServices
        });
      } catch (error) {
        console.error('Eroare la incarcarea datelor pentru dashboard:', error);
      } finally {
        setLoading(false);
      }
    };

    fetchDashboardData();
  }, []);

  if (loading) {
    return (
      <div className="admin-dashboard-container">
        <div className="loading">Se încarcă datele...</div>
      </div>
    );
  }

  return (
    <div className="admin-dashboard-container">
      <header className="dashboard-header">
        <h1>Bine ai venit, Admin!</h1>
        <p>Aici ai o privire de ansamblu asupra clinicii KinetoCare.</p>
      </header>

      {/* Statistici */}
      <div className="stats-grid">
        <div className="stat-card users">
          <div className="stat-icon">👥</div>
          <div className="stat-content">
            <h3>Utilizatori Totali</h3>
            <span className="stat-number">{stats.usersCount}</span>
            <small style={{ color: '#7f8c8d' }}>({stats.activeUsers} activi)</small>
          </div>
        </div>

        <div className="stat-card locations">
          <div className="stat-icon">📍</div>
          <div className="stat-content">
            <h3>Locații</h3>
            <span className="stat-number">{stats.locationsCount}</span>
          </div>
        </div>

        <div className="stat-card services">
          <div className="stat-icon">🩺</div>
          <div className="stat-content">
            <h3>Servicii</h3>
            <span className="stat-number">{stats.servicesCount}</span>
          </div>
        </div>
      </div>

      {/* Actiuni Rapide */}
      <section className="quick-actions">
        <h2>Acțiuni Rapide</h2>
        <div className="actions-grid">
          <Link to="/admin/utilizatori" className="action-card">
            <div className="action-icon">👥</div>
            <h3>Gestionează Utilizatori</h3>
            <p>Vezi lista de pacienți și terapeuți, activează sau dezactivează conturi.</p>
          </Link>

          <Link to="/admin/locatii" className="action-card">
            <div className="action-icon">📍</div>
            <h3>Gestionează Locații</h3>
            <p>Adaugă sau editează locațiile unde se desfășoară ședințele.</p>
          </Link>

          <Link to="/admin/servicii" className="action-card">
            <div className="action-icon">🩺</div>
            <h3>Gestionează Servicii</h3>
            <p>Definește tipurile de servicii și serviciile medicale disponibile.</p>
          </Link>

          <Link to="/admin/statistici" className="action-card">
            <div className="action-icon">📊</div>
            <h3>Statistici Clinică</h3>
            <p>Vizualizează programări, venituri și activitate pe locații și terapeuți.</p>
          </Link>
        </div>
      </section>
    </div>
  );
}
