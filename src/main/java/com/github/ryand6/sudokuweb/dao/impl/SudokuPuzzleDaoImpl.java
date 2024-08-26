package com.github.ryand6.sudokuweb.dao.impl;

import com.github.ryand6.sudokuweb.dao.SudokuPuzzleDao;
import com.github.ryand6.sudokuweb.domain.SudokuPuzzle;
import org.springframework.jdbc.core.JdbcTemplate;

public class SudokuPuzzleDaoImpl implements SudokuPuzzleDao {

    private JdbcTemplate jdbcTemplate;

    public SudokuPuzzleDaoImpl(final JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void create(SudokuPuzzle sudokuPuzzle) {
        jdbcTemplate.update("INSERT INTO sudoku_puzzles (id, initial_board_state, solution, difficulty) VALUES (?, ?, ?, ?)",
            sudokuPuzzle.getId(),
            sudokuPuzzle.getInitialBoardState(),
            sudokuPuzzle.getSolution(),
            sudokuPuzzle.getDifficulty()
        );
    }

}
