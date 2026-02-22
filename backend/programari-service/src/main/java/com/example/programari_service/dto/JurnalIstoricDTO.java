package com.example.programari_service.dto;

import java.time.LocalDate;
import java.time.LocalTime;

public record JurnalIstoricDTO(
    Long id,
    Long programareId,
    LocalDate dataJurnal,
    LocalTime oraSedinta,
    Integer nivelDurere,
    Integer dificultateExercitii,
    Integer nivelOboseala,
    String comentarii
) {}
