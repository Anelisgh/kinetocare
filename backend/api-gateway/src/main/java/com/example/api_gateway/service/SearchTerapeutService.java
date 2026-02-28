package com.example.api_gateway.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class SearchTerapeutService {

    private final WebClient terapeutiWebClient;
    private final WebClient pacientiWebClient;
    private final WebClient userWebClient;

    public SearchTerapeutService(
            @Qualifier("terapeutiWebClient") WebClient terapeutiWebClient,
            @Qualifier("pacientiWebClient") WebClient pacientiWebClient,
            @Qualifier("userWebClient") WebClient userWebClient) {
        this.terapeutiWebClient = terapeutiWebClient;
        this.pacientiWebClient = pacientiWebClient;
        this.userWebClient = userWebClient;
    }

    // cauta terapeuti dupa criterii si imbogateste rezultatele cu date din user-service
    public Mono<List<Map<String, Object>>> searchTerapeuti(String specializare, String judet,
                                                           String oras, Long locatieId,
                                                           String gen, String token) {
        return terapeutiWebClient.get()
                .uri(uriBuilder -> {
                    uriBuilder.path("/terapeuti/search")
                            .queryParam("specializare", specializare);
                    if (judet != null && !judet.isEmpty()) {
                        uriBuilder.queryParam("judet", judet);
                    }
                    if (oras != null && !oras.isEmpty()) {
                        uriBuilder.queryParam("oras", oras);
                    }
                    if (locatieId != null) {
                        uriBuilder.queryParam("locatieId", locatieId);
                    }
                    return uriBuilder.build();
                })
                .header("Authorization", "Bearer " + token)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<Map<String, Object>>>() {})
                .flatMap(terapeuti -> enrichTerapeutiWithUserData(terapeuti, token)
                        .map(enrichedList -> {
                            // filtrare dupa gen (se face in memorie dupa ce am primit datele)
                            if (gen != null && !gen.isEmpty() && !"null".equals(gen)) {
                                return enrichedList.stream()
                                        .filter(t -> {
                                            String userGen = (String) t.get("gen");
                                            return userGen != null && userGen.equalsIgnoreCase(gen);
                                        })
                                        .collect(Collectors.toList());
                            }
                            return enrichedList;
                        }));
    }

    // obtine detaliile terapeutului asignat pacientului curent
    public Mono<Map<String, Object>> getMyTerapeut(String keycloakId, String token) {
        return pacientiWebClient.get()
                .uri("/pacient/by-keycloak/{keycloakId}", keycloakId)
                .header("Authorization", "Bearer " + token)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                .flatMap(pacientData -> {
                    String terapeutKeycloakId = (String) pacientData.get("terapeutKeycloakId");

                    if (terapeutKeycloakId == null) {
                        Map<String, Object> noTerapeut = new HashMap<>();
                        noTerapeut.put("hasTerapeut", false);
                        return Mono.just(noTerapeut);
                    }

                    Mono<Map<String, Object>> terapeutDataMono = terapeutiWebClient.get()
                            .uri("/terapeuti/{id}/details", terapeutKeycloakId)
                            .header("Authorization", "Bearer " + token)
                            .retrieve()
                            .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {});

                    Mono<Map<String, Object>> userDataMono = userWebClient.get()
                            .uri("/users/by-keycloak/{id}", terapeutKeycloakId)
                            .header("Authorization", "Bearer " + token)
                            .retrieve()
                            .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {});

                    return Mono.zip(terapeutDataMono, userDataMono)
                            .map(tuple -> {
                                Map<String, Object> result = new HashMap<>();
                                result.put("hasTerapeut", true);
                                result.putAll(tuple.getT1());
                                result.putAll(tuple.getT2());
                                result.remove("id");
                                result.remove("role");
                                result.remove("active");
                                return result;
                            });
                });
    }

    // returneaza doar numele si prenumele unui terapeut pe baza keycloak_id (pentru chat istoric)
    public Mono<Map<String, String>> getTerapeutNumeSiPrenume(String terapeutKeycloakId, String token) {
        return userWebClient.get()
                .uri("/users/by-keycloak/{keycloakId}", terapeutKeycloakId)
                .header("Authorization", "Bearer " + token)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                .map(userData -> {
                    Map<String, String> result = new HashMap<>();
                    Object nume = userData.get("nume");
                    Object prenume = userData.get("prenume");
                    
                    result.put("nume", nume != null ? nume.toString() : "");
                    result.put("prenume", prenume != null ? prenume.toString() : "");
                    
                    return result;
                })
                .onErrorResume(e -> {
                    log.error("Eroare la preluarea numelui pentru terapeutul cu keycloakId {}", terapeutKeycloakId, e);
                    Map<String, String> empty = new HashMap<>();
                    empty.put("nume", "Terapeut");
                    empty.put("prenume", "");
                    return Mono.just(empty);
                });
    }

    // imbogateste lista de terapeuti cu date personale din user-service (nume, prenume, gen)
    private Mono<List<Map<String, Object>>> enrichTerapeutiWithUserData(
            List<Map<String, Object>> terapeuti, String token) {

        if (terapeuti == null || terapeuti.isEmpty()) {
            return Mono.just(terapeuti);
        }

        List<String> keycloakIds = terapeuti.stream()
                .map(t -> (String) t.get("keycloakId"))
                .filter(id -> id != null && !id.isEmpty())
                .distinct()
                .collect(Collectors.toList());

        if (keycloakIds.isEmpty()) {
            return Mono.just(terapeuti);
        }

        return userWebClient.post()
                .uri("/users/batch")
                .header("Authorization", "Bearer " + token)
                .bodyValue(keycloakIds)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<Map<String, Object>>>() {})
                .map(users -> {
                    Map<String, Map<String, Object>> userMap = users.stream()
                            .collect(Collectors.toMap(
                                    u -> (String) u.get("keycloakId"),
                                    u -> u,
                                    (existing, replacement) -> existing
                            ));

                    for (Map<String, Object> terapeut : terapeuti) {
                        String keycloakId = (String) terapeut.get("keycloakId");
                        Map<String, Object> userData = userMap.get(keycloakId);
                        if (userData != null) {
                            terapeut.put("nume", userData.get("nume"));
                            terapeut.put("prenume", userData.get("prenume"));
                            terapeut.put("gen", userData.get("gen"));
                        }
                    }
                    return terapeuti;
                })
                .onErrorReturn(terapeuti);
    }
}
