package com.example.user.mapper;

import com.example.user.domain.Pacient;
import com.example.user.domain.Terapeut;
import com.example.user.domain.security.RoleType;
import com.example.user.domain.security.User;
import com.example.user.dto.PacientDetails;
import com.example.user.dto.RegistrationDTO;
import com.example.user.dto.TerapeutDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class RegistrationMapper {
    public User toUser(RegistrationDTO dto, PasswordEncoder passwordEncoder) {
        return User.builder()
                .email(dto.getEmail())
                .password(passwordEncoder.encode(dto.getPassword()))
                .roleType(dto.getRoleType())
                .enabled(true)
                .accountNonExpired(true)
                .accountNonLocked(true)
                .credentialsNonExpired(true)
                .build();
    }

    public Pacient toPacient(RegistrationDTO dto, User user) {
        if (dto.getRoleType() != RoleType.ROLE_PACIENT || dto.getPacientDetails() == null) return null;
        PacientDetails p = dto.getPacientDetails();
        return Pacient.builder()
                .nume(p.getNume())
                .telefon(p.getTelefon())
                .email(dto.getEmail())
                .gen(p.getGen())
                .cnp(p.getCnp())
                .dataNastere(p.getDataNastere())
                .tipSport(p.getTipSport())
                .detaliiSport(p.getDetaliiSport())
                .user(user)
                .build();
    }

    public Terapeut toTerapeut(RegistrationDTO dto, User user) {
        if (dto.getRoleType() != RoleType.ROLE_TERAPEUT || dto.getTerapeutDetails() == null) return null;
        TerapeutDetails t = dto.getTerapeutDetails();
        return Terapeut.builder()
                .nume(t.getNume())
                .telefon(t.getTelefon())
                .cnp(t.getCnp())
                .dataNastere(t.getDataNastere())
                .user(user)
                .build();
    }
}
