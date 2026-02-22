package com.example.pacienti_service.dto;

import lombok.Builder;
import java.io.Serializable;

@Builder
public record NotificareEvent(
        String tipNotificare,
        Long userId,
        String tipUser,       // "PACIENT" sau "TERAPEUT"
        String titlu,
        String mesaj,
        Long entitateLegataId,
        String tipEntitateLegata, // "PROGRAMARE", "EVALUARE", "JURNAL"
        String urlActiune         // URL redirect cand se apasa notificarea
) implements Serializable {}
