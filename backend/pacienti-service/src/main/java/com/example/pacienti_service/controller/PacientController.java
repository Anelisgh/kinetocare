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

    // returneaza datele pacientului dupa id
    // api-gateway -> getMyTerapeut (SearchTerapeutController)
    // api-gateway -> getHomepageData (ProfileController)
    @GetMapping("/by-keycloak/{keycloakId}")
    public ResponseEntity<PacientResponse> getPacientByKeycloakId(@PathVariable("keycloakId") String keycloakId) {
        return ResponseEntity.ok(pacientService.getPacientByKeycloakId(keycloakId));
    }

    // apelat de programari-service pentru a obtine datele pacientului pentru calendar (PacientiClient)
    @GetMapping("/{id}")
    public ResponseEntity<PacientKeycloakDTO> getPacientById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(pacientService.getPacientById(id));
    }

    // in -> pacientId; out -> keycloakId
    @GetMapping("/{id}/keycloak-id")
    public ResponseEntity<PacientKeycloakDTO> getKeycloakId(@PathVariable("id") Long id) {
        return ResponseEntity.ok(pacientService.getKeycloakIdById(id));
    }

    // in -> List<Long> ids; out -> Map<Long, String>
    @PostMapping("/batch/keycloak-ids")
    public ResponseEntity<java.util.Map<Long, String>> getBatchKeycloakIds(@RequestBody java.util.List<Long> ids) {
        return ResponseEntity.ok(pacientService.getBatchKeycloakIds(ids));
    }

    // crearea unui pacient nou gol (dupa register)
    // folosit in user-service (initializeRoleSpecificProfile)
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

    // update partial al profilului (imediat dupa prima logare)
    // update pentru pacienti folosit in api-gateway (updateProfile)
    @PatchMapping("/{keycloakId}")
    public ResponseEntity<PacientResponse> updatePacient(
            @PathVariable String keycloakId,
            @Valid @RequestBody PacientRequest request) {
        return ResponseEntity.ok(pacientService.updatePacient(keycloakId, request));
    }

    // pacientul isi alege terapeutul
    // api-gateway -> chooseTerapeut (SearchTerapeutController)
    @PostMapping("/{keycloakId}/choose-terapeut/{terapeutKeycloakId}")
    public ResponseEntity<PacientResponse> chooseTerapeut(
            @PathVariable String keycloakId,
            @PathVariable String terapeutKeycloakId,
            @RequestParam(required = false) Long locatieId) {
        return ResponseEntity.ok(pacientService.chooseTerapeut(keycloakId, terapeutKeycloakId, locatieId));
    }

    // pacientul isi elimina terapeutul curent
    // api-gateway -> removeTerapeut (SearchTerapeutController)
    @DeleteMapping("/{keycloakId}/remove-terapeut")
    public ResponseEntity<PacientResponse> removeTerapeut(@PathVariable String keycloakId) {
        return ResponseEntity.ok(pacientService.removeTerapeut(keycloakId));
    }

    // seteaza starea activa a pacientului
    // folosit in: user-service (toggleUserActive) — endpoint intern
    @PatchMapping("/by-keycloak/{keycloakId}/toggle-active")
    public ResponseEntity<Void> toggleActive(
            @PathVariable String keycloakId,
            @RequestParam boolean active) {
        pacientService.setActive(keycloakId, active);
        return ResponseEntity.ok().build();
    }
}
