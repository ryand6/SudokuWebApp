package com.github.ryand6.sudokuweb.events.types.game;

import com.github.ryand6.sudokuweb.enums.GameStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class GameStatusUpdateEvent {

    private Long gameId;

    private GameStatus gameStatus;

}
