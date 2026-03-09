package com.github.ryand6.sudokuweb.services;

import com.github.ryand6.sudokuweb.dto.entity.game.GameDto;
import com.github.ryand6.sudokuweb.services.game.GameService;
import com.github.ryand6.sudokuweb.services.lobby.LobbyWebSocketsService;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;

@Service
public class TaskSchedulerService {

    private final TaskScheduler taskScheduler;

    private final String GAME_CREATION_TASK_NAME = "CREATE_GAME_FOR_LOBBY_";

    private final Map<String, ScheduledFuture<?>> scheduledTasks = new ConcurrentHashMap<>();

    private static final Logger log = LoggerFactory.getLogger(TaskSchedulerService.class);

    private final GameService gameService;
    private final LobbyWebSocketsService lobbyWebSocketsService;

    public TaskSchedulerService(TaskScheduler taskScheduler,
                                GameService gameService,
                                LobbyWebSocketsService lobbyWebSocketsService) {
        this.taskScheduler = taskScheduler;
        this.gameService = gameService;
        this.lobbyWebSocketsService = lobbyWebSocketsService;
    }

    public void scheduleGameCreationTask(Long lobbyId, Instant countdownEndsAt) {
        String taskId = GAME_CREATION_TASK_NAME + lobbyId;

        cancelGameCreationTask(lobbyId);

        ScheduledFuture<?> future = taskScheduler.schedule(() -> {
            try {
                createGame(lobbyId);
            } finally {
                scheduledTasks.remove(taskId);
            }
        }, countdownEndsAt.plusMillis(50));

        scheduledTasks.put(taskId, future);
    }

    public void cancelGameCreationTask(Long lobbyId) {
        String taskId = GAME_CREATION_TASK_NAME + lobbyId;
        ScheduledFuture<?> oldFuture = scheduledTasks.get(taskId);

        if (oldFuture != null) {
            oldFuture.cancel(true);
        }

        scheduledTasks.remove(taskId);
    }

    @Transactional
    private void createGame(Long lobbyId) {
        try {
            GameDto gameDto = gameService.createGameIfNoneActive(lobbyId);

            if (gameDto != null) {
                lobbyWebSocketsService.handleLobbyGameStart(gameDto);
            }
        } catch (Exception e) {
            log.error("Game creation task failed: " + e);
        }
    }

}
