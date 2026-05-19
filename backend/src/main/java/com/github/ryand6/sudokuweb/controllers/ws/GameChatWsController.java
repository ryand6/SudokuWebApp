package com.github.ryand6.sudokuweb.controllers.ws;

import com.github.ryand6.sudokuweb.dto.entity.user.UserDto;
import com.github.ryand6.sudokuweb.dto.request.GameChatMessageRequestDto;
import com.github.ryand6.sudokuweb.exceptions.auth.OAuth2LoginRequiredException;
import com.github.ryand6.sudokuweb.services.game.GameChatService;
import com.github.ryand6.sudokuweb.services.user.UserService;
import jakarta.validation.Valid;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;

@Controller
public class GameChatWsController {

    private final GameChatService gameChatService;
    private final UserService userService;

    public GameChatWsController(GameChatService gameChatService,
                                UserService userService) {
        this.gameChatService = gameChatService;
        this.userService = userService;
    }

    @MessageMapping("/game/{gameId}/chat/send-message")
    public void sendGameChatMessage(
            @DestinationVariable Long gameId,
            @AuthenticationPrincipal OAuth2User principal,
            OAuth2AuthenticationToken authToken,
            @Valid GameChatMessageRequestDto requestDto
    ) {
        if (principal == null || authToken == null) {
            throw new OAuth2LoginRequiredException("OAuth2 login required to carry out this action");
        }
        UserDto user = userService.getCurrentUserByOAuth(principal, authToken);
        gameChatService.createMessage(gameId, user.getId(), requestDto.getMessage(), requestDto.getMessageType());
    }

}
