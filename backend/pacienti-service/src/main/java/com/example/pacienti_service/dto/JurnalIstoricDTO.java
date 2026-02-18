package com.example.pacienti_service.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@Builder
public class JurnalIstoricDTO {
    private Long id;
    private Long programareId;
    private LocalDate dataJurnal; // Data ședinței
    private LocalTime oraSedinta; // Ora ședinței
    private Integer nivelDurere;
    private Integer dificultateExercitii;
    private Integer nivelOboseala;
    private String comentarii;
    // Date despre sedinta (populate prin Feign din programari-service)
    private String tipServiciu;
    private String numeTerapeut;
    private String numeLocatie;
}