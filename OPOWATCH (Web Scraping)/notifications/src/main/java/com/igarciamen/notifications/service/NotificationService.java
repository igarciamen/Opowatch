package com.igarciamen.notifications.service;

import com.igarciamen.notifications.payloads.request.NotificationRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {

    private static final Logger log = LoggerFactory.getLogger(NotificationService.class);

    private final EmailService emailService;

    public NotificationService(EmailService emailService) {
        this.emailService = emailService;
    }

    public NotificationResult sendToSubscribers(NotificationRequest request) {
        int sent = 0;
        int failed = 0;

        for (String recipient : request.getRecipientEmails()) {
            try {
                emailService.sendNewPostingsNotification(recipient, request.getWatcherName(), request.getPostings());
                sent++;
            } catch (Exception ex) {
                // One failing recipient (e.g. an invalid address) must not stop the rest.
                log.error("Could not send notification to {}: {}", recipient, ex.getMessage());
                failed++;
            }
        }

        return new NotificationResult(sent, failed);
    }

    public record NotificationResult(int sentCount, int failedCount) {}
}