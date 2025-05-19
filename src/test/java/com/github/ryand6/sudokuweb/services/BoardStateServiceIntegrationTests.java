package com.github.ryand6.sudokuweb.services;

import com.github.ryand6.sudokuweb.TestDataUtil;
import com.github.ryand6.sudokuweb.domain.*;
import com.github.ryand6.sudokuweb.dto.GenerateBoardRequestDto;
import com.github.ryand6.sudokuweb.dto.GenerateBoardResponseDto;
import com.github.ryand6.sudokuweb.repositories.LobbyRepository;
import com.github.ryand6.sudokuweb.repositories.SudokuPuzzleRepository;
import com.github.ryand6.sudokuweb.repositories.UserRepository;
import com.github.ryand6.sudokuweb.services.impl.BoardStateService;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
/*
Integration tests for BoardStateService
*/
public class BoardStateServiceIntegrationTests {

    private final BoardStateService boardStateService;
    private final LobbyRepository lobbyRepository;
    private final UserRepository userRepository;
    private final JdbcTemplate jdbcTemplate;
    private final SudokuPuzzleRepository sudokuPuzzleRepository;

    @Autowired
    public BoardStateServiceIntegrationTests(
            BoardStateService boardStateService,
            LobbyRepository lobbyRepository,
            UserRepository userRepository,
            JdbcTemplate jdbcTemplate,
            SudokuPuzzleRepository sudokuPuzzleRepository
    ) {
        this.boardStateService = boardStateService;
        this.lobbyRepository = lobbyRepository;
        this.userRepository = userRepository;
        this.jdbcTemplate = jdbcTemplate;
        this.sudokuPuzzleRepository = sudokuPuzzleRepository;
    }

    @MockBean
    private PuzzleGenerator puzzleGenerator;

    private LobbyEntity testLobby;
    private UserEntity user1;
    private UserEntity user2;
    private ScoreEntity score1;
    private ScoreEntity score2;

    @BeforeEach
    void setup() {
        jdbcTemplate.execute("DELETE FROM lobby_users");
        jdbcTemplate.execute("DELETE FROM lobby_state");
        jdbcTemplate.execute("DELETE FROM users");
        jdbcTemplate.execute("DELETE FROM scores");
        jdbcTemplate.execute("DELETE FROM sudoku_puzzles");
        jdbcTemplate.execute("DELETE FROM lobbies");
        // Setup Score entities to insert into users
        score1 = TestDataUtil.createTestScoreA();
        score2 = TestDataUtil.createTestScoreB();

        // Setup Lobby with Users
        user1 = TestDataUtil.createTestUserA(score1);
        user2 = TestDataUtil.createTestUserB(score2);

        userRepository.save(user1);
        userRepository.save(user2);

        Set<UserEntity> users = new HashSet<>();
        users.add(user1);
        users.add(user2);

        testLobby = new LobbyEntity();
        testLobby.setLobbyName("TestLobby");
        testLobby.setUserEntities(users);

        lobbyRepository.save(testLobby);
    }

    @Test
    @Transactional
    void testGenerateSudokuBoard() {
        // Used for mocking generatePuzzle
        String difficulty = "easy";
        String generatedPuzzle = "[[0,6,0,0,1,5,0,4,0],[0,5,4,0,9,0,6,0,0],[7,0,2,0,6,0,9,0,0],[0,0,5,0,0,7,0,0,1],[0,7,6,0,0,0,4,9,0],[4,0,0,9,0,0,3,0,0],[0,0,7,0,3,0,2,0,4],[0,0,9,0,7,0,5,3,0],[0,2,0,5,8,0,0,7,0]]";
        String solution = "[[9,6,8,2,1,5,7,4,3],[1,5,4,7,9,3,6,2,8],[7,3,2,4,6,8,9,1,5],[2,9,5,3,4,7,8,6,1],[3,7,6,8,5,1,4,9,2],[4,8,1,9,2,6,3,5,7],[5,1,7,6,3,9,2,8,4],[8,4,9,1,7,2,5,3,6],[6,2,3,5,8,4,1,7,9]]";

        // Mock the output of the generatePuzzle mocked bean
        when(puzzleGenerator.generatePuzzle(difficulty))
                .thenReturn(List.of(generatedPuzzle, solution));

        GenerateBoardRequestDto requestDto = new GenerateBoardRequestDto();
        requestDto.setDifficulty(difficulty);
        requestDto.setLobbyId(testLobby.getId());

        GenerateBoardResponseDto response = boardStateService.generateSudokuBoard(requestDto);

        // Test output of generateSudokuBoard
        assertThat(response).isNotNull();
        assertThat(response.getInitialBoard()).isEqualTo(generatedPuzzle);
        assertThat(response.getSolution()).isEqualTo(solution);

        // Test that a puzzle has been saved to the DB
        List<SudokuPuzzleEntity> puzzles = sudokuPuzzleRepository.findAll();
        assertThat(puzzles).hasSize(1);

        // Test that both lobby users have their own lobby state entities
        SudokuPuzzleEntity savedPuzzle = puzzles.get(0);
        assertThat(savedPuzzle.getLobbyStateEntities()).hasSize(2);

        // Test that the generated board has been assigned to each lobby user's state
        // and their lobby state score starts at 0
        for (
                LobbyStateEntity state : savedPuzzle.getLobbyStateEntities()) {
            assertThat(state.getCurrentBoardState()).isEqualTo(generatedPuzzle);
            assertThat(state.getScore()).isEqualTo(0);
        }
    }
}
