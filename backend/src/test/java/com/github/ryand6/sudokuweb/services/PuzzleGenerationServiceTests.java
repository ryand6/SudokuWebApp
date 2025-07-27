package com.github.ryand6.sudokuweb.services;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class PuzzleGenerationServiceTests {

    @Autowired
    private PuzzleGenerator puzzleGenerator;

    @Test
    void generatePuzzle_returnsValidPuzzle() {
        // For testing, you might need to set up a mock resource or use a test-specific file
        // Simulate expected behavior or use a test file
        String difficulty = "easy";

        List<String> puzzleDetails = puzzleGenerator.generatePuzzle(difficulty);

        assertNotNull(puzzleDetails, "Puzzle details should not be null");
        assertFalse(puzzleDetails.isEmpty(), "Puzzle details should not be empty");
        assertTrue(puzzleDetails.size() == 2, "Puzzle details should contain 2 elements");
    }
}
