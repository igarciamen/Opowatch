package com.igarciamen.notifications.controller;

import com.igarciamen.notifications.payloads.request.NotificationRequest;
import com.igarciamen.notifications.payloads.response.NotificationResultResponse;
import com.igarciamen.notifications.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @Operation(summary = "Sends an email to every recipient about the new postings detected by a watcher")
    @PostMapping(path = "/send", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<NotificationResultResponse> send(@Valid @RequestBody NotificationRequest request) {
        NotificationService.NotificationResult result = notificationService.sendToSubscribers(request);
        return ResponseEntity.ok(new NotificationResultResponse(result.sentCount(), result.failedCount()));
    }
}