package com.github.ryand6.sudokuweb.domain.factory;

import com.github.ryand6.sudokuweb.domain.SudokuPuzzleEntity;
import com.github.ryand6.sudokuweb.enums.Difficulty;

public class SudokuPuzzleFactory {

    public static SudokuPuzzleEntity createSudokuPuzzle(String initialBoardState, String solution, Difficulty difficulty) {
        SudokuPuzzleEntity sudokuPuzzleEntity = new SudokuPuzzleEntity();
        sudokuPuzzleEntity.setInitialBoardState(initialBoardState);
        sudokuPuzzleEntity.setSolution(solution);
        sudokuPuzzleEntity.setDifficulty(difficulty);
        return sudokuPuzzleEntity;
    }

}
