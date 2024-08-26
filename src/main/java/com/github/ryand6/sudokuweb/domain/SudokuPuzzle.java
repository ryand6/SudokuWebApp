package com.github.ryand6.sudokuweb.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SudokuPuzzle {

    private Long id;

    private String initialBoardState;

    private String solution;

    private String difficulty;

}
