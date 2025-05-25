package com.github.ryand6.sudokuweb.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.ryand6.sudokuweb.TestDataUtil;
import com.github.ryand6.sudokuweb.domain.LobbyEntity;
import com.github.ryand6.sudokuweb.domain.ScoreEntity;
import com.github.ryand6.sudokuweb.domain.UserEntity;
import com.github.ryand6.sudokuweb.dto.GameDto;
import com.github.ryand6.sudokuweb.dto.GenerateBoardRequestDto;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


@SpringBootTest
@ExtendWith(SpringExtension.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@AutoConfigureMockMvc
public class BoardStateControllerIntegrationTests {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    private final UserRepository userRepository;
    private final LobbyRepository lobbyRepository;
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public BoardStateControllerIntegrationTests(
            MockMvc mockMvc,
            UserRepository userRepository,
            LobbyRepository lobbyRepository,
            JdbcTemplate jdbcTemplate) {
        this.mockMvc = mockMvc;
        this.userRepository = userRepository;
        this.lobbyRepository = lobbyRepository;
        this.jdbcTemplate = jdbcTemplate;
        this.objectMapper = new ObjectMapper();
    }

    private ScoreEntity score;
    private UserEntity user;
    private LobbyEntity lobby;

    @BeforeEach
    public void setUp() {
        // Correct SQL syntax for deleting all rows from the tables
        jdbcTemplate.execute("DELETE FROM lobby_users");
        jdbcTemplate.execute("DELETE FROM game_state");
        jdbcTemplate.execute("DELETE FROM games");
        jdbcTemplate.execute("DELETE FROM lobbies");
        jdbcTemplate.execute("DELETE FROM users");
        jdbcTemplate.execute("DELETE FROM scores");
        jdbcTemplate.execute("DELETE FROM sudoku_puzzles");
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
                MockMvcRequestBuilders.post("/generate-board")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestDtoJson)
        ).andExpect(
                MockMvcResultMatchers.status().isOk()
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
                MockMvcRequestBuilders.post("/generate-board")
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
            MockMvcRequestBuilders.post("/generate-board")
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
