package com.github.ryand6.sudokuweb.events.types.game;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.Instant;

@Getter
@AllArgsConstructor
public class GameEndsAtUpdateEvent {

    private Long gameId;

    private Instant gameEndsAt;

}
