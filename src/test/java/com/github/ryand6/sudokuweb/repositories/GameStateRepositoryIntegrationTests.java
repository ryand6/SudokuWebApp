package com.github.ryand6.sudokuweb.repositories;

import com.github.ryand6.sudokuweb.TestDataUtil;
import com.github.ryand6.sudokuweb.domain.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class GameStateRepositoryIntegrationTests {

    private final GameStateRepository underTest;
    private final UserRepository userRepository;
    private final SudokuPuzzleRepository sudokuPuzzleRepository;
    private final LobbyRepository lobbyRepository;
    private final GameRepository gameRepository;
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public GameStateRepositoryIntegrationTests(
            GameStateRepository underTest,
            UserRepository userRepository,
            SudokuPuzzleRepository sudokuPuzzleRepository,
            LobbyRepository lobbyRepository,
            GameRepository gameRepository,
            JdbcTemplate jdbcTemplate) {
        this.underTest = underTest;
        this.userRepository = userRepository;
        this.sudokuPuzzleRepository = sudokuPuzzleRepository;
        this.lobbyRepository = lobbyRepository;
        this.gameRepository = gameRepository;
        this.jdbcTemplate = jdbcTemplate;
    }

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
    }

    @Test
    public void testGameStateCreationAndRecall() {
        // Create support objects in the db because lobby state relies on user, lobby and sudokuPuzzleEntity foreign keys
        ScoreEntity scoreEntity = TestDataUtil.createTestScoreA();
        UserEntity userEntity = TestDataUtil.createTestUserA(scoreEntity);
        // Persist transient instances - LobbyState does not persist other entities, it only references them
        userRepository.save(userEntity);
        LobbyEntity lobbyEntity = TestDataUtil.createTestLobbyA(userEntity);
        lobbyRepository.save(lobbyEntity);
        SudokuPuzzleEntity sudokuPuzzleEntity = TestDataUtil.createTestSudokuPuzzleA();
        sudokuPuzzleRepository.save(sudokuPuzzleEntity);
        GameEntity gameEntity = TestDataUtil.createGameA(lobbyEntity, sudokuPuzzleEntity);
        gameRepository.save(gameEntity);
        // Checks for score creation and retrieval
        GameStateEntity gameStateEntity = TestDataUtil.createTestGameStateA(gameEntity, userEntity);
        underTest.save(gameStateEntity);
        Optional<GameStateEntity> result = underTest.findById(gameStateEntity.getId());
        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(gameStateEntity);
    }

    @Test
    public void testMultipleGameStatesCreatedAndRecalled() {
        ScoreEntity scoreEntityA = TestDataUtil.createTestScoreA();
        ScoreEntity scoreEntityB = TestDataUtil.createTestScoreB();
        ScoreEntity scoreEntityC = TestDataUtil.createTestScoreC();
        UserEntity userEntityA = TestDataUtil.createTestUserA(scoreEntityA);
        UserEntity userEntityB = TestDataUtil.createTestUserB(scoreEntityB);
        UserEntity userEntityC = TestDataUtil.createTestUserC(scoreEntityC);
        userRepository.save(userEntityA);
        userRepository.save(userEntityB);
        userRepository.save(userEntityC);
        LobbyEntity lobbyEntityA = TestDataUtil.createTestLobbyA(userEntityA);
        LobbyEntity lobbyEntityB = TestDataUtil.createTestLobbyB(userEntityA, userEntityB);
        LobbyEntity lobbyEntityC = TestDataUtil.createTestLobbyC(userEntityA, userEntityB, userEntityC);
        lobbyRepository.save(lobbyEntityA);
        lobbyRepository.save(lobbyEntityB);
        lobbyRepository.save(lobbyEntityC);
        SudokuPuzzleEntity sudokuPuzzleEntityA = TestDataUtil.createTestSudokuPuzzleA();
        SudokuPuzzleEntity sudokuPuzzleEntityB = TestDataUtil.createTestSudokuPuzzleB();
        SudokuPuzzleEntity sudokuPuzzleEntityC = TestDataUtil.createTestSudokuPuzzleC();
        sudokuPuzzleRepository.save(sudokuPuzzleEntityA);
        sudokuPuzzleRepository.save(sudokuPuzzleEntityB);
        sudokuPuzzleRepository.save(sudokuPuzzleEntityC);
        GameEntity gameEntityA = TestDataUtil.createGameA(lobbyEntityA, sudokuPuzzleEntityA);
        GameEntity gameEntityB = TestDataUtil.createGameA(lobbyEntityB, sudokuPuzzleEntityB);
        GameEntity gameEntityC = TestDataUtil.createGameA(lobbyEntityC, sudokuPuzzleEntityC);
        gameRepository.save(gameEntityA);
        gameRepository.save(gameEntityB);
        gameRepository.save(gameEntityC);
        GameStateEntity gameStateEntityA = TestDataUtil.createTestGameStateA(gameEntityA, userEntityA);
        underTest.save(gameStateEntityA);
        GameStateEntity gameStateEntityB = TestDataUtil.createTestGameStateB(gameEntityB, userEntityB);
        underTest.save(gameStateEntityB);
        GameStateEntity gameStateEntityC = TestDataUtil.createTestGameStateC(gameEntityC, userEntityC);
        underTest.save(gameStateEntityC);

        Iterable<GameStateEntity> result = underTest.findAll();
        assertThat(result)
                .hasSize(3)
                .usingRecursiveComparison()
                // Avoid lazy loaded fields when comparing
                .ignoringFields(
                        "gameEntity.lobbyEntity.gameEntities",
                        "gameEntity.gameStateEntities"
                        )
                .isEqualTo(List.of(gameStateEntityA, gameStateEntityB, gameStateEntityC));
    }

    @Test
    public void testScoreFullUpdate() {
        ScoreEntity scoreEntity = TestDataUtil.createTestScoreA();
        UserEntity userEntity = TestDataUtil.createTestUserA(scoreEntity);
        userRepository.save(userEntity);
        LobbyEntity lobbyEntity = TestDataUtil.createTestLobbyA(userEntity);
        lobbyRepository.save(lobbyEntity);
        SudokuPuzzleEntity sudokuPuzzleEntity = TestDataUtil.createTestSudokuPuzzleA();
        sudokuPuzzleRepository.save(sudokuPuzzleEntity);
        GameEntity gameEntity = TestDataUtil.createGameA(lobbyEntity, sudokuPuzzleEntity);
        gameRepository.save(gameEntity);
        GameStateEntity gameStateEntityA = TestDataUtil.createTestGameStateA(gameEntity, userEntity);
        underTest.save(gameStateEntityA);
        gameStateEntityA.setScore(1000);
        underTest.save(gameStateEntityA);
        Optional<GameStateEntity> result = underTest.findById(gameStateEntityA.getId());
        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(gameStateEntityA);
    }

    // Made transactional so that deletion will be flushed to DB within session
    @Test
    public void testGameStateDeletion() {
        ScoreEntity scoreEntity = TestDataUtil.createTestScoreA();
        UserEntity userEntity = TestDataUtil.createTestUserA(scoreEntity);
        userRepository.save(userEntity);
        LobbyEntity lobbyEntity = TestDataUtil.createTestLobbyA(userEntity);
        lobbyRepository.save(lobbyEntity);
        SudokuPuzzleEntity sudokuPuzzleEntity = TestDataUtil.createTestSudokuPuzzleA();
        sudokuPuzzleRepository.save(sudokuPuzzleEntity);
        GameEntity gameEntity = TestDataUtil.createGameA(lobbyEntity, sudokuPuzzleEntity);
        gameRepository.save(gameEntity);
        GameStateEntity gameStateEntityA = TestDataUtil.createTestGameStateA(gameEntity, userEntity);
        underTest.save(gameStateEntityA);
        underTest.deleteById(gameStateEntityA.getId());
        Optional<GameStateEntity> result = underTest.findById(gameStateEntityA.getId());
        assertThat(result).isEmpty();
    }

}
