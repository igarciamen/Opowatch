package com.igarciamen.watchers.client.dto;

public class JsonApiPostingApiResponse {
    private String title;
    private String url;
    private String publicationDate;

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }

    public String getPublicationDate() { return publicationDate; }
    public void setPublicationDate(String publicationDate) { this.publicationDate = publicationDate; }
}