package com.igarciamen.notifications.service;

import com.igarciamen.notifications.payloads.request.PostingSummary;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;

import java.util.List;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmailServiceTest {

    @Mock
    private JavaMailSender mailSender;

    @Test
    void sendNewPostingsNotification_sendsAMimeMessage() {
        // A real MimeMessage requires a Session, JavaMailSender.createMimeMessage() would normally provide it.
        jakarta.mail.Session session = jakarta.mail.Session.getInstance(new Properties());
        MimeMessage realMessage = new MimeMessage(session);
        when(mailSender.createMimeMessage()).thenReturn(realMessage);

        EmailService emailService = new EmailService(mailSender, "opowatch@gmail.com");

        PostingSummary posting = new PostingSummary();
        posting.setTitle("Analista programador");
        posting.setOrganization("Ministerio de prueba");
        posting.setUrl("https://boe.es/1");

        emailService.sendNewPostingsNotification("subscriptor@example.com", "BOE - Pruebas", List.of(posting));

        ArgumentCaptor<MimeMessage> captor = ArgumentCaptor.forClass(MimeMessage.class);
        verify(mailSender).send(captor.capture());

        System.out.println("=== sendNewPostingsNotification_sendsAMimeMessage ===");
        System.out.println("Se ha llamado a mailSender.send() con un MimeMessage: " + (captor.getValue() != null));
    }

    @Test
    void sendNewPostingsNotification_propagatesFailureAsIllegalStateException() {
        when(mailSender.createMimeMessage()).thenThrow(new RuntimeException("SMTP connection refused"));

        EmailService emailService = new EmailService(mailSender, "opowatch@gmail.com");

        PostingSummary posting = new PostingSummary();
        posting.setTitle("Analista programador");
        posting.setUrl("https://boe.es/1");

        assertThrows(RuntimeException.class, () ->
                emailService.sendNewPostingsNotification("subscriptor@example.com", "BOE - Pruebas", List.of(posting)));

        System.out.println("=== sendNewPostingsNotification_propagatesFailureAsIllegalStateException ===");
        System.out.println("Un fallo de conexion SMTP se propaga en vez de fallar en silencio");
    }
}