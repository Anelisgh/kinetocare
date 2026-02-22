package com.example.pacienti_service.service;

import com.example.pacienti_service.client.ProgramariClient;
import com.example.pacienti_service.dto.JurnalIstoricDTO;
import com.example.pacienti_service.dto.JurnalRequestDTO;
import com.example.pacienti_service.dto.ProgramareJurnalDTO;
import com.example.pacienti_service.entity.JurnalPacient;
import com.example.pacienti_service.exception.ExternalServiceException;
import com.example.pacienti_service.exception.ForbiddenOperationException;
import com.example.pacienti_service.exception.ResourceNotFoundException;
import com.example.pacienti_service.mapper.JurnalMapper;
import com.example.pacienti_service.repository.JurnalRepository;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Collections;
import java.util.stream.Collectors;

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
        ProgramareJurnalDTO detaliiProgramare;
        try {
            // obtinem data programarii din programari-service
            detaliiProgramare = programariClient.getDetaliiProgramare(request.programareId());
        } catch (Exception e) {
            throw new ExternalServiceException("Eroare la obținerea detaliilor programării " + request.programareId(), e);
        }

        // salvam jurnalul cu data reala a sedintei
        JurnalPacient jurnal = JurnalPacient.builder()
                .pacientId(pacientId)
                .programareId(request.programareId())
                .nivelDurere(request.nivelDurere())
                .dificultateExercitii(request.dificultateExercitii())
                .nivelOboseala(request.nivelOboseala())
                .comentarii(request.comentarii())
                .data(detaliiProgramare.data()) // obtinem data reala, nu lasam pacientul sa o introduca
                .build();

        jurnalRepository.save(jurnal);
        log.info("Jurnal adăugat pentru pacientul {} - programare {}", pacientId, request.programareId());

        try {
            // marcam faptul ca programarea are jurnal completat
            programariClient.marcheazaJurnal(request.programareId());
        } catch (Exception e) {
            log.warn("Nu s-a putut marca jurnalul completat pentru programarea {}: {}", request.programareId(), e.getMessage());
            // aici nu aruncam exceptie neaparat, permitând salvarea jurnalului local.
        }

        // notificam terapeutul ca jurnalul a fost completat
        if (detaliiProgramare.terapeutId() != null) {
            notificarePublisher.jurnalCompletat(detaliiProgramare.terapeutId(), pacientId, request.programareId());
        }
    }

    // editare jurnal existent
    @Transactional
    public void actualizeazaJurnal(Long pacientId, Long jurnalId, JurnalRequestDTO request) {
        JurnalPacient jurnal = jurnalRepository.findById(jurnalId)
                .orElseThrow(() -> new ResourceNotFoundException("Jurnalul cu ID-ul " + jurnalId + " nu a fost găsit."));

        // verificam ca jurnalul apartine pacientului
        if (!jurnal.getPacientId().equals(pacientId)) {
            throw new ForbiddenOperationException("Jurnalul nu aparține acestui pacient.");
        }

        jurnal.setNivelDurere(request.nivelDurere());
        jurnal.setDificultateExercitii(request.dificultateExercitii());
        jurnal.setNivelOboseala(request.nivelOboseala());
        jurnal.setComentarii(request.comentarii());

        jurnalRepository.save(jurnal);
        log.info("Jurnal actualizat pentru pacientul {} - jurnal {}", pacientId, jurnalId);
    }

    // returneaza istoric jurnale
    @Transactional(readOnly = true)
    public List<JurnalIstoricDTO> getIstoric(Long pacientId) {
        // luam jurnalele din db
        List<JurnalPacient> jurnale = jurnalRepository.findByPacientIdOrderByDataDesc(pacientId);

        if (jurnale.isEmpty()) {
            return Collections.emptyList();
        }

        // extragem lista de ID-uri de programari pentru a face un singur call
        List<Long> programareIds = jurnale.stream()
                .map(JurnalPacient::getProgramareId)
                .distinct()
                .toList();

        Map<Long, ProgramareJurnalDTO> programariMapTemp;
        try {
            // Un singur call (batch)
            List<ProgramareJurnalDTO> batchResults = programariClient.getProgramariBatch(programareIds);
            programariMapTemp = batchResults.stream()
                    .collect(Collectors.toMap(ProgramareJurnalDTO::id, p -> p));
        } catch (Exception e) {
            log.warn("Eroare inter-service (Feign batch) la recuperarea detaliilor programărilor: {}", e.getMessage());
            // fallback gracefully - returnam jurnale fara detalii agregate in caz de esec parțial nedorit
            programariMapTemp = Collections.emptyMap();
        }
        final Map<Long, ProgramareJurnalDTO> programariMap = programariMapTemp;

        // pentru fiecare jurnal, imbogatim cu detaliile mapate
        return jurnale.stream()
                .map(jurnal -> {
                    ProgramareJurnalDTO detalii = programariMap.get(jurnal.getProgramareId());
                    return jurnalMapper.toIstoricDTO(jurnal, detalii);
                }).toList();
    }
}