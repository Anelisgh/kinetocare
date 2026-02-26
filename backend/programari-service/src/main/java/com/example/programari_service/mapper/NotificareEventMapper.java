package com.example.programari_service.mapper;

import com.example.programari_service.dto.NotificareEvent;
import com.example.programari_service.entity.Programare;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

@Mapper(componentModel = "spring")
public interface NotificareEventMapper {

    default String formatData(LocalDate data) {
        if (data == null) return "";
        return data.format(DateTimeFormatter.ofPattern("dd MMMM", Locale.of("ro", "RO")));
    }

    default String formatOra(LocalTime ora) {
        if (ora == null) return "";
        return ora.format(DateTimeFormatter.ofPattern("HH:mm"));
    }

    @Mapping(target = "tipNotificare", constant = "PROGRAMARE_NOUA")
    @Mapping(target = "userKeycloakId", source = "keycloakId")
    @Mapping(target = "tipUser", constant = "TERAPEUT")
    @Mapping(target = "titlu", constant = "Programare nouă")
    @Mapping(target = "mesaj", expression = "java(\"Ai o programare nouă pe \" + formatData(p.getData()) + \" la ora \" + formatOra(p.getOraInceput()) + \".\")")
    @Mapping(target = "entitateLegataId", source = "p.id")
    @Mapping(target = "tipEntitateLegata", constant = "PROGRAMARE")
    @Mapping(target = "urlActiune", constant = "/calendar")
    NotificareEvent toProgramareNoua(Programare p, String keycloakId);

    @Mapping(target = "tipNotificare", constant = "EVALUARE_INITIALA_NOUA")
    @Mapping(target = "userKeycloakId", source = "keycloakId")
    @Mapping(target = "tipUser", constant = "TERAPEUT")
    @Mapping(target = "titlu", constant = "Cerere evaluare inițială")
    @Mapping(target = "mesaj", expression = "java(\"Un pacient nou s-a programat pentru evaluare inițială pe \" + formatData(p.getData()) + \" la ora \" + formatOra(p.getOraInceput()) + \".\")")
    @Mapping(target = "entitateLegataId", source = "p.id")
    @Mapping(target = "tipEntitateLegata", constant = "PROGRAMARE")
    @Mapping(target = "urlActiune", constant = "/calendar")
    NotificareEvent toEvaluareInitialaNoua(Programare p, String keycloakId);

    @Mapping(target = "tipNotificare", constant = "PROGRAMARE_ANULATA_DE_PACIENT")
    @Mapping(target = "userKeycloakId", source = "keycloakId")
    @Mapping(target = "tipUser", constant = "TERAPEUT")
    @Mapping(target = "titlu", constant = "Programare anulată de pacient")
    @Mapping(target = "mesaj", expression = "java(\"O programare din \" + formatData(p.getData()) + \" la ora \" + formatOra(p.getOraInceput()) + \" a fost anulată de pacient.\")")
    @Mapping(target = "entitateLegataId", source = "p.id")
    @Mapping(target = "tipEntitateLegata", constant = "PROGRAMARE")
    @Mapping(target = "urlActiune", constant = "/calendar")
    NotificareEvent toProgramareAnulataDePacient(Programare p, String keycloakId);

    @Mapping(target = "tipNotificare", constant = "REEVALUARE_NECESARA")
    @Mapping(target = "userKeycloakId", source = "keycloakId")
    @Mapping(target = "tipUser", constant = "TERAPEUT")
    @Mapping(target = "titlu", constant = "Pacient necesită re-evaluare")
    @Mapping(target = "mesaj", expression = "java(\"Un pacient s-a programat pe \" + formatData(p.getData()) + \" la ora \" + formatOra(p.getOraInceput()) + \". Atenție: necesită reevaluare periodică.\")")
    @Mapping(target = "entitateLegataId", source = "p.id")
    @Mapping(target = "tipEntitateLegata", constant = "PROGRAMARE")
    @Mapping(target = "urlActiune", constant = "/calendar")
    NotificareEvent toReevaluareNecesara(Programare p, String keycloakId);

