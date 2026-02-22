package com.example.programari_service.dto;

import com.example.programari_service.entity.TipEvaluare;

public record EvaluareRequestDTO(
    Long pacientId,
    Long terapeutId,
    Long programareId,
    TipEvaluare tip,
    String diagnostic,
    Integer sedinteRecomandate,
    Long serviciuRecomandatId,
    String observatii
) {}
