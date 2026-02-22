package com.example.programari_service.dto;


public record EvolutieRequestDTO(
    Long pacientId,
    Long terapeutId,
    String observatii
) {}
