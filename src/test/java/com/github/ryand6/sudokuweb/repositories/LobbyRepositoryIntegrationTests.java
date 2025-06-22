package com.github.ryand6.sudokuweb.repositories;

import com.github.ryand6.sudokuweb.TestDataUtil;
import com.github.ryand6.sudokuweb.domain.LobbyEntity;
import com.github.ryand6.sudokuweb.domain.ScoreEntity;
import com.github.ryand6.sudokuweb.domain.UserEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class LobbyRepositoryIntegrationTests {

    private final LobbyRepository underTest;
    private final UserRepository userRepository;
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public LobbyRepositoryIntegrationTests(
            LobbyRepository underTest,
            UserRepository userRepository,
            JdbcTemplate jdbcTemplate
        ) {
        this.underTest = underTest;
        this.userRepository = userRepository;
        this.jdbcTemplate = jdbcTemplate;
    }

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
    public void testLobbyCreationAndRecall() {
        ScoreEntity scoreEntityA = TestDataUtil.createTestScoreA();
        UserEntity userEntityA = TestDataUtil.createTestUserA(scoreEntityA);
        userRepository.save(userEntityA);
        LobbyEntity lobbyEntity = TestDataUtil.createTestLobbyA(userEntityA);
        underTest.save(lobbyEntity);
        Optional<LobbyEntity> result = underTest.findById(lobbyEntity.getId());
        assertThat(result).isPresent();

        // Set the createdAt field using the retrieved lobby as this field is set on the lobbies creation in the db
        lobbyEntity.setCreatedAt(result.get().getCreatedAt());
        assertThat(result.get()).isEqualTo(lobbyEntity);
    }

    @Test
    public void testMultipleLobbiesCreatedAndRecalled() {
        ScoreEntity scoreEntityA = TestDataUtil.createTestScoreA();
        ScoreEntity scoreEntityB = TestDataUtil.createTestScoreB();
        ScoreEntity scoreEntityC = TestDataUtil.createTestScoreC();
        UserEntity userEntityA = TestDataUtil.createTestUserA(scoreEntityA);
        UserEntity userEntityB = TestDataUtil.createTestUserB(scoreEntityB);
        UserEntity userEntityC = TestDataUtil.createTestUserC(scoreEntityC);
        userRepository.save(userEntityA);
        userRepository.save(userEntityB);
        userRepository.save(userEntityC);
        LobbyEntity lobbyEntityA = TestDataUtil.createTestLobbyA(userEntityA);
        underTest.save(lobbyEntityA);
        LobbyEntity lobbyEntityB = TestDataUtil.createTestLobbyB(userEntityA, userEntityB);
        underTest.save(lobbyEntityB);
        LobbyEntity lobbyEntityC = TestDataUtil.createTestLobbyC(userEntityA, userEntityB, userEntityC);
        underTest.save(lobbyEntityC);

        Iterable<LobbyEntity> result = underTest.findAll();
        assertThat(result)
                .hasSize(3)
                .usingRecursiveComparison()
                // Avoid lazy loaded fields when comparing
                .ignoringFields("gameEntities")
                .isEqualTo(List.of(lobbyEntityA, lobbyEntityB, lobbyEntityC));
    }

    @Test
    public void testLobbyFullUpdate() {
        ScoreEntity scoreEntityA = TestDataUtil.createTestScoreA();
        UserEntity userEntityA = TestDataUtil.createTestUserA(scoreEntityA);
        userRepository.save(userEntityA);
        LobbyEntity lobbyEntityA = TestDataUtil.createTestLobbyA(userEntityA);
        underTest.save(lobbyEntityA);
        lobbyEntityA.setLobbyName("UPDATED");
        underTest.save(lobbyEntityA);
        Optional<LobbyEntity> result = underTest.findById(lobbyEntityA.getId());
        assertThat(result).isPresent();
        lobbyEntityA.setCreatedAt(result.get().getCreatedAt());
        assertThat(result.get()).isEqualTo(lobbyEntityA);
    }

    @Test
    public void testLobbyDeletion() {
        ScoreEntity scoreEntityA = TestDataUtil.createTestScoreA();
        UserEntity userEntityA = TestDataUtil.createTestUserA(scoreEntityA);
        userRepository.save(userEntityA);
        LobbyEntity lobbyEntityA = TestDataUtil.createTestLobbyA(userEntityA);
        underTest.save(lobbyEntityA);
        underTest.deleteById(lobbyEntityA.getId());
        Optional<LobbyEntity> result = underTest.findById(lobbyEntityA.getId());
        assertThat(result).isEmpty();
    }

    @Test
    public void testExistsByJoinCode() {
        ScoreEntity scoreEntityA = TestDataUtil.createTestScoreA();
        UserEntity userEntityA = TestDataUtil.createTestUserA(scoreEntityA);
        userRepository.save(userEntityA);
        ScoreEntity scoreEntityB = TestDataUtil.createTestScoreB();
        UserEntity userEntityB = TestDataUtil.createTestUserB(scoreEntityB);
        userRepository.save(userEntityB);
        LobbyEntity lobbyEntity = TestDataUtil.createTestLobbyB(userEntityA, userEntityB);
        underTest.save(lobbyEntity);
        assertThat(underTest.existsByJoinCode("aey3g-yagy3i3-u3ygu34")).isTrue();
        assertThat(underTest.existsByJoinCode("uhsfs-sffhfhe-4tgdgdg")).isFalse();
    }

    @Test
    public void testFindByIsPublicTrueAndIsActiveTrue() {
        ScoreEntity scoreEntityA = TestDataUtil.createTestScoreA();
        UserEntity userEntityA = TestDataUtil.createTestUserA(scoreEntityA);
        userRepository.save(userEntityA);
        LobbyEntity lobbyEntityA = TestDataUtil.createTestLobbyA(userEntityA);
        underTest.save(lobbyEntityA);
        ScoreEntity scoreEntityB = TestDataUtil.createTestScoreB();
        UserEntity userEntityB = TestDataUtil.createTestUserB(scoreEntityB);
        userRepository.save(userEntityB);
        LobbyEntity lobbyEntityB = TestDataUtil.createTestLobbyA(userEntityB);
        lobbyEntityB.setLobbyName("Test Lobby 2");
        underTest.save(lobbyEntityB);
        ScoreEntity scoreEntityC = TestDataUtil.createTestScoreC();
        UserEntity userEntityC = TestDataUtil.createTestUserC(scoreEntityC);
        userRepository.save(userEntityC);
        LobbyEntity lobbyEntityC = TestDataUtil.createTestLobbyA(userEntityC);
        lobbyEntityC.setLobbyName("Test Lobby 3");
        underTest.save(lobbyEntityC);
        // Want to return the last two created Lobbies
        Pageable pageable = PageRequest.of(0, 2, Sort.by("createdAt").descending());
        assertThat(underTest.findByIsPublicTrueAndIsActiveTrue(pageable)).containsExactly(lobbyEntityC, lobbyEntityB);
    }

}
