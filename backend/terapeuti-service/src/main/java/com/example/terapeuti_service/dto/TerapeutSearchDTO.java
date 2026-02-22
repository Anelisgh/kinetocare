package com.example.terapeuti_service.dto;

import java.util.List;

public record TerapeutSearchDTO(
    String keycloakId,
    String pozaProfil,
    String specializare,
    List<LocatieDisponibilaDTO> locatiiDisponibile
) {}
