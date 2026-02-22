package com.example.servicii_service.mapper;

import com.example.servicii_service.dto.ServiciuAdminDTO;
import com.example.servicii_service.dto.ServiciuDTO;
import com.example.servicii_service.entity.Serviciu;
import com.example.servicii_service.entity.TipServiciu;
import org.mapstruct.*;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ServiciuMapper {

    @Mapping(target = "nume", expression = "java(formateazaNumeComplet(serviciu))")
    ServiciuDTO toDto(Serviciu serviciu);

    @Mapping(source = "tipServiciu.id", target = "tipServiciuId")
    @Mapping(source = "tipServiciu.nume", target = "numeTip")
    ServiciuAdminDTO toAdminDto(Serviciu serviciu);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "tipServiciu", source = "tipServiciu")
    @Mapping(target = "nume", source = "dto.nume")
    @Mapping(target = "pret", source = "dto.pret")
    @Mapping(target = "durataMinute", source = "dto.durataMinute")
    @Mapping(target = "active", source = "dto.active", defaultValue = "true")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Serviciu toEntity(ServiciuAdminDTO dto, TipServiciu tipServiciu);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "tipServiciu", source = "tipServiciu")
    @Mapping(target = "nume", source = "dto.nume")
    @Mapping(target = "pret", source = "dto.pret")
    @Mapping(target = "durataMinute", source = "dto.durataMinute")
    @Mapping(target = "active", source = "dto.active")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntityFromDto(ServiciuAdminDTO dto, @MappingTarget Serviciu entity, TipServiciu tipServiciu);

    default String formateazaNumeComplet(Serviciu serviciu) {
        if (serviciu == null) return null;
        String numeComplet = (serviciu.getTipServiciu() != null && serviciu.getTipServiciu().getNume() != null)
                ? serviciu.getTipServiciu().getNume()
                : "Necunoscut";

        if (serviciu.getNume() != null && !serviciu.getNume().trim().isEmpty()) {
            numeComplet += " - " + serviciu.getNume();
        }
        return numeComplet;
    }
}
