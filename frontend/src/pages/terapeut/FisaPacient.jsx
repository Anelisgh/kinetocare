import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext';
import { profileService } from '../../services/profileService';
import { programariService } from '../../services/programariService';
import { evolutiiService } from '../../services/evolutiiService';
import { evaluariService } from '../../services/evaluariService';
import '../../styles/fisaPacient.css';

const FisaPacient = () => {
  const { pacientId } = useParams();
  const navigate = useNavigate();
  const { userInfo } = useAuth();

  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [fisa, setFisa] = useState(null);
  const [activeTab, setActiveTab] = useState('evaluari');

  const terapeutKeycloakId = userInfo?.keycloakId;

  const fetchFisa = async () => {
    try {
      const data = await programariService.getFisaPacient(pacientId, terapeutKeycloakId);
      setFisa(data);
    } catch (err) {
      console.error('Eroare la încărcarea fișei:', err);
      setError(err.message);
    }
  };

  useEffect(() => {
    const fetchData = async () => {
      try {
        await fetchFisa();
      } catch (err) {
        console.error('Eroare la încărcarea fișei:', err);
        setError(err.message);
      } finally {
        setLoading(false);
      }
    };
    if (terapeutKeycloakId) fetchData();
    else setLoading(false);
  }, [pacientId, terapeutKeycloakId]);

  // Refresh fisa dupa o editare
  const refreshFisa = () => {
    fetchFisa();
  };

  if (loading) return (
    <div className="fisa-loading">
      <span className="loading-icon">📋</span>
      <p>Se încarcă fișa pacientului...</p>
    </div>
  );

  if (error) return (
    <div className="fisa-error">
      <p>Eroare: {error}</p>
    </div>
  );

  if (!fisa) return null;

  const tabs = [
    { key: 'evaluari', label: 'Evaluări', icon: '📝', count: fisa.evaluari?.length },
    { key: 'evolutii', label: 'Evoluții', icon: '📈', count: fisa.evolutii?.length },
    { key: 'programari', label: 'Programări', icon: '📅', count: fisa.programari?.length },
    { key: 'jurnale', label: 'Jurnale', icon: '📓', count: fisa.jurnale?.length },
  ];

  // Progres tratament
  const situatie = fisa.situatie;
  const sedinteEfectuate = situatie?.sedinteEfectuate || 0;
  const sedinteRecomandate = situatie?.sedinteRecomandate || 1;
  const progressPercent = Math.min((sedinteEfectuate / sedinteRecomandate) * 100, 100);

  return (
    <div className="fisa-container">
      {/* Back Button */}
      <button className="fisa-back-btn" onClick={() => navigate('/terapeut/pacienti')}>
        ← Înapoi la pacienți
      </button>

      {/* Header Card */}
      <div className="fisa-header-card">
        <div className="fisa-header-left">
          <div className="fisa-avatar">
            {fisa.nume?.charAt(0)}{fisa.prenume?.charAt(0)}
          </div>
          <div className="fisa-header-info">
            <h1>{fisa.nume} {fisa.prenume}</h1>
            <div className="fisa-header-details">
              {fisa.gen && <span className="fisa-detail-chip">{fisa.gen}</span>}
              {fisa.telefon && <span className="fisa-detail-chip">📞 {fisa.telefon}</span>}
              {fisa.email && <span className="fisa-detail-chip">✉ {fisa.email}</span>}
            </div>
          </div>
        </div>

        {/* Situatie Curenta - Progress Bar */}
        {situatie && (
          <div className="fisa-situatie">
            <div className="situatie-item">
              <span className="situatie-label">Diagnostic</span>
              <span className="situatie-value diagnostic-badge">
                {situatie.diagnostic || 'N/A'}
              </span>
            </div>
            <div className="fisa-progress-section">
              <div className="fisa-progress-header">
                <span>Progres Tratament</span>
                <strong>{sedinteEfectuate} / {situatie.sedinteRecomandate || 0} ședințe</strong>
              </div>
              <div className="fisa-progress-bar-container">
                <div 
                  className="fisa-progress-bar-fill" 
                  style={{ width: `${progressPercent}%` }}
                ></div>
              </div>
            </div>
          </div>
        )}
      </div>

      {/* Tabs */}
      <div className="fisa-tabs">
        {tabs.map(tab => (
          <button
            key={tab.key}
            className={`fisa-tab ${activeTab === tab.key ? 'active' : ''}`}
            onClick={() => setActiveTab(tab.key)}
          >
            <span className="tab-icon">{tab.icon}</span>
            {tab.label}
            {tab.count > 0 && <span className="tab-count">{tab.count}</span>}
          </button>
        ))}
      </div>

      {/* Tab Content */}
      <div className="fisa-tab-content">
        {activeTab === 'evaluari' && <EvaluariTab evaluari={fisa.evaluari} terapeutKeycloakId={terapeutKeycloakId} onRefresh={refreshFisa} />}
        {activeTab === 'evolutii' && <EvolutiiTab evolutii={fisa.evolutii} onRefresh={refreshFisa} />}
        {activeTab === 'programari' && <ProgramariTab programari={fisa.programari} terapeutKeycloakId={terapeutKeycloakId} />}
        {activeTab === 'jurnale' && <JurnaleTab jurnale={fisa.jurnale} />}
      </div>
    </div>
  );
};

