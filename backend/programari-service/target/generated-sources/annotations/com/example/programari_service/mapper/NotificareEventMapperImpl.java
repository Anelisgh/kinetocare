package com.example.programari_service.mapper;

import com.example.programari_service.dto.NotificareEvent;
import com.example.programari_service.entity.Programare;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-05-13T13:22:10+0300",
    comments = "version: 1.6.3, compiler: Eclipse JDT (IDE) 3.46.0.v20260407-0427, environment: Java 21.0.10 (Eclipse Adoptium)"
)
@Component
public class NotificareEventMapperImpl implements NotificareEventMapper {

    @Override
    public NotificareEvent toProgramareNoua(Programare p, String keycloakId) {
        if ( p == null && keycloakId == null ) {
            return null;
        }

        NotificareEvent.NotificareEventBuilder notificareEvent = NotificareEvent.builder();

        if ( p != null ) {
            notificareEvent.entitateLegataId( p.getId() );
        }
        notificareEvent.userKeycloakId( keycloakId );
        notificareEvent.tipNotificare( "PROGRAMARE_NOUA" );
        notificareEvent.tipUser( "TERAPEUT" );
        notificareEvent.titlu( "Programare nouă" );
        notificareEvent.mesaj( "Ai o programare nouă pe " + formatData(p.getData()) + " la ora " + formatOra(p.getOraInceput()) + "." );
        notificareEvent.tipEntitateLegata( "PROGRAMARE" );
        notificareEvent.urlActiune( "/calendar" );

        return notificareEvent.build();
    }

    @Override
    public NotificareEvent toEvaluareInitialaNoua(Programare p, String keycloakId) {
        if ( p == null && keycloakId == null ) {
            return null;
        }

        NotificareEvent.NotificareEventBuilder notificareEvent = NotificareEvent.builder();

        if ( p != null ) {
            notificareEvent.entitateLegataId( p.getId() );
        }
        notificareEvent.userKeycloakId( keycloakId );
        notificareEvent.tipNotificare( "EVALUARE_INITIALA_NOUA" );
        notificareEvent.tipUser( "TERAPEUT" );
        notificareEvent.titlu( "Cerere evaluare inițială" );
        notificareEvent.mesaj( "Un pacient nou s-a programat pentru evaluare inițială pe " + formatData(p.getData()) + " la ora " + formatOra(p.getOraInceput()) + "." );
        notificareEvent.tipEntitateLegata( "PROGRAMARE" );
        notificareEvent.urlActiune( "/calendar" );

        return notificareEvent.build();
    }

    @Override
    public NotificareEvent toProgramareAnulataDePacient(Programare p, String keycloakId) {
        if ( p == null && keycloakId == null ) {
            return null;
        }

        NotificareEvent.NotificareEventBuilder notificareEvent = NotificareEvent.builder();

        if ( p != null ) {
            notificareEvent.entitateLegataId( p.getId() );
        }
        notificareEvent.userKeycloakId( keycloakId );
        notificareEvent.tipNotificare( "PROGRAMARE_ANULATA_DE_PACIENT" );
        notificareEvent.tipUser( "TERAPEUT" );
        notificareEvent.titlu( "Programare anulată de pacient" );
        notificareEvent.mesaj( "O programare din " + formatData(p.getData()) + " la ora " + formatOra(p.getOraInceput()) + " a fost anulată de pacient." );
        notificareEvent.tipEntitateLegata( "PROGRAMARE" );
        notificareEvent.urlActiune( "/calendar" );

        return notificareEvent.build();
    }

    @Override
    public NotificareEvent toReevaluareNecesara(Programare p, String keycloakId) {
        if ( p == null && keycloakId == null ) {
            return null;
        }

        NotificareEvent.NotificareEventBuilder notificareEvent = NotificareEvent.builder();

        if ( p != null ) {
            notificareEvent.entitateLegataId( p.getId() );
        }
        notificareEvent.userKeycloakId( keycloakId );
        notificareEvent.tipNotificare( "REEVALUARE_NECESARA" );
        notificareEvent.tipUser( "TERAPEUT" );
        notificareEvent.titlu( "Pacient necesită re-evaluare" );
        notificareEvent.mesaj( "Un pacient s-a programat pe " + formatData(p.getData()) + " la ora " + formatOra(p.getOraInceput()) + ". Atenție: necesită reevaluare periodică." );
        notificareEvent.tipEntitateLegata( "PROGRAMARE" );
        notificareEvent.urlActiune( "/calendar" );

        return notificareEvent.build();
    }

    @Override
    public NotificareEvent toJurnalCompletat(String pacientKeycloakId, Long programareId, String keycloakId) {
        if ( pacientKeycloakId == null && programareId == null && keycloakId == null ) {
            return null;
        }

        NotificareEvent.NotificareEventBuilder notificareEvent = NotificareEvent.builder();

        notificareEvent.entitateLegataId( programareId );
        notificareEvent.userKeycloakId( keycloakId );
        notificareEvent.tipNotificare( "JURNAL_COMPLETAT" );
        notificareEvent.tipUser( "TERAPEUT" );
        notificareEvent.titlu( "Jurnal completat" );
        notificareEvent.mesaj( "Un pacient a completat jurnalul pentru o ședință." );
        notificareEvent.tipEntitateLegata( "PROGRAMARE" );
        notificareEvent.urlActiune( "/terapeut/pacienti/" + pacientKeycloakId );

        return notificareEvent.build();
    }

