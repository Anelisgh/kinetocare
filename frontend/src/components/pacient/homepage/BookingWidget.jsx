import React, { useState, useEffect } from 'react';
import { programariService } from '../../../services/programariService';
import '../../../styles/HomepagePacient.css';

// widget pentru calendarul de programare
const BookingWidget = ({ pacientId, terapeutId, locatieId, onSuccess }) => {
  const [selectedDate, setSelectedDate] = useState(null);
  const [slots, setSlots] = useState([]);
  const [loadingSlots, setLoadingSlots] = useState(false);
  const [datesList, setDatesList] = useState([]);
  const [currentWeekOffset, setCurrentWeekOffset] = useState(0);
  const [serviciuRecomandat, setServiciuRecomandat] = useState(null);
  const [loadingService, setLoadingService] = useState(true);

  // generam datele si aflam serviciul recomandat
  useEffect(() => {
    const initWidget = async () => {
      // generare date calendar pt 30zile
      const dates = [];
      const today = new Date();
      for (let i = 0; i < 30; i++) {
        const d = new Date(today);
        d.setDate(today.getDate() + i);
        dates.push(d);
      }
      setDatesList(dates);
      setSelectedDate(dates[0]);

      // aflam ce serviciul trebuie sa faca pacientul
      try {
        const serviciu = await programariService.getServiciuRecomandat(pacientId);
        setServiciuRecomandat(serviciu);
      } catch (err) {
        console.error("Eroare la preluarea serviciului", err);
      } finally {
        setLoadingService(false);
      }
    };

    initWidget();
  }, [pacientId]);

  // cand se schimba data SAU serviciul a fost incarcat adaucem sloturile
  useEffect(() => {
    if (!selectedDate || !terapeutId || !locatieId || !serviciuRecomandat) return;

    const fetchSlots = async () => {
      setLoadingSlots(true);
      setSlots([]);
      try {
        const offset = selectedDate.getTimezoneOffset();
        const localDate = new Date(selectedDate.getTime() - (offset * 60 * 1000));
        const dateStr = localDate.toISOString().split('T')[0];
        const available = await programariService.getDisponibilitate(
          terapeutId,
          locatieId,
          dateStr,
          serviciuRecomandat.id
        );
        setSlots(available);
      } catch (error) {
        console.error("Nu am putut încărca orarul", error);
      } finally {
        setLoadingSlots(false);
      }
    };

    fetchSlots();
  }, [selectedDate, terapeutId, locatieId, serviciuRecomandat]);

  // creare programare
  const handleBooking = async (timeSlot) => {
    if (!serviciuRecomandat) return;

    const confirmMsg = `Confirmi programarea pentru ${serviciuRecomandat.nume} (${serviciuRecomandat.durataMinute} min)\n` +
      `Pe data: ${selectedDate.toLocaleDateString('ro-RO')}\n` +
      `La ora: ${timeSlot}?`;

    if (!window.confirm(confirmMsg)) return;

    try {
      const offset = selectedDate.getTimezoneOffset();
      const localDate = new Date(selectedDate.getTime() - (offset * 60 * 1000));
      const dateStr = localDate.toISOString().split('T')[0];

      await programariService.createProgramare({
        pacientId: pacientId,
        terapeutId: terapeutId,
        locatieId: locatieId,
        data: dateStr,
        oraInceput: timeSlot
      });

      onSuccess();
    } catch (error) {
      alert(error.message);
    }
  };

  if (loadingService) {
    return <div className="booking-widget-loading">Se încarcă...</div>;
  }

  // calculam zilele vizibile (7 zile per pagina)
  const visibleDates = datesList.slice(currentWeekOffset * 7, (currentWeekOffset * 7) + 7);
  const maxOffset = Math.ceil(datesList.length / 7) - 1;

  return (
    <div className="booking-widget">
      {/* Header */}
      <div className="booking-widget-header">
        <div className="booking-widget-header-top">
          <h3 className="booking-widget-title">
            <svg className="booking-widget-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
              <rect x="3" y="4" width="18" height="18" rx="2" ry="2"></rect>
              <line x1="16" y1="2" x2="16" y2="6"></line>
              <line x1="8" y1="2" x2="8" y2="6"></line>
              <line x1="3" y1="10" x2="21" y2="10"></line>
            </svg>
            Programare Rapidă
          </h3>
          {serviciuRecomandat && (
            <span className="booking-widget-service-badge">
              {serviciuRecomandat.nume}
            </span>
          )}
        </div>
        <p className="booking-widget-subtitle">
          {serviciuRecomandat?.durataMinute} minute • Selectează data și ora dorită
        </p>
      </div>

      {/* Date Navigator cu sageti */}
      <div className="booking-date-navigator">
        {/* Sageata stanga */}
        <button
          onClick={() => setCurrentWeekOffset(Math.max(0, currentWeekOffset - 1))}
          disabled={currentWeekOffset === 0}
          className={`booking-nav-arrow ${currentWeekOffset === 0 ? 'disabled' : ''}`}
          aria-label="Săptămâna anterioară"
        >
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
            <polyline points="15 18 9 12 15 6"></polyline>
          </svg>
        </button>

        {/* Grid cu datele */}
        <div className="booking-dates-grid">
          {visibleDates.map((date, index) => {
            const isSelected = selectedDate?.toDateString() === date.toDateString();
            const isToday = new Date().toDateString() === date.toDateString();
            const isWeekend = date.getDay() === 0 || date.getDay() === 6;

            const dayNames = ['Dum', 'Lun', 'Mar', 'Mie', 'Joi', 'Vin', 'Sâm'];
            const dayName = dayNames[date.getDay()];

            return (
              <button
                key={index}
                onClick={() => setSelectedDate(date)}
                className={`booking-date-card ${isSelected ? 'selected' : ''} ${isToday ? 'today' : ''} ${isWeekend ? 'weekend' : ''}`}
              >
                <div className="booking-date-day">{dayName}</div>
                <div className="booking-date-number">{date.getDate()}</div>
                {isToday && !isSelected && (
                  <div className="booking-date-today-badge">Azi</div>
                )}
              </button>
            );
          })}
        </div>

        {/* Sageata dreapta */}
        <button
          onClick={() => setCurrentWeekOffset(Math.min(maxOffset, currentWeekOffset + 1))}
          disabled={currentWeekOffset === maxOffset}
          className={`booking-nav-arrow ${currentWeekOffset === maxOffset ? 'disabled' : ''}`}
          aria-label="Săptămâna următoare"
        >
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
            <polyline points="9 18 15 12 9 6"></polyline>
          </svg>
        </button>
      </div>

      {/* Separator */}
      <div className="booking-separator" />

      {/* Time Slots */}
      {selectedDate ? (
        <div className="booking-slots-section">
          <div className="booking-slots-header">
            <svg className="booking-widget-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
              <circle cx="12" cy="12" r="10"></circle>
              <polyline points="12 6 12 12 16 14"></polyline>
            </svg>
            <h3 className="booking-slots-title">
              Ore disponibile - {selectedDate.toLocaleDateString('ro-RO', { weekday: 'long', day: 'numeric', month: 'long' })}
            </h3>
          </div>

          {loadingSlots ? (
            <div className="booking-widget-slots-loading">Se încarcă...</div>
          ) : slots.length > 0 ? (
            <div className="booking-slots-grid">
              {slots.map(time => (
                <button
                  key={time}
                  onClick={() => handleBooking(time)}
                  className="booking-slot-btn"
                >
                  {time.substring(0, 5)}
                </button>
              ))}
            </div>
          ) : (
            <div className="booking-empty-state">
              <p className="booking-empty-text">Nu sunt ore disponibile pentru această dată</p>
            </div>
          )}
        </div>
      ) : (
        <div className="booking-empty-state">
          <svg className="booking-empty-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
            <rect x="3" y="4" width="18" height="18" rx="2" ry="2"></rect>
            <line x1="16" y1="2" x2="16" y2="6"></line>
            <line x1="8" y1="2" x2="8" y2="6"></line>
            <line x1="3" y1="10" x2="21" y2="10"></line>
          </svg>
          <p className="booking-empty-text">
            Selectează o dată pentru a vedea orele disponibile
          </p>
        </div>
      )}
    </div>
  );
};

export default BookingWidget;