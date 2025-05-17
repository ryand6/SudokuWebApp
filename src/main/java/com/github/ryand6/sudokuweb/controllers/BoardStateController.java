package com.github.ryand6.sudokuweb.controllers;

import com.github.ryand6.sudokuweb.dto.GenerateBoardRequestDto;
import com.github.ryand6.sudokuweb.dto.GenerateBoardResponseDto;
import com.github.ryand6.sudokuweb.services.impl.BoardStateService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class BoardStateController {

    private final BoardStateService boardStateService;

    public BoardStateController(BoardStateService boardStateService) {
        this.boardStateService = boardStateService;
    }

    /* Generate a new puzzle for the current lobby and creating lobbyState records for each
    active user in the lobby for the new puzzle */
    @PostMapping("/generate-board")
    public GenerateBoardResponseDto generateSudokuBoard(@Valid @RequestBody GenerateBoardRequestDto generateBoardRequestDto) {
        return boardStateService.generateSudokuBoard(generateBoardRequestDto);
    }

}
