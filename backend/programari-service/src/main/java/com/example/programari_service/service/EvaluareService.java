package com.example.programari_service.service;

import com.example.programari_service.client.UserClient;
import com.example.programari_service.dto.*;
import com.example.programari_service.entity.Evaluare;
import com.example.programari_service.entity.Programare;
import com.example.programari_service.mapper.EvaluareMapper;
import com.example.programari_service.mapper.PacientMapper;
import com.example.programari_service.repository.EvaluareRepository;
import com.example.programari_service.repository.ProgramareRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class EvaluareService {

    private final EvaluareRepository evaluareRepository;
    private final ProgramareRepository programareRepository;
    private final UserClient userClient;
    private final RelatieService relatieService;
    private final EvaluareMapper evaluareMapper;
    private final PacientMapper pacientMapper;

    // 1. POPULARE DROPDOWN
    public List<UserNumeDTO> getPacientiTerapeut(Long terapeutId) {
        // Luăm ID-urile unice (care sunt User IDs) din tabela Programare
        List<Long> pacientIds = programareRepository.findPacientiIdByTerapeutId(terapeutId);
        List<UserNumeDTO> rezultat = new ArrayList<>();

        for (Long userId : pacientIds) {
            try {
                // APEL DIRECT CĂTRE USER SERVICE
                // Folosim metoda getUserById pe care o ai deja in UserClient
                UserDisplayCalendarDTO userDetails = userClient.getUserById(userId);

                if (userDetails != null) {
                    UserNumeDTO pacientDTO = pacientMapper.toPacientNumeDTO(userDetails, userId);
                    if (pacientDTO != null) {
                        rezultat.add(pacientDTO);
                    }
                }
            } catch (Exception e) {
                log.warn("Nu s-au putut încărca datele pentru pacientul (User ID) {}: {}", userId, e.getMessage());
            }
        }
        return rezultat;
    }

    // 2. MAGIC LINK (Găsește programarea de legat)
    public Programare getUltimaProgramare(Long pacientId, Long terapeutId) {
        // Luăm prima programare din listă (cea mai recentă)
        List<Programare> lista = programareRepository.findLatestAppointments(
                pacientId,
                terapeutId,
                PageRequest.of(0, 1));
        return lista.isEmpty() ? null : lista.get(0);
    }

    // 3. SALVARE
    @Transactional
    public Evaluare creeazaEvaluare(EvaluareRequestDTO request) {
        // Folosim mapper-ul pentru conversia de bază
        Evaluare evaluare = evaluareMapper.toEntity(request);

        // A. Determinăm Programarea și Data
        if (request.getProgramareId() == null) {
            // Dacă frontend-ul nu a trimis ID, îl căutăm noi
            Programare ultima = getUltimaProgramare(request.getPacientId(), request.getTerapeutId());

            if (ultima != null) {
                evaluare.setProgramareId(ultima.getId());
                evaluare.setData(ultima.getData());

                // Marcăm programarea că are evaluare
                ultima.setAreEvaluare(true);
                programareRepository.save(ultima);
            } else {
                // Fallback: Dacă nu există nicio ședință (ciudat, dar posibil), punem data
                // curentă
                evaluare.setData(LocalDate.now());
            }
        } else {
            // Dacă frontend-ul a trimis explicit un ID
            Programare p = programareRepository.findById(request.getProgramareId())
                    .orElseThrow(() -> new RuntimeException("Programarea specificată nu există"));

            evaluare.setProgramareId(p.getId());
            evaluare.setData(p.getData());
            p.setAreEvaluare(true);
            programareRepository.save(p);
        }

        Evaluare evaluareSalvata = evaluareRepository.save(evaluare);

        // dupa ce a fost salvata evaluarea, asiguram relatia activa
        relatieService.asiguraRelatieActiva(request.getPacientId(), request.getTerapeutId(), evaluareSalvata.getData());

        return evaluareSalvata;
    }
}