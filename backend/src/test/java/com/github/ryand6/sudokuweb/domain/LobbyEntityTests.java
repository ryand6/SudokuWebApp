package com.github.ryand6.sudokuweb.domain;

import com.github.ryand6.sudokuweb.TestDataUtil;
import com.github.ryand6.sudokuweb.enums.LobbyStatus;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.HashSet;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
public class LobbyEntityTests {

    private final Clock fixedClock = Clock.fixed(Instant.parse("2026-01-31T12:00:00Z"), ZoneOffset.UTC);

    @Test
    void determineNextHost_returnsNextHostBasedOnJoinedAtField() {
        // Set up users
        UserEntity hostUser = TestDataUtil.createTestUserA(new ScoreEntity());
        hostUser.setId(1L);
        UserEntity nextHostUser = TestDataUtil.createTestUserB(new ScoreEntity());
        nextHostUser.setId(2L);
        UserEntity lastJoinedUser = TestDataUtil.createTestUserC(new ScoreEntity());
        lastJoinedUser.setId(3L);

        // Set up lobby
        HashSet<LobbyPlayerEntity> lobbyPlayers = new HashSet<>();
        LobbyEntity lobby = TestDataUtil.createTestLobbyA(hostUser, lobbyPlayers);
        lobby.setId(1L);

        // Set up lobby players
        LobbyPlayerEntity hostPlayer = TestDataUtil.createTestLobbyPlayer(lobby, hostUser);
        LobbyPlayerEntity nextHostPlayer = TestDataUtil.createTestLobbyPlayer(lobby, nextHostUser);
        LobbyPlayerEntity lastJoinedPlayer = TestDataUtil.createTestLobbyPlayer(lobby, lastJoinedUser);

        // Set joinedAt values for each player
        hostPlayer.setJoinedAt(fixedClock.instant());
        nextHostPlayer.setJoinedAt(fixedClock.instant().plusSeconds(10));
        lastJoinedPlayer.setJoinedAt(fixedClock.instant().plusSeconds(20));

        // Add players to lobby
        lobbyPlayers.add(hostPlayer);
        lobbyPlayers.add(nextHostPlayer);
        lobbyPlayers.add(lastJoinedPlayer);
        lobby.setLobbyPlayers(lobbyPlayers);

        Optional<UserEntity> nextHost = lobby.determineNextHost();

        Assertions.assertTrue(nextHost.isPresent());
        Assertions.assertEquals(nextHost.get(), nextHostUser);

    }

    @Test
    void evaluateCountdownState_returnsEmptyOptionalWhenPlayerCountLessThanTwo() {
        // Set up user
        UserEntity hostUser = TestDataUtil.createTestUserA(new ScoreEntity());
        hostUser.setId(1L);

        // Set up lobby
        HashSet<LobbyPlayerEntity> lobbyPlayers = new HashSet<>();
        LobbyEntity lobby = TestDataUtil.createTestLobbyA(hostUser, lobbyPlayers);
        lobby.setId(1L);

        // Set up lobby player
        LobbyPlayerEntity hostPlayer = TestDataUtil.createTestLobbyPlayer(lobby, hostUser);

        // Add player to lobby
        lobbyPlayers.add(hostPlayer);
        lobby.setLobbyPlayers(lobbyPlayers);

        Optional<Long> initiatorId = lobby.getLobbyCountdownEntity().evaluateCountdownState();

        Assertions.assertFalse(initiatorId.isPresent());
        Assertions.assertFalse(lobby.getLobbyCountdownEntity().isCountdownActive());
        Assertions.assertNull(lobby.getLobbyCountdownEntity().getCountdownInitiatedBy());
    }

