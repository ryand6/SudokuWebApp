package com.github.ryand6.sudokuweb.domain.user;

import com.github.ryand6.sudokuweb.helpers.TestDataUtil;
import com.github.ryand6.sudokuweb.integration.AbstractIntegrationTest;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class UserRepositoryIntegrationTests extends AbstractIntegrationTest {

    private final UserRepository underTest;

    @Autowired
    public UserRepositoryIntegrationTests(UserRepository underTest) {
        this.underTest = underTest;
    }

    @Test
    @Order(1)
    public void testUserCreationAndRecall() {
        // Create score object in the db because user relies on a score foreign key
        UserEntity userEntity = TestDataUtil.createTestUserA();
        underTest.save(userEntity);
        Optional<UserEntity> result = underTest.findById(userEntity.getId());
        assertThat(result).isPresent();
        // Set the createdAt field using the retrieved user as this field is set on creation in the db
        userEntity.setCreatedAt(result.get().getCreatedAt());
        assertThat(result.get()).isEqualTo(userEntity);
    }

    @Test
    @Order(2)
    public void testStartsWithEmptyDatabase() {
        assertThat(underTest.count()).isZero();
    }

//    @Test
//    public void testMultipleUsersCreatedAndRecalled() {
//
//        UserEntity userEntityA = TestDataUtil.createTestUserA();
//        underTest.save(userEntityA);
//        UserEntity userEntityB = TestDataUtil.createTestUserB();
//        underTest.save(userEntityB);
//        UserEntity userEntityC = TestDataUtil.createTestUserC();
//        underTest.save(userEntityC);
//
//        Iterable<UserEntity> result = underTest.findAll();
//        assertThat(result)
//                .hasSize(3)
//                .usingRecursiveFieldByFieldElementComparatorIgnoringFields("createdAt")
//                .containsExactly(userEntityA, userEntityB, userEntityC);
//    }
//
//    @Test
//    public void testUserFullUpdate() {
//        UserEntity userEntityA = TestDataUtil.createTestUserA();
//        underTest.save(userEntityA);
//        userEntityA.setUsername("UPDATED");
//        underTest.save(userEntityA);
//        Optional<UserEntity> result = underTest.findById(userEntityA.getId());
//        assertThat(result).isPresent();
//        userEntityA.setCreatedAt(result.get().getCreatedAt());
//        assertThat(result.get()).isEqualTo(userEntityA);
//    }

    @Test
    public void testUserDeletion() {
        UserEntity userEntityA = TestDataUtil.createTestUserA();
        underTest.save(userEntityA);
        underTest.deleteById(userEntityA.getId());
        Optional<UserEntity> result = underTest.findById(userEntityA.getId());
        assertThat(result).isEmpty();
    }

//    @Test
//    public void testFindByProviderAndProviderId() {
//        UserEntity user = TestDataUtil.createTestUserA();
//        underTest.save(user);
//
//        String provider = "google";
//        String providerId = "a4ceE42GHa";
//
//        Optional<UserEntity> found = underTest.findByProviderAndProviderId(provider, providerId);
//
//        assertThat(found).isPresent();
//        assertThat(found.get().getUsername()).isEqualTo("Henry");
//    }

    @Test
    public void testExistsByUsername() {
        UserEntity user = TestDataUtil.createTestUserA();
        underTest.save(user);

        boolean exists = underTest.existsByUsername("Henry");
        boolean notExists = underTest.existsByUsername("nonexistentUser");

        assertThat(exists).isTrue();
        assertThat(notExists).isFalse();
    }

}
