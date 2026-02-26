package com.example.terapeuti_service.service;

import com.example.terapeuti_service.dto.*;
import com.example.terapeuti_service.entity.*;
import com.example.terapeuti_service.exception.ResourceAlreadyExistsException;
import com.example.terapeuti_service.exception.ResourceNotFoundException;
import com.example.terapeuti_service.mapper.*;
import com.example.terapeuti_service.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class TerapeutService {
    private final TerapeutRepository terapeutRepository;
    private final DisponibilitateRepository disponibilitateRepository;
    private final ConcediuRepository concediuRepository;
    private final LocatieRepository locatieRepository;
    private final TerapeutMapper terapeutMapper;
    private final DisponibilitateMapper disponibilitateMapper;
    private final ConcediuMapper concediuMapper;
    private final LocatieMapper locatieMapper;

    @Transactional(readOnly = true)
    public TerapeutDTO getTerapeutByKeycloakId(String keycloakId) {
        Terapeut terapeut = findByKIdOrThrow(keycloakId);
        TerapeutDTO dto = terapeutMapper.toDTO(terapeut);
        dto = populateTerapeutData(terapeut.getId(), dto);
        return dto;
    }

    @Transactional(readOnly = true)
    public String getKeycloakIdById(Long terapeutId) {
        return terapeutRepository.findById(terapeutId)
                .map(Terapeut::getKeycloakId)
                .orElse(null);
    }

    @Transactional(readOnly = true)
    public Map<Long, String> getBatchKeycloakIds(List<Long> ids) {
        return terapeutRepository.findAllById(ids).stream()
                .collect(Collectors.toMap(Terapeut::getId, Terapeut::getKeycloakId));
    }

    @Transactional
    public TerapeutDTO createTerapeut(String keycloakId) {
        if (terapeutRepository.existsByKeycloakId(keycloakId)) {
            throw new ResourceAlreadyExistsException("Terapeutul există deja");
        }
        Terapeut terapeut = terapeutMapper.toNewEntity(keycloakId);

        Terapeut saved = terapeutRepository.save(terapeut);
        log.info("Created terapeut profile for keycloakId: {}", keycloakId);

        TerapeutDTO dto = terapeutMapper.toDTO(saved);
        return new TerapeutDTO(
            dto.id(), dto.keycloakId(), dto.specializare(), dto.pozaProfil(), 
            dto.active(), dto.createdAt(), dto.updatedAt(),
            dto.disponibilitati(), dto.concedii(), dto.locatiiDisponibile(), true
        );
    }

    @Transactional
    public void initializeEmptyTerapeut(String keycloakId) {
        if (terapeutRepository.existsByKeycloakId(keycloakId)) {
            log.debug("Terapeut profile already exists for: {}", keycloakId);
            return;
        }

        Terapeut terapeut = terapeutMapper.toNewEntity(keycloakId);

        terapeutRepository.save(terapeut);
        log.info("Empty terapeut profile initialized for: {}", keycloakId);
    }

    @Transactional
    public TerapeutDTO updateTerapeut(String keycloakId, UpdateTerapeutDTO updateDTO) {
        Terapeut terapeut = findByKIdOrThrow(keycloakId);
        terapeutMapper.updateEntity(terapeut, updateDTO);

        Terapeut updated = terapeutRepository.save(terapeut);
        log.info("Updated terapeut with keycloakId: {}", keycloakId);

        TerapeutDTO dto = terapeutMapper.toDTO(updated);
        dto = populateTerapeutData(updated.getId(), dto);

        return dto;
    }

    @Transactional(readOnly = true)
    public TerapeutDetaliDTO getTerapeutDetails(String keycloakId) {
        Terapeut terapeut = findByKIdOrThrow(keycloakId);

        List<DisponibilitateDTO> disponibilitati = getDisponibilitatiForTerapeut(terapeut.getId());
        List<LocatieDisponibilaDTO> locatiiUnice = extrageLocatiiUnice(disponibilitati);

        return terapeutMapper.toDetaliDTO(terapeut, disponibilitati, locatiiUnice);
    }

    @Transactional(readOnly = true)
    public List<TerapeutSearchDTO> searchTerapeuti(Specializare specializare, String judet, String oras, Long locatieId) {
        List<Terapeut> terapeuti = terapeutRepository.findBySpecializareAndActiveTrue(specializare);
        if (terapeuti.isEmpty())
            return List.of();

        List<Long> terapeutIds = terapeuti.stream().map(Terapeut::getId).collect(Collectors.toList());

        List<DisponibilitateTerapeut> disponibilitati = disponibilitateRepository
                .findByTerapeutIdInAndActiveTrue(terapeutIds);

        Map<Long, Locatie> locatiiMap = getLocatiiMapFromDisponibilitati(disponibilitati);

        Map<Long, List<DisponibilitateTerapeut>> dispByTerapeut = disponibilitati.stream()
                .collect(Collectors.groupingBy(DisponibilitateTerapeut::getTerapeutId));

        return terapeuti.stream()
                .filter(t -> isTerapeutValidForFilter(t, dispByTerapeut, locatiiMap, judet, oras, locatieId))
                .map(t -> {
                    List<LocatieDisponibilaDTO> locatiiDisp = dispByTerapeut.get(t.getId()).stream()
                            .map(DisponibilitateTerapeut::getLocatieId)
                            .distinct()
                            .map(locatiiMap::get)
                            .filter(java.util.Objects::nonNull)
                            .map(locatieMapper::toDisponibilaDTO)
                            .collect(Collectors.toList());
                    return terapeutMapper.toSearchDTO(t, locatiiDisp);
                })
                .collect(Collectors.toList());
    }

    private Terapeut findByKIdOrThrow(String keycloakId) {
        return terapeutRepository.findByKeycloakId(keycloakId)
                .orElseThrow(() -> new ResourceNotFoundException("Terapeutul nu a fost găsit"));
    }

    private TerapeutDTO populateTerapeutData(Long terapeutId, TerapeutDTO baseDto) {
        List<DisponibilitateDTO> disponibilitati = getDisponibilitatiForTerapeut(terapeutId);
        List<LocatieDisponibilaDTO> locatiiList = extrageLocatiiUnice(disponibilitati);
        List<ConcediuDTO> concediiList = getConcediiFutureForTerapeut(terapeutId);
        
        return new TerapeutDTO(
            baseDto.id(), baseDto.keycloakId(), baseDto.specializare(), baseDto.pozaProfil(), 
            baseDto.active(), baseDto.createdAt(), baseDto.updatedAt(), 
            disponibilitati, concediiList, locatiiList, baseDto.profileIncomplete()
        );
    }

    private List<DisponibilitateDTO> getDisponibilitatiForTerapeut(Long terapeutId) {
        List<DisponibilitateTerapeut> disponibilitati = disponibilitateRepository
                .findByTerapeutIdAndActiveTrue(terapeutId);
        Map<Long, Locatie> locatiiMap = getLocatiiMapFromDisponibilitati(disponibilitati);

        return disponibilitati.stream()
                .map(disp -> disponibilitateMapper.toDTO(disp, locatiiMap.get(disp.getLocatieId())))
                .collect(Collectors.toList());
    }

    private Map<Long, Locatie> getLocatiiMapFromDisponibilitati(List<DisponibilitateTerapeut> disponibilitati) {
        List<Long> locatieIds = disponibilitati.stream()
                .map(DisponibilitateTerapeut::getLocatieId)
                .distinct()
                .collect(Collectors.toList());
        return locatieRepository.findAllById(locatieIds).stream()
                .collect(Collectors.toMap(Locatie::getId, loc -> loc));
    }

    private List<LocatieDisponibilaDTO> extrageLocatiiUnice(List<DisponibilitateDTO> disponibilitati) {
        return disponibilitati.stream()
                .map(locatieMapper::fromDisponibilitateDTO)
                .distinct()
                .collect(Collectors.toList());
    }

    private List<ConcediuDTO> getConcediiFutureForTerapeut(Long terapeutId) {
        return concediuRepository.findFutureAndCurrentConcedii(terapeutId, LocalDate.now())
                .stream()
                .map(concediuMapper::toDTO)
                .collect(Collectors.toList());
    }

    private boolean isTerapeutValidForFilter(Terapeut t,
            Map<Long, List<DisponibilitateTerapeut>> dispByTerapeut,
            Map<Long, Locatie> locatiiMap,
            String judet, String oras, Long locatieId) {
        List<DisponibilitateTerapeut> terapeutDisp = dispByTerapeut.get(t.getId());
        if (terapeutDisp == null || terapeutDisp.isEmpty())
            return false;

        return terapeutDisp.stream().anyMatch(disp -> {
            Locatie loc = locatiiMap.get(disp.getLocatieId());
            if (loc == null)
                return false;

            boolean match = true;
            if (judet != null && !judet.isEmpty())
                match &= loc.getJudet().equalsIgnoreCase(judet);
            if (oras != null && !oras.isEmpty())
                match &= loc.getOras().equalsIgnoreCase(oras);
            if (locatieId != null)
                match &= loc.getId().equals(locatieId);
            return match;
        });
    }

    @Transactional
    public void setActive(String keycloakId, boolean active) {
        Terapeut terapeut = findByKIdOrThrow(keycloakId);
        terapeut.setActive(active);
        terapeutRepository.save(terapeut);
        log.info("Terapeut {} (keycloakId={}) setat active={}", terapeut.getId(), keycloakId, active);
    }
}