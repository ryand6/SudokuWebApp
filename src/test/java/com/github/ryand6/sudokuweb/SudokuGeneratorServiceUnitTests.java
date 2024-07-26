package com.github.ryand6.sudokuweb;

import com.github.ryand6.sudokuweb.services.SudokuGeneratorService;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class SudokuGeneratorServiceUnitTests {

    @Test
    void testGeneratePuzzle() {
        // For testing, you might need to set up a mock resource or use a test-specific file
        // Simulate expected behavior or use a test file
        String difficulty = "easy";

        List<String> puzzleDetails = SudokuGeneratorService.generatePuzzle(difficulty);

        assertNotNull(puzzleDetails, "Puzzle details should not be null");
        assertFalse(puzzleDetails.isEmpty(), "Puzzle details should not be empty");
        assertTrue(puzzleDetails.size() >= 2, "Puzzle details should contain at least 2 elements");
    }
}
