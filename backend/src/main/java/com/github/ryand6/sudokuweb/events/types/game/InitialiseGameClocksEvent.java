package com.github.ryand6.sudokuweb.events.types.game;

import com.github.ryand6.sudokuweb.enums.GameStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.Instant;

@Getter
@AllArgsConstructor
public class InitialiseGameClocksEvent {

    private Long gameId;

    private Instant gameStartsAt;

    private Instant gameEndsAt;

    private GameStatus gameStatus;

}
