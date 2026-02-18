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
    private final NotificarePublisher notificarePublisher;

    // adaugarea jurnalului
    @Transactional
    public void adaugaJurnal(Long pacientId, JurnalRequestDTO request) {
        // obtinem data programarii din programari-service
        ProgramareJurnalDTO detaliiProgramare = programariClient.getDetaliiProgramare(request.getProgramareId());

        // salvam jurnalul cu data reala a sedintei
        JurnalPacient jurnal = JurnalPacient.builder()
                .pacientId(pacientId)
                .programareId(request.getProgramareId())
                .nivelDurere(request.getNivelDurere())
                .dificultateExercitii(request.getDificultateExercitii())
                .nivelOboseala(request.getNivelOboseala())
                .comentarii(request.getComentarii())
                .data(detaliiProgramare.getData()) // obtinem data reala, nu lasam pacientul sa o introduca
                .build();

        jurnalRepository.save(jurnal);
        log.info("Jurnal adăugat pentru pacientul {} - programare {}", pacientId, request.getProgramareId());

        // marcam faptul ca programarea are jurnal completat
        programariClient.marcheazaJurnal(request.getProgramareId());

        // notificam terapeutul ca jurnalul a fost completat
        if (detaliiProgramare.getTerapeutId() != null) {
            notificarePublisher.jurnalCompletat(detaliiProgramare.getTerapeutId(), pacientId, request.getProgramareId());
        }
    }

    // returneaza istoric jurnale
    public List<JurnalIstoricDTO> getIstoric(Long pacientId) {
        // luam jurnalele din db
        List<JurnalPacient> jurnale = jurnalRepository.findByPacientIdOrderByDataDesc(pacientId);

        // pentru fiecare jurnal, imbogatim cu detaliile programarii
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