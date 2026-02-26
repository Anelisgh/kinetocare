package com.example.notificari_service.dto;

import lombok.Builder;

import java.io.Serializable;

@Builder
public record NotificareEvent(
        String tipNotificare,
        String userKeycloakId,      // keycloakId al destinatarului notificarii (uniform pentru toti)
        String tipUser,             // "PACIENT" sau "TERAPEUT"
        String titlu,
        String mesaj,
        Long entitateLegataId,
        String tipEntitateLegata,   // "PROGRAMARE", "EVALUARE", "JURNAL", "CONVERSATIE"
        String urlActiune           // URL redirect cand se apasa notificarea
) implements Serializable {
}
