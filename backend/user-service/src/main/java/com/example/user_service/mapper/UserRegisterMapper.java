package com.example.user_service.mapper;

import com.example.user_service.dto.RegisterRequestDTO;
import com.example.user_service.dto.RegisterResponseDTO;
import com.example.user_service.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserRegisterMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(source = "request.email", target = "email")
    @Mapping(source = "request.gen", target = "gen")
    User toEntity(RegisterRequestDTO request, String keycloakId);

    RegisterResponseDTO toRegisterResponse(User user, String message);
}