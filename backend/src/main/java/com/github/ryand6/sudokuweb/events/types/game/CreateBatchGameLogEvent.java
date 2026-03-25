package com.github.ryand6.sudokuweb.events.types.game;

import com.github.ryand6.sudokuweb.domain.game.event.GameEventRequest;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class CreateBatchGameLogEvent {

    private Long gameId;

    private Long userId;

    List<GameEventRequest> gameEventRequests;

}
