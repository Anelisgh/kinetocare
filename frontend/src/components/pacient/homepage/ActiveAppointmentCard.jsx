import React from 'react';

const ActiveAppointmentCard = ({ programare, onCancel }) => {
  if (!programare) return null;

  return (
    <div className="homepage-appointment-card">
      <h3>Următoarea Ședință</h3>

      <div className="homepage-appointment-time-section">
        <div className="homepage-appointment-time">
          {programare.oraInceput.substring(0, 5)}
        </div>
        <div className="homepage-appointment-date-section">
          <p className="homepage-appointment-date">
            {new Date(programare.data).toLocaleDateString('ro-RO', { weekday: 'long', day: 'numeric', month: 'long' })}
          </p>
          <p className="homepage-appointment-service">{programare.tipServiciu}</p>
        </div>
      </div>

      <div className="homepage-appointment-footer">
        <span className="homepage-appointment-price">{programare.pret} RON</span>
        <button
          onClick={() => onCancel(programare.id)}
          className="homepage-cancel-btn"
        >
          Anulează
        </button>
      </div>
    </div>
  );
};

export default ActiveAppointmentCard;
