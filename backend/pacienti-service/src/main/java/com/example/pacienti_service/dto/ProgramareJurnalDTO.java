package com.example.pacienti_service.dto;

import java.time.LocalDate;
import java.time.LocalTime;

public record ProgramareJurnalDTO(
        Long id,
        String tipServiciu,
        LocalDate data,
        LocalTime ora,
        String numeTerapeut,
        Long terapeutId,
        String terapeutKeycloakId,   // keycloakId al terapeutului — pentru notificari
        String numeLocatie
) {}
