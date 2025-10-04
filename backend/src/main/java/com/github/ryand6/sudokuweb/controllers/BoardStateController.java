package com.github.ryand6.sudokuweb.controllers;

import com.github.ryand6.sudokuweb.dto.entity.GameDto;
import com.github.ryand6.sudokuweb.dto.request.GenerateBoardRequestDto;
import com.github.ryand6.sudokuweb.services.BoardStateService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;


@Controller
public class BoardStateController {

    private final BoardStateService boardStateService;

    public BoardStateController(BoardStateService boardStateService) {
        this.boardStateService = boardStateService;
    }

    /* Generate a new sudokuPuzzleEntity for the current lobby and creating lobbyState records for each
    active user in the lobby for the new sudokuPuzzleEntity */
    @PostMapping("/create-game")
    @ResponseBody
    public GameDto generateSudokuBoard(@Valid @RequestBody GenerateBoardRequestDto generateBoardRequestDto) {
        return boardStateService.createGame(generateBoardRequestDto);
    }

}
