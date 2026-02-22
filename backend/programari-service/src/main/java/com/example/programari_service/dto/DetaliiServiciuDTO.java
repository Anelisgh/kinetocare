package com.example.programari_service.dto;

import java.math.BigDecimal;

public record DetaliiServiciuDTO(
    Long id,
    String nume,
    BigDecimal pret,
    Integer durataMinute
) {}
