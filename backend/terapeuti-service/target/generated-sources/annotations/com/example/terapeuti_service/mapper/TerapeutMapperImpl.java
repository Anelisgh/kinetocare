package com.example.terapeuti_service.mapper;

import com.example.terapeuti_service.dto.ConcediuDTO;
import com.example.terapeuti_service.dto.DisponibilitateDTO;
import com.example.terapeuti_service.dto.LocatieDisponibilaDTO;
import com.example.terapeuti_service.dto.TerapeutDTO;
import com.example.terapeuti_service.dto.TerapeutDetaliDTO;
import com.example.terapeuti_service.dto.TerapeutSearchDTO;
import com.example.terapeuti_service.dto.UpdateTerapeutDTO;
import com.example.terapeuti_service.entity.Specializare;
import com.example.terapeuti_service.entity.Terapeut;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-05-13T13:22:18+0300",
    comments = "version: 1.6.3, compiler: Eclipse JDT (IDE) 3.46.0.v20260407-0427, environment: Java 21.0.10 (Eclipse Adoptium)"
)
@Component
public class TerapeutMapperImpl implements TerapeutMapper {

    @Override
    public Terapeut toNewEntity(String keycloakId) {
        if ( keycloakId == null ) {
            return null;
        }

        Terapeut.TerapeutBuilder terapeut = Terapeut.builder();

        terapeut.keycloakId( keycloakId );

        terapeut.active( true );

        return terapeut.build();
    }

    @Override
    public TerapeutDTO toDTO(Terapeut terapeut) {
        if ( terapeut == null ) {
            return null;
        }

        Long id = null;
        String keycloakId = null;
        Specializare specializare = null;
        String pozaProfil = null;
        Boolean active = null;
        OffsetDateTime createdAt = null;
        OffsetDateTime updatedAt = null;

        id = terapeut.getId();
        keycloakId = terapeut.getKeycloakId();
        specializare = terapeut.getSpecializare();
        pozaProfil = terapeut.getPozaProfil();
        active = terapeut.getActive();
        createdAt = terapeut.getCreatedAt();
        updatedAt = terapeut.getUpdatedAt();

        List<DisponibilitateDTO> disponibilitati = null;
        List<ConcediuDTO> concedii = null;
        List<LocatieDisponibilaDTO> locatiiDisponibile = null;
        Boolean profileIncomplete = null;

        TerapeutDTO terapeutDTO = new TerapeutDTO( id, keycloakId, specializare, pozaProfil, active, createdAt, updatedAt, disponibilitati, concedii, locatiiDisponibile, profileIncomplete );

        return terapeutDTO;
    }

    @Override
    public TerapeutDetaliDTO toDetaliDTO(Terapeut terapeut, List<DisponibilitateDTO> disponibilitati, List<LocatieDisponibilaDTO> locatiiUnice) {
        if ( terapeut == null && disponibilitati == null && locatiiUnice == null ) {
            return null;
        }

        String keycloakId = null;
        String pozaProfil = null;
        String specializare = null;
        if ( terapeut != null ) {
            keycloakId = terapeut.getKeycloakId();
            pozaProfil = terapeut.getPozaProfil();
            if ( terapeut.getSpecializare() != null ) {
                specializare = terapeut.getSpecializare().name();
            }
        }
        List<DisponibilitateDTO> disponibilitati1 = null;
        List<DisponibilitateDTO> list = disponibilitati;
        if ( list != null ) {
            disponibilitati1 = new ArrayList<DisponibilitateDTO>( list );
        }
        List<LocatieDisponibilaDTO> locatiiDisponibile = null;
        List<LocatieDisponibilaDTO> list1 = locatiiUnice;
        if ( list1 != null ) {
            locatiiDisponibile = new ArrayList<LocatieDisponibilaDTO>( list1 );
        }

        TerapeutDetaliDTO terapeutDetaliDTO = new TerapeutDetaliDTO( keycloakId, pozaProfil, specializare, disponibilitati1, locatiiDisponibile );

        return terapeutDetaliDTO;
    }

    @Override
    public TerapeutSearchDTO toSearchDTO(Terapeut terapeut, List<LocatieDisponibilaDTO> locatiiDisp) {
        if ( terapeut == null && locatiiDisp == null ) {
            return null;
        }

        String keycloakId = null;
        String pozaProfil = null;
        String specializare = null;
        if ( terapeut != null ) {
            keycloakId = terapeut.getKeycloakId();
            pozaProfil = terapeut.getPozaProfil();
            if ( terapeut.getSpecializare() != null ) {
                specializare = terapeut.getSpecializare().name();
            }
        }
        List<LocatieDisponibilaDTO> locatiiDisponibile = null;
        List<LocatieDisponibilaDTO> list = locatiiDisp;
        if ( list != null ) {
            locatiiDisponibile = new ArrayList<LocatieDisponibilaDTO>( list );
        }

        TerapeutSearchDTO terapeutSearchDTO = new TerapeutSearchDTO( keycloakId, pozaProfil, specializare, locatiiDisponibile );

        return terapeutSearchDTO;
    }

    @Override
    public void updateEntity(Terapeut terapeut, UpdateTerapeutDTO dto) {
        if ( dto == null ) {
            return;
        }

        if ( dto.pozaProfil() != null ) {
            terapeut.setPozaProfil( dto.pozaProfil() );
        }
        if ( dto.specializare() != null ) {
            terapeut.setSpecializare( dto.specializare() );
        }
    }
}
