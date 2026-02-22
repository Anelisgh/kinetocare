package com.example.pacienti_service.dto;

public record JurnalRequestDTO(
        Long programareId,
        Integer nivelDurere,
        Integer dificultateExercitii,
        Integer nivelOboseala,
        String comentarii
) {}
