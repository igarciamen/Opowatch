package com.igarciamen.watchers.client;

import com.igarciamen.watchers.client.dto.NotificationRequestDto;
import com.igarciamen.watchers.client.dto.NotificationResultResponseDto;
import com.igarciamen.watchers.client.dto.PostingSummaryDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Component
public class NotificationsClient {

    private final RestTemplate restTemplate;
    private final String baseUrl;

    public NotificationsClient(RestTemplate restTemplate, @Value("${notifications.base-url}") String baseUrl) {
        this.restTemplate = restTemplate;
        this.baseUrl = baseUrl;
    }

    public void notifySubscribers(String watcherName, List<PostingSummaryDto> postings, List<String> recipientEmails) {
        String url = baseUrl + "/api/notifications/send";
        NotificationRequestDto request = new NotificationRequestDto(watcherName, postings, recipientEmails);
        restTemplate.postForObject(url, request, NotificationResultResponseDto.class);
    }
}