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
    public void asiguraRelatieActiva(Long pacientId, Long terapeutId, LocalDate dataInceput) {
        Optional<RelatiePacientTerapeut> relatieOpt = relatieRepository.findByPacientIdAndTerapeutId(pacientId, terapeutId);

        if (relatieOpt.isPresent()) {
            // cazul 1: relatia exista deja cu acest terapeut
            RelatiePacientTerapeut relatie = relatieOpt.get();
            // daca era inactiva (poate au lucrat in trecut), o reactivam
            if (Boolean.FALSE.equals(relatie.getActiva())) {
                // dezactivam orice alta relatie activa a pacientului
                dezactiveazaRelatiaActiva(pacientId);
                relatie.setActiva(true);
                relatie.setDataSfarsit(null); // stergem data de sfarsit anterioara
                relatieRepository.save(relatie);
            }
            // daca e deja activa cu acest terapeut, nu facem nimic
        } else {
            // cazul 2: relatia noua — dezactivam relatia veche (daca exista)
            dezactiveazaRelatiaActiva(pacientId);
            RelatiePacientTerapeut nouaRelatie = relatieMapper.toEntity(pacientId, terapeutId, dataInceput);
            relatieRepository.save(nouaRelatie);
        }
    }

    // dezactiveaza relatia activa curenta a pacientului (cu orice terapeut)
    @Transactional
    public void dezactiveazaRelatiaActiva(Long pacientId) {
        relatieRepository.findByPacientIdAndActivaTrue(pacientId).ifPresent(relatie -> {
            relatie.setActiva(false);
            relatie.setDataSfarsit(LocalDate.now());
            relatieRepository.save(relatie);
            log.info("Relația pacientului {} cu terapeutul {} a fost dezactivată.", pacientId, relatie.getTerapeutId());
        });
    }

    // verifica daca o relatie anume intre pacient si terapeut este activa
    @Transactional(readOnly = true)
    public boolean isRelatieActiva(Long pacientId, Long terapeutId) {
        return relatieRepository.findByPacientIdAndTerapeutId(pacientId, terapeutId)
                .map(RelatiePacientTerapeut::getActiva)
                .orElse(false);
    }

    // verifica statusul relatiei folosind keycloakId (apelat de chat-service prin WebSocket)
    // Rezolva keycloakId -> service ID prin Feign, apoi face lookup in relatii
    @Transactional(readOnly = true)
    public boolean isRelatieActivaByKeycloak(String pacientKeycloakId, String terapeutKeycloakId) {
        try {
            Long pacientId = pacientiClient.getByKeycloakId(pacientKeycloakId).id();
            Long terapeutId = ((Number) terapeutiClient.getTerapeutByKeycloakId(terapeutKeycloakId).get("id")).longValue();
            return isRelatieActiva(pacientId, terapeutId);
        } catch (Exception e) {
            log.warn("Nu s-a putut verifica relatia prin keycloak [pac={}, ter={}]: {}",
                    pacientKeycloakId, terapeutKeycloakId, e.getMessage());
            return false;
        }
    }

    @Transactional(readOnly = true)
    public java.util.List<Long> getParteneriActivi(Long userId, String tipUser) {
        if ("PACIENT".equalsIgnoreCase(tipUser)) {
            return relatieRepository.findByPacientIdAndActivaTrue(userId)
                    .map(rel -> java.util.List.of(rel.getTerapeutId()))
                    .orElseGet(java.util.Collections::emptyList);
        } else if ("TERAPEUT".equalsIgnoreCase(tipUser)) {
            return relatieRepository.findByTerapeutIdAndActivaTrue(userId).stream()
                    .map(RelatiePacientTerapeut::getPacientId)
                    .toList();
        }
        return java.util.Collections.emptyList();
    }

    // Returneaza keycloakId-urile partenerilor activi
    // Folosit de api-gateway (ChatGatewayService) pentru noul sistem uniform cu keycloakId
    @Transactional(readOnly = true)
    public java.util.List<String> getParteneriActiviKeycloak(String userKeycloakId, String tipUser) {
        try {
            if ("PACIENT".equalsIgnoreCase(tipUser)) {
                Long pacientId = pacientiClient.getByKeycloakId(userKeycloakId).id();
                return relatieRepository.findByPacientIdAndActivaTrue(pacientId)
                        .map(rel -> terapeutiClient.getKeycloakIdByTerapeutId(rel.getTerapeutId()))
                        .map(java.util.List::of)
                        .orElseGet(java.util.Collections::emptyList);
            } else if ("TERAPEUT".equalsIgnoreCase(tipUser)) {
                Long terapeutId = ((Number) terapeutiClient.getTerapeutByKeycloakId(userKeycloakId).get("id")).longValue();
                return relatieRepository.findByTerapeutIdAndActivaTrue(terapeutId).stream()
                        .map(rel -> pacientiClient.getPacientById(rel.getPacientId()).keycloakId())
                        .filter(java.util.Objects::nonNull)
                        .toList();
            }
        } catch (Exception e) {
            log.warn("Nu s-au putut obtine partenerii activi prin keycloak pentru {}: {}", userKeycloakId, e.getMessage());
        }
        return java.util.Collections.emptyList();
    }
}