package com.github.ryand6.sudokuweb.events.types.game;

import com.github.ryand6.sudokuweb.enums.GameResult;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Map;

@Getter
@AllArgsConstructor
public class GameResultsDeterminedEvent {

    private Long gameId;

    private Map<Long, GameResult> gameResults;

}
