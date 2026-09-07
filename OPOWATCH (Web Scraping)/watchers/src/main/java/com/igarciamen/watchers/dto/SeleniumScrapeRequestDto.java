package com.igarciamen.watchers.dto;

public class SeleniumScrapeRequestDto {
    private String targetUrl;
    private String listSelector;
    private String titleSelector;
    private String organizationSelector;
    private String dateSelector;
    private String linkSelector;

    public SeleniumScrapeRequestDto(String targetUrl, String listSelector, String titleSelector,
                                     String organizationSelector, String dateSelector, String linkSelector) {
        this.targetUrl = targetUrl;
        this.listSelector = listSelector;
        this.titleSelector = titleSelector;
        this.organizationSelector = organizationSelector;
        this.dateSelector = dateSelector;
        this.linkSelector = linkSelector;
    }

    public String getTargetUrl() { return targetUrl; }
    public String getListSelector() { return listSelector; }
    public String getTitleSelector() { return titleSelector; }
    public String getOrganizationSelector() { return organizationSelector; }
    public String getDateSelector() { return dateSelector; }
    public String getLinkSelector() { return linkSelector; }
}