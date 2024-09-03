package com.github.ryand6.sudokuweb.dao.impl;

import com.github.ryand6.sudokuweb.TestDataUtil;
import com.github.ryand6.sudokuweb.domain.SudokuPuzzle;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class SudokuPuzzleDaoImplIntegrationTests {

    private SudokuPuzzleDaoImpl underTest;

    @Autowired
    public SudokuPuzzleDaoImplIntegrationTests(SudokuPuzzleDaoImpl underTest) {
        this.underTest = underTest;
    }

    @Test
    public void testSudokuPuzzleCreationAndRecall() {
        SudokuPuzzle sudokuPuzzle = TestDataUtil.createTestSudokuPuzzleA();
        underTest.create(sudokuPuzzle);
        Optional<SudokuPuzzle> result = underTest.findOne(sudokuPuzzle.getId());
        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(sudokuPuzzle);
    }

    @Test
    public void testMultipleSudokuPuzzlesCreatedAndRecalled() {
        SudokuPuzzle puzzleA = TestDataUtil.createTestSudokuPuzzleA();
        underTest.create(puzzleA);
        SudokuPuzzle puzzleB = TestDataUtil.createTestSudokuPuzzleB();
        underTest.create(puzzleB);
        SudokuPuzzle puzzleC = TestDataUtil.createTestSudokuPuzzleC();
        underTest.create(puzzleC);

        List<SudokuPuzzle> result = underTest.find();
        assertThat(result)
                .hasSize(3)
                .containsExactly(puzzleA, puzzleB, puzzleC);
    }

    @Test
    public void testSudokuPuzzleFullUpdate() {
        SudokuPuzzle sudokuPuzzle = TestDataUtil.createTestSudokuPuzzleA();
        underTest.create(sudokuPuzzle);
        sudokuPuzzle.setDifficulty("medium");
        underTest.update(sudokuPuzzle.getId(), sudokuPuzzle);
        Optional<SudokuPuzzle> result = underTest.findOne(sudokuPuzzle.getId());
        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(sudokuPuzzle);
    }

}
