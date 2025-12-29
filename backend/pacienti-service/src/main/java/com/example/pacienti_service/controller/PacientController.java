package com.example.pacienti_service.controller;

import com.example.pacienti_service.dto.PacientCompleteProfileRequest;
import com.example.pacienti_service.dto.PacientKeycloakDTO;
import com.example.pacienti_service.dto.PacientRequest;
import com.example.pacienti_service.dto.PacientResponse;
import com.example.pacienti_service.service.PacientService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/pacient")
@RequiredArgsConstructor
public class PacientController {

    private final PacientService pacientService;

    @GetMapping("/by-keycloak/{keycloakId}")
    public ResponseEntity<PacientResponse> getPacientByKeycloakId(@PathVariable("keycloakId") String keycloakId) {
        return ResponseEntity.ok(pacientService.getPacientByKeycloakId(keycloakId));
    }

    // Called by programari-service to get patient info for calendar
    @GetMapping("/{id}")
    public ResponseEntity<PacientKeycloakDTO> getPacientById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(pacientService.getPacientById(id));
    }

    // in -> pacientId; out -> keycloakId
    @GetMapping("/{id}/keycloak-id")
    public ResponseEntity<PacientKeycloakDTO> getKeycloakId(@PathVariable("id") Long id) {
        return ResponseEntity.ok(pacientService.getKeycloakIdById(id));
    }

    @PostMapping("/initialize/{keycloakId}")
    public ResponseEntity<Void> initializePacient(@PathVariable String keycloakId) {
        pacientService.initializeEmptyPacient(keycloakId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{keycloakId}")
    public ResponseEntity<PacientResponse> createPacient(
            @PathVariable String keycloakId,
            @Valid @RequestBody PacientCompleteProfileRequest request) {
        return ResponseEntity.ok(pacientService.createPacient(keycloakId, request));
    }

    @PatchMapping("/{keycloakId}")
    public ResponseEntity<PacientResponse> updatePacient(
            @PathVariable String keycloakId,
            @Valid @RequestBody PacientRequest request) {
        return ResponseEntity.ok(pacientService.updatePacient(keycloakId, request));
    }

    @PostMapping("/{keycloakId}/choose-terapeut/{terapeutKeycloakId}")
    public ResponseEntity<PacientResponse> chooseTerapeut(
            @PathVariable String keycloakId,
            @PathVariable String terapeutKeycloakId,
            @RequestParam(required = false) Long locatieId) {
        return ResponseEntity.ok(pacientService.chooseTerapeut(keycloakId, terapeutKeycloakId, locatieId));
    }

    @DeleteMapping("/{keycloakId}/remove-terapeut")
    public ResponseEntity<PacientResponse> removeTerapeut(@PathVariable String keycloakId) {
        return ResponseEntity.ok(pacientService.removeTerapeut(keycloakId));
    }
}
