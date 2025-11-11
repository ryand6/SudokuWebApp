package com.github.ryand6.sudokuweb.repositories;

import com.github.ryand6.sudokuweb.TestDataUtil;
import com.github.ryand6.sudokuweb.domain.LobbyEntity;
import com.github.ryand6.sudokuweb.domain.LobbyPlayerEntity;
import com.github.ryand6.sudokuweb.domain.ScoreEntity;
import com.github.ryand6.sudokuweb.domain.UserEntity;
import com.github.ryand6.sudokuweb.integration.AbstractIntegrationTest;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static org.assertj.core.api.Assertions.assertThat;

public class LobbyRepositoryIntegrationTests extends AbstractIntegrationTest {

    private final LobbyRepository underTest;
    private final UserRepository userRepository;
    private final PlatformTransactionManager platformTransactionManager;

    @Autowired
    public LobbyRepositoryIntegrationTests(
            LobbyRepository underTest,
            UserRepository userRepository,
            PlatformTransactionManager platformTransactionManager) {
        this.underTest = underTest;
        this.userRepository = userRepository;
        this.platformTransactionManager = platformTransactionManager;
    }

//    @Test
//    public void testLobbyCreationAndRecall() {
//        ScoreEntity scoreEntityA = TestDataUtil.createTestScoreA();
//        UserEntity userEntityA = TestDataUtil.createTestUserA(scoreEntityA);
//        userRepository.save(userEntityA);
//        LobbyEntity lobbyEntity = TestDataUtil.createTestLobbyA(userEntityA);
//        underTest.save(lobbyEntity);
//        LobbyPlayerEntity lobbyPlayerEntity = TestDataUtil.createTestLobbyPlayer(lobbyEntity, userEntityA);
//        lobbyEntity.setLobbyPlayers(Set.of(lobbyPlayerEntity));
//        Optional<LobbyEntity> result = underTest.findById(lobbyEntity.getId());
//        assertThat(result).isPresent();
//
//        // Set the createdAt field using the retrieved lobby as this field is set on the lobbies creation in the db
//        lobbyEntity.setCreatedAt(result.get().getCreatedAt());
//        assertThat(result.get()).isEqualTo(lobbyEntity);
//    }
//
//    @Test
//    public void testMultipleLobbiesCreatedAndRecalled() {
//        ScoreEntity scoreEntityA = TestDataUtil.createTestScoreA();
//        ScoreEntity scoreEntityB = TestDataUtil.createTestScoreB();
//        ScoreEntity scoreEntityC = TestDataUtil.createTestScoreC();
//        UserEntity userEntityA = TestDataUtil.createTestUserA(scoreEntityA);
//        UserEntity userEntityB = TestDataUtil.createTestUserB(scoreEntityB);
//        UserEntity userEntityC = TestDataUtil.createTestUserC(scoreEntityC);
//        userRepository.save(userEntityA);
//        userRepository.save(userEntityB);
//        userRepository.save(userEntityC);
//        LobbyEntity lobbyEntityA = TestDataUtil.createTestLobbyA(userEntityA);
//        underTest.save(lobbyEntityA);
//        LobbyEntity lobbyEntityB = TestDataUtil.createTestLobbyB(userEntityB);
//        underTest.save(lobbyEntityB);
//        LobbyEntity lobbyEntityC = TestDataUtil.createTestLobbyC(userEntityC);
//        underTest.save(lobbyEntityC);
//
//        Iterable<LobbyEntity> result = underTest.findAll();
//        assertThat(result)
//                .hasSize(3)
//                .usingRecursiveComparison()
//                // Avoid lazy loaded fields when comparing
//                .ignoringFields("gameEntities", "lobbyPlayers")
//                .isEqualTo(List.of(lobbyEntityA, lobbyEntityB, lobbyEntityC));
//    }
//
//    @Test
//    public void testLobbyFullUpdate() {
//        ScoreEntity scoreEntityA = TestDataUtil.createTestScoreA();
//        UserEntity userEntityA = TestDataUtil.createTestUserA(scoreEntityA);
//        userRepository.save(userEntityA);
//        LobbyEntity lobbyEntityA = TestDataUtil.createTestLobbyA(userEntityA);
//        underTest.save(lobbyEntityA);
//        lobbyEntityA.setLobbyName("UPDATED");
//        underTest.save(lobbyEntityA);
//        Optional<LobbyEntity> result = underTest.findById(lobbyEntityA.getId());
//        assertThat(result).isPresent();
//        lobbyEntityA.setCreatedAt(result.get().getCreatedAt());
//        assertThat(result.get()).isEqualTo(lobbyEntityA);
//    }
//
//    @Test
//    public void testLobbyDeletion() {
//        ScoreEntity scoreEntityA = TestDataUtil.createTestScoreA();
//        UserEntity userEntityA = TestDataUtil.createTestUserA(scoreEntityA);
//        userRepository.save(userEntityA);
//        LobbyEntity lobbyEntityA = TestDataUtil.createTestLobbyA(userEntityA);
//        underTest.save(lobbyEntityA);
//        underTest.deleteById(lobbyEntityA.getId());
//        Optional<LobbyEntity> result = underTest.findById(lobbyEntityA.getId());
//        assertThat(result).isEmpty();
//    }

//    @Test
//    public void testExistsByJoinCode() {
//        ScoreEntity scoreEntityA = TestDataUtil.createTestScoreA();
//        UserEntity userEntityA = TestDataUtil.createTestUserA(scoreEntityA);
//        userRepository.save(userEntityA);
//        ScoreEntity scoreEntityB = TestDataUtil.createTestScoreB();
//        UserEntity userEntityB = TestDataUtil.createTestUserB(scoreEntityB);
//        userRepository.save(userEntityB);
//        LobbyEntity lobbyEntity = TestDataUtil.createTestLobbyB(userEntityB);
//        underTest.save(lobbyEntity);
//        assertThat(underTest.existsByJoinCode("aey3g-yagy3i3-u3ygu34")).isTrue();
//        assertThat(underTest.existsByJoinCode("uhsfs-sffhfhe-4tgdgdg")).isFalse();
//    }

//    @Test
//    public void testFindByIsPublicTrueAndIsActiveTrue() {
//        ScoreEntity scoreEntityA = TestDataUtil.createTestScoreA();
//        UserEntity userEntityA = TestDataUtil.createTestUserA(scoreEntityA);
//        userRepository.save(userEntityA);
//        LobbyEntity lobbyEntityA = TestDataUtil.createTestLobbyA(userEntityA);
//        underTest.save(lobbyEntityA);
//        ScoreEntity scoreEntityB = TestDataUtil.createTestScoreB();
//        UserEntity userEntityB = TestDataUtil.createTestUserB(scoreEntityB);
//        userRepository.save(userEntityB);
//        LobbyEntity lobbyEntityB = TestDataUtil.createTestLobbyA(userEntityB);
//        lobbyEntityB.setLobbyName("Test Lobby 2");
//        underTest.save(lobbyEntityB);
//        ScoreEntity scoreEntityC = TestDataUtil.createTestScoreC();
//        UserEntity userEntityC = TestDataUtil.createTestUserC(scoreEntityC);
//        userRepository.save(userEntityC);
//        LobbyEntity lobbyEntityC = TestDataUtil.createTestLobbyA(userEntityC);
//        lobbyEntityC.setLobbyName("Test Lobby 3");
//        underTest.save(lobbyEntityC);
//        // Want to return the last two created Lobbies
//        Pageable pageable = PageRequest.of(0, 2, Sort.by("createdAt").descending());
//        assertThat(underTest.findByIsPublicTrueAndIsActiveTrue(pageable)).containsExactly(lobbyEntityC, lobbyEntityB);
//    }
//
//    @Test
//    @Transactional
//    public void testFindByIdForUpdate() {
//        UserEntity host = userRepository.save(TestDataUtil.createTestUserA(TestDataUtil.createTestScoreA()));
//        LobbyEntity lobby = underTest.save(TestDataUtil.createTestLobbyA(host));
//
//        Optional<LobbyEntity> lockedLobbyOpt = underTest.findByIdForUpdate(lobby.getId());
//
//        assertThat(lockedLobbyOpt).isPresent();
//        assertThat(lockedLobbyOpt.get().getId()).isEqualTo(lobby.getId());
//    }
//
//    @Test
//    public void findByIdForUpdate_testPessimisticWriteLockBlocksOtherThread() throws Exception {
//        UserEntity host = userRepository.save(TestDataUtil.createTestUserA(TestDataUtil.createTestScoreA()));
//        LobbyEntity lobby = underTest.save(TestDataUtil.createTestLobbyA(host));
//
//        // Simulate two users by creating 2x threads
//        ExecutorService executor = Executors.newFixedThreadPool(2);
//
//        // Used to synchronize threads
//        // Ensures thread 2 doesn't start until thread 1 has acquired lock
//        CountDownLatch lockAcquired = new CountDownLatch(1);
//        // Waits until both threads are done before main test thread continues
//        CountDownLatch testFinished = new CountDownLatch(1);
//
//        // Using Spring transaction managers for JPA so that lock life cycle is appropriately attached to EntityManager (JPA)
//        TransactionTemplate tx1 = new TransactionTemplate(platformTransactionManager);
//        TransactionTemplate tx2 = new TransactionTemplate(platformTransactionManager);
//
//        // First thread: acquires the lock and holds it
//        executor.submit(() -> {
//            tx1.executeWithoutResult(status -> {
//                underTest.findByIdForUpdate(lobby.getId()); // lock row
//                lockAcquired.countDown(); // signal to thread 2 that lock is held
//                try {
//                    Thread.sleep(3000); // hold the lock for 3 seconds
//                } catch (InterruptedException ignored) {
//                    Thread.currentThread().interrupt();
//                }
//            });
//        });
//
//        // Second thread: tries to acquire the lock and times how long it takes
//        Future<Long> waitTimeFuture = executor.submit(() -> {
//            lockAcquired.await(); // wait until lock is acquired by first thread
//            long start = System.currentTimeMillis();
//            tx2.executeWithoutResult(status -> {
//                underTest.findByIdForUpdate(lobby.getId()); // should block until lock is released
//            });
//            long end = System.currentTimeMillis();
//            testFinished.countDown();
//            return end - start;
//        });
//
//        // Wait for second thread to finish and get duration
//        testFinished.await();
//        long waitTime = waitTimeFuture.get();
//        executor.shutdown();
//
//        // Expect at least 2.5 seconds delay, confirming it waited on the lock
//        assertThat(waitTime).isGreaterThanOrEqualTo(2500L);
//    }

}
