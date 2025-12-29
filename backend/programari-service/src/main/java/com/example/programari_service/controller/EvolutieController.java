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

    @PostMapping
    public ResponseEntity<EvolutieResponseDTO> adaugaEvolutie(@RequestBody EvolutieRequestDTO request) {
        return ResponseEntity.ok(evolutieService.adaugaEvolutie(request));
    }

    @GetMapping
    public ResponseEntity<List<EvolutieResponseDTO>> getIstoric(
            @RequestParam Long pacientId,
            @RequestParam Long terapeutId) {
        return ResponseEntity.ok(evolutieService.getIstoricEvolutii(pacientId, terapeutId));
    }
}