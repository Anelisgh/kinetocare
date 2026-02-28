package com.example.programari_service.dto;

import com.example.programari_service.entity.TipEvaluare;

public record EvaluareRequestDTO(
    String pacientKeycloakId,
    String terapeutKeycloakId,
    Long programareId,
    TipEvaluare tip,
    String diagnostic,
    Integer sedinteRecomandate,
    Long serviciuRecomandatId,
    String observatii
) {}
