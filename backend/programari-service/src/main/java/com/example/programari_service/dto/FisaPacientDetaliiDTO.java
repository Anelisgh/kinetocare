package com.example.programari_service.dto;

import java.util.List;

public record FisaPacientDetaliiDTO(
    String pacientKeycloakId,
    String nume,
    String prenume,
    Integer varsta,
    String gen,
    String telefon,
    String email,
    SituatiePacientDTO situatie,
    List<EvaluareResponseDTO> evaluari,
    List<EvolutieResponseDTO> evolutii,
    List<IstoricProgramareDTO> programari,
    List<JurnalIstoricDTO> jurnale
) {}
