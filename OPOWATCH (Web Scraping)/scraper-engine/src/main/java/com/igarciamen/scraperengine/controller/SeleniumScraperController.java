package com.igarciamen.scraperengine.controller;

import com.igarciamen.scraperengine.payloads.request.SeleniumScrapeRequest;
import com.igarciamen.scraperengine.payloads.response.SeleniumPostingResponse;
import com.igarciamen.scraperengine.service.SeleniumScraperService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/scraper/selenium")
public class SeleniumScraperController {

    private final SeleniumScraperService seleniumScraperService;

    public SeleniumScraperController(SeleniumScraperService seleniumScraperService) {
        this.seleniumScraperService = seleniumScraperService;
    }

    @Operation(summary = "Scrapes a page with the given CSS selectors and returns each detected posting")
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<SeleniumPostingResponse>> scrape(@Valid @RequestBody SeleniumScrapeRequest request) {
        return ResponseEntity.ok(seleniumScraperService.scrape(request));
    }
}