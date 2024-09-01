package com.github.ryand6.sudokuweb.dao.impl;

import com.github.ryand6.sudokuweb.dao.LobbyStateDao;
import com.github.ryand6.sudokuweb.domain.LobbyState;
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

    @Override
    public Optional<LobbyState> findOne(Long stateId) {
        List<LobbyState> results = jdbcTemplate.query(
                "SELECT id, lobby_id, user_id, puzzle_id, current_board_state, score, last_active FROM lobby_state WHERE id = ? LIMIT 1",
                new LobbyStateDaoImpl.LobbyStateRowMapper(),
                stateId
        );
        return results.stream().findFirst();
    }

    public static class LobbyStateRowMapper implements RowMapper<LobbyState> {

        public LobbyState mapRow(ResultSet rs, int rowNum) throws SQLException {
            LobbyState lobbyState = LobbyState.builder().
                    id(rs.getLong("id")).
                    lobbyId(rs.getLong("lobby_id")).
                    userId(rs.getLong("user_id")).
                    puzzleId(rs.getLong("puzzle_id")).
                    currentBoardState(rs.getString("current_board_state")).
                    score(rs.getInt("score")).
                    build();
            // Set created_at field if exists
            Timestamp lastActiveTimestamp = rs.getTimestamp("last_active");
            if (lastActiveTimestamp != null) {
                LocalDateTime lastActive = lastActiveTimestamp.toLocalDateTime();
                lobbyState.setLastActive(lastActive);
            }
            return lobbyState;
        }

    }

}
