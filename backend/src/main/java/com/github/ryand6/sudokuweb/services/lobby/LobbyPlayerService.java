package com.github.ryand6.sudokuweb.services.lobby;

import com.github.ryand6.sudokuweb.domain.lobby.LobbyEntity;
import com.github.ryand6.sudokuweb.domain.lobby.countdown.CountdownEvaluationResult;
import com.github.ryand6.sudokuweb.domain.lobby.player.LobbyPlayerEntity;
import com.github.ryand6.sudokuweb.dto.entity.LobbyChatMessageDto;
import com.github.ryand6.sudokuweb.dto.entity.LobbyDto;
import com.github.ryand6.sudokuweb.enums.LobbyStatus;
import com.github.ryand6.sudokuweb.mappers.Impl.LobbyEntityDtoMapper;
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
    private final LobbyCountdownService lobbyCountdownService;

    public LobbyPlayerService(LobbyService lobbyService,
                              LobbyChatService lobbyChatService,
                              LobbyWebSocketsService lobbyWebSocketsService,
                              LobbyEntityDtoMapper lobbyEntityDtoMapper,
                              SimpMessagingTemplate simpMessagingTemplate,
                              LobbyCountdownService lobbyCountdownService) {
        this.lobbyService = lobbyService;
        this.lobbyChatService = lobbyChatService;
        this.lobbyWebSocketsService = lobbyWebSocketsService;
        this.lobbyEntityDtoMapper = lobbyEntityDtoMapper;
        this.simpMessagingTemplate = simpMessagingTemplate;
        this.lobbyCountdownService = lobbyCountdownService;
    }

    @Transactional
    public LobbyDto updateLobbyPlayerStatus(Long lobbyId, Long userId, LobbyStatus lobbyStatus) {
        LobbyEntity lobby = lobbyService.getLobbyById(lobbyId);
        LobbyPlayerEntity lobbyPlayer = lobbyService.findLobbyPlayer(lobby, userId);
        // Lobby Player managed by JPA therefore update will apply
        lobbyPlayer.setStatus(lobbyStatus);
        // Handle any countdown updates that may be required
        CountdownEvaluationResult countdownEvaluationResult = lobby.getLobbyCountdownEntity().evaluateCountdownState();
        if (countdownEvaluationResult.getNewInitiator() != null) {
            LobbyChatMessageDto infoMessage = lobbyChatService.submitInfoMessage(lobbyId, countdownEvaluationResult.getNewInitiator(), "started the new game countdown.");
            lobbyWebSocketsService.handleLobbyChatMessage(infoMessage, simpMessagingTemplate);
        }
        lobbyCountdownService.handleCountdownEvaluationResult(lobby, countdownEvaluationResult);
        return lobbyEntityDtoMapper.mapToDto(lobby);
    }


}
