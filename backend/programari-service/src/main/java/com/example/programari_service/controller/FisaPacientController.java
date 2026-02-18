package com.example.programari_service.controller;

import com.example.programari_service.dto.FisaPacientDetaliiDTO;
import com.example.programari_service.dto.ListaPacientiDTO;
import com.example.programari_service.service.FisaPacientService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/fisa-pacient")
@RequiredArgsConstructor
public class FisaPacientController {

    private final FisaPacientService fisaPacientService;

    // lista de pacienti (activi + arhivati) pentru un terapeut
    @GetMapping("/terapeut/{terapeutId}/lista")
    public ResponseEntity<ListaPacientiDTO> getListaPacienti(@PathVariable Long terapeutId) {
        return ResponseEntity.ok(fisaPacientService.getListaPacienti(terapeutId));
    }

    // fisa completa a unui pacient
    @GetMapping("/pacient/{pacientId}")
    public ResponseEntity<FisaPacientDetaliiDTO> getFisaPacient(
            @PathVariable Long pacientId,
            @RequestParam Long terapeutId) {
        return ResponseEntity.ok(fisaPacientService.getFisaPacient(pacientId, terapeutId));
    }
}
