package com.example.api_gateway.dto;

import java.util.Map;

public record ConversatieAgregataDTO(
    Long conversatieId,
    String partenerKeycloakId,    // keycloakId uniform — nu mai e Long service-specific ID
    String partenerNume,
    Map<String, Object> ultimulMesaj,
    Boolean isArhivat
) {}
