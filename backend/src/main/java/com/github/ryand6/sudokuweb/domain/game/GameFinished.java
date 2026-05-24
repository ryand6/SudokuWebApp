package com.github.ryand6.sudokuweb.domain.game;

import com.github.ryand6.sudokuweb.enums.GameStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.Instant;

@AllArgsConstructor
@Getter
public class GameFinished {

    private Instant gameEndedAt;

    private GameStatus gameStatus;

}
