package com.example.programari_service.controller;

import com.example.programari_service.dto.CreeazaProgramareRequest;
import com.example.programari_service.dto.DetaliiServiciuDTO;
import com.example.programari_service.dto.UrmatoareaProgramareDTO;
import com.example.programari_service.entity.Programare;
import com.example.programari_service.service.ProgramareService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@RestController
@RequestMapping("/programari")
@RequiredArgsConstructor
public class ProgramareController {

    private final ProgramareService programareService;

    @PostMapping
    public ResponseEntity<Programare> creeazaProgramare(@RequestBody CreeazaProgramareRequest request) {
        Programare noua = programareService.creeazaProgramare(request);
        return ResponseEntity.ok(noua);
    }

    @GetMapping("/pacient/{id}/next")
    public ResponseEntity<UrmatoareaProgramareDTO> getUrmatoareaProgramare(@PathVariable("id") Long pacientId) {
        return programareService.getUrmatoareaProgramare(pacientId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.noContent().build());
    }

    @PatchMapping("/{id}/cancel")
    public ResponseEntity<Void> anuleazaProgramare(@PathVariable Long id, @RequestParam Long pacientId) {
        programareService.anuleazaProgramare(id, pacientId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/disponibilitate")
    public ResponseEntity<List<LocalTime>> getDisponibilitate(
            @RequestParam Long terapeutId,
            @RequestParam Long locatieId,
            @RequestParam String data, // yyyy-mm-dd
            @RequestParam Long serviciuId) {

        LocalDate date = LocalDate.parse(data);
        List<LocalTime> sloturi = programareService.getSloturiDisponibile(terapeutId, locatieId, date, serviciuId);

        return ResponseEntity.ok(sloturi);
    }

    @GetMapping("/serviciu-recomandat")
    public ResponseEntity<DetaliiServiciuDTO> getServiciuRecomandat(@RequestParam Long pacientId) {
        DetaliiServiciuDTO serviciu = programareService.determinaServiciulCorect(pacientId);
        return ResponseEntity.ok(serviciu);
    }
}
