package com.igarciamen.watchers.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.igarciamen.watchers.enums.SourceType;
import com.igarciamen.watchers.model.Watcher;
import com.igarciamen.watchers.payloads.request.CreateWatcherRequest;
import com.igarciamen.watchers.service.ScrapingExecutionService;
import com.igarciamen.watchers.service.WatcherService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = WatcherController.class)
@AutoConfigureMockMvc(addFilters = false)
class WatcherControllerWebMvcTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @MockitoBean
    private WatcherService watcherService;

    @MockitoBean
    private ScrapingExecutionService scrapingExecutionService;

    @Test
    void getAll_ok() throws Exception {
        Watcher watcher = new Watcher("BOE - IT public postings", SourceType.BOE_API, 360);
        watcher.setId(1L);
        watcher.setKeywords("informatica");
        when(watcherService.findAll()).thenReturn(List.of(watcher));

        MvcResult result = mockMvc.perform(get("/api/watchers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("BOE - IT public postings"))
                .andReturn();

        System.out.println("=== GET /api/watchers ===");
        System.out.println("Status: " + result.getResponse().getStatus());
        System.out.println("Body  : " + result.getResponse().getContentAsString());
    }

    @Test
    void create_ok() throws Exception {
        CreateWatcherRequest req = new CreateWatcherRequest();
        req.setName("BOE - IT public postings");
        req.setSourceType(SourceType.BOE_API);
        req.setKeywords("informatica");
        req.setScrapeIntervalMinutes(360);

        Watcher saved = new Watcher("BOE - IT public postings", SourceType.BOE_API, 360);
        saved.setId(1L);
        saved.setKeywords("informatica");
        when(watcherService.create(org.mockito.ArgumentMatchers.any())).thenReturn(saved);

        MvcResult result = mockMvc.perform(post("/api/watchers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andReturn();

        System.out.println("=== POST /api/watchers ===");
        System.out.println("Status: " + result.getResponse().getStatus());
        System.out.println("Body  : " + result.getResponse().getContentAsString());
    }

    @Test
    void scrapeNow_ok() throws Exception {
        Watcher watcher = new Watcher("BOE - Pruebas", SourceType.BOE_API, 60);
        watcher.setId(1L);
        watcher.setKeywords("informatica");

        when(watcherService.findById(1L)).thenReturn(watcher);
        when(scrapingExecutionService.runWatcher(watcher))
                .thenReturn(new ScrapingExecutionService.ScrapeOutcome(2, 1));

        MvcResult result = mockMvc.perform(post("/api/watchers/1/scrape"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.newItemsFound").value(2))
                .andExpect(jsonPath("$.updatedItemsFound").value(1))
                .andReturn();

        System.out.println("=== POST /api/watchers/1/scrape ===");
        System.out.println("Status: " + result.getResponse().getStatus());
        System.out.println("Body  : " + result.getResponse().getContentAsString());
    }
}