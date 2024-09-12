package com.github.ryand6.sudokuweb.repositories;

import com.github.ryand6.sudokuweb.TestDataUtil;
import com.github.ryand6.sudokuweb.domain.SudokuPuzzle;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class SudokuPuzzleRepositoryIntegrationTests {

    private final SudokuPuzzleRepository underTest;

    @Autowired
    public SudokuPuzzleRepositoryIntegrationTests(SudokuPuzzleRepository underTest) {
        this.underTest = underTest;
    }

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    public void setUp() {
        // Correct SQL syntax for deleting all rows from the tables
        jdbcTemplate.execute("DELETE FROM lobby_state");
        jdbcTemplate.execute("DELETE FROM users");
        jdbcTemplate.execute("DELETE FROM scores");
        jdbcTemplate.execute("DELETE FROM sudoku_puzzles");
        jdbcTemplate.execute("DELETE FROM lobbies");
    }

    @Test
    public void testSudokuPuzzleCreationAndRecall() {
        SudokuPuzzle sudokuPuzzle = TestDataUtil.createTestSudokuPuzzleA();
        underTest.save(sudokuPuzzle);
        Optional<SudokuPuzzle> result = underTest.findById(sudokuPuzzle.getId());
        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(sudokuPuzzle);
    }

    @Test
    public void testMultipleSudokuPuzzlesCreatedAndRecalled() {
        SudokuPuzzle puzzleA = TestDataUtil.createTestSudokuPuzzleA();
        underTest.save(puzzleA);
        SudokuPuzzle puzzleB = TestDataUtil.createTestSudokuPuzzleB();
        underTest.save(puzzleB);
        SudokuPuzzle puzzleC = TestDataUtil.createTestSudokuPuzzleC();
        underTest.save(puzzleC);

        Iterable<SudokuPuzzle> result = underTest.findAll();
        assertThat(result)
                .hasSize(3)
                .containsExactly(puzzleA, puzzleB, puzzleC);
    }

    @Test
    public void testSudokuPuzzleFullUpdate() {
        SudokuPuzzle sudokuPuzzleA = TestDataUtil.createTestSudokuPuzzleA();
        underTest.save(sudokuPuzzleA);
        sudokuPuzzleA.setDifficulty(SudokuPuzzle.Difficulty.MEDIUM);
        underTest.save(sudokuPuzzleA);
        Optional<SudokuPuzzle> result = underTest.findById(sudokuPuzzleA.getId());
        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(sudokuPuzzleA);
    }

    @Test
    public void testSudokuPuzzleDeletion() {
        SudokuPuzzle sudokuPuzzleA = TestDataUtil.createTestSudokuPuzzleA();
        underTest.save(sudokuPuzzleA);
        underTest.deleteById(sudokuPuzzleA.getId());
        Optional<SudokuPuzzle> result = underTest.findById(sudokuPuzzleA.getId());
        assertThat(result).isEmpty();
    }

}
