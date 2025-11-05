package com.github.ryand6.sudokuweb.controllers;

import com.github.ryand6.sudokuweb.dto.request.LobbyChatMessageRequestDto;
import com.github.ryand6.sudokuweb.dto.response.LobbyChatSubmitMessageResponseDto;
import com.github.ryand6.sudokuweb.services.LobbyChatService;
import com.github.ryand6.sudokuweb.services.LobbyWebSocketsService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/lobby/chat")
public class LobbyChatRestController {

    private final LobbyChatService lobbyChatService;
    private final LobbyWebSocketsService lobbyWebSocketsService;
    private final SimpMessagingTemplate messagingTemplate;

    public LobbyChatRestController(LobbyChatService lobbyChatService,
                                   LobbyWebSocketsService lobbyWebSocketsService,
                                   SimpMessagingTemplate messagingTemplate) {
        this.lobbyChatService = lobbyChatService;
        this.lobbyWebSocketsService = lobbyWebSocketsService;
        this.messagingTemplate = messagingTemplate;
    }

    @PostMapping("/submit-chat-message")
    public ResponseEntity<?> sendLobbyChatMessage(@Valid @RequestBody LobbyChatMessageRequestDto requestDto,
                                                  BindingResult bindingResult) {
        // Check if validation errors occurred in DTO
        if (bindingResult.hasErrors()) {
            // Collect all validation errors
            List<String> errors = bindingResult.getAllErrors()
                    .stream()
                    .map(ObjectError::getDefaultMessage)
                    .toList();
            return ResponseEntity
                    .badRequest()
                    .body(errors);
        }

        lobbyChatService.submitMessage(requestDto.getLobbyId(), requestDto.getUserId(), requestDto.getMessage());

        lobbyWebSocketsService.handleLobbyChatMessage(requestDto.getLobbyId(), requestDto.getUsername(), requestDto.getMessage(), messagingTemplate);

        return ResponseEntity
                .ok(new LobbyChatSubmitMessageResponseDto(requestDto.getLobbyId(), requestDto.getUsername(), requestDto.getMessage()));
    }

}
