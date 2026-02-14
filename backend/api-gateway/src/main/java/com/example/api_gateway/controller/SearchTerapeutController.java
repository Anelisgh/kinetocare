package com.example.api_gateway.controller;

import com.example.api_gateway.service.SearchTerapeutService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/terapeut")
@RequiredArgsConstructor
@Slf4j
public class SearchTerapeutController {

    private final SearchTerapeutService searchTerapeutService;
    private final WebClient.Builder webClientBuilder;
    private static final String PACIENT_SERVICE_URL = "http://localhost:8083";

    // cauta terapeuti dupa criteriile pacientului -> SearchTerapeutService
    @GetMapping("/search-terapeuti")
    public Mono<ResponseEntity<List<Map<String, Object>>>> searchTerapeuti(
            @RequestParam String specializare,
            @RequestParam(required = false) String judet,
            @RequestParam(required = false) String oras,
            @RequestParam(required = false) Long locatieId,
            @RequestParam(required = false) String gen,
            @AuthenticationPrincipal Jwt jwt) {

        return searchTerapeutService.searchTerapeuti(specializare, judet, oras, locatieId, gen, jwt.getTokenValue())
                .map(ResponseEntity::ok)
                .onErrorResume(e -> {
                    log.error("Error searching terapeuti", e);
                    return Mono.just(ResponseEntity.internalServerError().build());
                });
    }

    // pacientul isi alege terapeutul -> pacienti-service
    @PostMapping("/choose-terapeut/{terapeutKeycloakId}")
    public Mono<ResponseEntity<Map<String, Object>>> chooseTerapeut(
            @PathVariable String terapeutKeycloakId,
            @RequestParam(required = false) Long locatieId,
            @AuthenticationPrincipal Jwt jwt) {

        String keycloakId = jwt.getSubject();
        // parametrul fiind optional, altfel s-ar putea interpreta locatieId="null" dand
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
                .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                .map(ResponseEntity::ok)
                .onErrorResume(e -> {
                    log.error("Error choosing terapeut for user: {}", keycloakId, e);
                    return Mono.just(ResponseEntity.internalServerError().build());
                });
    }

    // pacientul renunta la terapeutul curent -> pacienti-service
    @DeleteMapping("/remove-terapeut")
    public Mono<ResponseEntity<Map<String, Object>>> removeTerapeut(@AuthenticationPrincipal Jwt jwt) {
        String keycloakId = jwt.getSubject();

        return webClientBuilder.build()
                .delete()
                .uri(PACIENT_SERVICE_URL + "/pacient/" + keycloakId + "/remove-terapeut")
                .header("Authorization", "Bearer " + jwt.getTokenValue())
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                .map(ResponseEntity::ok)
                .onErrorResume(e -> {
                    log.error("Error removing terapeut for user: {}", keycloakId, e);
                    return Mono.just(ResponseEntity.internalServerError().build());
                });
    }

    // returneaza detaliile terapeutului asignat pacientului -> SearchTerapeutService
    @GetMapping("/my-terapeut")
    public Mono<ResponseEntity<Map<String, Object>>> getMyTerapeut(@AuthenticationPrincipal Jwt jwt) {
        String keycloakId = jwt.getSubject();

        return searchTerapeutService.getMyTerapeut(keycloakId, jwt.getTokenValue())
                .map(ResponseEntity::ok)
                .onErrorResume(e -> {
                    log.error("Error getting my terapeut for user: {}", keycloakId, e);
                    return Mono.just(
                            ResponseEntity.internalServerError().body(Map.<String, Object>of()));
                });
    }
}
