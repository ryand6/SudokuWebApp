package com.github.ryand6.sudokuweb.services;

import com.github.ryand6.sudokuweb.config.SudokuPuzzleLoader;
import com.github.ryand6.sudokuweb.domain.puzzle.SudokuPuzzleEntity;
import com.github.ryand6.sudokuweb.domain.puzzle.SudokuPuzzleFactory;
import com.github.ryand6.sudokuweb.enums.Difficulty;
import com.github.ryand6.sudokuweb.repositories.SudokuPuzzleRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Service
public class SudokuPuzzleService {

    private final SudokuPuzzleLoader sudokuPuzzleLoader;
    private final SudokuPuzzleRepository sudokuPuzzleRepository;

    public SudokuPuzzleService(SudokuPuzzleLoader sudokuPuzzleLoader,
                               SudokuPuzzleRepository sudokuPuzzleRepository) {
        this.sudokuPuzzleLoader = sudokuPuzzleLoader;
        this.sudokuPuzzleRepository = sudokuPuzzleRepository;
    }

    /* Randomly select problem and its solution from a file with the corresponding difficulty type.
    File name in format: <difficulty>sudoku.tsv
    File in format: col1 = string representation of the problem, col2 = string representation of the solution to the problem */
    @Transactional
    public SudokuPuzzleEntity generatePuzzle(Difficulty difficulty) {
        List<String> listOfPuzzles = getListOfPuzzles(difficulty);
        // Randomly select problem from array
        int index = new Random().nextInt(listOfPuzzles.size());
        String randomProblem = listOfPuzzles.get(index);
        String[] parts = randomProblem.split("\t");

        String initialBoardState = parts[0];
        String solution = parts[1];

        // Either create a new entity or get existing if an entity already exists with the same initial board state and solution
        SudokuPuzzleEntity sudokuPuzzle = sudokuPuzzleRepository.existsByInitialBoardStateAndSolution(initialBoardState, solution)
                ? sudokuPuzzleRepository.findByInitialBoardStateAndSolution(initialBoardState, solution)
                : SudokuPuzzleFactory.createSudokuPuzzle(initialBoardState, solution, difficulty);

        sudokuPuzzleRepository.save(sudokuPuzzle);
        return sudokuPuzzle;
    }

    // Get list of existing puzzles based on difficulty
    private List<String> getListOfPuzzles(Difficulty difficulty) {
        String difficultyString = difficulty.getProperCase();
        List<String> puzzleDetails = new ArrayList<>();
        List<String> listOfPuzzles = new ArrayList<>();
        switch (difficultyString) {
            case "Easy":
                listOfPuzzles = sudokuPuzzleLoader.getEasyPuzzles();
            case "Medium":
                listOfPuzzles = sudokuPuzzleLoader.getMediumPuzzles();
            case "Hard":
                listOfPuzzles = sudokuPuzzleLoader.getHardPuzzles();
            case "Extreme":
                listOfPuzzles = sudokuPuzzleLoader.getExtremePuzzles();
        }
        return listOfPuzzles;
    }

}
