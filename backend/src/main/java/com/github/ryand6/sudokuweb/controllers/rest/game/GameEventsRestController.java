package com.github.ryand6.sudokuweb.controllers.rest.game;

import com.github.ryand6.sudokuweb.dto.entity.game.GameEventDto;
import com.github.ryand6.sudokuweb.dto.response.GetGameEventsResponseDto;
import com.github.ryand6.sudokuweb.services.game.GameEventService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/game/events")
public class GameEventsRestController {

    private final GameEventService gameEventService;

    public GameEventsRestController(GameEventService gameEventService) {
        this.gameEventService = gameEventService;
    }

    @GetMapping("/get-game-events")
    public ResponseEntity<?> getGameEvents(@RequestParam Long gameId,
                                           @RequestParam int page) {
        List<GameEventDto> gameEvents = gameEventService.getGameEvents(gameId, page);
        return ResponseEntity.ok(new GetGameEventsResponseDto(gameEvents));
    }

}
