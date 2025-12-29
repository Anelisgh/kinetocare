package com.example.api_gateway.service;

import com.example.api_gateway.utils.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProfileService {

    private final WebClient.Builder webClientBuilder;
    private final SecurityUtils securityUtils;

    private static final String USER_SERVICE_URL = "http://localhost:8082";
    private static final String PACIENT_SERVICE_URL = "http://localhost:8083";
    private static final String TERAPEUT_SERVICE_URL = "http://localhost:8084";

    // obtine datele din user-service
    public Mono<Map<String, Object>> getProfile(String keycloakId, String role) {
        return securityUtils.getJwtToken().flatMap(token -> {
            // extragem datele de baza pentru orice user
            Mono<Map<String, Object>> userDataMono = webClientBuilder.build()
                    .get()
                    .uri(USER_SERVICE_URL + "/users/by-keycloak/" + keycloakId)
                    .header("Authorization", "Bearer " + token) // ✅ Adăugăm token-ul
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                    .onErrorResume(e -> {
                        log.error("Error fetching user data for keycloakId: {}", keycloakId, e);
                        return Mono.error(new RuntimeException("User not found in UserService"));
                    });

        // daca e pacient obtine datele si din pacienti-service
            if ("PACIENT".equals(role)) {
                Mono<Map<String, Object>> pacientDataMono = webClientBuilder.build()
                        .get()
                        .uri(PACIENT_SERVICE_URL + "/pacient/by-keycloak/" + keycloakId)
                        .header("Authorization", "Bearer " + token)
                        .retrieve()
                        .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                        .onErrorResume(WebClientResponseException.NotFound.class, e -> {
                            log.warn("Patient profile not found (404) for keycloakId: {}", keycloakId);
                            Map<String, Object> emptyProfile = new HashMap<>();
                            emptyProfile.put("profileIncomplete", true);
                            return Mono.just(emptyProfile);
                        })
                        .onErrorResume(e -> {
                            // pentru orice alta eroare
                            log.error("CRITICAL ERROR connecting to Pacient Service for user: {}", keycloakId, e);
                            // frontend-ul primeste 500
                            return Mono.error(e);
                        });

            // combinam ambele maps prin zip
            return Mono.zip(userDataMono, pacientDataMono)
                    .flatMap(tuple -> {
                        Map<String, Object> userData = tuple.getT1();
                        Map<String, Object> pacientData = tuple.getT2();

                        Boolean isIncomplete = (Boolean) pacientData.get("profileIncomplete");

                        // excludem dublicatele
                        pacientData.remove("keycloakId");
                        pacientData.remove("id");
                        userData.putAll(pacientData);

                        if (Boolean.TRUE.equals(isIncomplete)) {
                            userData.put("profileIncomplete", true);
                            return Mono.just(userData);
                        }

                        // verifica daca are terapeut si ii extrage datele
                        String terapeutId = (String) pacientData.get("terapeutKeycloakId");
                        Mono<Map<String, Object>> terapeutDetailsMono = Mono.just(Map.of()); // default gol

                        if (terapeutId != null && !terapeutId.isEmpty()) {
                            // 1. Luăm detalii profesionale (Specializare, ID Terapeut)
                            Mono<Map<String, Object>> infoTerapeut = webClientBuilder.build()
                                    .get()
                                    .uri(TERAPEUT_SERVICE_URL + "/terapeut/by-keycloak/" + terapeutId)
                                    .header("Authorization", "Bearer " + token)
                                    .retrieve()
                                    .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                                    .onErrorResume(e -> Mono.just(new HashMap<>()));

                            // 2. Luăm detalii personale (Nume, Prenume) din User Service
                            Mono<Map<String, Object>> infoUserTerapeut = webClientBuilder.build()
                                    .get()
                                    .uri(USER_SERVICE_URL + "/users/by-keycloak/" + terapeutId)
                                    .header("Authorization", "Bearer " + token)
                                    .retrieve()
                                    .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                                    .onErrorResume(e -> Mono.just(new HashMap<>()));

                            // 3. Le combinăm
                            terapeutDetailsMono = Mono.zip(infoTerapeut, infoUserTerapeut)
                                    .map(terapeutTuple -> {
                                        Map<String, Object> profesional = terapeutTuple.getT1();
                                        Map<String, Object> personal = terapeutTuple.getT2();

                                        Map<String, Object> merged = new HashMap<>(profesional);
                                        merged.put("nume", personal.get("nume"));
                                        merged.put("prenume", personal.get("prenume"));
                                        return merged;
                                    });
                        }

                        // pregatim cererea pentru detaliile locatiei preferate
                        Long locatieId = null;
                        if (pacientData.get("locatiePreferataId") instanceof Number) {
                            locatieId = ((Number) pacientData.get("locatiePreferataId")).longValue();
                        }
                        Mono<Map<String, Object>> locatieDetailsMono = Mono.just(Map.of()); // default gol

                        if (locatieId != null) {
                            locatieDetailsMono = webClientBuilder.build()
                                    .get()
                                    .uri(TERAPEUT_SERVICE_URL + "/locatii/" + locatieId)
                                    .header("Authorization", "Bearer " + token)
                                    .retrieve()
                                    .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                                    .onErrorResume(e -> {
                                        log.warn("Could not fetch locatie details", e);
                                        return Mono.just(Map.of());
                                    });
                        }

                        // executam ambele cereri in paralel si combinam rezultatele
                        return Mono.zip(terapeutDetailsMono, locatieDetailsMono)
                                .map(detailsTuple -> {
                                    Map<String, Object> tDetails = detailsTuple.getT1();
                                    Map<String, Object> lDetails = detailsTuple.getT2();
                                    // daca gasim terapeutul, il punem in map
                                    if (!tDetails.isEmpty()) {
                                        userData.put("terapeutDetalii", tDetails);
                                    }
                                    // daca gasim locatia, o punem in map
                                    if (!lDetails.isEmpty()) {
                                        userData.put("locatieDetalii", lDetails);
                                    }

                                    return userData;
                                });
                    });
        }
            // daca e terapeut, obtine datele din terapeut-service
            if ("TERAPEUT".equals(role)) {
                Mono<Map<String, Object>> terapeutDataMono = webClientBuilder.build()
                        .get()
                        .uri(TERAPEUT_SERVICE_URL + "/terapeut/by-keycloak/" + keycloakId)
                        .header("Authorization", "Bearer " + token)
                        .retrieve()
                        .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                        .onErrorResume(e -> {
                            log.warn("Terapeut profile not found for keycloakId: {} (may be incomplete profile)", keycloakId);
                            Map<String, Object> emptyProfile = new HashMap<>();
                            emptyProfile.put("profileIncomplete", true);
                            return Mono.just(emptyProfile);
                        });

                return Mono.zip(userDataMono, terapeutDataMono)
                        .map(terapeutTuple -> {
                            Map<String, Object> userData = terapeutTuple.getT1();
                            Map<String, Object> terapeutData = terapeutTuple.getT2();

                            Boolean isIncomplete = (Boolean) terapeutData.get("profileIncomplete");
// il salvam inainte sa ii dam remove
                            if (terapeutData.containsKey("id")) {
                                userData.put("terapeutId", terapeutData.get("id"));
                            }

                            // Excludem dublicatele
                            terapeutData.remove("keycloakId");
                            terapeutData.remove("id");

                            userData.putAll(terapeutData);

                            if (Boolean.TRUE.equals(isIncomplete)) {
                                userData.put("profileIncomplete", true);
                            }
                            return userData;
                        });
            }
            // returneaza doar datele din user
            return userDataMono;
    });
    }

    public Mono<Map<String, Object>> updateProfile(String keycloakId, String role, Map<String, Object> updateData) {
        log.info("Updating profile for keycloakId: {} with role: {}", keycloakId, role);
        return securityUtils.getJwtToken().flatMap(token -> {
            // Separam campurile
            Map<String, Object> userUpdate = extractUserFields(updateData);
            Map<String, Object> pacientUpdate = extractPacientFields(updateData);
            Map<String, Object> terapeutUpdate = extractTerapeutFields(updateData);

            // Lista de operațiuni de update
            List<Mono<Void>> updates = new ArrayList<>();

            // Update pentru user-service
            if (!userUpdate.isEmpty()) {
                log.debug("Updating user fields: {}", userUpdate.keySet());
                Mono<Void> userUpdateMono = webClientBuilder.build()
                        .patch()
                        .uri(USER_SERVICE_URL + "/users/" + keycloakId)
                        .header("Authorization", "Bearer " + token)
                        .bodyValue(userUpdate)
                        .retrieve()
                        .bodyToMono(Void.class)
                        .doOnSuccess(v -> log.info("User updated successfully for keycloakId: {}", keycloakId))
                        .onErrorResume(e -> {
                            log.error("Failed to update user for keycloakId: {}", keycloakId, e);
                            return Mono.error(new RuntimeException("Failed to update user: " + e.getMessage()));
                        });
                updates.add(userUpdateMono);
            }

            // Update/Create pentru pacienti
            if ("PACIENT".equals(role) && !pacientUpdate.isEmpty()) {
                log.debug("Updating patient fields: {}", pacientUpdate.keySet());
                Mono<Void> pacientUpdateMono = webClientBuilder.build()
                        .patch()
                        .uri(PACIENT_SERVICE_URL + "/pacient/" + keycloakId)
                        .header("Authorization", "Bearer " + token)
                        .bodyValue(pacientUpdate)
                        .retrieve()
                        .bodyToMono(Void.class)
                        .doOnSuccess(v -> log.info("Patient profile updated successfully for keycloakId: {}", keycloakId))
                        .onErrorResume(e -> {
                            log.error("Patient profile not found for keycloakId: {}", keycloakId, e);
                            return Mono.error(new RuntimeException("Profilul de pacient nu există"));
                        });
                updates.add(pacientUpdateMono);
            }

            // update pentru terapeuti
            if ("TERAPEUT".equals(role) && !terapeutUpdate.isEmpty()) {
                log.debug("Updating terapeut fields: {}", terapeutUpdate.keySet());
                Mono<Void> terapeutUpdateMono = webClientBuilder.build()
                        .patch()
                        .uri(TERAPEUT_SERVICE_URL + "/terapeut/" + keycloakId)
                        .header("Authorization", "Bearer " + token)
                        .bodyValue(terapeutUpdate)
                        .retrieve()
                        .bodyToMono(Void.class)
                        .doOnSuccess(v -> log.info("Terapeut profile updated successfully for keycloakId: {}", keycloakId))
                        .onErrorResume(e -> {
                            log.error("Terapeut profile not found for keycloakId: {}", keycloakId, e);
                            return Mono.error(new RuntimeException("Profilul de terapeut nu există"));
                        });
                updates.add(terapeutUpdateMono);
            }

            // Dacă nu avem ce actualiza, returnăm direct profilul
            if (updates.isEmpty()) {
                log.debug("No updates to perform for keycloakId: {}", keycloakId);
                return getProfile(keycloakId, role);
            }

            // Executăm toate update-urile și apoi returnăm profilul actualizat
            return Mono.when(updates)
                    .then(getProfile(keycloakId, role))
                    .doOnSuccess(profile -> log.info("Profile update completed for keycloakId: {}", keycloakId))
                    .onErrorResume(e -> {
                        log.error("Error in update process for keycloakId: {}", keycloakId, e);
                        return Mono.error(new RuntimeException("Failed to complete update: " + e.getMessage()));
                    });
        });
    }

    private Map<String, Object> extractUserFields(Map<String, Object> data) {
        Map<String, Object> userFields = new HashMap<>();
        if (data.containsKey("nume")) userFields.put("nume", data.get("nume"));
        if (data.containsKey("prenume")) userFields.put("prenume", data.get("prenume"));
        if (data.containsKey("gen")) userFields.put("gen", data.get("gen"));
        if (data.containsKey("email")) userFields.put("email", data.get("email"));
        if (data.containsKey("telefon")) userFields.put("telefon", data.get("telefon"));
        return userFields;
    }

    private Map<String, Object> extractPacientFields(Map<String, Object> data) {
        Map<String, Object> pacientFields = new HashMap<>();
        if (data.containsKey("dataNasterii")) pacientFields.put("dataNasterii", data.get("dataNasterii"));
        if (data.containsKey("cnp")) pacientFields.put("cnp", data.get("cnp"));
        if (data.containsKey("faceSport")) pacientFields.put("faceSport", data.get("faceSport"));
        if (data.containsKey("detaliiSport")) pacientFields.put("detaliiSport", data.get("detaliiSport"));
        if (data.containsKey("orasPreferat")) pacientFields.put("orasPreferat", data.get("orasPreferat"));
        if (data.containsKey("locatiePreferataId")) pacientFields.put("locatiePreferataId", data.get("locatiePreferataId"));
        if (data.containsKey("terapeutKeycloakId")) pacientFields.put("terapeutKeycloakId", data.get("terapeutKeycloakId"));
        return pacientFields;
    }

    private Map<String, Object> extractTerapeutFields(Map<String, Object> data) {
        Map<String, Object> terapeutFields = new HashMap<>();
        if (data.containsKey("specializare")) terapeutFields.put("specializare", data.get("specializare"));
        if (data.containsKey("pozaProfil")) terapeutFields.put("pozaProfil", data.get("pozaProfil"));
        return terapeutFields;
    }
}
