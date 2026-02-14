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
public class ConcediuController {

    private final WebClient.Builder webClientBuilder;
    private static final String TERAPEUT_SERVICE_URL = "http://localhost:8084";

    // returneaza concediile terapeutului curent -> terapeuti-service
    @GetMapping("/concediu")
    public Mono<ResponseEntity<List<Map<String, Object>>>> getConcedii(@AuthenticationPrincipal Jwt jwt) {
        String keycloakId = jwt.getSubject();

        return webClientBuilder.build()
                .get()
                .uri(TERAPEUT_SERVICE_URL + "/concediu/terapeut/" + keycloakId)
                .header("Authorization", "Bearer " + jwt.getTokenValue())
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<Map<String, Object>>>() {})
                .map(ResponseEntity::ok)
                .onErrorResume(e -> {
                    log.error("Error getting concedii for user: {}", keycloakId, e);
                    return Mono.just(ResponseEntity.internalServerError().build());
                });
    }

    // adauga un concediu nou pentru terapeut -> terapeuti-service
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
                .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                .map(ResponseEntity::ok)
                .onErrorResume(e -> {
                    log.error("Error adding concediu for user: {}", keycloakId, e);
                    return Mono.just(ResponseEntity.internalServerError().build());
                });
    }

    // sterge un concediu existent -> terapeuti-service
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
}
