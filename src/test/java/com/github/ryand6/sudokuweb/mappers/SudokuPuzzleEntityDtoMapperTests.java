package com.github.ryand6.sudokuweb.mappers;

import com.github.ryand6.sudokuweb.TestDataUtil;
import com.github.ryand6.sudokuweb.domain.SudokuPuzzleEntity;
import com.github.ryand6.sudokuweb.dto.SudokuPuzzleDto;
import com.github.ryand6.sudokuweb.enums.Difficulty;
import com.github.ryand6.sudokuweb.mappers.Impl.SudokuPuzzleEntityDtoMapper;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/*
Unit tests for SudokuPuzzleEntityDtoMapper
*/
public class SudokuPuzzleEntityDtoMapperTests {

    private final SudokuPuzzleEntityDtoMapper sudokuPuzzleEntityDtoMapper = new SudokuPuzzleEntityDtoMapper();

    @Test
    void mapToDto_shouldReturnValidSudokuPuzzleDto() {
        SudokuPuzzleEntity sudokuPuzzleEntity = TestDataUtil.createTestSudokuPuzzleA();
        sudokuPuzzleEntity.setId(5L);

        SudokuPuzzleDto sudokuPuzzleDto = sudokuPuzzleEntityDtoMapper.mapToDto(sudokuPuzzleEntity);

        assertThat(sudokuPuzzleDto).isNotNull();
        assertThat(sudokuPuzzleDto.getId()).isEqualTo(5L);
        assertThat(sudokuPuzzleDto.getInitialBoardState()).isEqualTo("092306001007008003043207080035680000080000020000035670070801950200500800500409130");
        assertThat(sudokuPuzzleDto.getDifficulty()).isEqualTo(Difficulty.EASY);
    }

}
