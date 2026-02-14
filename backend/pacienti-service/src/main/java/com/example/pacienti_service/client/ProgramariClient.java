package com.example.pacienti_service.client;

import com.example.pacienti_service.dto.ProgramareJurnalDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "programari-service", url = "http://localhost:8085")
public interface ProgramariClient {
    // marcheaza programarea ca are jurnal completat
    @PostMapping("/programari/{id}/mark-jurnal")
    void marcheazaJurnal(@PathVariable("id") Long id);

    // obtine detaliile unei programari (pentru imbunatatirea jurnalului)
    @GetMapping("/programari/{id}/detalii")
    ProgramareJurnalDTO getDetaliiProgramare(@PathVariable("id") Long id);
}