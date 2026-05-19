package com.github.ryand6.sudokuweb.controllers.ws;

import com.github.ryand6.sudokuweb.dto.entity.user.UserDto;
import com.github.ryand6.sudokuweb.dto.request.LobbyChatMessageRequestDto;
import com.github.ryand6.sudokuweb.exceptions.auth.OAuth2LoginRequiredException;
import com.github.ryand6.sudokuweb.services.lobby.LobbyChatService;
import com.github.ryand6.sudokuweb.services.user.UserService;
import jakarta.validation.Valid;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;

@Controller
public class LobbyChatWsController {

    private final LobbyChatService lobbyChatService;
    private final UserService userService;

    public LobbyChatWsController(LobbyChatService lobbyChatService,
                                 UserService userService) {
        this.lobbyChatService = lobbyChatService;
        this.userService = userService;
    }

    @MessageMapping("/lobby/{lobbyId}/chat/send-message")
    public void sendLobbyChatMessage(
            @DestinationVariable Long lobbyId,
            @AuthenticationPrincipal OAuth2User principal,
            OAuth2AuthenticationToken authToken,
            @Valid LobbyChatMessageRequestDto requestDto) {
        if (principal == null || authToken == null) {
            throw new OAuth2LoginRequiredException("OAuth2 login required to carry out this action");
        }
        UserDto user = userService.getCurrentUserByOAuth(principal, authToken);
        lobbyChatService.createPersonalMessage(lobbyId, user.getId(), requestDto.getMessage());
    }

}
