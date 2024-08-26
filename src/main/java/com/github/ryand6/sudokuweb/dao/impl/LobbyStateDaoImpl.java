package com.github.ryand6.sudokuweb.dao.impl;

import com.github.ryand6.sudokuweb.dao.LobbyStateDao;
import com.github.ryand6.sudokuweb.domain.LobbyState;
import org.springframework.jdbc.core.JdbcTemplate;

public class LobbyStateDaoImpl implements LobbyStateDao {

    private JdbcTemplate jdbcTemplate;

    public LobbyStateDaoImpl(final JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void create(LobbyState lobbyState) {
        jdbcTemplate.update("INSERT INTO lobby_state (id, lobby_id, user_id, puzzle_id, current_board_state, score) VALUES (?, ?, ?, ?, ?, ?)",
                lobbyState.getId(),
                lobbyState.getLobbyId(),
                lobbyState.getUserId(),
                lobbyState.getPuzzleId(),
                lobbyState.getCurrentBoardState(),
                lobbyState.getScore()
        );
    }

}
