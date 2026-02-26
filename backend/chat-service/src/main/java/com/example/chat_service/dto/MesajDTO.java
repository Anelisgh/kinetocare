package com.example.chat_service.dto;

import com.example.chat_service.entity.TipExpeditor;
import java.time.OffsetDateTime;

public record MesajDTO(
        Long id,
        Long conversatieId,
        String expeditorKeycloakId,
        TipExpeditor tipExpeditor,
        String continut,
        Boolean esteCitit,
        OffsetDateTime cititLa,
        OffsetDateTime trimisLa
) {
}
