package com.github.ryand6.sudokuweb.dao.impl;

import com.github.ryand6.sudokuweb.domain.Lobby;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.JdbcTemplate;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class LobbyDaoImplTests {

    @Mock
    private JdbcTemplate jdbcTemplate;

    @InjectMocks
    private LobbyDaoImpl underTest;

    @Test
    public void testCreateLobbyStateSql() {
        Lobby lobby = Lobby.builder().
                id(1L).
                lobbyName("Guru Lobby").
                isActive(true).
                build();

        underTest.create(lobby);

        verify(jdbcTemplate).update(
                eq("INSERT INTO lobbies (id, lobby_name, is_active) VALUES (?, ?, ?)"),
                eq(1L), eq("Guru Lobby"), eq(true)
        );
    }

}
