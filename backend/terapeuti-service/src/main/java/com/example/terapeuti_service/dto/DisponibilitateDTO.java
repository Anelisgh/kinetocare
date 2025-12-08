package com.example.terapeuti_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;
import java.time.OffsetDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DisponibilitateDTO {
    private Long id;
    private Long terapeutId;
    private Integer ziSaptamana; // 1=Luni ... 7=Duminica
    private String ziSaptamanaNume; // "Luni", "Marti"... - pentru display
    private Long locatieId;
    private String locatieNume;
    private String locatieAdresa;
    private String locatieOras;
    private LocalTime oraInceput;
    private LocalTime oraSfarsit;
    private Boolean active;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
}
