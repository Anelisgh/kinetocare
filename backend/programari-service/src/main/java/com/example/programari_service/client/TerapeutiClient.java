package com.example.programari_service.client;

import com.example.programari_service.dto.DisponibilitateDTO;
import com.example.programari_service.dto.LocatieDisponibilaDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Map;

@FeignClient(name = "terapeuti-service", url = "http://localhost:8084", configuration = CustomErrorDecoder.class)
public interface TerapeutiClient {

    // ia orarul specific al terapeutului. folosit la calculul sloturilor disponibile
    @GetMapping("/disponibilitate/terapeut/{terapeutId}/locatie/{locatieId}/zi/{zi}")
    DisponibilitateDTO getOrar(@PathVariable("terapeutId") Long terapeutId,
            @PathVariable("locatieId") Long locatieId,
            @PathVariable("zi") Integer zi);

    // verifica daca terapeutul e in concediu. folosit la calculul sloturilor disponibile
    @GetMapping("/concediu/check/terapeut/{terapeutId}/data/{data}")
    Boolean checkConcediu(@PathVariable("terapeutId") Long terapeutId,
            @PathVariable("data") String data);

    // ia numele locatiei. folosit la afisarea locatiei in calendar/jurnal
    @GetMapping("/locatii/{id}")
    LocatieDisponibilaDTO getLocatieById(@PathVariable("id") Long id);

    // [STATISTICI] ia toate locatiile pentru a mapa id -> nume eficient
    @GetMapping("/locatii")
    java.util.List<LocatieDisponibilaDTO> getLocatii();

    // converteste terapeutId in keycloakId. folosit ca bridge: programarea stocheaza terapeutId, dar user-service are nevoie de keycloakId pentru a obtine numele
    @GetMapping("/terapeut/id/{terapeutId}/keycloak-id")
    String getKeycloakIdByTerapeutId(@PathVariable("terapeutId") Long terapeutId);

    // keycloakId -> TerapeutDTO (contine id-ul intern). folosit la admin cancel
    @GetMapping("/terapeut/by-keycloak/{keycloakId}")
    Map<String, Object> getTerapeutByKeycloakId(@PathVariable("keycloakId") String keycloakId);
}
