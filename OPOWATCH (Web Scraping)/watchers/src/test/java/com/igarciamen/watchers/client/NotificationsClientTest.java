package com.igarciamen.watchers.client;

import com.igarciamen.watchers.client.dto.PostingSummaryDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class NotificationsClientTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private NotificationsClient notificationsClient;

    @Test
    void notifySubscribers_postsExpectedPayload() {
        ReflectionTestUtils.setField(notificationsClient, "baseUrl", "http://localhost:8084");

        List<PostingSummaryDto> postings = List.of(
                new PostingSummaryDto("Analista programador", "Ministerio", "https://boe.es/1"));

        notificationsClient.notifySubscribers("BOE - Pruebas", postings, List.of("a@example.com"));

        ArgumentCaptor<com.igarciamen.watchers.client.dto.NotificationRequestDto> captor =
                ArgumentCaptor.forClass(com.igarciamen.watchers.client.dto.NotificationRequestDto.class);
        verify(restTemplate).postForObject(
                eq("http://localhost:8084/api/notifications/send"),
                captor.capture(),
                eq(com.igarciamen.watchers.client.dto.NotificationResultResponseDto.class));

        System.out.println("=== notifySubscribers_postsExpectedPayload ===");
        System.out.println("watcherName enviado: " + captor.getValue().getWatcherName());
    }
}