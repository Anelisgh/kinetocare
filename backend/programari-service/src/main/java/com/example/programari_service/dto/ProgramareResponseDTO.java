package com.example.programari_service.dto;

import com.example.programari_service.entity.StatusProgramare;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProgramareResponseDTO {
    private Long id;
    private Long pacientId;
    private Long terapeutId;
    private Long locatieId;
    private Long serviciuId;
    private String tipServiciu;
    private BigDecimal pret;
    private Integer durataMinute;
    private Boolean primaIntalnire;
    private LocalDate data;
    private LocalTime oraInceput;
    private LocalTime oraSfarsit;
    private StatusProgramare status;
}
