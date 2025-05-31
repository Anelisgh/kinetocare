package com.example.evolutie.service;

import com.example.common.dto.EvolutieDTO;
import com.example.evolutie.domain.Evolutie;
import com.example.evolutie.mapper.EvolutieMapper;
import com.example.evolutie.repository.EvolutieRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EvolutieService {
    private final EvolutieRepository evolutieRepository;
    private final EvolutieMapper evolutieMapper;

    @Transactional
    public void adaugaEvolutie(@Valid EvolutieDTO evolutieDTO, String emailTerapeut) {
        Evolutie evolutie = evolutieMapper.toEvolutie(evolutieDTO, emailTerapeut);
        evolutieRepository.save(evolutie);
    }

    public List<EvolutieDTO> getEvolutiiByTerapeutId(Long terapeutId) {
        List<Evolutie> evolutii = evolutieRepository.findByTerapeutId(terapeutId);
        return evolutii.stream()
                .map(evolutieMapper::toDto)
                .collect(Collectors.toList());
    }
}
