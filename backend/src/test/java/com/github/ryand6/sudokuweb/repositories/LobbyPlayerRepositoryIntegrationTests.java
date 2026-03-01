package com.github.ryand6.sudokuweb.repositories;

import com.github.ryand6.sudokuweb.integration.AbstractIntegrationTest;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;

public class LobbyPlayerRepositoryIntegrationTests extends AbstractIntegrationTest {

    private final LobbyPlayerRepository underTest;
    private final LobbyRepository lobbyRepository;
    private final UserRepository userRepository;

    @Autowired
    public LobbyPlayerRepositoryIntegrationTests(
            LobbyPlayerRepository underTest,
            LobbyRepository lobbyRepository,
            UserRepository userRepository
    ) {
        this.underTest = underTest;
        this.lobbyRepository = lobbyRepository;
        this.userRepository = userRepository;
    }

//    @Test
//    public void testLobbyPlayerCreationAndRecall() {
//        ScoreEntity score = TestDataUtil.createTestScoreA();
//        UserEntity user = TestDataUtil.createTestUserA(score);
//        userRepository.save(user);
//
//        LobbyEntity lobby = TestDataUtil.createTestLobbyA(user);
//        lobbyRepository.save(lobby);
//
//        LobbyPlayerEntity lobbyPlayer = TestDataUtil.createTestLobbyPlayer(lobby, user);
//        underTest.save(lobbyPlayer);
//
//        Optional<LobbyPlayerEntity> result = underTest.findByCompositeId(lobbyPlayer.getId().getLobbyId(), lobbyPlayer.getId().getUserId());
//        assertThat(result).isPresent();
//        assertThat(result.get().getLobby().getId()).isEqualTo(lobby.getId());
//        assertThat(result.get().getUser().getId()).isEqualTo(user.getId());
//        assertThat(result.get().getJoinedAt()).isNotNull();
//    }
//
//    @Test
//    public void testLobbyPlayerDeletion() {
//        ScoreEntity score = TestDataUtil.createTestScoreA();
//        UserEntity user = TestDataUtil.createTestUserA(score);
//        userRepository.save(user);
//
//        LobbyEntity lobby = TestDataUtil.createTestLobbyA(user);
//        lobbyRepository.save(lobby);
//
//        LobbyPlayerEntity lobbyPlayer = TestDataUtil.createTestLobbyPlayer(lobby, user);
//        underTest.save(lobbyPlayer);
//
//        underTest.deleteByCompositeId(lobbyPlayer.getId().getLobbyId(), lobbyPlayer.getId().getUserId());
//        Optional<LobbyPlayerEntity> result = underTest.findByCompositeId(lobbyPlayer.getId().getLobbyId(), lobbyPlayer.getId().getUserId());
//        assertThat(result).isEmpty();
//    }
//
//    @Test
//    public void testMultipleLobbyPlayersInLobby() {
//        ScoreEntity scoreA = TestDataUtil.createTestScoreA();
//        ScoreEntity scoreB = TestDataUtil.createTestScoreB();
//        UserEntity userA = TestDataUtil.createTestUserA(scoreA);
//        UserEntity userB = TestDataUtil.createTestUserB(scoreB);
//        userRepository.save(userA);
//        userRepository.save(userB);
//
//        LobbyEntity lobby = TestDataUtil.createTestLobbyA(userA);
//        lobbyRepository.save(lobby);
//
//        LobbyPlayerEntity playerA = TestDataUtil.createTestLobbyPlayer(lobby, userA);
//        LobbyPlayerEntity playerB = TestDataUtil.createTestLobbyPlayer(lobby, userB);
//
//        underTest.save(playerA);
//        underTest.save(playerB);
//
//        assertThat(underTest.findAll()).hasSize(2);
//    }
}

