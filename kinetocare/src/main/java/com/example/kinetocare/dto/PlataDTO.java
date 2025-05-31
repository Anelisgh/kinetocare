package com.example.kinetocare.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PlataDTO {
    private LocalDate dataProgramare;
    private String serviciu;
    private BigDecimal suma;
    private String starePlata;
}