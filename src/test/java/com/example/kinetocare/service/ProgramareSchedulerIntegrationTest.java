package com.example.kinetocare.service;

import com.example.kinetocare.domain.Programare;
import com.example.kinetocare.domain.Status;
import com.example.kinetocare.domain.TipServiciu;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
public class ProgramareSchedulerIntegrationTest extends AbstractIntegrationTest {
    @BeforeEach
    void setUp() {
        TestUtils.clearDatabase(
                programareRepository,
                plataRepository,
                userRepository,
                authorityRepository,
                serviciuRepository,
                diagnosticRepository,
                evaluareRepository,
                pacientRepository,
                terapeutRepository,
                evolutieRepository
        );
        serviciu = TestUtils.createServiciu(TipServiciu.MASAJ, 30);
        serviciu = serviciuRepository.save(serviciu);

        terapeut = TestUtils.createAndSaveTerapeut(
                TestUtils.createTerapeutRegistrationDTO(),
                userRepository,
                authorityRepository,
                terapeutRepository
        );

        pacient = TestUtils.createAndSavePacient(
                TestUtils.createPacientRegistrationDTO(),
                terapeut,
                userRepository,
                authorityRepository,
                pacientRepository
        );
    }

    @Test
    void testActualizeazaStatusuriSiGenereazaPlati() {
        Programare programareExpirata = TestUtils.createProgramare(
                terapeut,
                pacient,
                LocalDate.now().minusDays(1), // Programare expirata
                LocalTime.of(10, 0),
                serviciu,
                programareRepository
        );

        Programare programareAnulata = TestUtils.createProgramare(
                terapeut,
                pacient,
                LocalDate.now().minusDays(8), // Programare veche
                LocalTime.of(11, 0),
                serviciu,
                programareRepository
        );
        programareAnulata.setStatus(Status.ANULATA);
        programareRepository.save(programareAnulata);

        scheduler.actualizeazaStatusuriSiGenereazaPlati();

        Programare updated = programareRepository.findById(programareExpirata.getId()).get();
        assertEquals(Status.FINALIZATA, updated.getStatus());
        assertTrue(plataRepository.existsByProgramare(updated));
        assertFalse(programareRepository.existsById(programareAnulata.getId()));
    }

    @Test
    void testNuCreazaPlataPentruProgramareFaraServiciu() {
        Programare programareFaraServiciu = TestUtils.createProgramare(
                terapeut,
                pacient,
                LocalDate.now().minusDays(1),
                LocalTime.of(10, 0),
                null,
                programareRepository
        );

        scheduler.actualizeazaStatusuriSiGenereazaPlati();

        Programare updated = programareRepository.findById(programareFaraServiciu.getId()).get();
        assertEquals(Status.FINALIZATA, updated.getStatus());
        assertFalse(plataRepository.existsByProgramare(updated));
    }
}
