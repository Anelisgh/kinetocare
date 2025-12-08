package com.example.terapeuti_service.service;

import com.example.terapeuti_service.dto.CreateDisponibilitateDTO;
import com.example.terapeuti_service.dto.DisponibilitateDTO;
import com.example.terapeuti_service.entity.DisponibilitateTerapeut;
import com.example.terapeuti_service.entity.Locatie;
import com.example.terapeuti_service.entity.Terapeut;
import com.example.terapeuti_service.mapper.DisponibilitateMapper;
import com.example.terapeuti_service.repository.DisponibilitateRepository;
import com.example.terapeuti_service.repository.LocatieRepository;
import com.example.terapeuti_service.repository.TerapeutRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class DisponibilitateService {

    private final DisponibilitateRepository disponibilitateRepository;
    private final TerapeutRepository terapeutRepository;
    private final LocatieRepository locatieRepository;
    private final DisponibilitateMapper disponibilitateMapper;

    public List<DisponibilitateDTO> getDisponibilitatiByKeycloakId(String keycloakId) {
        Terapeut terapeut = terapeutRepository.findByKeycloakId(keycloakId)
                .orElseThrow(() -> new RuntimeException("Terapeutul nu a fost găsit"));

        List<DisponibilitateTerapeut> disponibilitati =
                disponibilitateRepository.findByTerapeutIdAndActiveTrue(terapeut.getId());

        // Obține locațiile folosind metoda helper
        return getDisponibilitateDTOS(disponibilitati, locatieRepository, disponibilitateMapper);
    }

    @Transactional
    public DisponibilitateDTO addDisponibilitate(String keycloakId, CreateDisponibilitateDTO dto) {
        Terapeut terapeut = terapeutRepository.findByKeycloakId(keycloakId)
                .orElseThrow(() -> new RuntimeException("Terapeutul nu a fost găsit"));

        // Validări
        if (dto.getZiSaptamana() < 1 || dto.getZiSaptamana() > 7) {
            throw new RuntimeException("Ziua săptămânii trebuie să fie între 1 (Luni) și 7 (Duminică)");
        }

        if (dto.getOraInceput().isAfter(dto.getOraSfarsit()) ||
                dto.getOraInceput().equals(dto.getOraSfarsit())) {
            throw new RuntimeException("Ora de început trebuie să fie înainte de ora de sfârșit");
        }

        // Verifică dacă locația există și este activă
        Locatie locatie = locatieRepository.findById(dto.getLocatieId())
                .orElseThrow(() -> new RuntimeException("Locația nu a fost găsită"));

        if (!locatie.getActive()) {
            throw new RuntimeException("Locația nu este activă");
        }
        // verifica daca exista suprapuneri
        List<DisponibilitateTerapeut> overlapping = disponibilitateRepository.findOverlappingDisponibilitate(
                terapeut.getId(),
                dto.getLocatieId(),
                dto.getZiSaptamana(),
                dto.getOraInceput(),
                dto.getOraSfarsit()
        );

        if (!overlapping.isEmpty()) {
            throw new RuntimeException("Intervalul se suprapune cu o disponibilitate existentă.");
        }

        DisponibilitateTerapeut entity = disponibilitateMapper.toEntity(dto, terapeut.getId());
        DisponibilitateTerapeut saved = disponibilitateRepository.save(entity);

        log.info("Added disponibilitate for terapeut {}: {} at location {}",
                keycloakId, dto.getZiSaptamana(), dto.getLocatieId());

        return disponibilitateMapper.toDTO(saved, locatie);
    }

    @Transactional
    public void deleteDisponibilitate(String keycloakId, Long disponibilitateId) {
        Terapeut terapeut = terapeutRepository.findByKeycloakId(keycloakId)
                .orElseThrow(() -> new RuntimeException("Terapeutul nu a fost găsit"));

        DisponibilitateTerapeut disponibilitate = disponibilitateRepository.findById(disponibilitateId)
                .orElseThrow(() -> new RuntimeException("Disponibilitatea nu a fost găsită"));

        // Verifică că disponibilitatea aparține terapeutului
        if (!disponibilitate.getTerapeutId().equals(terapeut.getId())) {
            throw new RuntimeException("Nu aveți permisiunea să ștergeți această disponibilitate");
        }

        // Soft delete
        disponibilitate.setActive(false);
        disponibilitateRepository.save(disponibilitate);

        log.info("Deleted disponibilitate {} for terapeut {}", disponibilitateId, keycloakId);
    }

    public static List<DisponibilitateDTO> getDisponibilitateDTOS(
            List<DisponibilitateTerapeut> disponibilitati,
            LocatieRepository locatieRepository,
            DisponibilitateMapper disponibilitateMapper) {

        List<Long> locatieIds = disponibilitati.stream()
                .map(DisponibilitateTerapeut::getLocatieId)
                .distinct()
                .collect(Collectors.toList());

        Map<Long, Locatie> locatiiMap = locatieRepository.findAllById(locatieIds)
                .stream()
                .collect(Collectors.toMap(Locatie::getId, loc -> loc));

        return disponibilitati.stream()
                .map(disp -> disponibilitateMapper.toDTO(disp, locatiiMap.get(disp.getLocatieId())))
                .collect(Collectors.toList());
    }
}