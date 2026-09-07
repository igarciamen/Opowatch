package com.igarciamen.watchers.service;

import com.igarciamen.watchers.enums.SourceType;
import com.igarciamen.watchers.model.Watcher;
import com.igarciamen.watchers.payloads.request.CreateWatcherRequest;
import com.igarciamen.watchers.payloads.request.UpdateWatcherRequest;
import com.igarciamen.watchers.repository.WatcherRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WatcherService {

    private final WatcherRepository watcherRepository;

    public WatcherService(WatcherRepository watcherRepository) {
        this.watcherRepository = watcherRepository;
    }

    public List<Watcher> findAll() {
        return watcherRepository.findAll();
    }

    public Watcher findById(Long id) {
        return watcherRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Watcher not found: " + id));
    }

    public Watcher create(CreateWatcherRequest req) {
        Watcher watcher = new Watcher(req.getName(), req.getSourceType(), req.getScrapeIntervalMinutes());
        watcher.setKeywords(req.getKeywords());
        applySourceFields(watcher, req.getSourceType(),
                req.getTargetUrl(), req.getListSelector(), req.getTitleSelector(),
                req.getOrganizationSelector(), req.getDateSelector(), req.getLinkSelector(),
                req.getItemsPath(), req.getTitleField(), req.getLinkField(),
                req.getDateField(), req.getOrganizationText());
        return watcherRepository.save(watcher);
    }

    public Watcher update(Long id, UpdateWatcherRequest req) {
        Watcher watcher = findById(id);
        watcher.setName(req.getName());
        watcher.setKeywords(req.getKeywords());
        watcher.setScrapeIntervalMinutes(req.getScrapeIntervalMinutes());
        watcher.setActive(req.isActive());
        applySourceFields(watcher, req.getSourceType(),
                req.getTargetUrl(), req.getListSelector(), req.getTitleSelector(),
                req.getOrganizationSelector(), req.getDateSelector(), req.getLinkSelector(),
                req.getItemsPath(), req.getTitleField(), req.getLinkField(),
                req.getDateField(), req.getOrganizationText());
        return watcherRepository.save(watcher);
    }

    public void delete(Long id) {
        Watcher watcher = findById(id);
        watcherRepository.delete(watcher);
    }

    private void applySourceFields(Watcher watcher, SourceType sourceType,
                                   String targetUrl, String listSelector, String titleSelector,
                                   String organizationSelector, String dateSelector, String linkSelector,
                                   String itemsPath, String titleField, String linkField,
                                   String dateField, String organizationText) {

        watcher.setSourceType(sourceType);

        // Clear every source-specific field first, then fill in only what applies.
        watcher.setTargetUrl(null);
        watcher.setListSelector(null);
        watcher.setTitleSelector(null);
        watcher.setOrganizationSelector(null);
        watcher.setDateSelector(null);
        watcher.setLinkSelector(null);
        watcher.setItemsPath(null);
        watcher.setTitleField(null);
        watcher.setLinkField(null);
        watcher.setDateField(null);
        watcher.setOrganizationText(null);

        if (sourceType == SourceType.SELENIUM) {
            if (isBlank(targetUrl) || isBlank(listSelector) || isBlank(titleSelector) || isBlank(linkSelector)) {
                throw new IllegalArgumentException(
                        "targetUrl, listSelector, titleSelector and linkSelector are required for SELENIUM watchers");
            }
            watcher.setTargetUrl(targetUrl);
            watcher.setListSelector(listSelector);
            watcher.setTitleSelector(titleSelector);
            watcher.setOrganizationSelector(organizationSelector);
            watcher.setDateSelector(dateSelector);
            watcher.setLinkSelector(linkSelector);
        } else if (sourceType == SourceType.JSON_API) {
            if (isBlank(targetUrl) || isBlank(itemsPath) || isBlank(titleField) || isBlank(linkField)) {
                throw new IllegalArgumentException(
                        "targetUrl, itemsPath, titleField and linkField are required for JSON_API watchers");
            }
            watcher.setTargetUrl(targetUrl);
            watcher.setItemsPath(itemsPath);
            watcher.setTitleField(titleField);
            watcher.setLinkField(linkField);
            watcher.setDateField(dateField);
            watcher.setOrganizationText(organizationText);
        }
        // BOE_API needs none of these fields, scraper-engine already knows how to call it.
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}