    @Test
    void evaluateCountdownState_returnsEmptyOptionalWhenNeitherHostNorMajorityAreReady() {
        // Set up users
        UserEntity hostUser = TestDataUtil.createTestUserA(new ScoreEntity());
        hostUser.setId(1L);
        UserEntity nextHostUser = TestDataUtil.createTestUserB(new ScoreEntity());
        nextHostUser.setId(2L);
        UserEntity lastJoinedUser = TestDataUtil.createTestUserC(new ScoreEntity());
        lastJoinedUser.setId(3L);

        // Set up lobby
        HashSet<LobbyPlayerEntity> lobbyPlayers = new HashSet<>();
        LobbyEntity lobby = TestDataUtil.createTestLobbyA(hostUser, lobbyPlayers);
        lobby.setId(1L);

        // Set up lobby players
        LobbyPlayerEntity hostPlayer = TestDataUtil.createTestLobbyPlayer(lobby, hostUser);
        LobbyPlayerEntity nextHostPlayer = TestDataUtil.createTestLobbyPlayer(lobby, nextHostUser);
        LobbyPlayerEntity lastJoinedPlayer = TestDataUtil.createTestLobbyPlayer(lobby, lastJoinedUser);
        // Only one user (not host) ready
        lastJoinedPlayer.setLobbyStatus(LobbyStatus.READY);

        // Add players to lobby
        lobbyPlayers.add(hostPlayer);
        lobbyPlayers.add(nextHostPlayer);
        lobbyPlayers.add(lastJoinedPlayer);
        lobby.setLobbyPlayers(lobbyPlayers);

        Optional<Long> initiatorId = lobby.getLobbyCountdownEntity().evaluateCountdownState();

        Assertions.assertFalse(initiatorId.isPresent());
        Assertions.assertFalse(lobby.getLobbyCountdownEntity().isCountdownActive());
        Assertions.assertNull(lobby.getLobbyCountdownEntity().getCountdownInitiatedBy());
    }

    @Test
    void evaluateCountdownState_returnsIdOfLastUserToReady_whenMajorityReadyExceptForHost() {
        // Set up users
        UserEntity hostUser = TestDataUtil.createTestUserA(new ScoreEntity());
        hostUser.setId(1L);
        UserEntity nextHostUser = TestDataUtil.createTestUserB(new ScoreEntity());
        nextHostUser.setId(2L);
        UserEntity lastJoinedUser = TestDataUtil.createTestUserC(new ScoreEntity());
        lastJoinedUser.setId(3L);

        // Set up lobby
        HashSet<LobbyPlayerEntity> lobbyPlayers = new HashSet<>();
        LobbyEntity lobby = TestDataUtil.createTestLobbyA(hostUser, lobbyPlayers);
        lobby.setId(1L);

        // Set up lobby players
        LobbyPlayerEntity hostPlayer = TestDataUtil.createTestLobbyPlayer(lobby, hostUser);
        LobbyPlayerEntity nextHostPlayer = TestDataUtil.createTestLobbyPlayer(lobby, nextHostUser);
        LobbyPlayerEntity lastJoinedPlayer = TestDataUtil.createTestLobbyPlayer(lobby, lastJoinedUser);
        // Majority ready (not host)
        nextHostPlayer.setLobbyStatus(LobbyStatus.READY);
        lastJoinedPlayer.setLobbyStatus(LobbyStatus.READY);

        // Set readyAt times
        nextHostPlayer.setReadyAt(fixedClock.instant());
        lastJoinedPlayer.setReadyAt(fixedClock.instant().plusSeconds(10)); // Last user to ready

        // Add players to lobby
        lobbyPlayers.add(hostPlayer);
        lobbyPlayers.add(nextHostPlayer);
        lobbyPlayers.add(lastJoinedPlayer);
        lobby.setLobbyPlayers(lobbyPlayers);

        Optional<Long> initiatorId = lobby.getLobbyCountdownEntity().evaluateCountdownState();

        Assertions.assertTrue(initiatorId.isPresent());
        Assertions.assertEquals(initiatorId.get(), lastJoinedPlayer.getUser().getId());
        Assertions.assertEquals(lobby.getLobbyCountdownEntity().getCountdownInitiatedBy(), lastJoinedPlayer.getUser().getId());
        Assertions.assertTrue(lobby.getLobbyCountdownEntity().isCountdownActive());
    }

