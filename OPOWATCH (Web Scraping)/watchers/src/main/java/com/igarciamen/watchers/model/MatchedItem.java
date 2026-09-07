package com.igarciamen.watchers.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "matched_items",
        schema = "public",
        uniqueConstraints = @UniqueConstraint(columnNames = {"watcher_id", "source_url"})
)
public class MatchedItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "watcher_id", nullable = false)
    private Watcher watcher;

    @Column(nullable = false, length = 1000)
    private String title;

    @Column(length = 300)
    private String organization;

    @Column(length = 50)
    private String publicationDate;

    @Column(name = "source_url", nullable = false, length = 500)
    private String sourceUrl;

    @Column(nullable = false)
    private LocalDateTime detectedAt = LocalDateTime.now();

    // SHA-256 fingerprint of title + organization + publicationDate. Lets us tell apart
    // "this URL was already known but its content changed" from "nothing really changed".
    @Column(length = 64)
    private String contentHash;

    public MatchedItem() {}

    public MatchedItem(Watcher watcher, String title, String organization, String publicationDate, String sourceUrl) {
        this.watcher = watcher;
        this.title = title;
        this.organization = organization;
        this.publicationDate = publicationDate;
        this.sourceUrl = sourceUrl;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Watcher getWatcher() { return watcher; }
    public void setWatcher(Watcher watcher) { this.watcher = watcher; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getOrganization() { return organization; }
    public void setOrganization(String organization) { this.organization = organization; }

    public String getPublicationDate() { return publicationDate; }
    public void setPublicationDate(String publicationDate) { this.publicationDate = publicationDate; }

    public String getSourceUrl() { return sourceUrl; }
    public void setSourceUrl(String sourceUrl) { this.sourceUrl = sourceUrl; }

    public LocalDateTime getDetectedAt() { return detectedAt; }
    public void setDetectedAt(LocalDateTime detectedAt) { this.detectedAt = detectedAt; }

    public String getContentHash() { return contentHash; }
    public void setContentHash(String contentHash) { this.contentHash = contentHash; }
}