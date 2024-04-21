package com.github.ryand6.sudokuweb.controllers;

import com.github.ryand6.sudokuweb.services.SudokuGeneratorService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class BoardStateController {

    @GetMapping("/generate-board")
    public int[][] generateSudoku(@RequestParam String difficulty) {
        // Call static method to generate puzzle as a nested int array based on the
        // user's difficulty setting
        return SudokuGeneratorService.generatePuzzle(difficulty);
    }

}
