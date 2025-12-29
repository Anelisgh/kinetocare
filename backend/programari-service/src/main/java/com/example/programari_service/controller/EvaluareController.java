package com.example.programari_service.controller;

import com.example.programari_service.dto.EvaluareRequestDTO;
import com.example.programari_service.dto.UserNumeDTO;
import com.example.programari_service.entity.Evaluare;
import com.example.programari_service.service.EvaluareService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/evaluari")
@RequiredArgsConstructor
public class EvaluareController {

    private final EvaluareService evaluareService;

    // Endpoint pentru dropdown pacienți
    @GetMapping("/pacienti-recenti")
    public ResponseEntity<List<UserNumeDTO>> getPacientiTerapeut(@RequestParam Long terapeutId) {
        return ResponseEntity.ok(evaluareService.getPacientiTerapeut(terapeutId));
    }

    // Endpoint creare evaluare
    @PostMapping
    public ResponseEntity<Evaluare> creeazaEvaluare(@RequestBody EvaluareRequestDTO request) {
        return ResponseEntity.ok(evaluareService.creeazaEvaluare(request));
    }
}