/* === TAB COMPONENTS === */

const EvaluariTab = ({ evaluari, terapeutKeycloakId, onRefresh }) => {
  const [editingId, setEditingId] = useState(null);
  const [editData, setEditData] = useState({});
  const [saving, setSaving] = useState(false);
  const [message, setMessage] = useState(null);
  const [servicii, setServicii] = useState([]);

  // incarcam lista de servicii (pentru dropdown-ul din edit mode)
  useEffect(() => {
    evaluariService.getAllServicii()
      .then(data => setServicii(data || []))
      .catch(() => setServicii([]));
  }, []);

  if (!evaluari || evaluari.length === 0) {
    return <div className="tab-empty">Nu există evaluări pentru acest pacient.</div>;
  }

  const startEdit = (ev) => {
    setEditingId(ev.id);
    setEditData({
      diagnostic: ev.diagnostic || '',
      sedinteRecomandate: ev.sedinteRecomandate || 10,
      serviciuRecomandatId: ev.serviciuRecomandatId || '',
      observatii: ev.observatii || ''
    });
    setMessage(null);
  };

  const cancelEdit = () => {
    setEditingId(null);
    setEditData({});
  };

  const saveEdit = async (ev) => {
    try {
      setSaving(true);
      await evaluariService.updateEvaluare(ev.id, {
        diagnostic: editData.diagnostic,
        sedinteRecomandate: Number(editData.sedinteRecomandate),
        serviciuRecomandatId: editData.serviciuRecomandatId ? Number(editData.serviciuRecomandatId) : null,
        observatii: editData.observatii
      });
      setMessage({ type: 'success', text: 'Evaluare actualizată!' });
      setEditingId(null);
      onRefresh();
      setTimeout(() => setMessage(null), 3000);
    } catch (err) {
      setMessage({ type: 'error', text: err.message });
    } finally {
      setSaving(false);
    }
  };

  return (
    <div className="evaluari-list">
      {message && (
        <div className={`fisa-inline-message ${message.type}`}>{message.text}</div>
      )}
      {evaluari.map((ev, idx) => {
        const isOwnEvaluation = ev.terapeutKeycloakId === terapeutKeycloakId;
        const isEditing = editingId === ev.id;

        return (
          <div key={ev.id || idx} className={`evaluare-card ${isEditing ? 'editing' : ''}`}>
            <div className="evaluare-header">
              <div className="evaluare-header-left">
                <span className={`evaluare-tip ${ev.tipEvaluare?.includes('Inițială') ? 'initiala' : 'reevaluare'}`}>
                  {ev.tipEvaluare}
                </span>
                {!isOwnEvaluation && ev.numeTerapeut && (
                  <span className="evaluare-alt-terapeut">👤 {ev.numeTerapeut}</span>
                )}
              </div>
              <div className="evaluare-header-right">
                <span className="evaluare-data">
                  {ev.data ? new Date(ev.data).toLocaleDateString('ro-RO') : '—'}
                </span>
                {isOwnEvaluation && !isEditing && (
                  <button className="fisa-edit-btn" onClick={() => startEdit(ev)} title="Editează">
                    ✏️
                  </button>
                )}
              </div>
            </div>
            <div className="evaluare-body">
              {isEditing ? (
                <>
                  <div className="evaluare-row">
                    <span className="ev-label">Diagnostic</span>
                    <textarea
                      value={editData.diagnostic}
                      onChange={(e) => setEditData({ ...editData, diagnostic: e.target.value })}
                      className="fisa-edit-textarea"
                      rows="3"
                    />
                  </div>
                  <div className="evaluare-row">
                    <span className="ev-label">Serviciu Recomandat</span>
                    <select
                      value={editData.serviciuRecomandatId}
                      onChange={(e) => setEditData({ ...editData, serviciuRecomandatId: e.target.value })}
                      className="fisa-edit-input"
                    >
                      <option value="">— Selectează —</option>
                      {servicii.map(s => (
                        <option key={s.id} value={s.id}>{s.nume}</option>
                      ))}
                    </select>
                  </div>
                  <div className="evaluare-row">
                    <span className="ev-label">Ședințe Recomandate</span>
                    <input
                      type="number"
                      value={editData.sedinteRecomandate}
                      onChange={(e) => setEditData({ ...editData, sedinteRecomandate: e.target.value })}
                      className="fisa-edit-input"
                      min="1"
                      max="50"
                    />
                  </div>
                  <div className="evaluare-row">
                    <span className="ev-label">Observații</span>
                    <textarea
                      value={editData.observatii}
                      onChange={(e) => setEditData({ ...editData, observatii: e.target.value })}
                      className="fisa-edit-textarea"
                      rows="2"
                      placeholder="Observații..."
                    />
                  </div>
                  <div className="fisa-edit-actions">
                    <button className="fisa-save-btn" onClick={() => saveEdit(ev)} disabled={saving}>
                      {saving ? 'Se salvează...' : '💾 Salvează'}
                    </button>
                    <button className="fisa-cancel-btn" onClick={cancelEdit}>
                      ✕ Anulează
                    </button>
                  </div>
                </>
              ) : (
                <>
                  <div className="evaluare-row">
                    <span className="ev-label">Diagnostic</span>
                    <span className="ev-value">{ev.diagnostic || '—'}</span>
                  </div>
                  <div className="evaluare-row">
                    <span className="ev-label">Serviciu Recomandat</span>
                    <span className="ev-value">{ev.serviciuRecomandat || '—'}</span>
                  </div>
                  <div className="evaluare-row">
                    <span className="ev-label">Ședințe Recomandate</span>
                    <span className="ev-value">{ev.sedinteRecomandate || '—'}</span>
                  </div>
                  {ev.observatii && (
                    <div className="evaluare-observatii">
                      <span className="ev-label">Observații</span>
                      <p>{ev.observatii}</p>
                    </div>
                  )}
                </>
              )}
            </div>
          </div>
        );
      })}
    </div>
  );
};

