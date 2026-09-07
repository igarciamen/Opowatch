package com.igarciamen.notifications.payloads.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public class NotificationRequest {

    @NotBlank
    private String watcherName;

    @NotEmpty
    @Valid
    private List<PostingSummary> postings;

    @NotEmpty
    private List<String> recipientEmails;

    public NotificationRequest() {}

    public String getWatcherName() { return watcherName; }
    public void setWatcherName(String watcherName) { this.watcherName = watcherName; }

    public List<PostingSummary> getPostings() { return postings; }
    public void setPostings(List<PostingSummary> postings) { this.postings = postings; }

    public List<String> getRecipientEmails() { return recipientEmails; }
    public void setRecipientEmails(List<String> recipientEmails) { this.recipientEmails = recipientEmails; }
}