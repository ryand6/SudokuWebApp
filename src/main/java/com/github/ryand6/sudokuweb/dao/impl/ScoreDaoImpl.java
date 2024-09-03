package com.github.ryand6.sudokuweb.dao.impl;

import com.github.ryand6.sudokuweb.dao.ScoreDao;
import com.github.ryand6.sudokuweb.domain.Score;
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
public class ScoreDaoImpl implements ScoreDao {

    private final JdbcTemplate jdbcTemplate;

    public ScoreDaoImpl(final JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void create(Score score) {
        jdbcTemplate.update(
                "INSERT INTO scores (id, user_id, total_score, games_played) VALUES (?, ?, ?, ?)",
                score.getId(),
                score.getUserId(),
                score.getTotalScore(),
                score.getGamesPlayed()
        );
    }

    @Override
    public Optional<Score> findOne(Long scoreId) {
        List<Score> results = jdbcTemplate.query(
                "SELECT id, user_id, total_score, games_played, created_at, updated_at FROM scores WHERE id = ? LIMIT 1",
                new ScoreDaoImpl.ScoreRowMapper(),
                scoreId
        );
        return results.stream().findFirst();
    }

    @Override
    public List<Score> find() {
        return jdbcTemplate.query(
                "SELECT id, user_id, total_score, games_played, created_at, updated_at FROM scores",
                new ScoreRowMapper()
        );
    }

    @Override
    public void update(Long scoreId, Score score) {
        jdbcTemplate.update(
                "UPDATE scores SET id = ?, user_id = ?, total_score = ?, games_played = ? WHERE id = ?",
                score.getId(), score.getUserId(), score.getTotalScore(), score.getGamesPlayed(), scoreId
        );
    }

    public static class ScoreRowMapper implements RowMapper<Score> {

        public Score mapRow(ResultSet rs, int rowNum) throws SQLException {
            Score score = Score.builder().
                    id(rs.getLong("id")).
                    userId(rs.getLong("user_id")).
                    totalScore(rs.getInt("total_score")).
                    gamesPlayed(rs.getInt("games_played")).
                    build();
            // Set created_at field if exists
            Timestamp createdTimestamp = rs.getTimestamp("created_at");
            if (createdTimestamp != null) {
                LocalDateTime createdAt = createdTimestamp.toLocalDateTime();
                score.setCreatedAt(createdAt);
            }
            // Set updated_at field if exists
            Timestamp updatedTimestamp = rs.getTimestamp("updated_at");
            if (updatedTimestamp != null) {
                LocalDateTime updatedAt = updatedTimestamp.toLocalDateTime();
                score.setUpdatedAt(updatedAt);
            }
            return score;
        }

    }

}
