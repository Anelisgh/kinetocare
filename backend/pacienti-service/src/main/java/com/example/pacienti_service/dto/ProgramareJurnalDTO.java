package com.example.pacienti_service.dto;

import java.time.LocalDate;
import java.time.LocalTime;

public record ProgramareJurnalDTO(
        Long id,
        String tipServiciu,
        LocalDate data,
        LocalTime ora,
        String numeTerapeut,
        String terapeutKeycloakId,
        String numeLocatie
) {}
