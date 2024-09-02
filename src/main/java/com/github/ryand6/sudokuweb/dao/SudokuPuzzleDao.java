package com.github.ryand6.sudokuweb.dao;

import com.github.ryand6.sudokuweb.domain.SudokuPuzzle;

import java.util.List;
import java.util.Optional;

public interface SudokuPuzzleDao {

    void create(SudokuPuzzle sudokuPuzzle);

    Optional<SudokuPuzzle> findOne(Long puzzleId);

    List<SudokuPuzzle> find();

}
