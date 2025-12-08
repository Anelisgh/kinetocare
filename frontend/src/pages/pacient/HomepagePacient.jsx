import React, { useEffect, useState } from 'react';
import { HashLink } from 'react-router-hash-link';
import { homepageService } from '../../services/homepageService';
import { programariService } from '../../services/programariService';
import BookingWidget from '../../components/pacient/homepage/BookingWidget';
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
          {/* Daca avem info despre diagnostic din Evaluare, le afisam */}
          {data.diagnostic && (
            <div className="homepage-diagnostic-section">
              <p className="homepage-diagnostic-label">Diagnostic</p>
              <span className="homepage-diagnostic-badge">
                {data.diagnostic}
              </span>
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

        {/* STAREA 1: Fara Terapeut */}
        {!terapeutDetalii && (
          <div className="homepage-no-therapist-card">
            <h3>Începe Recuperarea</h3>
            <p>Selectează un terapeut pentru a putea face programări.</p>
            {/* HashLink in loc de Link -> Pentru că are un mecanism intern care așteaptă ca elementul să apară în DOM înainte să încerce scroll-ul */}
            <HashLink
              to="/pacient/profil#choose-terapeut"
              className="homepage-find-therapist-btn"
              scroll={(el) => el.scrollIntoView({ behavior: 'smooth', block: 'start' })}
            >
              Găsește Terapeut
            </HashLink>
          </div>
        )}


        {/* STAREA 2: Are Programare Viitoare */}
        {terapeutDetalii && urmatoareaProgramare && (
          <div className="homepage-appointment-card">
            <h3>Următoarea Ședință</h3>

            <div className="homepage-appointment-time-section">
              <div className="homepage-appointment-time">
                {urmatoareaProgramare.oraInceput.substring(0, 5)}
              </div>
              <div className="homepage-appointment-date-section">
                <p className="homepage-appointment-date">
                  {new Date(urmatoareaProgramare.data).toLocaleDateString('ro-RO', { weekday: 'long', day: 'numeric', month: 'long' })}
                </p>
                <p className="homepage-appointment-service">{urmatoareaProgramare.tipServiciu}</p>
              </div>
            </div>

            <div className="homepage-appointment-footer">
              <span className="homepage-appointment-price">{urmatoareaProgramare.pret} RON</span>
              <button
                onClick={() => handleCancel(urmatoareaProgramare.id)}
                className="homepage-cancel-btn"
              >
                Anulează
              </button>
            </div>
          </div>
        )}

        {/* STAREA 3: Booking Mode (Are terapeut, dar nu are programare viitoare) */}
        {terapeutDetalii && !urmatoareaProgramare && (
          <BookingWidget
            pacientId={data.id}
            terapeutId={terapeutDetalii.id}
            locatieId={locatieDetalii?.id}
            onSuccess={refreshDashboard}
          />
        )}

      </div>
    </div>
  );
};

export default HomepagePacient;