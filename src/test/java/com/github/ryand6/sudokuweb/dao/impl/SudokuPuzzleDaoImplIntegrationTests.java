package com.github.ryand6.sudokuweb.dao.impl;

import com.github.ryand6.sudokuweb.TestDataUtil;
import com.github.ryand6.sudokuweb.domain.SudokuPuzzle;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ExtendWith(SpringExtension.class)
public class SudokuPuzzleDaoImplIntegrationTests {

    private SudokuPuzzleDaoImpl underTest;

    @Autowired
    public SudokuPuzzleDaoImplIntegrationTests(SudokuPuzzleDaoImpl underTest) {
        this.underTest = underTest;
    }

    @Test
    public void testSudokuPuzzleCreationAndRecall() {
        SudokuPuzzle sudokuPuzzle = TestDataUtil.createTestSudokuPuzzle();
        underTest.create(sudokuPuzzle);
        Optional<SudokuPuzzle> result = underTest.findOne(sudokuPuzzle.getId());
        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(sudokuPuzzle);
    }

}
