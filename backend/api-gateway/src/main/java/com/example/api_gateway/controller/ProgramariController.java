package com.example.api_gateway.controller;

import com.example.api_gateway.service.ProfileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/programari")
@RequiredArgsConstructor
@Slf4j
public class ProgramariController {

    private final WebClient.Builder webClientBuilder;
    private final ProfileService profileService;
    private static final String PROGRAMARI_SERVICE_URL = "http://localhost:8085";

    // pentru PACIENT
    @PostMapping
    public Mono<ResponseEntity<Map<String, Object>>> creeazaProgramare(
            @AuthenticationPrincipal Jwt jwt,
            @RequestBody Map<String, Object> programareRequest) {

        String keycloakId = jwt.getSubject();
        log.info("Request creare programare de la user: {}", keycloakId);

        // luam JSON-ul din frontend si-l trimitem mai departe catre serviciul de
        // programari
        return webClientBuilder.build()
                .post()
                .uri(PROGRAMARI_SERVICE_URL + "/programari")
                .header("Authorization", "Bearer " + jwt.getTokenValue())
                .bodyValue(programareRequest)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {
                })
                .map(ResponseEntity::ok)
                .onErrorResume(e -> {
                    log.error("Eroare la crearea programării: ", e);
                    return Mono.just(ResponseEntity.badRequest()
                            .body(Map.of("error", "Nu s-a putut crea programarea. Verifică disponibilitatea.")));
                });
    }

    @GetMapping("/serviciu-recomandat")
    public Mono<ResponseEntity<Map<String, Object>>> getServiciuRecomandat(
            @RequestParam Long pacientId,
            @AuthenticationPrincipal Jwt jwt) {

        return webClientBuilder.build()
                .get()
                .uri(uriBuilder -> UriComponentsBuilder.fromUriString(PROGRAMARI_SERVICE_URL)
                        .path("/programari/serviciu-recomandat")
                        .queryParam("pacientId", pacientId)
                        .build()
                        .toUri())
                .header("Authorization", "Bearer " + jwt.getTokenValue())
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {
                })
                .map(ResponseEntity::ok)
                .onErrorResume(e -> {
                    log.error("Eroare la determinarea serviciului recomandat", e);
                    return Mono.just(ResponseEntity.internalServerError().build());
                });
    }

    @GetMapping("/disponibilitate")
    public Mono<ResponseEntity<List<String>>> getDisponibilitate(@RequestParam Long terapeutId,
            @RequestParam Long locatieId,
            @RequestParam String data,
            @RequestParam Long serviciuId,
            @AuthenticationPrincipal Jwt jwt) {

        return webClientBuilder.build()
                .get()
                .uri(uriBuilder -> UriComponentsBuilder.fromUriString(PROGRAMARI_SERVICE_URL)
                        .path("/programari/disponibilitate")
                        .queryParam("terapeutId", terapeutId)
                        .queryParam("locatieId", locatieId)
                        .queryParam("data", data)
                        .queryParam("serviciuId", serviciuId)
                        .build(true)
                        .toUri())
                .header("Authorization", "Bearer " + jwt.getTokenValue())
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<String>>() {
                })
                .map(ResponseEntity::ok)
                .onErrorResume(e -> Mono.just(ResponseEntity.ok(List.of())));
    }

    @PatchMapping("/{programareId}/cancel")
    public Mono<ResponseEntity<Void>> anuleazaProgramare(
            @PathVariable Long programareId,
            @AuthenticationPrincipal Jwt jwt) {

        String keycloakId = jwt.getSubject();

        // cautam id-ul pacientului folosind ProfileService
        return profileService.getProfile(keycloakId, "PACIENT")
                .flatMap(profile -> {
                    // Extragem ID-ul numeric (Long)
                    Object idObj = profile.get("id");
                    if (idObj == null) {
                        return Mono.error(new RuntimeException("Profilul pacientului nu are ID valid."));
                    }
                    Long pacientId = ((Number) idObj).longValue();

                    // trimitem cererea de anulare catre serviciul de programari
                    return webClientBuilder.build()
                            .patch()
                            .uri(uriBuilder -> UriComponentsBuilder.fromUriString(PROGRAMARI_SERVICE_URL)
                                    .path("/programari/" + programareId + "/cancel")
                                    .queryParam("pacientId", pacientId) // gateway pune id-ul real
                                    .build(true)
                                    .toUri())
                            .header("Authorization", "Bearer " + jwt.getTokenValue())
                            .retrieve()
                            .toBodilessEntity();
                })
                .map(response -> ResponseEntity.ok().<Void>build())
                .onErrorResume(e -> {
                    log.error("Eroare la anularea programării {} pentru user {}: {}", programareId, keycloakId,
                            e.getMessage());
                    return Mono.just(ResponseEntity.internalServerError().build());
                });
    }
}
