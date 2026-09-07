package com.igarciamen.users.controller;

import com.igarciamen.users.enums.ERole;
import com.igarciamen.users.model.Role;
import com.igarciamen.users.model.User;
import com.igarciamen.users.payloads.request.UpdateSubscriptionRequest;
import com.igarciamen.users.payloads.response.MessageResponse;
import com.igarciamen.users.payloads.response.UserInfoResponse;
import com.igarciamen.users.service.UserService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class UserControllerTest {

    private static final String TEST_INTERNAL_API_KEY = "test-key-123";

    @Mock
    private UserService userService;

    private UserController userController;

    private AutoCloseable mocks;

    @BeforeEach
    void setUp() {
        mocks = MockitoAnnotations.openMocks(this);
        userController = new UserController(userService, TEST_INTERNAL_API_KEY);
    }

    @AfterEach
    void tearDown() throws Exception {
        mocks.close();
    }

    @Test
    void me_authenticated_return_user() {
        Authentication auth = mock(Authentication.class);
        when(auth.getName()).thenReturn("john");

        User user = new User("john", "john@example.com", "1234");
        user.setId(1L);
        user.setRoles(Set.of(new Role(ERole.ROLE_USER)));
        when(userService.findByUsername("john")).thenReturn(user);

        ResponseEntity<UserInfoResponse> response = userController.me(auth);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getUsername()).isEqualTo("john");
        assertThat(response.getBody().getRoles()).containsExactly("ROLE_USER");

        System.out.println("=== me_authenticated_return_user ===");
        System.out.println("Status  : " + response.getStatusCode());
        System.out.println("Username: " + response.getBody().getUsername());
        System.out.println("Roles   : " + response.getBody().getRoles());
    }

    @Test
    void getById_user_exist() {
        User user = new User("jane", "jane@example.com", "1234");
        user.setId(5L);
        user.setRoles(Set.of(new Role(ERole.ROLE_USER)));
        when(userService.findById(5L)).thenReturn(user);

        ResponseEntity<UserInfoResponse> response = userController.getById(5L);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getUsername()).isEqualTo("jane");

        System.out.println("=== getById_user_exist ===");
        System.out.println("Status  : " + response.getStatusCode());
        System.out.println("Username: " + response.getBody().getUsername());
    }

    @Test
    void updateSubscription_enablesNotifications() {
        Authentication auth = mock(Authentication.class);
        when(auth.getName()).thenReturn("john");

        User user = new User("john", "john@example.com", "1234");
        user.setId(1L);
        user.setSubscribedToNotifications(true);
        when(userService.updateSubscription("john", true)).thenReturn(user);

        ResponseEntity<MessageResponse> response =
                userController.updateSubscription(auth, new UpdateSubscriptionRequest(true));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getMessage()).isEqualTo("Notifications enabled");

        verify(userService).updateSubscription("john", true);

        System.out.println("=== updateSubscription_enablesNotifications ===");
        System.out.println("Status : " + response.getStatusCode());
        System.out.println("Message: " + response.getBody().getMessage());
    }

    @Test
    void updateSubscription_disablesNotifications() {
        Authentication auth = mock(Authentication.class);
        when(auth.getName()).thenReturn("john");

        User user = new User("john", "john@example.com", "1234");
        user.setId(1L);
        user.setSubscribedToNotifications(false);
        when(userService.updateSubscription("john", false)).thenReturn(user);

        ResponseEntity<MessageResponse> response =
                userController.updateSubscription(auth, new UpdateSubscriptionRequest(false));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getMessage()).isEqualTo("Notifications disabled");

        verify(userService).updateSubscription("john", false);

        System.out.println("=== updateSubscription_disablesNotifications ===");
        System.out.println("Status : " + response.getStatusCode());
        System.out.println("Message: " + response.getBody().getMessage());
    }

    @Test
    void getSubscriberEmails_withCorrectKey_returnsEmails() {
        when(userService.findSubscriberEmails()).thenReturn(List.of("a@example.com", "b@example.com"));

        ResponseEntity<List<String>> response = userController.getSubscriberEmails(TEST_INTERNAL_API_KEY);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).containsExactly("a@example.com", "b@example.com");

        System.out.println("=== getSubscriberEmails_withCorrectKey_returnsEmails ===");
        System.out.println("Emails devueltos: " + response.getBody());
    }

    @Test
    void getSubscriberEmails_withWrongKey_returns403() {
        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> userController.getSubscriberEmails("clave-incorrecta"));

        assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);

        System.out.println("=== getSubscriberEmails_withWrongKey_returns403 ===");
        System.out.println("Status: " + ex.getStatusCode());
    }

    @Test
    void getSubscriberEmails_withMissingKey_returns403() {
        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> userController.getSubscriberEmails(null));

        assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);

        System.out.println("=== getSubscriberEmails_withMissingKey_returns403 ===");
        System.out.println("Status: " + ex.getStatusCode());
    }
}