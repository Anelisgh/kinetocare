package com.example.programari_service.dto;

import java.time.OffsetDateTime;

public record EvolutieResponseDTO(
    Long id,
    String observatii,
    OffsetDateTime createdAt
) {}
