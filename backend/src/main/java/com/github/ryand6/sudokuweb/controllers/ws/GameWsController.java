package com.github.ryand6.sudokuweb.controllers.ws;

import com.github.ryand6.sudokuweb.dto.events.PlayerHighlightedCellDto;
import com.github.ryand6.sudokuweb.dto.request.PlayerHighlightedCellRequestDto;
import com.github.ryand6.sudokuweb.services.game.GameInMemoryStateService;
import jakarta.validation.Valid;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

@Controller
public class GameWsController {

    private final GameInMemoryStateService gameInMemoryStateService;

    public GameWsController(GameInMemoryStateService gameInMemoryStateService) {
        this.gameInMemoryStateService = gameInMemoryStateService;
    }

    @MessageMapping("/game/{gameId}/update-highlighted-cell")
    public void updatePlayerHighlightedCell(
            @DestinationVariable Long gameId,
            @Valid PlayerHighlightedCellRequestDto requestDto) {
        gameInMemoryStateService.updatePlayerHighlightedCell(new PlayerHighlightedCellDto(gameId, requestDto.getUserId(), requestDto.getRow(), requestDto.getCol()));
    }
}
