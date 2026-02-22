package com.example.programari_service.dto.statistici;


public record StatisticiPacientiNoiDTO(
    Long locatieId,
    String locatieNume,
    Integer an,
    Integer luna,
    Long numarPacientiNoi
) {}
