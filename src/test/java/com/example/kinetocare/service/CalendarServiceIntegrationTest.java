package com.example.kinetocare.service;

import com.example.kinetocare.domain.Programare;
import com.example.kinetocare.domain.Status;
import com.example.kinetocare.domain.TipServiciu;
import com.example.kinetocare.dto.ProgramareTerapeutDTO;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@Transactional
class CalendarServiceIntegrationTest extends AbstractIntegrationTest{
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

        serviciu = TestUtils.createServiciu(TipServiciu.MASAJ, 60);
        serviciu = serviciuRepository.save(serviciu);
    }

    @Test
    void getProgramariForTerapeut_ShouldReturnAppointments() {
        Programare programare = TestUtils.createProgramare(
                terapeut,
                pacient,
                LocalDate.now().plusDays(1),
                LocalTime.of(10, 0),
                serviciu,
                programareRepository
        );
        List<ProgramareTerapeutDTO> result = calendarService.getProgramariForTerapeut(terapeut.getUser().getEmail());
        assertThat(result)
                .hasSize(1)
                .first()
                .satisfies(dto -> {
                    assertThat(dto.getProgramareId()).isEqualTo(programare.getId());
                    assertThat(dto.getNumePacient()).isEqualTo(pacient.getNume());
                    assertThat(dto.getTipServiciu()).isEqualTo(serviciu.getTipServiciu());
                });
    }

    @Test
    void getProgramariForCalendar_ShouldFilterByDateRange() {
        LocalDate startDate = LocalDate.now().plusDays(1);
        TestUtils.createProgramare(terapeut, pacient, startDate, LocalTime.of(10, 0), serviciu, programareRepository);
        TestUtils.createProgramare(terapeut, pacient, LocalDate.now().plusDays(5), LocalTime.of(14, 0), serviciu, programareRepository);
        List<ProgramareTerapeutDTO> result = calendarService.getProgramariForCalendar(
                terapeut.getUser().getEmail(),
                startDate,
                startDate.plusDays(3)
        );
        assertThat(result).hasSize(1);
    }

    @Test
    void updateProgramare_ShouldUpdateSuccessfully() {
        Programare programare = TestUtils.createProgramare(
                terapeut,
                pacient,
                LocalDate.now().plusDays(1),
                LocalTime.of(10, 0),
                serviciu,
                programareRepository
        );
        LocalDate newDate = LocalDate.now().plusDays(2);
        LocalTime newTime = LocalTime.of(14, 0);
        Status newStatus = Status.FINALIZATA;
        calendarService.updateProgramare(programare.getId(), newDate, newTime, newStatus);
        Programare updated = programareRepository.findById(programare.getId()).orElseThrow();
        assertThat(updated.getData()).isEqualTo(newDate);
        assertThat(updated.getOra()).isEqualTo(newTime);
        assertThat(updated.getStatus()).isEqualTo(newStatus);
        assertThat(plataRepository.findAll()).hasSize(1);
    }

    @Test
    void updateProgramare_ShouldThrowConflict() {
        LocalDate date = LocalDate.now().plusDays(1);
        Programare existing = TestUtils.createProgramare(terapeut, pacient, date, LocalTime.of(10, 0), serviciu, programareRepository);
        Programare toUpdate = TestUtils.createProgramare(terapeut, pacient, date, LocalTime.of(11, 0), serviciu, programareRepository);
        assertThatThrownBy(() -> calendarService.updateProgramare(
                toUpdate.getId(),
                date,
                LocalTime.of(10, 30),
                Status.PROGRAMATA
        ))
                .isInstanceOf(ResponseStatusException.class)
                .hasFieldOrPropertyWithValue("status", HttpStatus.CONFLICT);
    }

    @Test
    void getProgramariForTerapeut_InvalidEmail_ShouldThrow() {
        assertThatThrownBy(() -> calendarService.getProgramariForTerapeut("invalid@email.com"))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Terapeutul nu există");
    }
}
