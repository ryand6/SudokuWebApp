package com.github.ryand6.sudokuweb.controllers.ws;

import com.github.ryand6.sudokuweb.dto.request.LobbyChatMessageRequestDto;
import com.github.ryand6.sudokuweb.services.lobby.LobbyChatService;
import jakarta.validation.Valid;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

@Controller
public class LobbyChatWsController {

    private final LobbyChatService lobbyChatService;

    public LobbyChatWsController(LobbyChatService lobbyChatService) {
        this.lobbyChatService = lobbyChatService;
    }

    @MessageMapping("/lobby/{lobbyId}/chat")
    public void sendLobbyChatMessage(
            @DestinationVariable Long lobbyId,
            @Valid LobbyChatMessageRequestDto requestDto) {
        lobbyChatService.createPersonalMessage(lobbyId, requestDto.getUserId(), requestDto.getMessage());
    }

}
