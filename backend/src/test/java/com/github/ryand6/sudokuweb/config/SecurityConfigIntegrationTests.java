package com.github.ryand6.sudokuweb.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.ryand6.sudokuweb.TestOAuthUtil;
import com.github.ryand6.sudokuweb.dto.UserDto;
import com.github.ryand6.sudokuweb.dto.UserSetupRequestDto;
import com.github.ryand6.sudokuweb.services.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
class SecurityConfigIntegrationTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16");

    // Dynamically register Postgres container properties with Spring
    @DynamicPropertySource
    static void configureDataSource(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void whenNotAuthenticated_thenRedirectsToLogin() throws Exception {

        when(userService.getCurrentUserByOAuth(any(), any())).thenReturn(new UserDto());

        mockMvc.perform(get("/api/users/current-user"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/oauth2/authorization/**"));
    }

    @Test
    void whenAuthenticated_thenCanAccessProtectedEndpoint() throws Exception {

        when(userService.getCurrentUserByOAuth(any(), any())).thenReturn(new UserDto());

        mockMvc.perform(get("/api/users/current-user")
                        .with(SecurityMockMvcRequestPostProcessors.oauth2Login()))
                .andExpect(status().isOk());
    }

    @Test
    void logout_returns200AndDeletesSessionCookie() throws Exception {
        mockMvc.perform(post("/logout")
                        .with(SecurityMockMvcRequestPostProcessors.oauth2Login())
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isOk())
                .andExpect(cookie().maxAge("JSESSIONID", 0)); // ensures it's deleted
    }

}
