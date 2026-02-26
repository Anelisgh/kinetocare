package com.example.chat_service.dto;

import com.example.chat_service.entity.TipExpeditor;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record TrimitereMesajRequest(
        Long conversatieId, // Optional la prima trimitere (Lazy Initialization)
        @NotBlank(message = "ID-ul Keycloak al expeditorului este obligatoriu") String expeditorKeycloakId,
        @NotBlank(message = "ID-ul Keycloak al destinatarului este obligatoriu") String destinatarKeycloakId,
        @NotNull(message = "Tipul expeditorului este obligatoriu") TipExpeditor tipExpeditor,
        @NotBlank(message = "Conținutul mesajului nu poate fi gol") String continut
) {
}
