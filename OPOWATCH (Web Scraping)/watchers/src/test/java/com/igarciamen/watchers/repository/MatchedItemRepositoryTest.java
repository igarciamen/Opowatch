package com.igarciamen.watchers.repository;

import com.igarciamen.watchers.enums.SourceType;
import com.igarciamen.watchers.model.MatchedItem;
import com.igarciamen.watchers.model.Watcher;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class MatchedItemRepositoryTest {

    @Autowired
    private WatcherRepository watcherRepository;

    @Autowired
    private MatchedItemRepository matchedItemRepository;

    @Test
    void save_rejectsDuplicateSourceUrlForTheSameWatcher() {
        Watcher watcher = watcherRepository.save(
                new Watcher("BOE - IT public postings", SourceType.BOE_API, 360));

        MatchedItem first = new MatchedItem(
                watcher, "Analista programador", "Ministerio de prueba",
                "20260815", "https://boe.es/oferta-duplicada");
        matchedItemRepository.saveAndFlush(first);

        MatchedItem duplicate = new MatchedItem(
                watcher, "Analista programador (repetida)", "Ministerio de prueba",
                "20260815", "https://boe.es/oferta-duplicada");

        assertThrows(DataIntegrityViolationException.class,
                () -> matchedItemRepository.saveAndFlush(duplicate));

        System.out.println("=== save_rejectsDuplicateSourceUrlForTheSameWatcher ===");
        System.out.println("La base de datos rechaza una segunda fila con el mismo watcher y source_url");
    }

    @Test
    void save_allowsSameSourceUrlOnDifferentWatchers() {
        Watcher watcherA = watcherRepository.save(new Watcher("Watcher A", SourceType.SELENIUM, 60));
        Watcher watcherB = watcherRepository.save(new Watcher("Watcher B", SourceType.SELENIUM, 60));

        matchedItemRepository.saveAndFlush(new MatchedItem(
                watcherA, "Tecnico TIC", "Organismo A", "20260810", "https://example.org/misma-url"));

        MatchedItem itemB = matchedItemRepository.saveAndFlush(new MatchedItem(
                watcherB, "Tecnico TIC", "Organismo B", "20260810", "https://example.org/misma-url"));

        assertThat(itemB.getId()).isNotNull();

        System.out.println("=== save_allowsSameSourceUrlOnDifferentWatchers ===");
        System.out.println("La misma URL se permite si pertenece a watchers distintos");
    }
}