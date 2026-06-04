package com.example.programari_service;

import com.example.programari_service.client.*;
import com.example.programari_service.dto.*;
import com.example.programari_service.entity.*;
import com.example.programari_service.repository.*;
import com.example.programari_service.service.ProgramareService;
import com.example.programari_service.service.NotificarePublisher;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@ActiveProfiles("test")
public class DoubleBookingIntegrationTest {

    @Autowired
    private ProgramareService programareService;

    @Autowired
    private ProgramareRepository programareRepository;

    @Autowired
    private EvaluareRepository evaluareRepository;

    // Feign Clients and publishers must be mocked
    @MockitoBean private ServiciiClient serviciiClient;
    @MockitoBean private TerapeutiClient terapeutClient;
    @MockitoBean private PacientiClient pacientiClient;
    @MockitoBean private UserClient userClient;
    @MockitoBean private NotificarePublisher notificarePublisher;
    @MockitoBean private org.springframework.security.oauth2.jwt.JwtDecoder jwtDecoder;

    @BeforeEach
    void setUp() {
        evaluareRepository.deleteAll();
        programareRepository.deleteAll();

        DetaliiServiciuDTO mockServiciu = new DetaliiServiciuDTO(1L, "Evaluare Initiala", BigDecimal.valueOf(100), 50);
        when(serviciiClient.gasesteServiciuDupaNume("Evaluare Initiala")).thenReturn(mockServiciu);
    }

    @AfterEach
    void tearDown() {
        // Clean up database tables in order of foreign key dependencies
        try {
            evaluareRepository.deleteAll();
            programareRepository.deleteAll();
        } catch (Exception e) {
            System.err.println("Error during DB cleanup: " + e.getMessage());
        }
    }

    @Test
    void testConcurrentDoubleBooking_allowsExactlyOneSuccessfulBooking() throws InterruptedException {
        int threadsCount = 50;
        ExecutorService executorService = Executors.newFixedThreadPool(threadsCount);
        CountDownLatch latch = new CountDownLatch(1);
        CountDownLatch finishLatch = new CountDownLatch(threadsCount);

        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger conflictCount = new AtomicInteger(0);
        AtomicInteger otherErrors = new AtomicInteger(0);

        LocalDate date = LocalDate.now().plusDays(10); // future date
        LocalTime startTime = LocalTime.of(10, 0);

        for (int i = 0; i < threadsCount; i++) {
            final String pacientKey = "pacient-" + i;
            executorService.submit(() -> {
                try {
                    latch.await(); // wait for the start signal
                    CreeazaProgramareRequest request = new CreeazaProgramareRequest(
                            pacientKey,
                            "terapeut-conconcurent",
                            1L, // locatieId
                            date,
                            startTime
                    );
                    programareService.creeazaProgramare(request);
                    successCount.incrementAndGet();
                } catch (com.example.common.exception.ResourceAlreadyExistsException | org.springframework.dao.DataIntegrityViolationException e) {
                    conflictCount.incrementAndGet();
                } catch (Exception e) {
                    System.err.println("Unexpected error: " + e.getClass().getName() + " - " + e.getMessage());
                    otherErrors.incrementAndGet();
                } finally {
                    finishLatch.countDown();
                }
            });
        }

        // Release all threads simultaneously
        latch.countDown();
        boolean completed = finishLatch.await(15, TimeUnit.SECONDS);
        executorService.shutdown();

        assertTrue(completed, "Test threads did not finish within timeout");

        System.out.println("Successes: " + successCount.get());
        System.out.println("Conflicts: " + conflictCount.get());
        System.out.println("Other errors: " + otherErrors.get());

        // Verify that exactly 1 transaction succeeded and 49 failed with a conflict
        assertEquals(1, successCount.get(), "Should have exactly 1 successful booking");
        assertEquals(threadsCount - 1, conflictCount.get(), "The other threads should have failed with conflict/unique key violation");
        assertEquals(0, otherErrors.get(), "There should be no other unexpected exceptions");

        // Verify that only 1 record exists in the DB
        long count = programareRepository.count();
        assertEquals(1, count, "Only 1 appointment should exist in database");
    }
}
