package com.example.common.dto;

import com.example.common.enums.TipEvaluare;
import com.example.common.enums.TipServiciu;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EvaluareDTO {
    @NotNull(message = "Selectați un pacient")
    private Long pacientId;

    @NotNull(message = "Selectați tipul evaluării")
    private TipEvaluare tipEvaluare;

    @NotNull(message = "Data este obligatorie")
    @PastOrPresent(message = "Data nu poate fi din viitor")
    private LocalDate dataEvaluare;

    @NotBlank(message = "Diagnosticul este obligatoriu")
    private String numeDiagnostic;

    @NotNull(message = "Sedintele recomandate sunt obligatorii")
    @Positive(message = "Sedintele recomandate pot fi doar numere pozitive")
    private Integer sedinteRecomandate;

    private Integer sedintePanaLaReevaluare;

    @NotNull(message = "Selectați un serviciu")
    private TipServiciu tipServiciu;

    private String observatii;
}
