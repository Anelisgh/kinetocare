package com.example.kinetocare.service;

import com.example.kinetocare.domain.*;
import com.example.kinetocare.dto.ProgramareDTO;
import com.example.kinetocare.exception.ConflictException;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@Transactional
class ProgramareServiceIntegrationTest extends AbstractIntegrationTest{
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
        //Servicii
        serviciuKinetoterapie = TestUtils.createServiciu(TipServiciu.MASAJ, 60);
        serviciuKinetoterapie = serviciuRepository.save(serviciuKinetoterapie);

        serviciuReevaluare = TestUtils.createServiciu(TipServiciu.REEVALUARE, 30);
        serviciuReevaluare = serviciuRepository.save(serviciuReevaluare);

        //Terapeut
        terapeut = TestUtils.createAndSaveTerapeut(
                TestUtils.createTerapeutRegistrationDTO(),
                userRepository,
                authorityRepository,
                terapeutRepository
        );

        //Pacient cu evaluare
        pacient = TestUtils.createAndSavePacient(
                TestUtils.createPacientRegistrationDTO(),
                terapeut,
                userRepository,
                authorityRepository,
                pacientRepository
        );

        Diagnostic diagnostic = TestUtils.createDiagnosticTest();
        diagnostic.setServiciu(serviciuKinetoterapie);
        diagnostic.setSedinteRecomandate(5);
        diagnostic.setPacient(pacient);
        diagnostic.setTerapeut(terapeut);
        diagnostic = diagnosticRepository.save(diagnostic);

        Evaluare evaluare = TestUtils.createEvaluareTest(pacient, diagnostic);
        evaluare.setData(LocalDate.now().minusDays(10));
        evaluare.setTerapeut(terapeut);
        evaluare = evaluareRepository.save(evaluare);

        pacient.getEvaluari().add(evaluare);
        pacient = pacientRepository.saveAndFlush(pacient);
    }

    @Test
    void creazaProgramare_Success_Kinetoterapie() {
        ProgramareDTO dto = TestUtils.createValidProgramareDTO(LocalDate.now().plusDays(1), LocalTime.of(10, 0));
        ProgramareDTO result = programareService.creazaProgramare(dto, pacient.getUser().getEmail());
        assertThat(result.getId()).isNotNull();
        assertThat(result.getStatus()).isEqualTo(Status.PROGRAMATA);
        assertThat(result.getServiciu().getTipServiciu()).isEqualTo(TipServiciu.MASAJ);
    }

    @Test
    void creazaProgramare_Success_Reevaluare() {
        IntStream.range(0, 5).forEach(i ->
                TestUtils.createProgramareFinalizata(pacient, terapeut, serviciuKinetoterapie, programareRepository)
        );
        ProgramareDTO dto = TestUtils.createValidProgramareDTO(LocalDate.now().plusDays(1), LocalTime.of(10, 0));
        ProgramareDTO result = programareService.creazaProgramare(dto, pacient.getUser().getEmail());
        assertThat(result.getServiciu().getTipServiciu()).isEqualTo(TipServiciu.REEVALUARE);
    }

    @Test
    void creazaProgramare_ConflictSuprapunere_ShouldThrow() {
        TestUtils.createProgramare(
                terapeut,
                pacient,
                LocalDate.now().plusDays(1),
                LocalTime.of(10, 0),
                serviciuKinetoterapie,
                programareRepository
        );

        ProgramareDTO dto = TestUtils.createValidProgramareDTO(LocalDate.now().plusDays(1), LocalTime.of(10, 30));
        assertThatThrownBy(() -> programareService.creazaProgramare(dto, pacient.getUser().getEmail()))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining("Terapeutul are deja o programare");
    }

    @Test
    void modificaProgramare_Success() {
        Programare programare = TestUtils.createProgramare(
                terapeut,
                pacient,
                LocalDate.now().plusDays(1),
                LocalTime.of(10, 0),
                serviciuKinetoterapie,
                programareRepository
        );
        ProgramareDTO dto = TestUtils.createValidProgramareDTO(
                programare.getId(),
                LocalDate.now().plusDays(2),
                LocalTime.of(11, 0)
        );
        ProgramareDTO result = programareService.modificaProgramare(dto, pacient.getUser().getEmail());
        assertThat(result.getData()).isEqualTo(dto.getData());
        assertThat(result.getOra()).isEqualTo(dto.getOra());
    }

    @Test
    void stergeProgramare_Success() {
        Programare programare = TestUtils.createProgramare(
                terapeut,
                pacient,
                LocalDate.now().plusDays(1),
                LocalTime.of(10, 0),
                serviciuKinetoterapie,
                programareRepository
        );
        programareService.stergeProgramare(programare.getId(), pacient.getUser().getEmail());
        assertThat(programareRepository.findById(programare.getId())).isEmpty();
    }

    @Test
    void getProgramareById_Success() {
        Programare programare = TestUtils.createProgramare(
                terapeut,
                pacient,
                LocalDate.now().plusDays(1),
                LocalTime.of(10, 0),
                serviciuKinetoterapie,
                programareRepository
        );
        Programare result = programareService.getProgramareById(programare.getId());
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(programare.getId());
    }
}
