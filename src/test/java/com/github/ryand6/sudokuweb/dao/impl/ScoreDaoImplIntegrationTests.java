package com.github.ryand6.sudokuweb.dao.impl;

import com.github.ryand6.sudokuweb.TestDataUtil;
import com.github.ryand6.sudokuweb.domain.Score;
import com.github.ryand6.sudokuweb.domain.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ExtendWith(SpringExtension.class)
public class ScoreDaoImplIntegrationTests {

    private ScoreDaoImpl underTest;

    private UserDaoImpl supportTest;

    @Autowired
    public ScoreDaoImplIntegrationTests(ScoreDaoImpl underTest, UserDaoImpl supportTest) {
        this.underTest = underTest;
        this.supportTest = supportTest;
    }

    @Test
    public void testScoreCreationAndRecall() {
        // Create user object in the db because score relies on a user foreign key
        // DB updates aren't persistent so this is required
        User user = TestDataUtil.createTestUser();
        supportTest.create(user);
        // Checks for score creation and retrieval
        Score score = TestDataUtil.createTestScore();
        underTest.create(score);
        Optional<Score> result = underTest.findOne(score.getId());
        assertThat(result).isPresent();
        score.setCreatedAt(result.get().getCreatedAt());
        score.setUpdatedAt(result.get().getUpdatedAt());
        assertThat(result.get()).isEqualTo(score);
    }

}
