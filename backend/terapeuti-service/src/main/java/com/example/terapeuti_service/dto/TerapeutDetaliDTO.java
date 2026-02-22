package com.example.terapeuti_service.dto;

import java.util.List;

public record TerapeutDetaliDTO(
    String keycloakId,
    String pozaProfil,
    String specializare,
    List<DisponibilitateDTO> disponibilitati,
    List<LocatieDisponibilaDTO> locatiiDisponibile
) {}
