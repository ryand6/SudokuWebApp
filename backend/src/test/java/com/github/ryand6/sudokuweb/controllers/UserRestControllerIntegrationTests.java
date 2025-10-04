package com.github.ryand6.sudokuweb.controllers;

import com.github.ryand6.sudokuweb.TestOAuthUtil;
import com.github.ryand6.sudokuweb.dto.entity.UserDto;
import com.github.ryand6.sudokuweb.dto.request.UserSetupRequestDto;
import com.github.ryand6.sudokuweb.services.UserService;
import com.github.ryand6.sudokuweb.util.OAuthUtil;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
class UserRestControllerIntegrationTests {

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

    // GET /api/users/current-user
    @Test
    void userSetupForm_returnsUserDto() throws Exception {
        UserDto userDto = UserDto.builder().username("testUser").build();
        doReturn(userDto).when(userService).getCurrentUserByOAuth(any(), any());

        mockMvc.perform(get("/api/users/current-user")
                .with(SecurityMockMvcRequestPostProcessors.oauth2Login()))
                .andExpect(status().isOk());
    }

    @Test
    void userSetupForm_withoutPrincipalOrToken_returnsUnauthorized() throws Exception {
        mockMvc.perform(get("/api/users/current-user"))
                .andExpect(status().is3xxRedirection()); // 302 Redirection to OAuth2 login page
    }

    // POST /api/users/process-user-setup
    @Test
    void processUserSetupRequest_validRequest_returnsCreated() throws Exception {
        UserSetupRequestDto requestDto = UserSetupRequestDto.builder()
                .username("ValidUser")
                .build();

        OAuth2AuthenticationToken authToken = TestOAuthUtil.createOAuthToken();

        // Use mockStatic to mock the static methods
        try (MockedStatic<OAuthUtil> oauthUtilMock = mockStatic(OAuthUtil.class)) {
            // Mock the static methods to return what you need
            oauthUtilMock.when(() -> OAuthUtil.retrieveOAuthProviderName(authToken))
                    .thenReturn(authToken.getAuthorizedClientRegistrationId());
            oauthUtilMock.when(() -> OAuthUtil.retrieveOAuthProviderId(any(), any()))
                    .thenReturn("mock-provider-id");

            // Perform the test
            mockMvc.perform(post("/api/users/process-user-setup")
                            .with(SecurityMockMvcRequestPostProcessors.oauth2Login()) // simulates authenticated user
                            .with(SecurityMockMvcRequestPostProcessors.csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(requestDto)))
                    .andExpect(status().isCreated());
        }
    }

    @Test
    void processUserSetupRequest_validationErrors_returnsBadRequest() throws Exception {
        UserSetupRequestDto requestDto = UserSetupRequestDto.builder()
                .username("") // invalid: @NotBlank
                .build();

        mockMvc.perform(post("/api/users/process-user-setup")
                        .with(SecurityMockMvcRequestPostProcessors.oauth2Login())
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isBadRequest());
    }

    // POST /api/users/process-user-amend
    @Test
    void processUserAmendRequest_validRequest_returnsNoContent() throws Exception {
        UserSetupRequestDto requestDto = UserSetupRequestDto.builder()
                .username("UpdatedUser")
                .build();
        doNothing().when(userService).updateUsername(any(), any(), any());

        mockMvc.perform(post("/api/users/process-user-amend")
                        .with(SecurityMockMvcRequestPostProcessors.oauth2Login())
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isNoContent());
    }

    @Test
    void processUserAmendRequest_validationErrors_returnsBadRequest() throws Exception {
        UserSetupRequestDto requestDto = UserSetupRequestDto.builder()
                .username("") // invalid: @NotBlank
                .build();

        mockMvc.perform(post("/api/users/process-user-amend")
                        .with(SecurityMockMvcRequestPostProcessors.oauth2Login())
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isBadRequest());
    }
}
