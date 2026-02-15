import React from 'react';

// fereastra laterala care apare peste calendar la click-ul unei programari ce contine detaliile programarii
const AppointmentSidePanel = ({ appointment, onClose, onCancel, onMarkNeprezentare }) => {
    if (!appointment) return null;

    // Datele vin din "extendedProps" ale evenimentului FullCalendar
    const { title, start, end, extendedProps } = appointment;
    const { numeLocatie, tipServiciu, status, telefonPacient } = extendedProps;

    // Verificam timpul curent
    const now = new Date();
    // Consideram ca a trecut dacă ora de sfarsit este mai mica decat ora curenta
    const isPast = end < now;

    // Formatare data/ora
    const dataFormatata = start.toLocaleDateString('ro-RO', { weekday: 'long', year: 'numeric', month: 'long', day: 'numeric' });
    const intervalOrar = `${start.toLocaleTimeString('ro-RO', { hour: '2-digit', minute: '2-digit' })} - ${end.toLocaleTimeString('ro-RO', { hour: '2-digit', minute: '2-digit' })}`;
    // doar daca e in viitor si e programata
    const isCancelable = status === 'PROGRAMATA' && !isPast;
    // doar daca e finalizata sau timpul a trecut
    const isMarkableAsNeprezentare = status !== 'ANULATA' && (status === 'FINALIZATA' || isPast);

    // Status badge class
    const getStatusClass = () => {
        if (status === 'ANULATA') return 'panel-status-badge status-anulata';
        if (status === 'FINALIZATA') return 'panel-status-badge status-finalizata';
        return 'panel-status-badge status-programata';
    };

    return (
        <div className="side-panel-overlay" onClick={onClose}>
            <div className="side-panel" onClick={(e) => e.stopPropagation()}>

                <div className="panel-header">
                    <h2>Detalii Programare</h2>
                    <button className="close-btn" onClick={onClose}>&times;</button>
                </div>

                <div className="panel-content">
                    <h3>{title}</h3>

                    {telefonPacient && (
                        <a href={`tel:${telefonPacient}`} className="panel-phone">
                            📞 {telefonPacient}
                        </a>
                    )}

                    <div className="panel-info-row">
                        <div className="panel-label">Când</div>
                        <div className="panel-value">{dataFormatata}</div>
                        <div className="panel-value">{intervalOrar}</div>
                    </div>

                    <div className="panel-info-row">
                        <div className="panel-label">Serviciu</div>
                        <div className="panel-value">{tipServiciu}</div>
                    </div>

                    <div className="panel-info-row">
                        <div className="panel-label">Locație</div>
                        <div className="panel-value">{numeLocatie || 'Nedefinită'}</div>
                    </div>

                    <div className="panel-info-row">
                        <div className="panel-label">Status</div>
                        <div className="panel-value">
                            <span className={getStatusClass()}>
                                {status}
                            </span>
                        </div>
                    </div>

                </div>

                <div className="panel-actions">
                    {/* BUTONUL ANULARE (DOAR PENTRU VIITOR) */}
                    {isCancelable && (
                        <button className="btn-cancel-appointment" onClick={() => onCancel(appointment.id)}>
                            Anulează Programarea
                        </button>
                    )}

                    {/* BUTON NEPREZENTARE (DOAR PENTRU PROGRAMARI MARCATE CA FINALIZATE) */}
                    {isMarkableAsNeprezentare && (
                        <button className="btn-mark-neprezentare" onClick={() => onMarkNeprezentare(appointment.id)}>
                            Marchează ca Neprezentare
                        </button>
                    )}
                </div>
            </div>
        </div>
    );
};

export default AppointmentSidePanel;