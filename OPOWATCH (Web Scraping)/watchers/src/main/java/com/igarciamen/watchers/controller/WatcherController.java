package com.igarciamen.watchers.controller;

import com.igarciamen.watchers.model.Watcher;
import com.igarciamen.watchers.payloads.request.CreateWatcherRequest;
import com.igarciamen.watchers.payloads.request.UpdateWatcherRequest;
import com.igarciamen.watchers.payloads.response.MessageResponse;
import com.igarciamen.watchers.payloads.response.ScrapeResultResponse;
import com.igarciamen.watchers.payloads.response.WatcherResponse;
import com.igarciamen.watchers.service.ScrapingExecutionService;
import com.igarciamen.watchers.service.WatcherService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/watchers")
public class WatcherController {

    private final WatcherService watcherService;
    private final ScrapingExecutionService scrapingExecutionService;

    public WatcherController(WatcherService watcherService, ScrapingExecutionService scrapingExecutionService) {
        this.watcherService = watcherService;
        this.scrapingExecutionService = scrapingExecutionService;
    }

    @Operation(summary = "Lists all watchers", security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<WatcherResponse>> findAll() {
        List<WatcherResponse> response = watcherService.findAll()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Returns a watcher by id", security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping(path = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<WatcherResponse> findById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(toResponse(watcherService.findById(id)));
        } catch (IllegalArgumentException ex) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, ex.getMessage());
        }
    }

    @Operation(summary = "Creates a new watcher", security = @SecurityRequirement(name = "bearerAuth"))
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<WatcherResponse> create(@Valid @RequestBody CreateWatcherRequest req) {
        try {
            Watcher created = watcherService.create(req);
            return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(created));
        } catch (IllegalArgumentException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ex.getMessage());
        }
    }

    @Operation(summary = "Updates an existing watcher", security = @SecurityRequirement(name = "bearerAuth"))
    @PutMapping(path = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<WatcherResponse> update(@PathVariable Long id, @Valid @RequestBody UpdateWatcherRequest req) {
        try {
            Watcher updated = watcherService.update(id, req);
            return ResponseEntity.ok(toResponse(updated));
        } catch (IllegalArgumentException ex) {
            HttpStatus status = ex.getMessage() != null && ex.getMessage().startsWith("Watcher not found")
                    ? HttpStatus.NOT_FOUND
                    : HttpStatus.BAD_REQUEST;
            throw new ResponseStatusException(status, ex.getMessage());
        }
    }

    @Operation(summary = "Deletes a watcher", security = @SecurityRequirement(name = "bearerAuth"))
    @DeleteMapping(path = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<MessageResponse> delete(@PathVariable Long id) {
        try {
            watcherService.delete(id);
            return ResponseEntity.ok(new MessageResponse("Watcher deleted successfully"));
        } catch (IllegalArgumentException ex) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, ex.getMessage());
        }
    }

    @Operation(summary = "Runs a watcher immediately, ignoring its scheduled interval",
            security = @SecurityRequirement(name = "bearerAuth"))
    @PostMapping(path = "/{id}/scrape", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ScrapeResultResponse> scrapeNow(@PathVariable Long id) {
        try {
            Watcher watcher = watcherService.findById(id);
            ScrapingExecutionService.ScrapeOutcome outcome = scrapingExecutionService.runWatcher(watcher);
            return ResponseEntity.ok(new ScrapeResultResponse(outcome.newItemsFound(), outcome.updatedItemsFound()));
        } catch (IllegalArgumentException ex) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, ex.getMessage());
        }
    }

    private WatcherResponse toResponse(Watcher w) {
        return new WatcherResponse(
                w.getId(), w.getName(), w.getSourceType(), w.getTargetUrl(),
                w.getListSelector(), w.getTitleSelector(), w.getOrganizationSelector(),
                w.getDateSelector(), w.getLinkSelector(), w.getItemsPath(), w.getTitleField(),
                w.getLinkField(), w.getDateField(), w.getOrganizationText(), w.getKeywords(),
                w.getScrapeIntervalMinutes(), w.isActive(), w.getCreatedAt(), w.getLastScrapedAt()
        );
    }
}