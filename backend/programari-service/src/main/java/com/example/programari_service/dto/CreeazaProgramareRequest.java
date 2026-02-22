package com.example.programari_service.dto;

import java.time.LocalDate;
import java.time.LocalTime;

public record CreeazaProgramareRequest(
    Long pacientId,
    Long terapeutId,
    Long locatieId,
    LocalDate data,
    LocalTime oraInceput
) {}
