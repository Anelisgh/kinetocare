package com.example.programari_service.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

@Data
@Builder
public class UrmatoareaProgramareDTO {
    private Long id;
    private Long serviciuId;
    private String tipServiciu;
    private BigDecimal pret;
    private LocalDate data;
    private LocalTime oraInceput;
    private LocalTime oraSfarsit;
    private Long locatieId; // il traducem in gateway din id in nume
    private Long terapeutId;
}
