package com.github.ryand6.sudokuweb.repositories;

import com.github.ryand6.sudokuweb.TestDataUtil;
import com.github.ryand6.sudokuweb.domain.Score;
import com.github.ryand6.sudokuweb.domain.User;
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
public class UserRepositoryIntegrationTests {

    private final UserRepository underTest;

    @Autowired
    public UserRepositoryIntegrationTests(UserRepository underTest) {
        this.underTest = underTest;
    }

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    public void setUp() {
        // Correct SQL syntax for deleting all rows from the tables
        jdbcTemplate.execute("DELETE FROM lobby_users");
        jdbcTemplate.execute("DELETE FROM lobby_state");
        jdbcTemplate.execute("DELETE FROM users");
        jdbcTemplate.execute("DELETE FROM scores");
        jdbcTemplate.execute("DELETE FROM sudoku_puzzles");
        jdbcTemplate.execute("DELETE FROM lobbies");
    }

    @Test
    public void testUserCreationAndRecall() {
        // Create score object in the db because user relies on a score foreign key
        // DB updates aren't persistent so this is required
        Score score = TestDataUtil.createTestScoreA();
        User user = TestDataUtil.createTestUserA(score);
        underTest.save(user);
        Optional<User> result = underTest.findById(user.getId());
        assertThat(result).isPresent();
        // Set the createdAt field using the retrieved user as this field is set on creation in the db
        user.setCreatedAt(result.get().getCreatedAt());
        assertThat(result.get()).isEqualTo(user);
    }

    @Test
    public void testMultipleUsersCreatedAndRecalled() {
        Score scoreA = TestDataUtil.createTestScoreA();
        Score scoreB = TestDataUtil.createTestScoreB();
        Score scoreC = TestDataUtil.createTestScoreC();
        User userA = TestDataUtil.createTestUserA(scoreA);
        underTest.save(userA);
        User userB = TestDataUtil.createTestUserB(scoreB);
        underTest.save(userB);
        User userC = TestDataUtil.createTestUserC(scoreC);
        underTest.save(userC);

        Iterable<User> result = underTest.findAll();
        assertThat(result)
                .hasSize(3)
                .usingRecursiveFieldByFieldElementComparatorIgnoringFields("createdAt")
                .containsExactly(userA, userB, userC);
    }

    @Test
    public void testUserFullUpdate() {
        Score scoreA = TestDataUtil.createTestScoreA();
        User userA = TestDataUtil.createTestUserA(scoreA);
        underTest.save(userA);
        userA.setUsername("UPDATED");
        underTest.save(userA);
        Optional<User> result = underTest.findById(userA.getId());
        assertThat(result).isPresent();
        userA.setCreatedAt(result.get().getCreatedAt());
        assertThat(result.get()).isEqualTo(userA);
    }

    @Test
    public void testUserDeletion() {
        Score scoreA = TestDataUtil.createTestScoreA();
        User userA = TestDataUtil.createTestUserA(scoreA);
        underTest.save(userA);
        underTest.deleteById(userA.getId());
        Optional<User> result = underTest.findById(userA.getId());
        assertThat(result).isEmpty();
    }

}
