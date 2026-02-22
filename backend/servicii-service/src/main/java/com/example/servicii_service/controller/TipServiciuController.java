package com.example.servicii_service.controller;

import com.example.servicii_service.dto.TipServiciuDTO;
import com.example.servicii_service.service.TipServiciuService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/servicii/tipuri")
@RequiredArgsConstructor
public class TipServiciuController {

    private final TipServiciuService tipServiciuService;

    @GetMapping
    public ResponseEntity<List<TipServiciuDTO>> getAllTips() {
        return ResponseEntity.ok(tipServiciuService.getAllTips());
    }

    @PostMapping
    public ResponseEntity<TipServiciuDTO> createTip(@Valid @RequestBody TipServiciuDTO dto) {
        return ResponseEntity.ok(tipServiciuService.createTip(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TipServiciuDTO> updateTip(@PathVariable Long id, @Valid @RequestBody TipServiciuDTO dto) {
        return ResponseEntity.ok(tipServiciuService.updateTip(id, dto));
    }

    @PatchMapping("/{id}/active")
    public ResponseEntity<TipServiciuDTO> toggleActive(@PathVariable Long id) {
        return ResponseEntity.ok(tipServiciuService.toggleActive(id));
    }
}
