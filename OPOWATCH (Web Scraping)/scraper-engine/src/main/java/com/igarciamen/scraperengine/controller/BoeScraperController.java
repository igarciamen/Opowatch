package com.igarciamen.scraperengine.controller;

import com.igarciamen.scraperengine.payloads.response.BoePostingResponse;
import com.igarciamen.scraperengine.service.BoeScraperService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/scraper/boe")
public class BoeScraperController {

    private final BoeScraperService boeScraperService;

    public BoeScraperController(BoeScraperService boeScraperService) {
        this.boeScraperService = boeScraperService;
    }

    @Operation(summary = "Returns IT-related public job postings published in the BOE on the given date")
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<BoePostingResponse>> getPostings(
            @RequestParam(required = false)
            @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date) {

        LocalDate targetDate = date != null ? date : LocalDate.now();
        return ResponseEntity.ok(boeScraperService.getPostings(targetDate));
    }
}