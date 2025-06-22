package com.github.ryand6.sudokuweb.repositories;

import com.github.ryand6.sudokuweb.TestDataUtil;
import com.github.ryand6.sudokuweb.domain.ScoreEntity;
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
        jdbcTemplate.execute("DELETE FROM lobby_users");
        jdbcTemplate.execute("DELETE FROM game_state");
        jdbcTemplate.execute("DELETE FROM games");
        jdbcTemplate.execute("DELETE FROM lobbies");
        jdbcTemplate.execute("DELETE FROM lobbyPlayers");
        jdbcTemplate.execute("DELETE FROM scores");
        jdbcTemplate.execute("DELETE FROM sudoku_puzzles");
    }

    @Test
    public void testScoreCreationAndRecall() {
        // Checks for score creation and retrieval
        ScoreEntity scoreEntity = TestDataUtil.createTestScoreA();
        underTest.save(scoreEntity);
        Optional<ScoreEntity> result = underTest.findById(scoreEntity.getId());
        assertThat(result).isPresent();
        scoreEntity.setUpdatedAt(result.get().getUpdatedAt());
        assertThat(result.get()).isEqualTo(scoreEntity);
    }

    @Test
    public void testMultipleScoresCreatedAndRecalled() {
        ScoreEntity scoreEntityA = TestDataUtil.createTestScoreA();
        underTest.save(scoreEntityA);
        ScoreEntity scoreEntityB = TestDataUtil.createTestScoreB();
        underTest.save(scoreEntityB);
        ScoreEntity scoreEntityC = TestDataUtil.createTestScoreC();
        underTest.save(scoreEntityC);

        Iterable<ScoreEntity> result = underTest.findAll();
        assertThat(result)
                .hasSize(3)
                .usingRecursiveFieldByFieldElementComparatorIgnoringFields("updatedAt")
                .containsExactly(scoreEntityA, scoreEntityB, scoreEntityC);
    }

    @Test
    public void testScoreFullUpdate() {
        ScoreEntity scoreEntityA = TestDataUtil.createTestScoreA();
        underTest.save(scoreEntityA);
        scoreEntityA.setTotalScore(1000);
        underTest.save(scoreEntityA);
        Optional<ScoreEntity> result = underTest.findById(scoreEntityA.getId());
        assertThat(result).isPresent();
        scoreEntityA.setUpdatedAt(result.get().getUpdatedAt());
        assertThat(result.get()).isEqualTo(scoreEntityA);
    }

    @Test
    public void testScoreDeletion() {
        ScoreEntity scoreEntityA = TestDataUtil.createTestScoreA();
        underTest.save(scoreEntityA);
        underTest.deleteById(scoreEntityA.getId());
        Optional<ScoreEntity> result = underTest.findById(scoreEntityA.getId());
        assertThat(result).isEmpty();
    }

    @Test
    public void testGetScoreWithTotalScoreLessThan(){
        ScoreEntity testScoreAEntity = TestDataUtil.createTestScoreA();
        underTest.save(testScoreAEntity);
        ScoreEntity testScoreBEntity = TestDataUtil.createTestScoreB();
        underTest.save(testScoreBEntity);
        ScoreEntity testScoreCEntity = TestDataUtil.createTestScoreC();
        underTest.save(testScoreCEntity);

        Iterable<ScoreEntity> results = underTest.totalScoreLessThan(151);
        assertThat(results).containsExactly(testScoreAEntity, testScoreCEntity);
    }

}
