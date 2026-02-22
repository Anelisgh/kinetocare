package com.example.programari_service.dto;

import java.time.LocalTime;

public record DisponibilitateDTO(
    LocalTime oraInceput,
    LocalTime oraSfarsit
) {}
