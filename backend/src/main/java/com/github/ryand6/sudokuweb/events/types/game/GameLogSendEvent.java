package com.github.ryand6.sudokuweb.events.types.game;

import com.github.ryand6.sudokuweb.dto.entity.game.GameEventDto;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class GameLogSendEvent {

    private GameEventDto gameEventDto;

}
