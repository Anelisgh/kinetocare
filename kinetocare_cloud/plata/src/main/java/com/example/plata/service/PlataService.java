package com.example.plata.service;

import com.example.common.dto.*;
import com.example.common.enums.StarePlata;
import com.example.plata.domain.Plata;
import com.example.plata.feign.PacientFeignClient;
import com.example.plata.feign.ProgramareFeignClient;
import com.example.plata.feign.ServiciuFeignClient;
import com.example.plata.mapper.PlataMapper;
import com.example.plata.repository.PlataRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PlataService {
    private final PlataRepository plataRepository;
    private final PlataMapper plataMapper;
    private final ProgramareFeignClient programareClient;
    private final PacientFeignClient pacientClient;
    private final ServiciuFeignClient serviciuClient;

    public List<PlataDTO> getPlatiPentruPacient(Long pacientId) {
        return plataRepository.findByPacientId(pacientId)
                .stream()
                .map(plataMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public PlataDTO creazaPlata(Long programareId) {
        ProgramareDetaliiDTO programare = programareClient.getProgramareById(programareId);
        if (programare == null || plataRepository.existsByProgramareId(programareId)) {
            throw new EntityNotFoundException("Programare invalidă sau plată existentă");
        };
        ServiciuDTO serviciu = serviciuClient.getServiciuById(programare.getServiciuId());
        BigDecimal suma = serviciu != null ? serviciu.getPret() : BigDecimal.ZERO;
        Plata plata = Plata.builder()
                .data(LocalDate.now())
                .suma(suma)
                .starePlata(StarePlata.IN_ASTEPTARE)
                .programareId(programareId)
                .pacientId(programare.getPacientId())
                .build();
        Plata saved = plataRepository.save(plata);
        return plataMapper.toDto(saved);
    }

    @Transactional
    public PlataDTO actualizeazaStarePlata(Long id, UpdateStarePlataDTO dto) {
        Plata plata = plataRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Plată negăsită"));
        plata.setStarePlata(dto.getStarePlata());
        Plata updated = plataRepository.save(plata);
        return plataMapper.toDto(updated);
    }
}
