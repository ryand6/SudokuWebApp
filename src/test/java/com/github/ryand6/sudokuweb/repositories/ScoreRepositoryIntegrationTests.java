package com.github.ryand6.sudokuweb.repositories;

import com.github.ryand6.sudokuweb.TestDataUtil;
import com.github.ryand6.sudokuweb.domain.Score;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class ScoreRepositoryIntegrationTests {

    private ScoreRepository underTest;

    @Autowired
    public ScoreRepositoryIntegrationTests(ScoreRepository underTest) {
        this.underTest = underTest;
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

//    @Test
//    public void testMultipleScoresCreatedAndRecalled() {
//        User userA = TestDataUtil.createTestUserA();
//        userDao.create(userA);
//        User userB = TestDataUtil.createTestUserB();
//        userDao.create(userB);
//        User userC = TestDataUtil.createTestUserC();
//        userDao.create(userC);
//
//        Score scoreA = TestDataUtil.createTestScoreA();
//        underTest.create(scoreA);
//        Score scoreB = TestDataUtil.createTestScoreB();
//        underTest.create(scoreB);
//        Score scoreC = TestDataUtil.createTestScoreC();
//        underTest.create(scoreC);
//
//        List<Score> result = underTest.find();
//        scoreA.setCreatedAt(result.get(0).getCreatedAt());
//        scoreA.setUpdatedAt(result.get(0).getUpdatedAt());
//        scoreB.setCreatedAt(result.get(1).getCreatedAt());
//        scoreB.setUpdatedAt(result.get(1).getUpdatedAt());
//        scoreC.setCreatedAt(result.get(2).getCreatedAt());
//        scoreC.setUpdatedAt(result.get(2).getUpdatedAt());
//        assertThat(result)
//                .hasSize(3)
//                .containsExactly(scoreA, scoreB, scoreC);
//    }
//
//    @Test
//    public void testScoreFullUpdate() {
//        User user = TestDataUtil.createTestUserA();
//        userDao.create(user);
//        Score scoreA = TestDataUtil.createTestScoreA();
//        underTest.create(scoreA);
//        scoreA.setTotalScore(1000);
//        underTest.update(scoreA.getId(), scoreA);
//        Optional<Score> result = underTest.findOne(scoreA.getId());
//        assertThat(result).isPresent();
//        scoreA.setCreatedAt(result.get().getCreatedAt());
//        scoreA.setUpdatedAt(result.get().getUpdatedAt());
//        assertThat(result.get()).isEqualTo(scoreA);
//    }
//
//    @Test
//    public void testScoreDeletion() {
//        User user = TestDataUtil.createTestUserA();
//        userDao.create(user);
//        Score scoreA = TestDataUtil.createTestScoreA();
//        underTest.create(scoreA);
//        underTest.delete(scoreA.getId());
//        Optional<Score> result = underTest.findOne(scoreA.getId());
//        assertThat(result).isEmpty();
//    }

}
