package com.example.evolutie.feign;

import com.example.common.dto.PacientDTO;
import com.example.evolutie.config.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "user", contextId = "pacientClientInEvolutie", configuration = FeignConfig.class)
public interface PacientFeignClient {

    @GetMapping("/api/pacienti")
    List<PacientDTO> getAllPacienti();

    @GetMapping("/api/pacienti/{id}")
    PacientDTO getPacientById(@PathVariable Long id);
}


