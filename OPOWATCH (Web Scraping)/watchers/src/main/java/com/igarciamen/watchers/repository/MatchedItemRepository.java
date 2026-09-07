package com.igarciamen.watchers.repository;

import com.igarciamen.watchers.model.MatchedItem;
import com.igarciamen.watchers.model.Watcher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MatchedItemRepository extends JpaRepository<MatchedItem, Long> {

    List<MatchedItem> findAllByOrderByDetectedAtDesc();

    Optional<MatchedItem> findByWatcherAndSourceUrl(Watcher watcher, String sourceUrl);
}