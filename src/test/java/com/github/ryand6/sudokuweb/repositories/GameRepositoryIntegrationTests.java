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
public class GameRepositoryIntegrationTests {

    private final GameRepository underTest;
    private final UserRepository userRepository;
    private final SudokuPuzzleRepository sudokuPuzzleRepository;
    private final LobbyRepository lobbyRepository;
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public GameRepositoryIntegrationTests(
            GameRepository underTest,
            UserRepository userRepository,
            SudokuPuzzleRepository sudokuPuzzleRepository,
            LobbyRepository lobbyRepository,
            JdbcTemplate jdbcTemplate) {
        this.underTest = underTest;
        this.userRepository = userRepository;
        this.sudokuPuzzleRepository = sudokuPuzzleRepository;
        this.lobbyRepository = lobbyRepository;
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
        GameEntity gameEntity = TestDataUtil.createGame(lobbyEntity, sudokuPuzzleEntity);
        underTest.save(gameEntity);
        Optional<GameEntity> result = underTest.findById(gameEntity.getId());
        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(gameEntity);
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
        GameEntity gameEntityA = TestDataUtil.createGame(lobbyEntityA, sudokuPuzzleEntityA);
        GameEntity gameEntityB = TestDataUtil.createGame(lobbyEntityB, sudokuPuzzleEntityB);
        GameEntity gameEntityC = TestDataUtil.createGame(lobbyEntityC, sudokuPuzzleEntityC);
        underTest.save(gameEntityA);
        underTest.save(gameEntityB);
        underTest.save(gameEntityC);

        Iterable<GameEntity> result = underTest.findAll();
        assertThat(result)
                .hasSize(3)
                .usingRecursiveComparison()
                // Avoid lazy loaded fields when comparing
                .ignoringFields(
                        "lobbyEntity.gameEntities",
                        "gameStateEntities"
                )
                .isEqualTo(List.of(gameEntityA, gameEntityB, gameEntityC));
    }

    @Test
    public void testScoreFullUpdate() {
        ScoreEntity scoreEntity = TestDataUtil.createTestScoreA();
        UserEntity userEntity = TestDataUtil.createTestUserA(scoreEntity);
        userRepository.save(userEntity);
        LobbyEntity lobbyEntity = TestDataUtil.createTestLobbyA(userEntity);
        lobbyRepository.save(lobbyEntity);
        SudokuPuzzleEntity sudokuPuzzleEntityA = TestDataUtil.createTestSudokuPuzzleA();
        sudokuPuzzleRepository.save(sudokuPuzzleEntityA);
        GameEntity gameEntityA = TestDataUtil.createGame(lobbyEntity, sudokuPuzzleEntityA);
        underTest.save(gameEntityA);
        // Create new puzzle to assign to game
        SudokuPuzzleEntity sudokuPuzzleEntityB = TestDataUtil.createTestSudokuPuzzleB();
        sudokuPuzzleRepository.save(sudokuPuzzleEntityB);
        gameEntityA.setSudokuPuzzleEntity(sudokuPuzzleEntityB);
        underTest.save(gameEntityA);
        GameEntity gameEntityB = TestDataUtil.createGame(lobbyEntity, sudokuPuzzleEntityB);
        Optional<GameEntity> result = underTest.findById(gameEntityA.getId());
        assertThat(result).isPresent();
        assertThat(result.get().getSudokuPuzzleEntity()).isEqualTo(sudokuPuzzleEntityB);
    }

    @Test
    public void testGameStateDeletion() {
        ScoreEntity scoreEntity = TestDataUtil.createTestScoreA();
        UserEntity userEntity = TestDataUtil.createTestUserA(scoreEntity);
        userRepository.save(userEntity);
        LobbyEntity lobbyEntity = TestDataUtil.createTestLobbyA(userEntity);
        lobbyRepository.save(lobbyEntity);
        SudokuPuzzleEntity sudokuPuzzleEntity = TestDataUtil.createTestSudokuPuzzleA();
        sudokuPuzzleRepository.save(sudokuPuzzleEntity);
        GameEntity gameEntity = TestDataUtil.createGame(lobbyEntity, sudokuPuzzleEntity);
        underTest.save(gameEntity);
        underTest.deleteById(gameEntity.getId());
        Optional<GameEntity> result = underTest.findById(gameEntity.getId());
        assertThat(result).isEmpty();
    }

}
