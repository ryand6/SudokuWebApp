package com.github.ryand6.sudokuweb.controllers;

import com.github.ryand6.sudokuweb.TestOAuthUtil;
import com.github.ryand6.sudokuweb.dto.ScoreDto;
import com.github.ryand6.sudokuweb.dto.UserDto;
import com.github.ryand6.sudokuweb.exceptions.UsernameTakenException;
import com.github.ryand6.sudokuweb.services.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;

@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerIntegrationTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

//    @Test
//    public void userSetupForm_returnsCorrectView() throws Exception {
//        mockMvc.perform(MockMvcRequestBuilders.get("/user-setup")
//                        // Establish a mock authenticated user so that authentication is confirmed in SecurityFilterChain
//                        .with(SecurityMockMvcRequestPostProcessors.oauth2Login())
//                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
//                .andExpect(MockMvcResultMatchers.status().isOk()) // Expect HTTP 200
//                .andExpect(MockMvcResultMatchers.view().name("user-setup")); // Expect correct view name
//    }
//
//    @Test
//    public void processUserSetupRequest_testUsernameTakenException_fLashAttributeExists() throws Exception {
//        doThrow(new UsernameTakenException("Username is taken")).when(userService).createNewUser(anyString(), anyString(), anyString());
//        // Get dummy token to satisfy OAuthUtil static method requirements
//        OAuth2AuthenticationToken token = TestOAuthUtil.createOAuthToken();
//
//        mockMvc.perform(MockMvcRequestBuilders.post("/process-user-setup")
//                        .param("username", "newUser")
//                        // Establish a mock authenticated user so that authentication is confirmed in SecurityFilterChain
//                        .with(authentication(token))
//                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
//                .andExpect(MockMvcResultMatchers.status().is3xxRedirection()) // Expect HTTP redirect
//                .andExpect(redirectedUrl("/user-setup"))
//                .andExpect(flash().attribute("errorMessage", "Username is taken"));
//    }
//
//    @Test
//    public void processUserSetupRequest_testOtherException_fLashAttributeExists() throws Exception {
//        doThrow(new RuntimeException("Unknown error")).when(userService).createNewUser(anyString(), anyString(), anyString());
//        // Get dummy token to satisfy OAuthUtil static method requirements
//        OAuth2AuthenticationToken token = TestOAuthUtil.createOAuthToken();
//
//        mockMvc.perform(MockMvcRequestBuilders.post("/process-user-setup")
//                        .param("username", "newUser")
//                        // Establish a mock authenticated user so that authentication is confirmed in SecurityFilterChain
//                        .with(authentication(token))
//                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
//                .andExpect(MockMvcResultMatchers.status().is3xxRedirection()) // Expect HTTP redirect
//                .andExpect(redirectedUrl("/user-setup"))
//                .andExpect(flash().attribute("errorMessage", "Unexpected error occurred when trying to create User"));
//    }
//
//    @Test
//    public void processUserSetupRequest_successfulDashboardRedirect() throws Exception {
//        UserDto user = new UserDto();
//        user.setId(1L);
//        user.setUsername("test");
//        ScoreDto scoreDto = new ScoreDto();
//        scoreDto.setId(1L);
//        scoreDto.setGamesPlayed(0);
//        scoreDto.setTotalScore(0);
//        user.setScore(scoreDto);
//        user.setIsOnline(true);
//
//        // Get dummy token to satisfy OAuthUtil static method requirements
//        OAuth2AuthenticationToken token = TestOAuthUtil.createOAuthToken();
//
//        when(userService.createNewUser(anyString(), anyString(), anyString())).thenReturn(user);
//
//        mockMvc.perform(MockMvcRequestBuilders.post("/process-user-setup")
//                        .param("username", "newUser")
//                        // Establish a mock authenticated user so that authentication is confirmed in SecurityFilterChain
//                        .with(authentication(token))
//                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
//                .andExpect(MockMvcResultMatchers.status().is3xxRedirection()) // Expect HTTP redirect
//                .andExpect(redirectedUrl("/dashboard"));
//    }
//
//    @Test
//    public void getUserDashboard_ModelAttributesExist_returnsCorrectView() throws Exception {
//        UserDto user = new UserDto();
//        user.setId(1L);
//        user.setUsername("test");
//        ScoreDto scoreDto = new ScoreDto();
//        scoreDto.setId(1L);
//        scoreDto.setGamesPlayed(0);
//        scoreDto.setTotalScore(0);
//        user.setScore(scoreDto);
//        user.setIsOnline(true);
//
//        when(userService.getCurrentUserByOAuth(any(), any())).thenReturn(user);
//        when(userService.getTop5PlayersTotalScore()).thenReturn(List.of());
//        when(userService.getPlayerRank(1L)).thenReturn(1L);
//
//        mockMvc.perform(MockMvcRequestBuilders.get("/dashboard")
//                        // Establish a mock authenticated user so that authentication is confirmed in SecurityFilterChain
//                        .with(SecurityMockMvcRequestPostProcessors.oauth2Login())
//                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
//                .andExpect(MockMvcResultMatchers.status().isOk())
//                .andExpect(MockMvcResultMatchers.view().name("dashboard"))
//                .andExpect(MockMvcResultMatchers.model().attribute("user", user))
//                .andExpect(MockMvcResultMatchers.model().attributeExists("topPlayers"))
//                .andExpect(MockMvcResultMatchers.model().attribute("userRank", 1L));
//    }
//
//    @Test
//    public void getUserDashboard_userNotFound() throws Exception {
//        when(userService.getCurrentUserByOAuth(any(), any())).thenReturn(null);
//        when(userService.getTop5PlayersTotalScore()).thenReturn(List.of());
//        when(userService.getPlayerRank(1L)).thenReturn(1L);
//
//        mockMvc.perform(MockMvcRequestBuilders.get("/dashboard")
//                        // Establish a mock authenticated user so that authentication is confirmed in SecurityFilterChain
//                        .with(SecurityMockMvcRequestPostProcessors.oauth2Login())
//                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
//                .andExpect(MockMvcResultMatchers.status().isOk())
//                .andExpect(MockMvcResultMatchers.view().name("error/user-not-found"))
//                .andExpect(MockMvcResultMatchers.model().attributeExists("errorMessage"))
//                .andExpect(MockMvcResultMatchers.model().attribute("errorMessage", "User not found via OAuth token"));
//    }

}
