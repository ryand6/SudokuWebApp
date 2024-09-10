package com.github.ryand6.sudokuweb.repositories;

import com.github.ryand6.sudokuweb.TestDataUtil;
import com.github.ryand6.sudokuweb.domain.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
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

//    @Test
//    public void testMultipleLobbyStatesCreatedAndRecalled() {
//        User userA = TestDataUtil.createTestUserA();
//        userDao.create(userA);
//        User userB = TestDataUtil.createTestUserB();
//        userDao.create(userB);
//        User userC = TestDataUtil.createTestUserC();
//        userDao.create(userC);
//
//        Lobby lobbyA = TestDataUtil.createTestLobbyA();
//        lobbyDao.create(lobbyA);
//        Lobby lobbyB = TestDataUtil.createTestLobbyB();
//        lobbyDao.create(lobbyB);
//        Lobby lobbyC = TestDataUtil.createTestLobbyC();
//        lobbyDao.create(lobbyC);
//
//        SudokuPuzzle sudokuPuzzleA = TestDataUtil.createTestSudokuPuzzleA();
//        sudokuPuzzleDao.create(sudokuPuzzleA);
//        SudokuPuzzle sudokuPuzzleB = TestDataUtil.createTestSudokuPuzzleB();
//        sudokuPuzzleDao.create(sudokuPuzzleB);
//        SudokuPuzzle sudokuPuzzleC = TestDataUtil.createTestSudokuPuzzleC();
//        sudokuPuzzleDao.create(sudokuPuzzleC);
//
//        LobbyState lobbyStateA = TestDataUtil.createTestLobbyStateA();
//        underTest.create(lobbyStateA);
//        LobbyState lobbyStateB = TestDataUtil.createTestLobbyStateB();
//        underTest.create(lobbyStateB);
//        LobbyState lobbyStateC = TestDataUtil.createTestLobbyStateC();
//        underTest.create(lobbyStateC);
//
//        List<LobbyState> result = underTest.find();
//        lobbyStateA.setLastActive(result.get(0).getLastActive());
//        lobbyStateB.setLastActive(result.get(1).getLastActive());
//        lobbyStateC.setLastActive(result.get(2).getLastActive());
//        assertThat(result)
//                .hasSize(3)
//                .containsExactly(lobbyStateA, lobbyStateB, lobbyStateC);
//    }
//
//    @Test
//    public void testScoreFullUpdate() {
//        User user = TestDataUtil.createTestUserA();
//        userDao.create(user);
//        Lobby lobby = TestDataUtil.createTestLobbyA();
//        lobbyDao.create(lobby);
//        SudokuPuzzle sudokuPuzzle = TestDataUtil.createTestSudokuPuzzleA();
//        sudokuPuzzleDao.create(sudokuPuzzle);
//
//        LobbyState lobbyStateA = TestDataUtil.createTestLobbyStateA();
//        underTest.create(lobbyStateA);
//        lobbyStateA.setScore(1000);
//        underTest.update(lobbyStateA.getId(), lobbyStateA);
//        Optional<LobbyState> result = underTest.findOne(lobbyStateA.getId());
//        assertThat(result).isPresent();
//        lobbyStateA.setLastActive(result.get().getLastActive());
//        assertThat(result.get()).isEqualTo(lobbyStateA);
//    }
//
//    @Test
//    public void testLobbyStateDeletion() {
//        User user = TestDataUtil.createTestUserA();
//        userDao.create(user);
//        Lobby lobby = TestDataUtil.createTestLobbyA();
//        lobbyDao.create(lobby);
//        SudokuPuzzle sudokuPuzzle = TestDataUtil.createTestSudokuPuzzleA();
//        sudokuPuzzleDao.create(sudokuPuzzle);
//
//        LobbyState lobbyStateA = TestDataUtil.createTestLobbyStateA();
//        underTest.create(lobbyStateA);
//        underTest.delete(lobbyStateA.getId());
//        Optional<LobbyState> result = underTest.findOne(lobbyStateA.getId());
//        assertThat(result).isEmpty();
//    }

}
