import React, { useEffect, useState } from 'react';
import { HashLink } from 'react-router-hash-link';
import { homepageService } from '../../services/homepageService';
import { programariService } from '../../services/programariService';
import BookingWidget from '../../components/pacient/homepage/BookingWidget';
import ActiveAppointmentCard from '../../components/pacient/homepage/ActiveAppointmentCard';
import TerapeutSection from '../../components/pacient/profil/TerapeutSection';
import '../../styles/HomepagePacient.css';

const HomepagePacient = () => {
  const [data, setData] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  // reincarcare, dupa crearea/anularea unei programari
  const refreshDashboard = async () => {
    try {
      setLoading(true);
      const result = await homepageService.getDashboardData();
      setData(result);
    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    refreshDashboard();
  }, []);

  const handleCancel = async (programareId) => {
    if (!window.confirm('Sigur dorești să anulezi programarea?')) return;
    try {
      await programariService.cancelProgramare(programareId);
      refreshDashboard();
    } catch (err) {
      alert(err.message);
    }
  };

  if (loading) return <div className="homepage-loading">Se încarcă profilul...</div>;
  if (error) return <div className="homepage-error">Eroare: {error}</div>;
  if (!data) return null;

  // extragem datele
  const { nume, prenume, dataNasterii, terapeutDetalii, locatieDetalii, urmatoareaProgramare } = data;

  // calculam varsta
  const varsta = dataNasterii ? new Date().getFullYear() - new Date(dataNasterii).getFullYear() : '-';

  return (
    <div className="homepage-pacient-container">
      <div className="homepage-header">
        {/* <h1>Bine ați revenit, {prenume}!</h1> */}

        <div className="homepage-patient-info">
          <div className="homepage-patient-details">
            <p className="homepage-patient-name">{nume} {prenume} ({varsta} ani)</p>
          </div>
          
          {/* Diagnostic & Progres */}
          {/* Diagnostic & Progres */}
          {data.situatie && (
            <div className="homepage-situatie-container">
               <div className="homepage-diagnostic-section">
                  <p className="homepage-diagnostic-label">Diagnostic</p>
                  <span className="homepage-diagnostic-badge">
                    {data.situatie.diagnostic}
                  </span>
               </div>

               <div className="homepage-progress-section">
                  <div className="homepage-progress-header">
                      <span>Progres Tratament</span>
                      <strong>{data.situatie.sedinteEfectuate} / {data.situatie.sedinteRecomandate} ședințe</strong>
                  </div>
                  <div className="homepage-progress-bar-container">
                      <div 
                        className="homepage-progress-bar-fill" 
                        style={{ width: `${Math.min((data.situatie.sedinteEfectuate / (data.situatie.sedinteRecomandate || 1)) * 100, 100)}%` }}
                      ></div>
                  </div>
               </div>
            </div>
          )}
        </div>

        {/* Terapeut & Locatie Info */}
        <div className="homepage-therapist-card">
          <div className="homepage-therapist-content">
            <p className="homepage-therapist-label">Terapeutul și Locația</p>
            {terapeutDetalii ? (
              <>
                <p className="homepage-therapist-name">
                  {terapeutDetalii.nume} {terapeutDetalii.prenume}
                </p>
                <p className="homepage-location">
                  {locatieDetalii?.nume || 'Locație nedefinită'}
                </p>
              </>
            ) : (
              <p className="homepage-therapist-empty">Nu ai un terapeut selectat</p>
            )}
          </div>
        </div>
      </div>

      {/* 2. ACTION AREA (Logica starilor) */}
      <div className="homepage-action-area">

        {/* STAREA 1: Fara Terapeut -> Afiseaza un card pentru selectarea terapeutului */}
        {!terapeutDetalii && (
          <TerapeutSection 
            dataNasterii={dataNasterii}
            onProfileUpdate={refreshDashboard}
          />
        )}

        {/* STAREA 2: Are Programare Viitoare -> Afiseaza card cu detalii si buton de Anulare */}
        {terapeutDetalii && urmatoareaProgramare && (
          <ActiveAppointmentCard 
            programare={urmatoareaProgramare}
            onCancel={handleCancel}
          />
        )}

        {/* STAREA 3: Booking Mode (Are terapeut, dar nu are programare viitoare) -> Afiseaza widget-ul de programare */}
        {terapeutDetalii && !urmatoareaProgramare && (
          <BookingWidget
            pacientKeycloakId={data.keycloakId}
            terapeutKeycloakId={terapeutDetalii.keycloakId}
            locatieId={locatieDetalii?.id}
            onSuccess={refreshDashboard}
          />
        )}

      </div>
    </div>
  );
};

export default HomepagePacient;