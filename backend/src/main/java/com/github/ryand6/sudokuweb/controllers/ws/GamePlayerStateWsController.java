package com.github.ryand6.sudokuweb.controllers.ws;

import com.github.ryand6.sudokuweb.dto.request.RevertInGameStatusRequestDto;
import com.github.ryand6.sudokuweb.dto.request.SubmitCellUpdateRequestDto;
import com.github.ryand6.sudokuweb.services.game.GamePlayerStateService;
import com.github.ryand6.sudokuweb.services.lobby.LobbyPlayerService;
import jakarta.validation.Valid;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

@Controller
public class GamePlayerStateWsController {

    private final GamePlayerStateService gamePlayerStateService;
    private final LobbyPlayerService lobbyPlayerService;

    public GamePlayerStateWsController(GamePlayerStateService gamePlayerStateService,
                                       LobbyPlayerService lobbyPlayerService) {
        this.gamePlayerStateService = gamePlayerStateService;
        this.lobbyPlayerService = lobbyPlayerService;
    }

    @MessageMapping("/game/{gameId}/user/{userId}/submit-cell-update")
    public void submitCellUpdate(
            @DestinationVariable Long gameId,
            @DestinationVariable Long userId,
            @Valid SubmitCellUpdateRequestDto requestDto) {
        gamePlayerStateService.handleCellUpdateSubmission(gameId, userId, requestDto.getRow(), requestDto.getCol(), requestDto.getValue());
    }

    @MessageMapping("/game/{gameId}/user/{userId}/revert-in-game-status")
    public void revertInGameStatus(
            @DestinationVariable Long gameId,
            @DestinationVariable Long userId,
            @Valid RevertInGameStatusRequestDto requestDto) {
        lobbyPlayerService.revertLobbyPlayerInGameStatus(requestDto.getLobbyId(), userId);
    }

}
