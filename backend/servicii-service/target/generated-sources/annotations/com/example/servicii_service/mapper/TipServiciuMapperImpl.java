package com.example.servicii_service.mapper;

import com.example.servicii_service.dto.TipServiciuDTO;
import com.example.servicii_service.entity.TipServiciu;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-05-13T13:22:14+0300",
    comments = "version: 1.6.3, compiler: Eclipse JDT (IDE) 3.46.0.v20260407-0427, environment: Java 21.0.10 (Eclipse Adoptium)"
)
@Component
public class TipServiciuMapperImpl implements TipServiciuMapper {

    @Override
    public TipServiciuDTO toDto(TipServiciu tip) {
        if ( tip == null ) {
            return null;
        }

        Long id = null;
        String nume = null;
        String descriere = null;
        Boolean active = null;

        id = tip.getId();
        nume = tip.getNume();
        descriere = tip.getDescriere();
        active = tip.getActive();

        TipServiciuDTO tipServiciuDTO = new TipServiciuDTO( id, nume, descriere, active );

        return tipServiciuDTO;
    }

    @Override
    public TipServiciu toEntity(TipServiciuDTO dto) {
        if ( dto == null ) {
            return null;
        }

        TipServiciu.TipServiciuBuilder tipServiciu = TipServiciu.builder();

        if ( dto.active() != null ) {
            tipServiciu.active( dto.active() );
        }
        else {
            tipServiciu.active( true );
        }
        tipServiciu.descriere( dto.descriere() );
        tipServiciu.nume( dto.nume() );

        return tipServiciu.build();
    }

    @Override
    public void updateEntityFromDto(TipServiciuDTO dto, TipServiciu entity) {
        if ( dto == null ) {
            return;
        }

        if ( dto.active() != null ) {
            entity.setActive( dto.active() );
        }
        if ( dto.descriere() != null ) {
            entity.setDescriere( dto.descriere() );
        }
        if ( dto.nume() != null ) {
            entity.setNume( dto.nume() );
        }
    }
}
