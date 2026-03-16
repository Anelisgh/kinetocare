package com.example.user_service.controller;

import com.example.user_service.dto.ForgotPasswordRequestDTO;
import com.example.user_service.dto.RegisterRequestDTO;
import com.example.user_service.dto.RegisterResponseDTO;
import com.example.user_service.service.KeycloakService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final KeycloakService keycloakService;

    // nu avem login pentru ca se ocupa keycloak
    // folosit in authService.js
    @PostMapping("/register")
    public ResponseEntity<RegisterResponseDTO> register(@Valid @RequestBody RegisterRequestDTO request) {
        log.info("Incercare inregistrare pentru email: {}", request.email());
        RegisterResponseDTO response = keycloakService.registerUser(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    } // nu mai verificam daca email-ul deja exista, pentru ca keycloak nu accepta 2
      // users cu acelasi username (in cazul nostru mail-ul)

    // trimite mail de resetare parola pt useri neautentificati
    // folosit in authService.js (ForgotPasswordModal)
    @PostMapping("/forgot-password")
    public ResponseEntity<Void> forgotPassword(@Valid @RequestBody ForgotPasswordRequestDTO request) {
        log.info("Cerere resetare parola pentru email: {}", request.email());
        keycloakService.sendForgotPasswordEmail(request.email());
        return ResponseEntity.noContent().build();
    }
}
