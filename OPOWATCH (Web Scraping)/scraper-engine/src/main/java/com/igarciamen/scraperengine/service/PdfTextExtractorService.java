package com.igarciamen.scraperengine.service;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Optional;

@Service
public class PdfTextExtractorService {

    private static final Logger log = LoggerFactory.getLogger(PdfTextExtractorService.class);
    private static final int MAX_PDF_BYTES = 15 * 1024 * 1024; // 15 MB safety limit
    private static final int MAX_TEXT_CHARS = 20_000;

    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();

    public Optional<String> extractText(String pdfUrl) {
        try {
            HttpRequest request = HttpRequest.newBuilder(URI.create(pdfUrl))
                    .timeout(Duration.ofSeconds(20))
                    .GET()
                    .build();

            HttpResponse<byte[]> response = httpClient.send(request, HttpResponse.BodyHandlers.ofByteArray());

            if (response.statusCode() != 200 || response.body().length > MAX_PDF_BYTES) {
                return Optional.empty();
            }

            try (PDDocument document = Loader.loadPDF(response.body())) {
                String text = new PDFTextStripper().getText(document);
                if (text == null || text.isBlank()) {
                    // Likely a scanned document with no selectable text (would need OCR).
                    return Optional.empty();
                }
                return Optional.of(text.length() > MAX_TEXT_CHARS ? text.substring(0, MAX_TEXT_CHARS) : text);
            }
        } catch (Exception ex) {
            log.warn("Could not extract text from PDF {}: {}", pdfUrl, ex.getMessage());
            return Optional.empty();
        }
    }
}