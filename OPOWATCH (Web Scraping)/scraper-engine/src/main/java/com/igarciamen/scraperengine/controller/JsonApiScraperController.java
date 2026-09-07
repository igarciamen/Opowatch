package com.igarciamen.scraperengine.controller;

import com.igarciamen.scraperengine.payloads.request.JsonApiScrapeRequest;
import com.igarciamen.scraperengine.payloads.response.JsonApiPostingResponse;
import com.igarciamen.scraperengine.service.JsonApiScraperService;
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
@RequestMapping("/api/scraper/json")
public class JsonApiScraperController {

    private final JsonApiScraperService jsonApiScraperService;

    public JsonApiScraperController(JsonApiScraperService jsonApiScraperService) {
        this.jsonApiScraperService = jsonApiScraperService;
    }

    @Operation(summary = "Fetches a JSON API and flattens its items using a JsonPath expression")
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<JsonApiPostingResponse>> scrape(@Valid @RequestBody JsonApiScrapeRequest request) {
        return ResponseEntity.ok(jsonApiScraperService.scrape(request));
    }
}