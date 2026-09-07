package com.igarciamen.watchers.service;

import com.igarciamen.watchers.enums.SourceType;
import com.igarciamen.watchers.model.MatchedItem;
import com.igarciamen.watchers.model.Watcher;
import com.igarciamen.watchers.repository.MatchedItemRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FeedServiceTest {

    @Mock
    private MatchedItemRepository matchedItemRepository;

    @InjectMocks
    private FeedService feedService;

    @Test
    void getAggregatedFeed_returnsItemsOrderedByDetectionDate() {
        Watcher watcher = new Watcher("BOE - IT public postings", SourceType.BOE_API, 360);
        MatchedItem newest = new MatchedItem(watcher, "Analista programador", "Ministerio", "20260820", "https://boe.es/1");
        MatchedItem oldest = new MatchedItem(watcher, "Tecnico de sistemas", "Ministerio", "20260810", "https://boe.es/2");

        when(matchedItemRepository.findAllByOrderByDetectedAtDesc())
                .thenReturn(List.of(newest, oldest));

        List<MatchedItem> result = feedService.getAggregatedFeed();

        assertEquals(2, result.size());
        assertEquals("Analista programador", result.get(0).getTitle());

        System.out.println("=== getAggregatedFeed_returnsItemsOrderedByDetectionDate ===");
        System.out.println("Primer elemento: " + result.get(0).getTitle());
    }
}