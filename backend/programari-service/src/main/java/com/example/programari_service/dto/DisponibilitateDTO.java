package com.example.programari_service.dto;

import lombok.Data;

import java.time.LocalTime;

@Data
public class DisponibilitateDTO {
    private LocalTime oraInceput;
    private LocalTime oraSfarsit;
}
