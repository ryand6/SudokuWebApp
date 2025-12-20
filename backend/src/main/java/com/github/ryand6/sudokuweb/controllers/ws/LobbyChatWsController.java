package com.github.ryand6.sudokuweb.controllers.ws;

import com.github.ryand6.sudokuweb.dto.entity.LobbyChatMessageDto;
import com.github.ryand6.sudokuweb.dto.request.LobbyChatMessageRequestDto;
import com.github.ryand6.sudokuweb.services.LobbyChatService;
import com.github.ryand6.sudokuweb.services.LobbyWebSocketsService;
import jakarta.validation.Valid;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;

@Controller
public class LobbyChatWsController {

    private final LobbyChatService lobbyChatService;
    private final LobbyWebSocketsService lobbyWebSocketsService;
    private final SimpMessagingTemplate messagingTemplate;

    public LobbyChatWsController(LobbyChatService lobbyChatService,
                                   LobbyWebSocketsService lobbyWebSocketsService,
                                   SimpMessagingTemplate messagingTemplate) {
        this.lobbyChatService = lobbyChatService;
        this.lobbyWebSocketsService = lobbyWebSocketsService;
        this.messagingTemplate = messagingTemplate;
    }


    @MessageMapping("/lobby/{lobbyId}/chat")
    public void sendLobbyChatMessage(
            @DestinationVariable Long lobbyId,
            @Valid LobbyChatMessageRequestDto requestDto,
            @AuthenticationPrincipal OAuth2User principal,
            OAuth2AuthenticationToken authToken) {

        System.out.println("\n\n\nReceived WS message for lobby " + lobbyId + " from user " + requestDto.getUserId());
        System.out.println("\n\n\nPayload: " + requestDto);

        LobbyChatMessageDto lobbyChatMessageDto = lobbyChatService.submitMessage(lobbyId, requestDto.getUserId(), requestDto.getMessage());
        lobbyWebSocketsService.handleLobbyChatMessage(lobbyChatMessageDto, messagingTemplate);
    }

}
