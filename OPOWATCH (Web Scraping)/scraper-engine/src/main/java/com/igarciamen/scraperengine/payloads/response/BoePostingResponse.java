package com.igarciamen.scraperengine.payloads.response;

public class BoePostingResponse {
    private String identifier;
    private String title;
    private String organization;
    private String url;
    private String pdfUrl;

    public BoePostingResponse(String identifier, String title, String organization, String url, String pdfUrl) {
        this.identifier = identifier;
        this.title = title;
        this.organization = organization;
        this.url = url;
        this.pdfUrl = pdfUrl;
    }

    public String getIdentifier() { return identifier; }
    public String getTitle() { return title; }
    public String getOrganization() { return organization; }
    public String getUrl() { return url; }
    public String getPdfUrl() { return pdfUrl; }
}