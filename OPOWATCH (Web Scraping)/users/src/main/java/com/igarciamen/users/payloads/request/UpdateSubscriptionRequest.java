package com.igarciamen.users.payloads.request;

public class UpdateSubscriptionRequest {
    private boolean subscribed;

    public UpdateSubscriptionRequest() {}

    public UpdateSubscriptionRequest(boolean subscribed) {
        this.subscribed = subscribed;
    }

    public boolean isSubscribed() { return subscribed; }
    public void setSubscribed(boolean subscribed) { this.subscribed = subscribed; }
}