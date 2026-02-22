package com.example.programari_service.dto.statistici;

import java.math.BigDecimal;

public record StatisticiVenituriLocatieDTO(
    Long locatieId,
    String locatieNume,
    BigDecimal totalVenituri
) {}
