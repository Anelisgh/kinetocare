package com.example.plata.feign;

import com.example.common.dto.ProgramareDetaliiDTO;
import com.example.plata.config.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "programare", configuration = FeignConfig.class)
public interface ProgramareFeignClient {
    @GetMapping("/api/programare/{id}")
    ProgramareDetaliiDTO getProgramareById(@PathVariable Long id);
}
