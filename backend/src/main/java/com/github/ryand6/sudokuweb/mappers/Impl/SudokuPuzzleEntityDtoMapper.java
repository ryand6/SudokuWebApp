package com.github.ryand6.sudokuweb.mappers.Impl;

import com.github.ryand6.sudokuweb.domain.SudokuPuzzleEntity;
import com.github.ryand6.sudokuweb.dto.entity.SudokuPuzzleDto;
import com.github.ryand6.sudokuweb.mappers.EntityDtoMapper;
import org.springframework.stereotype.Component;

@Component
public class SudokuPuzzleEntityDtoMapper implements EntityDtoMapper<SudokuPuzzleEntity, SudokuPuzzleDto> {

    @Override
    // SudokuPuzzle only ever created in backend, therefore Dto to Entity mapping N/A
    public SudokuPuzzleDto mapToDto(SudokuPuzzleEntity sudokuPuzzleEntity) {
        return SudokuPuzzleDto.builder()
                .id(sudokuPuzzleEntity.getId())
                .initialBoardState(sudokuPuzzleEntity.getInitialBoardState())
                .difficulty(sudokuPuzzleEntity.getDifficulty())
                .build();
    }

}
