package com.example.pacienti_service.controller;

import com.example.pacienti_service.dto.JurnalIstoricDTO;
import com.example.pacienti_service.dto.JurnalRequestDTO;
import com.example.pacienti_service.service.JurnalService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/jurnal")
@RequiredArgsConstructor
public class JurnalController {

    private final JurnalService jurnalService;

    @PostMapping("/{pacientId}")
    public ResponseEntity<Void> adaugaJurnal(@PathVariable Long pacientId, @RequestBody JurnalRequestDTO request) {
        jurnalService.adaugaJurnal(pacientId, request);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{pacientId}/istoric")
    public ResponseEntity<List<JurnalIstoricDTO>> getIstoric(@PathVariable Long pacientId) {
        return ResponseEntity.ok(jurnalService.getIstoric(pacientId));
    }
}