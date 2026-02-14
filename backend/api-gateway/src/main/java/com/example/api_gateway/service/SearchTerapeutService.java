package com.example.api_gateway.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class SearchTerapeutService {

    private final WebClient.Builder webClientBuilder;

    private static final String TERAPEUT_SERVICE_URL = "http://localhost:8084";
    private static final String PACIENT_SERVICE_URL = "http://localhost:8083";
    private static final String USER_SERVICE_URL = "http://localhost:8082";

    // cauta terapeuti dupa criterii si imbogateste rezultatele cu date din user-service
    public Mono<List<Map<String, Object>>> searchTerapeuti(String specializare, String judet,
                                                           String oras, Long locatieId,
                                                           String gen, String token) {
        StringBuilder uriBuilder = new StringBuilder(
                TERAPEUT_SERVICE_URL + "/terapeuti/search?specializare=" + specializare);
        if (judet != null && !judet.isEmpty())
            uriBuilder.append("&judet=").append(judet);
        if (oras != null && !oras.isEmpty())
            uriBuilder.append("&oras=").append(oras);
        if (locatieId != null)
            uriBuilder.append("&locatieId=").append(locatieId);

        return webClientBuilder.build()
                .get()
                .uri(uriBuilder.toString())
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
        return webClientBuilder.build()
                .get()
                .uri(PACIENT_SERVICE_URL + "/pacient/by-keycloak/" + keycloakId)
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

                    Mono<Map<String, Object>> terapeutDataMono = webClientBuilder.build()
                            .get()
                            .uri(TERAPEUT_SERVICE_URL + "/terapeuti/" + terapeutKeycloakId + "/details")
                            .header("Authorization", "Bearer " + token)
                            .retrieve()
                            .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {});

                    Mono<Map<String, Object>> userDataMono = webClientBuilder.build()
                            .get()
                            .uri(USER_SERVICE_URL + "/users/by-keycloak/" + terapeutKeycloakId)
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

    // imbogateste lista de terapeuti cu date personale din user-service (nume, prenume, gen)
    private Mono<List<Map<String, Object>>> enrichTerapeutiWithUserData(
            List<Map<String, Object>> terapeuti, String token) {

        return Flux.fromIterable(terapeuti)
                .flatMap(terapeut -> {
                    String keycloakId = (String) terapeut.get("keycloakId");

                    return webClientBuilder.build()
                            .get()
                            .uri(USER_SERVICE_URL + "/users/by-keycloak/" + keycloakId)
                            .header("Authorization", "Bearer " + token)
                            .retrieve()
                            .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                            .map(userData -> {
                                terapeut.put("nume", userData.get("nume"));
                                terapeut.put("prenume", userData.get("prenume"));
                                terapeut.put("gen", userData.get("gen"));
                                return terapeut;
                            })
                            .onErrorReturn(terapeut);
                })
                .collectList();
    }
}
