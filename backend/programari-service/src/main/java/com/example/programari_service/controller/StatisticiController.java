package com.example.programari_service.controller;

import com.example.programari_service.dto.statistici.*;
import com.example.programari_service.service.StatisticiService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/programari/statistici")
@RequiredArgsConstructor
public class StatisticiController {

    private final StatisticiService statisticiService;

    @GetMapping("/locatii/programari-lunar")
    public ResponseEntity<List<StatisticiProgramariLunareDTO>> getProgramariLunare(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return ResponseEntity.ok(statisticiService.getProgramariLunare(startDate, endDate));
    }

    @GetMapping("/locatii/venituri")
    public ResponseEntity<List<StatisticiVenituriLocatieDTO>> getVenituriLocatie(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return ResponseEntity.ok(statisticiService.getVenituriLocatie(startDate, endDate));
    }

    @GetMapping("/locatii/rata-anulare")
    public ResponseEntity<List<StatisticiRataAnulareDTO>> getRataAnulare(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return ResponseEntity.ok(statisticiService.getRataAnulare(startDate, endDate));
    }

    @GetMapping("/locatii/pacienti-noi")
    public ResponseEntity<List<StatisticiPacientiNoiDTO>> getPacientiNoi(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return ResponseEntity.ok(statisticiService.getPacientiNoi(startDate, endDate));
    }

    @GetMapping("/terapeuti/programari")
    public ResponseEntity<List<StatisticiTerapeutDTO>> getProgramariTerapeut(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return ResponseEntity.ok(statisticiService.getProgramariTerapeut(startDate, endDate));
    }
}
