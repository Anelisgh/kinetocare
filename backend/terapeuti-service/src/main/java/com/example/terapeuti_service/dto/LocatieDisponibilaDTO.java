package com.example.terapeuti_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LocatieDisponibilaDTO {
    private Long id;
    private String nume;
    private String adresa;
    private String oras;
    private String judet;
}