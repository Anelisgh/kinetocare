package com.example.pacienti_service.controller;

import com.example.pacienti_service.dto.JurnalIstoricDTO;
import com.example.pacienti_service.dto.JurnalRequestDTO;
import com.example.pacienti_service.service.JurnalService;
import com.example.pacienti_service.service.PacientService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/jurnal")
@RequiredArgsConstructor
public class JurnalController {

    private final JurnalService jurnalService;
    private final PacientService pacientService;

    // adauga un jurnal nou (pacient logat)
    @PostMapping
    public ResponseEntity<Void> adaugaJurnal(@AuthenticationPrincipal Jwt jwt, @RequestBody JurnalRequestDTO request) {
        String keycloakId = jwt.getSubject();
        Long pacientId = pacientService.getPacientByKeycloakId(keycloakId).id();
        jurnalService.adaugaJurnal(pacientId, request);
        return ResponseEntity.ok().build();
    }

    // adauga un jurnal nou (explicit)
    @PostMapping("/by-keycloak/{keycloakId}")
    public ResponseEntity<Void> adaugaJurnal(@PathVariable String keycloakId, @RequestBody JurnalRequestDTO request) {
        Long pacientId = pacientService.getPacientByKeycloakId(keycloakId).id();
        jurnalService.adaugaJurnal(pacientId, request);
        return ResponseEntity.ok().build();
    }

    // editeaza un jurnal existent (pacient logat)
    @PutMapping("/{jurnalId}")
    public ResponseEntity<Void> updateJurnal(@AuthenticationPrincipal Jwt jwt, @PathVariable Long jurnalId, @RequestBody JurnalRequestDTO request) {
        String keycloakId = jwt.getSubject();
        Long pacientId = pacientService.getPacientByKeycloakId(keycloakId).id();
        jurnalService.actualizeazaJurnal(pacientId, jurnalId, request);
        return ResponseEntity.ok().build();
    }

    // editeaza un jurnal existent (explicit)
    @PutMapping("/by-keycloak/{keycloakId}/{jurnalId}")
    public ResponseEntity<Void> updateJurnal(@PathVariable String keycloakId, @PathVariable Long jurnalId, @RequestBody JurnalRequestDTO request) {
        Long pacientId = pacientService.getPacientByKeycloakId(keycloakId).id();
        jurnalService.actualizeazaJurnal(pacientId, jurnalId, request);
        return ResponseEntity.ok().build();
    }

    // returneaza istoricul jurnalului (pacient logat)
    @GetMapping("/istoric")
    public ResponseEntity<List<JurnalIstoricDTO>> getIstoric(@AuthenticationPrincipal Jwt jwt) {
        String keycloakId = jwt.getSubject();
        Long pacientId = pacientService.getPacientByKeycloakId(keycloakId).id();
        return ResponseEntity.ok(jurnalService.getIstoric(pacientId));
    }

    // returneaza istoricul jurnalului (explicit)
    @GetMapping("/by-keycloak/{keycloakId}/istoric")
    public ResponseEntity<List<JurnalIstoricDTO>> getIstoric(@PathVariable String keycloakId) {
        Long pacientId = pacientService.getPacientByKeycloakId(keycloakId).id();
        return ResponseEntity.ok(jurnalService.getIstoric(pacientId));
    }
}