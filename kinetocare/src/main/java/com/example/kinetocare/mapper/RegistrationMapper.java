package com.example.kinetocare.mapper;

import com.example.kinetocare.domain.Pacient;
import com.example.kinetocare.domain.Terapeut;
import com.example.kinetocare.domain.security.RoleType;
import com.example.kinetocare.domain.security.User;
import com.example.kinetocare.dto.RegistrationDTO;
import org.springframework.stereotype.Component;

import java.util.HashSet;

@Component
public class RegistrationMapper {

    public User toUser(RegistrationDTO dto) {
        User user = new User();
        user.setEmail(dto.getEmail());
        user.setPassword(dto.getPassword());
        user.setAuthorities(new HashSet<>());
        return user;
    }

    public Pacient toPacient(RegistrationDTO dto, User user) {
        if (dto.getRoleType() != RoleType.ROLE_PACIENT || dto.getPacientDetails() == null) return null;
        return Pacient.builder()
                .nume(dto.getPacientDetails().getNume())
                .telefon(dto.getPacientDetails().getTelefon())
                .email(dto.getEmail())
                .gen(dto.getPacientDetails().getGen())
                .cnp(dto.getPacientDetails().getCnp())
                .dataNastere(dto.getPacientDetails().getDataNastere())
                .tipSport(dto.getPacientDetails().getTipSport())
                .detaliiSport(dto.getPacientDetails().getDetaliiSport())
                .user(user)
                .build();
    }

    public Terapeut toTerapeut(RegistrationDTO dto, User user) {
        if (dto.getRoleType() != RoleType.ROLE_TERAPEUT || dto.getTerapeutDetails() == null) return null;
        return Terapeut.builder()
                .nume(dto.getTerapeutDetails().getNume())
                .telefon(dto.getTerapeutDetails().getTelefon())
                .cnp(dto.getTerapeutDetails().getCnp())
                .dataNastere(dto.getTerapeutDetails().getDataNastere())
                .user(user)
                .build();
    }
}

