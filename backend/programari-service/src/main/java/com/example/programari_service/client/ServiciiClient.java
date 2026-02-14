package com.example.programari_service.client;

import com.example.programari_service.dto.DetaliiServiciuDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "servicii-service", url = "http://localhost:8086")
public interface ServiciiClient {
    // extrage detaliile unui serviciu (nume, pret, durata)
    @GetMapping("/servicii/{id}")
    DetaliiServiciuDTO getServiciuById(@PathVariable("id") Long id);

    // cauta un serviciu dupa nume
    @GetMapping("/servicii/search")
    DetaliiServiciuDTO gasesteServiciuDupaNume(@RequestParam("nume") String nume);
}