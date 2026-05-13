package com.example.chat_service.mapper;

import com.example.chat_service.dto.ConversatieDTO;
import com.example.chat_service.dto.MesajDTO;
import com.example.chat_service.entity.Conversatie;
import com.example.chat_service.entity.Mesaj;
import com.example.chat_service.entity.TipExpeditor;
import java.time.OffsetDateTime;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-05-13T13:21:56+0300",
    comments = "version: 1.6.3, compiler: Eclipse JDT (IDE) 3.46.0.v20260407-0427, environment: Java 21.0.10 (Eclipse Adoptium)"
)
@Component
public class ChatMapperImpl implements ChatMapper {

    @Override
    public MesajDTO toMesajDTO(Mesaj mesaj) {
        if ( mesaj == null ) {
            return null;
        }

        String expeditorKeycloakId = null;
        Long id = null;
        Long conversatieId = null;
        TipExpeditor tipExpeditor = null;
        String continut = null;
        Boolean esteCitit = null;
        OffsetDateTime cititLa = null;
        OffsetDateTime trimisLa = null;

        expeditorKeycloakId = mesaj.getExpeditorKeycloakId();
        id = mesaj.getId();
        conversatieId = mesaj.getConversatieId();
        tipExpeditor = mesaj.getTipExpeditor();
        continut = mesaj.getContinut();
        esteCitit = mesaj.getEsteCitit();
        cititLa = mesaj.getCititLa();
        trimisLa = mesaj.getTrimisLa();

        MesajDTO mesajDTO = new MesajDTO( id, conversatieId, expeditorKeycloakId, tipExpeditor, continut, esteCitit, cititLa, trimisLa );

        return mesajDTO;
    }

    @Override
    public Mesaj toMesaj(MesajDTO mesajDTO) {
        if ( mesajDTO == null ) {
            return null;
        }

        Mesaj.MesajBuilder mesaj = Mesaj.builder();

        mesaj.cititLa( mesajDTO.cititLa() );
        mesaj.continut( mesajDTO.continut() );
        mesaj.conversatieId( mesajDTO.conversatieId() );
        mesaj.esteCitit( mesajDTO.esteCitit() );
        mesaj.expeditorKeycloakId( mesajDTO.expeditorKeycloakId() );
        mesaj.id( mesajDTO.id() );
        mesaj.tipExpeditor( mesajDTO.tipExpeditor() );
        mesaj.trimisLa( mesajDTO.trimisLa() );

        return mesaj.build();
    }

    @Override
    public ConversatieDTO toConversatieDTO(Conversatie conversatie) {
        if ( conversatie == null ) {
            return null;
        }

        Long id = null;
        String pacientKeycloakId = null;
        String terapeutKeycloakId = null;
        OffsetDateTime ultimulMesajLa = null;
        OffsetDateTime createdAt = null;
        OffsetDateTime updatedAt = null;

        id = conversatie.getId();
        pacientKeycloakId = conversatie.getPacientKeycloakId();
        terapeutKeycloakId = conversatie.getTerapeutKeycloakId();
        ultimulMesajLa = conversatie.getUltimulMesajLa();
        createdAt = conversatie.getCreatedAt();
        updatedAt = conversatie.getUpdatedAt();

        MesajDTO ultimulMesaj = null;

        ConversatieDTO conversatieDTO = new ConversatieDTO( id, pacientKeycloakId, terapeutKeycloakId, ultimulMesajLa, createdAt, updatedAt, ultimulMesaj );

        return conversatieDTO;
    }

    @Override
    public Conversatie toConversatie(ConversatieDTO conversatieDTO) {
        if ( conversatieDTO == null ) {
            return null;
        }

        Conversatie.ConversatieBuilder conversatie = Conversatie.builder();

        conversatie.createdAt( conversatieDTO.createdAt() );
        conversatie.id( conversatieDTO.id() );
        conversatie.pacientKeycloakId( conversatieDTO.pacientKeycloakId() );
        conversatie.terapeutKeycloakId( conversatieDTO.terapeutKeycloakId() );
        conversatie.ultimulMesajLa( conversatieDTO.ultimulMesajLa() );
        conversatie.updatedAt( conversatieDTO.updatedAt() );

        return conversatie.build();
    }
}
