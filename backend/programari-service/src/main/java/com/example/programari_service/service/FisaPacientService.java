package com.example.programari_service.service;

import com.example.programari_service.client.PacientiClient;
import com.example.programari_service.client.ServiciiClient;
import com.example.programari_service.client.UserClient;
import com.example.programari_service.dto.*;
import com.example.programari_service.entity.Evaluare;
import com.example.programari_service.entity.RelatiePacientTerapeut;
import com.example.programari_service.repository.EvaluareRepository;
import com.example.programari_service.repository.RelatieRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class FisaPacientService {

    private final RelatieRepository relatieRepository;
    private final EvaluareRepository evaluareRepository;
    private final UserClient userClient;
    private final PacientiClient pacientiClient;
    private final ServiciiClient serviciiClient;
    private final ProgramareService programareService;
    private final EvolutieService evolutieService;

    /**
     * Returneaza lista de pacienti (activi + arhivati) pentru un terapeut.
     */
    @Transactional(readOnly = true)
    public ListaPacientiDTO getListaPacienti(Long terapeutId) {
        List<FisaPacientDTO> activi = buildPatientList(
                relatieRepository.findByTerapeutIdAndActivaTrue(terapeutId), true);
        List<FisaPacientDTO> arhivati = buildPatientList(
                relatieRepository.findByTerapeutIdAndActivaFalse(terapeutId), false);

        return new ListaPacientiDTO(
                activi,
                arhivati
        );
    }

    /**
     * Construieste lista FisaPacientDTO din relatii.
     */
    private List<FisaPacientDTO> buildPatientList(List<RelatiePacientTerapeut> relatii, boolean activ) {
        List<FisaPacientDTO> rezultat = new ArrayList<>();

        for (RelatiePacientTerapeut rel : relatii) {
            try {
                // detalii user (nume, prenume)
                UserDisplayCalendarDTO userDetails = userClient.getUserById(rel.getPacientId());
                if (userDetails == null) continue;

                // situatie curenta (diagnostic + sedinte)
                SituatiePacientDTO situatie = programareService.getSituatiePacient(rel.getPacientId());

                rezultat.add(new FisaPacientDTO(
                        rel.getPacientId(),
                        userDetails.nume(),
                        userDetails.prenume(),
                        null, // varsta - assumed not provided previously or maybe added to DTO recently
                        situatie.diagnostic(),
                        situatie.sedinteRamase(),
                        activ
                ));
            } catch (Exception e) {
                log.warn("Nu s-au putut încărca datele pentru pacientul {}: {}", rel.getPacientId(), e.getMessage());
            }
        }
        return rezultat;
    }

    /**
     * Returneaza fisa completa a unui pacient (detalii, evaluari, evolutii, programari, jurnale).
     */
    @Transactional(readOnly = true)
    public FisaPacientDetaliiDTO getFisaPacient(Long pacientId, Long terapeutId) {
        // 1. Detalii user (nume, prenume, telefon, email, gen)
        UserDisplayCalendarDTO userDetails = userClient.getUserById(pacientId);

        // 2. Situatia curenta
        SituatiePacientDTO situatie = programareService.getSituatiePacient(pacientId);

        // 3. Evaluari (de la TOTI terapeutii)
        List<EvaluareResponseDTO> evaluari = buildEvaluariList(pacientId);

        // 4. Evolutii (doar ale TERAPEUTULUI CURENT)
        List<EvolutieResponseDTO> evolutii = evolutieService.getIstoricEvolutii(pacientId, terapeutId);

        // 5. Programari (istoric complet) - reuse
        List<IstoricProgramareDTO> programari = programareService.getIstoricPacient(pacientId);

        // 6. Jurnale - reuse
        List<JurnalIstoricDTO> jurnale = new ArrayList<>();
        try {
            jurnale = pacientiClient.getIstoricJurnal(pacientId);
        } catch (Exception e) {
            log.warn("Nu s-au putut obține jurnalele pentru pacientul {}: {}", pacientId, e.getMessage());
        }

        return new FisaPacientDetaliiDTO(
                pacientId,
                userDetails != null ? userDetails.nume() : null,
                userDetails != null ? userDetails.prenume() : null,
                null, // varsta missing in original mapping
                userDetails != null ? userDetails.gen() : null,
                userDetails != null ? userDetails.telefon() : null,
                userDetails != null ? userDetails.email() : null,
                situatie,
                evaluari,
                evolutii,
                programari,
                jurnale
        );
    }

    /**
     * Construieste lista de evaluari cu informatii despre terapeut si serviciu.
     */
    private List<EvaluareResponseDTO> buildEvaluariList(Long pacientId) {
        List<Evaluare> evaluari = evaluareRepository.findAllByPacientIdOrderByDataDesc(pacientId);

        return evaluari.stream().map(eval -> {
            // Numele terapeutului care a facut evaluarea
            String numeTerapeut = null;
            try {
                UserDisplayCalendarDTO terapeutDetails = userClient.getUserById(eval.getTerapeutId());
                if (terapeutDetails != null) {
                    numeTerapeut = terapeutDetails.nume() + " " + terapeutDetails.prenume();
                }
            } catch (Exception e) {
                log.warn("Nu s-a putut obține numele terapeutului {}: {}", eval.getTerapeutId(), e.getMessage());
            }

            // Numele serviciului recomandat
            String serviciuNume = null;
            if (eval.getServiciuRecomandatId() != null) {
                try {
                    DetaliiServiciuDTO serviciu = serviciiClient.getServiciuById(eval.getServiciuRecomandatId());
                    if (serviciu != null) {
                        serviciuNume = serviciu.nume();
                    }
                } catch (Exception e) {
                    log.warn("Nu s-a putut obține serviciul {}: {}", eval.getServiciuRecomandatId(), e.getMessage());
                }
            }

            return new EvaluareResponseDTO(
                    eval.getId(),
                    eval.getTip().getDisplayName(),
                    eval.getData(),
                    eval.getDiagnostic(),
                    serviciuNume,
                    eval.getServiciuRecomandatId(),
                    eval.getSedinteRecomandate(),
                    eval.getObservatii(),
                    numeTerapeut,
                    eval.getTerapeutId()
            );
        }).toList();
    }
}
