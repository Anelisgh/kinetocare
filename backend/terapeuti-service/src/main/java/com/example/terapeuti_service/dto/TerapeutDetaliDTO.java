package com.example.terapeuti_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

// pentru afisarea datelor sale catre pacient (in pagina disponibila pacientului -> ProfilPacient.jsx)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TerapeutDetaliDTO {
    private String keycloakId;
    private String nume;
    private String prenume;
    private String email;
    private String telefon;
    private String pozaProfil;
    private String specializare;
    private List<DisponibilitateDTO> disponibilitati;
    private List<LocatieDisponibilaDTO> locatiiDisponibile;
}