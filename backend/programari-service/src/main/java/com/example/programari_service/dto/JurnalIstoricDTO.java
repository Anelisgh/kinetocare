package com.example.programari_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JurnalIstoricDTO {
    private Long id;
    private Long programareId;
    private LocalDate dataJurnal;
    private LocalTime oraSedinta;
    private Integer nivelDurere;
    private Integer dificultateExercitii;
    private Integer nivelOboseala;
    private String comentarii;
    // We ignore other fields like tipServiciu for mapping as we only need metrics
}
