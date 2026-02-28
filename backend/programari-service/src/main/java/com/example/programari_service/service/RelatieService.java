package com.example.programari_service.service;

import com.example.programari_service.client.PacientiClient;
import com.example.programari_service.client.TerapeutiClient;
import com.example.programari_service.entity.RelatiePacientTerapeut;
import com.example.programari_service.mapper.RelatieMapper;
import com.example.programari_service.repository.RelatieRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class RelatieService {

    private final RelatieRepository relatieRepository;
    private final RelatieMapper relatieMapper;
    private final PacientiClient pacientiClient;
    private final TerapeutiClient terapeutiClient;

    // asigura ca relatia e activa
    @Transactional
    public void asiguraRelatieActiva(String pacientKeycloakId, String terapeutKeycloakId, LocalDate dataInceput) {
        Optional<RelatiePacientTerapeut> relatieOpt = relatieRepository.findByPacientKeycloakIdAndTerapeutKeycloakId(pacientKeycloakId, terapeutKeycloakId);

        if (relatieOpt.isPresent()) {
            // cazul 1: relatia exista deja cu acest terapeut
            RelatiePacientTerapeut relatie = relatieOpt.get();
            // daca era inactiva (poate au lucrat in trecut), o reactivam
            if (Boolean.FALSE.equals(relatie.getActiva())) {
                // dezactivam orice alta relatie activa a pacientului
                dezactiveazaRelatiaActiva(pacientKeycloakId);
                relatie.setActiva(true);
                relatie.setDataSfarsit(null); // stergem data de sfarsit anterioara
                relatieRepository.save(relatie);
            }
            // daca e deja activa cu acest terapeut, nu facem nimic
        } else {
            // cazul 2: relatia noua — dezactivam relatia veche (daca exista)
            dezactiveazaRelatiaActiva(pacientKeycloakId);
            RelatiePacientTerapeut nouaRelatie = relatieMapper.toEntity(pacientKeycloakId, terapeutKeycloakId, dataInceput);
            relatieRepository.save(nouaRelatie);
        }
    }

    // dezactiveaza relatia activa curenta a pacientului (cu orice terapeut)
    @Transactional
    public void dezactiveazaRelatiaActiva(String pacientKeycloakId) {
        relatieRepository.findByPacientKeycloakIdAndActivaTrue(pacientKeycloakId).ifPresent(relatie -> {
            relatie.setActiva(false);
            relatie.setDataSfarsit(LocalDate.now());
            relatieRepository.save(relatie);
            log.info("Relația pacientului {} cu terapeutul {} a fost dezactivată.", pacientKeycloakId, relatie.getTerapeutKeycloakId());
        });
    }

    // verifica daca o relatie anume intre pacient si terapeut este activa
    @Transactional(readOnly = true)
    public boolean isRelatieActiva(String pacientKeycloakId, String terapeutKeycloakId) {
        return relatieRepository.findByPacientKeycloakIdAndTerapeutKeycloakId(pacientKeycloakId, terapeutKeycloakId)
                .map(RelatiePacientTerapeut::getActiva)
                .orElse(false);
    }

    // verifica statusul relatiei folosind keycloakId (apelat de chat-service prin WebSocket)
    @Transactional(readOnly = true)
    public boolean isRelatieActivaByKeycloak(String pacientKeycloakId, String terapeutKeycloakId) {
        return isRelatieActiva(pacientKeycloakId, terapeutKeycloakId);
    }

    @Transactional(readOnly = true)
    public java.util.List<String> getParteneriActivi(String userKeycloakId, String tipUser) {
        return getParteneriActiviKeycloak(userKeycloakId, tipUser);
    }

    // Returneaza keycloakId-urile partenerilor activi
    // Folosit de api-gateway (ChatGatewayService) pentru noul sistem uniform cu keycloakId
    @Transactional(readOnly = true)
    public java.util.List<String> getParteneriActiviKeycloak(String userKeycloakId, String tipUser) {
        if ("PACIENT".equalsIgnoreCase(tipUser)) {
            return relatieRepository.findByPacientKeycloakIdAndActivaTrue(userKeycloakId)
                    .map(rel -> rel.getTerapeutKeycloakId())
                    .map(java.util.List::of)
                    .orElseGet(java.util.Collections::emptyList);
        } else if ("TERAPEUT".equalsIgnoreCase(tipUser)) {
            return relatieRepository.findByTerapeutKeycloakIdAndActivaTrue(userKeycloakId).stream()
                    .map(RelatiePacientTerapeut::getPacientKeycloakId)
                    .toList();
        }
        return java.util.Collections.emptyList();
    }
}