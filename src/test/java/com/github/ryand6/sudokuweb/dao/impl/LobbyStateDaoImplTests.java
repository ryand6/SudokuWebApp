package com.github.ryand6.sudokuweb.dao.impl;

import com.github.ryand6.sudokuweb.dao.impl.LobbyStateDaoImpl;
import com.github.ryand6.sudokuweb.domain.LobbyState;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.JdbcTemplate;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class LobbyStateDaoImplTests {

    @Mock
    private JdbcTemplate jdbcTemplate;

    @InjectMocks
    private LobbyStateDaoImpl underTest;

    @Test
    public void testCreateLobbyStateSql() {
        LobbyState lobbyState = LobbyState.builder().
                id(1L).
                lobbyId(1L).
                userId(1L).
                puzzleId(1L).
                currentBoardState("092306001007008003043207080035680000080000020000035670070801950200500800500409130").
                score(0).
                build();

        underTest.create(lobbyState);

        verify(jdbcTemplate).update(
                eq("INSERT INTO lobby_state (id, lobby_id, user_id, puzzle_id, current_board_state, score) VALUES (?, ?, ?, ?, ?, ?)"),
                eq(1L), eq(1L), eq(1L), eq(1L),
                eq("092306001007008003043207080035680000080000020000035670070801950200500800500409130"),
                eq(0)
        );
    }

}
