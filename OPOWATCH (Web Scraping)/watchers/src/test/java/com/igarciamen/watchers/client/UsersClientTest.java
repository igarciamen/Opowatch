package com.igarciamen.watchers.client;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UsersClientTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private UsersClient usersClient;

    @Test
    void fetchSubscriberEmails_sendsInternalApiKeyHeader() {
        ReflectionTestUtils.setField(usersClient, "baseUrl", "http://localhost:8081");
        ReflectionTestUtils.setField(usersClient, "internalApiKey", "test-key-123");

        String[] emails = {"a@example.com", "b@example.com"};
        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(HttpEntity.class), eq(String[].class)))
                .thenReturn(new ResponseEntity<>(emails, org.springframework.http.HttpStatus.OK));

        List<String> result = usersClient.fetchSubscriberEmails();

        assertEquals(2, result.size());
        assertTrue(result.contains("a@example.com"));

        ArgumentCaptor<HttpEntity<Void>> captor = ArgumentCaptor.forClass(HttpEntity.class);
        org.mockito.Mockito.verify(restTemplate)
                .exchange(anyString(), eq(HttpMethod.GET), captor.capture(), eq(String[].class));
        assertEquals("test-key-123", captor.getValue().getHeaders().getFirst("X-Internal-Api-Key"));

        System.out.println("=== fetchSubscriberEmails_sendsInternalApiKeyHeader ===");
        System.out.println("Emails recibidos: " + result);
    }
}