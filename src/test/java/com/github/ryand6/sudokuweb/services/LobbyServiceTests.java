package com.github.ryand6.sudokuweb.services;

import com.github.ryand6.sudokuweb.TestDataUtil;
import com.github.ryand6.sudokuweb.domain.LobbyEntity;
import com.github.ryand6.sudokuweb.domain.LobbyPlayerEntity;
import com.github.ryand6.sudokuweb.domain.LobbyPlayerId;
import com.github.ryand6.sudokuweb.domain.UserEntity;
import com.github.ryand6.sudokuweb.domain.factory.LobbyPlayerFactory;
import com.github.ryand6.sudokuweb.dto.LobbyDto;
import com.github.ryand6.sudokuweb.dto.LobbyPlayerDto;
import com.github.ryand6.sudokuweb.exceptions.*;
import com.github.ryand6.sudokuweb.mappers.Impl.LobbyEntityDtoMapper;
import com.github.ryand6.sudokuweb.repositories.LobbyPlayerRepository;
import com.github.ryand6.sudokuweb.repositories.LobbyRepository;
import com.github.ryand6.sudokuweb.services.impl.LobbyService;
import com.github.ryand6.sudokuweb.services.impl.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

public class LobbyServiceTests {

    @Mock
    private LobbyRepository lobbyRepository;

    @Mock
    private UserService userService;

    @Mock
    private LobbyEntityDtoMapper lobbyEntityDtoMapper;

    @Mock
    private LobbyPlayerRepository lobbyPlayerRepository;

    @InjectMocks
    private LobbyService lobbyService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void generateUniqueCode_verifyLoopIterations_checkCodeLengthAndClassType() {
        // Simulate first 2 attempts fail (return true), third attempt succeeds (false)
        when(lobbyRepository.existsByJoinCode(Mockito.anyString()))
                .thenReturn(true, true, false);

        String code = lobbyService.generateUniqueCode();

        // Verify existsByJoinCode called exactly 3 times - loop ran 3 times
        verify(lobbyRepository, Mockito.times(3)).existsByJoinCode(Mockito.anyString());

        assertNotNull(code);
        assertInstanceOf(String.class, code);
        assertEquals(25, code.length());
    }

    @Test
    public void createNewLobby_invalidIsPublicAndIsPrivateParameters() {
        String lobbyName = "Test Lobby";
        boolean isPublic = false;
        boolean isPrivate = false;
        String joinCode = null;
        Long requesterId = 1L;
        InvalidLobbyPublicStatusParametersException ex = assertThrows(
                InvalidLobbyPublicStatusParametersException.class,
                () -> lobbyService.createNewLobby(lobbyName, isPublic, isPrivate, joinCode, requesterId)
        );
        assertThat(ex.getMessage()).isEqualTo("Either Public or Private lobby must be checked");
    }

    @Test
    public void createNewLobby_successfulCreation() {
        UserEntity user = new UserEntity();
        user.setId(1L);
        user.setUsername("Test User");
        when(userService.findUserById(anyLong())).thenReturn(user);

        LobbyEntity lobby = new LobbyEntity();
        lobby.setId(1L);
        lobby.setLobbyName("Test Lobby");
        when(lobbyRepository.save(any(LobbyEntity.class))).thenReturn(lobby);

        LobbyPlayerEntity lobbyPlayerEntity = new LobbyPlayerEntity();
        lobbyPlayerEntity.setLobby(lobby);
        lobbyPlayerEntity.setUser(user);
        when(lobbyPlayerRepository.save(any(LobbyPlayerEntity.class))).thenReturn(lobbyPlayerEntity);

        LobbyPlayerDto lobbyPlayerDto = new LobbyPlayerDto();
        lobbyPlayerDto.setId(new LobbyPlayerId(lobby.getId(), user.getId()));

        LobbyDto lobbyDto = new LobbyDto();
        lobbyDto.setId(1L);
        lobbyDto.setLobbyName("Test Lobby");
        lobbyDto.setIsPublic(true);
        lobbyDto.setLobbyPlayers(Set.of(lobbyPlayerDto));
        when(lobbyEntityDtoMapper.mapToDto(any(LobbyEntity.class))).thenReturn(lobbyDto);

        String lobbyName = "Test Lobby";
        boolean isPublic = true;
        boolean isPrivate = false;
        String joinCode = null;
        Long requesterId = 1L;
        LobbyDto lobbyDtoTest = lobbyService.createNewLobby(lobbyName, isPublic, isPrivate, joinCode, requesterId);
        assertThat(lobbyDtoTest.getId()).isEqualTo(1L);
        assertThat(lobbyDtoTest.getLobbyName()).isEqualTo("Test Lobby");
        assertThat(lobbyDtoTest.getIsPublic()).isEqualTo(true);
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

        when(lobbyRepository.findByIsPublicTrueAndIsActiveTrue(PageRequest.of(page, size, Sort.by("createdAt").descending())))
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

        when(lobbyRepository.findByIdForUpdate(lobbyId)).thenReturn(Optional.empty());

        LobbyNotFoundException ex = assertThrows(LobbyNotFoundException.class, () -> {
            lobbyService.joinLobby(lobbyId, userId, null);
        });
        assertEquals("Lobby with ID 1 does not exist", ex.getMessage());
    }

