package com.example.api_gateway.controller;

import com.example.api_gateway.service.SearchTerapeutService;
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
@RequestMapping("/api/terapeut")
@Slf4j
public class SearchTerapeutController {

    private final SearchTerapeutService searchTerapeutService;
    private final WebClient pacientiWebClient;

    public SearchTerapeutController(
            SearchTerapeutService searchTerapeutService,
            @Qualifier("pacientiWebClient") WebClient pacientiWebClient) {
        this.searchTerapeutService = searchTerapeutService;
        this.pacientiWebClient = pacientiWebClient;
    }

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
                .map(ResponseEntity::ok);
    }

    // pacientul isi alege terapeutul -> pacienti-service
    @PostMapping("/choose-terapeut/{terapeutKeycloakId}")
    public Mono<ResponseEntity<Map<String, Object>>> chooseTerapeut(
            @PathVariable String terapeutKeycloakId,
            @RequestParam(required = false) Long locatieId,
            @AuthenticationPrincipal Jwt jwt) {

        String keycloakId = jwt.getSubject();

        return pacientiWebClient.post()
                .uri(uriBuilder -> {
                    uriBuilder.path("/pacient/{keycloakId}/choose-terapeut/{terapeutKeycloakId}");
                    if (locatieId != null) {
                        uriBuilder.queryParam("locatieId", locatieId);
                    }
                    return uriBuilder.build(keycloakId, terapeutKeycloakId);
                })
                .header("Authorization", "Bearer " + jwt.getTokenValue())
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                .map(ResponseEntity::ok);
    }

    // pacientul renunta la terapeutul curent -> pacienti-service
    @DeleteMapping("/remove-terapeut")
    public Mono<ResponseEntity<Map<String, Object>>> removeTerapeut(@AuthenticationPrincipal Jwt jwt) {
        String keycloakId = jwt.getSubject();

        return pacientiWebClient.delete()
                .uri("/pacient/{keycloakId}/remove-terapeut", keycloakId)
                .header("Authorization", "Bearer " + jwt.getTokenValue())
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                .map(ResponseEntity::ok);
    }

    // returneaza detaliile terapeutului asignat pacientului -> SearchTerapeutService
    @GetMapping("/my-terapeut")
    public Mono<ResponseEntity<Map<String, Object>>> getMyTerapeut(@AuthenticationPrincipal Jwt jwt) {
        String keycloakId = jwt.getSubject();

        return searchTerapeutService.getMyTerapeut(keycloakId, jwt.getTokenValue())
                .map(ResponseEntity::ok);
    }
}
