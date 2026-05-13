package com.example.user_service.mapper;

import com.example.user_service.dto.RegisterRequestDTO;
import com.example.user_service.dto.RegisterResponseDTO;
import com.example.user_service.entity.Gen;
import com.example.user_service.entity.User;
import com.example.user_service.entity.UserRole;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-05-13T13:22:24+0300",
    comments = "version: 1.6.3, compiler: Eclipse JDT (IDE) 3.46.0.v20260407-0427, environment: Java 21.0.10 (Eclipse Adoptium)"
)
@Component
public class UserRegisterMapperImpl implements UserRegisterMapper {

    @Override
    public User toEntity(RegisterRequestDTO request, String keycloakId) {
        if ( request == null && keycloakId == null ) {
            return null;
        }

        User.UserBuilder user = User.builder();

        if ( request != null ) {
            user.email( request.email() );
            user.gen( request.gen() );
            user.nume( request.nume() );
            user.prenume( request.prenume() );
            user.role( request.role() );
            user.telefon( request.telefon() );
        }
        user.keycloakId( keycloakId );

        return user.build();
    }

    @Override
    public RegisterResponseDTO toRegisterResponse(User user, String message) {
        if ( user == null && message == null ) {
            return null;
        }

        String keycloakId = null;
        String email = null;
        String nume = null;
        String prenume = null;
        UserRole role = null;
        Gen gen = null;
        if ( user != null ) {
            keycloakId = user.getKeycloakId();
            email = user.getEmail();
            nume = user.getNume();
            prenume = user.getPrenume();
            role = user.getRole();
            gen = user.getGen();
        }
        String message1 = null;
        message1 = message;

        RegisterResponseDTO registerResponseDTO = new RegisterResponseDTO( keycloakId, email, nume, prenume, role, gen, message1 );

        return registerResponseDTO;
    }
}
