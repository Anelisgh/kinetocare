package com.example.user.mapper;

import com.example.common.dto.TerapeutDTO;
import com.example.user.domain.Terapeut;
import org.springframework.stereotype.Component;

@Component
public class TerapeutMapper {
    public TerapeutDTO toDTO(Terapeut terapeut) {
        return new TerapeutDTO(
                terapeut.getId(),
                terapeut.getNume(),
                terapeut.getTelefon(),
                terapeut.getCnp(),
                terapeut.getDataNastere()
        );
    }
}
