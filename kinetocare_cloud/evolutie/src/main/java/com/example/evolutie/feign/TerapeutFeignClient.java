package com.example.evolutie.feign;

import com.example.common.dto.TerapeutDTO;
import com.example.evolutie.config.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "user", contextId = "terapeutClientInEvolutie", configuration = FeignConfig.class)
public interface TerapeutFeignClient {
    @GetMapping("/api/terapeuti/by-email")
    TerapeutDTO getTerapeutByEmail(@RequestParam String email);
}
