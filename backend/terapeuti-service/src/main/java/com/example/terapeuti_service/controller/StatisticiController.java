package com.example.terapeuti_service.controller;

import com.example.terapeuti_service.dto.statistici.StatisticiTerapeutiActiviDTO;
import com.example.terapeuti_service.service.StatisticiService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/terapeut/statistici")
@RequiredArgsConstructor
public class StatisticiController {

    private final StatisticiService statisticiService;

    @GetMapping("/locatii/terapeuti-activi")
    public ResponseEntity<List<StatisticiTerapeutiActiviDTO>> getTerapeutiActivi() {
        return ResponseEntity.ok(statisticiService.getTerapeutiActiviPerLocatie());
    }
}
