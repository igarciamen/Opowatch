package com.igarciamen.watchers.controller;

import com.igarciamen.watchers.enums.SourceType;
import com.igarciamen.watchers.model.Watcher;
import com.igarciamen.watchers.payloads.request.CreateWatcherRequest;
import com.igarciamen.watchers.payloads.request.UpdateWatcherRequest;
import com.igarciamen.watchers.payloads.response.MessageResponse;
import com.igarciamen.watchers.payloads.response.WatcherResponse;
import com.igarciamen.watchers.service.ScrapingExecutionService;
import com.igarciamen.watchers.service.WatcherService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class WatcherControllerTest {

    @Mock
    private WatcherService watcherService;

    @Mock
    private ScrapingExecutionService scrapingExecutionService;

    @InjectMocks
    private WatcherController watcherController;

    private AutoCloseable mocks;

    @BeforeEach
    void setUp() {
        mocks = MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    void tearDown() throws Exception {
        mocks.close();
    }

    private Watcher sampleWatcher() {
        Watcher watcher = new Watcher("BOE - IT public postings", SourceType.BOE_API, 360);
        watcher.setId(1L);
        watcher.setKeywords("informatica");
        return watcher;
    }

    @Test
    void findAll_returnsList() {
        when(watcherService.findAll()).thenReturn(List.of(sampleWatcher()));

        ResponseEntity<List<WatcherResponse>> response = watcherController.findAll();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).hasSize(1);

        System.out.println("=== findAll_returnsList ===");
        System.out.println("Watchers devueltos: " + response.getBody().size());
    }

    @Test
    void findById_returnsWatcher() {
        when(watcherService.findById(1L)).thenReturn(sampleWatcher());

        ResponseEntity<WatcherResponse> response = watcherController.findById(1L);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getName()).isEqualTo("BOE - IT public postings");

        System.out.println("=== findById_returnsWatcher ===");
        System.out.println("Nombre: " + response.getBody().getName());
    }

    @Test
    void findById_notFound_returns404() {
        when(watcherService.findById(99L)).thenThrow(new IllegalArgumentException("Watcher not found: 99"));

        ResponseStatusException ex = org.junit.jupiter.api.Assertions.assertThrows(
                ResponseStatusException.class, () -> watcherController.findById(99L));

        assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        System.out.println("=== findById_notFound_returns404 ===");
        System.out.println("Status: " + ex.getStatusCode());
    }

    @Test
    void create_returns201() {
        CreateWatcherRequest req = new CreateWatcherRequest();
        req.setName("BOE - IT public postings");
        req.setSourceType(SourceType.BOE_API);
        req.setKeywords("informatica");
        req.setScrapeIntervalMinutes(360);

        when(watcherService.create(req)).thenReturn(sampleWatcher());

        ResponseEntity<WatcherResponse> response = watcherController.create(req);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody().getId()).isEqualTo(1L);

        System.out.println("=== create_returns201 ===");
        System.out.println("Status: " + response.getStatusCode());
    }

    @Test
    void create_invalidRequest_returns400() {
        CreateWatcherRequest req = new CreateWatcherRequest();
        req.setName("Watcher incompleto");
        req.setSourceType(SourceType.SELENIUM);
        req.setKeywords("informatica");

        when(watcherService.create(req))
                .thenThrow(new IllegalArgumentException("targetUrl is required for SELENIUM watchers"));

        ResponseStatusException ex = org.junit.jupiter.api.Assertions.assertThrows(
                ResponseStatusException.class, () -> watcherController.create(req));

        assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        System.out.println("=== create_invalidRequest_returns400 ===");
        System.out.println("Status: " + ex.getStatusCode());
    }

    @Test
    void update_returns200() {
        UpdateWatcherRequest req = new UpdateWatcherRequest();
        req.setName("Nombre nuevo");
        req.setSourceType(SourceType.BOE_API);
        req.setKeywords("informatica");
        req.setScrapeIntervalMinutes(180);
        req.setActive(true);

        when(watcherService.update(1L, req)).thenReturn(sampleWatcher());

        ResponseEntity<WatcherResponse> response = watcherController.update(1L, req);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        System.out.println("=== update_returns200 ===");
        System.out.println("Status: " + response.getStatusCode());
    }

    @Test
    void update_notFound_returns404() {
        UpdateWatcherRequest req = new UpdateWatcherRequest();
        req.setName("X");
        req.setSourceType(SourceType.BOE_API);
        req.setKeywords("informatica");
        req.setScrapeIntervalMinutes(60);

        when(watcherService.update(99L, req))
                .thenThrow(new IllegalArgumentException("Watcher not found: 99"));

        ResponseStatusException ex = org.junit.jupiter.api.Assertions.assertThrows(
                ResponseStatusException.class, () -> watcherController.update(99L, req));

        assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        System.out.println("=== update_notFound_returns404 ===");
        System.out.println("Status: " + ex.getStatusCode());
    }

    @Test
    void delete_returns200() {
        doNothing().when(watcherService).delete(1L);

        ResponseEntity<MessageResponse> response = watcherController.delete(1L);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getMessage()).isEqualTo("Watcher deleted successfully");

        System.out.println("=== delete_returns200 ===");
        System.out.println("Message: " + response.getBody().getMessage());
    }

    @Test
    void delete_notFound_returns404() {
        doThrow(new IllegalArgumentException("Watcher not found: 99")).when(watcherService).delete(99L);

        ResponseStatusException ex = org.junit.jupiter.api.Assertions.assertThrows(
                ResponseStatusException.class, () -> watcherController.delete(99L));

        assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        System.out.println("=== delete_notFound_returns404 ===");
        System.out.println("Status: " + ex.getStatusCode());
    }
}