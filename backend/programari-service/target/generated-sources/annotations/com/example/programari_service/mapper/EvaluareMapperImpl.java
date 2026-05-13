package com.example.programari_service.mapper;

import com.example.programari_service.dto.EvaluareRequestDTO;
import com.example.programari_service.entity.Evaluare;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-05-13T13:22:10+0300",
    comments = "version: 1.6.3, compiler: Eclipse JDT (IDE) 3.46.0.v20260407-0427, environment: Java 21.0.10 (Eclipse Adoptium)"
)
@Component
public class EvaluareMapperImpl implements EvaluareMapper {

    @Override
    public Evaluare toEntity(EvaluareRequestDTO request) {
        if ( request == null ) {
            return null;
        }

        Evaluare.EvaluareBuilder evaluare = Evaluare.builder();

        evaluare.diagnostic( request.diagnostic() );
        evaluare.observatii( request.observatii() );
        evaluare.pacientKeycloakId( request.pacientKeycloakId() );
        evaluare.sedinteRecomandate( request.sedinteRecomandate() );
        evaluare.serviciuRecomandatId( request.serviciuRecomandatId() );
        evaluare.terapeutKeycloakId( request.terapeutKeycloakId() );
        evaluare.tip( request.tip() );

        return evaluare.build();
    }
}
