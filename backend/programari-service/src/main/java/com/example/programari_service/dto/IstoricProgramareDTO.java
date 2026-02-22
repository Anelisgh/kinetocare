package com.example.programari_service.dto;

import com.example.programari_service.entity.StatusProgramare;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

public record IstoricProgramareDTO(
    Long id,
    LocalDate data,
    LocalTime oraInceput,
    LocalTime oraSfarsit,
    String tipServiciu,
    BigDecimal pret,
    StatusProgramare status,
    Long terapeutId,
    String numeTerapeut,
    String numeLocatie,
    Boolean areEvaluare,
    Boolean areJurnal,
    String motivAnulare,
    DetaliiJurnalDTO detaliiJurnal,
    String diagnostic,
    Integer sedinteRecomandate,
    String observatii,
    String serviciuRecomandat
) {}
