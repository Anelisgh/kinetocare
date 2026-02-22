package com.example.terapeuti_service.controller;

import com.example.terapeuti_service.dto.TerapeutDTO;
import com.example.terapeuti_service.dto.UpdateTerapeutDTO;
import com.example.terapeuti_service.service.TerapeutService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/terapeut")
@RequiredArgsConstructor
@Slf4j
public class TerapeutController {

    private final TerapeutService terapeutService;

    // in: keycloakId -> out: date terapeut
    // api-gateway -> getProfile (ProfileController)
    @GetMapping("/by-keycloak/{keycloakId}")
    public ResponseEntity<TerapeutDTO> getTerapeutByKeycloakId(@PathVariable String keycloakId) {
        log.info("Getting terapeut by keycloakId: {}", keycloakId);
        return ResponseEntity.ok(terapeutService.getTerapeutByKeycloakId(keycloakId));
    }

    // creaza un terapeut (pastrat ca backup, ca noi de fapt folosim initializeTerapeut)
    @PostMapping("/by-keycloak/{keycloakId}")
    public ResponseEntity<TerapeutDTO> createTerapeut(@PathVariable String keycloakId) {
        log.info("Creating terapeut profile for keycloakId: {}", keycloakId);
        return ResponseEntity.ok(terapeutService.createTerapeut(keycloakId));
    }

    // folosit in user-service (initializeRoleSpecificProfile)
    @PostMapping("/initialize/{keycloakId}")
    public ResponseEntity<Void> initializeTerapeut(@PathVariable String keycloakId) {
        terapeutService.initializeEmptyTerapeut(keycloakId);
        return ResponseEntity.ok().build();
    }

    // api-gateway -> updateProfile (ProfileController)
    @PatchMapping("/{keycloakId}")
    public ResponseEntity<TerapeutDTO> updateTerapeut(
            @PathVariable String keycloakId,
            @Valid @RequestBody UpdateTerapeutDTO updateDTO) {
        log.info("Updating terapeut with keycloakId: {}", keycloakId);
        return ResponseEntity.ok(terapeutService.updateTerapeut(keycloakId, updateDTO));
    }

    // in: terapeutId -> out: keycloakId
    // folosit de programari-service (TerapeutiClient)
    @GetMapping("/id/{terapeutId}/keycloak-id")
    public ResponseEntity<String> getKeycloakIdById(@PathVariable Long terapeutId) {
        String keycloakId = terapeutService.getKeycloakIdById(terapeutId);
        return keycloakId != null ? ResponseEntity.ok(keycloakId) : ResponseEntity.notFound().build();
    }

    // seteaza starea activa a terapeutului
    // folosit in: user-service (toggleUserActive) — endpoint intern
    @PatchMapping("/by-keycloak/{keycloakId}/toggle-active")
    public ResponseEntity<Void> toggleActive(
            @PathVariable String keycloakId,
            @RequestParam boolean active) {
        terapeutService.setActive(keycloakId, active);
        return ResponseEntity.ok().build();
    }
}