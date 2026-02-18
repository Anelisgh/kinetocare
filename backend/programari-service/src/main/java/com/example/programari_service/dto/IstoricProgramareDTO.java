package com.example.programari_service.dto;

import com.example.programari_service.entity.StatusProgramare;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

@Data
@Builder
public class IstoricProgramareDTO {
    // Basic Info
    private Long id;
    private LocalDate data;
    private LocalTime oraInceput;
    private LocalTime oraSfarsit;
    private String tipServiciu;
    private BigDecimal pret;
    private StatusProgramare status;

    // Context
    private Long terapeutId;
    private String numeTerapeut;
    private String numeLocatie;

    // Metadata
    private Boolean areEvaluare;
    private Boolean areJurnal;
    private String motivAnulare;
    private DetaliiJurnalDTO detaliiJurnal;

    // Optional Evaluation Data
    private String diagnostic;
    private Integer sedinteRecomandate;
    private String observatii;
    private String serviciuRecomandat;
}
