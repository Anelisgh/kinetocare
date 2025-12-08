package com.example.terapeuti_service.controller;

import com.example.terapeuti_service.dto.ConcediuDTO;
import com.example.terapeuti_service.dto.CreateConcediuDTO;
import com.example.terapeuti_service.repository.ConcediuRepository;
import com.example.terapeuti_service.service.ConcediuService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/concediu")
@RequiredArgsConstructor
@Slf4j
public class ConcediuController {

    private final ConcediuService concediuService;
    private final ConcediuRepository concediuRepository;

    @GetMapping("/terapeut/{keycloakId}")
    public ResponseEntity<List<ConcediuDTO>> getConcedii(@PathVariable String keycloakId) {
        log.info("Getting concedii for terapeut: {}", keycloakId);
        return ResponseEntity.ok(concediuService.getConcediiByKeycloakId(keycloakId));
    }

    @PostMapping("/terapeut/{keycloakId}")
    public ResponseEntity<ConcediuDTO> addConcediu(
            @PathVariable String keycloakId,
            @Valid @RequestBody CreateConcediuDTO dto) {
        log.info("Adding concediu for terapeut: {}", keycloakId);
        return ResponseEntity.ok(concediuService.addConcediu(keycloakId, dto));
    }

    @DeleteMapping("/{concediuId}/terapeut/{keycloakId}")
    public ResponseEntity<Void> deleteConcediu(
            @PathVariable String keycloakId,
            @PathVariable Long concediuId) {
        log.info("Deleting concediu {} for terapeut: {}", concediuId, keycloakId);
        concediuService.deleteConcediu(keycloakId, concediuId);
        return ResponseEntity.noContent().build();
    }

    // verificam daca o data specifica este in concediu pentru un terapeut dat
    @GetMapping("/check/terapeut/{terapeutId}/data/{data}")
    public ResponseEntity<Boolean> isTerapeutInConcediu(
            @PathVariable Long terapeutId,
            @PathVariable String data) { // Format yyyy-mm-dd

        LocalDate date = LocalDate.parse(data);
        boolean inConcediu = concediuRepository.existsByTerapeutIdAndDataInceputLessThanEqualAndDataSfarsitGreaterThanEqual(
                terapeutId, date, date);

        return ResponseEntity.ok(inConcediu);
    }
}
