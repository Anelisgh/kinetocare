package com.example.pacienti_service.dto;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class ProgramareJurnalDTO {
    private Long id;
    private String tipServiciu;
    private LocalDate data;
    private LocalTime ora;
    private String numeTerapeut;
    private Long terapeutId;
    private String numeLocatie;
}
