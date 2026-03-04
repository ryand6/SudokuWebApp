package com.github.ryand6.sudokuweb.domain.score;

import com.github.ryand6.sudokuweb.TestDataUtil;
import com.github.ryand6.sudokuweb.domain.user.stats.UserStatsEntity;
import com.github.ryand6.sudokuweb.domain.user.stats.UserStatsRepository;
import com.github.ryand6.sudokuweb.integration.AbstractIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

public class UserStatsRepositoryIntegrationTests extends AbstractIntegrationTest {

    private final UserStatsRepository underTest;

    @Autowired
    public UserStatsRepositoryIntegrationTests(UserStatsRepository underTest) {
        this.underTest = underTest;
    }

    @Test
    public void testScoreCreationAndRecall() {
        // Checks for score creation and retrieval
        UserStatsEntity userStatsEntity = TestDataUtil.createTestScoreA();
        underTest.save(userStatsEntity);
        Optional<UserStatsEntity> result = underTest.findById(userStatsEntity.getId());
        assertThat(result).isPresent();
        userStatsEntity.setUpdatedAt(result.get().getUpdatedAt());
        assertThat(result.get()).isEqualTo(userStatsEntity);
    }

    @Test
    public void testMultipleScoresCreatedAndRecalled() {
        UserStatsEntity userStatsEntityA = TestDataUtil.createTestScoreA();
        underTest.save(userStatsEntityA);
        UserStatsEntity userStatsEntityB = TestDataUtil.createTestScoreB();
        underTest.save(userStatsEntityB);
        UserStatsEntity userStatsEntityC = TestDataUtil.createTestScoreC();
        underTest.save(userStatsEntityC);

        Iterable<UserStatsEntity> result = underTest.findAll();
        assertThat(result)
                .hasSize(3)
                .usingRecursiveFieldByFieldElementComparatorIgnoringFields("updatedAt")
                .containsExactly(userStatsEntityA, userStatsEntityB, userStatsEntityC);
    }

    @Test
    public void testScoreFullUpdate() {
        UserStatsEntity userStatsEntityA = TestDataUtil.createTestScoreA();
        underTest.save(userStatsEntityA);
        userStatsEntityA.setTotalScore(1000);
        underTest.save(userStatsEntityA);
        Optional<UserStatsEntity> result = underTest.findById(userStatsEntityA.getId());
        assertThat(result).isPresent();
        userStatsEntityA.setUpdatedAt(result.get().getUpdatedAt());
        assertThat(result.get()).isEqualTo(userStatsEntityA);
    }

    @Test
    public void testScoreDeletion() {
        UserStatsEntity userStatsEntityA = TestDataUtil.createTestScoreA();
        underTest.save(userStatsEntityA);
        underTest.deleteById(userStatsEntityA.getId());
        Optional<UserStatsEntity> result = underTest.findById(userStatsEntityA.getId());
        assertThat(result).isEmpty();
    }

    @Test
    public void testGetScoreWithTotalScoreLessThan(){
        UserStatsEntity testScoreAEntity = TestDataUtil.createTestScoreA();
        underTest.save(testScoreAEntity);
        UserStatsEntity testScoreBEntity = TestDataUtil.createTestScoreB();
        underTest.save(testScoreBEntity);
        UserStatsEntity testScoreCEntity = TestDataUtil.createTestScoreC();
        underTest.save(testScoreCEntity);

        Iterable<UserStatsEntity> results = underTest.totalScoreLessThan(151);
        assertThat(results).containsExactly(testScoreAEntity, testScoreCEntity);
    }

}
