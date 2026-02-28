package com.example.programari_service.dto;

import java.time.LocalDate;
import java.time.LocalTime;

public record CreeazaProgramareRequest(
    String pacientKeycloakId,
    String terapeutKeycloakId,
    Long locatieId,
    LocalDate data,
    LocalTime oraInceput
) {}
