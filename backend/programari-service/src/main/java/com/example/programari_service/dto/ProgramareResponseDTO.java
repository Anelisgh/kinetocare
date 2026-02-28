package com.example.programari_service.dto;

import com.example.programari_service.entity.StatusProgramare;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

public record ProgramareResponseDTO(
    Long id,
    String pacientKeycloakId,
    String terapeutKeycloakId,
    Long locatieId,
    Long serviciuId,
    String tipServiciu,
    BigDecimal pret,
    Integer durataMinute,
    Boolean primaIntalnire,
    LocalDate data,
    LocalTime oraInceput,
    LocalTime oraSfarsit,
    StatusProgramare status
) {}
