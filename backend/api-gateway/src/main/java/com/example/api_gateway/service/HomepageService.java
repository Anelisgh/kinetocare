package com.example.api_gateway.service;

import com.example.api_gateway.utils.SecurityUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Map;

@Slf4j
@Service
public class HomepageService {

    private final ProfileService profileService;
    private final WebClient programariWebClient;
    private final SecurityUtils securityUtils;

    public HomepageService(ProfileService profileService,
                           @Qualifier("programariWebClient") WebClient programariWebClient,
                           SecurityUtils securityUtils) {
        this.profileService = profileService;
        this.programariWebClient = programariWebClient;
        this.securityUtils = securityUtils;
    }

    public Mono<Map<String, Object>> getHomepageData(String keycloakId, String role) {
        // luam datele complete de profil (adica user, pacient, terapeut, locatie)
        return profileService.getProfile(keycloakId, role)
                .flatMap(profileData -> {
                    // daca nu e pacient, returnam doar profilul (fara programare)
                    // TODO adaugam altceva pentru terapeuti/admini
                    if (!"PACIENT".equals(role)) {
                        return Mono.just(profileData);
                    }
                    // extragem id-ul pacientului
                    Object idObj = profileData.get("id");
                    Long pacientId = null;

                    if (idObj instanceof Number) {
                        pacientId = ((Number) idObj).longValue();
                    }

                    if (pacientId == null) {
                        log.warn("Nu s-a găsit ID-ul numeric de pacient pentru keycloakId: {}", keycloakId);
                        return Mono.just(profileData);
                    }
                    // apelam serviciul de programari pentru a lua:
                    // 1. "Următoarea Programare"
                    // 2. "Situația Pacientului" (Diagnostic + Progres)
                    Long finalPacientId = pacientId;

                    Mono<Map<String, Object>> nextApptMono = securityUtils.getJwtToken().flatMap(token -> programariWebClient.get()
                            .uri("/programari/pacient/{id}/next", finalPacientId)
                            .header("Authorization", "Bearer " + token)
                            .retrieve()
                            .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {
                            })
                            .onErrorResume(e -> Mono.empty())); // Daca nu are programare, returnam empty

                    Mono<Map<String, Object>> situatieMono = securityUtils.getJwtToken().flatMap(token -> programariWebClient.get()
                            .uri("/programari/pacient/{id}/situatie", finalPacientId)
                            .header("Authorization", "Bearer " + token)
                            .retrieve()
                            .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {
                            })
                            .onErrorResume(e -> Mono.empty())); // Daca earaore, returnam empty

                    return Mono.zip(nextApptMono.defaultIfEmpty(Map.of()), situatieMono.defaultIfEmpty(Map.of()))
                            .map(tuple -> {
                                Map<String, Object> nextAppt = tuple.getT1();
                                Map<String, Object> situatie = tuple.getT2();

                                if (!nextAppt.isEmpty()) {
                                    profileData.put("urmatoareaProgramare", nextAppt);
                                }
                                if (!situatie.isEmpty()) {
                                    profileData.put("situatie", situatie);
                                }
                                return profileData;
                            });
                });
    }
}