    @Override
    public NotificareEvent toProgramareAnulataDeTerapeut(Programare p, String keycloakId) {
        if ( p == null && keycloakId == null ) {
            return null;
        }

        NotificareEvent.NotificareEventBuilder notificareEvent = NotificareEvent.builder();

        if ( p != null ) {
            notificareEvent.entitateLegataId( p.getId() );
        }
        notificareEvent.userKeycloakId( keycloakId );
        notificareEvent.tipNotificare( "PROGRAMARE_ANULATA_DE_TERAPEUT" );
        notificareEvent.tipUser( "PACIENT" );
        notificareEvent.titlu( "Programare anulată de terapeut" );
        notificareEvent.mesaj( "Programarea din " + formatData(p.getData()) + " la ora " + formatOra(p.getOraInceput()) + " a fost anulată de terapeut." );
        notificareEvent.tipEntitateLegata( "PROGRAMARE" );
        notificareEvent.urlActiune( "/programari" );

        return notificareEvent.build();
    }

    @Override
    public NotificareEvent toReminder24h(Programare p, String keycloakId) {
        if ( p == null && keycloakId == null ) {
            return null;
        }

        NotificareEvent.NotificareEventBuilder notificareEvent = NotificareEvent.builder();

        if ( p != null ) {
            notificareEvent.entitateLegataId( p.getId() );
        }
        notificareEvent.userKeycloakId( keycloakId );
        notificareEvent.tipNotificare( "REMINDER_24H" );
        notificareEvent.tipUser( "PACIENT" );
        notificareEvent.titlu( "Reminder: programare mâine" );
        notificareEvent.mesaj( "Ai o programare mâine, " + formatData(p.getData()) + ", la ora " + formatOra(p.getOraInceput()) + "." );
        notificareEvent.tipEntitateLegata( "PROGRAMARE" );
        notificareEvent.urlActiune( "/programari" );

        return notificareEvent.build();
    }

    @Override
    public NotificareEvent toReminder2h(Programare p, String keycloakId) {
        if ( p == null && keycloakId == null ) {
            return null;
        }

        NotificareEvent.NotificareEventBuilder notificareEvent = NotificareEvent.builder();

        if ( p != null ) {
            notificareEvent.entitateLegataId( p.getId() );
        }
        notificareEvent.userKeycloakId( keycloakId );
        notificareEvent.tipNotificare( "REMINDER_2H" );
        notificareEvent.tipUser( "PACIENT" );
        notificareEvent.titlu( "Reminder: programare în curând" );
        notificareEvent.mesaj( "Ai o programare în curând, astăzi la ora " + formatOra(p.getOraInceput()) + "." );
        notificareEvent.tipEntitateLegata( "PROGRAMARE" );
        notificareEvent.urlActiune( "/programari" );

        return notificareEvent.build();
    }

    @Override
    public NotificareEvent toReminderJurnal(Programare p, String keycloakId) {
        if ( p == null && keycloakId == null ) {
            return null;
        }

        NotificareEvent.NotificareEventBuilder notificareEvent = NotificareEvent.builder();

        if ( p != null ) {
            notificareEvent.entitateLegataId( p.getId() );
        }
        notificareEvent.userKeycloakId( keycloakId );
        notificareEvent.tipNotificare( "REMINDER_JURNAL" );
        notificareEvent.tipUser( "PACIENT" );
        notificareEvent.titlu( "Completează jurnalul" );
        notificareEvent.mesaj( "Ședința din " + formatData(p.getData()) + " a fost finalizată. Nu uita să completezi jurnalul!" );
        notificareEvent.tipEntitateLegata( "PROGRAMARE" );
        notificareEvent.urlActiune( "/jurnal" );

        return notificareEvent.build();
    }

    @Override
    public NotificareEvent toReevaluareRecomandata(Programare p, String keycloakId) {
        if ( p == null && keycloakId == null ) {
            return null;
        }

        NotificareEvent.NotificareEventBuilder notificareEvent = NotificareEvent.builder();

        if ( p != null ) {
            notificareEvent.entitateLegataId( p.getId() );
        }
        notificareEvent.userKeycloakId( keycloakId );
        notificareEvent.tipNotificare( "REEVALUARE_RECOMANDATA" );
        notificareEvent.tipUser( "PACIENT" );
        notificareEvent.titlu( "Re-evaluare recomandată" );
        notificareEvent.mesaj( "Ai finalizat toate ședințele recomandate. Te rugăm să te programezi pentru o re-evaluare." );
        notificareEvent.tipEntitateLegata( "PROGRAMARE" );
        notificareEvent.urlActiune( "/programari" );

        return notificareEvent.build();
    }
}
