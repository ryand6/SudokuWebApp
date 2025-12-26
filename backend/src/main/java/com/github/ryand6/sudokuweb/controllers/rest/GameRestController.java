package com.github.ryand6.sudokuweb.controllers.rest;

import com.github.ryand6.sudokuweb.dto.request.CheckUserInGameRequestDto;
import com.github.ryand6.sudokuweb.dto.request.GenerateBoardRequestDto;
import com.github.ryand6.sudokuweb.exceptions.GamePlayerNotFoundException;
import com.github.ryand6.sudokuweb.services.GameService;
import com.github.ryand6.sudokuweb.services.MembershipCheckService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/game")
public class GameRestController {

    private final GameService gameService;
    private final MembershipCheckService membershipCheckService;

    public GameRestController(GameService gameService,
                              MembershipCheckService membershipCheckService) {
        this.gameService = gameService;
        this.membershipCheckService = membershipCheckService;
    }

    @GetMapping("/check-user-in-game")
    public ResponseEntity<?> isUserInGame(@RequestBody CheckUserInGameRequestDto requestDto) {
        if (!membershipCheckService.isUserInGame(requestDto.getCurrentGameId(), requestDto.getUserId())) {
            throw new GamePlayerNotFoundException("User with ID + " + requestDto.getUserId() + " is not part of the game with ID " + requestDto.getCurrentGameId());
        } else {
            return ResponseEntity.ok(gameService.getGameById(requestDto.getCurrentGameId()));
        }
    }

    /* Generate a new sudokuPuzzleEntity for the current lobby and creating lobbyState records for each
    active user in the lobby for the new sudokuPuzzleEntity */
    @PostMapping("/create-game")
    public ResponseEntity<?> generateSudokuBoard(@Valid @RequestBody GenerateBoardRequestDto generateBoardRequestDto) {
        return ResponseEntity.ok(gameService.createGameIfNoneActive(generateBoardRequestDto.getLobby()));
    }

}
