package com.example.pacienti_service.client;

import com.example.pacienti_service.dto.ProgramareJurnalDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import java.util.List;

@FeignClient(name = "programari-service", url = "http://localhost:8085", configuration = CustomErrorDecoder.class)
public interface ProgramariClient {
    // marcheaza programarea ca are jurnal completat
    @PostMapping("/programari/{id}/mark-jurnal")
    void marcheazaJurnal(@PathVariable("id") Long id);

    // obtine detaliile unei programari (pentru imbunatatirea jurnalului)
    @GetMapping("/programari/{id}/detalii")
    ProgramareJurnalDTO getDetaliiProgramare(@PathVariable("id") Long id);

    // Endpoint batch pentru a lua detaliile mai multor programari in loc de N+1 calls
    @PostMapping("/programari/batch-detalii")
    List<ProgramareJurnalDTO> getProgramariBatch(@RequestBody List<Long> programareIds);

    // Endpoint pentru a anula programarile vechi cand se schimba terapeutul preferat
    @DeleteMapping("/programari/cancel-upcoming/pacient-keycloak/{pacientKeycloakId}/terapeut-keycloak/{terapeutKeycloakId}")
    void anuleazaProgramariCuTerapeut(@PathVariable("pacientKeycloakId") String pacientKeycloakId, @PathVariable("terapeutKeycloakId") String terapeutKeycloakId);
}