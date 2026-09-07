package com.igarciamen.watchers.controller;

import com.igarciamen.watchers.enums.SourceType;
import com.igarciamen.watchers.model.MatchedItem;
import com.igarciamen.watchers.model.Watcher;
import com.igarciamen.watchers.service.FeedService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = FeedController.class)
@AutoConfigureMockMvc(addFilters = false)
class FeedControllerWebMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private FeedService feedService;

    @Test
    void getFeed_ok() throws Exception {
        Watcher watcher = new Watcher("BOE - IT public postings", SourceType.BOE_API, 360);
        MatchedItem item = new MatchedItem(watcher, "Analista programador", "Ministerio", "20260820", "https://boe.es/1");
        when(feedService.getAggregatedFeed()).thenReturn(List.of(item));

        MvcResult result = mockMvc.perform(get("/api/watchers/feed"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Analista programador"))
                .andReturn();

        System.out.println("=== GET /api/watchers/feed ===");
        System.out.println("Status: " + result.getResponse().getStatus());
        System.out.println("Body  : " + result.getResponse().getContentAsString());
    }
}