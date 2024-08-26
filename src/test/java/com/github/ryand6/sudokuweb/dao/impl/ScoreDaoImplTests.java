package com.github.ryand6.sudokuweb.dao.impl;

import com.github.ryand6.sudokuweb.dao.impl.ScoreDaoImpl;
import com.github.ryand6.sudokuweb.domain.Score;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.JdbcTemplate;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class ScoreDaoImplTests {

    @Mock
    private JdbcTemplate jdbcTemplate;

    @InjectMocks
    private ScoreDaoImpl underTest;

    @Test
    public void testCreateScoreSql() {
        Score score = Score.builder().
                id(1L).
                userId(1L).
                totalScore(150).
                gamesPlayed(1).
                build();

        underTest.create(score);

        verify(jdbcTemplate).update(
                eq("INSERT INTO scores (id, user_id, total_score, games_played) VALUES (?, ?, ?, ?)"),
                eq(1L), eq(1L), eq(150), eq(1)
        );
    }

}
