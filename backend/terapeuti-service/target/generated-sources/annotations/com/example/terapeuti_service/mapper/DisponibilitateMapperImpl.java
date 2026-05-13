package com.example.terapeuti_service.mapper;

import com.example.terapeuti_service.dto.CreateDisponibilitateDTO;
import com.example.terapeuti_service.dto.DisponibilitateDTO;
import com.example.terapeuti_service.entity.DisponibilitateTerapeut;
import com.example.terapeuti_service.entity.Locatie;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-05-13T13:22:18+0300",
    comments = "version: 1.6.3, compiler: Eclipse JDT (IDE) 3.46.0.v20260407-0427, environment: Java 21.0.10 (Eclipse Adoptium)"
)
@Component
public class DisponibilitateMapperImpl implements DisponibilitateMapper {

    @Override
    public DisponibilitateDTO toDTO(DisponibilitateTerapeut entity, Locatie locatie) {
        if ( entity == null && locatie == null ) {
            return null;
        }

        Long id = null;
        Long terapeutId = null;
        Long locatieId = null;
        LocalTime oraInceput = null;
        LocalTime oraSfarsit = null;
        Boolean active = null;
        OffsetDateTime createdAt = null;
        OffsetDateTime updatedAt = null;
        Integer ziSaptamana = null;
        if ( entity != null ) {
            id = entity.getId();
            terapeutId = entity.getTerapeutId();
            locatieId = entity.getLocatieId();
            oraInceput = entity.getOraInceput();
            oraSfarsit = entity.getOraSfarsit();
            active = entity.getActive();
            createdAt = entity.getCreatedAt();
            updatedAt = entity.getUpdatedAt();
            ziSaptamana = entity.getZiSaptamana();
        }
        String locatieNume = null;
        String locatieAdresa = null;
        String locatieOras = null;
        if ( locatie != null ) {
            locatieNume = locatie.getNume();
            locatieAdresa = locatie.getAdresa();
            locatieOras = locatie.getOras();
        }

        String ziSaptamanaNume = getZiSaptamanaNume(entity.getZiSaptamana());

        DisponibilitateDTO disponibilitateDTO = new DisponibilitateDTO( id, terapeutId, ziSaptamana, ziSaptamanaNume, locatieId, locatieNume, locatieAdresa, locatieOras, oraInceput, oraSfarsit, active, createdAt, updatedAt );

        return disponibilitateDTO;
    }

    @Override
    public DisponibilitateTerapeut toEntity(CreateDisponibilitateDTO dto, Long terapeutId) {
        if ( dto == null && terapeutId == null ) {
            return null;
        }

        DisponibilitateTerapeut.DisponibilitateTerapeutBuilder disponibilitateTerapeut = DisponibilitateTerapeut.builder();

        if ( dto != null ) {
            disponibilitateTerapeut.locatieId( dto.locatieId() );
            disponibilitateTerapeut.oraInceput( dto.oraInceput() );
            disponibilitateTerapeut.oraSfarsit( dto.oraSfarsit() );
            disponibilitateTerapeut.ziSaptamana( dto.ziSaptamana() );
        }
        disponibilitateTerapeut.terapeutId( terapeutId );
        disponibilitateTerapeut.active( true );

        return disponibilitateTerapeut.build();
    }
}
