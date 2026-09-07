package com.igarciamen.scraperengine.payloads.response;

public class UserProfileResponse {
    private Long id;
    private String username;
    private String email;
    private boolean subscribedToNotifications;

    public UserProfileResponse(Long id, String username, String email, boolean subscribedToNotifications) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.subscribedToNotifications = subscribedToNotifications;
    }

    public Long getId() { return id; }
    public String getUsername() { return username; }
    public String getEmail() { return email; }
    public boolean isSubscribedToNotifications() { return subscribedToNotifications; }
}