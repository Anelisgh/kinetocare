package com.example.programari_service.mapper;

import com.example.programari_service.entity.RelatiePacientTerapeut;
import java.time.LocalDate;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-05-13T13:22:10+0300",
    comments = "version: 1.6.3, compiler: Eclipse JDT (IDE) 3.46.0.v20260407-0427, environment: Java 21.0.10 (Eclipse Adoptium)"
)
@Component
public class RelatieMapperImpl implements RelatieMapper {

    @Override
    public RelatiePacientTerapeut toEntity(String pacientKeycloakId, String terapeutKeycloakId, LocalDate dataInceput) {
        if ( pacientKeycloakId == null && terapeutKeycloakId == null && dataInceput == null ) {
            return null;
        }

        RelatiePacientTerapeut.RelatiePacientTerapeutBuilder relatiePacientTerapeut = RelatiePacientTerapeut.builder();

        relatiePacientTerapeut.pacientKeycloakId( pacientKeycloakId );
        relatiePacientTerapeut.terapeutKeycloakId( terapeutKeycloakId );
        relatiePacientTerapeut.dataInceput( dataInceput );
        relatiePacientTerapeut.activa( true );

        return relatiePacientTerapeut.build();
    }
}
