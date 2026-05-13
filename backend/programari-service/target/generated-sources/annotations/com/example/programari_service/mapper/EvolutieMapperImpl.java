package com.example.programari_service.mapper;

import com.example.programari_service.dto.EvolutieRequestDTO;
import com.example.programari_service.dto.EvolutieResponseDTO;
import com.example.programari_service.entity.Evolutie;
import java.time.OffsetDateTime;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-05-13T13:22:10+0300",
    comments = "version: 1.6.3, compiler: Eclipse JDT (IDE) 3.46.0.v20260407-0427, environment: Java 21.0.10 (Eclipse Adoptium)"
)
@Component
public class EvolutieMapperImpl implements EvolutieMapper {

    @Override
    public Evolutie toEntity(EvolutieRequestDTO request) {
        if ( request == null ) {
            return null;
        }

        Evolutie.EvolutieBuilder evolutie = Evolutie.builder();

        evolutie.observatii( request.observatii() );
        evolutie.pacientKeycloakId( request.pacientKeycloakId() );
        evolutie.terapeutKeycloakId( request.terapeutKeycloakId() );

        return evolutie.build();
    }

    @Override
    public EvolutieResponseDTO toDto(Evolutie entity) {
        if ( entity == null ) {
            return null;
        }

        Long id = null;
        String observatii = null;
        OffsetDateTime createdAt = null;

        id = entity.getId();
        observatii = entity.getObservatii();
        createdAt = entity.getCreatedAt();

        EvolutieResponseDTO evolutieResponseDTO = new EvolutieResponseDTO( id, observatii, createdAt );

        return evolutieResponseDTO;
    }
}
