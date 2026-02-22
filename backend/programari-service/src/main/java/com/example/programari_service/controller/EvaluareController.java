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

    // pentru dropdown pacienti
    // returneaza lista de pacienti cu care terapeutul a avut programari
    // api-gateway -> getPacientiTerapeut (EvaluareController)
    @GetMapping("/pacienti-recenti")
    public ResponseEntity<List<UserNumeDTO>> getPacientiTerapeut(@RequestParam Long terapeutId) {
        return ResponseEntity.ok(evaluareService.getPacientiTerapeut(terapeutId));
    }

    // creare evaluare
    // primeste diagnosticul, sedintele recomandate, serviciul recomandat si leaga programarea de ultima programare pentru a activa relatia pacient-terapeut
    // api-gateway -> creeazaEvaluare (EvaluareController)
    @PostMapping
    public ResponseEntity<Evaluare> creeazaEvaluare(@RequestBody EvaluareRequestDTO request) {
        return ResponseEntity.ok(evaluareService.creeazaEvaluare(request));
    }

    // editeaza evaluare
    // api-gateway -> updateEvaluare (EvaluareController)
    @PutMapping("/{id}")
    public ResponseEntity<Evaluare> updateEvaluare(@PathVariable Long id, @RequestBody EvaluareRequestDTO request) {
        return ResponseEntity.ok(evaluareService.actualizeazaEvaluare(id, request));
    }
}