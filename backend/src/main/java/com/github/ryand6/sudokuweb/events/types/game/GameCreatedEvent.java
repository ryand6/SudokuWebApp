package com.github.ryand6.sudokuweb.events.types.game;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class GameCreatedEvent {

    private Long gameId;

}
