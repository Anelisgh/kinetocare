package com.example.terapeuti_service.service;

import com.example.terapeuti_service.dto.statistici.StatisticiTerapeutiActiviDTO;
import com.example.terapeuti_service.entity.Locatie;
import com.example.terapeuti_service.repository.DisponibilitateRepository;
import com.example.terapeuti_service.repository.LocatieRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class StatisticiService {

    private final DisponibilitateRepository disponibilitateRepository;
    private final LocatieRepository locatieRepository;

    public List<StatisticiTerapeutiActiviDTO> getTerapeutiActiviPerLocatie() {
        log.info("Obtinere statistici: terapeuti activi per locatie");
        List<StatisticiTerapeutiActiviDTO> stats = disponibilitateRepository.countActiveTherapistsPerLocation();

        // Get map of location names
        Map<Long, String> locatiiMap = locatieRepository.findAll().stream()
                .collect(Collectors.toMap(Locatie::getId, Locatie::getNume));

        // Populate names
        return stats.stream()
                .map(s -> new StatisticiTerapeutiActiviDTO(s.locatieId(), locatiiMap.getOrDefault(s.locatieId(), "Necunoscut"), s.numarTerapeutiActivi()))
                .collect(Collectors.toList());
    }
}
