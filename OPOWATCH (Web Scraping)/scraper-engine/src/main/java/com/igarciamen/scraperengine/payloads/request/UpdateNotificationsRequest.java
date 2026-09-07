package com.igarciamen.scraperengine.payloads.request;

public class UpdateNotificationsRequest {
    private boolean subscribedToNotifications;

    public UpdateNotificationsRequest() {}

    public boolean isSubscribedToNotifications() { return subscribedToNotifications; }
    public void setSubscribedToNotifications(boolean subscribedToNotifications) { this.subscribedToNotifications = subscribedToNotifications; }
}