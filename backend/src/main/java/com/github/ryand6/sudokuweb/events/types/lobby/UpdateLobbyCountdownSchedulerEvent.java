package com.github.ryand6.sudokuweb.events.types.lobby;

import com.github.ryand6.sudokuweb.domain.lobby.countdown.CountdownEvaluationResult;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UpdateLobbyCountdownSchedulerEvent {

    private Long lobbyId;

    private CountdownEvaluationResult countdownEvaluationResult;

}
