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

    @GetMapping("/by-keycloak/{keycloakId}")
    public ResponseEntity<TerapeutDTO> getTerapeutByKeycloakId(@PathVariable String keycloakId) {
        log.info("Getting terapeut by keycloakId: {}", keycloakId);
        return ResponseEntity.ok(terapeutService.getTerapeutByKeycloakId(keycloakId));
    }

    @PostMapping("/by-keycloak/{keycloakId}")
    public ResponseEntity<TerapeutDTO> createTerapeut(@PathVariable String keycloakId) {
        log.info("Creating terapeut profile for keycloakId: {}", keycloakId);
        return ResponseEntity.ok(terapeutService.createTerapeut(keycloakId));
    }

    @PatchMapping("/{keycloakId}")
    public ResponseEntity<TerapeutDTO> updateTerapeut(
            @PathVariable String keycloakId,
            @Valid @RequestBody UpdateTerapeutDTO updateDTO) {
        log.info("Updating terapeut with keycloakId: {}", keycloakId);
        return ResponseEntity.ok(terapeutService.updateTerapeut(keycloakId, updateDTO));
    }
}