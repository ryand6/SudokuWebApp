package com.github.ryand6.sudokuweb.schedulers;

import com.github.ryand6.sudokuweb.domain.LobbyEntity;
import com.github.ryand6.sudokuweb.dto.entity.GameDto;
import com.github.ryand6.sudokuweb.dto.entity.LobbyDto;
import com.github.ryand6.sudokuweb.enums.MessageType;
import com.github.ryand6.sudokuweb.mappers.Impl.LobbyEntityDtoMapper;
import com.github.ryand6.sudokuweb.repositories.LobbyRepository;
import com.github.ryand6.sudokuweb.services.GameService;
import com.github.ryand6.sudokuweb.services.LobbyWebSocketsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.Nested;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.time.Instant;
import java.util.List;

@ExtendWith(MockitoExtension.class)
public class StartGameSchedulerTests {

    @Mock
    private LobbyRepository lobbyRepository;

    @Mock
    private GameService gameService;

    @Mock
    private LobbyWebSocketsService lobbyWebSocketsService;

    @Mock
    private SimpMessagingTemplate simpMessagingTemplate;

    @Mock
    private LobbyEntityDtoMapper lobbyEntityDtoMapper;

    @InjectMocks
    private StartGameScheduler startGameScheduler;

    @Nested
    public class SingleLobbyReturnedTests {
        @BeforeEach
        public void setUp() {
            Mockito.when(lobbyRepository.findAllLobbiesWithExpiredCountdowns(Mockito.any(Instant.class))).thenReturn(List.of(new LobbyEntity()));
            Mockito.when(lobbyEntityDtoMapper.mapToDto(Mockito.any(LobbyEntity.class))).thenReturn(new LobbyDto());
            Mockito.when(gameService.createGameIfNoneActive(Mockito.any(LobbyDto.class))).thenReturn(new GameDto());
        }

        @Test
        public void scheduleGameCreation_findAllLobbiesWithExpiredCountdowns_isRunOnce() {
            startGameScheduler.scheduleGameCreation();
            Mockito.verify(lobbyRepository, Mockito.times(1)).findAllLobbiesWithExpiredCountdowns(Mockito.any(Instant.class));
        }

        @Test
        public void scheduleGameCreation_oneGameCreationIsAttempted_ifOneLobbyIsReturned() {
            startGameScheduler.scheduleGameCreation();
            Mockito.verify(gameService, Mockito.times(1)).createGameIfNoneActive(Mockito.any(LobbyDto.class));
        }

        @Test
        public void scheduleGameCreation_handleLobbyGameStart_isCalledWhenDtoExists() {
            startGameScheduler.scheduleGameCreation();
            Mockito.verify(lobbyWebSocketsService, Mockito.times(1)).handleLobbyGameStart(Mockito.any(GameDto.class), Mockito.any(SimpMessagingTemplate.class));
        }
    }

    @Nested
    public class MultipleLobbiesReturnedTests {
        @BeforeEach
        public void setUp() {
            Mockito.when(lobbyRepository.findAllLobbiesWithExpiredCountdowns(Mockito.any(Instant.class))).thenReturn(List.of(new LobbyEntity(), new LobbyEntity()));
            Mockito.when(lobbyEntityDtoMapper.mapToDto(Mockito.any(LobbyEntity.class))).thenReturn(new LobbyDto());
            Mockito.when(gameService.createGameIfNoneActive(Mockito.any(LobbyDto.class))).thenReturn(new GameDto());
        }

        @Test
        public void scheduleGameCreation_multipleGameCreationsAttempted_ifMultipleLobbiesAreReturned() {
            startGameScheduler.scheduleGameCreation();
            Mockito.verify(gameService, Mockito.times(2)).createGameIfNoneActive(Mockito.any(LobbyDto.class));
        }
    }

    @Nested
    public class NoGameCreatedTests {
        @BeforeEach
        public void setUp() {
            Mockito.when(lobbyRepository.findAllLobbiesWithExpiredCountdowns(Mockito.any(Instant.class))).thenReturn(List.of(new LobbyEntity()));
            Mockito.when(lobbyEntityDtoMapper.mapToDto(Mockito.any(LobbyEntity.class))).thenReturn(new LobbyDto());
            Mockito.when(gameService.createGameIfNoneActive(Mockito.any(LobbyDto.class))).thenReturn(null);
        }

        @Test
        public void scheduleGameCreation_handleLobbyGameStart_isNotCalledWhenDtoDoesNotExist() {
            startGameScheduler.scheduleGameCreation();
            Mockito.verify(lobbyWebSocketsService, Mockito.never()).handleLobbyGameStart(Mockito.any(GameDto.class), Mockito.any(SimpMessagingTemplate.class));
        }
    }

}
