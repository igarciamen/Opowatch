package com.igarciamen.scraperengine.controller;

import com.igarciamen.scraperengine.payloads.request.SeleniumScrapeRequest;
import com.igarciamen.scraperengine.payloads.response.SeleniumPostingResponse;
import com.igarciamen.scraperengine.service.SeleniumScraperService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

class SeleniumScraperControllerTest {

    @Mock
    private SeleniumScraperService seleniumScraperService;

    @InjectMocks
    private SeleniumScraperController seleniumScraperController;

    private AutoCloseable mocks;

    @BeforeEach
    void setUp() {
        mocks = MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    void tearDown() throws Exception {
        mocks.close();
    }

    @Test
    void scrape_returnsPostingsFromService() {
        SeleniumScrapeRequest request = new SeleniumScrapeRequest();
        request.setTargetUrl("https://example.org/empleo");
        request.setListSelector(".oferta");
        request.setTitleSelector(".titulo");
        request.setLinkSelector("a");

        when(seleniumScraperService.scrape(request)).thenReturn(
                List.of(new SeleniumPostingResponse("Tecnico de sistemas", "Ayuntamiento", "2026-08-14", "https://example.org/1")));

        ResponseEntity<List<SeleniumPostingResponse>> response = seleniumScraperController.scrape(request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).hasSize(1);
        assertThat(response.getBody().get(0).getTitle()).isEqualTo("Tecnico de sistemas");

        System.out.println("=== scrape_returnsPostingsFromService ===");
        System.out.println("Postings devueltos: " + response.getBody().size());
    }
}