package com.example.programari_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

// DTO pentru afisarea evaluarilor in fisa pacientului
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EvaluareResponseDTO {
    private Long id;
    private String tipEvaluare; // EVALUARE_INITIALA, REEVALUARE
    private LocalDate data;
    private String diagnostic;
    private String serviciuRecomandat; // numele serviciului
    private Integer sedinteRecomandate;
    private String observatii;
    private String numeTerapeut;
    private Long terapeutId;
}
