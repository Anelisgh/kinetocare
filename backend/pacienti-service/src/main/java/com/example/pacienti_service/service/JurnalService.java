package com.example.pacienti_service.service;

import com.example.pacienti_service.client.ProgramariClient;
import com.example.pacienti_service.dto.JurnalIstoricDTO;
import com.example.pacienti_service.dto.JurnalRequestDTO;
import com.example.pacienti_service.dto.ProgramareJurnalDTO;
import com.example.pacienti_service.entity.JurnalPacient;
import com.example.pacienti_service.mapper.JurnalMapper;
import com.example.pacienti_service.repository.JurnalRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class JurnalService {
    private final JurnalRepository jurnalRepository;
    private final ProgramariClient programariClient;
    private final JurnalMapper jurnalMapper;

    @Transactional
    public void adaugaJurnal(Long pacientId, JurnalRequestDTO request) {
        // 1. Obținem data programării din programari-service
        ProgramareJurnalDTO detaliiProgramare = programariClient.getDetaliiProgramare(request.getProgramareId());

        // 2. Salvăm jurnalul cu data reală a ședinței
        JurnalPacient jurnal = JurnalPacient.builder()
                .pacientId(pacientId)
                .programareId(request.getProgramareId())
                .nivelDurere(request.getNivelDurere())
                .dificultateExercitii(request.getDificultateExercitii())
                .nivelOboseala(request.getNivelOboseala())
                .comentarii(request.getComentarii())
                .data(detaliiProgramare.getData()) // Data reală a programării
                .build();

        jurnalRepository.save(jurnal);
        log.info("Jurnal adăugat pentru pacientul {} - programare {}", pacientId, request.getProgramareId());

        // 3. Marcăm programarea că are jurnal completat
        programariClient.marcheazaJurnal(request.getProgramareId());
    }

    public List<JurnalIstoricDTO> getIstoric(Long pacientId) {
        // 1. Luăm jurnalele din DB
        List<JurnalPacient> jurnale = jurnalRepository.findByPacientIdOrderByDataDesc(pacientId);

        // 2. Pentru fiecare jurnal, îmbogățim cu detaliile programării
        return jurnale.stream().map(jurnal -> {
            ProgramareJurnalDTO detalii = null;
            try {
                detalii = programariClient.getDetaliiProgramare(jurnal.getProgramareId());
            } catch (Exception e) {
                log.warn("Nu s-au putut obține detaliile pentru programarea {}: {}",
                        jurnal.getProgramareId(), e.getMessage());
            }
            return jurnalMapper.toIstoricDTO(jurnal, detalii);
        }).toList();
    }
}
