package com.example.common.dto;

import com.example.common.enums.StarePlata;
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
    private Long id;
    private LocalDate data;
    private BigDecimal suma;
    private StarePlata starePlata;
    private Long programareId;
    private Long pacientId;
}