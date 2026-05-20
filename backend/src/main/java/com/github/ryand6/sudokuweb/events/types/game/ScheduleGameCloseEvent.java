package com.github.ryand6.sudokuweb.events.types.game;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.Instant;

@Getter
@AllArgsConstructor
public class ScheduleGameCloseEvent {

    private Long gameId;

    private Instant gameEndedAt;

}
