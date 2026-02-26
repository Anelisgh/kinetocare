package com.example.programari_service.dto;

import lombok.Builder;
import java.io.Serializable;

@Builder
public record NotificareEvent(
    String tipNotificare,
    String userKeycloakId,      // keycloakId al destinatarului (contractul RabbitMQ uniform)
    String tipUser,
    String titlu,
    String mesaj,
    Long entitateLegataId,
    String tipEntitateLegata,
    String urlActiune
) implements Serializable {}
