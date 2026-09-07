package com.igarciamen.watchers.dto;

public class SeleniumPostingApiResponse {
    private String title;
    private String organization;
    private String publicationDate;
    private String url;

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getOrganization() { return organization; }
    public void setOrganization(String organization) { this.organization = organization; }

    public String getPublicationDate() { return publicationDate; }
    public void setPublicationDate(String publicationDate) { this.publicationDate = publicationDate; }

    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }
}