package com.example.terapeuti_service.dto;

import jakarta.validation.constraints.NotNull;
import java.time.LocalTime;

public record CreateDisponibilitateDTO(
    @NotNull(message = "Ziua săptămânii este obligatorie")
    Integer ziSaptamana,

    @NotNull(message = "Locația este obligatorie")
    Long locatieId,

    @NotNull(message = "Ora de început este obligatorie")
    LocalTime oraInceput,

    @NotNull(message = "Ora de sfârșit este obligatorie")
    LocalTime oraSfarsit
) {}
