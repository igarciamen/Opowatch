package com.igarciamen.scraperengine.controller;

import com.igarciamen.scraperengine.payloads.response.DocumentTextResponse;
import com.igarciamen.scraperengine.service.PdfTextExtractorService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
public class DocumentTextController {

    private final PdfTextExtractorService pdfTextExtractorService;

    public DocumentTextController(PdfTextExtractorService pdfTextExtractorService) {
        this.pdfTextExtractorService = pdfTextExtractorService;
    }

    @Operation(summary = "Downloads a PDF document and returns its extracted plain text, if readable")
    @GetMapping(path = "/api/scraper/document-text", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<DocumentTextResponse> getDocumentText(@RequestParam String url) {
        Optional<String> text = pdfTextExtractorService.extractText(url);
        return ResponseEntity.ok(new DocumentTextResponse(text.isPresent(), text.orElse(null)));
    }
}