package com.example.programari_service.dto.statistici;


public record StatisticiProgramariLunareDTO(
    Long locatieId,
    String locatieNume,
    Integer an,
    Integer luna,
    Long count
) {}
