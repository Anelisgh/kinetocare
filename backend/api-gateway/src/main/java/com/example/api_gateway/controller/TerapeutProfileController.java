package com.example.api_gateway.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/terapeut")
@RequiredArgsConstructor
@Slf4j
public class TerapeutProfileController {

    private final WebClient.Builder webClientBuilder;
    private static final String TERAPEUT_SERVICE_URL = "http://localhost:8084";
    private static final String PACIENT_SERVICE_URL = "http://localhost:8083";
    private static final String USER_SERVICE_URL = "http://localhost:8082";

    @GetMapping("/disponibilitate")
    public Mono<ResponseEntity<List<Map<String, Object>>>> getDisponibilitati(@AuthenticationPrincipal Jwt jwt) {
        String keycloakId = jwt.getSubject();

        return webClientBuilder.build()
                .get()
                .uri(TERAPEUT_SERVICE_URL + "/disponibilitate/terapeut/" + keycloakId)
                .header("Authorization", "Bearer " + jwt.getTokenValue())
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<Map<String, Object>>>() {
                })
                .map(ResponseEntity::ok)
                .onErrorResume(e -> {
                    log.error("Error getting disponibilitati for user: {}", keycloakId, e);
                    return Mono.just(ResponseEntity.internalServerError().build());
                });
    }

    @PostMapping("/disponibilitate")
    public Mono<ResponseEntity<Map<String, Object>>> addDisponibilitate(
            @AuthenticationPrincipal Jwt jwt,
            @RequestBody Map<String, Object> disponibilitate) {
        String keycloakId = jwt.getSubject();

        return webClientBuilder.build()
                .post()
                .uri(TERAPEUT_SERVICE_URL + "/disponibilitate/terapeut/" + keycloakId)
                .header("Authorization", "Bearer " + jwt.getTokenValue())
                .bodyValue(disponibilitate)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {
                })
                .map(ResponseEntity::ok)
                .onErrorResume(e -> {
                    log.error("Error adding disponibilitate for user: {}", keycloakId, e);
                    return Mono.just(ResponseEntity.internalServerError().build());
                });
    }

    @DeleteMapping("/disponibilitate/{disponibilitateId}")
    public Mono<ResponseEntity<Void>> deleteDisponibilitate(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable Long disponibilitateId) {
        String keycloakId = jwt.getSubject();

        return webClientBuilder.build()
                .delete()
                .uri(TERAPEUT_SERVICE_URL + "/disponibilitate/" + disponibilitateId + "/terapeut/" + keycloakId)
                .header("Authorization", "Bearer " + jwt.getTokenValue())
                .retrieve()
                .bodyToMono(Void.class)
                .map(ResponseEntity::ok)
                .onErrorResume(e -> {
                    log.error("Error deleting disponibilitate {} for user: {}", disponibilitateId, keycloakId, e);
                    return Mono.just(ResponseEntity.internalServerError().build());
                });
    }

    @GetMapping("/concediu")
    public Mono<ResponseEntity<List<Map<String, Object>>>> getConcedii(@AuthenticationPrincipal Jwt jwt) {
        String keycloakId = jwt.getSubject();

        return webClientBuilder.build()
                .get()
                .uri(TERAPEUT_SERVICE_URL + "/concediu/terapeut/" + keycloakId)
                .header("Authorization", "Bearer " + jwt.getTokenValue())
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<Map<String, Object>>>() {
                })
                .map(ResponseEntity::ok)
                .onErrorResume(e -> {
                    log.error("Error getting concedii for user: {}", keycloakId, e);
                    return Mono.just(ResponseEntity.internalServerError().build());
                });
    }

    @PostMapping("/concediu")
    public Mono<ResponseEntity<Map<String, Object>>> addConcediu(
            @AuthenticationPrincipal Jwt jwt,
            @RequestBody Map<String, Object> concediu) {
        String keycloakId = jwt.getSubject();

        return webClientBuilder.build()
                .post()
                .uri(TERAPEUT_SERVICE_URL + "/concediu/terapeut/" + keycloakId)
                .header("Authorization", "Bearer " + jwt.getTokenValue())
                .bodyValue(concediu)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {
                })
                .map(ResponseEntity::ok)
                .onErrorResume(e -> {
                    log.error("Error adding concediu for user: {}", keycloakId, e);
                    return Mono.just(ResponseEntity.internalServerError().build());
                });
    }

    @DeleteMapping("/concediu/{concediuId}")
    public Mono<ResponseEntity<Void>> deleteConcediu(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable Long concediuId) {
        String keycloakId = jwt.getSubject();

        return webClientBuilder.build()
                .delete()
                .uri(TERAPEUT_SERVICE_URL + "/concediu/" + concediuId + "/terapeut/" + keycloakId)
                .header("Authorization", "Bearer " + jwt.getTokenValue())
                .retrieve()
                .bodyToMono(Void.class)
                .map(ResponseEntity::ok)
                .onErrorResume(e -> {
                    log.error("Error deleting concediu {} for user: {}", concediuId, keycloakId, e);
                    return Mono.just(ResponseEntity.internalServerError().build());
                });
    }

    // pentru dropdown-ul din Disponibilitate, dar si pentru ProfilPacient
    @GetMapping("/locatii")
    public Mono<ResponseEntity<List<Map<String, Object>>>> getLocatii(@AuthenticationPrincipal Jwt jwt) {
        return webClientBuilder.build()
                .get()
                .uri(TERAPEUT_SERVICE_URL + "/locatii")
                .header("Authorization", "Bearer " + jwt.getTokenValue())
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<Map<String, Object>>>() {
                })
                .map(ResponseEntity::ok)
                .onErrorResume(e -> {
                    log.error("Error getting locatii", e);
                    return Mono.just(ResponseEntity.internalServerError().build());
                });
    }

    // pacientul isi cauta terapeutul (profilPacient)
    @GetMapping("/search-terapeuti")
    public Mono<ResponseEntity<List<Map<String, Object>>>> searchTerapeuti(
            @RequestParam String specializare,
            @RequestParam(required = false) String judet,
            @RequestParam(required = false) String oras,
            @RequestParam(required = false) Long locatieId,
            @RequestParam(required = false) String gen, // Parametru nou: MASCULIN / FEMININ
            @AuthenticationPrincipal Jwt jwt) {

        // Construim URL-ul pentru Terapeut Service (filtrăm doar locația/specializarea
        // aici)
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
                .header("Authorization", "Bearer " + jwt.getTokenValue())
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<Map<String, Object>>>() {
                })
                .flatMap(terapeuti -> {
                    // Îmbogățim lista cu datele din User Service (Nume, Prenume, Gen)
                    return enrichTerapeutiWithUserData(terapeuti, jwt.getTokenValue())
                            .map(enrichedList -> {
                                // === AICI FILTRĂM DUPĂ GEN ===
                                // Filtrul se face în memorie, după ce am primit datele
                                if (gen != null && !gen.isEmpty() && !gen.equals("null")) {
                                    return enrichedList.stream()
                                            .filter(t -> {
                                                String userGen = (String) t.get("gen");
                                                // Verificăm dacă genul userului corespunde cu cel cerut
                                                return userGen != null && userGen.equalsIgnoreCase(gen);
                                            })
                                            .collect(Collectors.toList());
                                }
                                return enrichedList;
                            });
                })
                .map(ResponseEntity::ok)
                .onErrorResume(e -> {
                    log.error("Error searching terapeuti", e);
                    return Mono.just(ResponseEntity.internalServerError().build());
                });
    }

    @PostMapping("/choose-terapeut/{terapeutKeycloakId}")
    public Mono<ResponseEntity<Map<String, Object>>> chooseTerapeut(
            @PathVariable String terapeutKeycloakId,
            @RequestParam(required = false) Long locatieId,
            @AuthenticationPrincipal Jwt jwt) {

        String keycloakId = jwt.getSubject();
        // parametrul fiind optional, altfel s-ar putea intrpreta locatieId="null" dand
        // o eroare de formatare (pt ca asteapta Long, nu String)
        StringBuilder uriBuilder = new StringBuilder(
                PACIENT_SERVICE_URL + "/pacient/" + keycloakId + "/choose-terapeut/" + terapeutKeycloakId);
        if (locatieId != null) {
            uriBuilder.append("?locatieId=").append(locatieId);
        }

        return webClientBuilder.build()
                .post()
                .uri(uriBuilder.toString())
                .header("Authorization", "Bearer " + jwt.getTokenValue())
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {
                })
                .map(ResponseEntity::ok)
                .onErrorResume(e -> {
                    log.error("Error choosing terapeut for user: {}", keycloakId, e);
                    return Mono.just(ResponseEntity.internalServerError().build());
                });
    }

    @DeleteMapping("/remove-terapeut")
    public Mono<ResponseEntity<Map<String, Object>>> removeTerapeut(@AuthenticationPrincipal Jwt jwt) {
        String keycloakId = jwt.getSubject();

        return webClientBuilder.build()
                .delete()
                .uri(PACIENT_SERVICE_URL + "/pacient/" + keycloakId + "/remove-terapeut")
                .header("Authorization", "Bearer " + jwt.getTokenValue())
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {
                })
                .map(ResponseEntity::ok)
                .onErrorResume(e -> {
                    log.error("Error removing terapeut for user: {}", keycloakId, e);
                    return Mono.just(ResponseEntity.internalServerError().build());
                });
    }

    @GetMapping("/my-terapeut")
    public Mono<ResponseEntity<Map<String, Object>>> getMyTerapeut(@AuthenticationPrincipal Jwt jwt) {
        String keycloakId = jwt.getSubject();

        return webClientBuilder.build()
                .get()
                .uri(PACIENT_SERVICE_URL + "/pacient/by-keycloak/" + keycloakId)
                .header("Authorization", "Bearer " + jwt.getTokenValue())
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {
                })
                .flatMap(pacientData -> {
                    String terapeutKeycloakId = (String) pacientData.get("terapeutKeycloakId");

                    if (terapeutKeycloakId == null) {
                        Map<String, Object> noTerapeut = new HashMap<>();
                        noTerapeut.put("hasTerapeut", false);
                        return Mono.just(ResponseEntity.ok(noTerapeut));
                    }

                    Mono<Map<String, Object>> terapeutDataMono = webClientBuilder.build()
                            .get()
                            .uri(TERAPEUT_SERVICE_URL + "/terapeuti/" + terapeutKeycloakId + "/details")
                            .header("Authorization", "Bearer " + jwt.getTokenValue())
                            .retrieve()
                            .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {
                            });

                    Mono<Map<String, Object>> userDataMono = webClientBuilder.build()
                            .get()
                            .uri(USER_SERVICE_URL + "/users/by-keycloak/" + terapeutKeycloakId)
                            .header("Authorization", "Bearer " + jwt.getTokenValue())
                            .retrieve()
                            .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {
                            });

                    return Mono.zip(terapeutDataMono, userDataMono)
                            .map(tuple -> {
                                Map<String, Object> result = new java.util.HashMap<>();
                                result.put("hasTerapeut", true);
                                result.putAll(tuple.getT1());
                                result.putAll(tuple.getT2());
                                result.remove("id");
                                result.remove("role");
                                result.remove("active");
                                return ResponseEntity.<Map<String, Object>>ok(result);
                            });
                })
                .onErrorResume(e -> {
                    log.error("Error getting my terapeut for user: {}", keycloakId, e);
                    return Mono.just(
                            ResponseEntity.internalServerError().body(Map.<String, Object>of()));
                });
    }

    private Mono<List<Map<String, Object>>> enrichTerapeutiWithUserData(
            List<Map<String, Object>> terapeuti, String token) {

        return reactor.core.publisher.Flux.fromIterable(terapeuti)
                .flatMap(terapeut -> {
                    String keycloakId = (String) terapeut.get("keycloakId");

                    return webClientBuilder.build()
                            .get()
                            .uri(USER_SERVICE_URL + "/users/by-keycloak/" + keycloakId)
                            .header("Authorization", "Bearer " + token)
                            .retrieve()
                            .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {
                            })
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
