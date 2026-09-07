package com.igarciamen.users.payloads.response;

import java.util.Set;

public class UserInfoResponse {
    private Long id;
    private String username;
    private String email;
    private Set<String> roles;
    private boolean subscribedToNotifications;

    public UserInfoResponse(Long id, String username, String email, Set<String> roles, boolean subscribedToNotifications) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.roles = roles;
        this.subscribedToNotifications = subscribedToNotifications;
    }

    public Long getId() {
        return id;
    }
    public String getUsername() {
        return username;
    }
    public String getEmail() {
        return email;
    }
    public Set<String> getRoles() {
        return roles;
    }
    public boolean isSubscribedToNotifications() {
        return subscribedToNotifications;
    }
}