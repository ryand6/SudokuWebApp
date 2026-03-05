package com.github.ryand6.sudokuweb.dto.entity.puzzle;

import com.github.ryand6.sudokuweb.enums.Difficulty;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SudokuPuzzleDto {

    private Long id;

    private String initialBoardState;

    private Difficulty difficulty;

}
