package com.example.terapeuti_service.mapper;

import com.example.terapeuti_service.dto.TerapeutDTO;
import com.example.terapeuti_service.dto.UpdateTerapeutDTO;
import com.example.terapeuti_service.entity.Terapeut;
import org.springframework.stereotype.Component;

@Component
public class TerapeutMapper {

    public TerapeutDTO toDTO(Terapeut terapeut) {
        if (terapeut == null) {
            return null;
        }

        return TerapeutDTO.builder()
                .id(terapeut.getId())
                .keycloakId(terapeut.getKeycloakId())
                .specializare(terapeut.getSpecializare())
                .pozaProfil(terapeut.getPozaProfil())
                .active(terapeut.getActive())
                .createdAt(terapeut.getCreatedAt())
                .updatedAt(terapeut.getUpdatedAt())
                .build();
    }

    public void updateEntity(Terapeut terapeut, UpdateTerapeutDTO dto) {
        if (dto.getSpecializare() != null) {
            terapeut.setSpecializare(dto.getSpecializare());
        }
        if (dto.getPozaProfil() != null) {
            terapeut.setPozaProfil(dto.getPozaProfil());
        }
    }
}
