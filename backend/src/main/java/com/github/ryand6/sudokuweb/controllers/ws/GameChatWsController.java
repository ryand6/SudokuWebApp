package com.github.ryand6.sudokuweb.controllers.ws;

import com.github.ryand6.sudokuweb.dto.request.GameChatMessageRequestDto;
import com.github.ryand6.sudokuweb.services.game.GameChatService;
import jakarta.validation.Valid;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

@Controller
public class GameChatWsController {

    private final GameChatService gameChatService;

    public GameChatWsController(GameChatService gameChatService) {
        this.gameChatService = gameChatService;
    }

    @MessageMapping("/game/{gameId}/chat/send-message")
    public void sendGameChatMessage(
            @DestinationVariable Long gameId,
            @Valid GameChatMessageRequestDto requestDto
    ) {
        gameChatService.createMessage(gameId, requestDto.getUserId(), requestDto.getMessage(), requestDto.getMessageType());
    }

}
