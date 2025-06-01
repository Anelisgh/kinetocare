package com.example.user.controller;

import com.example.common.dto.TerapeutDTO;
import com.example.user.repository.TerapeutRepository;
import com.example.user.service.TerapeutService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/terapeuti")
@RequiredArgsConstructor
public class TerapeutController {

    private final TerapeutRepository terapeutRepository;
    private final TerapeutService terapeutService;

    @GetMapping("/by-email")
    public TerapeutDTO getTerapeutByEmail(@RequestParam String email) {
        return terapeutService.getTerapeutByEmail(email);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TerapeutDTO> getTerapeutById(@PathVariable Long id) {
        return ResponseEntity.ok(terapeutService.getTerapeutDtoById(id));
    }
}
