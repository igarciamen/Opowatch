package com.igarciamen.watchers.service;

import com.igarciamen.watchers.client.NotificationsClient;
import com.igarciamen.watchers.client.ScraperEngineClient;
import com.igarciamen.watchers.client.UsersClient;
import com.igarciamen.watchers.client.dto.PostingSummaryDto;
import com.igarciamen.watchers.dto.ScrapedPosting;
import com.igarciamen.watchers.enums.SourceType;
import com.igarciamen.watchers.model.MatchedItem;
import com.igarciamen.watchers.model.Watcher;
import com.igarciamen.watchers.repository.MatchedItemRepository;
import com.igarciamen.watchers.repository.WatcherRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ScrapingExecutionService {

    private static final Logger log = LoggerFactory.getLogger(ScrapingExecutionService.class);

    private final ScraperEngineClient scraperEngineClient;
    private final KeywordFilterService keywordFilterService;
    private final MatchedItemRepository matchedItemRepository;
    private final WatcherRepository watcherRepository;
    private final UsersClient usersClient;
    private final NotificationsClient notificationsClient;
    private final boolean deepCheckEnabled;
    private final int deepCheckMaxPerRun;

    public ScrapingExecutionService(ScraperEngineClient scraperEngineClient,
                                    KeywordFilterService keywordFilterService,
                                    MatchedItemRepository matchedItemRepository,
                                    WatcherRepository watcherRepository,
                                    UsersClient usersClient,
                                    NotificationsClient notificationsClient,
                                    @Value("${watchers.deep-check-enabled:true}") boolean deepCheckEnabled,
                                    @Value("${watchers.deep-check-max-per-run:15}") int deepCheckMaxPerRun) {
        this.scraperEngineClient = scraperEngineClient;
        this.keywordFilterService = keywordFilterService;
        this.matchedItemRepository = matchedItemRepository;
        this.watcherRepository = watcherRepository;
        this.usersClient = usersClient;
        this.notificationsClient = notificationsClient;
        this.deepCheckEnabled = deepCheckEnabled;
        this.deepCheckMaxPerRun = deepCheckMaxPerRun;
    }

    public ScrapeOutcome runWatcher(Watcher watcher) {
        if (watcher.getKeywords() == null || watcher.getKeywords().isBlank()) {
            log.warn("Watcher '{}' has no keywords configured, skipping (edit it and set keywords first).", watcher.getName());
        }

        List<ScrapedPosting> rawPostings;
        if (watcher.getSourceType() == SourceType.BOE_API) {
            rawPostings = scraperEngineClient.fetchBoePostings(LocalDate.now());
        } else if (watcher.getSourceType() == SourceType.SELENIUM) {
            rawPostings = scraperEngineClient.fetchSeleniumPostings(watcher);
        } else {
            rawPostings = scraperEngineClient.fetchJsonApiPostings(watcher);
        }

        List<ScrapedPosting> matched = keywordFilterService.filter(rawPostings, watcher.getKeywords());

        if (deepCheckEnabled && watcher.getSourceType() == SourceType.BOE_API) {
            matched = mergeWithDeepPdfMatches(rawPostings, matched, watcher.getKeywords());
        }

        List<MatchedItem> newItems = new ArrayList<>();
        List<MatchedItem> updatedItems = new ArrayList<>();

        for (ScrapedPosting posting : matched) {
            String hash = computeContentHash(posting);
            Optional<MatchedItem> existing = matchedItemRepository.findByWatcherAndSourceUrl(watcher, posting.getSourceUrl());

            if (existing.isEmpty()) {
                MatchedItem item = new MatchedItem(
                        watcher, posting.getTitle(), posting.getOrganization(),
                        posting.getPublicationDate(), posting.getSourceUrl());
                item.setContentHash(hash);
                matchedItemRepository.save(item);
                newItems.add(item);
            } else {
                MatchedItem item = existing.get();
                boolean changed = !hash.equals(item.getContentHash());
                if (changed) {
                    item.setTitle(posting.getTitle());
                    item.setOrganization(posting.getOrganization());
                    item.setPublicationDate(posting.getPublicationDate());
                    item.setContentHash(hash);
                    item.setDetectedAt(LocalDateTime.now());
                    matchedItemRepository.save(item);
                    updatedItems.add(item);
                }
            }
        }

        watcher.setLastScrapedAt(LocalDateTime.now());
        watcherRepository.save(watcher);

        List<MatchedItem> toNotify = new ArrayList<>(newItems);
        toNotify.addAll(updatedItems);
        if (!toNotify.isEmpty()) {
            notifySubscribersSafely(watcher, toNotify);
        }

        return new ScrapeOutcome(newItems.size(), updatedItems.size());
    }

    private String computeContentHash(ScrapedPosting posting) {
        String raw = safe(posting.getTitle()) + "|" + safe(posting.getOrganization()) + "|" + safe(posting.getPublicationDate());
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(raw.getBytes(StandardCharsets.UTF_8));
            StringBuilder hex = new StringBuilder();
            for (byte b : hashBytes) {
                hex.append(String.format("%02x", b));
            }
            return hex.toString();
        } catch (NoSuchAlgorithmException ex) {
            // SHA-256 is always available on the JVM, this branch is unreachable in practice.
            throw new IllegalStateException(ex);
        }
    }

    private String safe(String value) {
        return value == null ? "" : value;
    }

    private List<ScrapedPosting> mergeWithDeepPdfMatches(List<ScrapedPosting> allPostings,
                                                         List<ScrapedPosting> alreadyMatched,
                                                         String keywordsCsv) {
        List<ScrapedPosting> result = new ArrayList<>(alreadyMatched);
        List<String> normalizedKeywords = keywordFilterService.parseKeywords(keywordsCsv);

        int checksLeft = deepCheckMaxPerRun;
        for (ScrapedPosting posting : allPostings) {
            if (checksLeft <= 0) {
                break;
            }
            if (alreadyMatched.contains(posting) || posting.getPdfUrl() == null || posting.getPdfUrl().isBlank()) {
                continue;
            }

            checksLeft--;
            Optional<String> text = scraperEngineClient.fetchDocumentText(posting.getPdfUrl());
            if (text.isPresent() && keywordFilterService.matchesKeywords(text.get(), normalizedKeywords)) {
                result.add(posting);
                log.info("Deep PDF check rescued a generic posting: {}", posting.getSourceUrl());
            }

            sleepBriefly();
        }

        return result;
    }

    private void sleepBriefly() {
        try {
            Thread.sleep(300);
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
        }
    }

    private void notifySubscribersSafely(Watcher watcher, List<MatchedItem> items) {
        try {
            List<String> recipients = usersClient.fetchSubscriberEmails();
            if (recipients.isEmpty()) {
                return;
            }

            List<PostingSummaryDto> postings = items.stream()
                    .map(item -> new PostingSummaryDto(item.getTitle(), item.getOrganization(), item.getSourceUrl()))
                    .toList();

            notificationsClient.notifySubscribers(watcher.getName(), postings, recipients);
        } catch (Exception ex) {
            log.error("Could not notify subscribers for watcher '{}': {}", watcher.getName(), ex.getMessage());
        }
    }

    public record ScrapeOutcome(int newItemsFound, int updatedItemsFound) {}
}