package com.github.ryand6.sudokuweb.dao.impl;

import com.github.ryand6.sudokuweb.dao.SudokuPuzzleDao;
import com.github.ryand6.sudokuweb.domain.SudokuPuzzle;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Component
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

    @Override
    public Optional<SudokuPuzzle> findOne(Long puzzleId) {
        List<SudokuPuzzle> results = jdbcTemplate.query(
                "SELECT id, initial_board_state, solution, difficulty FROM sudoku_puzzles WHERE id = ? LIMIT 1",
                new SudokuPuzzleDaoImpl.SudokuPuzzleRowMapper(),
                puzzleId
        );
        return results.stream().findFirst();
    }

    public static class SudokuPuzzleRowMapper implements RowMapper<SudokuPuzzle> {

        public SudokuPuzzle mapRow(ResultSet rs, int rowNum) throws SQLException {
            return SudokuPuzzle.builder().
                    id(rs.getLong("id")).
                    initialBoardState(rs.getString("initial_board_state")).
                    solution(rs.getString("solution")).
                    difficulty(rs.getString("difficulty")).
                    build();
        }

    }

}
