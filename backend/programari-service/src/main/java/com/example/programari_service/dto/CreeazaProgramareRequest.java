package com.example.programari_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreeazaProgramareRequest {
    private Long pacientId;
    private Long terapeutId;
    private Long locatieId;
    private LocalDate data;
    private LocalTime oraInceput;
}
