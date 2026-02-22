package com.example.programari_service.controller;

import com.example.programari_service.dto.EvolutieRequestDTO;
import com.example.programari_service.dto.EvolutieResponseDTO;
import com.example.programari_service.service.EvolutieService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/evolutii")
@RequiredArgsConstructor
public class EvolutieController {

    private final EvolutieService evolutieService;

    // adauga evolutie
    // api-gateway -> adaugaEvolutie (EvolutieController)
    @PostMapping
    public ResponseEntity<EvolutieResponseDTO> adaugaEvolutie(@RequestBody EvolutieRequestDTO request) {
        return ResponseEntity.ok(evolutieService.adaugaEvolutie(request));
    }

    // editeaza evolutie
    // api-gateway -> updateEvolutie (EvolutieController)
    @PutMapping("/{id}")
    public ResponseEntity<EvolutieResponseDTO> updateEvolutie(@PathVariable Long id, @RequestBody EvolutieRequestDTO request) {
        return ResponseEntity.ok(evolutieService.actualizeazaEvolutie(id, request));
    }

    // afiseaza istoricul de evolutii pentru un pacient
    // api-gateway -> getIstoric (EvolutieController)
    @GetMapping
    public ResponseEntity<List<EvolutieResponseDTO>> getIstoric(
            @RequestParam Long pacientId,
            @RequestParam Long terapeutId) {
        return ResponseEntity.ok(evolutieService.getIstoricEvolutii(pacientId, terapeutId));
    }
}