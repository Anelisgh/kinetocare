package com.example.programari_service.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@Builder
public class ProgramareJurnalDTO {
    private Long id;
    private String tipServiciu;
    private LocalDate data;
    private LocalTime ora;
    private String numeTerapeut;
    private String numeLocatie;
}
