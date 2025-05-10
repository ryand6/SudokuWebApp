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
public class LobbyStateRepositoryIntegrationTests {

    private final LobbyStateRepository underTest;
    private final UserRepository userRepository;
    private final SudokuPuzzleRepository sudokuPuzzleRepository;
    private final LobbyRepository lobbyRepository;
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public LobbyStateRepositoryIntegrationTests(
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
        // Create support objects in the db because lobby state relies on user, lobby and puzzle foreign keys
        Score score = TestDataUtil.createTestScoreA();
        User user = TestDataUtil.createTestUserA(score);
        // Persist transient instances - LobbyState does not persist other entities, it only references them
        userRepository.save(user);
        Lobby lobby = TestDataUtil.createTestLobbyA(user);
        lobbyRepository.save(lobby);
        SudokuPuzzle sudokuPuzzle = TestDataUtil.createTestSudokuPuzzleA();
        sudokuPuzzleRepository.save(sudokuPuzzle);
        // Checks for score creation and retrieval
        LobbyState lobbyState = TestDataUtil.createTestLobbyStateA(lobby, user, sudokuPuzzle);
        underTest.save(lobbyState);
        Optional<LobbyState> result = underTest.findById(lobbyState.getId());
        assertThat(result).isPresent();
        lobbyState.setLastActive(result.get().getLastActive());
        assertThat(result.get()).isEqualTo(lobbyState);
    }

    @Test
    public void testMultipleLobbyStatesCreatedAndRecalled() {
        Score scoreA = TestDataUtil.createTestScoreA();
        Score scoreB = TestDataUtil.createTestScoreB();
        Score scoreC = TestDataUtil.createTestScoreC();
        User userA = TestDataUtil.createTestUserA(scoreA);
        User userB = TestDataUtil.createTestUserB(scoreB);
        User userC = TestDataUtil.createTestUserC(scoreC);
        userRepository.save(userA);
        userRepository.save(userB);
        userRepository.save(userC);
        Lobby lobbyA = TestDataUtil.createTestLobbyA(userA);
        Lobby lobbyB = TestDataUtil.createTestLobbyB(userA, userB);
        Lobby lobbyC = TestDataUtil.createTestLobbyC(userA, userB, userC);
        lobbyRepository.save(lobbyA);
        lobbyRepository.save(lobbyB);
        lobbyRepository.save(lobbyC);
        SudokuPuzzle sudokuPuzzleA = TestDataUtil.createTestSudokuPuzzleA();
        SudokuPuzzle sudokuPuzzleB = TestDataUtil.createTestSudokuPuzzleB();
        SudokuPuzzle sudokuPuzzleC = TestDataUtil.createTestSudokuPuzzleC();
        sudokuPuzzleRepository.save(sudokuPuzzleA);
        sudokuPuzzleRepository.save(sudokuPuzzleB);
        sudokuPuzzleRepository.save(sudokuPuzzleC);
        LobbyState lobbyStateA = TestDataUtil.createTestLobbyStateA(lobbyA, userA, sudokuPuzzleA);
        underTest.save(lobbyStateA);
        LobbyState lobbyStateB = TestDataUtil.createTestLobbyStateB(lobbyB, userB, sudokuPuzzleB);
        underTest.save(lobbyStateB);
        LobbyState lobbyStateC = TestDataUtil.createTestLobbyStateC(lobbyC, userC, sudokuPuzzleC);
        underTest.save(lobbyStateC);

        Iterable<LobbyState> result = underTest.findAll();
        assertThat(result)
                .hasSize(3)
                .usingRecursiveComparison()
                // Avoid lazy loaded fields when comparing
                .ignoringFields(
                        "lastActive",
                        "puzzle.lobbyStates",
                        "lobby.lobbyStates")
                .isEqualTo(List.of(lobbyStateA, lobbyStateB, lobbyStateC));
    }

    @Test
    public void testScoreFullUpdate() {
        Score score = TestDataUtil.createTestScoreA();
        User user = TestDataUtil.createTestUserA(score);
        userRepository.save(user);
        Lobby lobby = TestDataUtil.createTestLobbyA(user);
        lobbyRepository.save(lobby);
        SudokuPuzzle sudokuPuzzle = TestDataUtil.createTestSudokuPuzzleA();
        sudokuPuzzleRepository.save(sudokuPuzzle);
        LobbyState lobbyStateA = TestDataUtil.createTestLobbyStateA(lobby, user, sudokuPuzzle);
        underTest.save(lobbyStateA);
        lobbyStateA.setScore(1000);
        underTest.save(lobbyStateA);
        Optional<LobbyState> result = underTest.findById(lobbyStateA.getId());
        assertThat(result).isPresent();
        lobbyStateA.setLastActive(result.get().getLastActive());
        assertThat(result.get()).isEqualTo(lobbyStateA);
    }

    // Made transactional so that deletion will be flushed to DB within session
    @Test
    @Transactional
    public void testLobbyStateDeletion() {
        Score score = TestDataUtil.createTestScoreA();
        User user = TestDataUtil.createTestUserA(score);
        userRepository.save(user);
        Lobby lobby = TestDataUtil.createTestLobbyA(user);
        lobbyRepository.save(lobby);
        SudokuPuzzle sudokuPuzzle = TestDataUtil.createTestSudokuPuzzleA();
        sudokuPuzzleRepository.save(sudokuPuzzle);
        LobbyState lobbyStateA = TestDataUtil.createTestLobbyStateA(lobby, user, sudokuPuzzle);
        underTest.save(lobbyStateA);
        underTest.deleteById(lobbyStateA.getId());
        Optional<LobbyState> result = underTest.findById(lobbyStateA.getId());
        assertThat(result).isEmpty();
    }

}
