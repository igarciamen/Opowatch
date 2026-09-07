package com.igarciamen.watchers.repository;

import com.igarciamen.watchers.enums.SourceType;
import com.igarciamen.watchers.model.MatchedItem;
import com.igarciamen.watchers.model.Watcher;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class WatcherRepositoryTest {

    @Autowired
    private WatcherRepository watcherRepository;

    @Autowired
    private MatchedItemRepository matchedItemRepository;

    @Test
    void save_persistsWatcherWithDefaults() {
        Watcher watcher = new Watcher("BOE - IT public postings", SourceType.BOE_API, 360);

        Watcher saved = watcherRepository.save(watcher);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.isActive()).isTrue();
        assertThat(saved.getCreatedAt()).isNotNull();

        System.out.println("=== save_persistsWatcherWithDefaults ===");
        System.out.println("Watcher guardado con id: " + saved.getId() + ", active: " + saved.isActive());
    }

    @Test
    void deletingWatcher_cascadesToItsMatchedItems() {
        Watcher watcher = watcherRepository.save(
                new Watcher("Diputacion de prueba", SourceType.SELENIUM, 120));

        MatchedItem item = new MatchedItem(
                watcher, "Tecnico de sistemas", "Diputacion de prueba",
                "20260815", "https://example.org/oferta-1");
        watcher.getMatchedItems().add(item);
        matchedItemRepository.save(item);

        watcherRepository.delete(watcher);
        watcherRepository.flush();

        Optional<MatchedItem> found = matchedItemRepository.findById(item.getId());

        assertThat(found).isEmpty();

        System.out.println("=== deletingWatcher_cascadesToItsMatchedItems ===");
        System.out.println("Tras borrar el watcher, el MatchedItem asociado tambien desaparece: " + found.isEmpty());
    }
}