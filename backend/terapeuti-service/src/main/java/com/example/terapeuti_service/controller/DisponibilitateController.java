package com.example.terapeuti_service.controller;

import com.example.terapeuti_service.dto.CreateDisponibilitateDTO;
import com.example.terapeuti_service.dto.DisponibilitateDTO;
import com.example.terapeuti_service.entity.DisponibilitateTerapeut;
import com.example.terapeuti_service.repository.DisponibilitateRepository;
import com.example.terapeuti_service.service.DisponibilitateService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/disponibilitate")
@RequiredArgsConstructor
@Slf4j
public class DisponibilitateController {

    private final DisponibilitateService disponibilitateService;
    private final DisponibilitateRepository disponibilitateRepository;

    // returneaza disponibilitatile active ale unui terapeut
    // api-gateway -> getDisponibilitati (DisponibilitateController)
    @GetMapping
    public ResponseEntity<List<DisponibilitateDTO>> getDisponibilitati(@AuthenticationPrincipal Jwt jwt) {
        String keycloakId = jwt.getSubject();
        log.info("Getting disponibilitati for terapeut: {}", keycloakId);
        return ResponseEntity.ok(disponibilitateService.getDisponibilitatiByKeycloakId(keycloakId));
    }

    // adauga o noua disponibilitate pentru un terapeut
    // api-gateway -> addDisponibilitate (DisponibilitateController)
    @PostMapping
    public ResponseEntity<DisponibilitateDTO> addDisponibilitate(
            @AuthenticationPrincipal Jwt jwt,
            @Valid @RequestBody CreateDisponibilitateDTO dto) {
        String keycloakId = jwt.getSubject();
        log.info("Adding disponibilitate for terapeut: {}", keycloakId);
        return ResponseEntity.ok(disponibilitateService.addDisponibilitate(keycloakId, dto));
    }

    // sterge o disponibilitate a unui terapeut
    // api-gateway -> deleteDisponibilitate (DisponibilitateController)
    @DeleteMapping("/{disponibilitateId}")
    public ResponseEntity<Void> deleteDisponibilitate(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable Long disponibilitateId) {
        String keycloakId = jwt.getSubject();
        log.info("Deleting disponibilitate {} for terapeut: {}", disponibilitateId, keycloakId);
        disponibilitateService.deleteDisponibilitate(keycloakId, disponibilitateId);
        return ResponseEntity.noContent().build();
    }
    
    // cautam orarul specific pentru o zi a saptamanii si o locatie
    // programari-service -> getOrar (TerapeutiClient)
    @GetMapping("/terapeut/{terapeutId}/locatie/{locatieId}/zi/{zi}")
    public ResponseEntity<DisponibilitateTerapeut> getOrarSpecific(
            @PathVariable Long terapeutId,
            @PathVariable Long locatieId,
            @PathVariable Integer zi) {

        return disponibilitateRepository.findByTerapeutIdAndLocatieIdAndZiSaptamana(terapeutId, locatieId, zi)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
