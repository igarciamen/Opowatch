package com.igarciamen.watchers.payloads.response;

import com.igarciamen.watchers.enums.SourceType;

import java.time.LocalDateTime;

public class WatcherResponse {

    private Long id;
    private String name;
    private SourceType sourceType;
    private String targetUrl;
    private String listSelector;
    private String titleSelector;
    private String organizationSelector;
    private String dateSelector;
    private String linkSelector;
    private String itemsPath;
    private String titleField;
    private String linkField;
    private String dateField;
    private String organizationText;
    private String keywords;
    private int scrapeIntervalMinutes;
    private boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime lastScrapedAt;

    public WatcherResponse(Long id, String name, SourceType sourceType, String targetUrl,
                           String listSelector, String titleSelector, String organizationSelector,
                           String dateSelector, String linkSelector, String itemsPath, String titleField,
                           String linkField, String dateField, String organizationText, String keywords,
                           int scrapeIntervalMinutes, boolean active, LocalDateTime createdAt,
                           LocalDateTime lastScrapedAt) {
        this.id = id;
        this.name = name;
        this.sourceType = sourceType;
        this.targetUrl = targetUrl;
        this.listSelector = listSelector;
        this.titleSelector = titleSelector;
        this.organizationSelector = organizationSelector;
        this.dateSelector = dateSelector;
        this.linkSelector = linkSelector;
        this.itemsPath = itemsPath;
        this.titleField = titleField;
        this.linkField = linkField;
        this.dateField = dateField;
        this.organizationText = organizationText;
        this.keywords = keywords;
        this.scrapeIntervalMinutes = scrapeIntervalMinutes;
        this.active = active;
        this.createdAt = createdAt;
        this.lastScrapedAt = lastScrapedAt;
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public SourceType getSourceType() { return sourceType; }
    public String getTargetUrl() { return targetUrl; }
    public String getListSelector() { return listSelector; }
    public String getTitleSelector() { return titleSelector; }
    public String getOrganizationSelector() { return organizationSelector; }
    public String getDateSelector() { return dateSelector; }
    public String getLinkSelector() { return linkSelector; }
    public String getItemsPath() { return itemsPath; }
    public String getTitleField() { return titleField; }
    public String getLinkField() { return linkField; }
    public String getDateField() { return dateField; }
    public String getOrganizationText() { return organizationText; }
    public String getKeywords() { return keywords; }
    public int getScrapeIntervalMinutes() { return scrapeIntervalMinutes; }
    public boolean isActive() { return active; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getLastScrapedAt() { return lastScrapedAt; }
}