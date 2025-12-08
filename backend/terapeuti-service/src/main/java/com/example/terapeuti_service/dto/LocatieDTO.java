package com.example.terapeuti_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LocatieDTO {
    private Long id;
    private String nume;
    private String adresa;
    private String oras;
    private String judet;
    private String codPostal;
    private String telefon;
    private Boolean active;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
}
