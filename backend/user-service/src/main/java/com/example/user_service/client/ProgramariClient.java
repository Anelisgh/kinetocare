package com.example.user_service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "programari-service", url = "${programari.service.url}", configuration = {FeignClientConfig.class, CustomErrorDecoder.class})
public interface ProgramariClient {

    // anuleaza programarile viitoare ale unui terapeut (dezactivare cont)
    @PatchMapping("/programari/admin/cancel-by-terapeut")
    void cancelByTerapeut(@RequestParam("keycloakId") String keycloakId);

    // anuleaza programarile viitoare ale unui pacient (dezactivare cont)
    @PatchMapping("/programari/admin/cancel-by-pacient")
    void cancelByPacient(@RequestParam("keycloakId") String keycloakId);
}
