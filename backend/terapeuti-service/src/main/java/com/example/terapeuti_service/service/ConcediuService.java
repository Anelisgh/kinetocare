package com.example.terapeuti_service.service;

import com.example.terapeuti_service.dto.ConcediuDTO;
import com.example.terapeuti_service.dto.CreateConcediuDTO;
import com.example.terapeuti_service.entity.ConcediuTerapeut;
import com.example.terapeuti_service.entity.Terapeut;
import com.example.terapeuti_service.mapper.ConcediuMapper;
import com.example.terapeuti_service.repository.ConcediuRepository;
import com.example.terapeuti_service.repository.TerapeutRepository;
import lombok.RequiredArgsConstructor;
import com.example.terapeuti_service.exception.ResourceAlreadyExistsException;
import com.example.terapeuti_service.exception.ForbiddenOperationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.terapeuti_service.exception.ResourceNotFoundException;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ConcediuService {

    private final ConcediuRepository concediuRepository;
    private final TerapeutRepository terapeutRepository;
    private final ConcediuMapper concediuMapper;

    // gaseste concediile curente si viitoare ale terapeutului
    @Transactional(readOnly = true)
    public List<ConcediuDTO> getConcediiByKeycloakId(String keycloakId) {
        Terapeut terapeut = terapeutRepository.findByKeycloakId(keycloakId)
                .orElseThrow(() -> new ResourceNotFoundException("Terapeutul nu a fost găsit"));

        LocalDate today = LocalDate.now();
        return concediuRepository.findFutureAndCurrentConcedii(terapeut.getId(), today)
                .stream()
                .map(concediuMapper::toDTO)
                .collect(Collectors.toList());
    }

    // adauga un concediu nou
    @Transactional
    public ConcediuDTO addConcediu(String keycloakId, CreateConcediuDTO dto) {
        Terapeut terapeut = terapeutRepository.findByKeycloakId(keycloakId)
                .orElseThrow(() -> new ResourceNotFoundException("Terapeutul nu a fost găsit"));

        // Validari
        if (dto.dataInceput().isAfter(dto.dataSfarsit())) {
            throw new IllegalArgumentException("Data de început trebuie să fie înainte de data de sfârșit");
        }

        // Verifica suprapuneri
        List<ConcediuTerapeut> overlapping = concediuRepository.findOverlappingConcedii(
                terapeut.getId(), dto.dataInceput(), dto.dataSfarsit());

        if (!overlapping.isEmpty()) {
            throw new ResourceAlreadyExistsException("Concediul se suprapune cu o perioadă existentă");
        }

        ConcediuTerapeut entity = concediuMapper.toEntity(dto, terapeut.getId());
        ConcediuTerapeut saved = concediuRepository.save(entity);

        log.info("Added concediu for terapeut {}: {} - {}",
                keycloakId, dto.dataInceput(), dto.dataSfarsit());

        return concediuMapper.toDTO(saved);
    }

    // sterge un concediu
    @Transactional
    public void deleteConcediu(String keycloakId, Long concediuId) {
        Terapeut terapeut = terapeutRepository.findByKeycloakId(keycloakId)
                .orElseThrow(() -> new ResourceNotFoundException("Terapeutul nu a fost găsit"));
        ConcediuTerapeut concediu = concediuRepository.findById(concediuId)
                .orElseThrow(() -> new ResourceNotFoundException("Concediul nu a fost găsit"));

        // Verifica ca concediul apartine terapeutului
        if (!concediu.getTerapeutId().equals(terapeut.getId())) {
            throw new ForbiddenOperationException("Nu aveți permisiunea să ștergeți acest concediu");
        }

        concediuRepository.delete(concediu);

        log.info("Deleted concediu {} for terapeut {}", concediuId, keycloakId);
    }
}
