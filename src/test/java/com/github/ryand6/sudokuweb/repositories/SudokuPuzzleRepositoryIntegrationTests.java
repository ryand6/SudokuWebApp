package com.github.ryand6.sudokuweb.repositories;

import com.github.ryand6.sudokuweb.TestDataUtil;
import com.github.ryand6.sudokuweb.domain.SudokuPuzzleEntity;
import com.github.ryand6.sudokuweb.enums.Difficulty;
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
        jdbcTemplate.execute("DELETE FROM lobby_users");
        jdbcTemplate.execute("DELETE FROM game_state");
        jdbcTemplate.execute("DELETE FROM games");
        jdbcTemplate.execute("DELETE FROM lobbies");
        jdbcTemplate.execute("DELETE FROM users");
        jdbcTemplate.execute("DELETE FROM scores");
        jdbcTemplate.execute("DELETE FROM sudoku_puzzles");
    }

    @Test
    public void testSudokuPuzzleCreationAndRecall() {
        SudokuPuzzleEntity sudokuPuzzleEntity = TestDataUtil.createTestSudokuPuzzleA();
        underTest.save(sudokuPuzzleEntity);
        Optional<SudokuPuzzleEntity> result = underTest.findById(sudokuPuzzleEntity.getId());
        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(sudokuPuzzleEntity);
    }

    @Test
    public void testMultipleSudokuPuzzlesCreatedAndRecalled() {
        SudokuPuzzleEntity puzzleA = TestDataUtil.createTestSudokuPuzzleA();
        underTest.save(puzzleA);
        SudokuPuzzleEntity puzzleB = TestDataUtil.createTestSudokuPuzzleB();
        underTest.save(puzzleB);
        SudokuPuzzleEntity puzzleC = TestDataUtil.createTestSudokuPuzzleC();
        underTest.save(puzzleC);

        Iterable<SudokuPuzzleEntity> result = underTest.findAll();
        assertThat(result)
                .hasSize(3)
                .containsExactly(puzzleA, puzzleB, puzzleC);
    }

    @Test
    public void testSudokuPuzzleFullUpdate() {
        SudokuPuzzleEntity sudokuPuzzleEntityA = TestDataUtil.createTestSudokuPuzzleA();
        underTest.save(sudokuPuzzleEntityA);
        sudokuPuzzleEntityA.setDifficulty(Difficulty.MEDIUM);
        underTest.save(sudokuPuzzleEntityA);
        Optional<SudokuPuzzleEntity> result = underTest.findById(sudokuPuzzleEntityA.getId());
        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(sudokuPuzzleEntityA);
    }

    @Test
    public void testSudokuPuzzleDeletion() {
        SudokuPuzzleEntity sudokuPuzzleEntityA = TestDataUtil.createTestSudokuPuzzleA();
        underTest.save(sudokuPuzzleEntityA);
        underTest.deleteById(sudokuPuzzleEntityA.getId());
        Optional<SudokuPuzzleEntity> result = underTest.findById(sudokuPuzzleEntityA.getId());
        assertThat(result).isEmpty();
    }

}
