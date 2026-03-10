package com.github.ryand6.sudokuweb.events.types.game;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class GameLeftInMemoryStateEvent {

    private Long gameId;

}
