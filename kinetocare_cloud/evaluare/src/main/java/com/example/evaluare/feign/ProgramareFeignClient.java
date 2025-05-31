package com.example.evaluare.feign;

import com.example.common.dto.ProgramareDetaliiDTO;
import com.example.evaluare.config.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.List;

@FeignClient(name = "programare", configuration = FeignConfig.class)
public interface ProgramareFeignClient {
    @GetMapping("/api/programare/pacient/{pacientId}/completed")
    List<ProgramareDetaliiDTO> getCompletedSessionsAfterDate(
            @PathVariable Long pacientId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate);
}
