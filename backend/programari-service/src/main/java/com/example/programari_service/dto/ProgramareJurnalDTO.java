package com.example.programari_service.dto;

import java.time.LocalDate;
import java.time.LocalTime;

public record ProgramareJurnalDTO(
    Long id,
    String tipServiciu,
    LocalDate data,
    LocalTime ora,
    String numeTerapeut,
    String terapeutKeycloakId,   // keycloakId al terapeutului — pentru notificari
    String numeLocatie
) {}
