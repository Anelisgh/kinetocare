package com.example.programari_service.dto;


public record UserDisplayCalendarDTO(
    Long id,
    String keycloakId,
    String nume,
    String prenume,
    String telefon,
    String email,
    String gen
) {}
