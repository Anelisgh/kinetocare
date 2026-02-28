package com.example.programari_service.dto;

import java.time.LocalDate;

public record EvaluareResponseDTO(
    Long id,
    String tipEvaluare,
    LocalDate data,
    String diagnostic,
    String serviciuRecomandat,
    Long serviciuRecomandatId,
    Integer sedinteRecomandate,
    String observatii,
    String numeTerapeut,
    String terapeutKeycloakId
) {}
