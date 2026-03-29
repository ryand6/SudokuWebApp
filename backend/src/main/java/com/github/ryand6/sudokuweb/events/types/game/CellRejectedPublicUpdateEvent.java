package com.github.ryand6.sudokuweb.events.types.game;

import com.github.ryand6.sudokuweb.domain.game.CellRejectedPublicUpdate;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.Instant;

@Getter
@AllArgsConstructor
public class CellRejectedPublicUpdateEvent {

    private Long gameId;

    private CellRejectedPublicUpdate cellRejectedPublicUpdate;

}
