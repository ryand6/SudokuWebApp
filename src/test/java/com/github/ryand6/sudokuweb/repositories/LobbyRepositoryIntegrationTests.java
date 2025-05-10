package com.github.ryand6.sudokuweb.repositories;

import com.github.ryand6.sudokuweb.TestDataUtil;
import com.github.ryand6.sudokuweb.domain.Lobby;
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
        jdbcTemplate.execute("DELETE FROM lobby_state");
        jdbcTemplate.execute("DELETE FROM users");
        jdbcTemplate.execute("DELETE FROM scores");
        jdbcTemplate.execute("DELETE FROM sudoku_puzzles");
        jdbcTemplate.execute("DELETE FROM lobbies");
    }

    @Test
    public void testLobbyCreationAndRecall() {
        Score scoreA = TestDataUtil.createTestScoreA();
        User userA = TestDataUtil.createTestUserA(scoreA);
        userRepository.save(userA);
        Lobby lobby = TestDataUtil.createTestLobbyA(userA);
        underTest.save(lobby);
        Optional<Lobby> result = underTest.findById(lobby.getId());
        assertThat(result).isPresent();

        // Set the createdAt field using the retrieved lobby as this field is set on the lobbies creation in the db
        lobby.setCreatedAt(result.get().getCreatedAt());
        assertThat(result.get()).isEqualTo(lobby);
    }

    @Test
    public void testMultipleLobbiesCreatedAndRecalled() {
        Score scoreA = TestDataUtil.createTestScoreA();
        Score scoreB = TestDataUtil.createTestScoreB();
        Score scoreC = TestDataUtil.createTestScoreC();
        User userA = TestDataUtil.createTestUserA(scoreA);
        User userB = TestDataUtil.createTestUserB(scoreB);
        User userC = TestDataUtil.createTestUserC(scoreC);
        userRepository.save(userA);
        userRepository.save(userB);
        userRepository.save(userC);
        Lobby lobbyA = TestDataUtil.createTestLobbyA(userA);
        underTest.save(lobbyA);
        Lobby lobbyB = TestDataUtil.createTestLobbyB(userA, userB);
        underTest.save(lobbyB);
        Lobby lobbyC = TestDataUtil.createTestLobbyC(userA, userB, userC);
        underTest.save(lobbyC);

        Iterable<Lobby> result = underTest.findAll();
        assertThat(result)
                .hasSize(3)
                .usingRecursiveComparison()
                // Avoid lazy loaded fields when comparing
                .ignoringFields("lobbyStates")
                .isEqualTo(List.of(lobbyA, lobbyB, lobbyC));
    }

    @Test
    public void testLobbyFullUpdate() {
        Score scoreA = TestDataUtil.createTestScoreA();
        User userA = TestDataUtil.createTestUserA(scoreA);
        userRepository.save(userA);
        Lobby lobbyA = TestDataUtil.createTestLobbyA(userA);
        underTest.save(lobbyA);
        lobbyA.setLobbyName("UPDATED");
        underTest.save(lobbyA);
        Optional<Lobby> result = underTest.findById(lobbyA.getId());
        assertThat(result).isPresent();
        lobbyA.setCreatedAt(result.get().getCreatedAt());
        assertThat(result.get()).isEqualTo(lobbyA);
    }

    @Test
    public void testLobbyDeletion() {
        Score scoreA = TestDataUtil.createTestScoreA();
        User userA = TestDataUtil.createTestUserA(scoreA);
        userRepository.save(userA);
        Lobby lobbyA = TestDataUtil.createTestLobbyA(userA);
        underTest.save(lobbyA);
        underTest.deleteById(lobbyA.getId());
        Optional<Lobby> result = underTest.findById(lobbyA.getId());
        assertThat(result).isEmpty();
    }

}
