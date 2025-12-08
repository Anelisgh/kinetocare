package com.example.terapeuti_service.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateDisponibilitateDTO {
    @NotNull(message = "Ziua săptămânii este obligatorie")
    private Integer ziSaptamana;

    @NotNull(message = "Locația este obligatorie")
    private Long locatieId;

    @NotNull(message = "Ora de început este obligatorie")
    private LocalTime oraInceput;

    @NotNull(message = "Ora de sfârșit este obligatorie")
    private LocalTime oraSfarsit;
}
