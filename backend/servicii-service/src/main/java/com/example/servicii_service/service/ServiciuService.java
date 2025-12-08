package com.example.servicii_service.service;

import com.example.servicii_service.dto.ServiciuDTO;
import com.example.servicii_service.entity.Serviciu;
import com.example.servicii_service.mapper.ServiciuMapper;
import com.example.servicii_service.repository.ServiciuRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ServiciuService {

    private final ServiciuRepository serviciuRepository;
    private final ServiciuMapper serviciuMapper;

    public ServiciuDTO getDetaliiServiciu(Long id) {
        Serviciu serviciu = serviciuRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Serviciul nu există"));
        return serviciuMapper.toDto(serviciu);
    }
// pentru programarile facute de pacienti (in principiu pentru a trece prima programare dintre un p si un t ca evaluare initiala)
    public ServiciuDTO cautaDupaNume(String nume) {
        List<Serviciu> rezultate = serviciuRepository
                .findByTipServiciu_NumeContainingIgnoreCase(nume);

        if (rezultate.isEmpty()) {
            throw new RuntimeException("Nu s-au găsit servicii pentru numele dat");
        }

        Serviciu serviciu = rezultate.getFirst();
        return serviciuMapper.toDto(serviciu);
    }
}
