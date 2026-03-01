package com.github.ryand6.sudokuweb.services.lobby;

import com.github.ryand6.sudokuweb.domain.lobby.LobbyEntity;
import com.github.ryand6.sudokuweb.domain.lobby.countdown.CountdownEvaluationResult;
import com.github.ryand6.sudokuweb.services.TaskSchedulerService;
import org.springframework.stereotype.Service;

@Service
public class LobbyCountdownService {

    private final TaskSchedulerService taskSchedulerService;

    public LobbyCountdownService(TaskSchedulerService taskSchedulerService) {
        this.taskSchedulerService = taskSchedulerService;
    }

    void handleCountdownEvaluationResult(LobbyEntity lobby, CountdownEvaluationResult countdownEvaluationResult) {
        if (countdownEvaluationResult.shouldCountdownUpdate()) {
            taskSchedulerService.scheduleGameCreationTask(lobby, countdownEvaluationResult.getCountdownEndsAt());
        } else if (countdownEvaluationResult.shouldCountdownCancel()) {
            taskSchedulerService.cancelGameCreationTask(lobby);
        }
    }

}
