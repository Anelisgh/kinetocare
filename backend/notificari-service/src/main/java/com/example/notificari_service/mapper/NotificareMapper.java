package com.example.notificari_service.mapper;

import com.example.notificari_service.dto.NotificareDTO;
import com.example.notificari_service.entity.Notificare;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface NotificareMapper {

    @Mapping(source = "tip", target = "tipNotificare")
    NotificareDTO toDto(Notificare notificare);
}
