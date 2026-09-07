package com.igarciamen.watchers.client.dto;

public class BoePostingApiResponse {
    private String identifier;
    private String title;
    private String organization;
    private String url;
    private String pdfUrl;

    public String getIdentifier() { return identifier; }
    public void setIdentifier(String identifier) { this.identifier = identifier; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getOrganization() { return organization; }
    public void setOrganization(String organization) { this.organization = organization; }

    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }

    public String getPdfUrl() { return pdfUrl; }
    public void setPdfUrl(String pdfUrl) { this.pdfUrl = pdfUrl; }
}