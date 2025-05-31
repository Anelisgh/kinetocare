package com.example.programare.controller;

import com.example.common.dto.ProgramareDTO;
import com.example.common.dto.ProgramareDetaliiDTO;
import com.example.programare.service.ProgramareService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/programare")
@RequiredArgsConstructor
public class ProgramareController {
    private final ProgramareService programareService;

    @GetMapping("/{id}")
    public ResponseEntity<ProgramareDetaliiDTO> getProgramareById(@PathVariable Long id) {
        ProgramareDetaliiDTO programare = programareService.getProgramareById(id);
        return ResponseEntity.ok(programare);
    }

    @PostMapping("/adaugare")
    public ResponseEntity<ProgramareDetaliiDTO> creazaProgramare(
            @RequestBody @Valid ProgramareDTO dto,
            @RequestHeader("Authorization") String token) {
        ProgramareDetaliiDTO response = programareService.creazaProgramare(dto, extractEmail(token));
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/pacient/{pacientId}")
    public ResponseEntity<List<ProgramareDetaliiDTO>> getProgramariPacient(@PathVariable Long pacientId) {
        return ResponseEntity.ok(programareService.getProgramariByPacient(pacientId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> stergeProgramare(
            @PathVariable Long id,
            @RequestHeader("Authorization") String token) {
        programareService.stergeProgramare(id, extractEmail(token));
        return ResponseEntity.noContent().build();
    }

    public String extractEmail(String token) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getName();
    }
    // pt sedintePanaLaReevaluare in Evaluare
    @GetMapping("/pacient/{pacientId}/completed")
    public ResponseEntity<List<ProgramareDetaliiDTO>> getCompletedSessionsAfterDate(
            @PathVariable Long pacientId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate) {

        List<ProgramareDetaliiDTO> programari = programareService.getCompletedSessionsAfterDate(pacientId, startDate);
        return ResponseEntity.ok(programari);
    }
}
