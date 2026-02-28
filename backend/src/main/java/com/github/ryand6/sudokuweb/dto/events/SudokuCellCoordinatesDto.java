package com.github.ryand6.sudokuweb.dto.events;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SudokuCellCoordinatesDto {

    private int row;

    private int col;

}
