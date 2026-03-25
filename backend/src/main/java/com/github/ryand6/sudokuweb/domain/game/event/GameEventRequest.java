package com.github.ryand6.sudokuweb.domain.game.event;

import com.github.ryand6.sudokuweb.enums.GameEventType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
public class GameEventRequest {

    private GameEventType gameEventType;

    private String message;

}
