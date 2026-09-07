package com.igarciamen.watchers.service;

import com.igarciamen.watchers.model.MatchedItem;
import com.igarciamen.watchers.repository.MatchedItemRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FeedService {

    private final MatchedItemRepository matchedItemRepository;

    public FeedService(MatchedItemRepository matchedItemRepository) {
        this.matchedItemRepository = matchedItemRepository;
    }

    public List<MatchedItem> getAggregatedFeed() {
        return matchedItemRepository.findAllByOrderByDetectedAtDesc();
    }
}