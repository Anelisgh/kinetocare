package com.example.programari_service.dto;

import lombok.Data;

@Data
public class EvolutieRequestDTO {
    private Long pacientId;
    private Long terapeutId;
    private String observatii;
}