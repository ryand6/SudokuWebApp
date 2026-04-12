package com.github.ryand6.sudokuweb.controllers.rest.game;

import com.github.ryand6.sudokuweb.dto.entity.game.GameChatMessageDto;
import com.github.ryand6.sudokuweb.dto.response.GetGameChatMessagesResponseDto;
import com.github.ryand6.sudokuweb.services.game.GameChatService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/game/chat")
public class GameChatRestController {

    private final GameChatService gameChatService;

    public GameChatRestController(GameChatService gameChatService) {
        this.gameChatService = gameChatService;
    }

    @GetMapping("/get-chat-messages")
    public ResponseEntity<?> getGameChatMessages(@RequestParam Long gameId,
                                                 @RequestParam int page) {
        List<GameChatMessageDto> gameChatMessages = gameChatService.getGameChatMessages(gameId, page);
        return ResponseEntity.ok(new GetGameChatMessagesResponseDto(gameChatMessages));
    }

}
