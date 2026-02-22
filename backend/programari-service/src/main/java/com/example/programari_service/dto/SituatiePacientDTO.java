package com.example.programari_service.dto;


public record SituatiePacientDTO(
    String diagnostic,
    Integer sedinteRecomandate,
    Long sedinteEfectuate,
    Long sedinteRamase
) {}
