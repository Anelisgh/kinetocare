package com.example.kinetocare.service;

import com.example.kinetocare.domain.Evolutie;
import com.example.kinetocare.domain.Terapeut;
import com.example.kinetocare.dto.EvolutieDTO;
import com.example.kinetocare.mapper.EvolutieMapper;
import com.example.kinetocare.repository.EvolutieRepository;
import com.example.kinetocare.repository.TerapeutRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

@Service
@Validated
@RequiredArgsConstructor
public class EvolutieService {
    private final EvolutieMapper evolutieMapper;
    private final TerapeutRepository terapeutRepository;
    private final EvolutieRepository evolutieRepository;

    @Transactional
    public void adaugaEvolutie(@Valid EvolutieDTO evolutieDTO, String emailTerapeut) {
        Terapeut terapeut = terapeutRepository.findByUserEmail(emailTerapeut)
                .orElseThrow(() -> new EntityNotFoundException("Terapeut nu există"));
        Evolutie evolutie = evolutieMapper.toEvolutie(evolutieDTO, terapeut);
        evolutieRepository.save(evolutie);
    }
}
