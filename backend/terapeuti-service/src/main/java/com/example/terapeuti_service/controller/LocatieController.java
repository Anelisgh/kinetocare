package com.example.terapeuti_service.controller;

import com.example.terapeuti_service.dto.LocatieDTO;
import com.example.terapeuti_service.service.LocatieService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/locatii")
@RequiredArgsConstructor
@Slf4j
public class LocatieController {

    private final LocatieService locatieService;

    // returneaza toate locatiile active
    // api-gateway -> getLocatii (LocatieController)
    @GetMapping
    public ResponseEntity<List<LocatieDTO>> getAllActiveLocatii() {
        log.info("Getting all active locatii");
        return ResponseEntity.ok(locatieService.getAllActiveLocatii());
    }

    // returneaza o locatie dupa id (pentru afisarea corecta si completa in frontend)
    // api-gateway -> getProfile (ProfileController)
    // programari-service -> getLocatieById (TerapeutClient)
    @GetMapping("/{id}")
    public ResponseEntity<LocatieDTO> getLocatieById(@PathVariable Long id) {
        log.info("Getting locatie by id: {}", id);
        return ResponseEntity.ok(locatieService.getLocatieById(id));
    }

    // pt admin, afiseaza toate locatiile (inclusiv cele inactive)
    @GetMapping("/all")
    public ResponseEntity<List<LocatieDTO>> getAllLocatii() {
        return ResponseEntity.ok(locatieService.getAllLocatiiForAdmin());
    }

    // pt admin, adauga o noua locatie
    @PostMapping
    public ResponseEntity<LocatieDTO> createLocatie(@RequestBody LocatieDTO locatieDTO) {
        return ResponseEntity.ok(locatieService.createLocatie(locatieDTO));
    }

    // pt admin, actualizeaza o locatie
    @PatchMapping("/{id}")
    public ResponseEntity<LocatieDTO> updateLocatie(
            @PathVariable Long id,
            @RequestBody LocatieDTO locatieDTO) {
        return ResponseEntity.ok(locatieService.updateLocatie(id, locatieDTO));
    }

    // pt admin, dezactiveaza o locatie (soft delete)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> toggleLocatieStatus(@PathVariable Long id) {
        locatieService.deleteLocatie(id);
        return ResponseEntity.ok().build();
    }
}