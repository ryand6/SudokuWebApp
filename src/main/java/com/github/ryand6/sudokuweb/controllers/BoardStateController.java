package com.github.ryand6.sudokuweb.controllers;

import com.github.ryand6.sudokuweb.services.PuzzleGenerator;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

;

@RestController
public class BoardStateController {

    @GetMapping("/generate-board")
    public List<String> generateSudoku(@RequestParam String difficulty, PuzzleGenerator puzzleGenerator) {
        // Call static method to generate puzzle as a nested int array based on the
        // user's difficulty setting
        return puzzleGenerator.generatePuzzle(difficulty);
    }

}
