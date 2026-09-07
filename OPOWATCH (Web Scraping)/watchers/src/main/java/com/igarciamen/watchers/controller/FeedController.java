package com.igarciamen.watchers.controller;

import com.igarciamen.watchers.model.MatchedItem;
import com.igarciamen.watchers.payloads.response.MatchedItemResponse;
import com.igarciamen.watchers.service.FeedService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/watchers")
public class FeedController {

    private final FeedService feedService;

    public FeedController(FeedService feedService) {
        this.feedService = feedService;
    }

    @Operation(summary = "Public aggregated feed of every detected IT public job posting, no authentication required")
    @GetMapping(path = "/feed", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<MatchedItemResponse>> feed() {
        List<MatchedItemResponse> response = feedService.getAggregatedFeed()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    private MatchedItemResponse toResponse(MatchedItem item) {
        return new MatchedItemResponse(
                item.getId(),
                item.getWatcher().getName(),
                item.getTitle(),
                item.getOrganization(),
                item.getPublicationDate(),
                item.getSourceUrl(),
                item.getDetectedAt()
        );
    }
}