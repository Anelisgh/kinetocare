package com.example.servicii_service.controller;

import com.example.servicii_service.dto.ServiciuDTO;
import com.example.servicii_service.service.ServiciuService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/servicii")
@RequiredArgsConstructor
public class ServiciuController {

    private final ServiciuService serviciuService;

    // Endpoint pentru a returna toate serviciile
    @GetMapping
    public ResponseEntity<List<ServiciuDTO>> getAllServicii() {
        return ResponseEntity.ok(serviciuService.getAllServicii());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ServiciuDTO> getServiciu(@PathVariable Long id) {
        return ResponseEntity.ok(serviciuService.getDetaliiServiciu(id));
    }

    @GetMapping("/search")
    public ResponseEntity<ServiciuDTO> cautaServiciuDupaNume(
            @RequestParam String nume) {
        return ResponseEntity.ok(serviciuService.cautaDupaNume(nume));
    }

}
