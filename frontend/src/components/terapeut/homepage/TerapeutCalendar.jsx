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

    // Helper: construiește clase CSS bazate pe status

    const getStatusClass = (props) => {
        if (props.status === 'ANULATA') return 'status-anulata';
        if (props.status === 'FINALIZATA') return 'status-finalizata';
        if (props.status === 'PROGRAMATA' && props.primaIntalnire) return 'status-programata prima-intalnire';
        return 'status-programata';
    };

    // 4. Custom Rendering pentru "Căsuță"
    const renderEventContent = (eventInfo) => {
        const props = eventInfo.event.extendedProps;

        return (
            <div className={`custom-event-card ${getStatusClass(props)}`}>
                <div className="event-time">
                    {eventInfo.timeText}
                </div>
                <div className="event-title">
                    {eventInfo.event.title}
                </div>
                <div className="event-details">
                    {props.tipServiciu}
                </div>
                <div className="event-details event-details-location">
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