package com.igarciamen.notifications.controller;

import com.igarciamen.notifications.payloads.request.NotificationRequest;
import com.igarciamen.notifications.payloads.request.PostingSummary;
import com.igarciamen.notifications.service.NotificationService;
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

class NotificationControllerTest {

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private NotificationController notificationController;

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
    void send_returnsSentAndFailedCounts() {
        PostingSummary posting = new PostingSummary();
        posting.setTitle("Analista programador");
        posting.setUrl("https://boe.es/1");

        NotificationRequest request = new NotificationRequest();
        request.setWatcherName("BOE - Pruebas");
        request.setPostings(List.of(posting));
        request.setRecipientEmails(List.of("a@example.com", "b@example.com"));

        when(notificationService.sendToSubscribers(request))
                .thenReturn(new NotificationService.NotificationResult(2, 0));

        ResponseEntity<com.igarciamen.notifications.payloads.response.NotificationResultResponse> response =
                notificationController.send(request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getSentCount()).isEqualTo(2);
        assertThat(response.getBody().getFailedCount()).isEqualTo(0);

        System.out.println("=== send_returnsSentAndFailedCounts ===");
        System.out.println("Enviados: " + response.getBody().getSentCount());
    }
}