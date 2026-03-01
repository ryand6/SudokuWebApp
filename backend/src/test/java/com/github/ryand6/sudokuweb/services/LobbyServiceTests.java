package com.github.ryand6.sudokuweb.services;

import com.github.ryand6.sudokuweb.domain.game.LobbyPlayerFactory;
import com.github.ryand6.sudokuweb.domain.lobby.LobbyCountdownEntity;
import com.github.ryand6.sudokuweb.domain.lobby.LobbyEntity;
import com.github.ryand6.sudokuweb.domain.lobby.LobbyPlayerEntity;
import com.github.ryand6.sudokuweb.domain.lobby.LobbyPlayerId;
import com.github.ryand6.sudokuweb.domain.user.UserEntity;
import com.github.ryand6.sudokuweb.dto.entity.LobbyChatMessageDto;
import com.github.ryand6.sudokuweb.dto.entity.LobbyDto;
import com.github.ryand6.sudokuweb.dto.entity.LobbyPlayerDto;
import com.github.ryand6.sudokuweb.exceptions.lobby.LobbyNotFoundException;
import com.github.ryand6.sudokuweb.exceptions.lobby.player.LobbyPlayerNotFoundException;
import com.github.ryand6.sudokuweb.exceptions.lobby.UserExistsInActiveLobbyException;
import com.github.ryand6.sudokuweb.mappers.Impl.LobbyEntityDtoMapper;
import com.github.ryand6.sudokuweb.repositories.LobbyPlayerRepository;
import com.github.ryand6.sudokuweb.repositories.LobbyRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class LobbyServiceTests {

    @Mock
    private LobbyRepository lobbyRepository;

    @Mock
    private UserService userService;

    @Mock
    private LobbyEntityDtoMapper lobbyEntityDtoMapper;

    @Mock
    private PrivateLobbyTokenService privateLobbyTokenService;

    @Mock
    private LobbyChatService lobbyChatService;

    @Mock
    private LobbyWebSocketsService lobbyWebSocketsService;

    @Mock
    private SimpMessagingTemplate simpMessagingTemplate;

    @Mock
    private LobbyPlayerRepository lobbyPlayerRepository;

    @Spy
    @InjectMocks
    private LobbyService lobbyService;

    @Test
    public void createNewLobby_successfulCreation() {

        when(lobbyRepository.findFirstByIsActiveTrueAndLobbyPlayers_User_Id(anyLong())).thenReturn(Optional.empty());

        UserEntity user = new UserEntity();
        user.setId(1L);
        user.setUsername("Test User");
        when(userService.findUserById(anyLong())).thenReturn(user);

        LobbyEntity lobby = new LobbyEntity();
        lobby.setId(1L);
        lobby.setLobbyName("Test Lobby");

        LobbyPlayerEntity lobbyPlayerEntity = new LobbyPlayerEntity();
        lobbyPlayerEntity.setLobby(lobby);
        lobbyPlayerEntity.setUser(user);

        LobbyPlayerDto lobbyPlayerDto = new LobbyPlayerDto();
        lobbyPlayerDto.setId(new LobbyPlayerId(lobby.getId(), user.getId()));

        LobbyDto lobbyDto = new LobbyDto();
        lobbyDto.setId(1L);
        lobbyDto.setLobbyName("Test Lobby");
        lobbyDto.setPublic(true);
        lobbyDto.setLobbyPlayers(Set.of(lobbyPlayerDto));
        when(lobbyEntityDtoMapper.mapToDto(any(LobbyEntity.class))).thenReturn(lobbyDto);

        String lobbyName = "Test Lobby";
        boolean isPublic = true;
        Long requesterId = 1L;
        LobbyDto lobbyDtoTest = lobbyService.createNewLobby(lobbyName, isPublic, requesterId);
        assertThat(lobbyDtoTest.getId()).isEqualTo(1L);
        assertThat(lobbyDtoTest.getLobbyName()).isEqualTo("Test Lobby");
        assertThat(lobbyDtoTest.isPublic()).isEqualTo(true);
        assertThat(lobbyDtoTest.getLobbyPlayers()).hasSize(1);
    }

    @Test
    public void getPublicLobbies_testExpectedReturnList() {
        int page = 0;
        int size = 2;

        LobbyEntity lobby1 = new LobbyEntity();
        lobby1.setId(1L);
        LobbyEntity lobby2 = new LobbyEntity();
        lobby2.setId(2L);

        Page<LobbyEntity> lobbyPage = new PageImpl<>(List.of(lobby1, lobby2));

        when(lobbyRepository.findByIsActiveTrueAndLobbySettingsEntity_IsPublicTrue(PageRequest.of(page, size, Sort.by("createdAt").descending())))
                .thenReturn(lobbyPage);

        LobbyDto dto1 = new LobbyDto();
        dto1.setId(1L);
        LobbyDto dto2 = new LobbyDto();
        dto2.setId(2L);

        when(lobbyEntityDtoMapper.mapToDto(lobby1)).thenReturn(dto1);
        when(lobbyEntityDtoMapper.mapToDto(lobby2)).thenReturn(dto2);

        List<LobbyDto> result = lobbyService.getPublicLobbies(page, size);

        assertThat(result).hasSize(2);
        assertThat(result).containsExactly(dto1, dto2);
    }

    @Test
    public void joinLobby_throwsLobbyNotFoundException() {
        Long lobbyId = 1L;
        Long userId = 1L;

        LobbyNotFoundException ex = assertThrows(LobbyNotFoundException.class, () -> {
            lobbyService.joinLobby(userId, lobbyId);
        });
        Assertions.assertEquals("Lobby with ID 1 does not exist", ex.getMessage());
    }

    @Test
    void joinLobby_throwsUserExistsInActiveLobbyException_whenUserAlreadyMemberOfLobby() {
        Long lobbyId = 1L;
        Long userId = 1L;

        LobbyEntity lobby = new LobbyEntity();
        lobby.setId(1L);

        when(lobbyRepository.findFirstByIsActiveTrueAndLobbyPlayers_User_Id(userId)).thenReturn(Optional.of(lobby));

        UserExistsInActiveLobbyException ex = assertThrows(UserExistsInActiveLobbyException.class, () -> {
            lobbyService.joinLobby(userId, lobbyId);
        });
        Assertions.assertEquals("You are currently a player in an active lobby with ID 1. Players can only be in one active lobby at a time.", ex.getMessage());
    }

    @Test
    void joinLobby_publicLobby_returnsLobbyDto() {
        Long lobbyId = 1L;
        Long userId = 1L;

        LobbyEntity lobby = mock(LobbyEntity.class);
        when(lobby.isActive()).thenReturn(true);

        Set<LobbyPlayerEntity> players = new HashSet<>();
        for (int i = 0; i < 3; i++) {
            players.add(mock(LobbyPlayerEntity.class));
        }
        when(lobby.getLobbyPlayers()).thenReturn(players);

        when(lobbyRepository.findById(lobbyId)).thenReturn(Optional.of(lobby));

        UserEntity requester = mock(UserEntity.class);
        when(userService.findUserById(any())).thenReturn(requester);

        LobbyPlayerEntity lobbyPlayer = mock(LobbyPlayerEntity.class);
        LobbyDto lobbyDto = new LobbyDto();
        lobbyDto.setId(1L);

        when(lobbyEntityDtoMapper.mapToDto(any())).thenReturn(lobbyDto);

        // Used for mocking static methods - using try-with-resources
        try (MockedStatic<LobbyPlayerFactory> factoryMock = Mockito.mockStatic(LobbyPlayerFactory.class)) {
            factoryMock.when(() -> LobbyPlayerFactory.createLobbyPlayer(any(), any()))
                    .thenReturn(lobbyPlayer);

            LobbyDto returnVal = lobbyService.joinLobby(userId, lobbyId);
            assertThat(returnVal).isEqualTo(lobbyDto);
        }
    }

    @Test
    void joinLobby_privateLobby_returnsLobbyDto_whenCorrectJoinCode() {
        Long lobbyId = 1L;
        Long userId = 1L;
        String token = "abc123";

        when(privateLobbyTokenService.joinPrivateLobbyWithToken(token)).thenReturn(lobbyId);

        LobbyEntity lobby = mock(LobbyEntity.class);
        when(lobbyRepository.findById(lobbyId)).thenReturn(Optional.of(lobby));
        when(lobby.isActive()).thenReturn(true);

        Set<LobbyPlayerEntity> players = new HashSet<>();
        for (int i = 0; i < 2; i++) players.add(mock(LobbyPlayerEntity.class));
        when(lobby.getLobbyPlayers()).thenReturn(players);

        UserEntity requester = mock(UserEntity.class);
        when(userService.findUserById(userId)).thenReturn(requester);

        LobbyPlayerEntity lobbyPlayer = mock(LobbyPlayerEntity.class);
        LobbyDto lobbyDto = new LobbyDto();
        lobbyDto.setId(lobbyId);
        when(lobbyEntityDtoMapper.mapToDto(any())).thenReturn(lobbyDto);

        try (MockedStatic<LobbyPlayerFactory> factoryMock = Mockito.mockStatic(LobbyPlayerFactory.class)) {
            factoryMock.when(() -> LobbyPlayerFactory.createLobbyPlayer(any(), any()))
                    .thenReturn(lobbyPlayer);

            LobbyDto result = lobbyService.joinLobby(userId, token);
            assertThat(result).isEqualTo(lobbyDto);
        }
    }

    @Test
    public void removeFromLobby_throwsLobbyNotFoundException() {
        Long lobbyId = 1L;
        Long userId = 1L;

        when(lobbyRepository.findById(lobbyId)).thenReturn(Optional.empty());

        LobbyNotFoundException ex = assertThrows(LobbyNotFoundException.class, () -> {
            lobbyService.removeFromLobby(lobbyId, userId);
        });
        Assertions.assertEquals("Lobby with ID 1 does not exist", ex.getMessage());
    }

    @Test
    public void findLobbyPlayer_throwsLobbyPlayerNotFoundException() {
        LobbyEntity lobby = new LobbyEntity();
        lobby.setId(1L);

        LobbyPlayerEntity lobbyPlayerEntity = new LobbyPlayerEntity();

        LobbyPlayerId lobbyPlayerId = new LobbyPlayerId();
        lobbyPlayerId.setLobbyId(1L);
        lobbyPlayerId.setUserId(1L);

        lobbyPlayerEntity.setId(lobbyPlayerId);

        UserEntity playerEntity = new UserEntity();
        playerEntity.setId(1L);

        lobbyPlayerEntity.setUser(playerEntity);

        Set<LobbyPlayerEntity> lobbyPlayers = new HashSet<>();
        lobbyPlayers.add(lobbyPlayerEntity);

        lobby.setLobbyPlayers(lobbyPlayers);

        Long nonPlayerId = 2L;

        LobbyPlayerNotFoundException ex = assertThrows(LobbyPlayerNotFoundException.class, () -> {
            lobbyService.findLobbyPlayer(lobby, nonPlayerId);
        });
        Assertions.assertEquals("Lobby Player with Lobby ID 1 and User ID 2 does not exist", ex.getMessage());
    }

    @Test
    void removePlayerFromLobby_returnsNull_whenLobbyNoLongerActive() {
        Long userId = 1L;
        Long lobbyId = 100L;

        LobbyEntity lobby = mock(LobbyEntity.class);

        UserEntity playerLeaving = new UserEntity();
        playerLeaving.setId(userId);

        LobbyPlayerEntity lobbyPlayer = mock(LobbyPlayerEntity.class);

        Set<LobbyPlayerEntity> currentPlayers = new HashSet<>();
        currentPlayers.add(lobbyPlayer);

        when(lobby.getLobbyPlayers()).thenReturn(currentPlayers);
        when(lobbyPlayer.getUser()).thenReturn(playerLeaving);

        when(lobby.getHost()).thenReturn(playerLeaving);

        when(lobbyRepository.findById(lobbyId)).thenReturn(Optional.of(lobby));
        when(userService.findUserById(userId)).thenReturn(playerLeaving);
        when(lobby.determineNextHost()).thenReturn(Optional.empty());

        doAnswer(invocation -> {
            LobbyEntity lobbyInvocation = invocation.getArgument(0);
            lobbyInvocation.setActive(false);
            return null;
        }).when(lobbyService).closeLobby(any(LobbyEntity.class));

        LobbyDto result = lobbyService.removeFromLobby(lobbyId, userId);

        assertThat(result).isEqualTo(null);
    }

    @Test
    void removePlayerFromLobby_successfullyRemovesPlayer() {
        Long lobbyId = 100L;
        Long playerLeavingId = 1L;
        Long hostId = 2L;

        LobbyEntity lobby = mock(LobbyEntity.class);

        LobbyCountdownEntity lobbyCountdown = mock(LobbyCountdownEntity.class);

        UserEntity userLeaving = new UserEntity();
        userLeaving.setId(playerLeavingId);

        UserEntity host = new UserEntity();
        host.setId(hostId);

        LobbyPlayerEntity playerLeaving = new LobbyPlayerEntity();
        playerLeaving.setUser(userLeaving);

        LobbyPlayerEntity hostPlayer = new LobbyPlayerEntity();
        hostPlayer.setUser(host);

        Set<LobbyPlayerEntity> currentPlayers = new HashSet<>();
        currentPlayers.add(playerLeaving);
        currentPlayers.add(hostPlayer);

        when(lobbyRepository.findById(lobbyId)).thenReturn(Optional.of(lobby));

        when(lobby.getLobbyPlayers()).thenReturn(currentPlayers);

        when(lobby.getLobbyCountdownEntity()).thenReturn(lobbyCountdown);

        when(lobby.getLobbyCountdownEntity().evaluateCountdownState()).thenReturn(Optional.empty());

        when(userService.findUserById(playerLeavingId)).thenReturn(userLeaving);

        when(lobby.getHost()).thenReturn(host);

        when(lobbyChatService.submitInfoMessage(any(Long.class), any(Long.class), any(String.class))).thenReturn(new LobbyChatMessageDto());

        doNothing().when(lobbyWebSocketsService).handleLobbyChatMessage(any(LobbyChatMessageDto.class), any(SimpMessagingTemplate.class));

        when(lobbyEntityDtoMapper.mapToDto(any(LobbyEntity.class))).thenReturn(new LobbyDto());

        lobbyService.removeFromLobby(lobbyId, playerLeavingId);

        Assertions.assertFalse(lobby.getLobbyPlayers().contains(playerLeaving));
        Assertions.assertTrue(lobby.getLobbyPlayers().contains(hostPlayer));
    }

    @Test
    void removePlayerFromLobby_returnsLobbyDto_whenPlayerRemoved() {
        Long lobbyId = 100L;
        Long playerLeavingId = 1L;
        Long hostId = 2L;

        LobbyEntity lobby = mock(LobbyEntity.class);

        LobbyCountdownEntity lobbyCountdown = mock(LobbyCountdownEntity.class);

        UserEntity userLeaving = new UserEntity();
        userLeaving.setId(playerLeavingId);

        UserEntity host = new UserEntity();
        host.setId(hostId);

        LobbyPlayerEntity playerLeaving = new LobbyPlayerEntity();
        playerLeaving.setUser(userLeaving);

        LobbyPlayerEntity hostPlayer = new LobbyPlayerEntity();
        hostPlayer.setUser(host);

        Set<LobbyPlayerEntity> currentPlayers = new HashSet<>();
        currentPlayers.add(playerLeaving);
        currentPlayers.add(hostPlayer);

        when(lobbyRepository.findById(lobbyId)).thenReturn(Optional.of(lobby));

        when(lobby.getLobbyPlayers()).thenReturn(currentPlayers);

        when(lobby.getLobbyCountdownEntity()).thenReturn(lobbyCountdown);

        when(lobby.getLobbyCountdownEntity().evaluateCountdownState()).thenReturn(Optional.empty());

        when(userService.findUserById(playerLeavingId)).thenReturn(userLeaving);

        when(lobby.getHost()).thenReturn(host);

        when(lobbyChatService.submitInfoMessage(any(Long.class), any(Long.class), any(String.class))).thenReturn(new LobbyChatMessageDto());

        doNothing().when(lobbyWebSocketsService).handleLobbyChatMessage(any(LobbyChatMessageDto.class), any(SimpMessagingTemplate.class));

        when(lobbyEntityDtoMapper.mapToDto(any(LobbyEntity.class))).thenReturn(new LobbyDto());

        LobbyDto lobbyDto = lobbyService.removeFromLobby(lobbyId, playerLeavingId);

        assertThat(lobbyDto).isNotNull();
    }

    @Test
    void closeLobby_verifyMethodCalls() {
        doNothing().when(lobbyRepository).deleteById(any(Long.class));

        LobbyEntity lobby = mock(LobbyEntity.class);
        when(lobby.getId()).thenReturn(1L);

        lobbyService.closeLobby(lobby);

        verify(lobby).setActive(false);
        verify(lobbyRepository).deleteById(1L);
    }

    @Test
    void closeLobby_setsIsActiveToFalse() {
        doNothing().when(lobbyRepository).deleteById(any(Long.class));

        LobbyEntity lobby = new LobbyEntity();
        lobby.setId(1L);
        lobby.setActive(true);

        lobbyService.closeLobby(lobby);

        Assertions.assertFalse(lobby.isActive());
    }

    @Test
    void getLobbyById_returnsNull_whenMapToDtoReturnsNull() {
        LobbyEntity lobby = mock(LobbyEntity.class);

        when(lobbyRepository.findById(any(Long.class))).thenReturn(Optional.of(lobby));
        when(lobbyEntityDtoMapper.mapToDto(any(LobbyEntity.class))).thenReturn(null);

        LobbyDto result = lobbyService.getLobbyDtoById(1L);

        assertThat(result).isEqualTo(null);
    }

    @Test
    void getLobbyId_returnsLobbyDto_whenFoundAndMapped() {
        LobbyEntity lobby = mock(LobbyEntity.class);

        when(lobbyRepository.findById(any(Long.class))).thenReturn(Optional.of(lobby));

        LobbyDto lobbyDto = new LobbyDto();

        when(lobbyEntityDtoMapper.mapToDto(any(LobbyEntity.class))).thenReturn(lobbyDto);

        LobbyDto result = lobbyService.getLobbyDtoById(1L);

        assertThat(result).isEqualTo(lobbyDto);
    }

    @Test
    void getLobbyId_throwsLobbyNotFoundException_whenLobbyNotFound() {
        LobbyEntity lobby = mock(LobbyEntity.class);

        when(lobbyRepository.findById(any(Long.class))).thenReturn(Optional.empty());

        LobbyNotFoundException ex = assertThrows(LobbyNotFoundException.class, () -> {
            lobbyService.getLobbyDtoById(1L);
        });

        Assertions.assertEquals("Lobby with ID 1 does not exist", ex.getMessage());
    }

//
//    @Test
//    void removePlayerFromLobby_reassignsHost_whenHostLeavesAndNewHostExists() {
//        Long userId = 1L;
//        Long lobbyId = 100L;
//
//        LobbyEntity lobby = mock(LobbyEntity.class);
//        UserEntity host = mock(UserEntity.class);
//        UserEntity newHost = mock(UserEntity.class);
//        LobbyPlayerEntity lobbyPlayer = mock(LobbyPlayerEntity.class);
//
//        LobbyDto expectedDto = new LobbyDto();
//        expectedDto.setId(lobbyId);
//
//        // Setup basic behavior
//        when(lobbyRepository.findByIdForUpdate(lobbyId)).thenReturn(Optional.of(lobby));
//        when(lobbyPlayerRepository.findByCompositeId(lobbyId, userId)).thenReturn(Optional.of(lobbyPlayer));
//        when(userService.findUserById(userId)).thenReturn(host);
//        when(userService.findUserById(userId)).thenReturn(host);
//        when(lobby.getHost()).thenReturn(host);
//
//        // Assume players exist, new host will be found
//        Set<LobbyPlayerEntity> players = new HashSet<>();
//        players.add(lobbyPlayer);
//        when(lobby.getLobbyPlayers()).thenReturn(players);
//
//        // partial mock to override getNewHost
//        LobbyService spyService = Mockito.spy(lobbyService);
//        doReturn(Optional.of(newHost)).when(spyService).getNewHost(lobby);
//
//        when(lobbyEntityDtoMapper.mapToDto(lobby)).thenReturn(expectedDto);
//
//        LobbyDto result = spyService.removeFromLobby(userId, lobbyId);
//
//        assertThat(result).isEqualTo(expectedDto);
//
//        verify(lobby).setHost(newHost);
//        verify(lobbyPlayerRepository).deleteByCompositeId(lobbyId, userId);
//        verify(lobbyEntityDtoMapper).mapToDto(lobby);
//    }
//
//    @Test
//    void removePlayerFromLobby_closesLobby_whenHostLeavesAndNoNewHost() {
//        Long userId = 1L;
//        Long lobbyId = 100L;
//
//        LobbyEntity lobbyBeforeClosing = mock(LobbyEntity.class);
//        LobbyEntity closedLobby = mock(LobbyEntity.class); // Result of closeLobby
//        UserEntity host = mock(UserEntity.class);
//        LobbyPlayerEntity lobbyPlayer = mock(LobbyPlayerEntity.class);
//
//        LobbyDto expectedDto = new LobbyDto();
//        expectedDto.setId(lobbyId);
//
//        // Setup repository and entity mocks
//        when(lobbyRepository.findByIdForUpdate(lobbyId)).thenReturn(Optional.of(lobbyBeforeClosing));
//        when(lobbyPlayerRepository.findByCompositeId(lobbyId, userId)).thenReturn(Optional.of(lobbyPlayer));
//        when(userService.findUserById(userId)).thenReturn(host);
//        when(userService.findUserById(userId)).thenReturn(host);
//        when(lobbyBeforeClosing.getHost()).thenReturn(host);
//
//        Set<LobbyPlayerEntity> players = new HashSet<>();
//        players.add(lobbyPlayer);
//        when(lobbyBeforeClosing.getLobbyPlayers()).thenReturn(players);
//
//        // partial mock of service to override getNewHost and closeLobby
//        LobbyService spyService = Mockito.spy(lobbyService);
//        doReturn(Optional.empty()).when(spyService).getNewHost(lobbyBeforeClosing); // No replacement host
//        doReturn(closedLobby).when(spyService).closeLobby(lobbyBeforeClosing);     // Return closed lobby
//
//        when(lobbyEntityDtoMapper.mapToDto(closedLobby)).thenReturn(expectedDto);
//
//        LobbyDto result = spyService.removeFromLobby(userId, lobbyId);
//
//        assertThat(result).isEqualTo(expectedDto);
//        verify(spyService).closeLobby(lobbyBeforeClosing);
//        verify(lobbyPlayerRepository).deleteByCompositeId(lobbyId, userId);
//        verify(lobbyEntityDtoMapper).mapToDto(closedLobby);
//    }
//
//    @Test
//    void getNewHost_returnsEmpty_whenOnlyHostPresent() {
//        LobbyEntity lobby = mock(LobbyEntity.class);
//        UserEntity host = mock(UserEntity.class);
//        LobbyPlayerEntity hostPlayer = mock(LobbyPlayerEntity.class);
//
//        when(lobby.getHost()).thenReturn(host);
//        when(lobby.getLobbyPlayers()).thenReturn(Set.of(hostPlayer));
//        when(hostPlayer.getUser()).thenReturn(host);
//
//        Optional<UserEntity> result = lobbyService.getNewHost(lobby);
//
//        assertThat(result).isEmpty();
//    }
//
//    @Test
//    void getNewHost_returnsOtherPlayer_whenHostAndOneOtherPlayerPresent() {
//        LobbyEntity lobby = mock(LobbyEntity.class);
//        UserEntity host = mock(UserEntity.class);
//        UserEntity newHost = mock(UserEntity.class);
//
//        LobbyPlayerEntity hostPlayer = mock(LobbyPlayerEntity.class);
//        LobbyPlayerEntity otherPlayer = mock(LobbyPlayerEntity.class);
//
//        when(lobby.getHost()).thenReturn(host);
//        when(lobby.getLobbyPlayers()).thenReturn(Set.of(hostPlayer, otherPlayer));
//        when(hostPlayer.getUser()).thenReturn(host);
//        when(otherPlayer.getUser()).thenReturn(newHost);
//        when(otherPlayer.getJoinedAt()).thenReturn(Instant.now());
//
//        Optional<UserEntity> result = lobbyService.getNewHost(lobby);
//
//        assertThat(result).contains(newHost);
//    }
//
//    @Test
//    void getNewHost_returnsEarliestJoinedPlayer_whenMultiplePlayersPresent() {
//        LobbyEntity lobby = mock(LobbyEntity.class);
//        UserEntity host = mock(UserEntity.class);
//        UserEntity p1 = mock(UserEntity.class);
//        UserEntity p2 = mock(UserEntity.class);
//        UserEntity p3 = mock(UserEntity.class);
//
//        Instant t1 = Instant.parse("2024-01-01T10:00:00Z");
//        Instant t2 = Instant.parse("2024-01-01T10:01:00Z");
//        Instant t3 = Instant.parse("2024-01-01T10:02:00Z");
//
//        LobbyPlayerEntity hostPlayer = mock(LobbyPlayerEntity.class);
//        LobbyPlayerEntity lp1 = mock(LobbyPlayerEntity.class);
//        LobbyPlayerEntity lp2 = mock(LobbyPlayerEntity.class);
//        LobbyPlayerEntity lp3 = mock(LobbyPlayerEntity.class);
//
//        when(lobby.getHost()).thenReturn(host);
//        when(lobby.getLobbyPlayers()).thenReturn(Set.of(hostPlayer, lp1, lp2, lp3));
//
//        when(hostPlayer.getUser()).thenReturn(host);
//        when(lp1.getUser()).thenReturn(p1);
//        when(lp1.getJoinedAt()).thenReturn(t2);
//
//        when(lp2.getUser()).thenReturn(p2);
//        when(lp2.getJoinedAt()).thenReturn(t1); // earliest
//
//        when(lp3.getUser()).thenReturn(p3);
//        when(lp3.getJoinedAt()).thenReturn(t3);
//
//        Optional<UserEntity> result = lobbyService.getNewHost(lobby);
//
//        assertThat(result).contains(p2);
//    }

}
