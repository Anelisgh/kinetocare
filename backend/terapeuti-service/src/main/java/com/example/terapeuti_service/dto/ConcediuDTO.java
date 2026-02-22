package com.example.terapeuti_service.dto;

import java.time.LocalDate;
import java.time.OffsetDateTime;

public record ConcediuDTO(
    Long id,
    Long terapeutId,
    LocalDate dataInceput,
    LocalDate dataSfarsit,
    OffsetDateTime createdAt
) {}
