package com.github.ryand6.sudokuweb.domain.game.player.state;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
public class CellValueAndScoreUpdate {

    private int row;

    private int col;

    private int value;

    private int scoreUpdate;

}
