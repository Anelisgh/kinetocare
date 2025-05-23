package com.example.kinetocare.service;

import com.example.kinetocare.domain.Pacient;
import com.example.kinetocare.domain.Terapeut;
import com.example.kinetocare.domain.security.Authority;
import com.example.kinetocare.domain.security.RoleType;
import com.example.kinetocare.domain.security.User;
import com.example.kinetocare.dto.RegistrationDTO;
import com.example.kinetocare.mapper.RegistrationMapper;
import com.example.kinetocare.repository.AuthorityRepository;
import com.example.kinetocare.repository.PacientRepository;
import com.example.kinetocare.repository.TerapeutRepository;
import com.example.kinetocare.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class RegistrationService {
    private final UserRepository userRepository;
    private final PacientRepository pacientRepository;
    private final TerapeutRepository terapeutRepository;
    private final AuthorityRepository authorityRepository;
    private final PasswordEncoder passwordEncoder;
    private final RegistrationMapper registrationMapper;

    @Transactional
    public void registerUser(RegistrationDTO registrationDTO) {
        try {
            User user = registrationMapper.toUser(registrationDTO);
            user.setPassword(passwordEncoder.encode(user.getPassword()));

            Authority authority = authorityRepository.findByRoleType(registrationDTO.getRoleType())
                    .orElseThrow(() -> new IllegalStateException("Rol invalid: " + registrationDTO.getRoleType()));
            user.getAuthorities().add(authority);

            User savedUser = userRepository.save(user);

            if (registrationDTO.getRoleType() == RoleType.ROLE_PACIENT) {
                Pacient pacient = registrationMapper.toPacient(registrationDTO, savedUser);
                pacientRepository.save(pacient);
            } else if (registrationDTO.getRoleType() == RoleType.ROLE_TERAPEUT) {
                Terapeut terapeut = registrationMapper.toTerapeut(registrationDTO, savedUser);
                terapeutRepository.save(terapeut);
            }
        } catch (Exception e) {
            log.error("Registration failed for email {}: {}", registrationDTO.getEmail(), e.getMessage());
            throw e;
        }
    }
}
