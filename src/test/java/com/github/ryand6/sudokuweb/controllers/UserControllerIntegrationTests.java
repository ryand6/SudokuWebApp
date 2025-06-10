package com.github.ryand6.sudokuweb.controllers;

import com.github.ryand6.sudokuweb.TestOAuthUtil;
import com.github.ryand6.sudokuweb.exceptions.UsernameTakenException;
import com.github.ryand6.sudokuweb.services.impl.UserService;
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

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
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

    @Test
    public void userSetupForm_returnsCorrectView() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/user-setup")
                        // Establish a mock authenticated user so that authentication is confirmed in SecurityFilterChain
                        .with(SecurityMockMvcRequestPostProcessors.oauth2Login())
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(MockMvcResultMatchers.status().isOk()) // Expect HTTP 200
                .andExpect(MockMvcResultMatchers.view().name("user-setup")); // Expect correct view name
    }

    @Test
    public void processUserSetupRequest_testUsernameTakenException_fLashAttribute() throws Exception {
        doThrow(new UsernameTakenException("Username is taken")).when(userService).createNewUser(anyString(), anyString(), anyString());
        // Get dummy token to satisfy OAuthUtil static method requirements
        OAuth2AuthenticationToken token = TestOAuthUtil.createOAuthToken();

        mockMvc.perform(MockMvcRequestBuilders.post("/process-user-setup")
                        .param("username", "newUser")
                        // Establish a mock authenticated user so that authentication is confirmed in SecurityFilterChain
                        .with(authentication(token))
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection()) // Expect HTTP redirect
                .andExpect(redirectedUrl("/user-setup"))
                .andExpect(flash().attribute("errorMessage", "Username is taken"));
    }

}
