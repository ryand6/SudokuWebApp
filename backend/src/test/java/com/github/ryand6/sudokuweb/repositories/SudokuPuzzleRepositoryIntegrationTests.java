package com.github.ryand6.sudokuweb.repositories;

import com.github.ryand6.sudokuweb.TestDataUtil;
import com.github.ryand6.sudokuweb.domain.puzzle.SudokuPuzzleEntity;
import com.github.ryand6.sudokuweb.enums.Difficulty;
import com.github.ryand6.sudokuweb.integration.AbstractIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

public class SudokuPuzzleRepositoryIntegrationTests extends AbstractIntegrationTest {

    private final SudokuPuzzleRepository underTest;

    @Autowired
    public SudokuPuzzleRepositoryIntegrationTests(SudokuPuzzleRepository underTest) {
        this.underTest = underTest;
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
