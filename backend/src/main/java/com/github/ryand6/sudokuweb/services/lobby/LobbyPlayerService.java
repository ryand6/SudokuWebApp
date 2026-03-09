package com.github.ryand6.sudokuweb.services.lobby;

import com.github.ryand6.sudokuweb.domain.lobby.LobbyEntity;
import com.github.ryand6.sudokuweb.domain.lobby.countdown.CountdownEvaluationResult;
import com.github.ryand6.sudokuweb.domain.lobby.player.LobbyPlayerEntity;
import com.github.ryand6.sudokuweb.dto.entity.lobby.LobbyChatMessageDto;
import com.github.ryand6.sudokuweb.dto.entity.lobby.LobbyDto;
import com.github.ryand6.sudokuweb.enums.LobbyStatus;
import com.github.ryand6.sudokuweb.mappers.Impl.lobby.LobbyEntityDtoMapper;
import jakarta.transaction.Transactional;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class LobbyPlayerService {

    private final LobbyService lobbyService;
    private final LobbyChatService lobbyChatService;
    private final LobbyWebSocketsService lobbyWebSocketsService;
    private final LobbyEntityDtoMapper lobbyEntityDtoMapper;
    private final SimpMessagingTemplate simpMessagingTemplate;
    private final LobbyCountdownSchedulerService lobbyCountdownSchedulerService;
    private final LobbyCountdownMutationService lobbyCountdownMutationService;

    public LobbyPlayerService(LobbyService lobbyService,
                              LobbyChatService lobbyChatService,
                              LobbyWebSocketsService lobbyWebSocketsService,
                              LobbyEntityDtoMapper lobbyEntityDtoMapper,
                              SimpMessagingTemplate simpMessagingTemplate,
                              LobbyCountdownSchedulerService lobbyCountdownSchedulerService,
                              LobbyCountdownMutationService lobbyCountdownMutationService) {
        this.lobbyService = lobbyService;
        this.lobbyChatService = lobbyChatService;
        this.lobbyWebSocketsService = lobbyWebSocketsService;
        this.lobbyEntityDtoMapper = lobbyEntityDtoMapper;
        this.simpMessagingTemplate = simpMessagingTemplate;
        this.lobbyCountdownSchedulerService = lobbyCountdownSchedulerService;
        this.lobbyCountdownMutationService = lobbyCountdownMutationService;
    }

    @Transactional
    public LobbyDto updateLobbyPlayerStatus(Long lobbyId, Long userId, LobbyStatus lobbyStatus) {
        LobbyEntity lobby = lobbyService.getLobbyById(lobbyId);
        LobbyPlayerEntity lobbyPlayer = lobbyService.findLobbyPlayer(lobby, userId);
        lobbyPlayer.validateStatusChange();
        // Lobby Player managed by JPA therefore update will apply
        lobbyPlayer.setStatus(lobbyStatus);
        // Handle any countdown updates that may be required
        CountdownEvaluationResult countdownEvaluationResult = lobbyCountdownMutationService.safeEvaluateCountdown(lobby.getLobbyCountdownEntity());
        if (countdownEvaluationResult.getNewInitiator() != null) {
            LobbyChatMessageDto infoMessage = lobbyChatService.submitInfoMessage(lobbyId, countdownEvaluationResult.getNewInitiator(), "started the new game countdown.");
            lobbyWebSocketsService.handleLobbyChatMessage(infoMessage, simpMessagingTemplate);
        }
        lobbyCountdownSchedulerService.handleCountdownEvaluationResult(lobbyId, countdownEvaluationResult);
        return lobbyEntityDtoMapper.mapToDto(lobby);
    }


}
