package com.example.chat_service.dto;

import java.time.OffsetDateTime;

public record ConversatieDTO(
        Long id,
        String pacientKeycloakId,
        String terapeutKeycloakId,
        OffsetDateTime ultimulMesajLa,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt,
        MesajDTO ultimulMesaj
) {
}
