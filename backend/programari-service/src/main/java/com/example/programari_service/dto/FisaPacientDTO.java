package com.example.programari_service.dto;


public record FisaPacientDTO(
    String pacientKeycloakId,
    String nume,
    String prenume,
    Integer varsta,
    String diagnostic,
    Long sedinteRamase,
    Boolean activ
) {}
