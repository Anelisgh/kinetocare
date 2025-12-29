import React, { useRef, useEffect } from 'react';
import FullCalendar from '@fullcalendar/react';
import dayGridPlugin from '@fullcalendar/daygrid';
import timeGridPlugin from '@fullcalendar/timegrid';
import interactionPlugin from '@fullcalendar/interaction';
import roLocale from '@fullcalendar/core/locales/ro';
import { programariService } from '../../../services/programariService';

const TerapeutCalendar = ({ terapeutId, onEventClick, refreshTrigger, locatieId }) => {
    const calendarRef = useRef(null);

    const fetchEvents = async (info, successCallback, failureCallback) => {
        try {
            const startStr = info.start.toISOString().split('T')[0];
            const endStr = info.end.toISOString().split('T')[0];

            // Pasăm locatieId către backend (dacă e selectat un ID, altfel e string gol/null)
            // Backend-ul filtrează locația.
            let data = await programariService.getCalendarAppointments(
                terapeutId,
                startStr,
                endStr,
                locatieId || null
            );

            // 2. FILTRAREA INTELIGENTĂ
            const filteredData = data.filter(appt => {
                // Păstrăm tot ce e PROGRAMATA sau FINALIZATA
                if (appt.status !== 'ANULATA') return true;

                // Dacă e ANULATA, o păstrăm DOAR dacă motivul e NEPREZENTARE
                if (appt.status === 'ANULATA' && appt.motivAnulare === 'NEPREZENTARE') {
                    return true;
                }

                // Altfel (anulat de pacient, administrativ etc.), o ascundem
                return false;
            });

            const events = filteredData.map(appt => ({
                id: appt.id,
                title: appt.title,
                start: appt.start,
                end: appt.end,
                // O facem vizual distinctă (mai ștearsă) dacă e neprezentare
                display: 'auto',
                extendedProps: {
                    numeLocatie: appt.numeLocatie,
                    tipServiciu: appt.tipServiciu,
                    status: appt.status,
                    motivAnulare: appt.motivAnulare,
                    primaIntalnire: appt.primaIntalnire,
                    areJurnal: appt.areJurnal,
                    telefonPacient: appt.telefonPacient
                }
            }));

            successCallback(events);
        } catch (error) {
            console.error(error);
            failureCallback(error);
        }
    };

    // Trigger refresh când se schimbă filtrele
    useEffect(() => {
        if (calendarRef.current) {
            calendarRef.current.getApi().refetchEvents();
        }
    }, [refreshTrigger, locatieId]);

    // 3. Logica Culorilor
    const getBorderColor = (props) => {
        if (props.status === 'ANULATA' && props.motivAnulare === 'NEPREZENTARE') {
            return '#EF4444'; // Roșu aprins
        }
        if (props.status === 'FINALIZATA') return '#22C55E'; // Verde
        if (props.status === 'PROGRAMATA' && props.primaIntalnire) return '#EAB308'; // Galben
        return '#3B82F6'; // Albastru (Default)
    };

    // 4. Custom Rendering pentru "Căsuță"
    const renderEventContent = (eventInfo) => {
        const props = eventInfo.event.extendedProps;
        const isCancelled = props.status === 'ANULATA';

        let borderColor = '#3B82F6'; // Default Blue
        if (props.status === 'FINALIZATA') borderColor = '#22C55E';
        else if (props.status === 'PROGRAMATA' && props.primaIntalnire) borderColor = '#EAB308';
        else if (isCancelled) borderColor = '#9CA3AF'; // Gri pt anulate

        return (
            <div
                className={`custom-event-card ${isCancelled ? 'status-anulata-visual' : ''}`}
                style={{
                    borderLeft: `5px solid ${borderColor}`,
                    opacity: isCancelled ? 0.6 : 1,
                    textDecoration: isCancelled ? 'line-through' : 'none',
                    backgroundColor: isCancelled ? '#f3f4f6' : '#fafafa',
                    height: '100%',
                    padding: '2px 4px',
                    borderRadius: '4px',
                    overflow: 'hidden'
                }}
            >
                <div className="event-time" style={{ fontWeight: 'bold', fontSize: '0.8em' }}>
                    {eventInfo.timeText}
                </div>
                <div className="event-title" style={{ fontWeight: '600', fontSize: '0.85em' }}>
                    {eventInfo.event.title}
                </div>
                <div className="event-details" style={{ fontSize: '0.75em' }}>
                    {props.tipServiciu}
                </div>
                <div className="event-details" style={{ fontSize: '0.75em', fontStyle: 'italic' }}>
                    📍 {props.numeLocatie}
                </div>
            </div>
        );
    };

    return (
        <FullCalendar
            ref={calendarRef}
            plugins={[dayGridPlugin, timeGridPlugin, interactionPlugin]}
            initialView="timeGridWeek"
            locale={roLocale}
            headerToolbar={{
                left: 'prev,next today',
                center: 'title',
                right: 'dayGridMonth,timeGridWeek'
            }}
            slotMinTime="08:00:00"
            slotMaxTime="20:00:00"
            allDaySlot={false}
            height="100%"
            events={fetchEvents}
            eventContent={renderEventContent}
            eventClick={onEventClick}
            slotEventOverlap={false}
        />
    );
};

export default TerapeutCalendar;