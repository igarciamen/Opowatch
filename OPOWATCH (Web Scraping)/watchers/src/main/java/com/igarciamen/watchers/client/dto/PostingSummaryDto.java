package com.igarciamen.watchers.client.dto;

public class PostingSummaryDto {
    private String title;
    private String organization;
    private String url;

    public PostingSummaryDto(String title, String organization, String url) {
        this.title = title;
        this.organization = organization;
        this.url = url;
    }

    public String getTitle() { return title; }
    public String getOrganization() { return organization; }
    public String getUrl() { return url; }
}