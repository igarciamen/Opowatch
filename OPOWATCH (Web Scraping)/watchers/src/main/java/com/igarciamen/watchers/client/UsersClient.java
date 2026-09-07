package com.igarciamen.watchers.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

@Component
public class UsersClient {

    private final RestTemplate restTemplate;
    private final String baseUrl;
    private final String internalApiKey;

    public UsersClient(RestTemplate restTemplate,
                        @Value("${users.base-url}") String baseUrl,
                        @Value("${internal.api-key}") String internalApiKey) {
        this.restTemplate = restTemplate;
        this.baseUrl = baseUrl;
        this.internalApiKey = internalApiKey;
    }

    public List<String> fetchSubscriberEmails() {
        String url = baseUrl + "/api/user/subscribers";

        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Internal-Api-Key", internalApiKey);
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<String[]> response = restTemplate.exchange(url, HttpMethod.GET, entity, String[].class);
        String[] body = response.getBody();
        return body != null ? Arrays.asList(body) : List.of();
    }
}