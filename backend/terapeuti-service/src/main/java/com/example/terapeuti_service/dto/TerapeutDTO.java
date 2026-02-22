package com.example.terapeuti_service.dto;

import com.example.terapeuti_service.entity.Specializare;
import java.time.OffsetDateTime;
import java.util.List;

public record TerapeutDTO(
        Long id,
        String keycloakId,
        Specializare specializare,
        String pozaProfil,
        Boolean active,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt,
        List<DisponibilitateDTO> disponibilitati,
        List<ConcediuDTO> concedii,
        List<LocatieDisponibilaDTO> locatiiDisponibile,
        Boolean profileIncomplete
) {}