const EvolutiiTab = ({ evolutii, onRefresh }) => {
  const [editingId, setEditingId] = useState(null);
  const [editText, setEditText] = useState('');
  const [saving, setSaving] = useState(false);
  const [message, setMessage] = useState(null);

  if (!evolutii || evolutii.length === 0) {
    return <div className="tab-empty">Nu există note de evoluție pentru acest pacient.</div>;
  }

  const startEdit = (ev) => {
    setEditingId(ev.id);
    setEditText(ev.observatii || '');
    setMessage(null);
  };

  const cancelEdit = () => {
    setEditingId(null);
    setEditText('');
  };

  const saveEdit = async (ev) => {
    if (!editText.trim()) return;
    try {
      setSaving(true);
      await evolutiiService.updateEvolutie(ev.id, { observatii: editText });
      setMessage({ type: 'success', text: 'Notă actualizată!' });
      setEditingId(null);
      onRefresh();
      setTimeout(() => setMessage(null), 3000);
    } catch (err) {
      setMessage({ type: 'error', text: err.message });
    } finally {
      setSaving(false);
    }
  };

  return (
    <div className="evolutii-timeline">
      {message && (
        <div className={`fisa-inline-message ${message.type}`}>{message.text}</div>
      )}
      {evolutii.map((ev, idx) => {
        const isEditing = editingId === ev.id;

        return (
          <div key={ev.id || idx} className={`evolutie-item ${isEditing ? 'editing' : ''}`}>
            <div className="evolutie-header-row">
              <div className="evolutie-date">
                {ev.createdAt ? new Date(ev.createdAt).toLocaleDateString('ro-RO') : '—'}
              </div>
              {!isEditing && (
                <button className="fisa-edit-btn" onClick={() => startEdit(ev)} title="Editează">
                  ✏️
                </button>
              )}
            </div>
            <div className="evolutie-content">
              {isEditing ? (
                <>
                  <textarea
                    value={editText}
                    onChange={(e) => setEditText(e.target.value)}
                    className="fisa-edit-textarea"
                    rows="3"
                  />
                  <div className="fisa-edit-actions">
                    <button className="fisa-save-btn" onClick={() => saveEdit(ev)} disabled={saving || !editText.trim()}>
                      {saving ? 'Se salvează...' : '💾 Salvează'}
                    </button>
                    <button className="fisa-cancel-btn" onClick={cancelEdit}>
                      ✕ Anulează
                    </button>
                  </div>
                </>
              ) : (
                <p>{ev.observatii}</p>
              )}
            </div>
          </div>
        );
      })}
    </div>
  );
};

