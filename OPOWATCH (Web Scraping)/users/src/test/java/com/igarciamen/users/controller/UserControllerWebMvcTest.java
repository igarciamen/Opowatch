package com.igarciamen.users.controller;

import com.igarciamen.users.enums.ERole;
import com.igarciamen.users.model.Role;
import com.igarciamen.users.model.User;
import com.igarciamen.users.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.security.core.Authentication;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;
import java.util.Set;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserController.class)
@AutoConfigureMockMvc(addFilters = false)
@TestPropertySource(properties = "internal.api-key=test-key-123")
class UserControllerWebMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @Test
    void me_authenticated_ok() throws Exception {
        Authentication auth = mock(Authentication.class);
        when(auth.isAuthenticated()).thenReturn(true);
        when(auth.getName()).thenReturn("john");

        User user = new User("john", "john@example.com", "1234");
        user.setId(1L);
        user.setRoles(Set.of(new Role(ERole.ROLE_USER)));
        when(userService.findByUsername("john")).thenReturn(user);

        MvcResult result = mockMvc.perform(get("/api/user/me").principal(auth))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("john"))
                .andReturn();

        System.out.println("=== GET /api/user/me ===");
        System.out.println("Status: " + result.getResponse().getStatus());
        System.out.println("Body  : " + result.getResponse().getContentAsString());
    }

    @Test
    void getById_ok() throws Exception {
        User user = new User("jane", "jane@example.com", "1234");
        user.setId(5L);
        user.setRoles(Set.of(new Role(ERole.ROLE_USER)));
        when(userService.findById(5L)).thenReturn(user);

        MvcResult result = mockMvc.perform(get("/api/user/5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("jane"))
                .andReturn();

        System.out.println("=== GET /api/user/5 ===");
        System.out.println("Status: " + result.getResponse().getStatus());
        System.out.println("Body  : " + result.getResponse().getContentAsString());
    }

    @Test
    void getSubscribers_withCorrectKey_ok() throws Exception {
        when(userService.findSubscriberEmails()).thenReturn(List.of("a@example.com", "b@example.com"));

        MvcResult result = mockMvc.perform(get("/api/user/subscribers")
                        .header("X-Internal-Api-Key", "test-key-123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0]").value("a@example.com"))
                .andReturn();

        System.out.println("=== GET /api/user/subscribers (clave correcta) ===");
        System.out.println("Status: " + result.getResponse().getStatus());
        System.out.println("Body  : " + result.getResponse().getContentAsString());
    }

    @Test
    void getSubscribers_withoutKey_forbidden() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/user/subscribers"))
                .andExpect(status().isForbidden())
                .andReturn();

        System.out.println("=== GET /api/user/subscribers (sin clave) ===");
        System.out.println("Status: " + result.getResponse().getStatus());
    }

    @Test
    void getSubscribers_withWrongKey_forbidden() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/user/subscribers")
                        .header("X-Internal-Api-Key", "clave-equivocada"))
                .andExpect(status().isForbidden())
                .andReturn();

        System.out.println("=== GET /api/user/subscribers (clave incorrecta) ===");
        System.out.println("Status: " + result.getResponse().getStatus());
    }
}