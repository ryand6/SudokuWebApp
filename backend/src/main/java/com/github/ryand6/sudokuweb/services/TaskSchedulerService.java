package com.github.ryand6.sudokuweb.services;

import com.github.ryand6.sudokuweb.dto.entity.game.GameDto;
import com.github.ryand6.sudokuweb.events.types.game.CloseGameEvent;
import com.github.ryand6.sudokuweb.events.types.game.FinishGameEvent;
import com.github.ryand6.sudokuweb.events.types.game.GameEndsAtUpdateEvent;
import com.github.ryand6.sudokuweb.events.types.game.GameFinishSchedulerUpdateEvent;
import com.github.ryand6.sudokuweb.events.types.general.CancelScheduledTaskEvent;
import com.github.ryand6.sudokuweb.events.types.lobby.ScheduleGameCreationTaskEvent;
import com.github.ryand6.sudokuweb.services.game.GameService;
import com.github.ryand6.sudokuweb.services.lobby.LobbyWebSocketsService;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;

@Service
public class TaskSchedulerService {

    private final TaskScheduler taskScheduler;

    private final String GAME_CREATION_TASK_NAME = "CREATE_GAME_FOR_LOBBY_";
    private final String FINISH_GAME_TASK_NAME = "FINISH_GAME_";
    private final String CLOSE_GAME_TASK_NAME = "CLOSE_GAME_";

    private final Map<String, ScheduledFuture<?>> scheduledTasks = new ConcurrentHashMap<>();

    private static final Logger log = LoggerFactory.getLogger(TaskSchedulerService.class);

    private final GameService gameService;
    private final LobbyWebSocketsService lobbyWebSocketsService;
    private final ApplicationEventPublisher applicationEventPublisher;

    public TaskSchedulerService(TaskScheduler taskScheduler,
                                GameService gameService,
                                LobbyWebSocketsService lobbyWebSocketsService,
                                ApplicationEventPublisher applicationEventPublisher) {
        this.taskScheduler = taskScheduler;
        this.gameService = gameService;
        this.lobbyWebSocketsService = lobbyWebSocketsService;
        this.applicationEventPublisher = applicationEventPublisher;
    }

    //#######################//
    // Game Creation         //
    //#######################//

    public void scheduleGameCreationTask(Long lobbyId, Instant countdownEndsAt) {
        String taskId = GAME_CREATION_TASK_NAME + lobbyId;

        cancelTask(taskId);

        ScheduledFuture<?> future = taskScheduler.schedule(() -> {
            try {
                createGame(lobbyId);
            } finally {
                scheduledTasks.remove(taskId);
            }
        }, countdownEndsAt.plusMillis(50));

        scheduledTasks.put(taskId, future);
    }

    @Transactional
    private void createGame(Long lobbyId) {
        try {
            GameDto gameDto = gameService.createGameIfNoneActive(lobbyId);

            if (gameDto != null) {
                lobbyWebSocketsService.handleLobbyGameStart(gameDto);
            }
        } catch (Exception e) {
            log.error("Game creation task failed: ", e);
        }
    }

    //#######################//
    // Finish Game           //
    //#######################//

    public void scheduleGameFinish(Long gameId, Instant gameEndsAt) {
        String taskId = FINISH_GAME_TASK_NAME + gameId;

        cancelTask(taskId);

        ScheduledFuture<?> future = taskScheduler.schedule(() -> {
            applicationEventPublisher.publishEvent(
                    new FinishGameEvent(gameId)
            );
        }, gameEndsAt);

        scheduledTasks.put(taskId, future);
    }

    //#######################//
    // Close Game            //
    //#######################//

    public void scheduleGameClose(Long gameId, Instant gameEndedAt) {
        String taskId = CLOSE_GAME_TASK_NAME + gameId;

        cancelTask(taskId);

        ScheduledFuture<?> future = taskScheduler.schedule(() -> {
            applicationEventPublisher.publishEvent(
                    new CloseGameEvent(gameId)
            );
        }, gameEndedAt.plusMillis(60000));

        scheduledTasks.put(taskId, future);
    }

    //#######################//
    // Helpers               //
    //#######################//

    public void cancelTask(String taskId) {
        ScheduledFuture<?> oldFuture = scheduledTasks.get(taskId);

        if (oldFuture != null) {
            oldFuture.cancel(true);
        }

        scheduledTasks.remove(taskId);
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    void handleScheduleGameCreationTaskEvent(ScheduleGameCreationTaskEvent event) {
        scheduleGameCreationTask(event.getLobbyId(), event.getCountdownEndsAt());
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    void handleGameFinishSchedulerUpdate(GameFinishSchedulerUpdateEvent event) {
        scheduleGameFinish(event.getGameId(), event.getGameEndsAt());
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    void handleCancelScheduledTaskEvent(CancelScheduledTaskEvent event) {
        cancelTask(event.getTaskId());
    }

}
