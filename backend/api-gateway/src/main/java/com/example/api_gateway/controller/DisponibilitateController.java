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

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/terapeut")
@RequiredArgsConstructor
@Slf4j
public class DisponibilitateController {

    private final WebClient.Builder webClientBuilder;
    private static final String TERAPEUT_SERVICE_URL = "http://localhost:8084";

    // returneaza disponibilitatile terapeutului curent -> terapeuti-service
    @GetMapping("/disponibilitate")
    public Mono<ResponseEntity<List<Map<String, Object>>>> getDisponibilitati(@AuthenticationPrincipal Jwt jwt) {
        String keycloakId = jwt.getSubject();

        return webClientBuilder.build()
                .get()
                .uri(TERAPEUT_SERVICE_URL + "/disponibilitate/terapeut/" + keycloakId)
                .header("Authorization", "Bearer " + jwt.getTokenValue())
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<Map<String, Object>>>() {})
                .map(ResponseEntity::ok)
                .onErrorResume(e -> {
                    log.error("Error getting disponibilitati for user: {}", keycloakId, e);
                    return Mono.just(ResponseEntity.internalServerError().build());
                });
    }

    // adauga o noua disponibilitate pentru terapeut -> terapeuti-service
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
                .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                .map(ResponseEntity::ok)
                .onErrorResume(e -> {
                    log.error("Error adding disponibilitate for user: {}", keycloakId, e);
                    return Mono.just(ResponseEntity.internalServerError().build());
                });
    }

    // sterge o disponibilitate existenta -> terapeuti-service
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
}
