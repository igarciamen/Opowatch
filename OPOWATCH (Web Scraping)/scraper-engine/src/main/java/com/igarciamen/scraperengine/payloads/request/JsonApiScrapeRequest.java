package com.igarciamen.scraperengine.payloads.request;

import jakarta.validation.constraints.NotBlank;

public class JsonApiScrapeRequest {

    @NotBlank
    private String targetUrl;

    @NotBlank
    private String itemsPath;

    @NotBlank
    private String titleField;

    @NotBlank
    private String linkField;

    private String dateField;

    public JsonApiScrapeRequest() {}

    public String getTargetUrl() { return targetUrl; }
    public void setTargetUrl(String targetUrl) { this.targetUrl = targetUrl; }

    public String getItemsPath() { return itemsPath; }
    public void setItemsPath(String itemsPath) { this.itemsPath = itemsPath; }

    public String getTitleField() { return titleField; }
    public void setTitleField(String titleField) { this.titleField = titleField; }

    public String getLinkField() { return linkField; }
    public void setLinkField(String linkField) { this.linkField = linkField; }

    public String getDateField() { return dateField; }
    public void setDateField(String dateField) { this.dateField = dateField; }
}