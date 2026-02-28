package com.example.programari_service.controller;

import com.example.programari_service.service.RelatieService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/relatii")
@RequiredArgsConstructor
public class RelatiiController {

    private final RelatieService relatieService;

    @GetMapping("/status")
    public ResponseEntity<Boolean> getRelatieStatus(
            @RequestParam String pacientKeycloakId,
            @RequestParam String terapeutKeycloakId) {
        boolean isActiva = relatieService.isRelatieActivaByKeycloak(pacientKeycloakId, terapeutKeycloakId);
        return ResponseEntity.ok(isActiva);
    }

    // Endpoint folosit de chat-service (WebSocket) — identifica relatia prin keycloakId
    // deoarece chat-service stocheaza keycloakId, nu service-specific IDs
    @GetMapping("/status-keycloak")
    public ResponseEntity<Boolean> getRelatieStatusByKeycloak(
            @RequestParam String pacientKeycloakId,
            @RequestParam String terapeutKeycloakId) {
        boolean isActiva = relatieService.isRelatieActivaByKeycloak(pacientKeycloakId, terapeutKeycloakId);
        return ResponseEntity.ok(isActiva);
    }

    @GetMapping("/parteneri-activi")
    public ResponseEntity<java.util.List<String>> getParteneriActivi(
            @RequestParam String userKeycloakId,
            @RequestParam String tipUser) {
        return ResponseEntity.ok(relatieService.getParteneriActiviKeycloak(userKeycloakId, tipUser));
    }

    // Endpoint destinat api-gateway (ChatGatewayService) — returneaza keycloakId-urile
    // partenerilor activi in loc de service-specific ID-uri
    @GetMapping("/parteneri-activi-keycloak")
    public ResponseEntity<java.util.List<String>> getParteneriActiviKeycloak(
            @RequestParam String userKeycloakId,
            @RequestParam String tipUser) {
        return ResponseEntity.ok(relatieService.getParteneriActiviKeycloak(userKeycloakId, tipUser));
    }
}
