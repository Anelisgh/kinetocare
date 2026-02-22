package com.example.terapeuti_service.dto;

import java.time.LocalTime;
import java.time.OffsetDateTime;

public record DisponibilitateDTO(
    Long id,
    Long terapeutId,
    Integer ziSaptamana,
    String ziSaptamanaNume,
    Long locatieId,
    String locatieNume,
    String locatieAdresa,
    String locatieOras,
    LocalTime oraInceput,
    LocalTime oraSfarsit,
    Boolean active,
    OffsetDateTime createdAt,
    OffsetDateTime updatedAt
) {}
