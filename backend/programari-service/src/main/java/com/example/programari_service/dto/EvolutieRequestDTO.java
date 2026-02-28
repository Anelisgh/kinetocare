package com.example.programari_service.dto;


public record EvolutieRequestDTO(
    String pacientKeycloakId,
    String terapeutKeycloakId,
    String observatii
) {}
