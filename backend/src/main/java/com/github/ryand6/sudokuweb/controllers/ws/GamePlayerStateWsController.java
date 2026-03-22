package com.github.ryand6.sudokuweb.controllers.ws;

import com.github.ryand6.sudokuweb.dto.request.SubmitCellUpdateRequestDto;
import com.github.ryand6.sudokuweb.services.game.GamePlayerStateService;
import jakarta.validation.Valid;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

@Controller
public class GamePlayerStateWsController {

    private final GamePlayerStateService gamePlayerStateService;

    public GamePlayerStateWsController(GamePlayerStateService gamePlayerStateService) {
        this.gamePlayerStateService = gamePlayerStateService;
    }

    @MessageMapping("/game/{gameId}/user/{userId}/submit-cell-update")
    public void submitCellUpdate(
            @DestinationVariable Long gameId,
            @DestinationVariable Long userId,
            @Valid SubmitCellUpdateRequestDto requestDto) {
        // IMPLEMENT

    }

}
