package com.igarciamen.scraperengine.controller;

import com.igarciamen.scraperengine.payloads.response.BoePostingResponse;
import com.igarciamen.scraperengine.service.BoeScraperService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

class BoeScraperControllerTest {

    @Mock
    private BoeScraperService boeScraperService;

    private BoeScraperController boeScraperController;

    private AutoCloseable mocks;

    @BeforeEach
    void setUp() {
        mocks = MockitoAnnotations.openMocks(this);
        boeScraperController = new BoeScraperController(boeScraperService);
    }

    @AfterEach
    void tearDown() throws Exception {
        mocks.close();
    }

    @Test
    void getPostings_usesProvidedDate() {
        LocalDate date = LocalDate.of(2026, 8, 14);
        when(boeScraperService.getPostings(date)).thenReturn(
                List.of(new BoePostingResponse("BOE-1", "Analista programador", "MINISTERIO", "https://boe.es/1", null)));

        ResponseEntity<List<BoePostingResponse>> response = boeScraperController.getPostings(date);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).hasSize(1);

        System.out.println("=== getPostings_usesProvidedDate ===");
        System.out.println("Postings devueltos: " + response.getBody().size());
    }

    @Test
    void getPostings_defaultsToTodayWhenDateIsNull() {
        LocalDate today = LocalDate.now();
        when(boeScraperService.getPostings(today)).thenReturn(List.of());

        ResponseEntity<List<BoePostingResponse>> response = boeScraperController.getPostings(null);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEmpty();

        System.out.println("=== getPostings_defaultsToTodayWhenDateIsNull ===");
        System.out.println("Fecha usada por defecto: " + today);
    }
}