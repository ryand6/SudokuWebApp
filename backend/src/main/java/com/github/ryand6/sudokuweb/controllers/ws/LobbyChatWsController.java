package com.github.ryand6.sudokuweb.controllers.ws;

import com.github.ryand6.sudokuweb.dto.entity.lobby.LobbyChatMessageDto;
import com.github.ryand6.sudokuweb.dto.request.LobbyChatMessageRequestDto;
import com.github.ryand6.sudokuweb.services.lobby.LobbyChatService;
import com.github.ryand6.sudokuweb.services.lobby.LobbyWebSocketsService;
import jakarta.validation.Valid;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
public class LobbyChatWsController {

    private final LobbyChatService lobbyChatService;
    private final LobbyWebSocketsService lobbyWebSocketsService;

    public LobbyChatWsController(LobbyChatService lobbyChatService,
                                   LobbyWebSocketsService lobbyWebSocketsService) {
        this.lobbyChatService = lobbyChatService;
        this.lobbyWebSocketsService = lobbyWebSocketsService;
    }


    @MessageMapping("/lobby/{lobbyId}/chat")
    public void sendLobbyChatMessage(
            @DestinationVariable Long lobbyId,
            @Valid LobbyChatMessageRequestDto requestDto) {
        LobbyChatMessageDto lobbyChatMessageDto = lobbyChatService.submitMessage(lobbyId, requestDto.getUserId(), requestDto.getMessage());
        lobbyWebSocketsService.handleLobbyChatMessage(lobbyChatMessageDto);
    }

}
