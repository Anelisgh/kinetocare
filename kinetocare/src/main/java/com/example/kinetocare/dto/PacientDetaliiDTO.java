package com.example.kinetocare.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PacientDetaliiDTO {
    private Long id;
    private String nume;
    private String gen;
    private String telefon;
    private String email;
    private Integer varsta;

    private List<EvaluareDTO> evaluari;
    private List<EvolutieDTO> evolutii;
}
