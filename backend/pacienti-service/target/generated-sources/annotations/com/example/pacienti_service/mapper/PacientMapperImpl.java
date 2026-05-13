package com.example.pacienti_service.mapper;

import com.example.pacienti_service.dto.PacientCompleteProfileRequest;
import com.example.pacienti_service.dto.PacientRequest;
import com.example.pacienti_service.dto.PacientResponse;
import com.example.pacienti_service.entity.FaceSport;
import com.example.pacienti_service.entity.Pacient;
import java.time.LocalDate;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-05-13T13:22:07+0300",
    comments = "version: 1.6.3, compiler: Eclipse JDT (IDE) 3.46.0.v20260407-0427, environment: Java 21.0.10 (Eclipse Adoptium)"
)
@Component
public class PacientMapperImpl implements PacientMapper {

    @Override
    public Pacient toEntity(PacientCompleteProfileRequest request, String keycloakId) {
        if ( request == null && keycloakId == null ) {
            return null;
        }

        Pacient.PacientBuilder pacient = Pacient.builder();

        if ( request != null ) {
            pacient.cnp( request.cnp() );
            pacient.dataNasterii( request.dataNasterii() );
            pacient.detaliiSport( request.detaliiSport() );
            pacient.faceSport( request.faceSport() );
        }
        pacient.keycloakId( keycloakId );

        Pacient pacientResult = pacient.build();

        handleDetaliiSport( pacientResult );

        return pacientResult;
    }

    @Override
    public PacientResponse toResponse(Pacient pacient) {
        if ( pacient == null ) {
            return null;
        }

        Long id = null;
        String keycloakId = null;
        LocalDate dataNasterii = null;
        String cnp = null;
        FaceSport faceSport = null;
        String detaliiSport = null;
        String orasPreferat = null;
        Long locatiePreferataId = null;
        String terapeutKeycloakId = null;

        id = pacient.getId();
        keycloakId = pacient.getKeycloakId();
        dataNasterii = pacient.getDataNasterii();
        cnp = pacient.getCnp();
        faceSport = pacient.getFaceSport();
        detaliiSport = pacient.getDetaliiSport();
        orasPreferat = pacient.getOrasPreferat();
        locatiePreferataId = pacient.getLocatiePreferataId();
        terapeutKeycloakId = pacient.getTerapeutKeycloakId();

        PacientResponse pacientResponse = new PacientResponse( id, keycloakId, dataNasterii, cnp, faceSport, detaliiSport, orasPreferat, locatiePreferataId, terapeutKeycloakId );

        return pacientResponse;
    }

    @Override
    public void updateEntity(Pacient pacient, PacientRequest request) {
        if ( request == null ) {
            return;
        }

        if ( request.cnp() != null ) {
            pacient.setCnp( request.cnp() );
        }
        if ( request.dataNasterii() != null ) {
            pacient.setDataNasterii( request.dataNasterii() );
        }
        if ( request.detaliiSport() != null ) {
            pacient.setDetaliiSport( request.detaliiSport() );
        }
        if ( request.faceSport() != null ) {
            pacient.setFaceSport( request.faceSport() );
        }
        if ( request.locatiePreferataId() != null ) {
            pacient.setLocatiePreferataId( request.locatiePreferataId() );
        }
        if ( request.orasPreferat() != null ) {
            pacient.setOrasPreferat( request.orasPreferat() );
        }
        if ( request.terapeutKeycloakId() != null ) {
            pacient.setTerapeutKeycloakId( request.terapeutKeycloakId() );
        }

        handleDetaliiSport( pacient );
    }
}
