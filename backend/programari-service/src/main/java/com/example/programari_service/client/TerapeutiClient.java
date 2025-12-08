package com.example.programari_service.client;

import com.example.programari_service.dto.DisponibilitateDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "terapeuti-service", url = "http://localhost:8084")
public interface TerapeutiClient {

    @GetMapping("/disponibilitate/terapeut/{terapeutId}/locatie/{locatieId}/zi/{zi}")
    DisponibilitateDTO getOrar(@PathVariable("terapeutId") Long terapeutId,
                               @PathVariable("locatieId") Long locatieId,
                               @PathVariable("zi") Integer zi);

    @GetMapping("/concediu/check/terapeut/{terapeutId}/data/{data}")
    Boolean checkConcediu(@PathVariable("terapeutId") Long terapeutId,
                          @PathVariable("data") String data);
}
