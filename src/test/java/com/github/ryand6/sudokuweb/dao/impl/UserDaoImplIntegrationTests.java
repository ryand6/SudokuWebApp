package com.github.ryand6.sudokuweb.dao.impl;

import com.github.ryand6.sudokuweb.TestDataUtil;
import com.github.ryand6.sudokuweb.domain.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class UserDaoImplIntegrationTests {

    private UserDaoImpl underTest;

    @Autowired
    public UserDaoImplIntegrationTests(UserDaoImpl underTest) {
        this.underTest = underTest;
    }

    @Test
    public void testUserCreationAndRecall() {
        User user = TestDataUtil.createTestUserA();
        underTest.create(user);
        Optional<User> result = underTest.findOne(user.getId());
        assertThat(result).isPresent();
        // Set the createdAt field using the retrieved user as this field is set on creation in the db
        user.setCreatedAt(result.get().getCreatedAt());
        assertThat(result.get()).isEqualTo(user);
    }

    @Test
    public void testMultipleUsersCreatedAndRecalled() {
        User userA = TestDataUtil.createTestUserA();
        underTest.create(userA);
        User userB = TestDataUtil.createTestUserB();
        underTest.create(userB);
        User userC = TestDataUtil.createTestUserC();
        underTest.create(userC);

        List<User> result = underTest.find();
        userA.setCreatedAt(result.get(0).getCreatedAt());
        userB.setCreatedAt(result.get(1).getCreatedAt());
        userC.setCreatedAt(result.get(2).getCreatedAt());
        assertThat(result)
                .hasSize(3)
                .containsExactly(userA, userB, userC);
    }

    @Test
    public void testUserFullUpdate() {
        User userA = TestDataUtil.createTestUserA();
        underTest.create(userA);
        userA.setUsername("UPDATED");
        underTest.update(userA.getId(), userA);
        Optional<User> result = underTest.findOne(userA.getId());
        assertThat(result).isPresent();
        userA.setCreatedAt(result.get().getCreatedAt());
        assertThat(result.get()).isEqualTo(userA);
    }

}
