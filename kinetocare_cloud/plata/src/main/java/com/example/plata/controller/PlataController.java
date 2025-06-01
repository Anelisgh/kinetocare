package com.example.plata.controller;

import com.example.common.dto.PlataDTO;
import com.example.common.dto.UpdateStarePlataDTO;
import com.example.plata.service.PlataService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/plata")
@RequiredArgsConstructor
public class PlataController {
    private final PlataService plataService;

    @PostMapping("/programare/{programareId}")
    public ResponseEntity<PlataDTO> creazaPlata(@PathVariable Long programareId) {
        PlataDTO plataDTO = plataService.creazaPlata(programareId);
        return ResponseEntity.status(HttpStatus.CREATED).body(plataDTO);
    }

    @PatchMapping("/{id}/stare")
    public ResponseEntity<PlataDTO> actualizeazaStarePlata(
            @PathVariable Long id,
            @RequestBody UpdateStarePlataDTO dto) {
        return ResponseEntity.ok(plataService.actualizeazaStarePlata(id, dto));
    }

    @GetMapping("/pacient/{pacientId}")
    @PreAuthorize("hasRole('PACIENT')")
    public ResponseEntity<List<PlataDTO>> getPlatiPacient(
            @PathVariable Long pacientId,
            Authentication authentication) {
        try {
            String email = authentication.getName();
            return ResponseEntity.ok(plataService.getPlatiPentruPacient(pacientId));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
