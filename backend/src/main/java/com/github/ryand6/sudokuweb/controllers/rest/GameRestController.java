package com.github.ryand6.sudokuweb.controllers.rest;

import com.github.ryand6.sudokuweb.dto.entity.GameDto;
import com.github.ryand6.sudokuweb.dto.request.GenerateBoardRequestDto;
import com.github.ryand6.sudokuweb.exceptions.game.player.GamePlayerNotFoundException;
import com.github.ryand6.sudokuweb.services.GameService;
import com.github.ryand6.sudokuweb.services.MembershipService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/game")
public class GameRestController {

    private final GameService gameService;
    private final MembershipService membershipService;

    public GameRestController(GameService gameService,
                              MembershipService membershipService) {
        this.gameService = gameService;
        this.membershipService = membershipService;
    }

    @GetMapping("/check-user-in-game")
    public ResponseEntity<?> isUserInGame(@RequestParam Long gameId, @RequestParam Long userId) {
        if (!membershipService.isUserInGame(gameId, userId)) {
            throw new GamePlayerNotFoundException("User with ID + " + userId + " is not part of the game with ID " + gameId);
        } else {
            GameDto gameDto = gameService.getGameById(gameId);
            return ResponseEntity.ok(gameDto);
        }
    }

    /* Generate a new sudokuPuzzleEntity for the current lobby and creating lobbyState records for each
    active user in the lobby for the new sudokuPuzzleEntity */
    @PostMapping("/create-game")
    public ResponseEntity<?> generateSudokuBoard(@Valid @RequestBody GenerateBoardRequestDto generateBoardRequestDto) {
        return ResponseEntity.ok(gameService.createGameIfNoneActive(generateBoardRequestDto.getLobby()));
    }

    // Get Game DTO using gameId
    @GetMapping("/get-game")
    public ResponseEntity<?> getGame(@RequestParam Long gameId) {
        GameDto gameDto = gameService.getGameById(gameId);
        return ResponseEntity.ok(gameDto);
    }

}
