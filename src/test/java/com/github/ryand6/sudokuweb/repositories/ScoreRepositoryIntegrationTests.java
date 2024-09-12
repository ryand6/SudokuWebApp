package com.github.ryand6.sudokuweb.repositories;

import com.github.ryand6.sudokuweb.TestDataUtil;
import com.github.ryand6.sudokuweb.domain.Score;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class ScoreRepositoryIntegrationTests {

    private final ScoreRepository underTest;

    @Autowired
    public ScoreRepositoryIntegrationTests(ScoreRepository underTest) {
        this.underTest = underTest;
    }

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    public void setUp() {
        // Correct SQL syntax for deleting all rows from the tables
        jdbcTemplate.execute("DELETE FROM lobby_state");
        jdbcTemplate.execute("DELETE FROM users");
        jdbcTemplate.execute("DELETE FROM scores");
        jdbcTemplate.execute("DELETE FROM sudoku_puzzles");
        jdbcTemplate.execute("DELETE FROM lobbies");
    }

    @Test
    public void testScoreCreationAndRecall() {
        // Checks for score creation and retrieval
        Score score = TestDataUtil.createTestScoreA();
        underTest.save(score);
        Optional<Score> result = underTest.findById(score.getId());
        assertThat(result).isPresent();
        score.setCreatedAt(result.get().getCreatedAt());
        score.setUpdatedAt(result.get().getUpdatedAt());
        assertThat(result.get()).isEqualTo(score);
    }

    @Test
    public void testMultipleScoresCreatedAndRecalled() {
        Score scoreA = TestDataUtil.createTestScoreA();
        underTest.save(scoreA);
        Score scoreB = TestDataUtil.createTestScoreB();
        underTest.save(scoreB);
        Score scoreC = TestDataUtil.createTestScoreC();
        underTest.save(scoreC);

        Iterable<Score> result = underTest.findAll();
        assertThat(result)
                .hasSize(3)
                .usingRecursiveFieldByFieldElementComparatorIgnoringFields("createdAt")
                .usingRecursiveFieldByFieldElementComparatorIgnoringFields("updatedAt")
                .containsExactly(scoreA, scoreB, scoreC);
    }

    @Test
    public void testScoreFullUpdate() {
        Score scoreA = TestDataUtil.createTestScoreA();
        underTest.save(scoreA);
        scoreA.setTotalScore(1000);
        underTest.save(scoreA);
        Optional<Score> result = underTest.findById(scoreA.getId());
        assertThat(result).isPresent();
        scoreA.setCreatedAt(result.get().getCreatedAt());
        scoreA.setUpdatedAt(result.get().getUpdatedAt());
        assertThat(result.get()).isEqualTo(scoreA);
    }

    @Test
    public void testScoreDeletion() {
        Score scoreA = TestDataUtil.createTestScoreA();
        underTest.save(scoreA);
        underTest.deleteById(scoreA.getId());
        Optional<Score> result = underTest.findById(scoreA.getId());
        assertThat(result).isEmpty();
    }

}
