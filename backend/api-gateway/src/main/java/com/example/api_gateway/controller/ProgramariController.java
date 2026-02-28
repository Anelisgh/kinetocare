package com.example.api_gateway.controller;

import com.example.api_gateway.service.ProfileService;
import com.example.api_gateway.utils.SecurityUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
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
@RequestMapping("/api/programari")
@Slf4j
public class ProgramariController {

    private final WebClient programariWebClient;
    private final ProfileService profileService;
    private final SecurityUtils securityUtils;

    public ProgramariController(
            @Qualifier("programariWebClient") WebClient programariWebClient,
            ProfileService profileService,
            SecurityUtils securityUtils) {
        this.programariWebClient = programariWebClient;
        this.profileService = profileService;
        this.securityUtils = securityUtils;
    }

    // creeaza o programare noua (pacient) -> programari-service
    @PostMapping
    public Mono<ResponseEntity<Map<String, Object>>> creeazaProgramare(
            @AuthenticationPrincipal Jwt jwt,
            @RequestBody Map<String, Object> programareRequest) {

        String keycloakId = jwt.getSubject();
        log.info("Request creare programare de la user: {}", keycloakId);

        // luam JSON-ul din frontend si-l trimitem mai departe catre serviciul de
        // programari
        return programariWebClient.post()
                .uri("/programari")
                .header("Authorization", "Bearer " + jwt.getTokenValue())
                .bodyValue(programareRequest)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {
                })
                .map(ResponseEntity::ok);
    }

    // determina serviciul recomandat pentru un pacient -> programari-service
    @GetMapping("/serviciu-recomandat")
    public Mono<ResponseEntity<Map<String, Object>>> getServiciuRecomandat(
            @AuthenticationPrincipal Jwt jwt) {

        String keycloakId = jwt.getSubject();

        return programariWebClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/programari/serviciu-recomandat")
                        .queryParam("pacientKeycloakId", keycloakId)
                        .build()
                )
                .header("Authorization", "Bearer " + jwt.getTokenValue())
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {
                })
                .map(ResponseEntity::ok);
    }

    // returneaza sloturile orare disponibile pentru o zi -> programari-service
    @GetMapping("/disponibilitate")
    public Mono<ResponseEntity<List<String>>> getDisponibilitate(@RequestParam String terapeutKeycloakId,
                                                                 @RequestParam Long locatieId,
                                                                 @RequestParam String data,
                                                                 @RequestParam Long serviciuId,
                                                                 @AuthenticationPrincipal Jwt jwt) {

        return programariWebClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/programari/disponibilitate")
                        .queryParam("terapeutKeycloakId", terapeutKeycloakId)
                        .queryParam("locatieId", locatieId)
                        .queryParam("data", data)
                        .queryParam("serviciuId", serviciuId)
                        .build()
                )
                .header("Authorization", "Bearer " + jwt.getTokenValue())
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<String>>() {
                })
                .map(ResponseEntity::ok)
                .onErrorResume(e -> Mono.just(ResponseEntity.ok(List.of())));
    }
    // pacientul anuleaza o programare -> programari-service
    @PatchMapping("/{programareId}/cancel")
    public Mono<ResponseEntity<Void>> anuleazaProgramare(
            @PathVariable Long programareId,
            @AuthenticationPrincipal Jwt jwt) {

        String keycloakId = jwt.getSubject();

        // trimitem cererea de anulare catre serviciul de programari
        return programariWebClient.patch()
                .uri(uriBuilder -> uriBuilder
                        .path("/programari/{programareId}/cancel")
                        .queryParam("pacientKeycloakId", keycloakId) 
                        .build(programareId)
                )
                .header("Authorization", "Bearer " + jwt.getTokenValue())
                .retrieve()
                .toBodilessEntity()
                .map(response -> ResponseEntity.ok().<Void>build());
    }

    // returneaza programarile unui terapeut pentru calendar -> programari-service
    @GetMapping("/calendar")
    public Mono<ResponseEntity<List<Map<String, Object>>>> getCalendarTerapeut(
            @AuthenticationPrincipal Jwt jwt,
            @RequestParam String start,
            @RequestParam String end,
            @RequestParam(required = false) Long locatieId) {

        String terapeutKeycloakId = jwt.getSubject();

        return programariWebClient.get()
                .uri(uriBuilder -> {
                    var builder = uriBuilder
                            .path("/programari/calendar")
                            .queryParam("terapeutKeycloakId", terapeutKeycloakId)
                            .queryParam("start", start)
                            .queryParam("end", end);

                    if (locatieId != null) {
                        builder.queryParam("locatieId", locatieId);
                    }

                    return builder.build();
                })
                .header("Authorization", "Bearer " + jwt.getTokenValue())
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<Map<String, Object>>>() {
                })
                .map(ResponseEntity::ok);
    }

    // terapeutul anuleaza o programare -> programari-service
    @PatchMapping("/{programareId}/cancel-terapeut")
    public Mono<ResponseEntity<Void>> anuleazaProgramareTerapeut(
            @PathVariable Long programareId,
            @AuthenticationPrincipal Jwt jwt) {

        String terapeutKeycloakId = jwt.getSubject();

        return programariWebClient.patch()
                .uri(uriBuilder -> uriBuilder
                        .path("/programari/{programareId}/cancel-terapeut")
                        .queryParam("terapeutKeycloakId", terapeutKeycloakId)
                        .build(programareId))
                .header("Authorization", "Bearer " + jwt.getTokenValue())
                .retrieve()
                .toBodilessEntity()
                .map(response -> ResponseEntity.ok().<Void>build());
    }

    // marcheaza neprezentarea unui pacient (terapeut sau admin) -> programari-service
    @PatchMapping("/{programareId}/neprezentare")
    public Mono<ResponseEntity<Void>> marcheazaNeprezentare(
            @PathVariable Long programareId,
            @AuthenticationPrincipal Jwt jwt) {

        String role = securityUtils.extractRole(jwt);
        boolean isAdmin = "ADMIN".equals(role);
        String terapeutKeycloakId = jwt.getSubject();

        if (!isAdmin && !"TERAPEUT".equals(role)) {
            return Mono.just(ResponseEntity.status(403).build()); // Pacienții nu au voie
        }

        return programariWebClient.patch()
                .uri(uriBuilder -> uriBuilder
                        .path("/programari/{programareId}/neprezentare")
                        .queryParam("terapeutKeycloakId", terapeutKeycloakId)
                        .queryParam("isAdmin", isAdmin)
                        .build(programareId))
                .header("Authorization", "Bearer " + jwt.getTokenValue())
                .retrieve()
                .toBodilessEntity()
                .map(response -> ResponseEntity.ok().<Void>build());
    }

    // returneaza programarile finalizate fara jurnal completat -> programari-service
    @GetMapping("/pacient/by-keycloak/{keycloakId}/necompletate")
    public Mono<ResponseEntity<List<Map<String, Object>>>> getProgramariFaraJurnal(
            @PathVariable String keycloakId,
            @AuthenticationPrincipal Jwt jwt) {

        return programariWebClient.get()
                .uri("/programari/pacient/by-keycloak/{keycloakId}/necompletate", keycloakId)
                .header("Authorization", "Bearer " + jwt.getTokenValue())
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<Map<String, Object>>>() {})
                .map(ResponseEntity::ok);
    }
}