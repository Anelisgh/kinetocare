import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext';
import { programariService } from '../../services/programariService';
import '../../styles/pacientiTerapeut.css';

const PacientiTerapeut = () => {
  const { userInfo } = useAuth();
  const navigate = useNavigate();

  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  const [pacientiActivi, setPacientiActivi] = useState([]);
  const [pacientiArhivati, setPacientiArhivati] = useState([]);
  const [showArhiva, setShowArhiva] = useState(false);
  const [searchTerm, setSearchTerm] = useState('');

  useEffect(() => {
    const fetchData = async () => {
      try {
        // keycloakId-ul terapeutului e extras din JWT pe backend
        const lista = await programariService.getListaPacienti();
        setPacientiActivi(lista.activi || []);
        setPacientiArhivati(lista.arhivati || []);
      } catch (err) {
        console.error('Eroare la încărcarea pacienților:', err);
        setError(err.message);
      } finally {
        setLoading(false);
      }
    };
    fetchData();
  }, []);

  const handlePatientClick = (pacientId) => {
    navigate(`/terapeut/pacienti/${pacientId}`);
  };

  // Filtrare pacienti dupa search
  const filterPatients = (patients) => {
    if (!searchTerm.trim()) return patients;
    const term = searchTerm.toLowerCase();
    return patients.filter(p =>
      `${p.nume} ${p.prenume}`.toLowerCase().includes(term) ||
      (p.diagnostic && p.diagnostic.toLowerCase().includes(term))
    );
  };

  const filteredActivi = filterPatients(pacientiActivi);
  const filteredArhivati = filterPatients(pacientiArhivati);

  if (loading) return (
    <div className="pacienti-loading">
      <span className="loading-icon">👥</span>
      <p>Se încarcă lista de pacienți...</p>
    </div>
  );

  if (error) return (
    <div className="pacienti-error">
      <p>Eroare: {error}</p>
    </div>
  );

  return (
    <div className="pacienti-container">
      {/* Header */}
      <div className="pacienti-header">
        <div className="pacienti-title-section">
          <h1>Pacienții Mei</h1>
          <span className="pacienti-count">{pacientiActivi.length} activi</span>
        </div>
        <div className="pacienti-search">
          <span className="search-icon">🔍</span>
          <input
            type="text"
            placeholder="Caută pacient..."
            value={searchTerm}
            onChange={(e) => setSearchTerm(e.target.value)}
            className="search-input"
          />
        </div>
      </div>

      {/* Pacienti Activi */}
      <section className="pacienti-section">
        <h2 className="section-title">
          <span className="section-icon active">●</span>
          Pacienți Activi
        </h2>

        {filteredActivi.length === 0 ? (
          <div className="pacienti-empty">
            <p>{searchTerm ? 'Niciun pacient activ găsit.' : 'Nu ai pacienți activi momentan.'}</p>
          </div>
        ) : (
          <div className="pacienti-grid">
            {filteredActivi.map(pacient => (
              <div
                key={pacient.pacientId}
                className="pacient-card"
                onClick={() => handlePatientClick(pacient.pacientId)}
              >
                <div className="pacient-card-header">
                  <div className="pacient-avatar">
                    {pacient.nume?.charAt(0)}{pacient.prenume?.charAt(0)}
                  </div>
                  <div className="pacient-name-section">
                    <h3>{pacient.nume} {pacient.prenume}</h3>
                    {pacient.varsta && <span className="pacient-age">{pacient.varsta} ani</span>}
                  </div>
                </div>
                <div className="pacient-card-body">
                  <div className="pacient-info-row">
                    <span className="info-label">Diagnostic</span>
                    <span className="info-value diagnostic">{pacient.diagnostic || 'N/A'}</span>
                  </div>
                  <div className="pacient-info-row">
                    <span className="info-label">Ședințe rămase</span>
                    <span className={`info-value sessions ${pacient.sedinteRamase > 0 ? 'has-sessions' : 'no-sessions'}`}>
                      {pacient.sedinteRamase != null ? pacient.sedinteRamase : '—'}
                    </span>
                  </div>
                </div>
                <div className="pacient-card-footer">
                  <span className="view-details">Vezi fișa →</span>
                </div>
              </div>
            ))}
          </div>
        )}
      </section>

      {/* Arhiva */}
      {pacientiArhivati.length > 0 && (
        <section className="pacienti-section arhiva-section">
          <button
            className="arhiva-toggle"
            onClick={() => setShowArhiva(!showArhiva)}
          >
            <span className="section-icon archived">○</span>
            Arhivă ({pacientiArhivati.length} pacienți)
            <span className={`toggle-arrow ${showArhiva ? 'open' : ''}`}>▼</span>
          </button>

          {showArhiva && (
            <div className="pacienti-grid arhivati">
              {filteredArhivati.map(pacient => (
                <div
                  key={pacient.pacientId}
                  className="pacient-card archived"
                  onClick={() => handlePatientClick(pacient.pacientId)}
                >
                  <div className="pacient-card-header">
                    <div className="pacient-avatar archived">
                      {pacient.nume?.charAt(0)}{pacient.prenume?.charAt(0)}
                    </div>
                    <div className="pacient-name-section">
                      <h3>{pacient.nume} {pacient.prenume}</h3>
                    </div>
                  </div>
                  <div className="pacient-card-body">
                    <div className="pacient-info-row">
                      <span className="info-label">Ultimul Diagnostic</span>
                      <span className="info-value diagnostic">{pacient.diagnostic || 'N/A'}</span>
                    </div>
                  </div>
                  <div className="pacient-card-footer">
                    <span className="view-details">Vezi fișa →</span>
                  </div>
                </div>
              ))}
            </div>
          )}
        </section>
      )}
    </div>
  );
};

export default PacientiTerapeut;
