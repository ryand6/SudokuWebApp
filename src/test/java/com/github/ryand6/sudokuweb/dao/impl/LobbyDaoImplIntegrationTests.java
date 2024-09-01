package com.github.ryand6.sudokuweb.dao.impl;

import com.github.ryand6.sudokuweb.TestDataUtil;
import com.github.ryand6.sudokuweb.domain.Lobby;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ExtendWith(SpringExtension.class)
public class LobbyDaoImplIntegrationTests {

    private LobbyDaoImpl underTest;

    @Autowired
    public LobbyDaoImplIntegrationTests(LobbyDaoImpl underTest) {
        this.underTest = underTest;
    }

    @Test
    public void testLobbyCreationAndRecall() {
        Lobby lobby = TestDataUtil.createTestLobby();
        underTest.create(lobby);
        Optional<Lobby> result = underTest.findOne(lobby.getId());
        assertThat(result).isPresent();
        // Set the createdAt field using the retrieved lobby as this field is set on the lobbies creation in the db
        lobby.setCreatedAt(result.get().getCreatedAt());
        assertThat(result.get()).isEqualTo(lobby);
    }

}
