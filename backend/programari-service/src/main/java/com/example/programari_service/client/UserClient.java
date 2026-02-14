package com.example.programari_service.client;

import com.example.programari_service.dto.UserDisplayCalendarDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "user-service", url = "http://localhost:8082")
public interface UserClient {
    // folosim endpoint-ul existent, dar mapam doar campurile care ne intereseaza in DTO
    // afiseaza nume, prenume, telefon dupa keycloakId in jurnalul pacientului
    @GetMapping("/users/by-keycloak/{keycloakId}")
    UserDisplayCalendarDTO getUserByKeycloakId(@PathVariable("keycloakId") String keycloakId);

    // reminder ca am stocat pacientId = user.id
    // afiseaza doar numele in calendar
    @GetMapping("/users/{id}")
    UserDisplayCalendarDTO getUserById(@PathVariable("id") Long id);
}