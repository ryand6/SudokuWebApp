package com.github.ryand6.sudokuweb.services;

import com.github.ryand6.sudokuweb.TestDataUtil;
import com.github.ryand6.sudokuweb.domain.*;
import com.github.ryand6.sudokuweb.dto.entity.GameDto;
import com.github.ryand6.sudokuweb.dto.entity.GameStateDto;
import com.github.ryand6.sudokuweb.dto.request.GenerateBoardRequestDto;
import com.github.ryand6.sudokuweb.enums.PlayerColour;
import com.github.ryand6.sudokuweb.integration.AbstractIntegrationTest;
import com.github.ryand6.sudokuweb.repositories.*;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@Testcontainers
/*
Integration tests for BoardStateService
*/
public class BoardStateServiceIntegrationTests extends AbstractIntegrationTest {

    private final BoardStateService boardStateService;
    private final LobbyRepository lobbyRepository;
    private final UserRepository userRepository;
    private final SudokuPuzzleRepository sudokuPuzzleRepository;
    private final GameRepository gameRepository;
    private final GameStateRepository gameStateRepository;

    @Autowired
    public BoardStateServiceIntegrationTests(
            BoardStateService boardStateService,
            LobbyRepository lobbyRepository,
            UserRepository userRepository,
            SudokuPuzzleRepository sudokuPuzzleRepository,
            GameRepository gameRepository,
            GameStateRepository gameStateRepository) {
        this.boardStateService = boardStateService;
        this.lobbyRepository = lobbyRepository;
        this.userRepository = userRepository;
        this.sudokuPuzzleRepository = sudokuPuzzleRepository;
        this.gameRepository = gameRepository;
        this.gameStateRepository = gameStateRepository;
    }

    @MockBean
    private PuzzleGenerationService puzzleGenerationService;

    private LobbyEntity testLobby;
    private UserEntity user1;
    private UserEntity user2;
    private ScoreEntity score1;
    private ScoreEntity score2;
    private Set<UserEntity> users;

    @BeforeEach
    void setup() {
        // Setup Score entities to insert into users
        score1 = TestDataUtil.createTestScoreA();
        score2 = TestDataUtil.createTestScoreB();

        // Setup Lobby with Users
        user1 = TestDataUtil.createTestUserA(score1);
        user2 = TestDataUtil.createTestUserB(score2);

        userRepository.save(user1);
        userRepository.save(user2);

        testLobby = new LobbyEntity();
        testLobby.setLobbyName("TestLobby");
        testLobby.setHost(user1);

        lobbyRepository.save(testLobby);

        LobbyPlayerEntity lobbyPlayerA = TestDataUtil.createTestLobbyPlayer(testLobby, user1);
        LobbyPlayerEntity lobbyPlayerB = TestDataUtil.createTestLobbyPlayer(testLobby, user2);
        testLobby.setLobbyPlayers(Set.of(lobbyPlayerA, lobbyPlayerB));
    }

    @Test
    @Transactional
    void createGame_createsEntitiesAsExpected() {
        // Used for mocking generatePuzzle
        String difficulty = "easy";
        String generatedPuzzle = "[[0,6,0,0,1,5,0,4,0],[0,5,4,0,9,0,6,0,0],[7,0,2,0,6,0,9,0,0],[0,0,5,0,0,7,0,0,1],[0,7,6,0,0,0,4,9,0],[4,0,0,9,0,0,3,0,0],[0,0,7,0,3,0,2,0,4],[0,0,9,0,7,0,5,3,0],[0,2,0,5,8,0,0,7,0]]";
        String solution = "[[9,6,8,2,1,5,7,4,3],[1,5,4,7,9,3,6,2,8],[7,3,2,4,6,8,9,1,5],[2,9,5,3,4,7,8,6,1],[3,7,6,8,5,1,4,9,2],[4,8,1,9,2,6,3,5,7],[5,1,7,6,3,9,2,8,4],[8,4,9,1,7,2,5,3,6],[6,2,3,5,8,4,1,7,9]]";

        // Mock the output of the generatePuzzle mocked bean
        when(puzzleGenerationService.generatePuzzle(difficulty))
                .thenReturn(List.of(generatedPuzzle, solution));

        GenerateBoardRequestDto requestDto = new GenerateBoardRequestDto();
        requestDto.setDifficulty(difficulty);
        requestDto.setLobbyId(testLobby.getId());

        GameDto response = boardStateService.createGame(requestDto);

        // Test output of generateSudokuBoard
        assertThat(response).isNotNull();
        assertThat(response.getSudokuPuzzle().getInitialBoardState()).isEqualTo(generatedPuzzle);

        // Test that the game has been saved to the DB
        List<GameEntity> gameEntities = gameRepository.findAll();
        assertThat(gameEntities).hasSize(1);

        // Test that a puzzle has been saved to the DB
        List<SudokuPuzzleEntity> puzzles = sudokuPuzzleRepository.findAll();
        assertThat(puzzles).hasSize(1);

        // Test that both game users have their own game state entities
        assertThat(response.getGameStates()).hasSize(2);

        // Test that the game states have been saved to the DB
        List<GameStateEntity> gameStateEntities = gameStateRepository.findAll();
        assertThat(gameStateEntities).hasSize(2);

        // Test contents of each game state DTO
        for (GameStateDto state : response.getGameStates()) {
            assertThat(state.getCurrentBoardState()).isEqualTo(generatedPuzzle);
            assertThat(state.getScore()).isEqualTo(0);
            assertThat(state.getPlayerColour()).isIn(PlayerColour.values());
        }
    }
}
