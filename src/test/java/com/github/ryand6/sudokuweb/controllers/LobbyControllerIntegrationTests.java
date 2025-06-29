package com.github.ryand6.sudokuweb.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.ryand6.sudokuweb.dto.LobbyDto;
import com.github.ryand6.sudokuweb.dto.UserDto;
import com.github.ryand6.sudokuweb.exceptions.InvalidLobbyPublicStatusParametersException;
import com.github.ryand6.sudokuweb.exceptions.LobbyFullException;
import com.github.ryand6.sudokuweb.exceptions.LobbyInactiveException;
import com.github.ryand6.sudokuweb.exceptions.LobbyNotFoundException;
import com.github.ryand6.sudokuweb.services.impl.LobbyService;
import com.github.ryand6.sudokuweb.services.impl.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@SpringBootTest
@AutoConfigureMockMvc
public class LobbyControllerIntegrationTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private LobbyService lobbyService;

    @MockBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper; // Used to convert objects to JSON

    // Used for testing multiple exception types for one test case
    static Stream<Arguments> exceptionProvider() {
        return Stream.of(
                Arguments.of(new LobbyFullException("Lobby is full"), "Lobby is full"),
                Arguments.of(new LobbyInactiveException("Lobby is inactive"), "Lobby is inactive"),
                Arguments.of(new LobbyNotFoundException("Lobby not found"), "Lobby not found"),
                Arguments.of(new RuntimeException("Unexpected error occurred when trying to join Lobby"), "Unexpected error occurred when trying to join Lobby")
        );
    }

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
        when(lobbyService.generateUniqueCode()).thenReturn(mockCode);

        mockMvc.perform(MockMvcRequestBuilders.get("/lobby/generate-join-code")
                .with(SecurityMockMvcRequestPostProcessors.oauth2Login())
                .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string(mockCode ));
    }

    @Test
    public void processLobbySetupRequest_createsLobby_returnsCorrectView() throws Exception {
        LobbyDto lobbyDto = new LobbyDto();
        lobbyDto.setId(1L);
        UserDto userDto = new UserDto();
        userDto.setId(1L);
        when(lobbyService.createNewLobby(any(), any(), any(), any(), any())).thenReturn(lobbyDto);
        when(userService.getCurrentUserByOAuth(any(), any())).thenReturn(userDto);

        mockMvc.perform(MockMvcRequestBuilders.post("/lobby/process-lobby-setup")
                .with(SecurityMockMvcRequestPostProcessors.oauth2Login())
                .with(SecurityMockMvcRequestPostProcessors.csrf())
                .param("lobbyName", "testLobby"))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.redirectedUrl("/lobby/1"));
    }

    @Test
    public void processLobbySetupRequest_userNotFound() throws Exception {
        when(userService.getCurrentUserByOAuth(any(), any())).thenReturn(null);

        mockMvc.perform(MockMvcRequestBuilders.post("/lobby/process-lobby-setup")
                        // Establish a mock authenticated user so that authentication is confirmed in SecurityFilterChain
                        .with(SecurityMockMvcRequestPostProcessors.oauth2Login())
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .param("lobbyName", "testLobby"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("error/user-not-found"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("errorMessage"))
                .andExpect(MockMvcResultMatchers.model().attribute("errorMessage", "User not found via OAuth token"));
    }

    @Test
    public void processLobbySetupRequest_invalidLobbyPublicStatusParametersExceptionThrown_flashAttributeExists() throws Exception {
        UserDto userDto = new UserDto();
        userDto.setId(1L);
        when(lobbyService.createNewLobby(any(), any(), any(), any(), any())).thenThrow(new InvalidLobbyPublicStatusParametersException("Invalid isPublic and isPrivate parameter states"));
        when(userService.getCurrentUserByOAuth(any(), any())).thenReturn(userDto);

        mockMvc.perform(MockMvcRequestBuilders.post("/lobby/process-lobby-setup")
                        .with(SecurityMockMvcRequestPostProcessors.oauth2Login())
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .param("lobbyName", "testLobby"))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.flash().attributeExists("errorMessage"))
                .andExpect(MockMvcResultMatchers.flash().attribute("errorMessage", "Invalid isPublic and isPrivate parameter states"))
                .andExpect(MockMvcResultMatchers.redirectedUrl("/lobby/create-lobby"));
    }

    @Test
    public void processLobbySetupRequest_otherExceptionThrown_flashAttributeExists() throws Exception {
        UserDto userDto = new UserDto();
        userDto.setId(1L);
        when(lobbyService.createNewLobby(any(), any(), any(), any(), any())).thenThrow(new RuntimeException("Unknown error occurred"));
        when(userService.getCurrentUserByOAuth(any(), any())).thenReturn(userDto);

        mockMvc.perform(MockMvcRequestBuilders.post("/lobby/process-lobby-setup")
                        .with(SecurityMockMvcRequestPostProcessors.oauth2Login())
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .param("lobbyName", "testLobby"))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.flash().attributeExists("errorMessage"))
                .andExpect(MockMvcResultMatchers.flash().attribute("errorMessage", "Unexpected error occurred when trying to create Lobby"))
                .andExpect(MockMvcResultMatchers.redirectedUrl("/lobby/create-lobby"));
    }

    @Test
    public void getPublicLobbies_returnsHTTP200() throws Exception {
        List<LobbyDto> lobbyDtoList = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            LobbyDto lobbyDto = new LobbyDto();
            lobbyDto.setId(Long.valueOf(i));
            lobbyDtoList.add(lobbyDto);
        }
        when(lobbyService.getPublicLobbies(anyInt(), eq(10))).thenReturn(lobbyDtoList);

        mockMvc.perform(MockMvcRequestBuilders.get("/lobby/public/get-active-lobbies")
                        .with(SecurityMockMvcRequestPostProcessors.oauth2Login())
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .param("page", "0"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                // Serialises LobbyDtoList to JSON string and compares with Controller ouput
                .andExpect(MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(lobbyDtoList)));
    }

    @Test
    public void attemptJoinPublicLobby_userNotFound() throws Exception {
        when(userService.getCurrentUserByOAuth(any(), any())).thenReturn(null);

        mockMvc.perform(MockMvcRequestBuilders.post("/lobby/public/join/1")
                        // Establish a mock authenticated user so that authentication is confirmed in SecurityFilterChain
                        .with(SecurityMockMvcRequestPostProcessors.oauth2Login())
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("error/user-not-found"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("errorMessage"))
                .andExpect(MockMvcResultMatchers.model().attribute("errorMessage", "User not found via OAuth token"));
    }

    // Make use of exceptionProvider stream to test multiple arguments
    @ParameterizedTest
    @MethodSource("exceptionProvider")
    void attemptJoinPublicLobby_handlesExceptions(Exception exception, String expectedMessage) throws Exception {
        UserDto userDto = new UserDto();
        userDto.setId(1L);
        when(userService.getCurrentUserByOAuth(any(), any())).thenReturn(userDto);
        when(lobbyService.joinPublicLobby(eq(1L), eq(userDto.getId()))).thenThrow(exception);

        mockMvc.perform(MockMvcRequestBuilders.post("/lobby/public/join/1")
                        .with(SecurityMockMvcRequestPostProcessors.oauth2Login())
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.redirectedUrl("/dashboard"))
                .andExpect(MockMvcResultMatchers.flash().attribute("errorMessage", expectedMessage));
    }

    @Test
    public void attemptJoinPublicLobby_returnsCorrectView() throws Exception {
        UserDto userDto = new UserDto();
        userDto.setId(1L);
        when(userService.getCurrentUserByOAuth(any(), any())).thenReturn(userDto);
        LobbyDto lobbyDto = new LobbyDto();
        lobbyDto.setId(1L);
        when(lobbyService.joinPublicLobby(eq(lobbyDto.getId()), eq(userDto.getId()))).thenReturn(lobbyDto);
        mockMvc.perform(MockMvcRequestBuilders.post("/lobby/public/join/1")
                        // Establish a mock authenticated user so that authentication is confirmed in SecurityFilterChain
                        .with(SecurityMockMvcRequestPostProcessors.oauth2Login())
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.redirectedUrl("/lobby/1"));
    }

    @Test
    public void leavePublicLobby_handlesException() throws Exception {
        UserDto userDto = new UserDto();
        userDto.setId(1L);
        when(userService.getCurrentUserByOAuth(any(), any())).thenReturn(userDto);
        when(lobbyService.removePlayerFromLobby(eq(userDto.getId()), eq(1L))).thenThrow(new RuntimeException());
        mockMvc.perform(MockMvcRequestBuilders.post("/lobby/public/leave")
                        .param("lobbyId", String.valueOf(1L))
                        .with(SecurityMockMvcRequestPostProcessors.oauth2Login())
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("")); // empty body indicates null return
    }

    @Test
    void leavePublicLobby_returnsLobbyDtoWhenSuccessful() throws Exception {
        // Arrange
        UserDto user = new UserDto();
        user.setId(1L);

        LobbyDto expectedLobby = new LobbyDto();
        expectedLobby.setId(42L);
        expectedLobby.setLobbyName("Test Lobby");

        when(userService.getCurrentUserByOAuth(any(), any())).thenReturn(user);
        when(lobbyService.removePlayerFromLobby(1L, 42L)).thenReturn(expectedLobby);

        mockMvc.perform(MockMvcRequestBuilders.post("/lobby/public/leave")
                        .param("lobbyId", "42")
                        .with(SecurityMockMvcRequestPostProcessors.oauth2Login())
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(42))
                .andExpect(MockMvcResultMatchers.jsonPath("$.lobbyName").value("Test Lobby"));
    }

}
