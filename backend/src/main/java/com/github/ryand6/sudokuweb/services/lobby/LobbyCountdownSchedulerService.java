package com.github.ryand6.sudokuweb.services.lobby;

import com.github.ryand6.sudokuweb.domain.lobby.countdown.CountdownEvaluationResult;
import com.github.ryand6.sudokuweb.events.types.general.CancelScheduledTaskEvent;
import com.github.ryand6.sudokuweb.events.types.lobby.ScheduleGameCreationTaskEvent;
import com.github.ryand6.sudokuweb.events.types.lobby.UpdateLobbyCountdownSchedulerEvent;
import jakarta.transaction.Transactional;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

@Service
public class LobbyCountdownSchedulerService {

    private final ApplicationEventPublisher applicationEventPublisher;

    public LobbyCountdownSchedulerService(ApplicationEventPublisher applicationEventPublisher) {
        this.applicationEventPublisher = applicationEventPublisher;
    }

    @Transactional
    void handleCountdownEvaluationResult(Long lobbyId, CountdownEvaluationResult countdownEvaluationResult) {
        if (countdownEvaluationResult.shouldCountdownUpdate()) {
            applicationEventPublisher.publishEvent(
                    new ScheduleGameCreationTaskEvent(lobbyId, countdownEvaluationResult.getCountdownEndsAt())
            );
        } else if (countdownEvaluationResult.shouldCountdownCancel()) {
            String taskId = "CREATE_GAME_FOR_LOBBY_" + lobbyId;
            applicationEventPublisher.publishEvent(
                    new CancelScheduledTaskEvent(taskId)
            );
        }
    }

//    @EventListener
//    void handleUpdateLobbyCountdownSchedulerEvent(UpdateLobbyCountdownSchedulerEvent event) {
//        handleCountdownEvaluationResult(event.getLobbyId(), event.getCountdownEvaluationResult());
//    }

}
