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
import com.example.terapeuti_service.exception.ResourceAlreadyExistsException;
import com.example.terapeuti_service.exception.ForbiddenOperationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.terapeuti_service.exception.ResourceNotFoundException;

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

    // gaseste disponibilitatile active ale terapeutului si locatiile
    @Transactional(readOnly = true)
    public List<DisponibilitateDTO> getDisponibilitatiByKeycloakId(String keycloakId) {
        Terapeut terapeut = terapeutRepository.findByKeycloakId(keycloakId)
                .orElseThrow(() -> new ResourceNotFoundException("Terapeutul nu a fost găsit"));

        List<DisponibilitateTerapeut> disponibilitati =
                disponibilitateRepository.findByTerapeutIdAndActiveTrue(terapeut.getId());

        // obtine locatiile folosind metoda helper
        return getDisponibilitateDTOS(disponibilitati);
    }

    // adauga o disponibilitate
    @Transactional
    public DisponibilitateDTO addDisponibilitate(String keycloakId, CreateDisponibilitateDTO dto) {
        Terapeut terapeut = terapeutRepository.findByKeycloakId(keycloakId)
                .orElseThrow(() -> new ResourceNotFoundException("Terapeutul nu a fost găsit"));

        // Validari
        if (dto.ziSaptamana() < 1 || dto.ziSaptamana() > 7) {
            throw new IllegalArgumentException("Ziua săptămânii trebuie să fie între 1 (Luni) și 7 (Duminică)");
        }

        if (dto.oraInceput().isAfter(dto.oraSfarsit()) ||
                dto.oraInceput().equals(dto.oraSfarsit())) {
            throw new IllegalArgumentException("Ora de început trebuie să fie înainte de ora de sfârșit");
        }

        // verifica daca locatia exista si e activa
        Locatie locatie = locatieRepository.findById(dto.locatieId())
                .orElseThrow(() -> new ResourceNotFoundException("Locația nu a fost găsită"));

        if (!locatie.getActive()) {
            throw new ForbiddenOperationException("Locația nu este activă");
        }
        // verifica daca exista suprapuneri
        List<DisponibilitateTerapeut> overlapping = disponibilitateRepository.findOverlappingDisponibilitate(
                terapeut.getId(),
                dto.locatieId(),
                dto.ziSaptamana(),
                dto.oraInceput(),
                dto.oraSfarsit()
        );

        if (!overlapping.isEmpty()) {
            throw new ResourceAlreadyExistsException("Intervalul se suprapune cu o disponibilitate existentă.");
        }

        DisponibilitateTerapeut entity = disponibilitateMapper.toEntity(dto, terapeut.getId());
        DisponibilitateTerapeut saved = disponibilitateRepository.save(entity);

        log.info("Added disponibilitate for terapeut {}: {} at location {}",
                keycloakId, dto.ziSaptamana(), dto.locatieId());

        return disponibilitateMapper.toDTO(saved, locatie);
    }

    // sterge o disponibilitate (soft delete)
    @Transactional
    public void deleteDisponibilitate(String keycloakId, Long disponibilitateId) {
        Terapeut terapeut = terapeutRepository.findByKeycloakId(keycloakId)
                .orElseThrow(() -> new ResourceNotFoundException("Terapeutul nu a fost găsit"));

        DisponibilitateTerapeut disponibilitate = disponibilitateRepository.findById(disponibilitateId)
                .orElseThrow(() -> new ResourceNotFoundException("Disponibilitatea nu a fost găsită"));

        // Verifica ca disponibilitatea apartine terapeutului
        if (!disponibilitate.getTerapeutId().equals(terapeut.getId())) {
            throw new ForbiddenOperationException("Nu aveți permisiunea să ștergeți această disponibilitate");
        }

        // soft delete
        disponibilitate.setActive(false);
        disponibilitateRepository.save(disponibilitate);

        log.info("Deleted disponibilitate {} for terapeut {}", disponibilitateId, keycloakId);
    }

    // metoda helper pentru a obtine disponibilitatile si locatiile
    private List<DisponibilitateDTO> getDisponibilitateDTOS(List<DisponibilitateTerapeut> disponibilitati) {
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