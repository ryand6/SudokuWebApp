package com.github.ryand6.sudokuweb.services;

import com.github.ryand6.sudokuweb.enums.Difficulty;
import com.github.ryand6.sudokuweb.integration.AbstractIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class SudokuPuzzleServiceTests extends AbstractIntegrationTest {

    @Autowired
    private SudokuPuzzleService sudokuPuzzleService;
//
//    @Test
//    void generatePuzzle_returnsValidPuzzle() {
//        // For testing, you might need to set up a mock resource or use a test-specific file
//        // Simulate expected behavior or use a test file
//        Difficulty difficulty = Difficulty.EASY;
//
//        List<String> puzzleDetails = sudokuPuzzleService.generatePuzzle(difficulty);
//
//        assertNotNull(puzzleDetails, "Puzzle details should not be null");
//        assertFalse(puzzleDetails.isEmpty(), "Puzzle details should not be empty");
//        assertTrue(puzzleDetails.size() == 2, "Puzzle details should contain 2 elements");
//    }
}
