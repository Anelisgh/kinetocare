package com.example.programari_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

// DTO pentru raspunsul cu lista de pacienti (activi + arhivati)
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ListaPacientiDTO {
    private List<FisaPacientDTO> activi;
    private List<FisaPacientDTO> arhivati;
}
