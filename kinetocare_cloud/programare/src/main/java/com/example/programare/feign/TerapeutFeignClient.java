package com.example.programare.feign;

import com.example.common.dto.TerapeutDTO;
import com.example.programare.config.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "user", contextId = "terapeutClientInProgramare", configuration = FeignConfig.class)
public interface TerapeutFeignClient {
    @GetMapping("/api/terapeuti/by-email")
    TerapeutDTO getTerapeutByEmail(@RequestParam String email);
    @GetMapping("/api/terapeuti/{id}")
    TerapeutDTO getTerapeutById(@PathVariable Long id);
}
