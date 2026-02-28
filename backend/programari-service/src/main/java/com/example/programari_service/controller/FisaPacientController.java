package com.example.programari_service.controller;

import com.example.programari_service.dto.FisaPacientDetaliiDTO;
import com.example.programari_service.dto.ListaPacientiDTO;
import com.example.programari_service.service.FisaPacientService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/fisa-pacient")
@RequiredArgsConstructor
public class FisaPacientController {

    private final FisaPacientService fisaPacientService;

    // lista de pacienti (activi + arhivati) pentru terapeutul logat
    @GetMapping("/lista")
    public ResponseEntity<ListaPacientiDTO> getListaPacienti(@AuthenticationPrincipal Jwt jwt) {
        String terapeutKeycloakId = jwt.getSubject();
        return ResponseEntity.ok(fisaPacientService.getListaPacienti(terapeutKeycloakId));
    }

    // lista de pacienti (activi + arhivati) pentru un terapeut (intern/admin)
    @GetMapping("/terapeut/by-keycloak/{terapeutKeycloakId}/lista")
    public ResponseEntity<ListaPacientiDTO> getListaPacienti(@PathVariable String terapeutKeycloakId) {
        return ResponseEntity.ok(fisaPacientService.getListaPacienti(terapeutKeycloakId));
    }

    // fisa completa a unui pacient (folosind terapeutul logat din JWT)
    @GetMapping("/pacient/by-keycloak/{pacientKeycloakId}")
    public ResponseEntity<FisaPacientDetaliiDTO> getFisaPacient(
            @PathVariable String pacientKeycloakId,
            @AuthenticationPrincipal Jwt jwt) {
        String terapeutKeycloakId = jwt.getSubject();
        return ResponseEntity.ok(fisaPacientService.getFisaPacient(pacientKeycloakId, terapeutKeycloakId));
    }

    // fisa completa a unui pacient (cu terapeut explicit - intern/admin)
    @GetMapping("/pacient/by-keycloak/{pacientKeycloakId}/explicit")
    public ResponseEntity<FisaPacientDetaliiDTO> getFisaPacientExplicit(
            @PathVariable String pacientKeycloakId,
            @RequestParam String terapeutKeycloakId) {
        return ResponseEntity.ok(fisaPacientService.getFisaPacient(pacientKeycloakId, terapeutKeycloakId));
    }
}
