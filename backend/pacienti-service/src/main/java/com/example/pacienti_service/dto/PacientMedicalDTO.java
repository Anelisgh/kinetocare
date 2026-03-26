package com.example.pacienti_service.dto;

import com.example.pacienti_service.entity.FaceSport;

import java.time.LocalDate;

/**
 * DTO intern folosit de serviciile partenere (ex: programari-service)
 * pentru a prelua datele medicale/sportive ale unui pacient.
 * Returnează data nașterii brută — calculul vârstei se face în service-ul consumator.
 */
public record PacientMedicalDTO(
        String keycloakId,
        LocalDate dataNasterii,
        FaceSport faceSport,
        String detaliiSport
) {}
