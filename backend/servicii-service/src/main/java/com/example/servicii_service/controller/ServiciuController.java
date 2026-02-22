package com.example.servicii_service.controller;

import com.example.servicii_service.dto.ServiciuDTO;
import com.example.servicii_service.service.ServiciuService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import jakarta.validation.Valid;

import com.example.servicii_service.dto.ServiciuAdminDTO;

@RestController
@RequestMapping("/servicii")
@RequiredArgsConstructor
public class ServiciuController {

    private final ServiciuService serviciuService;

    // CLIENT: returneaza toate serviciile active
    @GetMapping
    public ResponseEntity<List<ServiciuDTO>> getAllServicii() {
        return ResponseEntity.ok(serviciuService.getAllServicii());
    }

    // ADMIN: returneaza toate (inclusiv inactive, cu detalii complete)
    @GetMapping("/admin")
    public ResponseEntity<List<ServiciuAdminDTO>> getAllServiciiAdmin() {
        return ResponseEntity.ok(serviciuService.getAllServiciiAdmin());
    }

    // ADMIN: Create
    @PostMapping
    public ResponseEntity<ServiciuAdminDTO> createServiciu(@Valid @RequestBody ServiciuAdminDTO dto) {
        return ResponseEntity.ok(serviciuService.createServiciu(dto));
    }

    // ADMIN: Update
    @PutMapping("/{id}")
    public ResponseEntity<ServiciuAdminDTO> updateServiciu(@PathVariable Long id, @Valid @RequestBody ServiciuAdminDTO dto) {
        return ResponseEntity.ok(serviciuService.updateServiciu(id, dto));
    }

    // ADMIN: Toggle Active
    @PatchMapping("/{id}/active")
    public ResponseEntity<ServiciuAdminDTO> toggleActive(@PathVariable Long id) {
        return ResponseEntity.ok(serviciuService.toggleActive(id));
    }

    // returneaza un serviciu dupa id
    // programari-service -> getServiciuById (ServiciiClient)
    @GetMapping("/{id}")
    public ResponseEntity<ServiciuDTO> getServiciu(@PathVariable Long id) {
        return ResponseEntity.ok(serviciuService.getDetaliiServiciu(id));
    }

    // cauta serviciu dupa nume
    // programari-service -> gasesteServiciuDupaNume (ServiciiClient)
    @GetMapping("/search")
    public ResponseEntity<ServiciuDTO> cautaServiciuDupaNume(
            @RequestParam String nume) {
        return ResponseEntity.ok(serviciuService.cautaDupaNume(nume));
    }

}
