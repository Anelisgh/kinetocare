import React, { useState } from 'react';
import '../../../styles/ProgramariPacient.css';

const HistoryAppointmentCard = ({ programare }) => {
    const [expanded, setExpanded] = useState(false);

    const getStatusColor = (status) => {
        switch (status) {
            case 'FINALIZATA': return '#2ecc71'; // Green
            case 'PROGRAMATA': return '#3498db'; // Blue
            case 'ANULATA': return '#e74c3c'; // Red
            default: return '#95a5a6'; // Gray
        }
    };

    const getStatusLabel = (status) => {
        switch (status) {
            case 'FINALIZATA': return 'Finalizată';
            case 'PROGRAMATA': return 'Programată';
            case 'ANULATA': return 'Anulată';
            default: return status;
        }
    };

    return (
        <div className="history-card">
            <div className="history-header">
                <div style={{ display: 'flex', flexDirection: 'column' }}>
                    <span className="history-date">
                        {new Date(programare.data).toLocaleDateString('ro-RO', { weekday: 'long', day: 'numeric', month: 'long', year: 'numeric' })}
                    </span>
                    <span className="history-service-type">
                        {programare.tipServiciu}
                    </span>
                </div>
                
                <div style={{ textAlign: 'right' }}>
                    <span className="history-status-badge" style={{
                        backgroundColor: `${getStatusColor(programare.status)}20`,
                        color: getStatusColor(programare.status),
                    }}>
                        {getStatusLabel(programare.status)}
                    </span>
                    <div className="history-time">
                        {programare.oraInceput.substring(0, 5)} - {programare.oraSfarsit.substring(0, 5)}
                    </div>
                </div>
            </div>

            <div className="history-details-row">
                <div>
                    <strong>Terapeut:</strong> {programare.numeTerapeut}
                </div>
                <div>
                    <strong>Locație:</strong> {programare.numeLocatie}
                </div>
                <div>
                    <strong>Preț:</strong> {programare.pret} RON
                </div>
            </div>

            {/* Journal Section */}
            {programare.status === 'FINALIZATA' && (
                <div className="history-journal-section">
                    {programare.detaliiJurnal ? (
                        <div className="history-journal-box">
                            <div className="history-journal-title">📝 Jurnal Recuperare</div>
                            <div className="history-journal-metrics">
                                <span title="Nivel Durere">🤕 Durere: <strong>{programare.detaliiJurnal.nivelDurere}/10</strong></span>
                                <span title="Dificultate Exerciții">🏋️ Dificultate: <strong>{programare.detaliiJurnal.dificultateExercitii}/10</strong></span>
                                <span title="Nivel Oboseală">🔋 Oboseală: <strong>{programare.detaliiJurnal.nivelOboseala}/10</strong></span>
                            </div>
                            {programare.detaliiJurnal.comentarii && (
                                <div className="history-journal-comment">
                                    "{programare.detaliiJurnal.comentarii}"
                                </div>
                            )}
                        </div>
                    ) : (
                        <div className="history-journal-btn-container">
                            {/* Aici poți pune link-ul către un modal sau acțiune de completare */}
                            <button className="history-journal-btn" onClick={() => alert('Deschide modal completare jurnal')}>
                                ✏️ Completează Jurnal
                            </button>
                        </div>
                    )}
                </div>
            )}

            {/* Expanded Details for Evaluation or Cancellation */}
            {(programare.areEvaluare || programare.status === 'ANULATA') && (
                <div>
                    <button 
                        onClick={() => setExpanded(!expanded)}
                        className="history-expanded-btn"
                    >
                        {expanded ? 'Ascunde detalii' : 'Vezi detalii'}
                    </button>

                    {expanded && (
                        <div className="history-expanded-content">
                            {programare.status === 'ANULATA' && (
                                <div className="history-detail-item" style={{ color: '#e74c3c' }}>
                                    <strong className="history-detail-label">Motiv Anulare:</strong> {programare.motivAnulare || 'Nespecificat'}
                                </div>
                            )}

                            {programare.areEvaluare && (
                                <>
                                    <h4 style={{ margin: '0 0 0.5rem 0', color: '#2c3e50' }}>Detalii Evaluare</h4>
                                    {programare.diagnostic && (
                                        <div className="history-detail-item">
                                            <strong className="history-detail-label">Diagnostic:</strong> {programare.diagnostic}
                                        </div>
                                    )}
                                    {programare.serviciuRecomandat && (
                                        <div className="history-detail-item">
                                            <strong className="history-detail-label">Serviciu Recomandat:</strong> {programare.serviciuRecomandat}
                                        </div>
                                    )}
                                    {programare.sedinteRecomandate && (
                                        <div className="history-detail-item">
                                            <strong className="history-detail-label">Ședințe Recomandate:</strong> {programare.sedinteRecomandate}
                                        </div>
                                    )}
                                    {programare.observatii && (
                                        <div className="history-detail-item">
                                            <strong className="history-detail-label">Observații:</strong> {programare.observatii}
                                        </div>
                                    )}
                                </>
                            )}
                        </div>
                    )}
                </div>
            )}
        </div>
    );
};

export default HistoryAppointmentCard;