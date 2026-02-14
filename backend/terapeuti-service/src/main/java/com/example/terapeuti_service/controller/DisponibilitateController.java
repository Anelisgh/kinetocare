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
    @GetMapping("/terapeut/{keycloakId}")
    public ResponseEntity<List<DisponibilitateDTO>> getDisponibilitati(@PathVariable String keycloakId) {
        log.info("Getting disponibilitati for terapeut: {}", keycloakId);
        return ResponseEntity.ok(disponibilitateService.getDisponibilitatiByKeycloakId(keycloakId));
    }

    // adauga o noua disponibilitate pentru un terapeut
    // api-gateway -> addDisponibilitate (DisponibilitateController)
    @PostMapping("/terapeut/{keycloakId}")
    public ResponseEntity<DisponibilitateDTO> addDisponibilitate(
            @PathVariable String keycloakId,
            @Valid @RequestBody CreateDisponibilitateDTO dto) {
        log.info("Adding disponibilitate for terapeut: {}", keycloakId);
        return ResponseEntity.ok(disponibilitateService.addDisponibilitate(keycloakId, dto));
    }

    // sterge o disponibilitate a unui terapeut
    // api-gateway -> deleteDisponibilitate (DisponibilitateController)
    @DeleteMapping("/{disponibilitateId}/terapeut/{keycloakId}")
    public ResponseEntity<Void> deleteDisponibilitate(
            @PathVariable String keycloakId,
            @PathVariable Long disponibilitateId) {
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
