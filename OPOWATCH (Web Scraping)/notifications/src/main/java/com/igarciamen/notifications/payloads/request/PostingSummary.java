package com.igarciamen.notifications.payloads.request;

import jakarta.validation.constraints.NotBlank;

public class PostingSummary {

    @NotBlank
    private String title;

    private String organization;

    @NotBlank
    private String url;

    public PostingSummary() {}

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getOrganization() { return organization; }
    public void setOrganization(String organization) { this.organization = organization; }

    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }
}