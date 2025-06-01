package com.example.plata.mapper;

import com.example.common.dto.PlataDTO;
import com.example.plata.domain.Plata;
import com.example.plata.feign.PacientFeignClient;
import com.example.plata.feign.ProgramareFeignClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PlataMapper {
    private final ProgramareFeignClient programareClient;
    private final PacientFeignClient pacientClient;

    public PlataDTO toDto(Plata plata) {
        return PlataDTO.builder()
                .id(plata.getId())
                .data(plata.getData())
                .suma(plata.getSuma())
                .starePlata(plata.getStarePlata())
                .programareId(plata.getProgramareId())
                .pacientId(plata.getPacientId())
                .build();
    }

    public Plata toEntity(PlataDTO dto) {
        return Plata.builder()
                .data(dto.getData())
                .suma(dto.getSuma())
                .starePlata(dto.getStarePlata())
                .programareId(dto.getProgramareId())
                .pacientId(dto.getPacientId())
                .build();
    }
}
