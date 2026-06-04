package com.example.programari_service;

import com.example.programari_service.client.*;
import com.example.programari_service.dto.*;
import com.example.programari_service.entity.*;
import com.example.programari_service.mapper.*;
import com.example.programari_service.repository.*;
import com.example.programari_service.service.ProgramareService;
import com.example.programari_service.service.RelatieService;
import com.example.programari_service.service.NotificarePublisher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProgramareServiceTest {

    @InjectMocks
    private ProgramareService programareService;

    @Mock private ProgramareRepository programareRepository;
    @Mock private ProgramareMapper programareMapper;
    @Mock private IstoricProgramareMapper istoricProgramareMapper;
    @Mock private ServiciiClient serviciiClient;
    @Mock private TerapeutiClient terapeutiClient;
    @Mock private EvaluareRepository evaluareRepository;
    @Mock private EvaluareMapper evaluareMapper;
    @Mock private PacientiClient pacientiClient;
    @Mock private UserClient userClient;
    @Mock private NotificarePublisher notificarePublisher;
    @Mock private RelatieService relatieService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(programareService, "numeEvaluareInitiala", "Evaluare Initiala");
        ReflectionTestUtils.setField(programareService, "numeReevaluare", "Reevaluare");
    }

    // FSM tests
    @Test
    void determinaServiciulCorect_noEvaluations_returnsEvaluareInitiala() {
        String pacientKeycloakId = "pacient-123";
        when(evaluareRepository.findFirstByPacientKeycloakIdOrderByDataDesc(pacientKeycloakId))
                .thenReturn(Optional.empty());

        DetaliiServiciuDTO mockServiciu = new DetaliiServiciuDTO(1L, "Evaluare Initiala", BigDecimal.valueOf(100), 50);
        when(serviciiClient.gasesteServiciuDupaNume("Evaluare Initiala")).thenReturn(mockServiciu);

        DetaliiServiciuDTO result = programareService.determinaServiciulCorect(pacientKeycloakId);

        assertNotNull(result);
        assertEquals("Evaluare Initiala", result.nume());
        verify(evaluareRepository).findFirstByPacientKeycloakIdOrderByDataDesc(pacientKeycloakId);
        verify(serviciiClient).gasesteServiciuDupaNume("Evaluare Initiala");
        verifyNoMoreInteractions(programareRepository);
    }

    @Test
    void determinaServiciulCorect_completedSessionsLessThanRecommended_returnsRecommendedServiciu() {
        String pacientKeycloakId = "pacient-123";
        LocalDate evalDate = LocalDate.now().minusDays(5);
        Evaluare mockEvaluare = Evaluare.builder()
                .id(1L)
                .pacientKeycloakId(pacientKeycloakId)
                .terapeutKeycloakId("terapeut-1")
                .tip(TipEvaluare.INITIALA)
                .data(evalDate)
                .diagnostic("Diagnostic test")
                .sedinteRecomandate(10)
                .serviciuRecomandatId(2L)
                .build();

        when(evaluareRepository.findFirstByPacientKeycloakIdOrderByDataDesc(pacientKeycloakId))
                .thenReturn(Optional.of(mockEvaluare));
        when(programareRepository.countSedintePacientDupaData(pacientKeycloakId, evalDate))
                .thenReturn(5L); // 5 < 10

        DetaliiServiciuDTO mockServiciu = new DetaliiServiciuDTO(2L, "Kinetoterapie", BigDecimal.valueOf(150), 50);
        when(serviciiClient.getServiciuById(2L)).thenReturn(mockServiciu);

        DetaliiServiciuDTO result = programareService.determinaServiciulCorect(pacientKeycloakId);

        assertNotNull(result);
        assertEquals("Kinetoterapie", result.nume());
        verify(evaluareRepository).findFirstByPacientKeycloakIdOrderByDataDesc(pacientKeycloakId);
        verify(programareRepository).countSedintePacientDupaData(pacientKeycloakId, evalDate);
        verify(serviciiClient).getServiciuById(2L);
    }

    @Test
    void determinaServiciulCorect_completedSessionsEqualOrMoreThanRecommended_returnsReevaluare() {
        String pacientKeycloakId = "pacient-123";
        LocalDate evalDate = LocalDate.now().minusDays(5);
        Evaluare mockEvaluare = Evaluare.builder()
                .id(1L)
                .pacientKeycloakId(pacientKeycloakId)
                .terapeutKeycloakId("terapeut-1")
                .tip(TipEvaluare.INITIALA)
                .data(evalDate)
                .diagnostic("Diagnostic test")
                .sedinteRecomandate(10)
                .serviciuRecomandatId(2L)
                .build();

        when(evaluareRepository.findFirstByPacientKeycloakIdOrderByDataDesc(pacientKeycloakId))
                .thenReturn(Optional.of(mockEvaluare));
        when(programareRepository.countSedintePacientDupaData(pacientKeycloakId, evalDate))
                .thenReturn(10L); // 10 >= 10

        DetaliiServiciuDTO mockReevaluare = new DetaliiServiciuDTO(3L, "Reevaluare", BigDecimal.valueOf(120), 45);
        when(serviciiClient.gasesteServiciuDupaNume("Reevaluare")).thenReturn(mockReevaluare);

        DetaliiServiciuDTO result = programareService.determinaServiciulCorect(pacientKeycloakId);

        assertNotNull(result);
        assertEquals("Reevaluare", result.nume());
        verify(evaluareRepository).findFirstByPacientKeycloakIdOrderByDataDesc(pacientKeycloakId);
        verify(programareRepository).countSedintePacientDupaData(pacientKeycloakId, evalDate);
        verify(serviciiClient).gasesteServiciuDupaNume("Reevaluare");
    }

    // Greedy tests
    @Test
    void getSloturiDisponibile_withBufferAndExistingAppointments_calculatesCorrectSlots() {
        String terapeutKeycloakId = "terapeut-123";
        Long locatieId = 1L;
        LocalDate data = LocalDate.now().plusDays(1); // Mâine, pentru a asigura că nu suntem în trecut
        Long serviciuId = 2L;

        // Mock terapeut map
        Map<String, Object> terapeutMap = new HashMap<>();
        terapeutMap.put("id", 10L);
        when(terapeutiClient.getTerapeutByKeycloakId(terapeutKeycloakId)).thenReturn(terapeutMap);

        // No vacation
        when(terapeutiClient.checkConcediu(10L, data.toString())).thenReturn(false);

        // Orar: 08:00 - 12:00
        DisponibilitateDTO orar = new DisponibilitateDTO(LocalTime.of(8, 0), LocalTime.of(12, 0));
        int ziSaptamana = data.getDayOfWeek().getValue();
        when(terapeutiClient.getOrar(10L, locatieId, ziSaptamana)).thenReturn(orar);

        // Serviciu de 60 de minute
        DetaliiServiciuDTO serviciu = new DetaliiServiciuDTO(2L, "Kinetoterapie", BigDecimal.valueOf(150), 60);
        when(serviciiClient.getServiciuById(serviciuId)).thenReturn(serviciu);

        // Programare existentă: 09:00 - 10:00
        Programare existenta = Programare.builder()
                .id(100L)
                .terapeutKeycloakId(terapeutKeycloakId)
                .data(data)
                .oraInceput(LocalTime.of(9, 0))
                .oraSfarsit(LocalTime.of(10, 0))
                .status(StatusProgramare.PROGRAMATA)
                .build();
        when(programareRepository.findByTerapeutKeycloakIdAndDataAndStatus(
                terapeutKeycloakId, data, StatusProgramare.PROGRAMATA))
                .thenReturn(List.of(existenta));

        // Apel
        List<LocalTime> sloturi = programareService.getSloturiDisponibile(terapeutKeycloakId, locatieId, data, serviciuId);

        // Verificări conform logicii Greedy din cod:
        // durataMinute = 60. limitaSfarsit = 12:00.
        // Bucla: while (!cursor.plusMinutes(durataMinute).isAfter(limitaSfarsit))
        // Pasul 1: cursor = 08:00. finalSlot = 09:00.
        //         esteLiber(08:00, 09:00) -> nu se suprapune cu 09:00-10:00 -> liber! -> adaugă 08:00.
        //         cursor = cursor.plusMinutes(60 + 10) = 09:10.
        // Pasul 2: cursor = 09:10. finalSlot = 10:10.
        //         esteLiber(09:10, 10:10) -> se suprapune cu 09:00-10:00 -> ocupat!
        //         cursor = cursor.plusMinutes(60 + 10) = 10:20.
        // Pasul 3: cursor = 10:20. finalSlot = 11:20.
        //         esteLiber(10:20, 11:20) -> nu se suprapune -> liber! -> adaugă 10:20.
        //         cursor = cursor.plusMinutes(60 + 10) = 11:30.
        // Pasul 4: cursor = 11:30. finalSlot = 12:30.
        //         finalSlot isAfter 12:00 -> bucla se oprește.
        //
        // Deci sloturile așteptate sunt exact: [08:00, 10:20]
        assertNotNull(sloturi);
        assertEquals(2, sloturi.size());
        assertEquals(LocalTime.of(8, 0), sloturi.get(0));
        assertEquals(LocalTime.of(10, 20), sloturi.get(1));
    }
}
