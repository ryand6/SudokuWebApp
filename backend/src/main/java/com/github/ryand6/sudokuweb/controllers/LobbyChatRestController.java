package com.github.ryand6.sudokuweb.controllers;

import com.github.ryand6.sudokuweb.dto.entity.LobbyChatMessageDto;
import com.github.ryand6.sudokuweb.dto.request.LobbyChatMessageRequestDto;
import com.github.ryand6.sudokuweb.dto.response.LobbyGetChatMessagesResponseDto;
import com.github.ryand6.sudokuweb.dto.response.LobbyChatSubmitMessageResponseDto;
import com.github.ryand6.sudokuweb.services.LobbyChatService;
import com.github.ryand6.sudokuweb.services.LobbyWebSocketsService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("/get-chat-messages")
    public ResponseEntity<?> getLobbyChatMessages(@RequestParam Long lobbyId,
                                                  @RequestParam int page) {
        List<LobbyChatMessageDto> lobbyChatMessages = lobbyChatService.getLobbyChatMessages(lobbyId, page);
        return ResponseEntity.ok(new LobbyGetChatMessagesResponseDto(lobbyChatMessages));
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

        LobbyChatMessageDto lobbyChatMessageDto = lobbyChatService.submitMessage(requestDto.getLobbyId(), requestDto.getUserId(), requestDto.getMessage());

        lobbyWebSocketsService.handleLobbyChatMessage(lobbyChatMessageDto, messagingTemplate);

        return ResponseEntity
                .ok(lobbyChatMessageDto);
    }

}
