package com.example.evaluare.feign;

import com.example.common.dto.*;
import com.example.evaluare.config.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "user", contextId = "terapeutClient", configuration = FeignConfig.class)
public interface TerapeutFeignClient {
    @GetMapping("/api/terapeuti/by-email")
    TerapeutDTO getTerapeutByEmail(@RequestParam String email);
}
