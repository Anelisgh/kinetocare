package com.example.terapeuti_service.dto;

import java.time.OffsetDateTime;

public record LocatieDTO(
    Long id,
    String nume,
    String adresa,
    String oras,
    String judet,
    String codPostal,
    String telefon,
    Boolean active,
    OffsetDateTime createdAt,
    OffsetDateTime updatedAt
) {}
