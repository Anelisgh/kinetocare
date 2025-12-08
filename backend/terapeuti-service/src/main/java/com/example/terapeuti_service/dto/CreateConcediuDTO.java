package com.example.terapeuti_service.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateConcediuDTO {
    @NotNull(message = "Data de început este obligatorie")
    private LocalDate dataInceput;

    @NotNull(message = "Data de sfârșit este obligatorie")
    private LocalDate dataSfarsit;
}
