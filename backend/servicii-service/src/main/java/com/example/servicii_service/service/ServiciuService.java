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

@Service
@RequiredArgsConstructor
public class ServiciuService {

    private final ServiciuRepository serviciuRepository;
    private final TipServiciuRepository tipServiciuRepository;
    private final ServiciuMapper serviciuMapper;

    // Metoda pentru a returna toate serviciile
    public List<ServiciuDTO> getAllServicii() {
        return serviciuRepository.findAll()
                .stream()
                .filter(Serviciu::getActive) // Clientii vad doar active
                .map(serviciuMapper::toDto)
                .toList();
    }

    // ADMIN: Returneaza toate serviciile cu detalii complete (inclusiv inactive)
    public List<ServiciuAdminDTO> getAllServiciiAdmin() {
        return serviciuRepository.findAll()
                .stream()
                .map(serviciuMapper::toAdminDto)
                .toList();
    }

    // returneaza un serviciu dupa id
    public ServiciuDTO getDetaliiServiciu(Long id) {
        Serviciu serviciu = serviciuRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Serviciul nu există"));
        return serviciuMapper.toDto(serviciu);
    }

    // ADMIN: Create Service
    public ServiciuAdminDTO createServiciu(ServiciuAdminDTO dto) {
        TipServiciu tip = tipServiciuRepository.findById(dto.getTipServiciuId())
                .orElseThrow(() -> new RuntimeException("Tipul de serviciu nu există"));

        Serviciu entity = serviciuMapper.toEntity(dto, tip);
        Serviciu saved = serviciuRepository.save(entity);
        return serviciuMapper.toAdminDto(saved);
    }

    // ADMIN: Update Service
    public ServiciuAdminDTO updateServiciu(Long id, ServiciuAdminDTO dto) {
        Serviciu entity = serviciuRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Serviciul nu există"));

        TipServiciu tip = null;
        if (dto.getTipServiciuId() != null) {
            tip = tipServiciuRepository.findById(dto.getTipServiciuId())
                    .orElseThrow(() -> new RuntimeException("Tipul de serviciu nu există"));
        }

        serviciuMapper.updateEntityFromDto(dto, entity, tip);
        Serviciu updated = serviciuRepository.save(entity);
        return serviciuMapper.toAdminDto(updated);
    }

    // ADMIN: Toggle Active
    public ServiciuAdminDTO toggleActive(Long id) {
        Serviciu entity = serviciuRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Serviciul nu există"));
        
        entity.setActive(!entity.getActive());
        Serviciu updated = serviciuRepository.save(entity);
        return serviciuMapper.toAdminDto(updated);
    }

    // in principiu pentru returnarea Evalurii initiale dupa prima programare (determinaServiciulCorect in programari-service)
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
            throw new RuntimeException("Nu s-au găsit servicii pentru numele dat: " + nume);
        }

        return serviciuMapper.toDto(rezultateTip.getFirst());
    }
}