    @Test
    void joinLobby_throwsLobbyInactiveException_whenLobbyInactive() {
        Long lobbyId = 1L;
        Long userId = 1L;

        LobbyEntity lobby = mock(LobbyEntity.class);
        when(lobbyRepository.findByIdForUpdate(lobbyId)).thenReturn(Optional.of(lobby));
        when(lobby.getIsActive()).thenReturn(false);

        LobbyInactiveException ex = assertThrows(LobbyInactiveException.class, () -> {
            lobbyService.joinLobby(lobbyId, userId, null);
        });
        assertEquals("Lobby with ID 1 is no longer active, please try joining a different lobby or creating your own", ex.getMessage());
    }

    @Test
    void joinLobby_throwsLobbyFullException_whenLobbyFull() {
        Long lobbyId = 1L;
        Long userId = 1L;
        LobbyEntity lobby = mock(LobbyEntity.class);
        when(lobby.getIsPublic()).thenReturn(true);
        Set<LobbyPlayerEntity> players = new HashSet<>();
        // Mock lobby to have 4 players already
        for (int i = 0; i < 4; i++) {
            players.add(mock(LobbyPlayerEntity.class));
        }

        when(lobbyRepository.findByIdForUpdate(lobbyId)).thenReturn(Optional.of(lobby));
        when(lobby.getIsActive()).thenReturn(true);
        when(lobby.getLobbyPlayers()).thenReturn(players);

        LobbyFullException ex = assertThrows(LobbyFullException.class, () -> {
            lobbyService.joinLobby(lobbyId, userId, null);
        });
        assertEquals("Lobby with ID 1 is currently full, please try joining a different lobby or create your own", ex.getMessage());
    }

    @Test
    void joinLobby_throwsInvalidJoinCodeException_whenJoinCodeMissing() {
        Long lobbyId = 1L;
        Long userId = 1L;

        LobbyEntity lobby = mock(LobbyEntity.class);
        when(lobbyRepository.findByIdForUpdate(lobbyId)).thenReturn(Optional.of(lobby));
        when(lobby.getIsActive()).thenReturn(true);
        when(lobby.getIsPublic()).thenReturn(false);
        when(lobby.getJoinCode()).thenReturn("abc123");

        InvalidTokenException ex = assertThrows(InvalidTokenException.class, () -> {
            lobbyService.joinLobby(lobbyId, userId, null);
        });

        assertEquals("Invalid or missing join code for private lobby", ex.getMessage());
    }

    @Test
    void joinPrivateLobby_throwsInvalidJoinCodeException_whenJoinCodeWrong() {
        Long lobbyId = 1L;
        Long userId = 1L;

        LobbyEntity lobby = mock(LobbyEntity.class);
        when(lobbyRepository.findByIdForUpdate(lobbyId)).thenReturn(Optional.of(lobby));
        when(lobby.getIsActive()).thenReturn(true);
        when(lobby.getIsPublic()).thenReturn(false);
        when(lobby.getJoinCode()).thenReturn("abc123");

        InvalidTokenException ex = assertThrows(InvalidTokenException.class, () -> {
            lobbyService.joinLobby(lobbyId, userId, "wrong-code");
        });

        assertEquals("Invalid or missing join code for private lobby", ex.getMessage());
    }

