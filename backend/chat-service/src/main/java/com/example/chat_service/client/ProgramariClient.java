package com.example.chat_service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "programari-service", url = "${application.urls.programari-service}", configuration = FeignClientConfig.class)
public interface ProgramariClient {

    // Verifica daca relatia terapeutica dintre pacient si terapeut este activa.
    // Foloseste keycloakId pentru ambii participanti.
    @GetMapping("/relatii/status-keycloak")
    Boolean getRelatieStatusByKeycloak(
            @RequestParam("pacientKeycloakId") String pacientKeycloakId,
            @RequestParam("terapeutKeycloakId") String terapeutKeycloakId
    );
}
