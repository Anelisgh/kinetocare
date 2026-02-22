package com.example.programari_service.service;

import com.example.programari_service.client.UserClient;
import com.example.programari_service.dto.*;
import com.example.programari_service.exception.*;
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

    // populeaza dropdown-ul din formularul de evaluare
    @Transactional(readOnly = true)
    public List<UserNumeDTO> getPacientiTerapeut(Long terapeutId) {
        // luam user IDs din tabela Programare
        List<Long> pacientIds = programareRepository.findPacientiIdByTerapeutId(terapeutId);
        List<UserNumeDTO> rezultat = new ArrayList<>();

        // pentru fiecare user ID, cautam detalii user si il adaugam in lista
        for (Long userId : pacientIds) {
            try {
                // apelam user-service pentru a obtine detalii user
                // folosim getUserById din UserClient
                UserDisplayCalendarDTO userDetails = userClient.getUserById(userId);

                // daca user-ul exista, il adaugam in lista
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

    // gaseste ultima programare dintre un pacient si un terapeut
    @Transactional(readOnly = true)
    public Programare getUltimaProgramare(Long pacientId, Long terapeutId) {
        // luam prima programare din lista, adica cea mai recenta
        List<Programare> lista = programareRepository.findLatestAppointments(
                pacientId,
                terapeutId,
                PageRequest.of(0, 1));
        // daca lista e goala, returnam null
        return lista.isEmpty() ? null : lista.get(0);
    }

    // fluxul complet de creare a unei evaluari
    @Transactional
    public Evaluare creeazaEvaluare(EvaluareRequestDTO request) {
        // folosim mapper pentru conversie
        Evaluare evaluare = evaluareMapper.toEntity(request);

        // determinam programarea si data
        if (request.programareId() == null) {
            // daca frontend-ul nu a trimis ID, il cautam noi
            Programare ultima = getUltimaProgramare(request.pacientId(), request.terapeutId());

            if (ultima != null) {
                evaluare.setProgramareId(ultima.getId());
                evaluare.setData(ultima.getData());
                // marcam programarea ca are evaluare
                ultima.setAreEvaluare(true);
                programareRepository.save(ultima);
            } else {
                // fallback: daca nu exista nicio programare, punem data curenta
                evaluare.setData(LocalDate.now());
            }
        } else {
            // daca frontend-ul a trimis explicit un ID
            Programare p = programareRepository.findById(request.programareId())
                    .orElseThrow(() -> new ResourceNotFoundException("Programarea specificată nu există"));

            evaluare.setProgramareId(p.getId());
            evaluare.setData(p.getData());
            p.setAreEvaluare(true);
            programareRepository.save(p);
        }

        Evaluare evaluareSalvata = evaluareRepository.save(evaluare);

        // dupa ce a fost salvata evaluarea, ne asiguram ca relatia e activa
        relatieService.asiguraRelatieActiva(request.pacientId(), request.terapeutId(), evaluareSalvata.getData());

        return evaluareSalvata;
    }

    // editare evaluare existenta
    @Transactional
    public Evaluare actualizeazaEvaluare(Long id, EvaluareRequestDTO request) {
        Evaluare evaluare = evaluareRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Evaluarea cu ID-ul " + id + " nu a fost găsită."));

        evaluare.setDiagnostic(request.diagnostic());
        evaluare.setSedinteRecomandate(request.sedinteRecomandate());
        evaluare.setServiciuRecomandatId(request.serviciuRecomandatId());
        evaluare.setObservatii(request.observatii());

        return evaluareRepository.save(evaluare);
    }
}