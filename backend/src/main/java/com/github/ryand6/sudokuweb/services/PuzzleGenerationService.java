package com.github.ryand6.sudokuweb.services;

import com.github.ryand6.sudokuweb.config.SudokuPuzzleLoader;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Service
public class PuzzleGenerationService implements PuzzleGenerator {

    private final SudokuPuzzleLoader sudokuPuzzleLoader;

    public PuzzleGenerationService(SudokuPuzzleLoader sudokuPuzzleLoader) {
        this.sudokuPuzzleLoader = sudokuPuzzleLoader;
    }

    /* Randomly select problem and its solution from a file with the corresponding
    difficulty type. File name in format: <difficulty>sudoku.tsv, file in format:
    col1 = string representation of the problem as a nested int array, col2 = string
    representation of the solution to the problem as a nested int array */
    public List<String> generatePuzzle(String difficulty) {
        List<String> puzzleDetails = new ArrayList<>();
        List<String> listOfPuzzles = new ArrayList<>();
        switch (difficulty) {
            case "easy":
                listOfPuzzles = sudokuPuzzleLoader.getEasyPuzzles();
            case "medium":
                listOfPuzzles = sudokuPuzzleLoader.getMediumPuzzles();
            case "hard":
                listOfPuzzles = sudokuPuzzleLoader.getHardPuzzles();
            case "extreme":
                listOfPuzzles = sudokuPuzzleLoader.getExtremePuzzles();
        }
        // Randomly select problem from array
        int index = new Random().nextInt(listOfPuzzles.size());
        String randomProblem = listOfPuzzles.get(index);
        String[] parts = randomProblem.split("\t");
        // First part is sudokuPuzzleEntity
        puzzleDetails.add(parts[0]);
        // Second part is solution
        puzzleDetails.add(parts[1]);
        return puzzleDetails;
    }

}
