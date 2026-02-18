package com.example.programari_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

// DTO complet pentru fisa pacientului (pagina de detalii)
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FisaPacientDetaliiDTO {
    // Date personale
    private Long pacientId;
    private String nume;
    private String prenume;
    private Integer varsta;
    private String gen;
    private String telefon;
    private String email;

    // Situatie curenta (diagnostic + progres)
    private SituatiePacientDTO situatie;

    // Evaluari (de la toti terapeutii)
    private List<EvaluareResponseDTO> evaluari;

    // Evolutii (doar ale terapeutului curent)
    private List<EvolutieResponseDTO> evolutii;

    // Programari (istoric complet)
    private List<IstoricProgramareDTO> programari;

    // Jurnale
    private List<JurnalIstoricDTO> jurnale;
}
