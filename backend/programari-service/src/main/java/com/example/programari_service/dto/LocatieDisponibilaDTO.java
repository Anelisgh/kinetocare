package com.example.programari_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

// pentru Calendarul Terapeutului
// locatiile unde este disponibil terapeutul
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LocatieDisponibilaDTO {
    private Long id;
    private String nume;
    private String adresa;
    private String oras;
}