    @Mapping(target = "tipNotificare", constant = "JURNAL_COMPLETAT")
    @Mapping(target = "userKeycloakId", source = "keycloakId")
    @Mapping(target = "tipUser", constant = "TERAPEUT")
    @Mapping(target = "titlu", constant = "Jurnal completat")
    @Mapping(target = "mesaj", constant = "Un pacient a completat jurnalul pentru o ședință.")
    @Mapping(target = "entitateLegataId", source = "programareId")
    @Mapping(target = "tipEntitateLegata", constant = "PROGRAMARE")
    @Mapping(target = "urlActiune", expression = "java(\"/fisa-pacient/\" + pacientId)")
    NotificareEvent toJurnalCompletat(Long pacientId, Long programareId, String keycloakId);

    @Mapping(target = "tipNotificare", constant = "PROGRAMARE_ANULATA_DE_TERAPEUT")
    @Mapping(target = "userKeycloakId", source = "keycloakId")
    @Mapping(target = "tipUser", constant = "PACIENT")
    @Mapping(target = "titlu", constant = "Programare anulată de terapeut")
    @Mapping(target = "mesaj", expression = "java(\"Programarea din \" + formatData(p.getData()) + \" la ora \" + formatOra(p.getOraInceput()) + \" a fost anulată de terapeut.\")")
    @Mapping(target = "entitateLegataId", source = "p.id")
    @Mapping(target = "tipEntitateLegata", constant = "PROGRAMARE")
    @Mapping(target = "urlActiune", constant = "/programari")
    NotificareEvent toProgramareAnulataDeTerapeut(Programare p, String keycloakId);

    @Mapping(target = "tipNotificare", constant = "REMINDER_24H")
    @Mapping(target = "userKeycloakId", source = "keycloakId")
    @Mapping(target = "tipUser", constant = "PACIENT")
    @Mapping(target = "titlu", constant = "Reminder: programare mâine")
    @Mapping(target = "mesaj", expression = "java(\"Ai o programare mâine, \" + formatData(p.getData()) + \", la ora \" + formatOra(p.getOraInceput()) + \".\")")
    @Mapping(target = "entitateLegataId", source = "p.id")
    @Mapping(target = "tipEntitateLegata", constant = "PROGRAMARE")
    @Mapping(target = "urlActiune", constant = "/programari")
    NotificareEvent toReminder24h(Programare p, String keycloakId);

    @Mapping(target = "tipNotificare", constant = "REMINDER_2H")
    @Mapping(target = "userKeycloakId", source = "keycloakId")
    @Mapping(target = "tipUser", constant = "PACIENT")
    @Mapping(target = "titlu", constant = "Reminder: programare în curând")
    @Mapping(target = "mesaj", expression = "java(\"Ai o programare în curând, astăzi la ora \" + formatOra(p.getOraInceput()) + \".\")")
    @Mapping(target = "entitateLegataId", source = "p.id")
    @Mapping(target = "tipEntitateLegata", constant = "PROGRAMARE")
    @Mapping(target = "urlActiune", constant = "/programari")
    NotificareEvent toReminder2h(Programare p, String keycloakId);

    @Mapping(target = "tipNotificare", constant = "REMINDER_JURNAL")
    @Mapping(target = "userKeycloakId", source = "keycloakId")
    @Mapping(target = "tipUser", constant = "PACIENT")
    @Mapping(target = "titlu", constant = "Completează jurnalul")
    @Mapping(target = "mesaj", expression = "java(\"Ședința din \" + formatData(p.getData()) + \" a fost finalizată. Nu uita să completezi jurnalul!\")")
    @Mapping(target = "entitateLegataId", source = "p.id")
    @Mapping(target = "tipEntitateLegata", constant = "PROGRAMARE")
    @Mapping(target = "urlActiune", constant = "/jurnal")
    NotificareEvent toReminderJurnal(Programare p, String keycloakId);

    @Mapping(target = "tipNotificare", constant = "REEVALUARE_RECOMANDATA")
    @Mapping(target = "userKeycloakId", source = "keycloakId")
    @Mapping(target = "tipUser", constant = "PACIENT")
    @Mapping(target = "titlu", constant = "Re-evaluare recomandată")
    @Mapping(target = "mesaj", constant = "Ai finalizat toate ședințele recomandate. Te rugăm să te programezi pentru o re-evaluare.")
    @Mapping(target = "entitateLegataId", source = "p.id")
    @Mapping(target = "tipEntitateLegata", constant = "PROGRAMARE")
    @Mapping(target = "urlActiune", constant = "/programari")
    NotificareEvent toReevaluareRecomandata(Programare p, String keycloakId);
}
