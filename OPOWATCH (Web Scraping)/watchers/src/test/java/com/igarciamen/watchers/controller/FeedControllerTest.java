package com.igarciamen.watchers.controller;

import com.igarciamen.watchers.enums.SourceType;
import com.igarciamen.watchers.model.MatchedItem;
import com.igarciamen.watchers.model.Watcher;
import com.igarciamen.watchers.payloads.response.MatchedItemResponse;
import com.igarciamen.watchers.service.FeedService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

class FeedControllerTest {

    @Mock
    private FeedService feedService;

    @InjectMocks
    private FeedController feedController;

    private AutoCloseable mocks;

    @BeforeEach
    void setUp() {
        mocks = MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    void tearDown() throws Exception {
        mocks.close();
    }

    @Test
    void feed_returnsAggregatedItems() {
        Watcher watcher = new Watcher("BOE - IT public postings", SourceType.BOE_API, 360);
        MatchedItem item = new MatchedItem(watcher, "Analista programador", "Ministerio", "20260820", "https://boe.es/1");
        when(feedService.getAggregatedFeed()).thenReturn(List.of(item));

        ResponseEntity<List<MatchedItemResponse>> response = feedController.feed();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).hasSize(1);
        assertThat(response.getBody().get(0).getWatcherName()).isEqualTo("BOE - IT public postings");

        System.out.println("=== feed_returnsAggregatedItems ===");
        System.out.println("Items en el feed: " + response.getBody().size());
    }
}