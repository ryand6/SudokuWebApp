package com.github.ryand6.sudokuweb.dao.impl;

import com.github.ryand6.sudokuweb.domain.SudokuPuzzle;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.JdbcTemplate;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class SudokuPuzzleDaoImplTests {

    @Mock
    private JdbcTemplate jdbcTemplate;

    @InjectMocks
    private SudokuPuzzleDaoImpl underTest;

    @Test
    public void testCreateSudokuPuzzleSql() {
        SudokuPuzzle sudokuPuzzle = SudokuPuzzle.builder().
                id(1L).
                initialBoardState("092306001007008003043207080035680000080000020000035670070801950200500800500409130").
                solution("892356741657148293143297586735682419986714325421935678374861952219573864568429137").
                difficulty("easy").
                build();

        underTest.create(sudokuPuzzle);

        verify(jdbcTemplate).update(
                eq("INSERT INTO sudoku_puzzles (id, initial_board_state, solution, difficulty) VALUES (?, ?, ?, ?)"),
                eq(1L), eq("092306001007008003043207080035680000080000020000035670070801950200500800500409130"),
                eq("892356741657148293143297586735682419986714325421935678374861952219573864568429137"), eq("easy")
        );
    }

    @Test
    public void testFindOneSudokuPuzzleSql() {
        underTest.findOne(1L);

        verify(jdbcTemplate).query(
                eq("SELECT id, initial_board_state, solution, difficulty FROM sudoku_puzzles WHERE id = ? LIMIT 1"),
                ArgumentMatchers.<SudokuPuzzleDaoImpl.SudokuPuzzleRowMapper>any(),
                eq(1L)
        );
    }

}
