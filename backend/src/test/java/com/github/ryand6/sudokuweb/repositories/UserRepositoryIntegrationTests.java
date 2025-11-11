package com.github.ryand6.sudokuweb.repositories;

import com.github.ryand6.sudokuweb.TestDataUtil;
import com.github.ryand6.sudokuweb.domain.ScoreEntity;
import com.github.ryand6.sudokuweb.domain.UserEntity;
import com.github.ryand6.sudokuweb.integration.AbstractIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

public class UserRepositoryIntegrationTests extends AbstractIntegrationTest {

    private final UserRepository underTest;

    @Autowired
    public UserRepositoryIntegrationTests(UserRepository underTest) {
        this.underTest = underTest;
    }

    @Test
    public void testUserCreationAndRecall() {
        // Create score object in the db because user relies on a score foreign key
        // DB updates aren't persistent so this is required
        ScoreEntity scoreEntity = TestDataUtil.createTestScoreA();
        UserEntity userEntity = TestDataUtil.createTestUserA(scoreEntity);
        underTest.save(userEntity);
        Optional<UserEntity> result = underTest.findById(userEntity.getId());
        assertThat(result).isPresent();
        // Set the createdAt field using the retrieved user as this field is set on creation in the db
        userEntity.setCreatedAt(result.get().getCreatedAt());
        assertThat(result.get()).isEqualTo(userEntity);
    }

    @Test
    public void testMultipleUsersCreatedAndRecalled() {
        ScoreEntity scoreEntityA = TestDataUtil.createTestScoreA();
        ScoreEntity scoreEntityB = TestDataUtil.createTestScoreB();
        ScoreEntity scoreEntityC = TestDataUtil.createTestScoreC();
        UserEntity userEntityA = TestDataUtil.createTestUserA(scoreEntityA);
        underTest.save(userEntityA);
        UserEntity userEntityB = TestDataUtil.createTestUserB(scoreEntityB);
        underTest.save(userEntityB);
        UserEntity userEntityC = TestDataUtil.createTestUserC(scoreEntityC);
        underTest.save(userEntityC);

        Iterable<UserEntity> result = underTest.findAll();
        assertThat(result)
                .hasSize(3)
                .usingRecursiveFieldByFieldElementComparatorIgnoringFields("createdAt")
                .containsExactly(userEntityA, userEntityB, userEntityC);
    }

    @Test
    public void testUserFullUpdate() {
        ScoreEntity scoreEntityA = TestDataUtil.createTestScoreA();
        UserEntity userEntityA = TestDataUtil.createTestUserA(scoreEntityA);
        underTest.save(userEntityA);
        userEntityA.setUsername("UPDATED");
        underTest.save(userEntityA);
        Optional<UserEntity> result = underTest.findById(userEntityA.getId());
        assertThat(result).isPresent();
        userEntityA.setCreatedAt(result.get().getCreatedAt());
        assertThat(result.get()).isEqualTo(userEntityA);
    }

    @Test
    public void testUserDeletion() {
        ScoreEntity scoreEntityA = TestDataUtil.createTestScoreA();
        UserEntity userEntityA = TestDataUtil.createTestUserA(scoreEntityA);
        underTest.save(userEntityA);
        underTest.deleteById(userEntityA.getId());
        Optional<UserEntity> result = underTest.findById(userEntityA.getId());
        assertThat(result).isEmpty();
    }

    @Test
    public void testFindByProviderAndProviderId() {
        ScoreEntity scoreEntityA = TestDataUtil.createTestScoreA();
        UserEntity user = TestDataUtil.createTestUserA(scoreEntityA);
        underTest.save(user);

        String provider = "google";
        String providerId = "a4ceE42GHa";

        Optional<UserEntity> found = underTest.findByProviderAndProviderId(provider, providerId);

        assertThat(found).isPresent();
        assertThat(found.get().getUsername()).isEqualTo("Henry");
    }

    @Test
    public void testExistsByUsername() {
        ScoreEntity scoreEntityA = TestDataUtil.createTestScoreA();
        UserEntity user = TestDataUtil.createTestUserA(scoreEntityA);
        underTest.save(user);

        boolean exists = underTest.existsByUsername("Henry");
        boolean notExists = underTest.existsByUsername("nonexistentUser");

        assertThat(exists).isTrue();
        assertThat(notExists).isFalse();
    }

    @Test
    public void test_findByOrderByScoreEntity_TotalScoreDesc() {
        ScoreEntity scoreEntityA = TestDataUtil.createTestScoreA();
        UserEntity userA = TestDataUtil.createTestUserA(scoreEntityA);

        ScoreEntity scoreEntityB = TestDataUtil.createTestScoreB();
        UserEntity userB = TestDataUtil.createTestUserB(scoreEntityB);

        ScoreEntity scoreEntityC = TestDataUtil.createTestScoreC();
        UserEntity userC = TestDataUtil.createTestUserC(scoreEntityC);

        underTest.save(userA);
        underTest.save(userB);
        underTest.save(userC);

        Pageable pageable = PageRequest.of(0, 2);
        Page<UserEntity> top2Pages = underTest.findByOrderByScoreEntity_TotalScoreDesc(pageable);

        List<UserEntity> top2PagesList = top2Pages.stream().toList();
        assertThat(top2PagesList.get(0).getUsername()).isEqualTo("dk0ng");
        assertThat(top2PagesList.get(1).getUsername()).isEqualTo("Henry");
    }

    @Test
    public void testGetUserRankById() {
        ScoreEntity scoreEntityA = TestDataUtil.createTestScoreA();
        UserEntity userA = TestDataUtil.createTestUserA(scoreEntityA);

        ScoreEntity scoreEntityB = TestDataUtil.createTestScoreB();
        UserEntity userB = TestDataUtil.createTestUserB(scoreEntityB);

        ScoreEntity scoreEntityC = TestDataUtil.createTestScoreC();
        UserEntity userC = TestDataUtil.createTestUserC(scoreEntityC);

        underTest.save(userA);
        underTest.save(userB);
        underTest.save(userC);

        Long rank = underTest.getUserRankById(userA.getId());
        assertThat(rank).isEqualTo(2L);

        rank = underTest.getUserRankById(userB.getId());
        assertThat(rank).isEqualTo(1L);

        rank = underTest.getUserRankById(userC.getId());
        assertThat(rank).isEqualTo(3L);
    }

}
