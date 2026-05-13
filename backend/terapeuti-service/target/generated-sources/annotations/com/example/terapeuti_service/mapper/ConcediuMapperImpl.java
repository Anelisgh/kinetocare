package com.example.terapeuti_service.mapper;

import com.example.terapeuti_service.dto.ConcediuDTO;
import com.example.terapeuti_service.dto.CreateConcediuDTO;
import com.example.terapeuti_service.entity.ConcediuTerapeut;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-05-13T13:22:18+0300",
    comments = "version: 1.6.3, compiler: Eclipse JDT (IDE) 3.46.0.v20260407-0427, environment: Java 21.0.10 (Eclipse Adoptium)"
)
@Component
public class ConcediuMapperImpl implements ConcediuMapper {

    @Override
    public ConcediuDTO toDTO(ConcediuTerapeut entity) {
        if ( entity == null ) {
            return null;
        }

        Long id = null;
        Long terapeutId = null;
        LocalDate dataInceput = null;
        LocalDate dataSfarsit = null;
        OffsetDateTime createdAt = null;

        id = entity.getId();
        terapeutId = entity.getTerapeutId();
        dataInceput = entity.getDataInceput();
        dataSfarsit = entity.getDataSfarsit();
        createdAt = entity.getCreatedAt();

        ConcediuDTO concediuDTO = new ConcediuDTO( id, terapeutId, dataInceput, dataSfarsit, createdAt );

        return concediuDTO;
    }

    @Override
    public ConcediuTerapeut toEntity(CreateConcediuDTO dto, Long terapeutId) {
        if ( dto == null && terapeutId == null ) {
            return null;
        }

        ConcediuTerapeut.ConcediuTerapeutBuilder concediuTerapeut = ConcediuTerapeut.builder();

        if ( dto != null ) {
            concediuTerapeut.dataInceput( dto.dataInceput() );
            concediuTerapeut.dataSfarsit( dto.dataSfarsit() );
        }
        concediuTerapeut.terapeutId( terapeutId );

        return concediuTerapeut.build();
    }
}
