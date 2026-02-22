package com.example.terapeuti_service.dto;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public record CreateConcediuDTO(
    @NotNull(message = "Data de început este obligatorie")
    LocalDate dataInceput,

    @NotNull(message = "Data de sfârșit este obligatorie")
    LocalDate dataSfarsit
) {}
