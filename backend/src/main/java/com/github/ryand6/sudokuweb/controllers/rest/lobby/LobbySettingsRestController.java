package com.github.ryand6.sudokuweb.controllers.rest.lobby;

import com.github.ryand6.sudokuweb.dto.entity.LobbyChatMessageDto;
import com.github.ryand6.sudokuweb.dto.entity.LobbyDto;
import com.github.ryand6.sudokuweb.dto.request.LobbyDifficultyUpdateRequestDto;
import com.github.ryand6.sudokuweb.dto.request.LobbyTimeLimitUpdateRequestDto;
import com.github.ryand6.sudokuweb.services.lobby.LobbyChatService;
import com.github.ryand6.sudokuweb.services.lobby.LobbySettingsService;
import com.github.ryand6.sudokuweb.services.lobby.LobbyWebSocketsService;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/lobby/settings")
public class LobbySettingsRestController {

    private final LobbySettingsService lobbySettingsService;
    private final LobbyChatService lobbyChatService;
    private final LobbyWebSocketsService lobbyWebSocketsService;
    private final SimpMessagingTemplate messagingTemplate;

    public LobbySettingsRestController(LobbySettingsService lobbySettingsService,
                                       LobbyChatService lobbyChatService,
                                       LobbyWebSocketsService lobbyWebSocketsService,
                                       SimpMessagingTemplate messagingTemplate) {
        this.lobbySettingsService = lobbySettingsService;
        this.lobbyChatService = lobbyChatService;
        this.lobbyWebSocketsService = lobbyWebSocketsService;
        this.messagingTemplate = messagingTemplate;
    }

    // Update difficulty to be applied to lobby games
    @PostMapping("/update-difficulty")
    public ResponseEntity<?> updateLobbyDifficulty(@RequestBody LobbyDifficultyUpdateRequestDto requestDto) {
        LobbyDto lobbyDto = lobbySettingsService.updateLobbyDifficulty(requestDto.getLobbyId(), requestDto.getDifficulty());
        lobbyWebSocketsService.handleLobbyUpdate(lobbyDto, messagingTemplate);

        // Send an info update to the lobby chat
        String message = "updated the difficulty to " + requestDto.getDifficulty().toString().toLowerCase() + ".";
        LobbyChatMessageDto infoMessage = lobbyChatService.submitInfoMessage(lobbyDto.getId(), requestDto.getUserId(), message);
        lobbyWebSocketsService.handleLobbyChatMessage(infoMessage, messagingTemplate);

        return ResponseEntity.ok(lobbyDto);
    }

    // Update time limit to be applied to lobby games
    @PostMapping("/update-time-limit")
    public ResponseEntity<?> updateLobbyTimeLimit(@RequestBody LobbyTimeLimitUpdateRequestDto requestDto) {
        LobbyDto lobbyDto = lobbySettingsService.updateLobbyTimeLimit(requestDto.getLobbyId(), requestDto.getTimeLimit());
        lobbyWebSocketsService.handleLobbyUpdate(lobbyDto, messagingTemplate);

        // Send an info update to the lobby chat
        String message = "updated the time limit to " + requestDto.getTimeLimit().toString().toLowerCase() + ".";
        LobbyChatMessageDto infoMessage = lobbyChatService.submitInfoMessage(lobbyDto.getId(), requestDto.getUserId(), message);
        lobbyWebSocketsService.handleLobbyChatMessage(infoMessage, messagingTemplate);

        return ResponseEntity.ok(lobbyDto);
    }

}
