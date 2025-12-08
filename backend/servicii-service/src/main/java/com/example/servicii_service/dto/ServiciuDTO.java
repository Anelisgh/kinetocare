package com.example.servicii_service.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Builder
@Data
public class ServiciuDTO {
    private Long id;
    private String nume;
    private BigDecimal pret;
    private Integer durataMinute;
}
