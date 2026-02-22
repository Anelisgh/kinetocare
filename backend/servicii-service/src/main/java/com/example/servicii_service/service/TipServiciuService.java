package com.example.servicii_service.service;

import com.example.servicii_service.dto.TipServiciuDTO;
import com.example.servicii_service.entity.TipServiciu;
import com.example.servicii_service.mapper.TipServiciuMapper;
import com.example.servicii_service.repository.TipServiciuRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.example.servicii_service.exception.ResourceNotFoundException;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TipServiciuService {

    private final TipServiciuRepository tipServiciuRepository;
    private final TipServiciuMapper tipServiciuMapper;

    @Transactional(readOnly = true)
    public List<TipServiciuDTO> getAllTips() {
        return tipServiciuRepository.findAll()
                .stream()
                .map(tipServiciuMapper::toDto)
                .toList();
    }

    @Transactional
    public TipServiciuDTO createTip(TipServiciuDTO dto) {
        TipServiciu entity = tipServiciuMapper.toEntity(dto);
        TipServiciu saved = tipServiciuRepository.save(entity);
        return tipServiciuMapper.toDto(saved);
    }

    @Transactional
    public TipServiciuDTO updateTip(Long id, TipServiciuDTO dto) {
        TipServiciu entity = tipServiciuRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tipul de serviciu nu există"));
        
        tipServiciuMapper.updateEntityFromDto(dto, entity);
        TipServiciu updated = tipServiciuRepository.save(entity);
        return tipServiciuMapper.toDto(updated);
    }

    @Transactional
    public TipServiciuDTO toggleActive(Long id) {
        TipServiciu entity = tipServiciuRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tipul de serviciu nu există"));

        entity.setActive(!entity.getActive());
        TipServiciu updated = tipServiciuRepository.save(entity);
        return tipServiciuMapper.toDto(updated);
    }
}
