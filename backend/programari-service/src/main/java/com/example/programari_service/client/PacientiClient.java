package com.example.programari_service.client;

import com.example.programari_service.dto.JurnalIstoricDTO;
import com.example.programari_service.dto.PacientKeycloakDTO;
import com.example.programari_service.dto.PacientMedicalDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "pacienti-service", url = "${application.urls.pacienti-service:http://localhost:8083}", configuration = CustomErrorDecoder.class)
public interface PacientiClient {
    @GetMapping("/pacient/{id}")
    PacientKeycloakDTO getPacientById(@PathVariable("id") Long id);

    @GetMapping("/jurnal/by-keycloak/{keycloakId}/istoric")
    List<JurnalIstoricDTO> getIstoricJurnal(@PathVariable("keycloakId") String keycloakId);

    @GetMapping("/pacient/by-keycloak/{keycloakId}")
    PacientMedicalDTO getMedicalInfo(@PathVariable("keycloakId") String keycloakId);
}
