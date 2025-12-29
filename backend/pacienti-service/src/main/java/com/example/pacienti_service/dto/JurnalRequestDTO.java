package com.example.pacienti_service.dto;

import lombok.Data;

@Data
public class JurnalRequestDTO {
    private Long programareId;
    private Integer nivelDurere;
    private Integer dificultateExercitii;
    private Integer nivelOboseala;
    private String comentarii;
}
