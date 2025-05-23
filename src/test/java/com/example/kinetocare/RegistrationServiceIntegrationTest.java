package com.example.kinetocare;

import com.example.kinetocare.domain.Pacient;
import com.example.kinetocare.domain.Terapeut;
import com.example.kinetocare.domain.security.User;
import com.example.kinetocare.dto.RegistrationDTO;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@Transactional
public class RegistrationServiceIntegrationTest extends AbstractIntegrationTest{
    @Test
    void testRegisterUser_AsPacient_SavesUserAndPacient() {
        RegistrationDTO dto = TestUtils.createPacientRegistrationDTO();

        registrationService.registerUser(dto);

        // Verificam utilizand datele din TestUtils
        Optional<User> userOpt = userRepository.findByEmail("pacient@gmail.com");
        assertTrue(userOpt.isPresent());

        Pacient pacient = pacientRepository.findByUserEmail("pacient@gmail.com")
                .orElseThrow();
        assertEquals("Popescu Alina", pacient.getNume());
    }

    @Test
    void testRegisterUser_AsTerapeut_SavesUserAndTerapeut() {
        RegistrationDTO dto = TestUtils.createTerapeutRegistrationDTO();

        registrationService.registerUser(dto);

        Optional<User> userOpt = userRepository.findByEmail("terapeut@gmail.com");
        assertTrue(userOpt.isPresent());

        Terapeut terapeut = terapeutRepository.findByUserEmail("terapeut@gmail.com")
                .orElseThrow();
        assertEquals("Ionescu Vlad", terapeut.getNume());
    }
}