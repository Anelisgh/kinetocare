package com.example.servicii_service.dto;

import java.math.BigDecimal;

public record ServiciuDTO(
        Long id,
        String nume,
        BigDecimal pret,
        Integer durataMinute
) {
}
