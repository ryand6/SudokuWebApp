package com.github.ryand6.sudokuweb.dao.impl;

import com.github.ryand6.sudokuweb.TestDataUtil;
import com.github.ryand6.sudokuweb.dao.SudokuPuzzleDao;
import com.github.ryand6.sudokuweb.domain.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ExtendWith(SpringExtension.class)
public class LobbyStateDaoImplIntegrationTests {

    private LobbyStateDaoImpl underTest;
    private UserDaoImpl userDao;
    private LobbyDaoImpl lobbyDao;
    private SudokuPuzzleDao sudokuPuzzleDao;

    @Autowired
    public LobbyStateDaoImplIntegrationTests(LobbyStateDaoImpl underTest, UserDaoImpl supportTestUser, LobbyDaoImpl supportTestLobby, SudokuPuzzleDao supportTestPuzzle) {
        this.underTest = underTest;
        this.userDao = supportTestUser;
        this.lobbyDao = supportTestLobby;
        this.sudokuPuzzleDao = supportTestPuzzle;
    }

    @Test
    public void testScoreCreationAndRecall() {
        // Create support objects in the db because lobby state relies on user, lobby and puzzle foreign keys
        // DB updates aren't persistent so this is required
        User user = TestDataUtil.createTestUserA();
        userDao.create(user);
        Lobby lobby = TestDataUtil.createTestLobby();
        lobbyDao.create(lobby);
        SudokuPuzzle sudokuPuzzle = TestDataUtil.createTestSudokuPuzzle();
        sudokuPuzzleDao.create(sudokuPuzzle);
        // Checks for score creation and retrieval
        LobbyState lobbyState = TestDataUtil.createTestLobbyState();
        underTest.create(lobbyState);
        Optional<LobbyState> result = underTest.findOne(lobbyState.getId());
        assertThat(result).isPresent();
        lobbyState.setLastActive(result.get().getLastActive());
        assertThat(result.get()).isEqualTo(lobbyState);
    }

}
