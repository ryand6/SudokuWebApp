package com.github.ryand6.sudokuweb.services;

import com.github.ryand6.sudokuweb.dto.entity.GameDto;
import com.github.ryand6.sudokuweb.dto.entity.LobbyDto;
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
    private final SimpMessagingTemplate messagingTemplate;

    public TaskSchedulerService(TaskScheduler taskScheduler,
                                GameService gameService,
                                LobbyWebSocketsService lobbyWebSocketsService, SimpMessagingTemplate messagingTemplate) {
        this.taskScheduler = taskScheduler;
        this.gameService = gameService;
        this.lobbyWebSocketsService = lobbyWebSocketsService;
        this.messagingTemplate = messagingTemplate;
    }

    public void scheduleGameCreationTask(LobbyDto lobby, Instant countdownEndsAt) {
        String taskId = GAME_CREATION_TASK_NAME + lobby.getId();

        cancelGameCreationTask(lobby);

        ScheduledFuture<?> future = taskScheduler.schedule(() -> {
            try {
                createGame(lobby);
            } finally {
                scheduledTasks.remove(taskId);
            }
        }, countdownEndsAt);

        scheduledTasks.put(taskId, future);
    }

    public void cancelGameCreationTask(LobbyDto lobby) {
        String taskId = GAME_CREATION_TASK_NAME + lobby.getId();
        ScheduledFuture<?> oldFuture = scheduledTasks.get(taskId);

        if (oldFuture != null) {
            oldFuture.cancel(true);
        }

        scheduledTasks.remove(taskId);
    }

    private void createGame(LobbyDto lobby) {
        try {
            GameDto gameDto = gameService.createGameIfNoneActive(lobby);
            if (gameDto != null) {
                lobbyWebSocketsService.handleLobbyGameStart(gameDto, messagingTemplate);
            }
        } catch (Exception e) {
            log.error("Game creation task failed: " + e.getMessage());
        }
    }

}
