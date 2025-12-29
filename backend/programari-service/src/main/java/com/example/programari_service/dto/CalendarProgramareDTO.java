package com.example.programari_service.dto;

import com.example.programari_service.entity.MotivAnulare;
import com.example.programari_service.entity.StatusProgramare;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class CalendarProgramareDTO {
    private Long id;
    private String title;          // Nume + Prenume Pacient
    private LocalDateTime start;   // Data + OraInceput
    private LocalDateTime end;     // Data + OraSfarsit
    private String numeLocatie;
    private String tipServiciu;
    private StatusProgramare status;
    private MotivAnulare motivAnulare;
    private boolean primaIntalnire;
    private String telefonPacient;
}