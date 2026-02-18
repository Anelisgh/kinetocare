package com.example.programari_service.mapper;

import com.example.programari_service.dto.NotificareEvent;
import com.example.programari_service.entity.Programare;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

// transforma datele brute in text prietenos pentru user si se contruieste link-ul dinamic
@Component
public class NotificareEventMapper {

    private static final Locale RO = Locale.of("ro", "RO");
    private static final DateTimeFormatter FORMAT_DATA = DateTimeFormatter.ofPattern("dd MMMM", RO);
    private static final DateTimeFormatter FORMAT_ORA = DateTimeFormatter.ofPattern("HH:mm");

    // Conversii Programare -> NotificareEvent

    // PROGRAMARE_NOUA → terapeut
    public NotificareEvent toProgramareNoua(Programare p) {
        return toEventTerapeut(p, "PROGRAMARE_NOUA",
                "Programare nouă",
                "Ai o programare nouă pe " + formatData(p.getData()) + " la ora " + formatOra(p.getOraInceput()) + ".",
                "/calendar");
    }

    // EVALUARE_INITIALA_NOUA → terapeut
    public NotificareEvent toEvaluareInitialaNoua(Programare p) {
        return toEventTerapeut(p, "EVALUARE_INITIALA_NOUA",
                "Cerere evaluare inițială",
                "Un pacient nou s-a programat pentru evaluare inițială pe " + formatData(p.getData()) + " la ora " + formatOra(p.getOraInceput()) + ".",
                "/calendar");
    }

    // PROGRAMARE_ANULATA_DE_PACIENT → terapeut
    public NotificareEvent toProgramareAnulataDePacient(Programare p) {
        return toEventTerapeut(p, "PROGRAMARE_ANULATA_DE_PACIENT",
                "Programare anulată de pacient",
                "O programare din " + formatData(p.getData()) + " la ora " + formatOra(p.getOraInceput()) + " a fost anulată de pacient.",
                "/calendar");
    }

    // REEVALUARE_NECESARA → terapeut
    public NotificareEvent toReevaluareNecesara(Programare p) {
        return toEventTerapeut(p, "REEVALUARE_NECESARA",
                "Pacient necesită re-evaluare",
                "Un pacient s-a programat pe " + formatData(p.getData()) + " la ora " + formatOra(p.getOraInceput()) + ". Atenție: necesită reevaluare periodică.",
                "/calendar");
    }

    // JURNAL_COMPLETAT → terapeut (fara Programare, doar ids)
    public NotificareEvent toJurnalCompletat(Long terapeutId, Long pacientId, Long programareId) {
        return NotificareEvent.builder()
                .tipNotificare("JURNAL_COMPLETAT")
                .userId(terapeutId)
                .tipUser("TERAPEUT")
                .titlu("Jurnal completat")
                .mesaj("Un pacient a completat jurnalul pentru o ședință.")
                .entitateLegataId(programareId)
                .tipEntitateLegata("PROGRAMARE")
                .urlActiune("/fisa-pacient/" + pacientId)
                .build();
    }

    // PROGRAMARE_ANULATA_DE_TERAPEUT → pacient
    public NotificareEvent toProgramareAnulataDeTerapeut(Programare p) {
        return toEventPacient(p, "PROGRAMARE_ANULATA_DE_TERAPEUT",
                "Programare anulată de terapeut",
                "Programarea din " + formatData(p.getData()) + " la ora " + formatOra(p.getOraInceput()) + " a fost anulată de terapeut.",
                "/programari");
    }

    // REMINDER_24H → pacient
    public NotificareEvent toReminder24h(Programare p) {
        return toEventPacient(p, "REMINDER_24H",
                "Reminder: programare mâine",
                "Ai o programare mâine, " + formatData(p.getData()) + ", la ora " + formatOra(p.getOraInceput()) + ".",
                "/programari");
    }

    // REMINDER_2H → pacient
    public NotificareEvent toReminder2h(Programare p) {
        return toEventPacient(p, "REMINDER_2H",
                "Reminder: programare în curând",
                "Ai o programare în curând, astăzi la ora " + formatOra(p.getOraInceput()) + ".",
                "/programari");
    }

    // REMINDER_JURNAL → pacient
    public NotificareEvent toReminderJurnal(Programare p) {
        return toEventPacient(p, "REMINDER_JURNAL",
                "Completează jurnalul",
                "Ședința din " + formatData(p.getData()) + " a fost finalizată. Nu uita să completezi jurnalul!",
                "/jurnal");
    }

    // REEVALUARE_RECOMANDATA → pacient
    public NotificareEvent toReevaluareRecomandata(Programare p) {
        return toEventPacient(p, "REEVALUARE_RECOMANDATA",
                "Re-evaluare recomandată",
                "Ai finalizat toate ședințele recomandate. Te rugăm să te programezi pentru o re-evaluare.",
                "/programari");
    }

    // Helpers

    // helper comun pentru notificari catre terapeut
    private NotificareEvent toEventTerapeut(Programare p, String tip, String titlu, String mesaj, String url) {
        return NotificareEvent.builder()
                .tipNotificare(tip)
                .userId(p.getTerapeutId())
                .tipUser("TERAPEUT")
                .titlu(titlu)
                .mesaj(mesaj)
                .entitateLegataId(p.getId())
                .tipEntitateLegata("PROGRAMARE")
                .urlActiune(url)
                .build();
    }

    // helper comun pentru notificari catre pacient
    private NotificareEvent toEventPacient(Programare p, String tip, String titlu, String mesaj, String url) {
        return NotificareEvent.builder()
                .tipNotificare(tip)
                .userId(p.getPacientId())
                .tipUser("PACIENT")
                .titlu(titlu)
                .mesaj(mesaj)
                .entitateLegataId(p.getId())
                .tipEntitateLegata("PROGRAMARE")
                .urlActiune(url)
                .build();
    }

    // formateaza data in romana: "17 februarie"
    private String formatData(LocalDate data) {
        return data.format(FORMAT_DATA);
    }

    // formateaza ora: "14:30"
    private String formatOra(LocalTime ora) {
        return ora.format(FORMAT_ORA);
    }
}
