package com.example.notificari_service.mapper;

import com.example.notificari_service.dto.NotificareDTO;
import com.example.notificari_service.entity.Notificare;
import com.example.notificari_service.entity.TipNotificare;
import java.time.OffsetDateTime;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-05-13T13:22:04+0300",
    comments = "version: 1.6.3, compiler: Eclipse JDT (IDE) 3.46.0.v20260407-0427, environment: Java 21.0.10 (Eclipse Adoptium)"
)
@Component
public class NotificareMapperImpl implements NotificareMapper {

    @Override
    public NotificareDTO toDto(Notificare notificare) {
        if ( notificare == null ) {
            return null;
        }

        TipNotificare tipNotificare = null;
        Long id = null;
        String titlu = null;
        String mesaj = null;
        String urlActiune = null;
        Boolean esteCitita = null;
        OffsetDateTime createdAt = null;

        tipNotificare = notificare.getTip();
        id = notificare.getId();
        titlu = notificare.getTitlu();
        mesaj = notificare.getMesaj();
        urlActiune = notificare.getUrlActiune();
        esteCitita = notificare.getEsteCitita();
        createdAt = notificare.getCreatedAt();

        NotificareDTO notificareDTO = new NotificareDTO( id, tipNotificare, titlu, mesaj, urlActiune, esteCitita, createdAt );

        return notificareDTO;
    }
}
