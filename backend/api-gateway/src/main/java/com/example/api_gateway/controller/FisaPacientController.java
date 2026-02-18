package com.example.api_gateway.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Map;

@RestController
@RequestMapping("/api/fisa-pacient")
@RequiredArgsConstructor
public class FisaPacientController {

    private final WebClient.Builder webClientBuilder;
    private static final String PROGRAMARI_SERVICE_URL = "http://localhost:8085";

    // lista de pacienti (activi + arhivati) -> programari-service
    @GetMapping("/lista")
    public Mono<ResponseEntity<Map<String, Object>>> getListaPacienti(
            @RequestParam Long terapeutId,
            @AuthenticationPrincipal Jwt jwt) {

        return webClientBuilder.build()
                .get()
                .uri(PROGRAMARI_SERVICE_URL + "/fisa-pacient/terapeut/" + terapeutId + "/lista")
                .header("Authorization", "Bearer " + jwt.getTokenValue())
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                .map(ResponseEntity::ok);
    }

    // fisa completa a unui pacient -> programari-service
    @GetMapping("/{pacientId}")
    public Mono<ResponseEntity<Map<String, Object>>> getFisaPacient(
            @PathVariable Long pacientId,
            @RequestParam Long terapeutId,
            @AuthenticationPrincipal Jwt jwt) {

        return webClientBuilder.build()
                .get()
                .uri(PROGRAMARI_SERVICE_URL + "/fisa-pacient/pacient/" + pacientId + "?terapeutId=" + terapeutId)
                .header("Authorization", "Bearer " + jwt.getTokenValue())
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                .map(ResponseEntity::ok);
    }
}
