package com.example.plata.feign;

import com.example.common.dto.ServiciuDTO;
import com.example.plata.config.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "evaluare", configuration = FeignConfig.class)
public interface ServiciuFeignClient {
    @GetMapping("/api/evaluare/servicii/{id}")
    ServiciuDTO getServiciuById(@PathVariable("id") Long id);
}
