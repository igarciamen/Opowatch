package com.igarciamen.scraperengine.client;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.igarciamen.scraperengine.dto.boe.BoeSumarioResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Component
public class BoeApiClient {

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyyMMdd");

    private final RestTemplate restTemplate;
    private final String baseUrl;

    // Configured separately from the app's default JSON converter, so a single object
    // in the BOE response (epigrafe or item) is read as a one-element list automatically.
    private final ObjectMapper boeMapper = new ObjectMapper()
            .configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true)
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    public BoeApiClient(RestTemplate restTemplate, @Value("${boe.api.base-url}") String baseUrl) {
        this.restTemplate = restTemplate;
        this.baseUrl = baseUrl;
    }

    public Optional<BoeSumarioResponse> fetchSummary(LocalDate date) {
        String url = baseUrl + "/" + date.format(DATE_FORMAT);

        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        try {
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
            BoeSumarioResponse parsed = boeMapper.readValue(response.getBody(), BoeSumarioResponse.class);
            return Optional.of(parsed);
        } catch (HttpClientErrorException.NotFound ex) {
            // No BOE issue published on this date (typically Sundays).
            return Optional.empty();
        } catch (Exception ex) {
            throw new IllegalStateException("Could not fetch or parse BOE summary for " + date, ex);
        }
    }
}