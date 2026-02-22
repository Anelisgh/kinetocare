package com.example.terapeuti_service.controller;

import com.example.terapeuti_service.dto.ConcediuDTO;
import com.example.terapeuti_service.dto.CreateConcediuDTO;
import com.example.terapeuti_service.repository.ConcediuRepository;
import com.example.terapeuti_service.service.ConcediuService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
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

    // returneaza concediile unui terapeut 
    // api-gateway -> getConcedii (ConcediuController)
    @GetMapping
    public ResponseEntity<List<ConcediuDTO>> getConcedii(@AuthenticationPrincipal Jwt jwt) {
        String keycloakId = jwt.getSubject();
        log.info("Getting concedii for terapeut: {}", keycloakId);
        return ResponseEntity.ok(concediuService.getConcediiByKeycloakId(keycloakId));
    }

    // adauga un concediu pentru un terapeut 
    // api-gateway -> addConcediu (ConcediuController)
    @PostMapping
    public ResponseEntity<ConcediuDTO> addConcediu(
            @AuthenticationPrincipal Jwt jwt,
            @Valid @RequestBody CreateConcediuDTO dto) {
        String keycloakId = jwt.getSubject();
        log.info("Adding concediu for terapeut: {}", keycloakId);
        return ResponseEntity.ok(concediuService.addConcediu(keycloakId, dto));
    }

    // sterge un concediu pentru un terapeut 
    // api-gateway -> deleteConcediu (ConcediuController)
    @DeleteMapping("/{concediuId}")
    public ResponseEntity<Void> deleteConcediu(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable Long concediuId) {
        String keycloakId = jwt.getSubject();
        log.info("Deleting concediu {} for terapeut: {}", concediuId, keycloakId);
        concediuService.deleteConcediu(keycloakId, concediuId);
        return ResponseEntity.noContent().build();
    }

    // verifica daca terapeutul e in concediu la o data specifica
    // programari-service -> checkConcediu (TerapeutiClient) 
    @GetMapping("/check/terapeut/{terapeutId}/data/{data}")
    public ResponseEntity<Boolean> isTerapeutInConcediu(
            @PathVariable Long terapeutId,
            @PathVariable String data) { // Format yyyy-mm-dd
        LocalDate date = LocalDate.parse(data);
        boolean inConcediu = concediuRepository.isTerapeutInConcediu(
                terapeutId, date, date);
        return ResponseEntity.ok(inConcediu);
    }
}
