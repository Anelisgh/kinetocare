package com.example.evaluare.controller;

import com.example.common.dto.EvaluareDTO;
import com.example.common.dto.PacientDTO;
import com.example.common.dto.ServiciuDTO;
import com.example.common.enums.TipEvaluare;
import com.example.common.enums.TipServiciu;
import com.example.common.security.JwtUtil;
import com.example.evaluare.domain.Serviciu;
import com.example.evaluare.feign.PacientFeignClient;
import com.example.evaluare.feign.TerapeutFeignClient;
import com.example.evaluare.repository.ServiciuRepository;
import com.example.evaluare.service.EvaluareService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/evaluare")
@RequiredArgsConstructor
public class EvaluareController {

    private final EvaluareService evaluareService;
    private final ServiciuRepository serviciuRepository;
    private final PacientFeignClient pacientClient;
    private final TerapeutFeignClient terapeutClient;
    private final JwtUtil jwtUtil;

    @GetMapping("/pacienti")
    public ResponseEntity<List<PacientDTO>> getPacienti() {
        return ResponseEntity.ok(pacientClient.getAllPacienti());
    }
// Metoda "ajutatoare"
    @GetMapping("/formular")
    public ResponseEntity<Map<String, Object>> getFormData() {
        List<PacientDTO> pacienti = pacientClient.getAllPacienti();
        List<TipEvaluare> tipuriEvaluare = Arrays.asList(TipEvaluare.values());
        List<TipServiciu> servicii = evaluareService.getServiciiFiltrate();

        return ResponseEntity.ok(Map.of(
                "pacienti", pacienti,
                "tipuriEvaluare", tipuriEvaluare,
                "servicii", servicii
        ));
    }

    @PostMapping("/adaugare")
    @PreAuthorize("hasRole('TERAPEUT')")
    public ResponseEntity<?> adaugaEvaluare(
            @RequestBody @Valid EvaluareDTO evaluareDTO,
            Authentication authentication) {
        try {
            String email = authentication.getName();
            evaluareService.adaugaEvaluare(evaluareDTO, email);
            return ResponseEntity.status(HttpStatus.CREATED).build();
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

// ex de cerere, sa-mi fie mai usor:
//{
//    "pacientId": 1,
//        "tipEvaluare": "INITIALA",
//        "dataEvaluare": "2025-05-27",
//        "numeDiagnostic": "Lombalgie cronică",
//        "sedinteRecomandate": 10,
//        "tipServiciu": "TERAPIE_MOTORIE",
//        "observatii": "Pacientul prezintă rigiditate în zona lombară"
//}

    @GetMapping("/terapeut/{terapeutId}")
    public ResponseEntity<List<EvaluareDTO>> getEvaluariByTerapeut(@PathVariable Long terapeutId) {
        List<EvaluareDTO> evaluari = evaluareService.getEvaluariByTerapeutId(terapeutId);
        return ResponseEntity.ok(evaluari);
    }

    // pentru feign-ul din programare si plata
    @GetMapping("/servicii/{id}")
    public ResponseEntity<ServiciuDTO> getServiciuById(@PathVariable Long id) {
        Serviciu serviciu = serviciuRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Serviciul nu există"));

        ServiciuDTO dto = ServiciuDTO.builder()
                .id(serviciu.getId())
                .tipServiciu(serviciu.getTipServiciu())
                .pret(serviciu.getPret())
                .durataMinute(serviciu.getDurataMinute())
                .build();

        return ResponseEntity.ok(dto);
    }
}
