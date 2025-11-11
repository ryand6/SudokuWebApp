package com.github.ryand6.sudokuweb.repositories;

import com.github.ryand6.sudokuweb.TestDataUtil;
import com.github.ryand6.sudokuweb.domain.LobbyChatMessageEntity;
import com.github.ryand6.sudokuweb.domain.LobbyEntity;
import com.github.ryand6.sudokuweb.domain.ScoreEntity;
import com.github.ryand6.sudokuweb.domain.UserEntity;
import com.github.ryand6.sudokuweb.integration.AbstractIntegrationTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

public class LobbyChatMessageRepositoryIntegrationTests extends AbstractIntegrationTest {

    private final LobbyChatMessageRepository underTest;

    @Autowired
    public LobbyChatMessageRepositoryIntegrationTests(LobbyChatMessageRepository underTest) {
        this.underTest = underTest;
    }

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private LobbyRepository lobbyRepository;

    @Autowired
    private ScoreRepository scoreRepository;

    private UserEntity testUser;
    private LobbyEntity testLobby;

    @BeforeEach
    public void setUp() {
        // Create and persist base entities for message relations
        ScoreEntity scoreEntity = TestDataUtil.createTestScoreA();

        testUser = TestDataUtil.createTestUserA(scoreEntity);
        userRepository.save(testUser);

        testLobby = TestDataUtil.createTestLobbyA(testUser);
        lobbyRepository.save(testLobby);
    }

    @Test
    public void testCreateAndRetrieveChatMessage() {
        // Create a new chat message
        LobbyChatMessageEntity message = LobbyChatMessageEntity.builder()
                .lobbyEntity(testLobby)
                .userEntity(testUser)
                .message("Hello, world!")
                .build();

        underTest.save(message);

        Optional<LobbyChatMessageEntity> found = underTest.findById(message.getId());
        assertThat(found).isPresent();

        LobbyChatMessageEntity saved = found.get();
        assertThat(saved.getMessage()).isEqualTo("Hello, world!");
        assertThat(saved.getLobbyEntity().getId()).isEqualTo(testLobby.getId());
        assertThat(saved.getUserEntity().getId()).isEqualTo(testUser.getId());
    }

    @Test
    public void testFindByLobbyEntity_IdOrderByCreatedAtDesc() throws InterruptedException {
        // Create 3 messages with small delays to ensure descending creation order
        LobbyChatMessageEntity msg1 = LobbyChatMessageEntity.builder()
                .lobbyEntity(testLobby)
                .userEntity(testUser)
                .message("First message")
                .build();

        underTest.save(msg1);
        Thread.sleep(10);

        LobbyChatMessageEntity msg2 = LobbyChatMessageEntity.builder()
                .lobbyEntity(testLobby)
                .userEntity(testUser)
                .message("Second message")
                .build();

        underTest.save(msg2);
        Thread.sleep(10);

        LobbyChatMessageEntity msg3 = LobbyChatMessageEntity.builder()
                .lobbyEntity(testLobby)
                .userEntity(testUser)
                .message("Third message")
                .build();

        underTest.save(msg3);

        Pageable pageable = PageRequest.of(0, 10);
        Page<LobbyChatMessageEntity> resultPage =
                underTest.findByLobbyEntity_IdOrderByCreatedAtDesc(testLobby.getId(), pageable);

        List<LobbyChatMessageEntity> resultList = resultPage.getContent();

        // Expect 3 messages, newest first
        assertThat(resultList).hasSize(3);
        assertThat(resultList.get(0).getMessage()).isEqualTo("Third message");
        assertThat(resultList.get(1).getMessage()).isEqualTo("Second message");
        assertThat(resultList.get(2).getMessage()).isEqualTo("First message");
    }

    @Test
    public void testUpdateChatMessage() {
        LobbyChatMessageEntity message = LobbyChatMessageEntity.builder()
                .lobbyEntity(testLobby)
                .userEntity(testUser)
                .message("Original message")
                .build();

        underTest.save(message);

        message.setMessage("Updated message");
        underTest.save(message);

        Optional<LobbyChatMessageEntity> found = underTest.findById(message.getId());
        assertThat(found).isPresent();
        assertThat(found.get().getMessage()).isEqualTo("Updated message");
    }

    @Test
    public void testDeleteChatMessage() {
        LobbyChatMessageEntity message = LobbyChatMessageEntity.builder()
                .lobbyEntity(testLobby)
                .userEntity(testUser)
                .message("This will be deleted")
                .build();

        underTest.save(message);
        underTest.deleteById(message.getId());

        Optional<LobbyChatMessageEntity> found = underTest.findById(message.getId());
        assertThat(found).isEmpty();
    }

    @Test
    public void testFindAllChatMessages() {
        LobbyChatMessageEntity msgA = LobbyChatMessageEntity.builder()
                .lobbyEntity(testLobby)
                .userEntity(testUser)
                .message("Msg A")
                .build();

        LobbyChatMessageEntity msgB = LobbyChatMessageEntity.builder()
                .lobbyEntity(testLobby)
                .userEntity(testUser)
                .message("Msg B")
                .build();

        underTest.save(msgA);
        underTest.save(msgB);

        Iterable<LobbyChatMessageEntity> all = underTest.findAll();
        assertThat(all).hasSize(2)
                .extracting(LobbyChatMessageEntity::getMessage)
                .containsExactlyInAnyOrder("Msg A", "Msg B");
    }
}
