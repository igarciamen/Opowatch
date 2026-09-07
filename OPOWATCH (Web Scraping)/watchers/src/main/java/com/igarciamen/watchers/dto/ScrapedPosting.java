package com.igarciamen.watchers.dto;

public class ScrapedPosting {

    private final String title;
    private final String organization;
    private final String publicationDate;
    private final String sourceUrl;
    private final String pdfUrl;

    public ScrapedPosting(String title, String organization, String publicationDate, String sourceUrl, String pdfUrl) {
        this.title = title;
        this.organization = organization;
        this.publicationDate = publicationDate;
        this.sourceUrl = sourceUrl;
        this.pdfUrl = pdfUrl;
    }

    public String getTitle() { return title; }
    public String getOrganization() { return organization; }
    public String getPublicationDate() { return publicationDate; }
    public String getSourceUrl() { return sourceUrl; }
    public String getPdfUrl() { return pdfUrl; }
}