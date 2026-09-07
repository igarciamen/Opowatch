package com.igarciamen.scraperengine.payloads.request;

import jakarta.validation.constraints.NotBlank;

public class SeleniumScrapeRequest {

    @NotBlank
    private String targetUrl;

    @NotBlank
    private String listSelector;

    @NotBlank
    private String titleSelector;

    private String organizationSelector;
    private String dateSelector;

    @NotBlank
    private String linkSelector;

    public SeleniumScrapeRequest() {}

    public String getTargetUrl() { return targetUrl; }
    public void setTargetUrl(String targetUrl) { this.targetUrl = targetUrl; }

    public String getListSelector() { return listSelector; }
    public void setListSelector(String listSelector) { this.listSelector = listSelector; }

    public String getTitleSelector() { return titleSelector; }
    public void setTitleSelector(String titleSelector) { this.titleSelector = titleSelector; }

    public String getOrganizationSelector() { return organizationSelector; }
    public void setOrganizationSelector(String organizationSelector) { this.organizationSelector = organizationSelector; }

    public String getDateSelector() { return dateSelector; }
    public void setDateSelector(String dateSelector) { this.dateSelector = dateSelector; }

    public String getLinkSelector() { return linkSelector; }
    public void setLinkSelector(String linkSelector) { this.linkSelector = linkSelector; }
}