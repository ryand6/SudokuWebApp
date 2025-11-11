package com.github.ryand6.sudokuweb.repositories;

import com.github.ryand6.sudokuweb.TestDataUtil;
import com.github.ryand6.sudokuweb.domain.ScoreEntity;
import com.github.ryand6.sudokuweb.integration.AbstractIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

public class ScoreRepositoryIntegrationTests extends AbstractIntegrationTest {

    private final ScoreRepository underTest;

    @Autowired
    public ScoreRepositoryIntegrationTests(ScoreRepository underTest) {
        this.underTest = underTest;
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
