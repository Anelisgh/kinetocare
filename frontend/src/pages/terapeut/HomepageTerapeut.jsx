import React, { useState, useEffect } from 'react';
import { useAuth } from '../../context/AuthContext';
import { profileService } from '../../services/profileService';
import { programariService } from '../../services/programariService';
import TerapeutCalendar from '../../components/terapeut/homepage/TerapeutCalendar';
import AppointmentSidePanel from '../../components/terapeut/homepage/AppointmentSidePanel';
import '../../styles/HomepageTerapeut.css';

const HomepageTerapeut = () => {
  const { userInfo } = useAuth();

  const [terapeutId, setTerapeutId] = useState(null);
  const [loading, setLoading] = useState(true);

  const [locatii, setLocatii] = useState([]);
  const [selectedLocatieId, setSelectedLocatieId] = useState("");


  const [selectedAppointment, setSelectedAppointment] = useState(null);
  const [refreshKey, setRefreshKey] = useState(0);

  useEffect(() => {
    const fetchProfileAndLocations = async () => {
      try {
        // 1. Profil Terapeut
        const profile = await profileService.getProfile();
        setTerapeutId(profile.terapeutId || profile.id);

        // 2. Încărcare Locații (pentru dropdown)
        const locs = await profileService.getLocatii();
        setLocatii(locs);

      } catch (err) {
        console.error("Eroare la încărcarea datelor:", err);
      } finally {
        setLoading(false);
      }
    };
    fetchProfileAndLocations();
  }, []);

  const handleEventClick = (info) => {
    info.jsEvent.preventDefault();
    setSelectedAppointment(info.event);
  };

  const handleCancelAppointment = async (programareId) => {
    if (!window.confirm("Sigur dorești să anulezi această programare?")) return;
    try {
      await programariService.cancelProgramareTerapeut(programareId, terapeutId);
      setSelectedAppointment(null);
      setRefreshKey(old => old + 1);
      alert("Programarea a fost anulată.");
    } catch (error) {
      alert("Eroare la anulare: " + error.message);
    }
  };

  const handleMarkNeprezentare = async (programareId) => {
    if (!window.confirm("Confirmi că pacientul NU s-a prezentat?")) return;
    try {
      await programariService.markNeprezentare(programareId, terapeutId);
      setSelectedAppointment(null);
      setRefreshKey(old => old + 1); // reincarcam calendarul
      alert("Programarea a fost marcată ca neprezentare.");
    } catch (error) {
      alert("Eroare : " + error.message);
    }
  };

  if (loading) return <div className="homepage-terapeut-loading">Se încarcă calendarul...</div>;
  if (!terapeutId) return <div className="homepage-terapeut-error">Eroare: Nu s-a putut identifica terapeutul.</div>;

  return (
    <div className="homepage-terapeut-container">

      {/* --- ZONA DE FILTRE (HEADER) --- */}
      <div className="calendar-header-controls" style={{ marginBottom: '15px', display: 'flex', gap: '20px', alignItems: 'center' }}>

        {/* Selector Locație */}
        <select
          className="filter-select" // Poți stiliza clasa asta în CSS
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

      <div className="calendar-wrapper">
        <TerapeutCalendar
          terapeutId={terapeutId}
          locatieId={selectedLocatieId}
          onEventClick={handleEventClick}
          refreshTrigger={refreshKey}
        />
      </div>

      <AppointmentSidePanel
        appointment={selectedAppointment}
        onClose={() => setSelectedAppointment(null)}
        onCancel={handleCancelAppointment}
        onMarkNeprezentare={handleMarkNeprezentare}
      />
    </div>
  );
};

export default HomepageTerapeut;