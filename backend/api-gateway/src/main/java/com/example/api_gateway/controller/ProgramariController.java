package com.example.api_gateway.controller;

import com.example.api_gateway.service.ProfileService;
import com.example.api_gateway.utils.SecurityUtils;
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
    private final SecurityUtils securityUtils;
    private static final String PROGRAMARI_SERVICE_URL = "http://localhost:8085";

    // creeaza o programare noua (pacient) -> programari-service
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
                .uri(PROGRAMARI_SERVICE_URL, uriBuilder ->
                        uriBuilder.path("/programari").build()
                )
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

    // determina serviciul recomandat pentru un pacient -> programari-service
    @GetMapping("/serviciu-recomandat")
    public Mono<ResponseEntity<Map<String, Object>>> getServiciuRecomandat(
            @RequestParam Long pacientId,
            @AuthenticationPrincipal Jwt jwt) {

        return webClientBuilder.build()
                .get()
                .uri(PROGRAMARI_SERVICE_URL, uriBuilder ->
                        uriBuilder
                                .path("/programari/serviciu-recomandat")
                                .queryParam("pacientId", pacientId)
                                .build()
                )
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

    // returneaza sloturile orare disponibile pentru o zi -> programari-service
    @GetMapping("/disponibilitate")
    public Mono<ResponseEntity<List<String>>> getDisponibilitate(@RequestParam Long terapeutId,
                                                                 @RequestParam Long locatieId,
                                                                 @RequestParam String data,
                                                                 @RequestParam Long serviciuId,
                                                                 @AuthenticationPrincipal Jwt jwt) {

        return webClientBuilder.build()
                .get()
                .uri(PROGRAMARI_SERVICE_URL, uriBuilder ->
                        uriBuilder
                                .path("/programari/disponibilitate")
                                .queryParam("terapeutId", terapeutId)
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
    // pacientul anuleaza o programare (extrage pacientId din profil) -> programari-service
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
                            .uri(PROGRAMARI_SERVICE_URL, uriBuilder ->
                                    uriBuilder
                                            .path("/programari/{programareId}/cancel")
                                            .queryParam("pacientId", pacientId) // gateway pune id-ul real
                                            .build(programareId)
                            )
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

    // returneaza programarile unui terapeut pentru calendar -> programari-service
    @GetMapping("/calendar")
    public Mono<ResponseEntity<List<Map<String, Object>>>> getCalendarTerapeut(
            @RequestParam Long terapeutId,
            @RequestParam String start,
            @RequestParam String end,
            @RequestParam(required = false) Long locatieId,
            @AuthenticationPrincipal Jwt jwt) {

        return webClientBuilder.build()
                .get()
                .uri(PROGRAMARI_SERVICE_URL, uriBuilder -> {
                    var builder = uriBuilder
                            .path("/programari/calendar")
                            .queryParam("terapeutId", terapeutId)
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
                .map(ResponseEntity::ok)
                .onErrorResume(e -> {
                    log.error("Eroare la preluarea calendarului pentru terapeut {}: {}", terapeutId, e.getMessage());
                    return Mono.just(ResponseEntity.internalServerError().build());
                });
    }

    // terapeutul anuleaza o programare -> programari-service
    @PatchMapping("/{programareId}/cancel-terapeut")
    public Mono<ResponseEntity<Void>> anuleazaProgramareTerapeut(
            @PathVariable Long programareId,
            @RequestParam Long terapeutId, // din frontend
            @AuthenticationPrincipal Jwt jwt) {

        return webClientBuilder.build()
                .patch()
                .uri(uriBuilder -> UriComponentsBuilder.fromUriString(PROGRAMARI_SERVICE_URL)
                        .path("/programari/" + programareId + "/cancel-terapeut")
                        .queryParam("terapeutId", terapeutId)
                        .build(true)
                        .toUri())
                .header("Authorization", "Bearer " + jwt.getTokenValue())
                .retrieve()
                .toBodilessEntity()
                .map(response -> ResponseEntity.ok().<Void>build())
                .onErrorResume(e -> {
                    log.error("Eroare anulare terapeut pt programarea {}: {}", programareId, e.getMessage());
                    return Mono.just(ResponseEntity.internalServerError().build());
                });
    }

    // marcheaza neprezentarea unui pacient (terapeut sau admin) -> programari-service
    @PatchMapping("/{programareId}/neprezentare")
    public Mono<ResponseEntity<Void>> marcheazaNeprezentare(
            @PathVariable Long programareId,
            @RequestParam(required = false) Long terapeutId, // Poate veni din frontend sau nu
            @AuthenticationPrincipal Jwt jwt) {

        String role = securityUtils.extractRole(jwt);
        boolean isAdmin = "ADMIN".equals(role);

        // Logica de decizie
        Mono<Long> terapeutIdMono;

        if (isAdmin) {
            // Dacă e admin, nu ne pasă de ID-ul terapeutului (trimitem null sau 0)
            terapeutIdMono = Mono.just(0L);
        } else if ("TERAPEUT".equals(role)) {
            // Dacă e terapeut, trebuie să validăm că trimite ID-ul lui (sau îl luăm din profil ca să fim siguri)
            // Aici fie te bazezi pe ce trimite frontend-ul (dacă ai încredere), fie îl extragi iar din profil.
            // Varianta simplă: luăm ce vine din frontend, backend-ul validează oricum ownership-ul.
            if (terapeutId == null) {
                return Mono.error(new RuntimeException("ID-ul terapeutului este necesar."));
            }
            terapeutIdMono = Mono.just(terapeutId);
        } else {
            return Mono.just(ResponseEntity.status(403).build()); // Pacienții nu au voie
        }

        return terapeutIdMono.flatMap(finalTId ->
                webClientBuilder.build()
                        .patch()
                        .uri(uriBuilder -> UriComponentsBuilder.fromUriString(PROGRAMARI_SERVICE_URL)
                                .path("/programari/" + programareId + "/neprezentare")
                                .queryParam("terapeutId", finalTId)
                                .queryParam("isAdmin", isAdmin)
                                .build(true)
                                .toUri())
                        .header("Authorization", "Bearer " + jwt.getTokenValue())
                        .retrieve()
                        .toBodilessEntity()
                        .map(response -> ResponseEntity.ok().<Void>build())
                        .onErrorResume(e -> {
                            log.error("Eroare marcare neprezentare: {}", e.getMessage());
                            return Mono.just(ResponseEntity.internalServerError().build());
                        })
        );
    }

    // returneaza programarile finalizate fara jurnal completat -> programari-service
    @GetMapping("/pacient/{id}/necompletate")
    public Mono<ResponseEntity<List<Map<String, Object>>>> getProgramariFaraJurnal(
            @PathVariable Long id,
            @AuthenticationPrincipal Jwt jwt) {

        return webClientBuilder.build()
                .get()
                .uri(PROGRAMARI_SERVICE_URL + "/programari/pacient/" + id + "/necompletate")
                .header("Authorization", "Bearer " + jwt.getTokenValue())
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<Map<String, Object>>>() {})
                .map(ResponseEntity::ok)
                .onErrorResume(e -> {
                    log.error("Eroare la preluarea programărilor necompletate: {}", e.getMessage());
                    return Mono.just(ResponseEntity.internalServerError().build());
                });
    }
}