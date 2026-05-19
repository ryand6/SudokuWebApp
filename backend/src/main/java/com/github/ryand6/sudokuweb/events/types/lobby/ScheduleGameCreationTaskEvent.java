package com.github.ryand6.sudokuweb.events.types.lobby;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.Instant;

@Getter
@AllArgsConstructor
public class ScheduleGameCreationTaskEvent {

    private Long lobbyId;

    private Instant countdownEndsAt;

}
