package com.igarciamen.watchers.service;

import com.igarciamen.watchers.model.Watcher;
import com.igarciamen.watchers.repository.WatcherRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class WatcherSchedulingService {

    private static final Logger log = LoggerFactory.getLogger(WatcherSchedulingService.class);

    private final WatcherRepository watcherRepository;
    private final ScrapingExecutionService scrapingExecutionService;

    public WatcherSchedulingService(WatcherRepository watcherRepository,
                                    ScrapingExecutionService scrapingExecutionService) {
        this.watcherRepository = watcherRepository;
        this.scrapingExecutionService = scrapingExecutionService;
    }

    // Checks every minute which active watchers are due, based on their own interval.
    @Scheduled(fixedRate = 60_000)
    public void runDueWatchers() {
        List<Watcher> activeWatchers = watcherRepository.findByActiveTrue();

        for (Watcher watcher : activeWatchers) {
            if (!isDue(watcher)) {
                continue;
            }
            try {
                ScrapingExecutionService.ScrapeOutcome outcome = scrapingExecutionService.runWatcher(watcher);
                log.info("Watcher '{}' executed: {} new, {} updated",
                        watcher.getName(), outcome.newItemsFound(), outcome.updatedItemsFound());
            } catch (Exception ex) {
                // A single failing watcher (e.g. a site temporarily down) must not stop the others.
                log.error("Watcher '{}' failed: {}", watcher.getName(), ex.getMessage());
            }
        }
    }

    private boolean isDue(Watcher watcher) {
        if (watcher.getLastScrapedAt() == null) {
            return true;
        }
        long minutesSinceLastRun = Duration.between(watcher.getLastScrapedAt(), LocalDateTime.now()).toMinutes();
        return minutesSinceLastRun >= watcher.getScrapeIntervalMinutes();
    }
}