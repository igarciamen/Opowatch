package com.igarciamen.watchers.client.dto;

public class NotificationResultResponseDto {
    private int sentCount;
    private int failedCount;

    public int getSentCount() { return sentCount; }
    public void setSentCount(int sentCount) { this.sentCount = sentCount; }

    public int getFailedCount() { return failedCount; }
    public void setFailedCount(int failedCount) { this.failedCount = failedCount; }
}