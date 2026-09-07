package com.igarciamen.scraperengine.service;

import com.igarciamen.scraperengine.payloads.request.SeleniumScrapeRequest;
import com.igarciamen.scraperengine.payloads.response.SeleniumPostingResponse;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Tag("integration")
class SeleniumScraperServiceTest {

    private final SeleniumScraperService service = new SeleniumScraperService();

    @Test
    void scrape_extractsPostingsFromRealPage() {
        SeleniumScrapeRequest request = new SeleniumScrapeRequest();
        request.setTargetUrl("https://books.toscrape.com/");
        request.setListSelector("article.product_pod");
        request.setTitleSelector("h3 a");
        request.setLinkSelector("h3 a");
        request.setOrganizationSelector("p.price_color");

        List<SeleniumPostingResponse> result = service.scrape(request);

        assertFalse(result.isEmpty());
        assertNotNull(result.get(0).getTitle());
        assertNotNull(result.get(0).getUrl());
        assertNotNull(result.get(0).getOrganization());

        System.out.println("=== scrape_extractsPostingsFromRealPage ===");
        System.out.println("Postings encontrados: " + result.size());
        System.out.println("Primero: " + result.get(0).getTitle() + " | " + result.get(0).getOrganization());
    }

    @Test
    void scrape_skipsItemsWithoutRequiredFields() {
        SeleniumScrapeRequest request = new SeleniumScrapeRequest();
        request.setTargetUrl("https://books.toscrape.com/");
        request.setListSelector("article.product_pod");
        // A selector que no existe en la pagina: ningun item deberia colarse.
        request.setTitleSelector(".este-selector-no-existe-en-la-pagina");
        request.setLinkSelector("h3 a");

        List<SeleniumPostingResponse> result = service.scrape(request);

        assertTrue(result.isEmpty());

        System.out.println("=== scrape_skipsItemsWithoutRequiredFields ===");
        System.out.println("Resultado con selector de titulo invalido: " + result.size() + " items (esperado 0)");
    }
}