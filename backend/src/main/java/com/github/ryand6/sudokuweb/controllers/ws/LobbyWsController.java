package com.github.ryand6.sudokuweb.controllers.ws;

import com.github.ryand6.sudokuweb.dto.entity.LobbyDto;
import com.github.ryand6.sudokuweb.dto.request.LobbyDifficultyUpdateRequestDto;
import com.github.ryand6.sudokuweb.dto.request.LobbyTimeLimitUpdateRequestDto;
import com.github.ryand6.sudokuweb.services.LobbyService;
import com.github.ryand6.sudokuweb.services.LobbyWebSocketsService;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
public class LobbyWsController {

    private final LobbyService lobbyService;
    private final LobbyWebSocketsService lobbyWebSocketsService;
    private final SimpMessagingTemplate messagingTemplate;

    public LobbyWsController(LobbyService lobbyService,
                             LobbyWebSocketsService lobbyWebSocketsService,
                             SimpMessagingTemplate messagingTemplate) {
        this.lobbyService = lobbyService;
        this.lobbyWebSocketsService = lobbyWebSocketsService;
        this.messagingTemplate = messagingTemplate;
    }

    // Update difficulty to be applied to lobby games
    @MessageMapping("/lobby/{lobbyId}/update-difficulty")
    public void updateLobbyDifficulty(@DestinationVariable Long lobbyId,
                                      LobbyDifficultyUpdateRequestDto requestDto) {
        LobbyDto lobbyDto = lobbyService.updateLobbyDifficulty(lobbyId, requestDto.getDifficulty());
        lobbyWebSocketsService.handleLobbyUpdate(lobbyDto, messagingTemplate);
    }

    // Update time limit to be applied to lobby games
    @MessageMapping("/lobby/{lobbyId}/update-time-limit")
    public void updateLobbyTimeLimit(@DestinationVariable Long lobbyId,
                                                  LobbyTimeLimitUpdateRequestDto requestDto) {
        LobbyDto lobbyDto = lobbyService.updateLobbyTimeLimit(lobbyId, requestDto.getTimeLimit());
        lobbyWebSocketsService.handleLobbyUpdate(lobbyDto, messagingTemplate);
    }

}
