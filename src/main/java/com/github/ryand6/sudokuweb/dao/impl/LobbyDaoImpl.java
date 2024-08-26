package com.github.ryand6.sudokuweb.dao.impl;

import com.github.ryand6.sudokuweb.dao.LobbyDao;
import com.github.ryand6.sudokuweb.domain.Lobby;
import org.springframework.jdbc.core.JdbcTemplate;

public class LobbyDaoImpl implements LobbyDao {

    private JdbcTemplate jdbcTemplate;

    public LobbyDaoImpl(final JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void create(Lobby lobby) {
        jdbcTemplate.update("INSERT INTO lobbies (id, lobby_name, is_active) VALUES (?, ?, ?)",
                lobby.getId(),
                lobby.getLobbyName(),
                lobby.getIsActive()
        );
    }

}
