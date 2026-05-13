package com.example.servicii_service.mapper;

import com.example.servicii_service.dto.ServiciuAdminDTO;
import com.example.servicii_service.dto.ServiciuDTO;
import com.example.servicii_service.entity.Serviciu;
import com.example.servicii_service.entity.TipServiciu;
import java.math.BigDecimal;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-05-13T13:22:14+0300",
    comments = "version: 1.6.3, compiler: Eclipse JDT (IDE) 3.46.0.v20260407-0427, environment: Java 21.0.10 (Eclipse Adoptium)"
)
@Component
public class ServiciuMapperImpl implements ServiciuMapper {

    @Override
    public ServiciuDTO toDto(Serviciu serviciu) {
        if ( serviciu == null ) {
            return null;
        }

        Long id = null;
        BigDecimal pret = null;
        Integer durataMinute = null;

        id = serviciu.getId();
        pret = serviciu.getPret();
        durataMinute = serviciu.getDurataMinute();

        String nume = formateazaNumeComplet(serviciu);

        ServiciuDTO serviciuDTO = new ServiciuDTO( id, nume, pret, durataMinute );

        return serviciuDTO;
    }

    @Override
    public ServiciuAdminDTO toAdminDto(Serviciu serviciu) {
        if ( serviciu == null ) {
            return null;
        }

        Long tipServiciuId = null;
        String numeTip = null;
        Long id = null;
        String nume = null;
        BigDecimal pret = null;
        Integer durataMinute = null;
        Boolean active = null;

        tipServiciuId = serviciuTipServiciuId( serviciu );
        numeTip = serviciuTipServiciuNume( serviciu );
        id = serviciu.getId();
        nume = serviciu.getNume();
        pret = serviciu.getPret();
        durataMinute = serviciu.getDurataMinute();
        active = serviciu.getActive();

        ServiciuAdminDTO serviciuAdminDTO = new ServiciuAdminDTO( id, tipServiciuId, numeTip, nume, pret, durataMinute, active );

        return serviciuAdminDTO;
    }

    @Override
    public Serviciu toEntity(ServiciuAdminDTO dto, TipServiciu tipServiciu) {
        if ( dto == null && tipServiciu == null ) {
            return null;
        }

        Serviciu.ServiciuBuilder serviciu = Serviciu.builder();

        if ( dto != null ) {
            serviciu.nume( dto.nume() );
            serviciu.pret( dto.pret() );
            serviciu.durataMinute( dto.durataMinute() );
            if ( dto.active() != null ) {
                serviciu.active( dto.active() );
            }
            else {
                serviciu.active( true );
            }
        }
        serviciu.tipServiciu( tipServiciu );

        return serviciu.build();
    }

    @Override
    public void updateEntityFromDto(ServiciuAdminDTO dto, Serviciu entity, TipServiciu tipServiciu) {
        if ( dto == null && tipServiciu == null ) {
            return;
        }

        if ( dto != null ) {
            if ( dto.nume() != null ) {
                entity.setNume( dto.nume() );
            }
            if ( dto.pret() != null ) {
                entity.setPret( dto.pret() );
            }
            if ( dto.durataMinute() != null ) {
                entity.setDurataMinute( dto.durataMinute() );
            }
            if ( dto.active() != null ) {
                entity.setActive( dto.active() );
            }
        }
        if ( tipServiciu != null ) {
            entity.setTipServiciu( tipServiciu );
        }
    }

    private Long serviciuTipServiciuId(Serviciu serviciu) {
        TipServiciu tipServiciu = serviciu.getTipServiciu();
        if ( tipServiciu == null ) {
            return null;
        }
        return tipServiciu.getId();
    }

    private String serviciuTipServiciuNume(Serviciu serviciu) {
        TipServiciu tipServiciu = serviciu.getTipServiciu();
        if ( tipServiciu == null ) {
            return null;
        }
        return tipServiciu.getNume();
    }
}
