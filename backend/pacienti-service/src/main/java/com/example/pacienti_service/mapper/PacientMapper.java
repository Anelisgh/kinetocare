package com.example.pacienti_service.mapper;

import com.example.pacienti_service.dto.PacientCompleteProfileRequest;
import com.example.pacienti_service.dto.PacientRequest;
import com.example.pacienti_service.dto.PacientResponse;
import com.example.pacienti_service.entity.Pacient;
import com.example.pacienti_service.entity.FaceSport;
import org.springframework.stereotype.Component;
import java.util.Objects;

@Component
public class PacientMapper {

    public Pacient toEntity(PacientCompleteProfileRequest request, String keycloakId) {
        return Pacient.builder()
                .keycloakId(keycloakId)
                .dataNasterii(request.getDataNasterii())
                .cnp(request.getCnp())
                .faceSport(request.getFaceSport())
                .detaliiSport(request.getFaceSport() == FaceSport.DA ? request.getDetaliiSport() : null)
                .orasPreferat(null)
                .locatiePreferataId(null)
                .terapeutKeycloakId(null)
                .build();
    }

    public PacientResponse toResponse(Pacient pacient) {
        return new PacientResponse(
                pacient.getId(),
                pacient.getKeycloakId(),
                pacient.getDataNasterii(),
                pacient.getCnp(),
                pacient.getFaceSport(),
                pacient.getDetaliiSport(),
                pacient.getOrasPreferat(),
                pacient.getLocatiePreferataId(),
                pacient.getTerapeutKeycloakId()
        );
    }

    public void updateEntity(Pacient pacient, PacientRequest request) {
        if (request.getDataNasterii() != null) {
            pacient.setDataNasterii(request.getDataNasterii());
        }
        if (request.getCnp() != null) {
            pacient.setCnp(request.getCnp());
        }

        if (request.getFaceSport() != null) {
            pacient.setFaceSport(request.getFaceSport());

            if (request.getFaceSport() == FaceSport.NU) {
                pacient.setDetaliiSport(null);
            } else if (request.getDetaliiSport() != null) {
                pacient.setDetaliiSport(request.getDetaliiSport());
            }
        }

        if (request.getDetaliiSport() != null) {
            pacient.setDetaliiSport(request.getDetaliiSport());
        }

        if (Objects.nonNull(request.getOrasPreferat())) {
            pacient.setOrasPreferat(request.getOrasPreferat());
        }
        if (Objects.nonNull(request.getLocatiePreferataId())) {
            pacient.setLocatiePreferataId(request.getLocatiePreferataId());
        }
        if (Objects.nonNull(request.getTerapeutKeycloakId())) {
            pacient.setTerapeutKeycloakId(request.getTerapeutKeycloakId());
        }
    }
}