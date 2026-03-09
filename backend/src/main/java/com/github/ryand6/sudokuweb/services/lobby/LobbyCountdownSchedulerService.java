package com.github.ryand6.sudokuweb.services.lobby;

import com.github.ryand6.sudokuweb.domain.lobby.countdown.CountdownEvaluationResult;
import com.github.ryand6.sudokuweb.services.TaskSchedulerService;
import org.springframework.stereotype.Service;

@Service
public class LobbyCountdownSchedulerService {

    private final TaskSchedulerService taskSchedulerService;

    public LobbyCountdownSchedulerService(TaskSchedulerService taskSchedulerService) {
        this.taskSchedulerService = taskSchedulerService;
    }

    void handleCountdownEvaluationResult(Long lobbyId, CountdownEvaluationResult countdownEvaluationResult) {
        if (countdownEvaluationResult.shouldCountdownUpdate()) {
            taskSchedulerService.scheduleGameCreationTask(lobbyId, countdownEvaluationResult.getCountdownEndsAt());
        } else if (countdownEvaluationResult.shouldCountdownCancel()) {
            taskSchedulerService.cancelGameCreationTask(lobbyId);
        }
    }

}