    @Test
    void evaluateCountdownState_returnsHostIdWhenOnly() {
        // Set up users
        UserEntity hostUser = TestDataUtil.createTestUserA(new ScoreEntity());
        hostUser.setId(1L);
        UserEntity nextHostUser = TestDataUtil.createTestUserB(new ScoreEntity());
        nextHostUser.setId(2L);
        UserEntity lastJoinedUser = TestDataUtil.createTestUserC(new ScoreEntity());
        lastJoinedUser.setId(3L);

        // Set up lobby
        HashSet<LobbyPlayerEntity> lobbyPlayers = new HashSet<>();
        LobbyEntity lobby = TestDataUtil.createTestLobbyA(hostUser, lobbyPlayers);
        lobby.setId(1L);

        // Set up lobby players
        LobbyPlayerEntity hostPlayer = TestDataUtil.createTestLobbyPlayer(lobby, hostUser);
        LobbyPlayerEntity nextHostPlayer = TestDataUtil.createTestLobbyPlayer(lobby, nextHostUser);
        LobbyPlayerEntity lastJoinedPlayer = TestDataUtil.createTestLobbyPlayer(lobby, lastJoinedUser);
        // Only host ready
        hostPlayer.setLobbyStatus(LobbyStatus.READY);

        // Add players to lobby
        lobbyPlayers.add(hostPlayer);
        lobbyPlayers.add(nextHostPlayer);
        lobbyPlayers.add(lastJoinedPlayer);
        lobby.setLobbyPlayers(lobbyPlayers);

        Optional<Long> initiatorId = lobby.getLobbyCountdownEntity().evaluateCountdownState();

        Assertions.assertTrue(initiatorId.isPresent());
        Assertions.assertEquals(initiatorId.get(), hostPlayer.getUser().getId());
        Assertions.assertEquals(lobby.getLobbyCountdownEntity().getCountdownInitiatedBy(), hostPlayer.getUser().getId());
        Assertions.assertTrue(lobby.getLobbyCountdownEntity().isCountdownActive());
    }

    @Test
    void evaluateCountdownState_setsCountdownEndsAtBasedOnNonReadyPlayerCount() {
        // Set up Users
        UserEntity hostUser = TestDataUtil.createTestUserA(new ScoreEntity());
        hostUser.setId(1L);
        UserEntity user2 = TestDataUtil.createTestUserB(new ScoreEntity());
        user2.setId(2L);
        UserEntity user3 = TestDataUtil.createTestUserC(new ScoreEntity());
        user3.setId(3L);

        // Set up Lobby
        HashSet<LobbyPlayerEntity> lobbyPlayers = new HashSet<>();
        LobbyEntity lobby = TestDataUtil.createTestLobbyA(hostUser, lobbyPlayers);
        lobby.setId(1L);

        LobbyCountdownEntity lobbyCountdown = lobby.getLobbyCountdownEntity();
        lobbyCountdown.setClock(fixedClock);
        lobby.setLobbyCountdownEntity(lobbyCountdown);

        // Set up Players
        LobbyPlayerEntity hostPlayer = TestDataUtil.createTestLobbyPlayer(lobby, hostUser);
        LobbyPlayerEntity player2 = TestDataUtil.createTestLobbyPlayer(lobby, user2);
        LobbyPlayerEntity player3 = TestDataUtil.createTestLobbyPlayer(lobby, user3);

        // Host ready, therefore countdown initiates
        hostPlayer.setLobbyStatus(LobbyStatus.READY);

        lobbyPlayers.add(hostPlayer);
        lobbyPlayers.add(player2);
        lobbyPlayers.add(player3);
        lobby.setLobbyPlayers(lobbyPlayers);

        lobby.getLobbyCountdownEntity().evaluateCountdownState();

        // 2 players not ready therefore +2 minutes
        Instant expectedEnd =
                fixedClock.instant().plus(Duration.ofMinutes(2));

        assertThat(lobby.getLobbyCountdownEntity().getCountdownEndsAt()).isEqualTo(expectedEnd);
    }

}
