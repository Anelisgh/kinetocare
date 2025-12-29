package com.example.programari_service.mapper;

import com.example.programari_service.dto.EvolutieRequestDTO;
import com.example.programari_service.dto.EvolutieResponseDTO;
import com.example.programari_service.entity.Evolutie;
import org.springframework.stereotype.Component;

@Component
public class EvolutieMapper {

    public Evolutie toEntity(EvolutieRequestDTO dto) {
        if (dto == null) return null;

        return Evolutie.builder()
                .pacientId(dto.getPacientId())
                .terapeutId(dto.getTerapeutId())
                .observatii(dto.getObservatii())
                .build();
    }

    public EvolutieResponseDTO toDto(Evolutie entity) {
        if (entity == null) return null;

        return EvolutieResponseDTO.builder()
                .id(entity.getId())
                .observatii(entity.getObservatii())
                .createdAt(entity.getCreatedAt())
                .build();
    }
}