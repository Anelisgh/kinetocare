package com.example.kinetocare.service;

import com.example.kinetocare.domain.Plata;
import com.example.kinetocare.domain.StarePlata;
import com.example.kinetocare.domain.TipServiciu;
import com.example.kinetocare.dto.PlataDTO;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@Transactional
class PlataServiceIntegrationTest extends AbstractIntegrationTest {
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
        serviciu = TestUtils.createServiciu(TipServiciu.MASAJ, 100);
        serviciu = serviciuRepository.save(serviciu);
        programare = TestUtils.createProgramare(
                terapeut,
                pacient,
                LocalDate.now().plusDays(1),
                LocalTime.of(10, 0),
                serviciu,
                programareRepository
        );
    }

    @Test
    void getPlatiPentruPacient_NoPlati_ReturnsEmpty() {
        Map<String, Object> result = plataService.getPlatiPentruPacient(
                pacient.getUser().getEmail(),
                PageRequest.of(0, 10)
        );
        assertThat((Page<?>) result.get("plati")).isEmpty();
        assertThat(result.get("totalPlatit")).isEqualTo(BigDecimal.ZERO);
        assertThat(result.get("dePlatit")).isEqualTo(BigDecimal.ZERO);
    }

    @Test
    void getPlatiPentruPacient_WithPlati_ReturnsCorrectData() {
        Plata plata1 = TestUtils.createPlata(programare, BigDecimal.valueOf(200), StarePlata.ACHITATA, LocalDate.now().minusDays(1), plataRepository);
        Plata plata2 = TestUtils.createPlata(programare, BigDecimal.valueOf(300), StarePlata.IN_ASTEPTARE, LocalDate.now().minusDays(1), plataRepository);
        Map<String, Object> result = plataService.getPlatiPentruPacient(
                pacient.getUser().getEmail(),
                PageRequest.of(0, 10, Sort.by("data").descending())
        );
        Page<PlataDTO> platiPage = (Page<PlataDTO>) result.get("plati");
        assertThat(platiPage).hasSize(2);
        assertThat((BigDecimal) result.get("totalPlatit"))
                .usingComparator(Comparator.naturalOrder())
                .isEqualTo(BigDecimal.valueOf(200));

        assertThat((BigDecimal) result.get("dePlatit"))
                .usingComparator(Comparator.naturalOrder())
                .isEqualTo(BigDecimal.valueOf(300));

        assertThat(platiPage.getContent())
                .extracting(PlataDTO::getSuma)
                .usingComparatorForType(Comparator.naturalOrder(), BigDecimal.class)
                .containsExactlyInAnyOrder(BigDecimal.valueOf(300), BigDecimal.valueOf(200));
    }

    @Test
    void creazaPlata_NewProgramare_CreatesPlata() {
        plataService.creazaPlata(programare);
        List<Plata> plati = plataRepository.findAll();
        assertThat(plati).hasSize(1);
        assertThat(plati.get(0)).satisfies(p -> {
            assertThat(p.getSuma()).isEqualTo(serviciu.getPret());
            assertThat(p.getStarePlata()).isEqualTo(StarePlata.IN_ASTEPTARE);
        });
    }

    @Test
    void creazaPlata_NoServiciu_DoesNotCreatePlata() {
        programare.setServiciu(null);
        programareRepository.save(programare);
        plataService.creazaPlata(programare);
        assertThat(plataRepository.count()).isZero();
    }

    @Test
    void creazaPlata_Duplicate_DoesNotCreateDuplicate() {
        plataService.creazaPlata(programare);
        plataService.creazaPlata(programare);
        assertThat(plataRepository.count()).isEqualTo(1);
    }

    @Test
    void getPlatiPentruPacient_PaginationWorks() {
        IntStream.range(0, 15).forEach(i ->
                TestUtils.createPlata(programare, BigDecimal.valueOf(100), StarePlata.ACHITATA, LocalDate.now().plusDays(i), plataRepository)
        );
        Map<String, Object> result = plataService.getPlatiPentruPacient(
                pacient.getUser().getEmail(),
                PageRequest.of(1, 10)
        );
        assertThat(result.get("currentPage")).isEqualTo(1);
        assertThat(result.get("totalPages")).isEqualTo(2);
        assertThat(result.get("pageSize")).isEqualTo(10);
        assertThat(((Page<?>) result.get("plati")).getContent()).hasSize(5);
    }
}
