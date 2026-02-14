package com.example.api_gateway.controller;

import com.example.api_gateway.dto.KeycloakTokenResponse;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.http.ResponseCookie;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.Map;

@Controller
public class KeycloakProxyController {

    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${spring.security.oauth2.resourceserver.jwt.issuer-uri}")
    private String keycloakIssuerUri;

    @Value("${keycloak.client-id}")
    private String clientId;

    private String getTokenEndpoint() {
        return keycloakIssuerUri + "/protocol/openid-connect/token";
    }

    private ResponseCookie createCookie(String name, String value, Duration maxAge) {
        return ResponseCookie.from(name, value)
                .httpOnly(true)
                .secure(false) // se schimba cu true in productie -> otherwise in dezvoltare login-ul ar esua (are nevoie de https, nu de http-ul din localhost)
                .path("/")
                .maxAge(Duration.ofDays(7))
                .sameSite("Lax") // in ce situatii are voie sa trimite cookies catre server -> previne atacurile evidente . (3 variante in functie de severitate: strict, lax, none)
                .build();
    } // in productie se foloseste secure true impreuna cu sameSite none.

    // proxy login/refresh -> trimite cererea catre Keycloak si salveaza refresh_token in cookie httpOnly
    @PostMapping("/api/auth/token")
    public Mono<ResponseEntity<?>> handleTokenRequest(ServerWebExchange exchange) {
        return exchange.getFormData() // extrage campurile trimise in body
                .flatMap(formData -> { // flatMap -> lanseaza o actiune dupa ce datele sunt gata
                    // daca grantType e gol -> error bad request
                    String grantType = formData.getFirst("grant_type");
                    if (grantType == null || grantType.isEmpty()) {
                        return buildError(HttpStatus.BAD_REQUEST, "grant_type missing");
                    }
                    // copiam datele si adaugam client_id ('react_client' din keycloak)
                    MultiValueMap<String, String> keycloakParams = new LinkedMultiValueMap<>(formData);
                    keycloakParams.add("client_id", clientId);

                    // daca avem o cerere de tip refresh_token -> extragem cookie-ul cu numele refresh_token
                    if ("refresh_token".equals(grantType)) {
                        HttpCookie refreshCookie = exchange.getRequest().getCookies().getFirst("refresh_token");
                        if (refreshCookie != null) {  // adaugam refresh_token la parametrii trimisi catre keycloak
                            keycloakParams.set("refresh_token", refreshCookie.getValue());
                        } else {
                            return Mono.just(ResponseEntity.status(HttpStatus.UNAUTHORIZED).build());
                        }
                    }

                    HttpHeaders headers = new HttpHeaders();
                    headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED); // Content-Type: application/x-www-form-urlencoded
                    HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(keycloakParams, headers); // grupam body si header

                    try { // POST request catre keycloak
                        ResponseEntity<KeycloakTokenResponse> keycloakResponse = restTemplate.postForEntity(
                                getTokenEndpoint(),
                                request,
                                KeycloakTokenResponse.class
                        );

                        if (keycloakResponse.getStatusCode() == HttpStatus.OK && keycloakResponse.getBody() != null) {
                            // extragem body din raspuns (contine access_token, refresh_token si expires_in)
                            KeycloakTokenResponse body = keycloakResponse.getBody();
                            Duration refreshMaxAge = Duration.ofDays(30);
                            ResponseCookie refreshTokenCookie = createCookie(
                                    "refresh_token",
                                    body.getRefresh_token(),
                                    refreshMaxAge
                            ); // creeaza cookie-ul httpOnly cu refresh token-ul
                            body.setRefresh_token(null); // nu-l trimitem in JSON (pt ca va fi accesibil din JS), ci doar in cookie

                            return Mono.just( // browser-ul va salva automat cookie-ul
                                    ResponseEntity.ok()
                                            .header(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString())
                                            .body(body)
                            );
                        }
                        // aici gestionăm cazurile de credentiale greșite
                        if (keycloakResponse.getStatusCode() == HttpStatus.BAD_REQUEST) {
                            return buildError(HttpStatus.BAD_REQUEST, "Email sau parolă incorectă");
                        }

                        return buildError(HttpStatus.valueOf(keycloakResponse.getStatusCode().value()), "Eroare de la Keycloak");

                    } catch (HttpClientErrorException e) {
                        // de exemplu când Keycloak trimite 401
                        if (e.getStatusCode() == HttpStatus.UNAUTHORIZED) {
                            return buildError(HttpStatus.UNAUTHORIZED, "Email sau parolă incorectă");
                        }
                        return buildError(HttpStatus.INTERNAL_SERVER_ERROR, "Eroare la autentificare");
                    } catch (Exception e) {
                        return buildError(HttpStatus.INTERNAL_SERVER_ERROR, "Eroare internă la procesarea cererii");
                    }
                });
    }

    private Mono<ResponseEntity<Map<String, Object>>> buildError(HttpStatus status, String message) {
        Map<String, Object> body = Map.of(
                "error", status.getReasonPhrase(),
                "message", message
        );
        return Mono.just(ResponseEntity.status(status).contentType(MediaType.APPLICATION_JSON).body(body));
    }

    // logout -> suprascrie cookie-ul refresh_token cu unul expirat
    @PostMapping("/api/auth/logout")
    public Mono<ResponseEntity<Void>> logout(ServerWebExchange exchange) {
        // browser-ul va sterge cookie-ul vechi prin suprascriere
        ResponseCookie expiredCookie = createCookie("refresh_token", "", Duration.ZERO);
        return Mono.just(
                ResponseEntity.ok()
                        .header(HttpHeaders.SET_COOKIE, expiredCookie.toString())
                        .build()
        );
    }
}