import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext';
import { profileService } from '../../services/profileService';
import { programariService } from '../../services/programariService';
import TerapeutCalendar from '../../components/terapeut/homepage/TerapeutCalendar';
import AppointmentSidePanel from '../../components/terapeut/homepage/AppointmentSidePanel';
import '../../styles/HomepageTerapeut.css';

// Pagina principală a terapeutului -> afiseaza calendarul si panelul de programare
const HomepageTerapeut = () => {
  const { userInfo } = useAuth();

  const [loading, setLoading] = useState(true);

  const [locatii, setLocatii] = useState([]);
  const [selectedLocatieId, setSelectedLocatieId] = useState("");
  const [selectedAppointment, setSelectedAppointment] = useState(null);
  const [refreshKey, setRefreshKey] = useState(0);
  const [actionLoading, setActionLoading] = useState(false);

  const [profileIncomplete, setProfileIncomplete] = useState({
    specializare: false,
    disponibilitati: false
  });

  useEffect(() => {
    // Functie care incarca datele necesare pentru verificare si dropdown
    const fetchData = async () => {
      try {
        const [locs, profile, disp] = await Promise.all([
          profileService.getLocatii(),
          profileService.getProfile(),
          profileService.getDisponibilitati()
        ]);

        setLocatii(locs);
        
        // Verificam daca profilul e incomplet
        setProfileIncomplete({
          specializare: !profile?.specializare || profile.specializare.trim() === "",
          disponibilitati: !disp || disp.length === 0
        });

      } catch (err) {
        console.error("Eroare la încărcarea datelor:", err);
      } finally {
        setLoading(false);
      }
    };
    fetchData();
  }, []);

  // Functie care selecteaza programarea
  const handleEventClick = (info) => {
    info.jsEvent.preventDefault();
    setSelectedAppointment(info.event);
  };

  // Functie care anuleaza programarea
  const handleCancelAppointment = async (programareId) => {
    if (!window.confirm("Sigur dorești să anulezi această programare?")) return;
    try {
      setActionLoading(true);
      await programariService.cancelProgramareTerapeut(programareId);
      setSelectedAppointment(null);
      setRefreshKey(old => old + 1);
      alert("Programarea a fost anulată.");
    } catch (error) {
      alert("Eroare la anulare: " + error.message);
    } finally {
      setActionLoading(false);
    }
  };

  // Functie care marcaza programarea ca neprezentare
  const handleMarkNeprezentare = async (programareId) => {
    if (!window.confirm("Confirmi că pacientul NU s-a prezentat?")) return;
    try {
      setActionLoading(true);
      await programariService.markNeprezentare(programareId);
      setSelectedAppointment(null);
      setRefreshKey(old => old + 1); // reincarcam calendarul
      alert("Programarea a fost marcată ca neprezentare.");
    } catch (error) {
      alert("Eroare : " + error.message);
    } finally {
      setActionLoading(false);
    }
  };

  if (loading) return <div className="homepage-terapeut-loading">Se încarcă calendarul...</div>;

  return (
    <div className="homepage-terapeut-container">

      {/* Banner Reminder Profil Incomplet */}
      {(profileIncomplete.specializare || profileIncomplete.disponibilitati) && (
        <div className="profile-reminder-banner">
          <div className="profile-reminder-icon-wrapper">
            <span>⚠️</span>
          </div>
          <div className="profile-reminder-content">
            <h4>Profilul tău are nevoie de atenție</h4>
            <p>Pentru a fi vizibil pacienților și a putea primi programări, te rugăm să finalizezi următorii pași:</p>
            
            <ul className="profile-reminder-list">
              {profileIncomplete.specializare && (
                <li className="profile-reminder-item">
                  <div className="profile-reminder-bullet"></div>
                  <span>Adaugă <Link to="/terapeut/profil" className="profile-reminder-link">specializarea</Link> în profil</span>
                </li>
              )}
              {profileIncomplete.disponibilitati && (
                <li className="profile-reminder-item">
                  <div className="profile-reminder-bullet"></div>
                  <span>Setează <Link to="/terapeut/profil" className="profile-reminder-link">programul de lucru</Link></span>
                </li>
              )}
            </ul>
          </div>
        </div>
      )}

      {/* --- ZONA DE FILTRE (HEADER) --- */}
      <div className="calendar-header-controls" style={{ marginBottom: '15px', display: 'flex', gap: '20px', alignItems: 'center' }}>

        {/* Selector Locație */}
        <select
          className="filter-select"
          value={selectedLocatieId}
          onChange={(e) => setSelectedLocatieId(e.target.value)}
          style={{ padding: '8px', borderRadius: '4px', border: '1px solid #ccc' }}
        >
          <option value="">Toate Locațiile</option>
          {locatii.map(loc => (
            <option key={loc.id} value={loc.id}>{loc.nume}</option>
          ))}
        </select>
      </div>

      {/* --- ZONA DE CALENDAR --- */}
      <div className="calendar-wrapper">
        <TerapeutCalendar
          locatieId={selectedLocatieId}
          onEventClick={handleEventClick}
          refreshTrigger={refreshKey}
        />
      </div>

      {/* --- ZONA DE PANEL DE PROGRAMARE --- */}
      <AppointmentSidePanel
        appointment={selectedAppointment}
        onClose={() => setSelectedAppointment(null)}
        onCancel={handleCancelAppointment}
        onMarkNeprezentare={handleMarkNeprezentare}
        actionLoading={actionLoading}
      />
    </div>
  );
};

export default HomepageTerapeut;