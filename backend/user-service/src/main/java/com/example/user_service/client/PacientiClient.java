package com.example.user_service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "pacienti-service", url = "${pacienti.service.url}", configuration = {FeignClientConfig.class, CustomErrorDecoder.class})
public interface PacientiClient {

    // seteaza starea activa a pacientului (dezactivare/reactivare cont)
    @PatchMapping("/pacient/by-keycloak/{keycloakId}/toggle-active")
    void toggleActive(@PathVariable("keycloakId") String keycloakId, @RequestParam("active") boolean active);
}
