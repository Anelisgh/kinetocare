package com.example.api_gateway.service;

import com.example.api_gateway.utils.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class HomepageService {

    private final ProfileService profileService;
    private final WebClient.Builder webClientBuilder;
    private final SecurityUtils securityUtils;

    private static final String PROGRAMARI_SERVICE_URL = "http://localhost:8085";

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
                    // apelam serviciul de programari pentru a lua "Următoarea Programare" a
                    // pacientului
                    Long finalPacientId = pacientId;
                    return securityUtils.getJwtToken().flatMap(token -> webClientBuilder.build()
                            .get()
                            .uri(PROGRAMARI_SERVICE_URL + "/programari/pacient/" + finalPacientId + "/next")
                            .header("Authorization", "Bearer " + token)
                            .retrieve()
                            .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {
                            })
                            .map(nextAppt -> {
                                profileData.put("urmatoareaProgramare", nextAppt);
                                return profileData;
                            })
                            // daca nu are programare continuam doar cu profilul
                            .switchIfEmpty(Mono.just(profileData))
                            .onErrorResume(e -> {
                                log.error("Eroare la preluarea următoarei programări: ", e);
                                // nu blocam fluxul daca pica serviciul de programari
                                return Mono.just(profileData);
                            }));
                });
    }
}
