package com.example.terapeuti_service.dto;

import com.example.terapeuti_service.entity.Specializare;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.List;

// pentru profil
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TerapeutDTO {
    private Long id;
    private String keycloakId;
    private Specializare specializare;
    private String pozaProfil;
    private Boolean active;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
    private List<DisponibilitateDTO> disponibilitati;
    private List<ConcediuDTO> concedii;
    private List<LocatieDisponibilaDTO> locatiiDisponibile;
    private Boolean profileIncomplete; // flag pentru frontend
}
