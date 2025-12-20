package com.github.ryand6.sudokuweb.controllers.rest;

import com.github.ryand6.sudokuweb.dto.entity.LobbyChatMessageDto;
import com.github.ryand6.sudokuweb.dto.response.LobbyGetChatMessagesResponseDto;
import com.github.ryand6.sudokuweb.services.LobbyChatService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/lobby/chat")
public class LobbyChatRestController {

    private final LobbyChatService lobbyChatService;

    public LobbyChatRestController(LobbyChatService lobbyChatService) {
        this.lobbyChatService = lobbyChatService;
    }

    @GetMapping("/get-chat-messages")
    public ResponseEntity<?> getLobbyChatMessages(@RequestParam Long lobbyId,
                                                  @RequestParam int page) {
        List<LobbyChatMessageDto> lobbyChatMessages = lobbyChatService.getLobbyChatMessages(lobbyId, page);
        return ResponseEntity.ok(new LobbyGetChatMessagesResponseDto(lobbyChatMessages));
    }

}
