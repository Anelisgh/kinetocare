package com.example.terapeuti_service.mapper;

import com.example.terapeuti_service.dto.ConcediuDTO;
import com.example.terapeuti_service.dto.CreateConcediuDTO;
import com.example.terapeuti_service.entity.ConcediuTerapeut;
import org.springframework.stereotype.Component;

@Component
public class ConcediuMapper {

    public ConcediuDTO toDTO(ConcediuTerapeut entity) {
        if (entity == null) {
            return null;
        }

        return ConcediuDTO.builder()
                .id(entity.getId())
                .terapeutId(entity.getTerapeutId())
                .dataInceput(entity.getDataInceput())
                .dataSfarsit(entity.getDataSfarsit())
                .createdAt(entity.getCreatedAt())
                .build();
    }

    public ConcediuTerapeut toEntity(CreateConcediuDTO dto, Long terapeutId) {
        if (dto == null) {
            return null;
        }

        return ConcediuTerapeut.builder()
                .terapeutId(terapeutId)
                .dataInceput(dto.getDataInceput())
                .dataSfarsit(dto.getDataSfarsit())
                .build();
    }
}
