package com.github.ryand6.sudokuweb.schedulers;

import com.github.ryand6.sudokuweb.domain.LobbyEntity;
import com.github.ryand6.sudokuweb.dto.entity.GameDto;
import com.github.ryand6.sudokuweb.mappers.Impl.LobbyEntityDtoMapper;
import com.github.ryand6.sudokuweb.repositories.LobbyRepository;
import com.github.ryand6.sudokuweb.services.GameService;
import com.github.ryand6.sudokuweb.services.LobbyWebSocketsService;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;

@Component
public class StartGameScheduler {

    private final LobbyRepository lobbyRepository;
    private final GameService gameService;
    private final LobbyWebSocketsService lobbyWebSocketsService;
    private final SimpMessagingTemplate messagingTemplate;
    private final LobbyEntityDtoMapper lobbyEntityDtoMapper;

    public StartGameScheduler(LobbyRepository lobbyRepository,
                              GameService gameService,
                              LobbyWebSocketsService lobbyWebSocketsService,
                              SimpMessagingTemplate messagingTemplate,
                              LobbyEntityDtoMapper lobbyEntityDtoMapper) {
        this.lobbyRepository = lobbyRepository;
        this.gameService = gameService;
        this.lobbyWebSocketsService = lobbyWebSocketsService;
        this.messagingTemplate = messagingTemplate;
        this.lobbyEntityDtoMapper = lobbyEntityDtoMapper;
    }

    // Runs every second - creates a new game for a lobby when the countdown has reached zero and there is no other active game
    // Notifies lobby topic of game creation via WebSockets
    @Scheduled(fixedRate = 1000)
    public void scheduleGameCreation() {
        List<LobbyEntity> lobbiesAwaitingGameStart = lobbyRepository.findAllLobbiesWithExpiredCountdowns(Instant.now());
        for (LobbyEntity lobby : lobbiesAwaitingGameStart) {
            GameDto gameDto = gameService.createGameIfNoneActive(lobbyEntityDtoMapper.mapToDto(lobby));
            if (gameDto != null) {
                lobbyWebSocketsService.handleLobbyGameStart(gameDto, messagingTemplate);
            }
        }
    }

}
