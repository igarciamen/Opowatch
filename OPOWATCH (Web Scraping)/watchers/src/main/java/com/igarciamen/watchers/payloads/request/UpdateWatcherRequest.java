package com.igarciamen.watchers.payloads.request;

import com.igarciamen.watchers.enums.SourceType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class UpdateWatcherRequest {

    @NotBlank
    private String name;

    @NotNull
    private SourceType sourceType;

    private String targetUrl;
    private String listSelector;
    private String titleSelector;
    private String organizationSelector;
    private String dateSelector;
    private String linkSelector;

    private String itemsPath;
    private String titleField;
    private String linkField;
    private String dateField;
    private String organizationText;

    @NotBlank
    private String keywords;

    @Positive
    private int scrapeIntervalMinutes;

    private boolean active;

    public UpdateWatcherRequest() {}

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public SourceType getSourceType() { return sourceType; }
    public void setSourceType(SourceType sourceType) { this.sourceType = sourceType; }

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

    public String getItemsPath() { return itemsPath; }
    public void setItemsPath(String itemsPath) { this.itemsPath = itemsPath; }

    public String getTitleField() { return titleField; }
    public void setTitleField(String titleField) { this.titleField = titleField; }

    public String getLinkField() { return linkField; }
    public void setLinkField(String linkField) { this.linkField = linkField; }

    public String getDateField() { return dateField; }
    public void setDateField(String dateField) { this.dateField = dateField; }

    public String getOrganizationText() { return organizationText; }
    public void setOrganizationText(String organizationText) { this.organizationText = organizationText; }

    public String getKeywords() { return keywords; }
    public void setKeywords(String keywords) { this.keywords = keywords; }

    public int getScrapeIntervalMinutes() { return scrapeIntervalMinutes; }
    public void setScrapeIntervalMinutes(int scrapeIntervalMinutes) { this.scrapeIntervalMinutes = scrapeIntervalMinutes; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
}