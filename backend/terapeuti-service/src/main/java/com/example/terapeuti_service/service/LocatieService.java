package com.example.terapeuti_service.service;

import com.example.terapeuti_service.dto.LocatieDTO;
import com.example.terapeuti_service.entity.Locatie;
import com.example.terapeuti_service.mapper.LocatieMapper;
import com.example.terapeuti_service.repository.LocatieRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class LocatieService {

    private final LocatieRepository locatieRepository;
    private final LocatieMapper locatieMapper;

    // gaseste locatiile active
    public List<LocatieDTO> getAllActiveLocatii() {
        return locatieRepository.findByActiveTrue()
                .stream()
                .map(locatieMapper::toDTO)
                .collect(Collectors.toList());
    }

    // doar pt admin (include si locatiile inactive)
    public List<LocatieDTO> getAllLocatiiForAdmin() {
        return locatieRepository.findAll()
                .stream()
                .map(locatieMapper::toDTO)
                .collect(Collectors.toList());
    }

    // gaseste o locatie dupa id
    public LocatieDTO getLocatieById(Long id) {
        Locatie locatie = locatieRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Locația nu a fost găsită"));
        return locatieMapper.toDTO(locatie);
    }

    // adauga o locatie noua (doar pt admin)
    @Transactional
    public LocatieDTO createLocatie(LocatieDTO locatieDTO) {
        Locatie locatie = locatieMapper.toEntity(locatieDTO);
        Locatie saved = locatieRepository.save(locatie);
        log.info("Locatie noua creata: {}", saved.getId());
        return locatieMapper.toDTO(saved);
    }

    // actualizeaza o locatie (doar pt admin)
    @Transactional
    public LocatieDTO updateLocatie(Long id, LocatieDTO locatieDTO) {
        Locatie locatie = locatieRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Locația nu a fost găsită"));

        locatieMapper.updateEntityFromDTO(locatie, locatieDTO);
        Locatie updated = locatieRepository.save(locatie);
        log.info("Locatie actualizata: {}", id);
        return locatieMapper.toDTO(updated);
    }

    // sterge o locatie (soft delete, deci se marcheaza ca invalida)
    @Transactional
    public void deleteLocatie(Long id) {
        Locatie locatie = locatieRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Locația nu a fost găsită"));
        boolean statusNou = !Boolean.TRUE.equals(locatie.getActive());
        locatie.setActive(statusNou);

        locatieRepository.save(locatie);
        log.info("Locatie {} status schimbat in: {}", id, statusNou);
    }
}
