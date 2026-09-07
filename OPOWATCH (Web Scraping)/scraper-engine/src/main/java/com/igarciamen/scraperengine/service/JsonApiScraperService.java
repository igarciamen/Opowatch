package com.igarciamen.scraperengine.service;

import com.igarciamen.scraperengine.payloads.request.JsonApiScrapeRequest;
import com.igarciamen.scraperengine.payloads.response.JsonApiPostingResponse;
import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.Option;
import com.jayway.jsonpath.TypeRef;
import com.jayway.jsonpath.spi.json.JacksonJsonProvider;
import com.jayway.jsonpath.spi.mapper.JacksonMappingProvider;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class JsonApiScraperService {

    // Uses Jackson explicitly instead of JsonPath's default json-smart provider, since
    // json-smart cannot convert results into generic types like List<Map<String, Object>>.
    // Also tolerant to missing keys: real APIs like Zaragoza's do not include "items" on
    // every category, and a single missing key should not break the whole read.
    private static final Configuration LENIENT_CONFIG = Configuration.builder()
            .jsonProvider(new JacksonJsonProvider())
            .mappingProvider(new JacksonMappingProvider())
            .options(Option.SUPPRESS_EXCEPTIONS, Option.DEFAULT_PATH_LEAF_TO_NULL)
            .build();

    private final RestTemplate restTemplate;

    public JsonApiScraperService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public List<JsonApiPostingResponse> scrape(JsonApiScrapeRequest request) {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(
                request.getTargetUrl(), HttpMethod.GET, entity, String.class);

        List<Map<String, Object>> items = JsonPath.using(LENIENT_CONFIG)
                .parse(response.getBody())
                .read(request.getItemsPath(), new TypeRef<List<Map<String, Object>>>() {});

        List<JsonApiPostingResponse> result = new ArrayList<>();
        if (items == null) {
            return result;
        }

        for (Map<String, Object> item : items) {
            if (item == null) {
                continue;
            }
            String title = asString(item.get(request.getTitleField()));
            String url = asString(item.get(request.getLinkField()));

            if (title == null || title.isBlank() || url == null || url.isBlank()) {
                continue;
            }

            String date = request.getDateField() != null
                    ? asString(item.get(request.getDateField()))
                    : null;

            result.add(new JsonApiPostingResponse(title.trim(), url, date));
        }

        return result;
    }

    private String asString(Object value) {
        return value != null ? value.toString() : null;
    }
}