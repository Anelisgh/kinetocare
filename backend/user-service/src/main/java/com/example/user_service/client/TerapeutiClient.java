package com.example.user_service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "terapeuti-service", url = "${terapeuti.service.url}", configuration = {FeignClientConfig.class, CustomErrorDecoder.class})
public interface TerapeutiClient {

    // seteaza starea activa a terapeutului (dezactivare/reactivare cont)
    @PatchMapping("/terapeut/by-keycloak/{keycloakId}/toggle-active")
    void toggleActive(@PathVariable("keycloakId") String keycloakId, @RequestParam("active") boolean active);
}
