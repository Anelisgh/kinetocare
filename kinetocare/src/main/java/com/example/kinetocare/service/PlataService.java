package com.example.kinetocare.service;

import com.example.kinetocare.domain.Pacient;
import com.example.kinetocare.domain.Plata;
import com.example.kinetocare.domain.Programare;
import com.example.kinetocare.domain.StarePlata;
import com.example.kinetocare.mapper.PlataMapper;
import com.example.kinetocare.repository.PlataRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import org.springframework.data.domain.Pageable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class PlataService {
    private final PlataRepository plataRepository;
    private final PlataMapper plataMapper;
    private final PacientService pacientService;

// PAGINA PENTRU FACTURI
//‧˚₊꒷꒦︶︶︶︶︶꒷꒦︶︶︶︶︶꒦꒷‧₊˚⊹
    public Map<String, Object> getPlatiPentruPacient(String email, Pageable pageable) {
        Pacient pacient = pacientService.getPacientByEmail(email);

        Page<Plata> platiPage = plataRepository.findAllByPacient(pacient, pageable);

        BigDecimal totalPlatit = plataRepository.sumByPacientAndStarePlata(pacient, StarePlata.ACHITATA);
        BigDecimal dePlatit = plataRepository.sumByPacientAndStarePlata(pacient, StarePlata.IN_ASTEPTARE);

        return Map.of(
                "plati", platiPage.map(plataMapper::toDto),
                "totalPlatit", totalPlatit != null ? totalPlatit : BigDecimal.ZERO,
                "dePlatit", dePlatit != null ? dePlatit : BigDecimal.ZERO,
                "currentPage", platiPage.getNumber(),
                "totalPages", platiPage.getTotalPages(),
                "pageSize", platiPage.getSize()
        );
    }

// SE CREEAZA PLATA AUTOMAT
//‧˚₊꒷꒦︶︶︶︶︶꒷꒦︶︶︶︶︶꒦꒷‧₊˚⊹
    public void creazaPlata(Programare programare) {
        if (programare.getServiciu() == null || plataRepository.existsByProgramare(programare)) {
            return;
        }
        Plata plata = Plata.builder()
                .data(LocalDate.now())
                .suma(programare.getServiciu().getPret())
                .starePlata(StarePlata.IN_ASTEPTARE)
                .programare(programare)
                .pacient(programare.getPacient())
                .build();
        plataRepository.save(plata);
    }
}
