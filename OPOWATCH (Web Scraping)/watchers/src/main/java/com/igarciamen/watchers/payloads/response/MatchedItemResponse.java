package com.igarciamen.watchers.payloads.response;

import java.time.LocalDateTime;

public class MatchedItemResponse {

    private Long id;
    private String watcherName;
    private String title;
    private String organization;
    private String publicationDate;
    private String sourceUrl;
    private LocalDateTime detectedAt;

    public MatchedItemResponse(Long id, String watcherName, String title, String organization,
                                String publicationDate, String sourceUrl, LocalDateTime detectedAt) {
        this.id = id;
        this.watcherName = watcherName;
        this.title = title;
        this.organization = organization;
        this.publicationDate = publicationDate;
        this.sourceUrl = sourceUrl;
        this.detectedAt = detectedAt;
    }

    public Long getId() { return id; }
    public String getWatcherName() { return watcherName; }
    public String getTitle() { return title; }
    public String getOrganization() { return organization; }
    public String getPublicationDate() { return publicationDate; }
    public String getSourceUrl() { return sourceUrl; }
    public LocalDateTime getDetectedAt() { return detectedAt; }
}