package com.github.ryand6.sudokuweb.events.types.game;

import com.github.ryand6.sudokuweb.domain.game.CellAcceptedPublicUpdate;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.Instant;

@Getter
@AllArgsConstructor
public class CellAcceptedPublicUpdateEvent {

    private Long gameId;

    private CellAcceptedPublicUpdate cellAcceptedPublicUpdate;

}
