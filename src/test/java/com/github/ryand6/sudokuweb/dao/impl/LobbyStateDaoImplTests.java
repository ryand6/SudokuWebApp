package com.github.ryand6.sudokuweb.dao.impl;

import com.github.ryand6.sudokuweb.TestDataUtil;
import com.github.ryand6.sudokuweb.domain.LobbyState;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
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
        LobbyState lobbyState = TestDataUtil.createTestLobbyStateA();

        underTest.create(lobbyState);

        verify(jdbcTemplate).update(
                eq("INSERT INTO lobby_state (id, lobby_id, user_id, puzzle_id, current_board_state, score) VALUES (?, ?, ?, ?, ?, ?)"),
                eq(1L), eq(1L), eq(1L), eq(1L),
                eq("092306001007008003043207080035680000080000020000035670070801950200500800500409130"),
                eq(0)
        );
    }

    @Test
    public void testFindOneLobbyStateSql() {
        underTest.findOne(1L);

        verify(jdbcTemplate).query(
                eq("SELECT id, lobby_id, user_id, puzzle_id, current_board_state, score, last_active FROM lobby_state WHERE id = ? LIMIT 1"),
                ArgumentMatchers.<LobbyStateDaoImpl.LobbyStateRowMapper>any(),
                eq(1L)
        );
    }

    @Test
    public void testFindManyLobbyStatesSql() {
        underTest.find();
        verify(jdbcTemplate).query(
                eq("SELECT id, lobby_id, user_id, puzzle_id, current_board_state, score, last_active FROM lobby_state"),
                ArgumentMatchers.<LobbyStateDaoImpl.LobbyStateRowMapper>any()
        );
    }

    @Test
    public void testFullUpdateLobbyStateSql() {
        LobbyState lobbyState = TestDataUtil.createTestLobbyStateA();
        underTest.update(3L, lobbyState);

        verify(jdbcTemplate).update(
                eq("UPDATE lobby_state SET id = ?, lobby_id = ?, user_id = ?, puzzle_id = ?, current_board_state = ?, score = ? WHERE id = ?"),
                eq(1L), eq(1L), eq(1L), eq(1L),
                eq("092306001007008003043207080035680000080000020000035670070801950200500800500409130"),
                eq(0),
                eq(3L)
        );
    }

}
