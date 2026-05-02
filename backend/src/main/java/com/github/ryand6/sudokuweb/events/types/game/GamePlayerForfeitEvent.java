package com.github.ryand6.sudokuweb.events.types.game;

import com.github.ryand6.sudokuweb.dto.entity.game.GamePlayerDto;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class GamePlayerForfeitEvent {

    private Long gameId;

    private GamePlayerDto gamePlayerDto;

}
