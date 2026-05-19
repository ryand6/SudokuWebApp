package com.github.ryand6.sudokuweb.controllers.ws;

import com.github.ryand6.sudokuweb.dto.entity.user.UserDto;
import com.github.ryand6.sudokuweb.dto.events.PlayerHighlightedCellDto;
import com.github.ryand6.sudokuweb.dto.request.PlayerGameLoadedRequestDto;
import com.github.ryand6.sudokuweb.dto.request.PlayerHighlightedCellRequestDto;
import com.github.ryand6.sudokuweb.exceptions.auth.OAuth2LoginRequiredException;
import com.github.ryand6.sudokuweb.services.game.GameInMemoryStateService;
import com.github.ryand6.sudokuweb.services.game.GameService;
import com.github.ryand6.sudokuweb.services.user.UserService;
import jakarta.validation.Valid;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;

@Controller
public class GameWsController {

    private final GameInMemoryStateService gameInMemoryStateService;
    private final GameService gameService;
    private final UserService userService;

    public GameWsController(GameInMemoryStateService gameInMemoryStateService,
                            GameService gameService,
                            UserService userService) {
        this.gameInMemoryStateService = gameInMemoryStateService;
        this.gameService = gameService;
        this.userService = userService;
    }

    @MessageMapping("/game/{gameId}/update-highlighted-cell")
    public void updatePlayerHighlightedCell(
            @DestinationVariable Long gameId,
            @AuthenticationPrincipal OAuth2User principal,
            OAuth2AuthenticationToken authToken,
            @Valid PlayerHighlightedCellRequestDto requestDto) {
        if (principal == null || authToken == null) {
            throw new OAuth2LoginRequiredException("OAuth2 login required to carry out this action");
        }
        UserDto user = userService.getCurrentUserByOAuth(principal, authToken);
        gameInMemoryStateService.updatePlayerHighlightedCell(new PlayerHighlightedCellDto(gameId, user.getId(), requestDto.getRow(), requestDto.getCol()));
    }

    @MessageMapping("/game/{gameId}/game-loaded-confirmation")
    public void updatePlayerGameLoaded(
            @DestinationVariable Long gameId,
            @AuthenticationPrincipal OAuth2User principal,
            OAuth2AuthenticationToken authToken) {
        if (principal == null || authToken == null) {
            throw new OAuth2LoginRequiredException("OAuth2 login required to carry out this action");
        }
        UserDto user = userService.getCurrentUserByOAuth(principal, authToken);
        gameService.handlePlayerGameLoadedConfirmation(gameId, user.getId());
    }

}
