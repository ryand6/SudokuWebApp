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

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class LobbyStateRepositoryIntegrationTests {

    private final LobbyStateRepository underTest;

    @Autowired
    public LobbyStateRepositoryIntegrationTests(LobbyStateRepository underTest) {
        this.underTest = underTest;
    }

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    public void setUp() {
        // Correct SQL syntax for deleting all rows from the tables
        jdbcTemplate.execute("DELETE FROM lobby_state");
        jdbcTemplate.execute("DELETE FROM users");
        jdbcTemplate.execute("DELETE FROM scores");
        jdbcTemplate.execute("DELETE FROM sudoku_puzzles");
        jdbcTemplate.execute("DELETE FROM lobbies");
    }

    @Test
    public void testLobbyStateCreationAndRecall() {
        // Create support objects in the db because lobby state relies on user, lobby and puzzle foreign keys
        // DB updates aren't persistent so this is required
        Score score = TestDataUtil.createTestScoreA();
        User user = TestDataUtil.createTestUserA(score);
        Lobby lobby = TestDataUtil.createTestLobbyA();
        SudokuPuzzle sudokuPuzzle = TestDataUtil.createTestSudokuPuzzleA();
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
        Lobby lobbyA = TestDataUtil.createTestLobbyA();
        Lobby lobbyB = TestDataUtil.createTestLobbyB();
        Lobby lobbyC = TestDataUtil.createTestLobbyC();
        SudokuPuzzle sudokuPuzzleA = TestDataUtil.createTestSudokuPuzzleA();
        SudokuPuzzle sudokuPuzzleB = TestDataUtil.createTestSudokuPuzzleB();
        SudokuPuzzle sudokuPuzzleC = TestDataUtil.createTestSudokuPuzzleC();

        LobbyState lobbyStateA = TestDataUtil.createTestLobbyStateA(lobbyA, userA, sudokuPuzzleA);
        underTest.save(lobbyStateA);
        LobbyState lobbyStateB = TestDataUtil.createTestLobbyStateB(lobbyB, userB, sudokuPuzzleB);
        underTest.save(lobbyStateB);
        LobbyState lobbyStateC = TestDataUtil.createTestLobbyStateC(lobbyC, userC, sudokuPuzzleC);
        underTest.save(lobbyStateC);

        Iterable<LobbyState> result = underTest.findAll();
        assertThat(result)
                .hasSize(3)
                .usingRecursiveFieldByFieldElementComparatorIgnoringFields("lastActive")
                .containsExactly(lobbyStateA, lobbyStateB, lobbyStateC);
    }

    @Test
    public void testScoreFullUpdate() {
        Score score = TestDataUtil.createTestScoreA();
        User user = TestDataUtil.createTestUserA(score);
        Lobby lobby = TestDataUtil.createTestLobbyA();
        SudokuPuzzle sudokuPuzzle = TestDataUtil.createTestSudokuPuzzleA();
        LobbyState lobbyStateA = TestDataUtil.createTestLobbyStateA(lobby, user, sudokuPuzzle);
        underTest.save(lobbyStateA);
        lobbyStateA.setScore(1000);
        underTest.save(lobbyStateA);
        Optional<LobbyState> result = underTest.findById(lobbyStateA.getId());
        assertThat(result).isPresent();
        lobbyStateA.setLastActive(result.get().getLastActive());
        assertThat(result.get()).isEqualTo(lobbyStateA);
    }

    @Test
    public void testLobbyStateDeletion() {
        Score score = TestDataUtil.createTestScoreA();
        User user = TestDataUtil.createTestUserA(score);
        Lobby lobby = TestDataUtil.createTestLobbyA();
        SudokuPuzzle sudokuPuzzle = TestDataUtil.createTestSudokuPuzzleA();
        LobbyState lobbyStateA = TestDataUtil.createTestLobbyStateA(lobby, user, sudokuPuzzle);
        underTest.save(lobbyStateA);
        underTest.deleteById(lobbyStateA.getId());
        Optional<LobbyState> result = underTest.findById(lobbyStateA.getId());
        assertThat(result).isEmpty();
    }

}
