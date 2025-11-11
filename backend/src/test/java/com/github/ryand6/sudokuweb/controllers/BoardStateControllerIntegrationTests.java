package com.github.ryand6.sudokuweb.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.github.ryand6.sudokuweb.TestDataUtil;
import com.github.ryand6.sudokuweb.domain.LobbyEntity;
import com.github.ryand6.sudokuweb.domain.ScoreEntity;
import com.github.ryand6.sudokuweb.domain.UserEntity;
import com.github.ryand6.sudokuweb.dto.entity.GameDto;
import com.github.ryand6.sudokuweb.dto.request.GenerateBoardRequestDto;
import com.github.ryand6.sudokuweb.integration.AbstractControllerIntegrationTests;
import com.github.ryand6.sudokuweb.repositories.LobbyRepository;
import com.github.ryand6.sudokuweb.repositories.UserRepository;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class BoardStateControllerIntegrationTests extends AbstractControllerIntegrationTests {

    @Autowired
    private ObjectMapper objectMapper;
    private final UserRepository userRepository;
    private final LobbyRepository lobbyRepository;

    @Autowired
    public BoardStateControllerIntegrationTests(
            UserRepository userRepository,
            LobbyRepository lobbyRepository) {
        this.userRepository = userRepository;
        this.lobbyRepository = lobbyRepository;
    }

    private ScoreEntity score;
    private UserEntity user;
    private LobbyEntity lobby;

    @BeforeEach
    public void setUp() {
        // Lobby needs to exist for generateSudokuBoard() to work
        score = TestDataUtil.createTestScoreA();
        user = TestDataUtil.createTestUserA(score);
        userRepository.save(user);
        lobby = lobbyRepository.save(TestDataUtil.createTestLobbyA(user));
    }

    @Test
    public void generateSudokuBoard_returnsHttp200Response() throws Exception {
        GenerateBoardRequestDto requestDto = GenerateBoardRequestDto.builder()
                .difficulty("easy")
                .lobbyId(lobby.getId())
                .build();

        String requestDtoJson = objectMapper.writeValueAsString(requestDto);

        mockMvc.perform(
                MockMvcRequestBuilders.post("/create-game")
                        // Establish a mock authenticated user so that authentication is confirmed in SecurityFilterChain
                        .with(SecurityMockMvcRequestPostProcessors.oauth2Login())
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestDtoJson)
        ).andExpect(
                MockMvcResultMatchers.status().isOk()
        );
    }

    @Test
    public void generateSudokuBoard_unauthenticatedUser_returnsHttp403Response() throws Exception {
        GenerateBoardRequestDto requestDto = GenerateBoardRequestDto.builder()
                .difficulty("easy")
                .lobbyId(lobby.getId())
                .build();

        String requestDtoJson = objectMapper.writeValueAsString(requestDto);

        mockMvc.perform(
                MockMvcRequestBuilders.post("/create-game")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestDtoJson)
        ).andExpect(
                MockMvcResultMatchers.status().isForbidden()
        );
    }

    @Test
    public void generateSudokuBoard_returnsValidPuzzle() throws Exception {
        GenerateBoardRequestDto requestDto = GenerateBoardRequestDto.builder()
                .difficulty("easy")
                .lobbyId(lobby.getId())
                .build();

        String requestDtoJson = objectMapper.writeValueAsString(requestDto);

        MvcResult result = mockMvc.perform(
                MockMvcRequestBuilders.post("/create-game")
                        .with(SecurityMockMvcRequestPostProcessors.oauth2Login())
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestDtoJson)
        ).andReturn();

        String response = result.getResponse().getContentAsString();

        // Parse the response to the DTO
        GameDto gameDto = objectMapper.readValue(response, GameDto.class);

        // Parse initialBoard and solution as 2D arrays
        int[][] initialBoard = objectMapper.readValue(gameDto.getSudokuPuzzle().getInitialBoardState(), int[][].class);

        // Check dimensions
        assertEquals(9, initialBoard.length);

        // Check dimensions of each row and that all values are within correct range
        for (int i = 0; i < 9; i++) {
            assertEquals(9, initialBoard[i].length, "initialBoard row " + i + " length mismatch");

            for (int j = 0; j < 9; j++) {
                assertTrue(initialBoard[i][j] >= 0 && initialBoard[i][j] <= 9, "Invalid number in initialBoard");
            }
        }

    }

    @Test
    public void generateSudokuBoard_invalidDifficulty() throws Exception {
        GenerateBoardRequestDto requestDto = GenerateBoardRequestDto.builder()
                .difficulty("XTREME")
                .lobbyId(lobby.getId())
                .build();

        String requestDtoJson = objectMapper.writeValueAsString(requestDto);

        mockMvc.perform(
            MockMvcRequestBuilders.post("/create-game")
                    .with(SecurityMockMvcRequestPostProcessors.oauth2Login())
                    .with(SecurityMockMvcRequestPostProcessors.csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requestDtoJson)
            ).andExpect(
                    MockMvcResultMatchers.status().isBadRequest()
            )
            .andExpect(
                    MockMvcResultMatchers.content().string(
                            Matchers.containsString("Invalid difficulty value")
                    )
            );
    }

}