const ProgramariTab = ({ programari, terapeutKeycloakId }) => {
  const [showAnulate, setShowAnulate] = useState(false);

  if (!programari || programari.length === 0) {
    return <div className="tab-empty">Nu există programări pentru acest pacient.</div>;
  }

  const locationCounts = {};
  programari.forEach(p => {
    if (p.numeLocatie) {
      locationCounts[p.numeLocatie] = (locationCounts[p.numeLocatie] || 0) + 1;
    }
  });
  const defaultLocation = Object.entries(locationCounts)
    .sort((a, b) => b[1] - a[1])[0]?.[0];

  const getStatusClass = (status) => {
    if (!status) return '';
    const s = status.toString().toLowerCase();
    if (s.includes('programat') || s === 'programata') return 'status-programata';
    if (s.includes('finalizat') || s === 'finalizata') return 'status-finalizata';
    if (s.includes('anulat') || s === 'anulata') return 'status-anulata';
    if (s.includes('neprezentare')) return 'status-anulata';
    return '';
  };

  const getStatusLabel = (status) => {
    if (!status) return '—';
    switch(status) {
      case 'PROGRAMATA': return 'Programată';
      case 'FINALIZATA': return 'Finalizată';
      case 'ANULATA_PACIENT': return 'Anulată (pacient)';
      case 'ANULATA_TERAPEUT': return 'Anulată (terapeut)';
      case 'NEPREZENTARE': return 'Neprezentare';
      default: return status;
    }
  };

  const isAnulata = (status) => {
    if (!status) return false;
    const s = status.toString().toLowerCase();
    return s.includes('anulat') || s.includes('neprezentare');
  };

  const anulateCount = programari.filter(p => isAnulata(p.status)).length;
  const filteredProgramari = showAnulate
    ? programari
    : programari.filter(p => !isAnulata(p.status));

  return (
    <div className="programari-tab-wrapper">
      {anulateCount > 0 && (
        <button
          className={`programari-filter-toggle ${showAnulate ? 'active' : ''}`}
          onClick={() => setShowAnulate(!showAnulate)}
        >
          {showAnulate
            ? `✕ Ascunde anulate (${anulateCount})`
            : `Arată și anulate (${anulateCount})`
          }
        </button>
      )}

      <div className="programari-list">
        {filteredProgramari.map((prog, idx) => {
          const isOwnAppointment = prog.terapeutKeycloakId === terapeutKeycloakId;
          const showLocation = prog.numeLocatie && prog.numeLocatie !== defaultLocation;

          return (
            <div key={prog.id || idx} className={`programare-card ${getStatusClass(prog.status)}`}>
              <div className="programare-header">
                <div className="programare-date">
                  <span className="prog-day">
                    {prog.data ? new Date(prog.data).toLocaleDateString('ro-RO', { day: 'numeric', month: 'short', year: 'numeric' }) : '—'}
                  </span>
                  <span className="prog-time">
                    {prog.oraInceput?.substring(0, 5)} - {prog.oraSfarsit?.substring(0, 5)}
                  </span>
                </div>
                <span className={`programare-status ${getStatusClass(prog.status)}`}>
                  {getStatusLabel(prog.status)}
                </span>
              </div>
              <div className="programare-body">
                {prog.tipServiciu && (
                  <span className="prog-service">{prog.tipServiciu}</span>
                )}
                {!isOwnAppointment && prog.numeTerapeut && (
                  <span className="prog-alt-terapeut">👤 {prog.numeTerapeut}</span>
                )}
                {showLocation && (
                  <span className="prog-locatie">📍 {prog.numeLocatie}</span>
                )}
              </div>
              {isAnulata(prog.status) && prog.motivAnulare && (
                <div className="programare-motiv-anulare">
                  <span className="motiv-label">Motiv:</span> {prog.motivAnulare}
                </div>
              )}
            </div>
          );
        })}
      </div>
    </div>
  );
};

