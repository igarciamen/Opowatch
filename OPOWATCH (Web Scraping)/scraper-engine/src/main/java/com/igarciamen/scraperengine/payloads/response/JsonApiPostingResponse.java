package com.igarciamen.scraperengine.payloads.response;

public class JsonApiPostingResponse {
    private String title;
    private String url;
    private String publicationDate;

    public JsonApiPostingResponse(String title, String url, String publicationDate) {
        this.title = title;
        this.url = url;
        this.publicationDate = publicationDate;
    }

    public String getTitle() { return title; }
    public String getUrl() { return url; }
    public String getPublicationDate() { return publicationDate; }
}