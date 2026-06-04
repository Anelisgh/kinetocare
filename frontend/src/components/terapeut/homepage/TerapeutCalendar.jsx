import React, { useRef, useEffect } from 'react';
import FullCalendar from '@fullcalendar/react';
import dayGridPlugin from '@fullcalendar/daygrid';
import timeGridPlugin from '@fullcalendar/timegrid';
import interactionPlugin from '@fullcalendar/interaction';
import roLocale from '@fullcalendar/core/locales/ro';
import { programariService } from '../../../services/programariService';

// Calendarul terapeutului folosind FullCalendar
const TerapeutCalendar = ({ onEventClick, refreshTrigger, locatieId }) => {
    const calendarRef = useRef(null);

    // Paleta de 20 de culori pastelate reordonată pentru contrast maxim între pacienți consecutivi
    const COLORS_PALETTE = [
        { bg: '#e0f2fe', border: '#0284c7' }, // 1. Sky Blue
        { bg: '#fee2e2', border: '#dc2626' }, // 2. Red
        { bg: '#dcfce7', border: '#16a34a' }, // 3. Green
        { bg: '#fef3c7', border: '#d97706' }, // 4. Amber
        { bg: '#f3e8ff', border: '#9333ea' }, // 5. Purple
        { bg: '#ffedd5', border: '#ea580c' }, // 6. Orange
        { bg: '#ccfbf1', border: '#0d9488' }, // 7. Teal
        { bg: '#fce7f3', border: '#db2777' }, // 8. Pink
        { bg: '#ecfccb', border: '#65a30d' }, // 9. Lime
        { bg: '#e0e7ff', border: '#4f46e5' }, // 10. Indigo
        { bg: '#ffe4e6', border: '#e11d48' }, // 11. Rose
        { bg: '#cffafe', border: '#0891b2' }, // 12. Cyan
        { bg: '#fffaee', border: '#b45309' }, // 13. Apricot
        { bg: '#d1fae5', border: '#059669' }, // 14. Emerald
        { bg: '#ede9fe', border: '#7c3aed' }, // 15. Violet
        { bg: '#fef9c3', border: '#ca8a04' }, // 16. Yellow
        { bg: '#fae8ff', border: '#c026d3' }, // 17. Fuchsia
        { bg: '#f1f5f9', border: '#475569' }, // 18. Slate
        { bg: '#f5f5f4', border: '#57534e' }, // 19. Stone
        { bg: '#e2e8f0', border: '#334155' }, // 20. Blue-Gray
    ];

    const fetchEvents = async (info, successCallback, failureCallback) => {
        try {
            const startStr = info.start.toISOString().split('T')[0];
            const endStr = info.end.toISOString().split('T')[0];

            // Pasam locatieId catre backend (daca e selectat un ID, altfel e string gol/null)
            // Backend-ul filtreaza locatia.
            let data = await programariService.getCalendarAppointments(
                startStr,
                endStr,
                locatieId || null
            );

            // 2. FILTRAREA INTELIGENTA
            const filteredData = data.filter(appt => {
                // Pstram tot ce e PROGRAMATA sau FINALIZATA
                if (appt.status !== 'ANULATA') return true;

                // Dacă e ANULATA, o pstrăm DOAR daca motivul e NEPREZENTARE
                if (appt.status === 'ANULATA' && appt.motivAnulare === 'NEPREZENTARE') {
                    return true;
                }

                // Altfel (anulat de pacient, administrativ etc.), o ascundem
                return false;
            });

            // Generam culorile secvential pentru fiecare pacient UNIC din aceasta saptamana
            const patientColorMap = {};
            let nextColorIndex = 0;

            const events = filteredData.map(appt => {
                const patientName = appt.title || 'Necunoscut';
                if (patientColorMap[patientName] === undefined) {
                    patientColorMap[patientName] = nextColorIndex % COLORS_PALETTE.length;
                    nextColorIndex++;
                }
                const colorObj = COLORS_PALETTE[patientColorMap[patientName]];

                return {
                    id: appt.id,
                    title: appt.title,
                    start: appt.start,
                    end: appt.end,
                    // O facem vizual distincta (mai stearsa) daca e neprezentare
                    display: 'auto',
                    extendedProps: {
                        numeLocatie: appt.numeLocatie,
                        tipServiciu: appt.tipServiciu,
                        status: appt.status,
                        motivAnulare: appt.motivAnulare,
                        primaIntalnire: appt.primaIntalnire,
                        areJurnal: appt.areJurnal,
                        telefonPacient: appt.telefonPacient,
                        // Alocam culoarea garantat distincta
                        patientBg: colorObj.bg,
                        patientBorder: colorObj.border
                    }
                };
            });

            successCallback(events);
        } catch (error) {
            console.error(error);
            failureCallback(error);
        }
    };

    // Trigger refresh cand se schimba filtrele
    useEffect(() => {
        if (calendarRef.current) {
            calendarRef.current.getApi().refetchEvents();
        }
    }, [refreshTrigger, locatieId]);



    // Pastram doar clasele functionale pentru text taiat / extra styling (nu culorile de status)
    const getStatusClass = (props) => {
        let classes = [];
        if (props.status === 'ANULATA') classes.push('status-anulata');
        if (props.primaIntalnire) classes.push('prima-intalnire');
        return classes.join(' ');
    };

    // 4. Custom Rendering pentru "Casuta"
    const renderEventContent = (eventInfo) => {
        const props = eventInfo.event.extendedProps;
        const patientName = eventInfo.event.title;

        // Folosim culorile precalculate in fetchEvents
        const eventStyle = {
            backgroundColor: props.patientBg,
            borderLeftColor: props.patientBorder
        };

        return (
            <div 
                className={`custom-event-card ${getStatusClass(props)}`}
                style={eventStyle}
            >
                <div className="event-time">
                    {eventInfo.timeText}
                </div>
                <div className="event-title">
                    {props.primaIntalnire && <span title="Prima Întâlnire" style={{ marginRight: '4px' }}>⭐</span>}
                    {patientName}
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
// 5. Returnam calendarul
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