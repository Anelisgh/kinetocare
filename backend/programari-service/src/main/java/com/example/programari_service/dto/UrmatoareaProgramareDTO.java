package com.example.programari_service.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

public record UrmatoareaProgramareDTO(
    Long id,
    Long serviciuId,
    String tipServiciu,
    BigDecimal pret,
    LocalDate data,
    LocalTime oraInceput,
    LocalTime oraSfarsit,
    Long locatieId,
    Long terapeutId
) {}
