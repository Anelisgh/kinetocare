package com.example.programari_service.client;

import com.example.programari_service.dto.JurnalIstoricDTO;
import com.example.programari_service.dto.PacientKeycloakDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "pacienti-service", url = "http://localhost:8083")
public interface PacientiClient {
    // ia datele pacientului. returneaza keycloakId-ul pacientului
    @GetMapping("/pacient/{id}")
    PacientKeycloakDTO getPacientById(@PathVariable("id") Long id);

    @GetMapping("/jurnal/{pacientId}/istoric")
    List<JurnalIstoricDTO> getIstoricJurnal(@PathVariable("pacientId") Long pacientId);
}