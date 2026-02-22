package com.example.terapeuti_service.dto;

public record LocatieDisponibilaDTO(
    Long id,
    String nume,
    String adresa,
    String oras,
    String judet,
    Boolean active
) {}
