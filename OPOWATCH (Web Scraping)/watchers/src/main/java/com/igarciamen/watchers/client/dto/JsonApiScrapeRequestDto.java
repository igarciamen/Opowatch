package com.igarciamen.watchers.client.dto;

public class JsonApiScrapeRequestDto {
    private String targetUrl;
    private String itemsPath;
    private String titleField;
    private String linkField;
    private String dateField;

    public JsonApiScrapeRequestDto(String targetUrl, String itemsPath, String titleField,
                                    String linkField, String dateField) {
        this.targetUrl = targetUrl;
        this.itemsPath = itemsPath;
        this.titleField = titleField;
        this.linkField = linkField;
        this.dateField = dateField;
    }

    public String getTargetUrl() { return targetUrl; }
    public String getItemsPath() { return itemsPath; }
    public String getTitleField() { return titleField; }
    public String getLinkField() { return linkField; }
    public String getDateField() { return dateField; }
}