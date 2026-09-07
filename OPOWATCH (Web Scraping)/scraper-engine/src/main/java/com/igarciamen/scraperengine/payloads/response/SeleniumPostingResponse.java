package com.igarciamen.scraperengine.payloads.response;

public class SeleniumPostingResponse {
    private String title;
    private String organization;
    private String publicationDate;
    private String url;

    public SeleniumPostingResponse(String title, String organization, String publicationDate, String url) {
        this.title = title;
        this.organization = organization;
        this.publicationDate = publicationDate;
        this.url = url;
    }

    public String getTitle() { return title; }
    public String getOrganization() { return organization; }
    public String getPublicationDate() { return publicationDate; }
    public String getUrl() { return url; }
}