package com.example.chat_service.dto;

import lombok.Builder;

import java.io.Serializable;

@Builder
public record NotificareEvent(
        String tipNotificare,
        String userKeycloakId,    // keycloakId al destinatarului notificarii
        String tipUser,
        String titlu,
        String mesaj,
        Long entitateLegataId,
        String tipEntitateLegata,
        String urlActiune
) implements Serializable {
}
