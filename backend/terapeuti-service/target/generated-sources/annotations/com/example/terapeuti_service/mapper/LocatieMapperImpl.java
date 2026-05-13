package com.example.terapeuti_service.mapper;

import com.example.terapeuti_service.dto.DisponibilitateDTO;
import com.example.terapeuti_service.dto.LocatieDTO;
import com.example.terapeuti_service.dto.LocatieDisponibilaDTO;
import com.example.terapeuti_service.entity.Locatie;
import java.time.OffsetDateTime;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-05-13T13:22:18+0300",
    comments = "version: 1.6.3, compiler: Eclipse JDT (IDE) 3.46.0.v20260407-0427, environment: Java 21.0.10 (Eclipse Adoptium)"
)
@Component
public class LocatieMapperImpl implements LocatieMapper {

    @Override
    public LocatieDTO toDTO(Locatie entity) {
        if ( entity == null ) {
            return null;
        }

        Long id = null;
        String nume = null;
        String adresa = null;
        String oras = null;
        String judet = null;
        String codPostal = null;
        String telefon = null;
        Boolean active = null;
        OffsetDateTime createdAt = null;
        OffsetDateTime updatedAt = null;

        id = entity.getId();
        nume = entity.getNume();
        adresa = entity.getAdresa();
        oras = entity.getOras();
        judet = entity.getJudet();
        codPostal = entity.getCodPostal();
        telefon = entity.getTelefon();
        active = entity.getActive();
        createdAt = entity.getCreatedAt();
        updatedAt = entity.getUpdatedAt();

        LocatieDTO locatieDTO = new LocatieDTO( id, nume, adresa, oras, judet, codPostal, telefon, active, createdAt, updatedAt );

        return locatieDTO;
    }

    @Override
    public Locatie toEntity(LocatieDTO dto) {
        if ( dto == null ) {
            return null;
        }

        Locatie.LocatieBuilder locatie = Locatie.builder();

        locatie.adresa( dto.adresa() );
        locatie.codPostal( dto.codPostal() );
        locatie.judet( dto.judet() );
        locatie.nume( dto.nume() );
        locatie.oras( dto.oras() );
        locatie.telefon( dto.telefon() );

        locatie.active( true );

        return locatie.build();
    }

    @Override
    public void updateEntityFromDTO(Locatie locatie, LocatieDTO dto) {
        if ( dto == null ) {
            return;
        }

        if ( dto.adresa() != null ) {
            locatie.setAdresa( dto.adresa() );
        }
        if ( dto.codPostal() != null ) {
            locatie.setCodPostal( dto.codPostal() );
        }
        if ( dto.judet() != null ) {
            locatie.setJudet( dto.judet() );
        }
        if ( dto.nume() != null ) {
            locatie.setNume( dto.nume() );
        }
        if ( dto.oras() != null ) {
            locatie.setOras( dto.oras() );
        }
        if ( dto.telefon() != null ) {
            locatie.setTelefon( dto.telefon() );
        }
    }

    @Override
    public LocatieDisponibilaDTO toDisponibilaDTO(Locatie locatie) {
        if ( locatie == null ) {
            return null;
        }

        Long id = null;
        String nume = null;
        String adresa = null;
        String oras = null;
        String judet = null;
        Boolean active = null;

        id = locatie.getId();
        nume = locatie.getNume();
        adresa = locatie.getAdresa();
        oras = locatie.getOras();
        judet = locatie.getJudet();
        active = locatie.getActive();

        LocatieDisponibilaDTO locatieDisponibilaDTO = new LocatieDisponibilaDTO( id, nume, adresa, oras, judet, active );

        return locatieDisponibilaDTO;
    }

    @Override
    public LocatieDisponibilaDTO fromDisponibilitateDTO(DisponibilitateDTO disp) {
        if ( disp == null ) {
            return null;
        }

        Long id = null;
        String nume = null;
        String adresa = null;
        String oras = null;

        id = disp.locatieId();
        nume = disp.locatieNume();
        adresa = disp.locatieAdresa();
        oras = disp.locatieOras();

        String judet = null;
        Boolean active = null;

        LocatieDisponibilaDTO locatieDisponibilaDTO = new LocatieDisponibilaDTO( id, nume, adresa, oras, judet, active );

        return locatieDisponibilaDTO;
    }
}
