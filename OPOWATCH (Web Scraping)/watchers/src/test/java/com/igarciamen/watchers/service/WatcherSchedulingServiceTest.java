package com.igarciamen.watchers.service;

import com.igarciamen.watchers.enums.SourceType;
import com.igarciamen.watchers.model.Watcher;
import com.igarciamen.watchers.repository.WatcherRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WatcherSchedulingServiceTest {

    @Mock private WatcherRepository watcherRepository;
    @Mock private ScrapingExecutionService scrapingExecutionService;

    @InjectMocks
    private WatcherSchedulingService watcherSchedulingService;

    @Test
    void runDueWatchers_runsWatcherNeverScrapedBefore() {
        Watcher watcher = new Watcher("Nunca ejecutado", SourceType.BOE_API, 60);
        watcher.setId(1L);
        watcher.setLastScrapedAt(null);

        when(watcherRepository.findByActiveTrue()).thenReturn(List.of(watcher));

        watcherSchedulingService.runDueWatchers();

        verify(scrapingExecutionService).runWatcher(watcher);

        System.out.println("=== runDueWatchers_runsWatcherNeverScrapedBefore ===");
        System.out.println("Un watcher con lastScrapedAt nulo se ejecuta siempre: true");
    }

    @Test
    void runDueWatchers_skipsWatcherWithinItsInterval() {
        Watcher watcher = new Watcher("Recien ejecutado", SourceType.BOE_API, 60);
        watcher.setId(2L);
        watcher.setLastScrapedAt(LocalDateTime.now().minusMinutes(5));

        when(watcherRepository.findByActiveTrue()).thenReturn(List.of(watcher));

        watcherSchedulingService.runDueWatchers();

        verify(scrapingExecutionService, never()).runWatcher(any());

        System.out.println("=== runDueWatchers_skipsWatcherWithinItsInterval ===");
        System.out.println("Un watcher ejecutado hace 5 min con intervalo de 60 min no se ejecuta: true");
    }

    @Test
    void runDueWatchers_runsWatcherPastItsInterval() {
        Watcher watcher = new Watcher("Intervalo cumplido", SourceType.BOE_API, 30);
        watcher.setId(3L);
        watcher.setLastScrapedAt(LocalDateTime.now().minusMinutes(45));

        when(watcherRepository.findByActiveTrue()).thenReturn(List.of(watcher));

        watcherSchedulingService.runDueWatchers();

        verify(scrapingExecutionService).runWatcher(watcher);

        System.out.println("=== runDueWatchers_runsWatcherPastItsInterval ===");
        System.out.println("Un watcher con 45 min transcurridos e intervalo de 30 min se ejecuta: true");
    }

    @Test
    void runDueWatchers_oneWatcherFailing_doesNotStopTheOthers() {
        Watcher failing = new Watcher("Falla", SourceType.SELENIUM, 30);
        failing.setId(4L);
        failing.setLastScrapedAt(null);

        Watcher healthy = new Watcher("Sano", SourceType.BOE_API, 30);
        healthy.setId(5L);
        healthy.setLastScrapedAt(null);

        when(watcherRepository.findByActiveTrue()).thenReturn(List.of(failing, healthy));
        when(scrapingExecutionService.runWatcher(failing)).thenThrow(new RuntimeException("Site is down"));

        watcherSchedulingService.runDueWatchers();

        verify(scrapingExecutionService).runWatcher(failing);
        verify(scrapingExecutionService).runWatcher(healthy);

        System.out.println("=== runDueWatchers_oneWatcherFailing_doesNotStopTheOthers ===");
        System.out.println("El fallo de un watcher no impide que se ejecute el siguiente: true");
    }
}