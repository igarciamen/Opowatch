package com.igarciamen.users.controller;

import com.igarciamen.users.model.User;
import com.igarciamen.users.payloads.request.UpdateSubscriptionRequest;
import com.igarciamen.users.payloads.response.MessageResponse;
import com.igarciamen.users.payloads.response.UserInfoResponse;
import com.igarciamen.users.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/user")
public class UserController {

    private final UserService userService;
    private final String internalApiKey;

    public UserController(UserService userService, @Value("${internal.api-key}") String internalApiKey) {
        this.userService = userService;
        this.internalApiKey = internalApiKey;
    }

    @Operation(
            summary = "Returns the current user's info",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @GetMapping(path = "/me", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UserInfoResponse> me(Authentication authentication) {
        User user = userService.findByUsername(authentication.getName());
        return ResponseEntity.ok(toResponse(user));
    }

    @Operation(
            summary = "Returns user by ID",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @GetMapping(path = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UserInfoResponse> getById(@PathVariable Long id) {
        try {
            User user = userService.findById(id);
            return ResponseEntity.ok(toResponse(user));
        } catch (IllegalArgumentException ex) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found: " + id);
        }
    }

    @Operation(
            summary = "Enables or disables email notifications for the current user",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @PatchMapping(path = "/me/subscription",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<MessageResponse> updateSubscription(
            Authentication authentication,
            @Valid @RequestBody UpdateSubscriptionRequest req) {

        userService.updateSubscription(authentication.getName(), req.isSubscribed());

        String message = req.isSubscribed()
                ? "Notifications enabled"
                : "Notifications disabled";
        return ResponseEntity.ok(new MessageResponse(message));
    }

    @Operation(summary = "Internal endpoint: returns the email of every subscribed user. " +
            "Requires the X-Internal-Api-Key header, not a user JWT, since it is only meant to be called by watchers.")
    @GetMapping(path = "/subscribers", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<String>> getSubscriberEmails(
            @RequestHeader(name = "X-Internal-Api-Key", required = false) String apiKey) {

        if (apiKey == null || !apiKey.equals(internalApiKey)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Invalid or missing internal API key");
        }

        return ResponseEntity.ok(userService.findSubscriberEmails());
    }

    private UserInfoResponse toResponse(User user) {
        Set<String> roles = user.getRoles()
                .stream()
                .map(r -> r.getName().name())
                .collect(Collectors.toSet());

        return new UserInfoResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                roles,
                user.isSubscribedToNotifications()
        );
    }
}