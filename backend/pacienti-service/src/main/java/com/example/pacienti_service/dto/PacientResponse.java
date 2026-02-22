package com.example.pacienti_service.dto;

import com.example.pacienti_service.entity.FaceSport;

import java.time.LocalDate;

public record PacientResponse(
        Long id,
        String keycloakId,
        LocalDate dataNasterii,
        String cnp,
        FaceSport faceSport,
        String detaliiSport,
        String orasPreferat,
        Long locatiePreferataId,
        String terapeutKeycloakId
) {}