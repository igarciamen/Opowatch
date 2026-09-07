package com.igarciamen.watchers.service;

import com.igarciamen.watchers.enums.SourceType;
import com.igarciamen.watchers.model.Watcher;
import com.igarciamen.watchers.payloads.request.CreateWatcherRequest;
import com.igarciamen.watchers.payloads.request.UpdateWatcherRequest;
import com.igarciamen.watchers.repository.WatcherRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WatcherServiceTest {

    @Mock
    private WatcherRepository watcherRepository;

    @InjectMocks
    private WatcherService watcherService;

    private CreateWatcherRequest boeRequest() {
        CreateWatcherRequest req = new CreateWatcherRequest();
        req.setName("BOE - IT public postings");
        req.setSourceType(SourceType.BOE_API);
        req.setScrapeIntervalMinutes(360);
        return req;
    }

    private CreateWatcherRequest seleniumRequest() {
        CreateWatcherRequest req = new CreateWatcherRequest();
        req.setName("Diputacion de prueba");
        req.setSourceType(SourceType.SELENIUM);
        req.setTargetUrl("https://example.org/empleo");
        req.setListSelector(".oferta");
        req.setTitleSelector(".oferta .titulo");
        req.setLinkSelector(".oferta a");
        req.setScrapeIntervalMinutes(120);
        return req;
    }

    @Test
    void findAll_returnsAllWatchers() {
        when(watcherRepository.findAll()).thenReturn(List.of(
                new Watcher("A", SourceType.BOE_API, 60),
                new Watcher("B", SourceType.SELENIUM, 30)
        ));

        List<Watcher> result = watcherService.findAll();

        assertEquals(2, result.size());
        System.out.println("=== findAll_returnsAllWatchers ===");
        System.out.println("Watchers encontrados: " + result.size());
    }

    @Test
    void findById_returnsWatcherIfExists() {
        Watcher watcher = new Watcher("A", SourceType.BOE_API, 60);
        watcher.setId(1L);
        when(watcherRepository.findById(1L)).thenReturn(Optional.of(watcher));

        Watcher result = watcherService.findById(1L);

        assertEquals("A", result.getName());
        System.out.println("=== findById_returnsWatcherIfExists ===");
        System.out.println("Watcher encontrado: " + result.getName());
    }

    @Test
    void findById_throwsIfNotFound() {
        when(watcherRepository.findById(99L)).thenReturn(Optional.empty());

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> watcherService.findById(99L));

        System.out.println("=== findById_throwsIfNotFound ===");
        System.out.println("Mensaje: " + ex.getMessage());
    }

    @Test
    void create_boeApiWatcher_leavesSeleniumFieldsNull() {
        when(watcherRepository.save(any(Watcher.class))).thenAnswer(inv -> inv.getArgument(0));

        Watcher result = watcherService.create(boeRequest());

        assertEquals(SourceType.BOE_API, result.getSourceType());
        assertNull(result.getTargetUrl());
        assertNull(result.getListSelector());
        assertNull(result.getTitleSelector());
        assertNull(result.getLinkSelector());

        System.out.println("=== create_boeApiWatcher_leavesSeleniumFieldsNull ===");
        System.out.println("targetUrl: " + result.getTargetUrl() + ", listSelector: " + result.getListSelector());
    }

    @Test
    void create_seleniumWatcher_savesAllSelectors() {
        ArgumentCaptor<Watcher> captor = ArgumentCaptor.forClass(Watcher.class);
        when(watcherRepository.save(captor.capture())).thenAnswer(inv -> inv.getArgument(0));

        Watcher result = watcherService.create(seleniumRequest());

        assertEquals("https://example.org/empleo", result.getTargetUrl());
        assertEquals(".oferta", result.getListSelector());
        assertEquals(".oferta .titulo", result.getTitleSelector());
        assertEquals(".oferta a", result.getLinkSelector());

        System.out.println("=== create_seleniumWatcher_savesAllSelectors ===");
        System.out.println("listSelector guardado: " + captor.getValue().getListSelector());
    }

    @Test
    void create_seleniumWatcherWithoutRequiredSelectors_throws() {
        CreateWatcherRequest req = new CreateWatcherRequest();
        req.setName("Watcher incompleto");
        req.setSourceType(SourceType.SELENIUM);
        req.setScrapeIntervalMinutes(60);
        // targetUrl y selectores sin rellenar a proposito

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> watcherService.create(req));

        verify(watcherRepository, never()).save(any());
        System.out.println("=== create_seleniumWatcherWithoutRequiredSelectors_throws ===");
        System.out.println("Se rechaza con el mensaje: " + ex.getMessage());
    }

    @Test
    void update_changesNameIntervalAndActiveFlag() {
        Watcher existing = new Watcher("Nombre viejo", SourceType.BOE_API, 60);
        existing.setId(1L);
        existing.setActive(true);
        when(watcherRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(watcherRepository.save(any(Watcher.class))).thenAnswer(inv -> inv.getArgument(0));

        UpdateWatcherRequest req = new UpdateWatcherRequest();
        req.setName("Nombre nuevo");
        req.setSourceType(SourceType.BOE_API);
        req.setScrapeIntervalMinutes(180);
        req.setActive(false);

        Watcher result = watcherService.update(1L, req);

        assertEquals("Nombre nuevo", result.getName());
        assertEquals(180, result.getScrapeIntervalMinutes());
        assertFalse(result.isActive());

        System.out.println("=== update_changesNameIntervalAndActiveFlag ===");
        System.out.println("Nombre: " + result.getName() + ", active: " + result.isActive());
    }

    @Test
    void update_throwsIfWatcherNotFound() {
        when(watcherRepository.findById(99L)).thenReturn(Optional.empty());

        UpdateWatcherRequest req = new UpdateWatcherRequest();
        req.setName("X");
        req.setSourceType(SourceType.BOE_API);
        req.setScrapeIntervalMinutes(60);

        assertThrows(IllegalArgumentException.class, () -> watcherService.update(99L, req));

        System.out.println("=== update_throwsIfWatcherNotFound ===");
        System.out.println("No se permite actualizar un watcher inexistente");
    }

    @Test
    void delete_removesExistingWatcher() {
        Watcher watcher = new Watcher("A", SourceType.BOE_API, 60);
        watcher.setId(1L);
        when(watcherRepository.findById(1L)).thenReturn(Optional.of(watcher));

        watcherService.delete(1L);

        verify(watcherRepository).delete(watcher);
        System.out.println("=== delete_removesExistingWatcher ===");
        System.out.println("Se ha llamado a delete sobre el watcher correcto");
    }

    @Test
    void delete_throwsIfWatcherNotFound() {
        when(watcherRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> watcherService.delete(99L));

        verify(watcherRepository, never()).delete(any());
        System.out.println("=== delete_throwsIfWatcherNotFound ===");
        System.out.println("No se llama a delete si el watcher no existe");
    }
}