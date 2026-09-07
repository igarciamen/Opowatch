package com.igarciamen.watchers.service;

import com.igarciamen.watchers.client.NotificationsClient;
import com.igarciamen.watchers.client.ScraperEngineClient;
import com.igarciamen.watchers.client.UsersClient;
import com.igarciamen.watchers.dto.ScrapedPosting;
import com.igarciamen.watchers.enums.SourceType;
import com.igarciamen.watchers.model.MatchedItem;
import com.igarciamen.watchers.model.Watcher;
import com.igarciamen.watchers.repository.MatchedItemRepository;
import com.igarciamen.watchers.repository.WatcherRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ScrapingExecutionServiceTest {

    @Mock private ScraperEngineClient scraperEngineClient;
    @Mock private KeywordFilterService keywordFilterService;
    @Mock private MatchedItemRepository matchedItemRepository;
    @Mock private WatcherRepository watcherRepository;
    @Mock private UsersClient usersClient;
    @Mock private NotificationsClient notificationsClient;

    private ScrapingExecutionService scrapingExecutionService;

    @BeforeEach
    void setUp() {
        // deep-check desactivado aqui a proposito: estos tests validan el flujo de
        // notificacion y guardado, no la comprobacion profunda de PDF del BOE.
        scrapingExecutionService = new ScrapingExecutionService(
                scraperEngineClient, keywordFilterService, matchedItemRepository, watcherRepository,
                usersClient, notificationsClient, false, 15);
    }

    private Watcher boeWatcher() {
        Watcher watcher = new Watcher("BOE - Pruebas", SourceType.BOE_API, 60);
        watcher.setId(1L);
        watcher.setKeywords("informatica");
        return watcher;
    }

    @Test
    void runWatcher_newItemFound_notifiesSubscribers() {
        Watcher watcher = boeWatcher();

        List<ScrapedPosting> postings = List.of(
                new ScrapedPosting("Analista programador", "Ministerio", "20260814", "https://boe.es/1", null));

        when(scraperEngineClient.fetchBoePostings(any(LocalDate.class))).thenReturn(postings);
        when(keywordFilterService.filter(postings, "informatica")).thenReturn(postings);
        when(matchedItemRepository.findByWatcherAndSourceUrl(watcher, "https://boe.es/1")).thenReturn(Optional.empty());
        when(usersClient.fetchSubscriberEmails()).thenReturn(List.of("subscriptor@example.com"));

        ScrapingExecutionService.ScrapeOutcome outcome = scrapingExecutionService.runWatcher(watcher);

        assertEquals(1, outcome.newItemsFound());
        assertEquals(0, outcome.updatedItemsFound());
        verify(notificationsClient).notifySubscribers(eq("BOE - Pruebas"), any(), eq(List.of("subscriptor@example.com")));

        System.out.println("=== runWatcher_newItemFound_notifiesSubscribers ===");
        System.out.println("Nuevos: " + outcome.newItemsFound() + ", actualizados: " + outcome.updatedItemsFound());
    }

    @Test
    void runWatcher_existingItemWithChangedContent_countsAsUpdated() {
        Watcher watcher = boeWatcher();

        List<ScrapedPosting> postings = List.of(
                new ScrapedPosting("Analista programador senior", "Ministerio nuevo", "20260815", "https://boe.es/1", null));

        MatchedItem existing = new MatchedItem(watcher, "Analista programador", "Ministerio", "20260814", "https://boe.es/1");
        existing.setId(10L);
        existing.setContentHash("hash-antiguo-que-no-coincide");

        when(scraperEngineClient.fetchBoePostings(any(LocalDate.class))).thenReturn(postings);
        when(keywordFilterService.filter(postings, "informatica")).thenReturn(postings);
        when(matchedItemRepository.findByWatcherAndSourceUrl(watcher, "https://boe.es/1")).thenReturn(Optional.of(existing));
        when(usersClient.fetchSubscriberEmails()).thenReturn(List.of("subscriptor@example.com"));

        ScrapingExecutionService.ScrapeOutcome outcome = scrapingExecutionService.runWatcher(watcher);

        assertEquals(0, outcome.newItemsFound());
        assertEquals(1, outcome.updatedItemsFound());
        assertEquals("Analista programador senior", existing.getTitle());
        assertEquals("Ministerio nuevo", existing.getOrganization());
        verify(matchedItemRepository).save(existing);
        verify(notificationsClient).notifySubscribers(eq("BOE - Pruebas"), any(), eq(List.of("subscriptor@example.com")));

        System.out.println("=== runWatcher_existingItemWithChangedContent_countsAsUpdated ===");
        System.out.println("Nuevos: " + outcome.newItemsFound() + ", actualizados: " + outcome.updatedItemsFound());
    }

    @Test
    void runWatcher_existingItemWithSameContent_doesNothing() {
        Watcher watcher = boeWatcher();

        ScrapedPosting posting = new ScrapedPosting("Analista programador", "Ministerio", "20260814", "https://boe.es/1", null);
        List<ScrapedPosting> postings = List.of(posting);

        MatchedItem existing = new MatchedItem(watcher, "Analista programador", "Ministerio", "20260814", "https://boe.es/1");
        existing.setId(10L);
        // Mismo hash que generaria este mismo posting, para simular que no ha cambiado nada.
        existing.setContentHash(sha256("Analista programador|Ministerio|20260814"));

        when(scraperEngineClient.fetchBoePostings(any(LocalDate.class))).thenReturn(postings);
        when(keywordFilterService.filter(postings, "informatica")).thenReturn(postings);
        when(matchedItemRepository.findByWatcherAndSourceUrl(watcher, "https://boe.es/1")).thenReturn(Optional.of(existing));

        ScrapingExecutionService.ScrapeOutcome outcome = scrapingExecutionService.runWatcher(watcher);

        assertEquals(0, outcome.newItemsFound());
        assertEquals(0, outcome.updatedItemsFound());
        verify(matchedItemRepository, never()).save(any());
        verify(usersClient, never()).fetchSubscriberEmails();
        verify(notificationsClient, never()).notifySubscribers(any(), any(), any());

        System.out.println("=== runWatcher_existingItemWithSameContent_doesNothing ===");
        System.out.println("Nuevos: " + outcome.newItemsFound() + ", actualizados: " + outcome.updatedItemsFound());
    }

    @Test
    void runWatcher_noNewOrUpdatedItems_doesNotCallNotifications() {
        Watcher watcher = boeWatcher();

        when(scraperEngineClient.fetchBoePostings(any(LocalDate.class))).thenReturn(List.of());
        when(keywordFilterService.filter(List.of(), "informatica")).thenReturn(List.of());

        ScrapingExecutionService.ScrapeOutcome outcome = scrapingExecutionService.runWatcher(watcher);

        assertEquals(0, outcome.newItemsFound());
        assertEquals(0, outcome.updatedItemsFound());
        verify(usersClient, never()).fetchSubscriberEmails();
        verify(notificationsClient, never()).notifySubscribers(any(), any(), any());

        System.out.println("=== runWatcher_noNewOrUpdatedItems_doesNotCallNotifications ===");
        System.out.println("Sin novedades, no se llama ni a users ni a notifications");
    }

    @Test
    void runWatcher_noSubscribers_doesNotCallNotifications() {
        Watcher watcher = boeWatcher();

        List<ScrapedPosting> postings = List.of(
                new ScrapedPosting("Analista programador", "Ministerio", "20260814", "https://boe.es/1", null));

        when(scraperEngineClient.fetchBoePostings(any(LocalDate.class))).thenReturn(postings);
        when(keywordFilterService.filter(postings, "informatica")).thenReturn(postings);
        when(matchedItemRepository.findByWatcherAndSourceUrl(watcher, "https://boe.es/1")).thenReturn(Optional.empty());
        when(usersClient.fetchSubscriberEmails()).thenReturn(List.of());

        ScrapingExecutionService.ScrapeOutcome outcome = scrapingExecutionService.runWatcher(watcher);

        assertEquals(1, outcome.newItemsFound());
        verify(notificationsClient, never()).notifySubscribers(any(), any(), any());

        System.out.println("=== runWatcher_noSubscribers_doesNotCallNotifications ===");
        System.out.println("Sin suscriptores, se guarda el item pero no se llama a notifications");
    }

    @Test
    void runWatcher_notificationFailure_doesNotBreakTheRun() {
        Watcher watcher = boeWatcher();

        List<ScrapedPosting> postings = List.of(
                new ScrapedPosting("Analista programador", "Ministerio", "20260814", "https://boe.es/1", null));

        when(scraperEngineClient.fetchBoePostings(any(LocalDate.class))).thenReturn(postings);
        when(keywordFilterService.filter(postings, "informatica")).thenReturn(postings);
        when(matchedItemRepository.findByWatcherAndSourceUrl(watcher, "https://boe.es/1")).thenReturn(Optional.empty());
        when(usersClient.fetchSubscriberEmails()).thenThrow(new RuntimeException("users is down"));

        ScrapingExecutionService.ScrapeOutcome outcome = scrapingExecutionService.runWatcher(watcher);

        assertEquals(1, outcome.newItemsFound());
        verify(watcherRepository).save(watcher);

        System.out.println("=== runWatcher_notificationFailure_doesNotBreakTheRun ===");
        System.out.println("Aunque falle la notificacion, el item se guarda y el metodo no lanza excepcion");
    }

    // Replica el mismo algoritmo de hash que usa ScrapingExecutionService, solo para este test.
    private String sha256(String raw) {
        try {
            java.security.MessageDigest digest = java.security.MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(raw.getBytes(java.nio.charset.StandardCharsets.UTF_8));
            StringBuilder hex = new StringBuilder();
            for (byte b : hashBytes) {
                hex.append(String.format("%02x", b));
            }
            return hex.toString();
        } catch (java.security.NoSuchAlgorithmException ex) {
            throw new IllegalStateException(ex);
        }
    }
}