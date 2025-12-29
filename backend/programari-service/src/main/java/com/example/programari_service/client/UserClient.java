package com.example.programari_service.client;

import com.example.programari_service.dto.UserDisplayCalendarDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "user-service", url = "http://localhost:8082") // Verifică portul
public interface UserClient {
    // Folosim endpoint-ul existent, dar mapăm doar câmpurile care ne interesează în
    // DTO
    @GetMapping("/users/by-keycloak/{keycloakId}")
    UserDisplayCalendarDTO getUserByKeycloakId(@PathVariable("keycloakId") String keycloakId);

    // Lookup by user ID (programari stores user.id as pacient_id)
    @GetMapping("/users/{id}")
    UserDisplayCalendarDTO getUserById(@PathVariable("id") Long id);
}