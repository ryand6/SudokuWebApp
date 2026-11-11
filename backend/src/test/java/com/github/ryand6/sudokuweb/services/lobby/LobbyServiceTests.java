package com.github.ryand6.sudokuweb.services.lobby;

import com.github.ryand6.sudokuweb.domain.lobby.countdown.CountdownEvaluationResult;
import com.github.ryand6.sudokuweb.domain.lobby.player.LobbyPlayerFactory;
import com.github.ryand6.sudokuweb.domain.lobby.countdown.LobbyCountdownEntity;
import com.github.ryand6.sudokuweb.domain.lobby.LobbyEntity;
import com.github.ryand6.sudokuweb.domain.lobby.player.LobbyPlayerEntity;
import com.github.ryand6.sudokuweb.domain.lobby.player.LobbyPlayerId;
import com.github.ryand6.sudokuweb.domain.user.UserEntity;
import com.github.ryand6.sudokuweb.dto.entity.lobby.LobbyDto;
import com.github.ryand6.sudokuweb.dto.entity.lobby.LobbyPlayerDto;
import com.github.ryand6.sudokuweb.enums.GameMode;
import com.github.ryand6.sudokuweb.enums.GameType;
import com.github.ryand6.sudokuweb.exceptions.lobby.LobbyNotFoundException;
import com.github.ryand6.sudokuweb.exceptions.lobby.player.LobbyPlayerNotFoundException;
import com.github.ryand6.sudokuweb.exceptions.lobby.UserExistsInActiveLobbyException;
import com.github.ryand6.sudokuweb.mappers.Impl.lobby.LobbyCountdownEntityDtoMapper;
import com.github.ryand6.sudokuweb.mappers.Impl.lobby.LobbyEntityDtoMapper;
import com.github.ryand6.sudokuweb.mappers.Impl.lobby.LobbyPlayerEntityDtoMapper;
import com.github.ryand6.sudokuweb.mappers.Impl.user.UserEntityDtoMapper;
import com.github.ryand6.sudokuweb.domain.lobby.LobbyRepository;
import com.github.ryand6.sudokuweb.services.user.UserService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class LobbyServiceTests {

    // One @Mock for every constructor parameter of LobbyService.
    // Any parameter without a matching @Mock is passed in as null, which causes NullPointerExceptions.
    @Mock
    private LobbyRepository lobbyRepository;

    @Mock
    private UserService userService;

    @Mock
    private LobbyChatService lobbyChatService;

    @Mock
    private LobbyEntityDtoMapper lobbyEntityDtoMapper;

    @Mock
    private LobbyPlayerEntityDtoMapper lobbyPlayerEntityDtoMapper;

    @Mock
    private LobbyCountdownEntityDtoMapper lobbyCountdownEntityDtoMapper;

    @Mock
    private UserEntityDtoMapper userEntityDtoMapper;

    @Mock
    private PrivateLobbyTokenService privateLobbyTokenService;

    @Mock
    private LobbyCountdownMutationService lobbyCountdownMutationService;

    @Mock
    private ApplicationEventPublisher applicationEventPublisher;

    @Spy
    @InjectMocks
    private LobbyService lobbyService;

    @Test
    public void createNewLobby_successfulCreation() {

        when(lobbyRepository.findByLobbyPlayers_User_IdAndIsActiveTrue(anyLong())).thenReturn(Set.of());

        UserEntity user = new UserEntity();
        user.setId(1L);
        user.setUsername("Test User");
        when(userService.findUserById(anyLong())).thenReturn(user);

        LobbyPlayerDto lobbyPlayerDto = new LobbyPlayerDto();
        lobbyPlayerDto.setId(new LobbyPlayerId(1L, user.getId()));

        LobbyDto lobbyDto = new LobbyDto();
        lobbyDto.setId(1L);
        lobbyDto.setLobbyName("Test Lobby");
        lobbyDto.setLobbyPlayers(Set.of(lobbyPlayerDto));
        when(lobbyEntityDtoMapper.mapToDto(any(LobbyEntity.class))).thenReturn(lobbyDto);

        String lobbyName = "Test Lobby";
        boolean isPublic = true;
        Long requesterId = 1L;
        LobbyDto lobbyDtoTest = lobbyService.createNewLobby(lobbyName, isPublic, requesterId, GameMode.CLASSIC, GameType.RANKED);
        assertThat(lobbyDtoTest.getId()).isEqualTo(1L);
        assertThat(lobbyDtoTest.getLobbyName()).isEqualTo("Test Lobby");
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
        lobby.setLobbyName("Test Lobby");

        when(lobbyRepository.findByLobbyPlayers_User_IdAndIsActiveTrue(anyLong())).thenReturn(Set.of(lobby));

        UserExistsInActiveLobbyException ex = assertThrows(UserExistsInActiveLobbyException.class, () -> {
            lobbyService.joinLobby(userId, lobbyId);
        });
        Assertions.assertEquals("You are currently a player in an active lobby called Test Lobby. Players can only be in one active lobby at a time.", ex.getMessage());
    }

    @Test
    void joinLobby_publicLobby_returnsLobbyDto() {
        Long lobbyId = 1L;
        Long userId = 1L;

        LobbyEntity lobby = mock(LobbyEntity.class);

        Set<LobbyPlayerEntity> players = new HashSet<>();
        for (int i = 0; i < 3; i++) {
            players.add(mock(LobbyPlayerEntity.class));
        }
        when(lobby.getLobbyPlayers()).thenReturn(players);

        when(lobbyRepository.findById(lobbyId)).thenReturn(Optional.of(lobby));

        UserEntity requester = mock(UserEntity.class);
        when(userService.findUserById(any())).thenReturn(requester);

        LobbyPlayerEntity lobbyPlayer = mock(LobbyPlayerEntity.class);
        when(lobbyPlayerEntityDtoMapper.mapToDto(any())).thenReturn(new LobbyPlayerDto());

        LobbyDto lobbyDto = new LobbyDto();
        lobbyDto.setId(1L);
        when(lobbyEntityDtoMapper.mapToDto(any())).thenReturn(lobbyDto);

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

        Set<LobbyPlayerEntity> players = new HashSet<>();
        for (int i = 0; i < 2; i++) players.add(mock(LobbyPlayerEntity.class));
        when(lobby.getLobbyPlayers()).thenReturn(players);

        UserEntity requester = mock(UserEntity.class);
        when(userService.findUserById(userId)).thenReturn(requester);

        LobbyPlayerEntity lobbyPlayer = mock(LobbyPlayerEntity.class);
        when(lobbyPlayerEntityDtoMapper.mapToDto(any())).thenReturn(new LobbyPlayerDto());

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
        when(lobby.getHost()).thenReturn(host);
        when(lobby.isActive()).thenReturn(true);
        when(lobby.getLobbyCountdownEntity()).thenReturn(lobbyCountdown);
        when(lobbyCountdownMutationService.safeEvaluateCountdown(lobbyCountdown)).thenReturn(new CountdownEvaluationResult());
        when(lobbyEntityDtoMapper.mapToDto(any(LobbyEntity.class))).thenReturn(new LobbyDto());

        lobbyService.removeFromLobby(lobbyId, playerLeavingId);

        Assertions.assertFalse(lobby.getLobbyPlayers().contains(playerLeaving));
        Assertions.assertTrue(lobby.getLobbyPlayers().contains(hostPlayer));
        verify(lobbyChatService).createInfoMessage(lobbyId, playerLeavingId, "left the lobby.", true);
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
        when(lobby.getHost()).thenReturn(host);
        when(lobby.isActive()).thenReturn(true);
        when(lobby.getLobbyCountdownEntity()).thenReturn(lobbyCountdown);
        when(lobbyCountdownMutationService.safeEvaluateCountdown(lobbyCountdown)).thenReturn(new CountdownEvaluationResult());
        when(lobbyEntityDtoMapper.mapToDto(any(LobbyEntity.class))).thenReturn(new LobbyDto());

        LobbyDto lobbyDto = lobbyService.removeFromLobby(lobbyId, playerLeavingId);

        assertThat(lobbyDto).isNotNull();
    }

    @Test
    void closeLobby_verifyMethodCalls() {
        LobbyEntity lobby = mock(LobbyEntity.class);
        when(lobby.getId()).thenReturn(1L);

        lobbyService.closeLobby(lobby);

        verify(lobby).setActive(false);
        verify(applicationEventPublisher).publishEvent(any(Object.class));
    }

    @Test
    void closeLobby_setsIsActiveToFalse() {
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
        when(lobbyRepository.findById(any(Long.class))).thenReturn(Optional.empty());

        LobbyNotFoundException ex = assertThrows(LobbyNotFoundException.class, () -> {
            lobbyService.getLobbyDtoById(1L);
        });

        Assertions.assertEquals("Lobby with ID 1 does not exist", ex.getMessage());
    }

}
