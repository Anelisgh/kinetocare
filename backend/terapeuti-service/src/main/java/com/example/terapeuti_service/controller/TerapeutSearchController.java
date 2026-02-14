package com.example.terapeuti_service.controller;

import com.example.terapeuti_service.dto.TerapeutSearchDTO;
import com.example.terapeuti_service.dto.TerapeutDetaliDTO;
import com.example.terapeuti_service.entity.Specializare;
import com.example.terapeuti_service.service.TerapeutService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/terapeuti")
@RequiredArgsConstructor
@Slf4j
public class TerapeutSearchController {

    private final TerapeutService terapeutService;

    // cauta terapeuti activi dupa specializare (oglibatoriu), judet, oras sau locatieId
    // api-gateway -> searchTerapeuti (SearchTerapeutController)
    @GetMapping("/search")
    public ResponseEntity<List<TerapeutSearchDTO>> searchTerapeuti(
            @RequestParam Specializare specializare,
            @RequestParam(required = false) String judet,
            @RequestParam(required = false) String oras,
            @RequestParam(required = false) Long locatieId) {

        log.info("Searching terapeuti: specializare={}, judet={}, oras={}, locatieId={}",
                specializare, judet, oras, locatieId);

        List<TerapeutSearchDTO> results = terapeutService.searchTerapeuti(specializare, judet, oras, locatieId);
        return ResponseEntity.ok(results);
    }

    // returneaza detaliile completele ale unui terapeut 
    // api-gateway -> getMyTerapeut (SearchTerapeutController)
    @GetMapping("/{keycloakId}/details")
    public ResponseEntity<TerapeutDetaliDTO> getTerapeutDetails(@PathVariable String keycloakId) {
        log.info("Getting details for terapeut: {}", keycloakId);
        return ResponseEntity.ok(terapeutService.getTerapeutDetails(keycloakId));
    }
}