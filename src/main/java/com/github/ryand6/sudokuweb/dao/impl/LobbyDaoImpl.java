package com.github.ryand6.sudokuweb.dao.impl;

import com.github.ryand6.sudokuweb.dao.LobbyDao;
import com.github.ryand6.sudokuweb.domain.Lobby;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Component
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

    @Override
    public Optional<Lobby> findOne(Long lobbyId) {
        List<Lobby> results = jdbcTemplate.query(
                "SELECT id, lobby_name, created_at, is_active FROM lobbies WHERE id = ? LIMIT 1",
                new LobbyDaoImpl.LobbyRowMapper(),
                lobbyId
        );
        return results.stream().findFirst();
    }

    @Override
    public List<Lobby> find() {
        return jdbcTemplate.query(
                "SELECT id, lobby_name, created_at, is_active FROM lobbies",
                new LobbyRowMapper()
        );
    }

    @Override
    public void update(Long lobbyId, Lobby lobby) {
        jdbcTemplate.update(
                "UPDATE lobbies SET id = ?, lobby_name = ?, is_active = ? WHERE id = ?",
                lobby.getId(), lobby.getLobbyName(), lobby.getIsActive(), lobbyId
        );
    }

    @Override
    public void delete(Long lobbyId) {
        jdbcTemplate.update(
                "DELETE FROM lobbies WHERE id = ?",
                lobbyId
        );
    }

    public static class LobbyRowMapper implements RowMapper<Lobby> {

        public Lobby mapRow(ResultSet rs, int rowNum) throws SQLException {
            Lobby lobby = Lobby.builder().
                    id(rs.getLong("id")).
                    lobbyName(rs.getString("lobby_name")).
                    isActive(rs.getBoolean("is_active")).
                    build();
            // Set created_at field if exists
            Timestamp createdTimestamp = rs.getTimestamp("created_at");
            if (createdTimestamp != null) {
                LocalDateTime createdAt = createdTimestamp.toLocalDateTime();
                lobby.setCreatedAt(createdAt);
            }
            return lobby;
        }

    }

}
