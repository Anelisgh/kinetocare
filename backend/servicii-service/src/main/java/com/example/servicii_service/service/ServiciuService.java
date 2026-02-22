package com.example.servicii_service.service;

import com.example.servicii_service.dto.ServiciuDTO;
import com.example.servicii_service.entity.Serviciu;
import com.example.servicii_service.mapper.ServiciuMapper;
import com.example.servicii_service.repository.ServiciuRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

import com.example.servicii_service.dto.ServiciuAdminDTO;
import com.example.servicii_service.entity.TipServiciu;
import com.example.servicii_service.repository.TipServiciuRepository;
import com.example.servicii_service.exception.ResourceNotFoundException;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ServiciuService {

    private final ServiciuRepository serviciuRepository;
    private final TipServiciuRepository tipServiciuRepository;
    private final ServiciuMapper serviciuMapper;

    // Metoda pentru a returna toate serviciile
    @Transactional(readOnly = true)
    public List<ServiciuDTO> getAllServicii() {
        return serviciuRepository.findAll()
                .stream()
                .filter(Serviciu::getActive) // Clientii vad doar active
                .map(serviciuMapper::toDto)
                .toList();
    }

    // ADMIN: Returneaza toate serviciile cu detalii complete (inclusiv inactive)
    @Transactional(readOnly = true)
    public List<ServiciuAdminDTO> getAllServiciiAdmin() {
        return serviciuRepository.findAll()
                .stream()
                .map(serviciuMapper::toAdminDto)
                .toList();
    }

    // returneaza un serviciu dupa id
    @Transactional(readOnly = true)
    public ServiciuDTO getDetaliiServiciu(Long id) {
        Serviciu serviciu = serviciuRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Serviciul nu există"));
        return serviciuMapper.toDto(serviciu);
    }

    // ADMIN: Create Service
    @Transactional
    public ServiciuAdminDTO createServiciu(ServiciuAdminDTO dto) {
        TipServiciu tip = tipServiciuRepository.findById(dto.tipServiciuId())
                .orElseThrow(() -> new ResourceNotFoundException("Tipul de serviciu nu există"));

        Serviciu entity = serviciuMapper.toEntity(dto, tip);
        Serviciu saved = serviciuRepository.save(entity);
        return serviciuMapper.toAdminDto(saved);
    }

    // ADMIN: Update Service
    @Transactional
    public ServiciuAdminDTO updateServiciu(Long id, ServiciuAdminDTO dto) {
        Serviciu entity = serviciuRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Serviciul nu există"));

        TipServiciu tip = null;
        if (dto.tipServiciuId() != null) {
            tip = tipServiciuRepository.findById(dto.tipServiciuId())
                    .orElseThrow(() -> new ResourceNotFoundException("Tipul de serviciu nu există"));
        }

        serviciuMapper.updateEntityFromDto(dto, entity, tip);
        Serviciu updated = serviciuRepository.save(entity);
        return serviciuMapper.toAdminDto(updated);
    }

    // ADMIN: Toggle Active
    @Transactional
    public ServiciuAdminDTO toggleActive(Long id) {
        Serviciu entity = serviciuRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Serviciul nu există"));
        
        entity.setActive(!entity.getActive());
        Serviciu updated = serviciuRepository.save(entity);
        return serviciuMapper.toAdminDto(updated);
    }

    // in principiu pentru returnarea Evalurii initiale dupa prima programare (determinaServiciulCorect in programari-service)
    @Transactional(readOnly = true)
    public ServiciuDTO cautaDupaNume(String nume) {
        // 1. Cautam intai dupa nume specific
        List<Serviciu> rezultateNume = serviciuRepository.findByNumeContainingIgnoreCase(nume);
        if (!rezultateNume.isEmpty()) {
             return serviciuMapper.toDto(rezultateNume.getFirst());
        }

        // 2. Fallback: Cautam dupa numele Tipului
        List<Serviciu> rezultateTip = serviciuRepository
                .findByTipServiciu_NumeContainingIgnoreCase(nume);

        if (rezultateTip.isEmpty()) {
            throw new ResourceNotFoundException("Nu s-au găsit servicii pentru numele dat: " + nume);
        }

        return serviciuMapper.toDto(rezultateTip.getFirst());
    }
}
