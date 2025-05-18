package com.github.ryand6.sudokuweb.repositories;

import com.github.ryand6.sudokuweb.TestDataUtil;
import com.github.ryand6.sudokuweb.domain.*;
import jakarta.transaction.Transactional;
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
public class LobbyEntityStateRepositoryIntegrationTests {

    private final LobbyStateRepository underTest;
    private final UserRepository userRepository;
    private final SudokuPuzzleRepository sudokuPuzzleRepository;
    private final LobbyRepository lobbyRepository;
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public LobbyEntityStateRepositoryIntegrationTests(
            LobbyStateRepository underTest,
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
        jdbcTemplate.execute("DELETE FROM lobby_state");
        jdbcTemplate.execute("DELETE FROM users");
        jdbcTemplate.execute("DELETE FROM scores");
        jdbcTemplate.execute("DELETE FROM sudoku_puzzles");
        jdbcTemplate.execute("DELETE FROM lobbies");
    }

    @Test
    public void testLobbyStateCreationAndRecall() {
        // Create support objects in the db because lobby state relies on user, lobby and sudokuPuzzleEntity foreign keys
        ScoreEntity scoreEntity = TestDataUtil.createTestScoreA();
        UserEntity userEntity = TestDataUtil.createTestUserA(scoreEntity);
        // Persist transient instances - LobbyState does not persist other entities, it only references them
        userRepository.save(userEntity);
        LobbyEntity lobbyEntity = TestDataUtil.createTestLobbyA(userEntity);
        lobbyRepository.save(lobbyEntity);
        SudokuPuzzleEntity sudokuPuzzleEntity = TestDataUtil.createTestSudokuPuzzleA();
        sudokuPuzzleRepository.save(sudokuPuzzleEntity);
        // Checks for score creation and retrieval
        LobbyStateEntity lobbyStateEntity = TestDataUtil.createTestLobbyStateA(lobbyEntity, userEntity, sudokuPuzzleEntity);
        underTest.save(lobbyStateEntity);
        Optional<LobbyStateEntity> result = underTest.findById(lobbyStateEntity.getId());
        assertThat(result).isPresent();
        lobbyStateEntity.setLastActive(result.get().getLastActive());
        assertThat(result.get()).isEqualTo(lobbyStateEntity);
    }

    @Test
    public void testMultipleLobbyStatesCreatedAndRecalled() {
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
        LobbyStateEntity lobbyStateEntityA = TestDataUtil.createTestLobbyStateA(lobbyEntityA, userEntityA, sudokuPuzzleEntityA);
        underTest.save(lobbyStateEntityA);
        LobbyStateEntity lobbyStateEntityB = TestDataUtil.createTestLobbyStateB(lobbyEntityB, userEntityB, sudokuPuzzleEntityB);
        underTest.save(lobbyStateEntityB);
        LobbyStateEntity lobbyStateEntityC = TestDataUtil.createTestLobbyStateC(lobbyEntityC, userEntityC, sudokuPuzzleEntityC);
        underTest.save(lobbyStateEntityC);

        Iterable<LobbyStateEntity> result = underTest.findAll();
        assertThat(result)
                .hasSize(3)
                .usingRecursiveComparison()
                // Avoid lazy loaded fields when comparing
                .ignoringFields(
                        "lastActive",
                        "sudokuPuzzleEntity.lobbyStates",
                        "lobby.lobbyStates")
                .isEqualTo(List.of(lobbyStateEntityA, lobbyStateEntityB, lobbyStateEntityC));
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
        LobbyStateEntity lobbyStateEntityA = TestDataUtil.createTestLobbyStateA(lobbyEntity, userEntity, sudokuPuzzleEntity);
        underTest.save(lobbyStateEntityA);
        lobbyStateEntityA.setScore(1000);
        underTest.save(lobbyStateEntityA);
        Optional<LobbyStateEntity> result = underTest.findById(lobbyStateEntityA.getId());
        assertThat(result).isPresent();
        lobbyStateEntityA.setLastActive(result.get().getLastActive());
        assertThat(result.get()).isEqualTo(lobbyStateEntityA);
    }

    // Made transactional so that deletion will be flushed to DB within session
    @Test
    @Transactional
    public void testLobbyStateDeletion() {
        ScoreEntity scoreEntity = TestDataUtil.createTestScoreA();
        UserEntity userEntity = TestDataUtil.createTestUserA(scoreEntity);
        userRepository.save(userEntity);
        LobbyEntity lobbyEntity = TestDataUtil.createTestLobbyA(userEntity);
        lobbyRepository.save(lobbyEntity);
        SudokuPuzzleEntity sudokuPuzzleEntity = TestDataUtil.createTestSudokuPuzzleA();
        sudokuPuzzleRepository.save(sudokuPuzzleEntity);
        LobbyStateEntity lobbyStateEntityA = TestDataUtil.createTestLobbyStateA(lobbyEntity, userEntity, sudokuPuzzleEntity);
        underTest.save(lobbyStateEntityA);
        underTest.deleteById(lobbyStateEntityA.getId());
        Optional<LobbyStateEntity> result = underTest.findById(lobbyStateEntityA.getId());
        assertThat(result).isEmpty();
    }

}
