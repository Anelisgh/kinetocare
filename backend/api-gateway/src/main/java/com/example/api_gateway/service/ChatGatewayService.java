package com.example.api_gateway.service;

import com.example.api_gateway.dto.ConversatieAgregataDTO;
import com.example.api_gateway.dto.UserDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ChatGatewayService {

    private final WebClient chatWebClient;
    private final WebClient userWebClient;
    private final WebClient programariWebClient;

    public ChatGatewayService(
            @Qualifier("chatWebClient") WebClient chatWebClient,
            @Qualifier("userWebClient") WebClient userWebClient,
            @Qualifier("programariWebClient") WebClient programariWebClient) {
        this.chatWebClient = chatWebClient;
        this.userWebClient = userWebClient;
        this.programariWebClient = programariWebClient;
    }

    /**
     * BFF endpoint — agregă conversații din chat-service cu:
     * 1. Parteneri activi din programari-service (chiar fără mesaje istorice)
     * 2. Numele partenerilor din user-service (keycloakId → Nume, UN singur hop)
     * 3. Statusul relației (activ/arhivat) din programari-service
     *
     * userKeycloakId = keycloakId al utilizatorului curent (uniform pentru toți)
     */
    public Mono<List<ConversatieAgregataDTO>> getConversatiiAgregate(String userKeycloakId, String tipUser, String token) {

        // 1. Conversații existente din chat-service (acum cu keycloakId)
        Mono<List<Map<String, Object>>> conversatiiMono = chatWebClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/chat/conversatii")
                        .queryParam("userKeycloakId", userKeycloakId)
                        .queryParam("tipUser", tipUser)
                        .build())
                .header("Authorization", "Bearer " + token)
                .retrieve()
                .bodyToFlux(new ParameterizedTypeReference<Map<String, Object>>() {})
                .collectList();

        // 2. Parteneri activi din programari-service
        Mono<List<String>> parteneriActiviMono = programariWebClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/relatii/parteneri-activi-keycloak")
                        .queryParam("userKeycloakId", userKeycloakId)
                        .queryParam("tipUser", tipUser)
                        .build())
                .header("Authorization", "Bearer " + token)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<String>>() {})
                .onErrorResume(e -> {
                    log.warn("Nu au putut fi obtinuti partenerii activi pentru keycloakId={}", userKeycloakId, e);
                    return Mono.just(List.of());
                });

        return Mono.zip(conversatiiMono, parteneriActiviMono).flatMap(tuple -> {
            List<Map<String, Object>> conversatiiBruteFromDb = tuple.getT1();
            List<String> parteneriActivi = tuple.getT2();

            List<Map<String, Object>> toateConversatiile = new ArrayList<>(conversatiiBruteFromDb);

            // Partenerii cu care avem deja conversații
            Set<String> parteneriInConversatii = conversatiiBruteFromDb.stream()
                    .map(conv -> {
                        String key = "PACIENT".equalsIgnoreCase(tipUser) ? "terapeutKeycloakId" : "pacientKeycloakId";
                        return (String) conv.get(key);
                    })
                    .filter(java.util.Objects::nonNull)
                    .collect(Collectors.toSet());

            // Conversații virtuale (Lazy Init) pentru parteneri activi fără istoric
            for (String partenerKeycloakId : parteneriActivi) {
                if (!parteneriInConversatii.contains(partenerKeycloakId)) {
                    Map<String, Object> virtualConv = new HashMap<>();
                    virtualConv.put("id", null);
                    if ("PACIENT".equalsIgnoreCase(tipUser)) {
                        virtualConv.put("pacientKeycloakId", userKeycloakId);
                        virtualConv.put("terapeutKeycloakId", partenerKeycloakId);
                    } else {
                        virtualConv.put("pacientKeycloakId", partenerKeycloakId);
                        virtualConv.put("terapeutKeycloakId", userKeycloakId);
                    }
                    virtualConv.put("ultimulMesaj", null);
                    toateConversatiile.add(virtualConv);
                }
            }

            if (toateConversatiile.isEmpty()) {
                return Mono.just(List.<ConversatieAgregataDTO>of());
            }

            // keycloakId-urile partenerilor (toți în același spațiu de ID — SIMPLIFICAT!)
            List<String> partenerKeycloakIds = toateConversatiile.stream()
                    .map(conv -> {
                        String key = "PACIENT".equalsIgnoreCase(tipUser) ? "terapeutKeycloakId" : "pacientKeycloakId";
                        return (String) conv.get(key);
                    })
                    .filter(java.util.Objects::nonNull)
                    .distinct()
                    .collect(Collectors.toList());

            if (partenerKeycloakIds.isEmpty()) {
                return processConversations(toateConversatiile, Map.of(), tipUser, token);
            }

            // ✅ UN SINGUR HOP: keycloakId → Nume, indiferent că e pacient sau terapeut
            Mono<Map<String, String>> keycloakToNameMono = userWebClient.post()
                    .uri("/users/batch")
                    .header("Authorization", "Bearer " + token)
                    .bodyValue(partenerKeycloakIds)
                    .retrieve()
                    .bodyToFlux(UserDTO.class)
                    .collectMap(UserDTO::keycloakId, u -> u.nume() + " " + u.prenume())
                    .onErrorResume(e -> {
                        log.error("Eroare la preluarea numelor pentru keycloakIds: {}", partenerKeycloakIds, e);
                        return Mono.just(Map.of());
                    });

            return keycloakToNameMono.flatMap(keycloakToName ->
                    processConversations(toateConversatiile, keycloakToName, tipUser, token)
            );
        });
    }

    private Mono<List<ConversatieAgregataDTO>> processConversations(
            List<Map<String, Object>> conversatiiBrute,
            Map<String, String> keycloakToName,
            String tipUser,
            String token) {

        return Flux.fromIterable(conversatiiBrute)
                .flatMap(conv -> {
                    Number convIdRaw = (Number) conv.get("id");
                    Long conversatieId = convIdRaw != null ? convIdRaw.longValue() : null;

                    String pacientKeycloakId = (String) conv.get("pacientKeycloakId");
                    String terapeutKeycloakId = (String) conv.get("terapeutKeycloakId");

                    // Partenerul e cel opus utilizatorului curent
                    String partenerKeycloakId = "PACIENT".equalsIgnoreCase(tipUser)
                            ? terapeutKeycloakId : pacientKeycloakId;

                    // Lookup direct — același spațiu de ID pentru toți
                    String partenerNume = keycloakToName.getOrDefault(partenerKeycloakId, "Necunoscut");

                    // Verifică statusul relației în programari-service
                    return programariWebClient.get()
                            .uri(uriBuilder -> uriBuilder
                                    .path("/relatii/status-keycloak")
                                    .queryParam("pacientKeycloakId", pacientKeycloakId)
                                    .queryParam("terapeutKeycloakId", terapeutKeycloakId)
                                    .build())
                            .header("Authorization", "Bearer " + token)
                            .retrieve()
                            .bodyToMono(Boolean.class)
                            .onErrorResume(e -> {
                                log.warn("Eroare la verificarea statusului relației pac={} ter={}", pacientKeycloakId, terapeutKeycloakId);
                                return Mono.just(false);
                            })
                            .map(isActiva -> {
                                @SuppressWarnings("unchecked")
                                Map<String, Object> ultimulMesaj = (Map<String, Object>) conv.get("ultimulMesaj");

                                return new ConversatieAgregataDTO(
                                        conversatieId,
                                        partenerKeycloakId,
                                        partenerNume,
                                        ultimulMesaj,
                                        !isActiva // isArhivat = !isActiva
                                );
                            });
                })
                .collectList();
    }
}
