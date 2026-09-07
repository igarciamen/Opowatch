package com.igarciamen.notifications.payloads.response;

public class NotificationResultResponse {
    private int sentCount;
    private int failedCount;

    public NotificationResultResponse(int sentCount, int failedCount) {
        this.sentCount = sentCount;
        this.failedCount = failedCount;
    }

    public int getSentCount() { return sentCount; }
    public int getFailedCount() { return failedCount; }
}