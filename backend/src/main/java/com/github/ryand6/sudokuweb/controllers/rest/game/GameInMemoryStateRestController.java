package com.github.ryand6.sudokuweb.controllers.rest.game;

import com.github.ryand6.sudokuweb.dto.events.SudokuCellCoordinatesDto;
import com.github.ryand6.sudokuweb.services.game.GameInMemoryStateService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/game/memory/")
public class GameInMemoryStateRestController {

    private final GameInMemoryStateService gameInMemoryStateService;

    public GameInMemoryStateRestController(GameInMemoryStateService gameInMemoryStateService) {
        this.gameInMemoryStateService = gameInMemoryStateService;
    }

    @GetMapping("/get-game-highlighted-cells")
    public ResponseEntity<?> getGameHighlightedCells(@RequestParam Long gameId) {
        Map<Long, SudokuCellCoordinatesDto> gameHighlightedCells = gameInMemoryStateService.gameGameHighlights(gameId);
        return ResponseEntity.ok(gameHighlightedCells);
    }

}
