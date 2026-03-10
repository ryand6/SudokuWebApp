package com.github.ryand6.sudokuweb.controllers.rest.lobby;

import com.github.ryand6.sudokuweb.dto.entity.lobby.LobbyChatMessageDto;
import com.github.ryand6.sudokuweb.dto.entity.lobby.LobbyDto;
import com.github.ryand6.sudokuweb.dto.request.LobbyPlayerStatusUpdateRequestDto;
import com.github.ryand6.sudokuweb.services.lobby.LobbyChatService;
import com.github.ryand6.sudokuweb.services.lobby.LobbyPlayerService;
import com.github.ryand6.sudokuweb.services.lobby.LobbyWebSocketsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/lobby/player")
public class LobbyPlayerRestController {

    private final LobbyPlayerService lobbyPlayerService;

    public LobbyPlayerRestController(LobbyPlayerService lobbyPlayerService) {
        this.lobbyPlayerService = lobbyPlayerService;
    }

    // Update a lobby player's status
    @PostMapping("/update-player-status")
    public ResponseEntity<?> updateLobbyPlayerStatus(@RequestBody LobbyPlayerStatusUpdateRequestDto requestDto) {
        LobbyDto lobbyDto = lobbyPlayerService.updateLobbyPlayerStatus(requestDto.getLobbyId(), requestDto.getUserId(), requestDto.getLobbyStatus());
        return ResponseEntity.ok(lobbyDto);
    }

}
