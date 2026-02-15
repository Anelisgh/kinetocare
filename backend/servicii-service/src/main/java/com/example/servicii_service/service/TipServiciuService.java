package com.example.servicii_service.service;

import com.example.servicii_service.dto.TipServiciuDTO;
import com.example.servicii_service.entity.TipServiciu;
import com.example.servicii_service.mapper.TipServiciuMapper;
import com.example.servicii_service.repository.TipServiciuRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TipServiciuService {

    private final TipServiciuRepository tipServiciuRepository;
    private final TipServiciuMapper tipServiciuMapper;

    public List<TipServiciuDTO> getAllTips() {
        return tipServiciuRepository.findAll()
                .stream()
                .map(tipServiciuMapper::toDto)
                .toList();
    }

    public TipServiciuDTO createTip(TipServiciuDTO dto) {
        TipServiciu entity = tipServiciuMapper.toEntity(dto);
        TipServiciu saved = tipServiciuRepository.save(entity);
        return tipServiciuMapper.toDto(saved);
    }

    public TipServiciuDTO updateTip(Long id, TipServiciuDTO dto) {
        TipServiciu entity = tipServiciuRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tipul de serviciu nu există"));
        
        tipServiciuMapper.updateEntityFromDto(dto, entity);
        TipServiciu updated = tipServiciuRepository.save(entity);
        return tipServiciuMapper.toDto(updated);
    }

    public TipServiciuDTO toggleActive(Long id) {
        TipServiciu entity = tipServiciuRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tipul de serviciu nu există"));

        entity.setActive(!entity.getActive());
        TipServiciu updated = tipServiciuRepository.save(entity);
        return tipServiciuMapper.toDto(updated);
    }
}
