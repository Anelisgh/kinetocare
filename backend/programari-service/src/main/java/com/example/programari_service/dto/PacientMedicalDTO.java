package com.example.programari_service.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.time.LocalDate;

/**
 * DTO primit de la pacienti-service cu datele medicale/sportive ale pacientului.
 * faceSport este deserializat ca String pentru a evita dependenta de enum-ul din pacienti-service.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record PacientMedicalDTO(
        String keycloakId,
        LocalDate dataNasterii,
        String faceSport,
        String detaliiSport
) {}
