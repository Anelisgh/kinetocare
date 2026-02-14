package com.example.terapeuti_service.mapper;

import com.example.terapeuti_service.dto.*;
import com.example.terapeuti_service.dto.UpdateTerapeutDTO;
import com.example.terapeuti_service.entity.Terapeut;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class TerapeutMapper {

    // keycloakId -> entity gol (pentru createTerapeut si initializeEmptyTerapeut)
    public Terapeut toNewEntity(String keycloakId) {
        return Terapeut.builder()
                .keycloakId(keycloakId)
                .active(true)
                .build();
    }

    // entity -> TerapeutDTO (campuri directe)
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

    // entity + date suplimentare -> TerapeutDetaliDTO
    public TerapeutDetaliDTO toDetaliDTO(Terapeut terapeut,
                                          List<DisponibilitateDTO> disponibilitati,
                                          List<LocatieDisponibilaDTO> locatiiUnice) {
        return TerapeutDetaliDTO.builder()
                .keycloakId(terapeut.getKeycloakId())
                .pozaProfil(terapeut.getPozaProfil())
                .specializare(terapeut.getSpecializare() != null ? terapeut.getSpecializare().name() : null)
                .disponibilitati(disponibilitati)
                .locatiiDisponibile(locatiiUnice)
                .build();
    }

    // entity + locatii -> TerapeutSearchDTO
    public TerapeutSearchDTO toSearchDTO(Terapeut terapeut, List<LocatieDisponibilaDTO> locatiiDisp) {
        return TerapeutSearchDTO.builder()
                .keycloakId(terapeut.getKeycloakId())
                .pozaProfil(terapeut.getPozaProfil())
                .specializare(terapeut.getSpecializare().name())
                .locatiiDisponibile(locatiiDisp)
                .build();
    }

    // updateaza entity din UpdateTerapeutDTO
    public void updateEntity(Terapeut terapeut, UpdateTerapeutDTO dto) {
        if (dto.getSpecializare() != null) {
            terapeut.setSpecializare(dto.getSpecializare());
        }
        if (dto.getPozaProfil() != null) {
            terapeut.setPozaProfil(dto.getPozaProfil());
        }
    }
}
