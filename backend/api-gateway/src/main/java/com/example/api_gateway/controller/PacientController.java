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
@RequestMapping("/api/pacienti")
@RequiredArgsConstructor
@Slf4j
public class PacientController {

    private final WebClient.Builder webClientBuilder;
    private static final String PACIENTI_SERVICE_URL = "http://localhost:8083";

    @PostMapping("/{pacientId}/jurnal")
    public Mono<ResponseEntity<Void>> adaugaJurnal(
            @PathVariable Long pacientId,
            @RequestBody Map<String, Object> jurnalRequest,
            @AuthenticationPrincipal Jwt jwt) {

        return webClientBuilder.build()
                .post()
                .uri(PACIENTI_SERVICE_URL + "/jurnal/" + pacientId)
                .header("Authorization", "Bearer " + jwt.getTokenValue())
                .bodyValue(jurnalRequest)
                .retrieve()
                .toBodilessEntity() // Nu așteptăm răspuns body, doar status 200 OK
                .map(response -> ResponseEntity.ok().<Void>build())
                .onErrorResume(e -> {
                    log.error("Eroare la salvarea jurnalului: {}", e.getMessage());
                    return Mono.just(ResponseEntity.internalServerError().build());
                });
    }

    // Endpoint nou: Istoric Jurnal
    @GetMapping("/{pacientId}/jurnal/istoric")
    public Mono<ResponseEntity<List<Map<String, Object>>>> getIstoricJurnal(
            @PathVariable Long pacientId,
            @AuthenticationPrincipal Jwt jwt) {

        return webClientBuilder.build()
                .get()
                .uri(PACIENTI_SERVICE_URL + "/jurnal/" + pacientId + "/istoric")
                .header("Authorization", "Bearer " + jwt.getTokenValue())
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<Map<String, Object>>>() {})
                .map(ResponseEntity::ok)
                .onErrorResume(e -> {
                    log.error("Eroare la preluarea istoricului: {}", e.getMessage());
                    return Mono.just(ResponseEntity.internalServerError().build());
                });
    }
}
