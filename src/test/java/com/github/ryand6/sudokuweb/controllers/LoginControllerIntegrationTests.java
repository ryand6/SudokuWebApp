package com.github.ryand6.sudokuweb.controllers;

import com.github.ryand6.sudokuweb.dto.ScoreDto;
import com.github.ryand6.sudokuweb.dto.UserDto;
import com.github.ryand6.sudokuweb.services.impl.UserService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class LoginControllerIntegrationTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Test
    public void loginSuccess_userNotFound_redirectsToUserSetup() throws Exception {
        // Mock OAuth2User and OAuth2AuthenticationToken
        OAuth2User mockPrincipal = Mockito.mock(OAuth2User.class);
        OAuth2AuthenticationToken mockAuthToken = Mockito.mock(OAuth2AuthenticationToken.class);

        // Make userService return null for this input to simulate user not found
        Mockito.when(userService.getCurrentUserByOAuth(mockPrincipal, mockAuthToken)).thenReturn(null);

        mockMvc.perform(
                MockMvcRequestBuilders.get("/login-success")
                        // Establish a mock authenticated user so that authentication is confirmed in SecurityFilterChain
                        .with(SecurityMockMvcRequestPostProcessors.oauth2Login())
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
        )
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/user-setup"));
    }

    @Test
    public void loginSuccess_userFound_redirectsToDashboard() throws Exception {
        OAuth2User mockPrincipal = Mockito.mock(OAuth2User.class);
        OAuth2AuthenticationToken mockAuthToken = Mockito.mock(OAuth2AuthenticationToken.class);

        // Simulate found user
        UserDto mockDto = new UserDto();
        mockDto.setUsername("Test");
        mockDto.setIsOnline(true);
        ScoreDto scoreDto = new ScoreDto();
        scoreDto.setTotalScore(0);
        scoreDto.setGamesPlayed(0);
        mockDto.setScore(scoreDto);

        Mockito.when(userService.getCurrentUserByOAuth(any(OAuth2User.class), any(OAuth2AuthenticationToken.class))).thenReturn(mockDto);

        mockMvc.perform(get("/login-success")
                        // Establish a mock authenticated user so that authentication is confirmed in SecurityFilterChain
                        .with(SecurityMockMvcRequestPostProcessors.oauth2Login())
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/dashboard"));
    }
}
