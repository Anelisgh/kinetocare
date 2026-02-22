package com.example.programari_service.dto;

import java.util.List;

public record ListaPacientiDTO(
    List<FisaPacientDTO> activi,
    List<FisaPacientDTO> arhivati
) {}
