package com.github.ryand6.sudokuweb.dto;

import com.github.ryand6.sudokuweb.domain.SudokuPuzzleEntity;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SudokuPuzzleDto {

    private Long id;

    private String initialBoardState;

    private SudokuPuzzleEntity.Difficulty difficulty;

}
