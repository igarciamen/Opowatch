package com.igarciamen.watchers.client.dto;

import java.util.List;

public class NotificationRequestDto {
    private String watcherName;
    private List<PostingSummaryDto> postings;
    private List<String> recipientEmails;

    public NotificationRequestDto(String watcherName, List<PostingSummaryDto> postings, List<String> recipientEmails) {
        this.watcherName = watcherName;
        this.postings = postings;
        this.recipientEmails = recipientEmails;
    }

    public String getWatcherName() { return watcherName; }
    public List<PostingSummaryDto> getPostings() { return postings; }
    public List<String> getRecipientEmails() { return recipientEmails; }
}