package com.example.user.service;

import com.example.user.domain.Pacient;
import com.example.user.domain.Terapeut;
import com.example.user.domain.security.RoleType;
import com.example.user.domain.security.User;
import com.example.user.dto.RegistrationDTO;
import com.example.user.mapper.RegistrationMapper;
import com.example.user.repository.PacientRepository;
import com.example.user.repository.TerapeutRepository;
import com.example.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RegistrationService {

    private final RegistrationMapper registrationMapper;
    private final UserRepository userRepository;
    private final PacientRepository pacientRepository;
    private final TerapeutRepository terapeutRepository;
    private final PasswordEncoder passwordEncoder;

    public void register(RegistrationDTO registrationDTO) {
        if (userRepository.existsByEmail(registrationDTO.getEmail())) {
            throw new IllegalArgumentException("Email deja folosit");
        }

        User user = registrationMapper.toUser(registrationDTO, passwordEncoder);
        user = userRepository.save(user);

        if (user.getRoleType() == RoleType.ROLE_PACIENT) {
            Pacient pacient = registrationMapper.toPacient(registrationDTO, user);
            pacientRepository.save(pacient);
        } else if (user.getRoleType() == RoleType.ROLE_TERAPEUT) {
            Terapeut terapeut = registrationMapper.toTerapeut(registrationDTO, user);
            terapeutRepository.save(terapeut);
        }
    }
}
