package com.github.ryand6.sudokuweb.dao.impl;

import com.github.ryand6.sudokuweb.dao.ScoreDao;
import com.github.ryand6.sudokuweb.domain.Score;
import org.springframework.jdbc.core.JdbcTemplate;

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

}
