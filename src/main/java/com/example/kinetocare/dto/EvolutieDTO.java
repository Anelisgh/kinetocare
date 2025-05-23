package com.example.kinetocare.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EvolutieDTO {
    @NotNull(message = "Selectați un pacient")
    private Long pacientId;

    @NotNull(message = "Data este obligatorie")
    @PastOrPresent(message = "Data nu poate fi din viitor")
    private LocalDate dataEvolutie;

    private String observatii;
}
