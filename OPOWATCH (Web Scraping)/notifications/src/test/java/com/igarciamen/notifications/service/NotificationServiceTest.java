package com.igarciamen.notifications.service;

import com.igarciamen.notifications.payloads.request.NotificationRequest;
import com.igarciamen.notifications.payloads.request.PostingSummary;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

    @Mock
    private EmailService emailService;

    @InjectMocks
    private NotificationService notificationService;

    private NotificationRequest sampleRequest(List<String> recipients) {
        PostingSummary posting = new PostingSummary();
        posting.setTitle("Analista programador");
        posting.setOrganization("Ministerio de prueba");
        posting.setUrl("https://boe.es/1");

        NotificationRequest request = new NotificationRequest();
        request.setWatcherName("BOE - Pruebas");
        request.setPostings(List.of(posting));
        request.setRecipientEmails(recipients);
        return request;
    }

    @Test
    void sendToSubscribers_sendsToEveryRecipient() {
        NotificationRequest request = sampleRequest(List.of("a@example.com", "b@example.com"));

        NotificationService.NotificationResult result = notificationService.sendToSubscribers(request);

        assertEquals(2, result.sentCount());
        assertEquals(0, result.failedCount());
        verify(emailService, times(2)).sendNewPostingsNotification(anyString(), eq("BOE - Pruebas"), anyList());

        System.out.println("=== sendToSubscribers_sendsToEveryRecipient ===");
        System.out.println("Enviados: " + result.sentCount() + ", fallidos: " + result.failedCount());
    }

    @Test
    void sendToSubscribers_oneRecipientFailing_doesNotStopTheOthers() {
        NotificationRequest request = sampleRequest(List.of("valido@example.com", "roto@invalido", "otro-valido@example.com"));

        // Un unico stubbing que cubre cualquier llamada, decidiendo dentro si lanza el fallo.
        // Evita el PotentialStubbingProblem que Mockito lanza en modo estricto cuando distintas
        // llamadas al mismo metodo no encajan todas con el mismo conjunto de matchers.
        doAnswer(invocation -> {
            String recipient = invocation.getArgument(0);
            if ("roto@invalido".equals(recipient)) {
                throw new IllegalStateException("Invalid address");
            }
            return null;
        }).when(emailService).sendNewPostingsNotification(anyString(), anyString(), anyList());

        NotificationService.NotificationResult result = notificationService.sendToSubscribers(request);

        assertEquals(2, result.sentCount());
        assertEquals(1, result.failedCount());
        verify(emailService, times(3)).sendNewPostingsNotification(anyString(), anyString(), anyList());

        System.out.println("=== sendToSubscribers_oneRecipientFailing_doesNotStopTheOthers ===");
        System.out.println("Enviados: " + result.sentCount() + ", fallidos: " + result.failedCount());
    }
}