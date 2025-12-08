package com.example.terapeuti_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.OffsetDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConcediuDTO {
    private Long id;
    private Long terapeutId;
    private LocalDate dataInceput;
    private LocalDate dataSfarsit;
    private OffsetDateTime createdAt;
}
