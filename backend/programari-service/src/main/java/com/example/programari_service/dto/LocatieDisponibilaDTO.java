package com.example.programari_service.dto;


public record LocatieDisponibilaDTO(
    Long id,
    String nume,
    String adresa,
    String oras,
    Boolean active
) {}