const JurnaleTab = ({ jurnale }) => {
  if (!jurnale || jurnale.length === 0) {
    return <div className="tab-empty">Nu există jurnale completate de pacient.</div>;
  }

  return (
    <div className="jurnale-list">
      {jurnale.map((j, idx) => (
        <div key={j.id || idx} className="jurnal-card">
          <div className="jurnal-header">
            <span className="jurnal-date">
              {j.dataJurnal ? new Date(j.dataJurnal).toLocaleDateString('ro-RO') : '—'}
            </span>
            {j.oraSedinta && (
              <span className="jurnal-ora">Ora: {j.oraSedinta?.substring(0, 5)}</span>
            )}
          </div>
          <div className="jurnal-metrics">
            <div className="metric">
              <span className="metric-label">Durere</span>
              <div className="metric-bar-container">
                <div className="metric-bar durere" style={{ width: `${(j.nivelDurere || 0) * 10}%` }}></div>
              </div>
              <span className="metric-value">{j.nivelDurere}/10</span>
            </div>
            <div className="metric">
              <span className="metric-label">Dificultate</span>
              <div className="metric-bar-container">
                <div className="metric-bar dificultate" style={{ width: `${(j.dificultateExercitii || 0) * 10}%` }}></div>
              </div>
              <span className="metric-value">{j.dificultateExercitii}/10</span>
            </div>
            <div className="metric">
              <span className="metric-label">Oboseală</span>
              <div className="metric-bar-container">
                <div className="metric-bar oboseala" style={{ width: `${(j.nivelOboseala || 0) * 10}%` }}></div>
              </div>
              <span className="metric-value">{j.nivelOboseala}/10</span>
            </div>
          </div>
          {j.comentarii && (
            <div className="jurnal-comentarii">
              <span className="ev-label">Comentarii</span>
              <p>{j.comentarii}</p>
            </div>
          )}
        </div>
      ))}
    </div>
  );
};

export default FisaPacient;
