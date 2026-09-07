package com.igarciamen.watchers.model;

import com.igarciamen.watchers.enums.SourceType;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "watchers", schema = "public")
public class Watcher {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private SourceType sourceType;

    private String targetUrl;
    private String listSelector;
    private String titleSelector;
    private String organizationSelector;
    private String dateSelector;
    private String linkSelector;

    // Fields for JSON_API sources.
    private String itemsPath;
    private String titleField;
    private String linkField;
    private String dateField;
    private String organizationText;

    @Column(length = 1000)
    private String keywords;

    @Column(nullable = false)
    private int scrapeIntervalMinutes;

    @Column(nullable = false)
    private boolean active = true;

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    private LocalDateTime lastScrapedAt;

    @OneToMany(mappedBy = "watcher", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MatchedItem> matchedItems = new ArrayList<>();

    public Watcher() {}

    public Watcher(String name, SourceType sourceType, int scrapeIntervalMinutes) {
        this.name = name;
        this.sourceType = sourceType;
        this.scrapeIntervalMinutes = scrapeIntervalMinutes;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public SourceType getSourceType() { return sourceType; }
    public void setSourceType(SourceType sourceType) { this.sourceType = sourceType; }

    public String getTargetUrl() { return targetUrl; }
    public void setTargetUrl(String targetUrl) { this.targetUrl = targetUrl; }

    public String getListSelector() { return listSelector; }
    public void setListSelector(String listSelector) { this.listSelector = listSelector; }

    public String getTitleSelector() { return titleSelector; }
    public void setTitleSelector(String titleSelector) { this.titleSelector = titleSelector; }

    public String getOrganizationSelector() { return organizationSelector; }
    public void setOrganizationSelector(String organizationSelector) { this.organizationSelector = organizationSelector; }

    public String getDateSelector() { return dateSelector; }
    public void setDateSelector(String dateSelector) { this.dateSelector = dateSelector; }

    public String getLinkSelector() { return linkSelector; }
    public void setLinkSelector(String linkSelector) { this.linkSelector = linkSelector; }

    public String getItemsPath() { return itemsPath; }
    public void setItemsPath(String itemsPath) { this.itemsPath = itemsPath; }

    public String getTitleField() { return titleField; }
    public void setTitleField(String titleField) { this.titleField = titleField; }

    public String getLinkField() { return linkField; }
    public void setLinkField(String linkField) { this.linkField = linkField; }

    public String getDateField() { return dateField; }
    public void setDateField(String dateField) { this.dateField = dateField; }

    public String getOrganizationText() { return organizationText; }
    public void setOrganizationText(String organizationText) { this.organizationText = organizationText; }

    public String getKeywords() { return keywords; }
    public void setKeywords(String keywords) { this.keywords = keywords; }

    public int getScrapeIntervalMinutes() { return scrapeIntervalMinutes; }
    public void setScrapeIntervalMinutes(int scrapeIntervalMinutes) { this.scrapeIntervalMinutes = scrapeIntervalMinutes; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getLastScrapedAt() { return lastScrapedAt; }
    public void setLastScrapedAt(LocalDateTime lastScrapedAt) { this.lastScrapedAt = lastScrapedAt; }

    public List<MatchedItem> getMatchedItems() { return matchedItems; }
    public void setMatchedItems(List<MatchedItem> matchedItems) { this.matchedItems = matchedItems; }
}