package com.example.programari_service.dto;

import com.example.programari_service.entity.MotivAnulare;
import com.example.programari_service.entity.StatusProgramare;
import java.time.LocalDateTime;

public record CalendarProgramareDTO(
    Long id,
    String title,
    LocalDateTime start,
    LocalDateTime end,
    String numeLocatie,
    String tipServiciu,
    StatusProgramare status,
    MotivAnulare motivAnulare,
    boolean primaIntalnire,
    String telefonPacient
) {}
