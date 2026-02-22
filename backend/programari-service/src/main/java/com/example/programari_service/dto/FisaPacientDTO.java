package com.example.programari_service.dto;


public record FisaPacientDTO(
    Long pacientId,
    String nume,
    String prenume,
    Integer varsta,
    String diagnostic,
    Long sedinteRamase,
    Boolean activ
) {}
