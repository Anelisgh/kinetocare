package com.example.user.controller;

import com.example.common.dto.PacientDTO;
import com.example.user.domain.Pacient;
import com.example.user.mapper.PacientMapper;
import com.example.user.repository.PacientRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/pacienti")
@RequiredArgsConstructor
public class PacientController {

    private final PacientRepository pacientRepository;
    private final PacientMapper pacientMapper;

    @GetMapping
    public List<PacientDTO> getAllPacienti() {
        return pacientRepository.findAll().stream()
                .map(pacientMapper::toDTO)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public PacientDTO getPacientById(@PathVariable Long id) {
        Pacient pacient = pacientRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Pacient negăsit"));
        return pacientMapper.toDTO(pacient);
    }
}
