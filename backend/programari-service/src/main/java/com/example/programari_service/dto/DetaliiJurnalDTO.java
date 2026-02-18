package com.example.programari_service.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DetaliiJurnalDTO {
    private Integer nivelDurere;
    private Integer dificultateExercitii;
    private Integer nivelOboseala;
    private String comentarii;
}
