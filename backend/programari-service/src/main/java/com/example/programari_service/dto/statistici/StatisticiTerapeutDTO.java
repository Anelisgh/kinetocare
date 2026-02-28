package com.example.programari_service.dto.statistici;


public record StatisticiTerapeutDTO(
    String terapeutKeycloakId,
    String terapeutNume,
    Long count
) {}
