package com.example.evolutie.controller;

import com.example.common.dto.EvolutieDTO;
import com.example.common.dto.PacientDTO;
import com.example.evolutie.feign.PacientFeignClient;
import com.example.evolutie.service.EvolutieService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/evolutie")
@RequiredArgsConstructor
public class EvolutieController {
    private final EvolutieService evolutieService;
    private final PacientFeignClient pacientClient;

    @GetMapping("/formular")
    public ResponseEntity<Map<String, Object>> getFormData() {
        List<PacientDTO> pacienti = pacientClient.getAllPacienti();
        return ResponseEntity.ok(Map.of("pacienti", pacienti));
    }

    @PostMapping("/adaugare")
    @PreAuthorize("hasRole('TERAPEUT')")
    public ResponseEntity<?> adaugaEvolutie(
            @RequestBody @Valid EvolutieDTO evolutieDTO,
            Authentication authentication) {
        try {
            String email = authentication.getName();
            evolutieService.adaugaEvolutie(evolutieDTO, email);
            return ResponseEntity.status(HttpStatus.CREATED).build();
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    private String extractEmailFromToken(String token) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getName();
    }
}
