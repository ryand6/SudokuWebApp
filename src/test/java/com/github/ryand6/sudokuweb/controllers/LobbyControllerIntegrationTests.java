package com.github.ryand6.sudokuweb.controllers;

import com.github.ryand6.sudokuweb.services.impl.LobbyService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.mockito.Mockito.when;

@SpringBootTest
@AutoConfigureMockMvc
public class LobbyControllerIntegrationTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private LobbyService lobbyService;

    @Test
    public void createLobbyView_returnsCorrectView() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/lobby/create-lobby")
                // Establish mock authenticated user so that authentication is confirmed in SecurityFilterChain
                .with(SecurityMockMvcRequestPostProcessors.oauth2Login())
                .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(MockMvcResultMatchers.status().isOk()) // Expect HTTP 200
                .andExpect(MockMvcResultMatchers.view().name("lobby/create-lobby"));
    }

    @Test
    public void generateJoinCode_returnsJoinCodeString_returnsHTTP200() throws Exception {
        String mockCode  = "ABCDEF123456";
        when(lobbyService.generateUniqueCode()).thenReturn(mockCode );

        mockMvc.perform(MockMvcRequestBuilders.get("/lobby/generate-join-code")
                .with(SecurityMockMvcRequestPostProcessors.oauth2Login())
                .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string(mockCode ));
    }

}
