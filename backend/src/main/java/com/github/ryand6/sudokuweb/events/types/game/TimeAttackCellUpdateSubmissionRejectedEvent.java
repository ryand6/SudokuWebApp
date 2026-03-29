package com.github.ryand6.sudokuweb.events.types.game;

import com.github.ryand6.sudokuweb.domain.game.player.state.TimeAttackUpdate;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TimeAttackCellUpdateSubmissionRejectedEvent {

    private Long gameId;

    private Long userId;

    private TimeAttackUpdate timeAttackUpdate;

}
