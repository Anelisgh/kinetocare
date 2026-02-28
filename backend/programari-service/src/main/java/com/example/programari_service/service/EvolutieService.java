package com.example.programari_service.service;

import com.example.programari_service.dto.EvolutieRequestDTO;
import com.example.programari_service.dto.EvolutieResponseDTO;
import com.example.programari_service.entity.Evolutie;
import com.example.programari_service.exception.ResourceNotFoundException;
import com.example.programari_service.mapper.EvolutieMapper;
import com.example.programari_service.repository.EvolutieRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class EvolutieService {

    private final EvolutieRepository evolutieRepository;
    private final EvolutieMapper evolutieMapper;

    // adaugare evolutie noua
    @Transactional
    public EvolutieResponseDTO adaugaEvolutie(EvolutieRequestDTO request) {
        Evolutie evolutie = evolutieMapper.toEntity(request);
        Evolutie salvata = evolutieRepository.save(evolutie);
        return evolutieMapper.toDto(salvata);
    }

    // editare evolutie existenta
    @Transactional
    public EvolutieResponseDTO actualizeazaEvolutie(Long id, EvolutieRequestDTO request) {
        Evolutie evolutie = evolutieRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Evoluția cu ID-ul " + id + " nu a fost găsită."));

        evolutie.setObservatii(request.observatii());
        Evolutie salvata = evolutieRepository.save(evolutie);
        return evolutieMapper.toDto(salvata);
    }

    // istoric evolutii pentru un pacient specific (doar notele acestui terapeut)
    @Transactional(readOnly = true)
    public List<EvolutieResponseDTO> getIstoricEvolutii(String pacientKeycloakId, String terapeutKeycloakId) {
        return evolutieRepository.findAllByPacientKeycloakIdAndTerapeutKeycloakIdOrderByCreatedAtDesc(pacientKeycloakId, terapeutKeycloakId)
                .stream()
                .map(evolutieMapper::toDto)
                .toList();
    }
}