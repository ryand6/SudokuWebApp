package com.github.ryand6.sudokuweb.events.types.game;

import com.github.ryand6.sudokuweb.domain.game.player.state.CellValueUpdate;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CellUpdateSubmissionInvalidEvent {

    private Long gameId;

    private Long userId;

    private CellValueUpdate cellValueUpdate;

}