    @Test
    void joinLobby_publicLobby_returnsLobbyDto() {
        Long lobbyId = 1L;
        Long userId = 1L;

        LobbyEntity lobby = mock(LobbyEntity.class);
        when(lobby.getIsPublic()).thenReturn(true);
        when(lobbyRepository.findByIdForUpdate(lobbyId)).thenReturn(Optional.of(lobby));
        when(lobby.getIsActive()).thenReturn(true);

        Set<LobbyPlayerEntity> players = new HashSet<>();
        for (int i = 0; i < 3; i++) {
            players.add(mock(LobbyPlayerEntity.class));
        }
        when(lobby.getLobbyPlayers()).thenReturn(players);

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

            LobbyDto returnVal = lobbyService.joinLobby(lobbyId, userId, null);
            assertThat(returnVal).isEqualTo(lobbyDto);
        }
    }

    @Test
    void joinLobby_privateLobby_returnsLobbyDto_whenCorrectJoinCode() {
        Long lobbyId = 1L;
        Long userId = 1L;
        String joinCode = "abc123";

        LobbyEntity lobby = mock(LobbyEntity.class);
        when(lobbyRepository.findByIdForUpdate(lobbyId)).thenReturn(Optional.of(lobby));
        when(lobby.getIsActive()).thenReturn(true);
        when(lobby.getIsPublic()).thenReturn(false);
        when(lobby.getJoinCode()).thenReturn(joinCode);

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

            LobbyDto result = lobbyService.joinLobby(lobbyId, userId, joinCode);
            assertThat(result).isEqualTo(lobbyDto);
        }
    }

    @Test
    public void removePlayerFromLobby_throwsLobbyNotFoundException() {
        Long lobbyId = 1L;
        Long userId = 1L;

        when(lobbyRepository.findByIdForUpdate(lobbyId)).thenReturn(Optional.empty());

        LobbyNotFoundException ex = assertThrows(LobbyNotFoundException.class, () -> {
            lobbyService.removeFromLobby(lobbyId, userId);
        });
        assertEquals("Lobby with ID 1 does not exist", ex.getMessage());
    }

    @Test
    public void removePlayerFromLobby_throwsLobbyPlayerNotFoundException() {
        Long lobbyId = 1L;
        Long userId = 1L;

        LobbyEntity lobby = mock(LobbyEntity.class);
        when(lobbyRepository.findByIdForUpdate(lobbyId)).thenReturn(Optional.of(lobby));

        when(lobbyPlayerRepository.findByCompositeId(anyLong(), anyLong())).thenReturn(Optional.empty());

        LobbyPlayerNotFoundException ex = assertThrows(LobbyPlayerNotFoundException.class, () -> {
            lobbyService.removeFromLobby(lobbyId, userId);
        });
        assertEquals("Lobby Player with Lobby ID 1 and User ID 1 does not exist", ex.getMessage());
    }

    @Test
    void removePlayerFromLobby_returnsLobbyDto_whenSuccessfulAndNotHost() {
        // Arrange
        Long userId = 1L;
        Long lobbyId = 100L;

        LobbyEntity lobby = mock(LobbyEntity.class);
        UserEntity host = mock(UserEntity.class);
        UserEntity playerLeaving = mock(UserEntity.class);
        LobbyPlayerEntity lobbyPlayer = mock(LobbyPlayerEntity.class);
        LobbyDto expectedLobbyDto = new LobbyDto();
        expectedLobbyDto.setId(lobbyId);

        when(userService.findUserById(userId)).thenReturn(host);
        when(lobby.getHost()).thenReturn(host);

        Set<LobbyPlayerEntity> currentPlayers = new HashSet<>();
        currentPlayers.add(lobbyPlayer);
        when(lobby.getLobbyPlayers()).thenReturn(currentPlayers);

        when(lobbyRepository.findByIdForUpdate(lobbyId)).thenReturn(Optional.of(lobby));
        when(lobbyPlayerRepository.findByCompositeId(lobbyId, userId)).thenReturn(Optional.of(lobbyPlayer));
        when(userService.findUserById(userId)).thenReturn(playerLeaving);
        when(lobbyEntityDtoMapper.mapToDto(lobby)).thenReturn(expectedLobbyDto);

        LobbyDto result = lobbyService.removeFromLobby(userId, lobbyId);

        assertThat(result).isEqualTo(expectedLobbyDto);
        verify(lobbyPlayerRepository).deleteByCompositeId(lobbyId, userId);
        verify(lobby).setLobbyPlayers(any(Set.class));
        verify(lobbyEntityDtoMapper).mapToDto(lobby);
    }

    @Test
    void removePlayerFromLobby_reassignsHost_whenHostLeavesAndNewHostExists() {
        Long userId = 1L;
        Long lobbyId = 100L;

        LobbyEntity lobby = mock(LobbyEntity.class);
        UserEntity host = mock(UserEntity.class);
        UserEntity newHost = mock(UserEntity.class);
        LobbyPlayerEntity lobbyPlayer = mock(LobbyPlayerEntity.class);

        LobbyDto expectedDto = new LobbyDto();
        expectedDto.setId(lobbyId);

        // Setup basic behavior
        when(lobbyRepository.findByIdForUpdate(lobbyId)).thenReturn(Optional.of(lobby));
        when(lobbyPlayerRepository.findByCompositeId(lobbyId, userId)).thenReturn(Optional.of(lobbyPlayer));
        when(userService.findUserById(userId)).thenReturn(host);
        when(userService.findUserById(userId)).thenReturn(host);
        when(lobby.getHost()).thenReturn(host);

        // Assume players exist, new host will be found
        Set<LobbyPlayerEntity> players = new HashSet<>();
        players.add(lobbyPlayer);
        when(lobby.getLobbyPlayers()).thenReturn(players);

        // partial mock to override getNewHost
        LobbyService spyService = Mockito.spy(lobbyService);
        doReturn(Optional.of(newHost)).when(spyService).getNewHost(lobby);

        when(lobbyEntityDtoMapper.mapToDto(lobby)).thenReturn(expectedDto);

        LobbyDto result = spyService.removeFromLobby(userId, lobbyId);

        assertThat(result).isEqualTo(expectedDto);

        verify(lobby).setHost(newHost);
        verify(lobbyPlayerRepository).deleteByCompositeId(lobbyId, userId);
        verify(lobbyEntityDtoMapper).mapToDto(lobby);
    }

    @Test
    void removePlayerFromLobby_closesLobby_whenHostLeavesAndNoNewHost() {
        Long userId = 1L;
        Long lobbyId = 100L;

        LobbyEntity lobbyBeforeClosing = mock(LobbyEntity.class);
        LobbyEntity closedLobby = mock(LobbyEntity.class); // Result of closeLobby
        UserEntity host = mock(UserEntity.class);
        LobbyPlayerEntity lobbyPlayer = mock(LobbyPlayerEntity.class);

        LobbyDto expectedDto = new LobbyDto();
        expectedDto.setId(lobbyId);

        // Setup repository and entity mocks
        when(lobbyRepository.findByIdForUpdate(lobbyId)).thenReturn(Optional.of(lobbyBeforeClosing));
        when(lobbyPlayerRepository.findByCompositeId(lobbyId, userId)).thenReturn(Optional.of(lobbyPlayer));
        when(userService.findUserById(userId)).thenReturn(host);
        when(userService.findUserById(userId)).thenReturn(host);
        when(lobbyBeforeClosing.getHost()).thenReturn(host);

        Set<LobbyPlayerEntity> players = new HashSet<>();
        players.add(lobbyPlayer);
        when(lobbyBeforeClosing.getLobbyPlayers()).thenReturn(players);

        // partial mock of service to override getNewHost and closeLobby
        LobbyService spyService = Mockito.spy(lobbyService);
        doReturn(Optional.empty()).when(spyService).getNewHost(lobbyBeforeClosing); // No replacement host
        doReturn(closedLobby).when(spyService).closeLobby(lobbyBeforeClosing);     // Return closed lobby

        when(lobbyEntityDtoMapper.mapToDto(closedLobby)).thenReturn(expectedDto);

        LobbyDto result = spyService.removeFromLobby(userId, lobbyId);

        assertThat(result).isEqualTo(expectedDto);
        verify(spyService).closeLobby(lobbyBeforeClosing);
        verify(lobbyPlayerRepository).deleteByCompositeId(lobbyId, userId);
        verify(lobbyEntityDtoMapper).mapToDto(closedLobby);
    }

    @Test
    void getNewHost_returnsEmpty_whenOnlyHostPresent() {
        LobbyEntity lobby = mock(LobbyEntity.class);
        UserEntity host = mock(UserEntity.class);
        LobbyPlayerEntity hostPlayer = mock(LobbyPlayerEntity.class);

        when(lobby.getHost()).thenReturn(host);
        when(lobby.getLobbyPlayers()).thenReturn(Set.of(hostPlayer));
        when(hostPlayer.getUser()).thenReturn(host);

        Optional<UserEntity> result = lobbyService.getNewHost(lobby);

        assertThat(result).isEmpty();
    }

    @Test
    void getNewHost_returnsOtherPlayer_whenHostAndOneOtherPlayerPresent() {
        LobbyEntity lobby = mock(LobbyEntity.class);
        UserEntity host = mock(UserEntity.class);
        UserEntity newHost = mock(UserEntity.class);

        LobbyPlayerEntity hostPlayer = mock(LobbyPlayerEntity.class);
        LobbyPlayerEntity otherPlayer = mock(LobbyPlayerEntity.class);

        when(lobby.getHost()).thenReturn(host);
        when(lobby.getLobbyPlayers()).thenReturn(Set.of(hostPlayer, otherPlayer));
        when(hostPlayer.getUser()).thenReturn(host);
        when(otherPlayer.getUser()).thenReturn(newHost);
        when(otherPlayer.getJoinedAt()).thenReturn(Instant.now());

        Optional<UserEntity> result = lobbyService.getNewHost(lobby);

        assertThat(result).contains(newHost);
    }

    @Test
    void getNewHost_returnsEarliestJoinedPlayer_whenMultiplePlayersPresent() {
        LobbyEntity lobby = mock(LobbyEntity.class);
        UserEntity host = mock(UserEntity.class);
        UserEntity p1 = mock(UserEntity.class);
        UserEntity p2 = mock(UserEntity.class);
        UserEntity p3 = mock(UserEntity.class);

        Instant t1 = Instant.parse("2024-01-01T10:00:00Z");
        Instant t2 = Instant.parse("2024-01-01T10:01:00Z");
        Instant t3 = Instant.parse("2024-01-01T10:02:00Z");

        LobbyPlayerEntity hostPlayer = mock(LobbyPlayerEntity.class);
        LobbyPlayerEntity lp1 = mock(LobbyPlayerEntity.class);
        LobbyPlayerEntity lp2 = mock(LobbyPlayerEntity.class);
        LobbyPlayerEntity lp3 = mock(LobbyPlayerEntity.class);

        when(lobby.getHost()).thenReturn(host);
        when(lobby.getLobbyPlayers()).thenReturn(Set.of(hostPlayer, lp1, lp2, lp3));

        when(hostPlayer.getUser()).thenReturn(host);
        when(lp1.getUser()).thenReturn(p1);
        when(lp1.getJoinedAt()).thenReturn(t2);

        when(lp2.getUser()).thenReturn(p2);
        when(lp2.getJoinedAt()).thenReturn(t1); // earliest

        when(lp3.getUser()).thenReturn(p3);
        when(lp3.getJoinedAt()).thenReturn(t3);

        Optional<UserEntity> result = lobbyService.getNewHost(lobby);

        assertThat(result).contains(p2);
    }

    @Test
    void closeLobby_returnsClosedLobby() {
        UserEntity userEntity = TestDataUtil.createTestUserA(TestDataUtil.createTestScoreA());
        LobbyEntity lobbyEntity = TestDataUtil.createTestLobbyA(userEntity);
        lobbyEntity = lobbyService.closeLobby(lobbyEntity);
        assertThat(lobbyEntity.getIsActive()).isEqualTo(false);
    }

}
