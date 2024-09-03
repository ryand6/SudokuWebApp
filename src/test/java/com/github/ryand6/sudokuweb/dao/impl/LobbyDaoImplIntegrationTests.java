package com.github.ryand6.sudokuweb.dao.impl;

import com.github.ryand6.sudokuweb.TestDataUtil;
import com.github.ryand6.sudokuweb.domain.Lobby;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class LobbyDaoImplIntegrationTests {

    private LobbyDaoImpl underTest;

    @Autowired
    public LobbyDaoImplIntegrationTests(LobbyDaoImpl underTest) {
        this.underTest = underTest;
    }

    @Test
    public void testLobbyCreationAndRecall() {
        Lobby lobby = TestDataUtil.createTestLobbyA();
        underTest.create(lobby);
        Optional<Lobby> result = underTest.findOne(lobby.getId());
        assertThat(result).isPresent();
        // Set the createdAt field using the retrieved lobby as this field is set on the lobbies creation in the db
        lobby.setCreatedAt(result.get().getCreatedAt());
        assertThat(result.get()).isEqualTo(lobby);
    }

    @Test
    public void testMultipleLobbiesCreatedAndRecalled() {
        Lobby lobbyA = TestDataUtil.createTestLobbyA();
        underTest.create(lobbyA);
        Lobby lobbyB = TestDataUtil.createTestLobbyB();
        underTest.create(lobbyB);
        Lobby lobbyC = TestDataUtil.createTestLobbyC();
        underTest.create(lobbyC);

        List<Lobby> result = underTest.find();
        lobbyA.setCreatedAt(result.get(0).getCreatedAt());
        lobbyB.setCreatedAt(result.get(1).getCreatedAt());
        lobbyC.setCreatedAt(result.get(2).getCreatedAt());
        assertThat(result)
                .hasSize(3)
                .containsExactly(lobbyA, lobbyB, lobbyC);
    }

    @Test
    public void testLobbyFullUpdate() {
        Lobby lobbyA = TestDataUtil.createTestLobbyA();
        underTest.create(lobbyA);
        lobbyA.setLobbyName("UPDATED");
        underTest.update(lobbyA.getId(), lobbyA);
        Optional<Lobby> result = underTest.findOne(lobbyA.getId());
        assertThat(result).isPresent();
        lobbyA.setCreatedAt(result.get().getCreatedAt());
        assertThat(result.get()).isEqualTo(lobbyA);
    }

    @Test
    public void testSudokuPuzzleDeletion() {
        Lobby lobbyA = TestDataUtil.createTestLobbyA();
        underTest.create(lobbyA);
        underTest.delete(lobbyA.getId());
        Optional<Lobby> result = underTest.findOne(lobbyA.getId());
        assertThat(result).isEmpty();
    }

}
