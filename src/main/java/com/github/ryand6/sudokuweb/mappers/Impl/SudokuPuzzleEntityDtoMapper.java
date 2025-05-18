package com.github.ryand6.sudokuweb.mappers.Impl;

import com.github.ryand6.sudokuweb.domain.SudokuPuzzleEntity;
import com.github.ryand6.sudokuweb.dto.SudokuPuzzleDto;
import org.springframework.stereotype.Component;

@Component
public class SudokuPuzzleEntityDtoMapper {

    // SudokuPuzzle only ever created in backend, therefore Dto to Entity mapping N/A
    public SudokuPuzzleDto mapToDto(SudokuPuzzleEntity sudokuPuzzleEntity) {
        return SudokuPuzzleDto.builder()
                .id(sudokuPuzzleEntity.getId())
                .initialBoardState(sudokuPuzzleEntity.getInitialBoardState())
                .difficulty(sudokuPuzzleEntity.getDifficulty())
                .build();
    }

}
