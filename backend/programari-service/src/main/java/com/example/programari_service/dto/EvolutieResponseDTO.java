package com.example.programari_service.dto;

import lombok.Builder;
import lombok.Data;

import java.time.OffsetDateTime;

@Data
@Builder
public class EvolutieResponseDTO {
    private Long id;
    private String observatii;
    private OffsetDateTime createdAt;
}