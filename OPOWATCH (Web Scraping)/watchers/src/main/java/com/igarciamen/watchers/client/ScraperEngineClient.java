package com.igarciamen.watchers.client;

import com.igarciamen.watchers.client.dto.BoePostingApiResponse;
import com.igarciamen.watchers.client.dto.JsonApiPostingApiResponse;
import com.igarciamen.watchers.client.dto.JsonApiScrapeRequestDto;

import com.igarciamen.watchers.dto.ScrapedPosting;
import com.igarciamen.watchers.dto.SeleniumPostingApiResponse;
import com.igarciamen.watchers.dto.SeleniumScrapeRequestDto;
import com.igarciamen.watchers.model.Watcher;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
public class ScraperEngineClient {

    private static final DateTimeFormatter API_DATE_FORMAT = DateTimeFormatter.ISO_LOCAL_DATE;

    private final RestTemplate restTemplate;
    private final String baseUrl;

    public ScraperEngineClient(RestTemplate restTemplate, @Value("${scraper-engine.base-url}") String baseUrl) {
        this.restTemplate = restTemplate;
        this.baseUrl = baseUrl;
    }

    public List<ScrapedPosting> fetchBoePostings(LocalDate date) {
        String url = baseUrl + "/api/scraper/boe?date=" + date.format(API_DATE_FORMAT);

        BoePostingApiResponse[] response = restTemplate.getForObject(url, BoePostingApiResponse[].class);
        if (response == null) {
            return List.of();
        }

        List<ScrapedPosting> result = new ArrayList<>();
        String publicationDate = date.format(API_DATE_FORMAT);
        for (BoePostingApiResponse item : response) {
            result.add(new ScrapedPosting(
                    item.getTitle(), item.getOrganization(), publicationDate, item.getUrl(), item.getPdfUrl()));
        }
        return result;
    }

    public List<ScrapedPosting> fetchSeleniumPostings(Watcher watcher) {
        String url = baseUrl + "/api/scraper/selenium";

        SeleniumScrapeRequestDto request = new SeleniumScrapeRequestDto(
                watcher.getTargetUrl(),
                watcher.getListSelector(),
                watcher.getTitleSelector(),
                watcher.getOrganizationSelector(),
                watcher.getDateSelector(),
                watcher.getLinkSelector()
        );

        SeleniumPostingApiResponse[] response =
                restTemplate.postForObject(url, request, SeleniumPostingApiResponse[].class);
        if (response == null) {
            return List.of();
        }

        List<ScrapedPosting> result = new ArrayList<>();
        for (SeleniumPostingApiResponse item : response) {
            result.add(new ScrapedPosting(item.getTitle(), item.getOrganization(), item.getPublicationDate(), item.getUrl(), null));
        }
        return result;
    }

    public List<ScrapedPosting> fetchJsonApiPostings(Watcher watcher) {
        String url = baseUrl + "/api/scraper/json";

        JsonApiScrapeRequestDto request = new JsonApiScrapeRequestDto(
                watcher.getTargetUrl(),
                watcher.getItemsPath(),
                watcher.getTitleField(),
                watcher.getLinkField(),
                watcher.getDateField()
        );

        JsonApiPostingApiResponse[] response =
                restTemplate.postForObject(url, request, JsonApiPostingApiResponse[].class);
        if (response == null) {
            return List.of();
        }

        List<ScrapedPosting> result = new ArrayList<>();
        for (JsonApiPostingApiResponse item : response) {
            result.add(new ScrapedPosting(
                    item.getTitle(), watcher.getOrganizationText(), item.getPublicationDate(), item.getUrl(), null));
        }
        return result;
    }

    public Optional<String> fetchDocumentText(String pdfUrl) {
        String url = UriComponentsBuilder.fromUriString(baseUrl + "/api/scraper/document-text")
                .queryParam("url", pdfUrl)
                .toUriString();

        DocumentTextApiResponse response = restTemplate.getForObject(url, DocumentTextApiResponse.class);
        if (response == null || !response.isTextFound()) {
            return Optional.empty();
        }
        return Optional.ofNullable(response.getText());
    }

    static class DocumentTextApiResponse {
        private boolean textFound;
        private String text;

        public boolean isTextFound() { return textFound; }
        public void setTextFound(boolean textFound) { this.textFound = textFound; }

        public String getText() { return text; }
        public void setText(String text) { this.text = text; }
    }
}