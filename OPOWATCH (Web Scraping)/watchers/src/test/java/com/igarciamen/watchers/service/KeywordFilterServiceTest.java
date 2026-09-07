package com.igarciamen.watchers.service;

import com.igarciamen.watchers.dto.ScrapedPosting;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class KeywordFilterServiceTest {

    private final KeywordFilterService filterService = new KeywordFilterService();

    private static final String IT_KEYWORDS = "informatica,TIC,sistemas,programador,desarrollador,ciberseguridad";

    @Test
    void matchesKeywords_matchesRegardlessOfCase() {
        List<String> keywords = filterService.parseKeywords(IT_KEYWORDS);

        assertTrue(filterService.matchesKeywords("Convocatoria de PROGRAMADOR senior", keywords));
        assertTrue(filterService.matchesKeywords("convocatoria de programador senior", keywords));

        System.out.println("=== matchesKeywords_matchesRegardlessOfCase ===");
        System.out.println("Coincide en mayusculas y minusculas: true");
    }

    @Test
    void matchesKeywords_matchesRegardlessOfAccents() {
        List<String> keywords = filterService.parseKeywords(IT_KEYWORDS);

        // La palabra clave se escribe sin tilde, el titulo real la trae con tilde.
        assertTrue(filterService.matchesKeywords("Plaza de Técnico de Sistemas y Tecnologías de la Información", keywords));

        System.out.println("=== matchesKeywords_matchesRegardlessOfAccents ===");
        System.out.println("Coincide aunque el titulo lleve tildes y la palabra clave no: true");
    }

    @Test
    void matchesKeywords_rejectsUnrelatedTitles() {
        List<String> keywords = filterService.parseKeywords(IT_KEYWORDS);

        assertFalse(filterService.matchesKeywords("Plaza de Auxiliar de Enfermeria", keywords));
        assertFalse(filterService.matchesKeywords("Convocatoria de Bombero", keywords));

        System.out.println("=== matchesKeywords_rejectsUnrelatedTitles ===");
        System.out.println("Titulos sin relacion con las palabras clave quedan excluidos: true");
    }

    @Test
    void matchesKeywords_returnsFalseForNullOrBlankTitle() {
        List<String> keywords = filterService.parseKeywords(IT_KEYWORDS);

        assertFalse(filterService.matchesKeywords(null, keywords));
        assertFalse(filterService.matchesKeywords("   ", keywords));

        System.out.println("=== matchesKeywords_returnsFalseForNullOrBlankTitle ===");
        System.out.println("Titulo nulo o vacio nunca coincide: true");
    }

    @Test
    void matchesKeywords_returnsFalseWhenNoKeywordsConfigured() {
        List<String> keywords = filterService.parseKeywords(null);

        assertFalse(filterService.matchesKeywords("Analista Programador", keywords));

        System.out.println("=== matchesKeywords_returnsFalseWhenNoKeywordsConfigured ===");
        System.out.println("Sin palabras clave configuradas, nunca hay coincidencia: true");
    }

    @Test
    void filter_keepsOnlyMatchingPostings() {
        List<ScrapedPosting> postings = List.of(
                new ScrapedPosting("Tecnico de Sistemas", "Ministerio A", "20260814", "https://boe.es/1", null),
                new ScrapedPosting("Auxiliar Administrativo", "Ministerio B", "20260814", "https://boe.es/2", null),
                new ScrapedPosting("Analista Programador", "Ayuntamiento X", "20260814", "https://example.org/3", null),
                new ScrapedPosting("Bombero", "Ayuntamiento Y", "20260814", "https://example.org/4", null)
        );

        List<ScrapedPosting> result = filterService.filter(postings, IT_KEYWORDS);

        assertEquals(2, result.size());
        assertTrue(result.stream().anyMatch(p -> p.getTitle().equals("Tecnico de Sistemas")));
        assertTrue(result.stream().anyMatch(p -> p.getTitle().equals("Analista Programador")));

        System.out.println("=== filter_keepsOnlyMatchingPostings ===");
        System.out.println("De 4 ofertas, se quedan " + result.size() + " que coinciden con las palabras clave");
    }

    @Test
    void filter_returnsEmptyWhenWatcherHasNoKeywords() {
        List<ScrapedPosting> postings = List.of(
                new ScrapedPosting("Analista Programador", "Ayuntamiento X", "20260814", "https://example.org/3", null)
        );

        List<ScrapedPosting> result = filterService.filter(postings, null);

        assertTrue(result.isEmpty());

        System.out.println("=== filter_returnsEmptyWhenWatcherHasNoKeywords ===");
        System.out.println("Un watcher sin palabras clave configuradas no encuentra nada: true");
    }
}