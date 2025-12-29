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
@RequestMapping("/api/evaluari")
@RequiredArgsConstructor
@Slf4j
public class EvaluareController {

    private final WebClient.Builder webClientBuilder;
    private static final String PROGRAMARI_SERVICE_URL = "http://localhost:8085";

    @GetMapping("/pacienti-recenti")
    public Mono<ResponseEntity<List<Map<String, Object>>>> getPacientiTerapeut(
            @RequestParam Long terapeutId,
            @AuthenticationPrincipal Jwt jwt) {

        return webClientBuilder.build()
                .get()
                .uri(PROGRAMARI_SERVICE_URL + "/evaluari/pacienti-recenti?terapeutId=" + terapeutId)
                .header("Authorization", "Bearer " + jwt.getTokenValue())
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<Map<String, Object>>>() {})
                .map(ResponseEntity::ok);
    }

    @PostMapping
    public Mono<ResponseEntity<Map<String, Object>>> creeazaEvaluare(
            @RequestBody Map<String, Object> request,
            @AuthenticationPrincipal Jwt jwt) {

        return webClientBuilder.build()
                .post()
                .uri(PROGRAMARI_SERVICE_URL + "/evaluari")
                .header("Authorization", "Bearer " + jwt.getTokenValue())
                .bodyValue(request)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                .map(ResponseEntity::ok);
    }
}